# Microservices with the Akka Platform

| Technology                                                                                                                                               | Version |                             Type                              |
|----------------------------------------------------------------------------------------------------------------------------------------------------------|:-------:|:-------------------------------------------------------------:|
| <img align="left" alt="Scala" width="40" src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/scala/scala-original.svg" />   Scala                    | 2.13.8  | ![programming](https://img.shields.io/badge/-programming-red) |
| <img align="left" alt="Akka Actors Typed" width="40" src="https://www.svgrepo.com/show/353381/akka.svg" /> Akka Actors Typed                             | 2.6.19  | ![programming](https://img.shields.io/badge/-programming-red) |
| <img align="left" alt="Akka Http" width="40" src="https://www.svgrepo.com/show/353381/akka.svg" /> Akka Http                                             | 10.2.9  | ![programming](https://img.shields.io/badge/-programming-red) |
| <img align="left" alt="Akka Management" width="40" src="https://www.svgrepo.com/show/353381/akka.svg" /> Akka Management                                 |  1.1.3  | ![programming](https://img.shields.io/badge/-programming-red) |
| <img align="left" alt="Akka Persistence" width="40" src="https://www.svgrepo.com/show/353381/akka.svg" /> Akka Persistence Jdbc                          |  5.0.4  | ![programming](https://img.shields.io/badge/-programming-red) |
| <img align="left" alt="Akka Projection" width="40" src="https://www.svgrepo.com/show/353381/akka.svg" /> Akka Projection                                 |  1.2.4  | ![programming](https://img.shields.io/badge/-programming-red) |
| <img align="left" alt="sbt" width="40" src="https://upload.wikimedia.org/wikipedia/commons/4/43/Sbt-logo.svg" /> SBT                                     |  1.7.1  |      ![build](https://img.shields.io/badge/-build-blue)       |
| <img align="left" alt="PostgreSQL" width="40" src="https://raw.githubusercontent.com/github/explore/80688e429a7d4ef2fca1e82350fe8e3517d3494d/topics/postgresql/postgresql.png" /> PostgreSQL                                                                                       |    14.5.0     |     ![build](https://img.shields.io/badge/-infra-orange)      |
| <img align="left" alt="Kubernetes" width="40" src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/kubernetes/kubernetes-plain.svg" /> Kubernetes     | 1.24.0  |      ![infa](https://img.shields.io/badge/-infra-orange)      |
| <img align="left" alt="KinD" width="40" src="https://d33wubrfki0l68.cloudfront.net/d0c94836ab5b896f29728f3c4798054539303799/9f948/logo/logo.png" /> KinD | 0.14.0  |      ![infa](https://img.shields.io/badge/-infra-orange)      |
| <img align="left" alt="Helm" width="40" src="https://cncf-branding.netlify.app/img/projects/helm/stacked/color/helm-stacked-color.svg" /> Helm           |  3.9.3  |      ![infa](https://img.shields.io/badge/-infra-orange)      |


## Introduction
The goal of this project is to create an microservices application using different tech-stacks. So I'll replicate it in several ways. Mi intention for the first version is the use of Scala + Akka for the backend and React with TypeScript for the front end.

I already have some hands-on experience using **Docker**, **Kubernetes**, **KinD**, **Helm**, and **Skaffold**; but I want to use this chance to better understand them and documented some of the most used steps. I will also add **Itsio** as a service mesh in some point during the development. 

This section page will resume the general steps that are independent of the tech-stack and will add more documentation as I move forward in the project.

The project will be a simple shopping  system with three microservices that will communicate each other using gRPC. I'm also aiming to follow the [Event Sourcing](https://martinfowler.com/eaaDev/EventSourcing.html) using Kafka, enabling CQRS and event based communication with other services.

TODO: Add more information details about the project.

TODO: Add a high level architecture diagram of the project.

### Kubernetes and KinD

I'm using `KinD` to create my local cluster, so I can develop and test in a multi-node environment. 
I'll use a semi-standard KinD cluster with two nodes.

To re-create the same node as me, just run: 

```batch
kind create cluster --name cluster01 --config .kind/01-kind-cluster.yaml
```

The status of the pods can be checked with:
```batch
kubectl get pod -n kube-system
```

To install Nginx as entry point (maybe latter I'll use an Ingress controller):

```batch
kubectl apply -f .kind/02-nginx-deployment.yaml
```
Wait until de *deployment* is created:

```batch
kubectl wait --namespace default \
  --for=condition=ready pod \
  --selector=app=nginx-app \
  --timeout=90s
```

To create the service and tight it to an end point:

```batch
kubectl apply -f .kind/03-nginx-service.yaml
```
To verify that the *End Point* has en IP assigned: 

```batch
kubectl get ep nginx-service
```
Browse to http://localhost:30080/ and verify that the Nginx welcome screen is displayed.

To check that the pod can be found from inside the cluster using the service name this command can be used:

```batch
kubectl run -i --rm --restart=Never curl-client --image=curlimages/curl --command -- curl -s 'http://nginx-service:80'
```
The command looks a bit complex, but it will do the following:
1. Create a Pod with a small container based on the Docker image `curlimages/curl`, which contains the `curl` command
2. Run the `curl -s 'http://nginx-service:80'` command inside the container and redirect the output to the Terminal using the `-i` option
3. Delete the Pod using the `--rm` option


<details>
  <summary>Kubernetes components overview</summary>

The following diagram is from the [Kubernetes.io](https://kubernetes.io/) site and shows a high-level overview of a Kubernetes cluster component:
![Kubernetes Cluster](https://d33wubrfki0l68.cloudfront.net/2475489eaf20163ec0f54ddc1d92aa8d4c87c96b/e7c81/images/docs/components-of-kubernetes.svg)

### kubelet

You may hear a worker node referred to as a `kubelet`. The kubelet is an agent that runs on all worker nodes, and it is responsible for running the actual containers.


### kube-proxy

Contrary to the name, `kube-proxy` is not a proxy server at all. kube-proxy is actually responsible for routing network communication between a Pod and the network.

### Container runtime

Each node also needs a container runtime. A container runtime is responsible for running the containers. While `Docker` is a container runtime, it is not the only runtime option available. Over the last year, other options have become available and are quickly replacing Docker as the preferred container runtime. The two most prominent Docker replacements are `CRI-O` and `containerd`.

### Extras
- Kubernetes is updated every 4 months. This includes upgrades to the base Kubernetes cluster components and the `kubectl` utility. Is possible to run into a version mismatch 
between a cluster and the `kubectl` command, requiring to either upgrade or download the `kubectl` executable. To check the version of both run `kubectl version` command, which will output the version of both the API server and the kubectl client.

- To retrieve a list of resources a cluster supports, use the `kubectl api-resources` command.

### Other useful commands:

```batch
kubectl create namespace first-attempts
kubectl delete namespace first-attempts
kubectl config set-context $(kubectl config current-context) --namespace=hands-on
```
</details>

## Helm
**Helm** is an open source tool used for packaging and deploying applications on Kubernetes. It is often referred to as the Kubernetes package manager because of its 
similarities to any other package manager. A Helm chart can be thought of as a Kubernetes package. Charts contain the declarative Kubernetes resource files required to
deploy an application. Similar to an RPM package, it can also declare one or more dependencies that the application needs in order to run.

<details>
<summary>Helm's useful commands</summary>

Helm can be used to deploy Redis, an in-memory cache, to Kubernetes by using a chart from an upstream repository. This can be performed using Helm’s install command, as illustrated here:
```shell
helm install redis bitnami/redis --namespace=redis
```

If a new version of the redis chart becomes available, users can upgrade to the new version using the upgrade command, as follows:
```shell
helm upgrade redis bitnami/redis --namespace=redis
```

Helm provides the rollback command, as illustrated here:
```shell
helm rollback redis 1 --namespace=redis
```

Finally, Helm provides the ability to remove redis altogether with the uninstall command, as follows:
```shell
helm uninstall redis --namespace=redis
```

Helm provides the `repo` subcommand to allow users to manage configured chart repositories. Here are the five `repo` subcommands:

- `add`: To add a chart repository
- `list`: To list chart repositories
- `remove`: To remove a chart repository
- `update`: To update information on available charts locally from chart repositories
- `index`: To generate an index file, given a directory containing packaged charts

</details>

Helm has a concept called release history. When a Helm chart is installed for the first time, Helm adds that initial revision to the history. The history is further modified as revisions increase via upgrades, keeping various snapshots of how the application was configured at varying revisions.

First, add a new helm repo (if it is not added yet):
```shell
helm repo add bitnami  https://charts.bitnami.com 
```

Next, update your local repositories:

```shell
helm repo update
```

As I spend some time explaining how Helm works, I'll use it to install all the infrastructure elements that I'm using for this project.

### Installing PostgreSQL

I'm using PostgreSQL as one of the persistence data storage. The data in the PostgreSQL database need to persist across pod restarts.
To achieve this, I created a `PersistentVolume` resource:

```shell
sudo mkdir /var/postgresql
````

```shell
kubectl apply -f .helm/04-postgresql-pv.yaml
```

Create a Persistent Volume Claim (PVC) to request the storage allocated in the previous step.

```shell
kubectl apply -f .helm/05-postgres-pvc.yaml
```

Use `kubectl` get to check if the **PVC** is connected to the **PV** successfully:

```shell
kubectl get pvc
```
The status column shows that the claim is `Bound`.

I'm storing the PostgreSQL username and passwords in a Kubernetes *secret* and I'm referencing those secrets in the configuration file for the helm chart, so first I created the secret:

```shell
kubectl apply -f .helm/06-postgres-secret.yaml
```

#### Install the Helm chart.

```shell
helm install \
shopping-db \
--namespace default \
-f .helm/postgresql-values.yaml bitnami/postgresql
```

Wait for the database to start.

```shell
kubectl wait --namespace default \
--for=condition=ready pod \
--selector=app.kubernetes.io/instance=shopping-db \
--timeout=180s
```

#### Connect to PostgreSQL Client 

Export the `POSTGRES_PASSWORD` and `POSTGRES_ADMIN_PASSWORD` environment variables to be able to log into the PostgreSQL instance:
```shell
export POSTGRES_PASSWORD=$(kubectl get secret --namespace default postgres-secret -o jsonpath="{.data.password}" | base64 -d)
```
```shell
export POSTGRES_ADMIN_PASSWORD=$(kubectl get secret --namespace default postgres-secret -o jsonpath="{.data.postgresPassword}" | base64 -d)
```

To connect to the database from outside the cluster execute the following commands:

```shell
 kubectl port-forward --namespace default svc/shopping-db-postgresql 5432:5432 & PGPASSWORD="$POSTGRES_PASSWORD" psql --host 127.0.0.1 -U shopping_user -d v -p 5432
```
I connect using [DBeaver](https://dbeaver.io/) and from there execute the `.scripts/dll/01-create_tables.sql` script that contains transactional tables and akka projections as well.

## gRPC

<details>
  <summary>gRPC concepts</summary>

`gRPC` is a transport mechanism for request/response and streaming use cases. It can run in almost any environment with bindings to many programming languages. It takes a 
**schema-first** approach, where your protocol is declared in a *Protobuf* service descriptor. From the service descriptor the source code for the messages, client and 
server stubs are generated.

It has several advantages:
- Schema-first design favors well-defined and decoupled service interfaces over brittle ad-hoc solutions.
- The Protobuf-based wire protocol is efficient, well-known, and allows compatible schema evolution.
- It is based on HTTP/2 which allows multiplexing several data streams over a single connection.
- Streaming requests and responses are first class.
- There are tools available for many languages allowing seamless interoperability between clients and services written in different languages.

That makes it well-suited for:
- Connections between internal services
- Connecting to external services that expose a gRPC API (even ones written in other languages)
- Serving data to web or mobile front-ends

[Akka gRPC](https://doc.akka.io/docs/akka-grpc/current/) is a gRPC library for the Akka ecosystem. It has support for Maven, gradle and sbt. The gRPC servers and clients use 
*Akka Streams*, Scala `Future` and Java `CompletionStage` in the user facing API.
</details>

For Akka, the `akka.grpc.sbt.AkkaGrpcPlugin` auto generates the source code for the messages, client and server stubs from a protobuf service descriptor file (`.proto`) when the project is compiled, so it is enough to run:

```shell
sbt compile
```

See: [ShoppingCartService.proto](./src/main/protobuf/ShoppingCartService.proto)


## Event Sourcing
<details>
<summary>Event Sourcing Concepts</summary>

Event Sourcing is a modelling technique where you not only model the state of your business but also the transitions between states. Then, instead of storing the current state the datastore saves these increments.

A difference to persistence based on Create-Read-Update-Delete (CRUD) data-stores is that we don’t need to map our imagined entities to a database model up-front. Instead, we model the entities, and the events that affect their state.

To update an entity’s state we use commands from the outside and events on the inside:

- `Commands`: The state of the entity can be changed only by sending commands to it. The commands are the "external" API of an entity. Commands request state changes. The current state may reject the command, or it may accept it producing zero, one or many events (depending on the command and the current state).
- `Events`: The events represent changes of the entity’s state and are the only way to change it. The entity creates events from commands. Events are an internal mechanism for the entity to mutate the state, other parties can’t send events. Other parts of the application may listen to the created events. Summing up, events are facts new tab.

Events will be serialized and published into the Journal table in the database. The Journal can then be consumed by the emitting entity or by third parties.

The events are persisted to the datastore, while the entity state is kept in memory. In case of a restart the latest state gets rebuilt by replaying the events from the Event Journal.

A client trying to mutate the state of an entity will produce a command message and send it to the entity. Commands are a type of message. Sometimes, commands include the address of the sender, the entity can use the sender address to send a message back with a reply.

Events are facts, while commands are requests for a state mutation.

### Advantages of Event Sourcing

Event Sourcing achieves persistence by storing state changes as historical events that capture business activity. This decouples the events from the storage mechanism, allowing them to be aggregated, or placed in a group with logical boundaries. Event Sourcing is one of the patterns that enables concurrent, distributed systems to achieve high performance, scalability and resilience.

In a distributed architecture, Event Sourcing provides the following advantages:

- In a traditional CRUD model, entities use a dual representation as a mutable object in memory, and a mutable row in a relational database table. This leads to the infamous object relational impedance mismatch. Object-relational mappers bridge this divide, but bring new complexities of their own. The event sourcing model treats the database as an append-only log of serialized events. It does not attempt to model the state of each entity or the relationships between them directly in the database schema. This greatly simplifies the code that writes to and reads from the database.
- The history of how an entity reached its current state remains in the stored events. Consistency between Transactional data and audit data are the same data which guarantees consistency between them.
- Event Sourcing brings the ability to analyze the event stream and derive important business information from it — perhaps things that were not even thought about when designing the events. You can add new views on our system’s activity without making the write-side more complicated.
- It improves write performance, since the data store only needs to append the events. There are no updates and no deletes.
- Event Sourced systems are easy to test and debug. Commands and Events can be simulated for test purposes. The event log provides a good record for debugging. When detecting an issue in production, you can replay the event log in a controlled environment to understand how an entity reached the bad state.

</details>


# Resources

## Akka
- [Akka Platform Guide](https://developer.lightbend.com/docs/akka-platform-guide/index.html)
- [Akka Documentation](https://doc.akka.io/docs/akka/current/typed/guide/introduction.html#how-to-get-started)
- [Akka in Action, Second Edition](https://www.manning.com/books/akka-in-action-second-edition)
- [Build Cloud Native Microservices on Kubernetes](https://go.lightbend.com/akka-platform-workshop-part-1-on-demand-recording)
- [Implementing Microservices with Akka](https://developer.lightbend.com/docs/akka-platform-guide/microservices-tutorial/index.html)
- [Programming Reactive Systems (Scala 2 version)](https://www.coursera.org/learn/scala2-akka-reactive/home/info)

## Kubernetes
- [Kubernetes from Zero to Hero!](https://www.youtube.com/watch?v=X48VuDVv0do) from Youtube
- [Kubernetes – An Enterprise Guide - Second Edition](https://www.packtpub.com/product/kubernetes-an-enterprise-guide-second-edition/9781803230030) from Packt pub 
- [Managing Kubernetes resources using Helm - Second Edition](https://www.packtpub.com/product/managing-kubernetes-resources-using-helm/9781803242897) from Packt pub 