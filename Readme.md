# Microservices with the Akka Platform

| Technology                                                                                                                                               | Version |
|----------------------------------------------------------------------------------------------------------------------------------------------------------|:-------:|
| <img align="left" alt="Scala" width="40" src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/scala/scala-original.svg" />   Scala                    | 2.13.8  |
| <img align="left" alt="Akka" width="40" src="https://www.svgrepo.com/show/353381/akka.svg" /> Akka Actors                                                | 2.6.19  |
| <img align="left" alt="sbt" width="40" src="https://upload.wikimedia.org/wikipedia/commons/4/43/Sbt-logo.svg" /> SBT                                     |  1.7.1  |
| <img align="left" alt="Kubernetes" width="40" src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/kubernetes/kubernetes-plain.svg" /> Kubernetes                                                                                       | 1.24.0  |
| <img align="left" alt="KinD" width="40" src="https://d33wubrfki0l68.cloudfront.net/d0c94836ab5b896f29728f3c4798054539303799/9f948/logo/logo.png" /> KinD | 0.14.0  |


## Introduction
The goal of this project is to create an microservices application using different tech-stacks. So I'll replicate it in several ways. Mi intention for the first version is the use of Scala + Akka for the backend and React with TypeScript for the front end.

I already have some hands-on experience using Docker, Kubernetes, KinD, Helm, and Skaffold; but I want to use this chance to better understand them and documented some of the most used steps. I will also add Itsio as a service mesh in some point during the develoipment. 

This section page will resume the general steps that are independent of the tech-stack and will add more documentation as I move forward in the project.


I'm using KinD to create my local cluster, so I can develop and test in a multi-node environment. 
I'll use a semi-standard KinD cluster with two nodes, the only part that I'm replacing is the CNI; I'm using **Calico** instead of the default **Kindnet**.

To re-create the same node as me, just run: 

```batch
kind create cluster --name cluster01 --config .\kind\kind-cluster.yml
```




# Resources

## Akka
- [Akka Platform Guide](https://developer.lightbend.com/docs/akka-platform-guide/index.html)
- [Implementing Microservices with Akka](https://developer.lightbend.com/docs/akka-platform-guide/microservices-tutorial/index.html)
  - [Part 1 - Developer Set Up and gRPC Cart Service](https://go.lightbend.com/akka-platform-workshop-part-1-on-demand-recording?_ga=2.191337898.1858362480.1660105671-1972591103.1660105671)


## Kubernetes
- [Kubernetes from Zero to Hero!](https://www.youtube.com/watch?v=X48VuDVv0do)
- [Kubernetes – An Enterprise Guide - Second Edition](https://www.packtpub.com/product/kubernetes-an-enterprise-guide-second-edition/9781803230030) from Packt pub 