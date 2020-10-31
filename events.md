# EventDriven Design

Modern architectures rely on short running processes.
Often processes rely on other processes' outcome and are therefore event-driven.
One tool, which name comes up in those scenarios, is [Apache Kafka](https://kafka.apache.org/).

As far as I understood _Kafka_'s way of working, I'm not sure if it can be a real substitution for the system at my work, especially when taking migration effort into account.
On my work we use a very old, vendor specific solution (see ["Current scenario" section](#current-scenario)), and I'm convinced that we need to migrate to a modern environment / tech-stack, especially because the system has to process several hundred millions events - sometimes in only a few days. 
I don't know if someone, especially from management, shares my opinion, but I learned that you should not just blame something bad, without being able to present a better and valid solution.
 
Therefore, I want to investigate _Kafka_ and find out if we can/should use it without having to reimplement the whole system (amount of resources is far from where it should be).
I will describe our current system / situation, the motivation for modernization, and then investigate if and maybe how it can be transformed into _Kafka_ or if there is another, more fitting solution.

# Current scenario

In this chapter I'll describe the current scenario, divided into three parts.
The first part will show the overall process, while the second one will describe the technical environment.
In the last part I'll show the problems we have and why I think that we should or even need to migrate to a more modern environment.

## Process description

First I want to describe the steps of the process, without going into functional (in terms of business related) details. 

1. At the beginning the system retrieves XML-files with up to 50.000 single elements, which are stored as-is in the database.
2. A component searches for new stored files and adds their primary key to a JMS-queue.
3. The component which consumes those JMS-messages then loads the XML, unmarshalls it, does some validation which can't be done using XSD (e.g. is the sender allowed to send such a file), and if everything is fine stores the 50.000 single elements in a structured (database normalization) form.
4. For each element a stored procedure inside, triggered by a database job, creates an entry in a so called "_EventStore_" (see [section about _EventStores_](#eventstores-based-on-_jca_-and-_as_)), which is created and picked up by the first workflow of the process engine.
5. The process engine picks up a single element, performs further, single element based validation and again stores the result in the database.
   Again entries in two (let's assume the element was valid) _EventStore_ are made for further processing. 
6. The second workflow picks up results of the overall validation or of all single elements of a received XML-File (see step 1) and creates an XML-file including the result, which is transmitted back to the sender as an answer to the received file.
7. Parallel to the second workflow, a third one picks up the data of a valid single element, extracts some values out of it and applies them to the "storage part" the system where other components can access the data.

As you can see, the process is cut into short, single steps, and the result of each step is stored inside the database.
This has following advantages:
* Some later steps are only processed if a previous step was successful, e.g. step 4 is only done if step 3 was successful
* Short running steps / methods
* Processing can be parallelized very good
* If an error (e.g. network issues when calling an external service) occurs during one step, esp. when a single element is processes, only the single event fails but not all 50.000.
Those single failed events can then be reset after the problem was solved.

## Technical environment

Our environment is divided into two server clusters and an _Oracle RDMS_.
I won't mention the database anymore, because it does not play any role in the further investigation. 

The first server cluster consists of four _(traditional) WebSphere Application Servers (tWAS)_ with version 9.0.0.x.
_tWAS 9.0.0.x_ only supports _JavaEE 7_ and _Java 1.8_ (IBM JDK).
In this "WAS cluster" the first three steps of the processes described above are executed.

The second cluster consists of eight _Business Automation Workflows 19 (BAW19)_ servers.
Inside this cluster the steps 4 to 7 are executed.

Formerly the _BAW19_ was called _WebSphere Process Server (WPS)_.
To those who don't know the _WPS_ and/or _BAW19_:
The _BAW19_ is a regular _tWAS_ with a [Business Process Execution Language (BPEL)](https://en.wikipedia.org/wiki/Business_Process_Execution_Language) processing engine on top of it.
The underlying _tWAS_ is a _tWAS 8.5_ with _Java 1.8_ since 2019.
The _BAW19_ (or to be exactly the _tWAS 8.5_) therefore supports _JavaEE6_ and is also based on the IBM JDK with _Java 1.8_.
The predecessor _WPS_ only supported _tWAS 8.0_ with _Java 1.7_.

## Motivation for modernization

There are a bunch of things which make me want to modernize.
Many developers won't be surprised that almost all things point to lightweight application servers to increase productivity.
But, there are also some points, which are a total "no-go" for today's software development.

* The IBM JDK is not developed anymore, because IBM joined the circle of organizations, which support _AdoptOpenJDK_.
  There is still support yet, but it will end in a few years, and as said no Java 9+ will be provided.
* _tWAS 9.0.0.x_ does not get any updates anymore, only _tWAS 9.0.5.x_ (which can be used with a _Liberty_ core) is supported, but our operations department does not offer that versions.
* The supported _JavaEE_ version of both servers, _tWAS 9.0.0.x_ and _BAW19_, are long outdated and libraries / APIs are not supported anymore.
  For example _Hibernate 4.2.21_ is the last supported _Hibernate_ version which only requires _JPA 2.0_ (_JavaEE 6_), but due bugs in the _BAW19_ only an even older version (_4.2.3_) works without issues.
  As a comparison: Currently the _Hibernate_ team has already released version _6.0.0.Alpha_.
* To model _BAW19_ business workflows you have to use a special IDE, called the _IBM Integration Designer (IID)_.
  The _IID_ is based on a very old, outdated _Eclipse_ version and therefore many plugins are not supported or outdated.
  On top of that the _IID_ is full of bugs, e.g. claiming that it can't compile the sources even that it's accurate.
  You then have to restart the _IID_ several times (or even more often) until the _IID_ finally "decides" to compile your flawless code.
  Even with enterprise support you don't get fixes for that, or at least not in time.
  So every need to change the sources is literally a projects risk, because you never know if you can make the change (in time).
* The _BAW19_ business workflows can't be build with a automatic build tool like `Maven`.
  Writing and executing automatic tests, especially unittests, is also not possible.
* Development takes extremely long, because the servers take extremely long to start up and deploy.
  This makes integration tests, e.g. using _Arquillian_, take ages.
  Hot-deployment, e.g. while developing the frontend, is not supported by both application servers
* The environment is a shared environment and not exclusive to a specific project.
* Deployments are made manually by the operations department.
  This takes much time and even that they use scripts for some things it's very error-prone.
* No automated deployments are possible at the moment.
* Scaling the environment or setting up a new stage requires much effort, money and time. 
* The old technology stack leads to problems getting external developing support.
* No pure Java Enterprise tech stack combined with vendor lock.

# Event driven solutions

In this section I want to take a short look into the solutions we currently use and what I (don't) like about them.

## JMS

The _Java Message Service (JMS)_ provides message queueing and publish-subscribe technology.
At my work we use _JMS_ in the message queueing style to distribute events automatically inside the cluster.
So every node can pick up a new message after it has finished.
In our setup those messages only contain the primary key of the related object (here XML file).
A retry mechanism is also supported:
When a consuming component fails during processing a message, it can add the message back to the queue.


On the other hand:
As the component, which fills the queue, is also running on every node, we implemented a mechanism which blocks the other nodes, so only one node searches for new files (see [process step 2](#process-description)) at a time.
That means that even if one node drops out, the queue gets still filled.
In a containerized environment you could just deploy one replica of this "providing" instance and _Kubernetes_ would take care that there is always one running container of that.

Another downside of queues is that the actual queues objects are somehow not connected to your system.
Because of that, in our environment we check, if the related object (here XML file) has already been processed, right after the messages has been put out of the queue.

The _JMS_ takes away much work from you by providing an automated distribution and retry mechanic.
It fits into our environment very well, where we have strong cuts between the processing steps.

On open question I'll investigate somewhat later (not in this text) is how to use a _JMS_ queue inside a containerized environment.

## EventStores based on _JCA_ and _AS_

To start a workflow you can use the combination of an _ActivationSpec (AS)_ with a JDBC based JCA resource adapter, which is shipped within the _BAW19_.
In this approach a JDBC-AS will trigger every x seconds, tries to read up to y rows from a so called _EventStore_.
For every row a defined workflow business flow is called.

### The _EventStore_ table

An _EventStore_ is a database table with a fixed column definition of which the most important columns are shown below.

```text
EventStore E1

+-----------+-----------+-------------+--------------+--------------+
| event_id  | object_id | object_name | event_status | connector_id |
+-----------+-----------+-------------+--------------+--------------+
```

* The  **event_id** is just the regular primary key of the table and therefore unique inside a table.
* **object_id**: This column contains the primary key of an object (e.g. one single element which should be validated - see [process step 5](#process-description)).
  As it is up to you, what you define as an object here, each workflow may work on other objects and therefore a same value in this column does not mean that the same object will be processed.
  It's "just" a primary key value from some table.
* The **object_name** serves as the identifier which workflow should be called from the process engine.
  In general, you can use a single _EventStore_ for as multiple workflows (shown in the example below), but in our performance test we found out that you should provide individual _EventStores_ if you process a huge amount of events.
* The **event_status** represents the current status of an event.
  It may contain the following values: 0 (outstanding), 1 (success, row gets deleted - _in reality you never see this status, because the deletion happens inside the same transaction_), 3 (in process), -1 (failed after maximum numbers of retries).
* The **connector_id** is used as an identifier, which allows an _ActivationSpec_ to only select rows with a specific value, e.g. a node name.

Next to the columns described here, there are more for priority, timeout and current processor.

### The _ActivationSpec_ definition

The _ActivationSpec_ configuration takes a bunch of settings, whereas the most important are:

* The table name of the related _EventStore_
* The polling information (maximum connections, interval, and quantity)
* Identifier to select only entries where the value matches the column "_connector_id_" of the related _EventStore_.
  We put the node's name into that to distribute events to selected nodes
* The maximum number of retries (if any), until a failing event is finally declared as failed

### Example

The following example shall visualize the setup and how events are picked up.
Let's assume there are two _EventStores_ (E1 and E2) and three _ActivationSpecs_ (A1, A2, A3).

* A1 reads entries from E1 and calls the workflow W1
* A2 reads entries from E2 and calls the workflow W2
* A3 also reads entries from E2, but calls the workflow W3

Furthermore, let's assume the cluster contains three servers (S1, S2, S3), meaning on each server all three _ActivationSpec_ are active.

If the entries of E1 and E2 would look like shown below, brings up the following scenario:

**_EventStore_ E1**:
* _EventStore_ E1 contains events for the workflow W1 (`object_name = W1`)
* All events are waiting to be picked up (`event_status = 0`) by the related _ActivationSpec_ A1
* There is one single event for the server S2 (`object_id = 8`), one for the server S3 (`9`), and two for S1 (`7, 10`)

**_EventStore_ E2**:
* _EventStore_ E2 contains events for both workflows W1 and W2, but all existing events are only distributed to S1 and S2
* Next time A2 and A3 are triggered on S1, they will process an object with the id `1`, but call their individual workflows W2 (`event_id = 1`) or W3 (`event_id = 2`)
* At the moment the A2 on the server S2 is processing (`event_status = 3`) the event with `event_id = 3`
* At some point in the past, A3 on the server S2 picked up the event with `event_id = 4`, but the workflow W3 did not finish successful (`event_status = -1`)
* So this failure should be investigated and after fixing the problem, set back to `event_status = 0` to be picked up again

```text
EventStore E1

+-----------+-----------+-------------+--------------+--------------+
| event_id  | object_id | object_name | event_status | connector_id |
+-----------+-----------+-------------+--------------+--------------+
|         1 |         7 | W1          |            0 | S1           |
|         2 |         8 | W1          |            0 | S2           |
|         3 |         9 | W1          |            0 | S3           |
|         4 |        10 | W1          |            0 | S1           |
+-----------+-----------+-------------+--------------+--------------+

EventStore E2

+-----------+-----------+-------------+--------------+--------------+
| event_id  | object_id | object_name | event_status | connector_id |
+-----------+-----------+-------------+--------------+--------------+
|         1 |         1 | W2          |            0 | S1           |
|         2 |         1 | W3          |            0 | S1           |
|         3 |         2 | W2          |            3 | S2           |
|         4 |         3 | W3          |           -1 | S2           |
+-----------+-----------+-------------+--------------+--------------+
```

### _EventStores_: Summary

In our current environment the concept of _EventStores_ is an easy way to distribute events inside the clustered environment, including a mechanism to retry processing an event, if the workflow failed.
For best performance events are created and distributed via a self-written stored procedure inside the database, which is regularly executed by a scheduler job.
This stored procedures also checks how many events exists to not flood the store, because in our tests we found out that the performances of _BAW19's_ resource adapter decreases, if there are too many events stored in an _EventStore_.
Of course each _EventStore_ is filled by an individual stored procedure.

You can achieve high parallel processing, because each _ActivationSpec_ can start up to 40 processes (or even more) at once.
In a cluster with eight servers this means up to 320 parallel executions.
Of course this number is not that impressive, when thinking about scaling containers in a _Kubernetes_ cluster, especially because server resources are shared with all other processes running on the same server.

As the current implementation is vendor specific and shipped with the _BAW19_, you have to create your own J2C resource adapter when dropping the _BAW19_.
This should not be too difficult (e.g. with timers and [SQL SKIP LOCK](https://vladmihalcea.com/database-job-queue-skip-locked/)).
Maybe other adapters already exist and can be used in a standard Java Enterprise scenario.


## CQRS & Kafka
This section is primarily based on the [official _Kafka_ website](https://kafka.apache.org/intro) and the corresponding section of Sebastian Daschner's book "Architecting Modern Java EE Applications".
To understand what _Event Sourcing is, take a look at [Martin Fowler's article on _Event Sourcing_](https://martinfowler.com/eaaDev/EventSourcing.html).

### CQRS
The _Command Query Responsibility Segregation (CQRS)_ is an event-driven architecture based on _Event Sourcing_.
An action that changes system's state (writes) is called a _command_, while an action that does not change the system's (read) is a _query_.
In a _CQRS_ architecture every action must either be a command or a query, therefore only command system can create events, while both can consume them.
The only connection between command and query services is an event store or event hub, which would be similar to the central database in a _Create Read Update Delete (CRUD)_-based system.
The communication between services and the event hub is done via interfaces, often REST-like HTTP services.

Each system represents the current state of its domain entities, but not the whole history of its state.
The event hub is the only source which contains all atomic events.
Because of that, _CQRS_ allows every service to scale individually, especially query services.

When the event hub is not available, command services can't store events in it.
This is similar to a traditional _CRUD_-based system and keeps the system from changing its state.
In contrast to that, even when the event hub is not available, the query services in a _CQRS_ system are still fully available and provide correct results, because they store the current state of their domain entities.
That is not true in a CRUD-based system, because the query services can't access the database.

### Kafka
_Apache Kafka_ is a distributed message broker which provides publish/subscribe approach like JMS.
On the official _Kafka_ website it claims to offer high performance and throughput.

_Kafka_ organizes messages (events) in topics where consumer groups can subscribe to.
Each message inside a topic is only consumed once per consumer group.

Producers are those client applications that publish (write) events to Kafka, and consumers are those that subscribe to (read and process) these events.
That means producers are command services, while consumers are query services in terms of _CQRS_.
These functionalities are offered due the _Producers API_ and _Kafka Consumers API_.
Next to these two API _Kafka_ offers an _Admin API_ to manage topics, clients etc., an _Stream API_ to implement stream processing of events, and a _Connect API_ with which e.g. services can receive every change to a database table.


# Summary 
As far as I understand, _Kafka_ offers functionality similar to JMS in easy-to-use APIs to produces or consume events.
The segregation into different topics and consumer groups is quite similar to the several _EventStores_ or _JMS_ queues that are used at my work to distribute events to services which implement the related workflow.
Of course, I expect using _Kafka_ for this allows a more harmonious / self-consistent integration, because developers can work with a more consistent API.
The _Admin_ API seems to offer a built-in possibility to manage all related things, which has to be self-created otherwise.

In terms of scaling and fault-tolerance, I don't see a real difference between _CQRS_ and an enterprise-ready database.
It all about the ability to scale the database (in _CQRS_ the event hub) in which events are stored.
Same for ability to increase fault-tolerance by running more than one instance (with synchronization of course).
As far as I know enterprise-ready databases (e.g. _Oracle_) are scalable in a good way.
As of now the database was never the bottleneck at my work so far, but external services out of our control. 
Scaling the producers and consumers is, in my point of view, independent of the topic of topic investigated here.
Containerized services (application services) are of course easier tp scale than traditional shared application servers.

I see _CQRS_ (and _Kafka_ as one framework for it) as a good event-driven architecture, which we have implemented at my work in a very similar way.
Most obvious difference is that we don't store each event permanent, but the whole state history, so we can track what happened to a single processed element.
_Kafka_ could be an alternative at my work, but would mean to reimplement the whole distribution system.
I think the distribution system we use itself is not our problem, because - regardless of using _Kafka_ or not - we would have to implement a way to read the events from the database.
_Kafka's Connect API_ could be one way to do this, but implementing an own JCA resource-adapter would be another one (among others).

_Kafka_ is not part of _Jakarta Enterprise Edition_ which would be against our goal to get independent of vendor-specific implementations.

