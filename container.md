
# Container

## What are containers?

_from [Docker documentation - Part 1: Orientation and setup](https://docs.docker.com/get-started/)_

**Note**: At this part of the Docker documentation, there is also a comparision of VM vs. container with an picture for visualization.

> Fundamentally, a container is nothing but a running process, with some added encapsulation features applied to it in order to keep it isolated from the host and from other containers.
> One of the most important aspects of container isolation is that each container interacts with its own private filesystem;
> this filesystem is provided by a Docker image.
> An image includes everything needed to run an application - the code or binary, runtimes, dependencies, and any other filesystem objects required.

_from [Docker curriculum](https://docker-curriculum.com/#what-are-containers-)_

> The industry standard today is to use Virtual Machines (VMs) to run software applications.
> VMs run applications inside a guest Operating System, which runs on virtual hardware powered by the server’s host OS.
>
> VMs are great at providing full process isolation for applications:
> there are very few ways a problem in the host operating system can affect the software running in the guest operating system, and vice-versa.
> But this isolation comes at great cost — the computational overhead spent virtualizing hardware for a guest OS to use is substantial.
> 
> Containers take a different approach: by leveraging the low-level mechanics of the host operating system, containers provide most of the isolation of virtual machines at a fraction of the computing power.

## Why use containers?
_from [Docker curriculum](https://docker-curriculum.com/#why-use-containers-)_

> Containers offer a logical packaging mechanism in which applications can be abstracted from the environment in which they actually run.
> This decoupling allows container-based applications to be deployed easily and consistently, regardless of whether the target environment is a private data center, the public cloud, or even a developer’s personal laptop.
> This gives developers the ability to create predictable environments that are isolated from the rest of the applications and can be run anywhere.
> 
> From an operations standpoint, apart from portability containers also give more granular control over resources giving your infrastructure improved efficiency which can result in better utilization of your compute resources.


# Docker

_from [Docker documentation - Part 1: Orientation and setup](https://docs.docker.com/get-started/)_

> Docker is a platform for developers and sysadmins to build, run, and share applications with containers. The use of containers to deploy applications is called containerization. Containers are not new, but their use for easily deploying applications is.
> 
> Containerization is increasingly popular because containers are:
>  
> * Flexible: Even the most complex applications can be containerized.
> * Lightweight: Containers leverage and share the host kernel, making them much more efficient in terms of system resources than virtual machines.
> * Portable: You can build locally, deploy to the cloud, and run anywhere.
> * Loosely coupled: Containers are highly self sufficient and encapsulated, allowing you to replace or upgrade one without disrupting others.
> * Scalable: You can increase and automatically distribute container replicas across a datacenter.
> * Secure: Containers apply aggressive constraints and isolations to processes without any configuration required on the part of the user.

## Setup Docker

### Local developer environment (Windows)

Install `Docker Desktop`, see
 
* [Docker documentation - Part 1: Orientation and setup](https://docs.docker.com/get-started/)
* [Install Docker Desktop (for Windows)](https://docs.docker.com/docker-for-windows/install/)

### CI-Server (Linux)

**TODO**: How to use Docker on a "naked" linux CI-Server

## Creating Docker images

### What is a Dockerfile?
_from [Docker curriculum](https://docker-curriculum.com/#dockerfile)_

> A Dockerfile is a simple text file that contains a list of commands that the Docker client calls while creating an image.
> It's a simple way to automate the image creation process.
> The best part is that the commands you write in a Dockerfile are almost identical to their equivalent Linux commands. This means you don't really have to learn new syntax to create your own dockerfiles.

_from  [Docker documentation - Part 2: Build and run your image](https://docs.docker.com/get-started/part2/)_

> Dockerfiles describe how to assemble a private filesystem for a container, and can also contain some metadata describing how to run a container based on this image.

### Build an Image

This section contains several links to guides and blogs about how to (best) create Docker images.

* [Docker documentation - Part 2: Build and run your image](https://docs.docker.com/get-started/part2/)
* [Docker Blog: Intro Guide to Dockerfile Best Practices](https://www.docker.com/blog/intro-guide-to-dockerfile-best-practices/)
* [Docker documentation](https://docs.docker.com/develop/develop-images/dockerfile_best-practices/)
* [Full Dockerfile reference](https://docs.docker.com/engine/reference/builder/)

### Share images

**TODO**: Nexus? 


## Conclusion



The information about exposing a service etc. (e.g. the port) inside the container and to the "outer world" needs to be configured when starting the image.



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
   


