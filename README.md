# FaunaDB: Operational Resilience Demo

In this tutorial we will demonstrate FaunaDB resilience capabilities in the face of operational failures. For that, we will first set up a fully workable FaunaDB cluster through a series of simple steps and then verify its availability under a number of error scenarios.

#### Table of Contents
* [Set up cluster](#set-up-cluster)
* [Scenarios](#scenarios)
* [Conclusions](#conclusions)


## Prerequisites
In order to run the examples below, you will need to have [Docker](https://www.docker.com/) setup in your machine. If you don't have it already, please follow the instructions on [Get Started with Docker](https://www.docker.com/get-started).

## Set up cluster

The cluster will consist of three replicas each made up of three nodes:

![alt text](images/faunadb_cluster.svg "FaunaDB Cluster")


>A node is a computer with a unique network address running the FaunaDB database software.

> A replica is named group of one or more co-located nodes containing a complete copy of the data.

In front of the cluster there's a Load Balancer which will distribute the requests using RoundRobin.


For putting together this architecture we will levearge [Docker Compose](https://docs.docker.com/compose/) features. Let's create a file named `docker-compose.yml` with the following content:

```
version: '3'

services:
  # Replica 1
  replica1_node1:
      container_name: replica1_node1
      image: lregnier/faunadb
      # First node initializes the cluster. The rest
      # of the nodes join the cluster through it.
      command: '--replica_name replica1 --init'

  replica1_node2:
      container_name: replica1_node2
      image: lregnier/faunadb
      command: '--replica_name replica1 --join replica1_node1' 

  replica1_node3:
      container_name: replica1_node3
      image: lregnier/faunadb
      command: '--replica_name replica1 --join replica1_node1' 

  # Replica 2
  replica2_node1:
      container_name: replica2_node1
      image: lregnier/faunadb
      command: '--replica_name replica2 --join replica1_node1'

  replica2_node2:
      container_name: replica2_node2
      image: lregnier/faunadb
      command: '--replica_name replica2 --join replica1_node1' 

  replica2_node3:
      container_name: replica2_node3
      image: lregnier/faunadb
      command: '--replica_name replica2 --join replica1_node1' 

  # Replica 3
  replica3_node1:
      container_name: replica3_node1
      image: lregnier/faunadb
      command: '--replica_name replica3 --join replica1_node1'

  replica3_node2:
      container_name: replica3_node2
      image: lregnier/faunadb
      command: '--replica_name replica3 --join replica1_node1' 

  replica3_node3:
      container_name: replica3_node3
      image: lregnier/faunadb
      command: '--replica_name replica3 --join replica1_node1' 

  # Load Balancer 
  load_balancer:
      container_name: load_balancer
      image: nginx
      volumes:
        - './load_balancer/etc/nginx/nginx.conf:/etc/nginx/nginx.conf'
      ports:
          - '8443:8443'
```

-----------

Execute `docker ps`:

```
CONTAINER ID        IMAGE               COMMAND                  CREATED            STATUS             PORTS                            NAMES
151aecb7771d        nginx               "nginx -g 'daemon of…"   2 minutes ago      Up 2 minutes       80/tcp, 0.0.0.0:8443->8443/tcp   load_balancer
1a9ea356893f        fauna/faunadb       "faunadb-entrypoint.…"   2 minutes ago      Up 2 minutes       7500-7501/tcp, 8443/tcp          replica1_node1
b3167cf3b049        fauna/faunadb       "faunadb-entrypoint.…"   2 minutes ago      Up 2 minutes       7500-7501/tcp, 8443/tcp          replica1_node2
df02a28de6b5        fauna/faunadb       "faunadb-entrypoint.…"   2 minutes ago      Up 2 minutes       7500-7501/tcp, 8443/tcp          replica1_node3
d3d2f8bcd4f0        fauna/faunadb       "faunadb-entrypoint.…"   2 minutes ago      Up 2 minutes       7500-7501/tcp, 8443/tcp          replica2_node1
57e049c5578d        fauna/faunadb       "faunadb-entrypoint.…"   2 minutes ago      Up 2 minutes       7500-7501/tcp, 8443/tcp          replica2_node2
9e51c29b7400        fauna/faunadb       "faunadb-entrypoint.…"   2 minutes ago      Up 2 minutes       7500-7501/tcp, 8443/tcp          replica2_node3
e1b71e5ed550        fauna/faunadb       "faunadb-entrypoint.…"   2 minutes ago      Up 2 minutes       7500-7501/tcp, 8443/tcp          replica3_node1
1d299969c679        fauna/faunadb       "faunadb-entrypoint.…"   2 minutes ago      Up 2 minutes       7500-7501/tcp, 8443/tcp          replica3_node2
79f93c8db669        fauna/faunadb       "faunadb-entrypoint.…"   2 minutes ago      Up 2 minutes       7500-7501/tcp, 8443/tcp          replica3_node3
```

Execute `faunadb-admin status`:

```
Replica: replica1 (data+log)
============================
Status  State  WorkerID  Log Segment  Address      Owns   Goal   HostID                              
up      live   512       Segment-0    172.18.0.9   34.3%  34.3%  c1ec8f33-cfad-4dab-8745-4b56c0939f6c
up      live   519       Segment-2    172.18.0.3   32.5%  32.5%  2454a1cb-b4d6-419c-8f2c-095193f3138a
up      live   520       Segment-1    172.18.0.5   33.2%  33.2%  f95e248b-def3-4b5e-a880-bba4810ff645

Replica: replica2 (compute)
===========================
Status  State  WorkerID  Log Segment  Address      Owns   Goal   HostID                              
up      live   513       none         172.18.0.6    0.0%   0.0%  2bf4ab38-574c-4e5c-8a56-cb2e2eb17e60
up      live   516       none         172.18.0.2    0.0%   0.0%  a8546fa6-5fb0-4eeb-b6ae-8a1d617ed131
up      live   518       none         172.18.0.10   0.0%   0.0%  573fd03c-575a-475b-bef5-28550a234f63

Replica: replica3 (compute)
===========================
Status  State  WorkerID  Log Segment  Address      Owns   Goal   HostID                              
up      live   514       none         172.18.0.7    0.0%   0.0%  05a6ccf1-411f-4935-aef7-bea7513a71ac
up      live   515       none         172.18.0.4    0.0%   0.0%  e1b2c2d3-be82-415d-a1f5-91f78bc9329d
up      live   517       none         172.18.0.8    0.0%   0.0%  db4f575e-17c4-458c-b828-d551230c9afa
```

Execute `faunadb-admin update-replication replica1 replica2 replica3`:

```
Replica: replica1 (data+log)
============================
Status  State  WorkerID  Log Segment  Address      Owns   Goal   HostID                              
up      live   512       Segment-0    172.18.0.9   34.3%  34.3%  c1ec8f33-cfad-4dab-8745-4b56c0939f6c
up      live   520       Segment-1    172.18.0.5   33.2%  33.2%  f95e248b-def3-4b5e-a880-bba4810ff645
up      live   519       Segment-2    172.18.0.3   32.5%  32.5%  2454a1cb-b4d6-419c-8f2c-095193f3138a

Replica: replica2 (data+log)
============================
Status  State  WorkerID  Log Segment  Address      Owns   Goal   HostID                              
up      live   516       Segment-0    172.18.0.2   31.9%  31.9%  a8546fa6-5fb0-4eeb-b6ae-8a1d617ed131
up      live   513       Segment-1    172.18.0.6   30.8%  30.8%  2bf4ab38-574c-4e5c-8a56-cb2e2eb17e60
up      live   518       Segment-2    172.18.0.10  37.3%  37.3%  573fd03c-575a-475b-bef5-28550a234f63

Replica: replica3 (data+log)
============================
Status  State  WorkerID  Log Segment  Address      Owns   Goal   HostID                              
up      live   517       Segment-0    172.18.0.8   32.5%  32.5%  db4f575e-17c4-458c-b828-d551230c9afa
up      live   515       Segment-1    172.18.0.4   37.9%  37.9%  e1b2c2d3-be82-415d-a1f5-91f78bc9329d
up      live   514       Segment-2    172.18.0.7   29.6%  29.6%  05a6ccf1-411f-4935-aef7-bea7513a71ac
```


## Scenarios
### 1. Kill a node 
### 2. Kill a whole replica
### 3. Skew clocks on remaining nodes 




## Conclusions
// TODO: elaborate below points

- FaunaDB doesn't depend on specific hardware
- FaunaDB doesn't depend on clocks
- FaunaDB architecture is unique as it offers masterless, multi-cloud, multi-region, active-active clustering features.