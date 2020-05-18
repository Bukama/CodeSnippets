
# Container

## What are containers?
_from [Docker curriculum](https://docker-curriculum.com/#what-are-containers-)_

> The industry standard today is to use Virtual Machines (VMs) to run software applications. VMs run applications inside a guest Operating System, which runs on virtual hardware powered by the server’s host OS.
>
> VMs are great at providing full process isolation for applications: there are very few ways a problem in the host operating system can affect the software running in the guest operating system, and vice-versa. But this isolation comes at great cost — the computational overhead spent virtualizing hardware for a guest OS to use is substantial.
> 
> Containers take a different approach: by leveraging the low-level mechanics of the host operating system, containers provide most of the isolation of virtual machines at a fraction of the computing power.

## Why use containers?
_from [Docker curriculum](https://docker-curriculum.com/#why-use-containers-)_

> Containers offer a logical packaging mechanism in which applications can be abstracted from the environment in which they actually run. This decoupling allows container-based applications to be deployed easily and consistently, regardless of whether the target environment is a private data center, the public cloud, or even a developer’s personal laptop. This gives developers the ability to create predictable environments that are isolated from the rest of the applications and can be run anywhere.
> 
> From an operations standpoint, apart from portability containers also give more granular control over resources giving your infrastructure improved efficiency which can result in better utilization of your compute resources.



---
---
---

# TODO / Check

# Docker

* Setup Docker
* How to Create / Reuse images
* How to create multi layer images like in [this video](https://developers.redhat.com/commit-to-excellence-java-in-containers/?sc_cid=7013a000002DTukAAG)
* How to create Oracle-Database images
* How to create DB-Images with predefinied Schema but still updateable when new test data is needed

# Application

* How liberty gets configured, datasources etc
* How is this done dynamically? (even needed in containers)
* Using [Testcontainers](https://www.testcontainers.org/) / [maven-liberty-plugin / Microshed](https://openliberty.io/guides/microshed-testing.html)
* How are `ResEnvEntries` possible in liberty?

# Container for deployment

* How to deploy to container which are used as different stages?
* How to size them?
* How to configure them (different credentials / url etc) (when only base x86 linux is provided)
   


