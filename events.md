# EventDriven Design

Modern architectures rely on short running processes.
Often processes rely on other processes' outcome and are therefore event-driven.
One tool, often used for this, is [Apache Kafka](https://kafka.apache.org/).

As far as I understood _Kafka_'s way of working, I'm not sure if it is a real substitution for the system at my work.
On my work we use a very old, vendor specific solution (see ["Current scenario" section](#current-scenario)), and I'm convinced that we need to migrate to a modern environment / tech-stack. 
I don't know if someone, especially from management, shares my opinion, but I learned that you should not just make something bad, without being able to present a better and valid solution.
 
Therefore, I want to investigate this and find out which possibilities we have.
I will describe our current situation and then investigate if and maybe how it can be transformed into _Kafka_ or if there is another, more fitting solution.

# Current scenario

In this chapter I'll describe the current scenario, divided into three parts.
The first part will show the overall process, while the second one will describe the technical environment.
In the last part I'll show the problems we have and why I think that we should or even need to migrate to a more modern environment.

## Process description

First I want to describe the steps of the process, without going into functional (in terms of business related) details. 

1. At the beginning the system retrieves XML-files with up to 50.000 single elements, which are stored as-is in the database.
2. A component searches for new stored files and adds their primary key to a JMS-queue.
3. The component which consumes those JMS-messages then loads the XML, unmarshalls it, does some validation which can't be done using XSD (e.g. is the sender allowed to send such a file), and if everything is fine stores the 50.000 single elements in a structured (database normalization) form.
4. For each element an entry in a so called "_EventStore_" (see below) is created and picked up by the first "action" (don't want to use the word process here) of the process engine.
5. The process engine picks up a single element, performs further, single element based validation and again stores the result in the database.
   Again entries in two (let's assume the element was valid) _EventStore_ are made for further processing. 
6. The second actions picks up results of the overall validation or of all single elements of a received XML-File (see step 1) and creates an XML-file including the result, which is transmitted back to the sender as an answer to the received file.
7. Parallel to the second action, a third one picks up the data of a valid single element, extracts some values out of it and applies them to the "storage" the system where other components can access the data.

As you can see process is cut into short, single steps, with the result of each step is stored into the database.
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

* _tWAS 9.0.0.x_ does not get any updates anymore, only _tWAS 9.0.5.x_ (which can be used with a _Liberty_ core) is supported, but our operations department does not offer that versions.
* The IBM JDK is not developed anymore, because IBM joined the circle of firms, which support _AdoptOpenJDK_.
  There is still support yet, but it will end in a few years, and as said no Java 9+ will be provided.
* The supported _JavaEE_ version of both servers, _tWAS 9.0.0.x_ and _BAW19_, are long outdated and libraries / APIs are not supported anymore.
  For example _Hibernate 4.2.21_ is the last supported _Hibernate_ version which only requires _JPA 2.0_ (_JavaEE 6_), but due bugs in the _BAW19_ only an even older version (_4.2.3_) works without issues.
  As a comparison: Currently the _Hibernate_ team has already released version _6.0.0.Alpha_.
* To model _BAW19_ business workflows you have to use a special IDE, called the _IBM Integration Designer (IID)_.
  The _IID_ is based on a very old, outdated _Eclipse_ version and therefore many plugins are not supported or outdated.
  On top of that the _IID_ is full of bugs, e.g. claiming that it can't compile the sources even that it's accurate.
  You then have to restart the _IID_ several times (or even more often) until the _IID_ finally "decides" to compile your flawless code.
  Even with enterprise support you don't get fixes for that, or at least not in time.
  So every need to change the sources is literally a projects risk, because you never know if you can make the change (in time). 
* Development takes extremely long, because the servers take extremely long to start up and deploy.
  This makes integration tests, e.g. using _Arquillian_, take ages.
  Hot-deployment, e.g. while developing the frontend, is not supported by both application servers
* The environment is a shared environment and not exclusive to a specific project.
* Deployments are made manually by the operations department.
  This takes much time and even that they use scripts for some things it's very error-prone.
* No automated deployments are possible at the moment.
* Scaling the environment or setting up a new stage requires much effort, money and time. 
* The old technology stack leads to problems getting external developing support.

# Event driven solutions

In this section I want to take a short look into the solutions we currently use and what I (don't) like about them.

## JMS

The _Java Message Service (JMS)_ provides message queueing and publish-subscribe technology.
At my work we use _JMS_ in the message queueing style to distribute events automatically inside the cluster.
So every node can pick up a new message after it has finished.
A retry mechanism is also supported:
When a consuming component fails during processing a message, it can add the message back to the queue.

On the other hand:
As the component, which fills the queue, is also running on every node, we implemented a mechanism which blocks the other nodes, so only one node searches for new files (see [process step 2](#process-description)) at a time.
That means that even if one node drops out, the queue gets still filled.
In a containerized environment you could just deploy one replica of this "providing" instance and _Kubernetes_ would take care that there is always one running container.

Another downside of queues is that the actual queues objects are somehow not connected to your system.
Because of that, in our environment we check, if the related object (here XML file) has already been processed, right after the messages has been put out of the queue.

**Open question**:
How to use a _JMS_ inside a containerized environment?

**Summary (so far)**:
The _JMS_ takes away much work from you by providing an automated distribution and retry mechanic.
It fits into our environment very well, where we have strong cuts between the processing steps.


## JCA - EventStores

# Kafka

# Comparison with current 
