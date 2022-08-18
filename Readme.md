# Microservices with the Akka Platform

| Technology                                                                                                                                               | Version |                             Type                              |
|----------------------------------------------------------------------------------------------------------------------------------------------------------|:-------:|:-------------------------------------------------------------:|
| <img align="left" alt="Scala" width="40" src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/scala/scala-original.svg" />   Scala                    | 2.13.8  | ![programming](https://img.shields.io/badge/-programming-red) |
| <img align="left" alt="Akka" width="40" src="https://www.svgrepo.com/show/353381/akka.svg" /> Akka Actors                                                | 2.6.19  | ![programming](https://img.shields.io/badge/-programming-red) |
| <img align="left" alt="sbt" width="40" src="https://upload.wikimedia.org/wikipedia/commons/4/43/Sbt-logo.svg" /> SBT                                     |  1.7.1  |      ![build](https://img.shields.io/badge/-build-blue)       |
| <img align="left" alt="Kubernetes" width="40" src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/kubernetes/kubernetes-plain.svg" /> Kubernetes     | 1.24.0  |      ![infa](https://img.shields.io/badge/-infra-orange)      |
| <img align="left" alt="KinD" width="40" src="https://d33wubrfki0l68.cloudfront.net/d0c94836ab5b896f29728f3c4798054539303799/9f948/logo/logo.png" /> KinD | 0.14.0  |      ![infa](https://img.shields.io/badge/-infra-orange)      |
| <img align="left" alt="Helm" width="40" src="https://cncf-branding.netlify.app/img/projects/helm/stacked/color/helm-stacked-color.svg" /> Helm           |  3.9.3  |      ![infa](https://img.shields.io/badge/-infra-orange)      |


## Introduction
The goal of this project is to create an microservices application using different tech-stacks. So I'll replicate it in several ways. Mi intention for the first version is the use of Scala + Akka for the backend and React with TypeScript for the front end.

I already have some hands-on experience using **Docker**, **Kubernetes**, **KinD**, **Helm**, and **Skaffold**; but I want to use this chance to better understand them and documented some of the most used steps. I will also add **Itsio** as a service mesh in some point during the development. 

This section page will resume the general steps that are independent of the tech-stack and will add more documentation as I move forward in the project.


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

Export the `POSTGRES_PASSWORD` environment variable to be able to log into the PostgreSQL instance:
```shell
export POSTGRES_PASSWORD=$(kubectl get secret --namespace default shopping-db-postgresql -o jsonpath="{.data.password}" | base64 -d)
```
To connect to the database from outside the cluster execute the following commands:

```shell
 kubectl port-forward --namespace default svc/shopping-db-postgresql 5432:5432 & PGPASSWORD="$POSTGRES_PASSWORD" psql --host 127.0.0.1 -U shopping_user -d v -p 5432
```
I connect using [DBeaver](https://dbeaver.io/) and from there execute the `.scripts/dll/01-create_tables.sql` script that contains transactional tables and akka projections as well.





# Resources

## Akka
- [Akka Platform Guide](https://developer.lightbend.com/docs/akka-platform-guide/index.html)
- [Implementing Microservices with Akka](https://developer.lightbend.com/docs/akka-platform-guide/microservices-tutorial/index.html)
  - [Part 1 - Developer Set Up and gRPC Cart Service](https://go.lightbend.com/akka-platform-workshop-part-1-on-demand-recording?_ga=2.191337898.1858362480.1660105671-1972591103.1660105671)


## Kubernetes
- [Kubernetes from Zero to Hero!](https://www.youtube.com/watch?v=X48VuDVv0do) from Youtube
- [Kubernetes – An Enterprise Guide - Second Edition](https://www.packtpub.com/product/kubernetes-an-enterprise-guide-second-edition/9781803230030) from Packt pub 
- [Managing Kubernetes resources using Helm - Second Edition](https://www.packtpub.com/product/managing-kubernetes-resources-using-helm/9781803242897) from Packt pub 