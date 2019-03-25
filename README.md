## Setup Cluster

### Replica 1

#### Replica 1: Node 1

```
docker run --rm --name replica1_node1 \
  -v "$(pwd)/replica_1/node_1/var/lib/faunadb:/var/lib/faunadb" \
  -v "$(pwd)/replica_1/node_1/var/log/faunadb:/var/log/faunadb" \
  -v "$(pwd)/replica_1/node_1/etc/faunadb.yml:/etc/faunadb.yml" \
  fauna/faunadb --config /etc/faunadb.yml
```

#### Replica 1: Node 2

```
docker run --rm --name replica1_node2 \
  -v "$(pwd)/replica_1/node_2/var/lib/faunadb:/var/lib/faunadb" \
  -v "$(pwd)/replica_1/node_2/var/log/faunadb:/var/log/faunadb" \
  -v "$(pwd)/replica_1/node_2/etc/faunadb.yml:/etc/faunadb.yml" \
  fauna/faunadb --config /etc/faunadb.yml --join 172.17.0.2
```

#### Replica 1: Node 3

```
docker run --rm --name replica1_node3 \
  -v "$(pwd)/replica_1/node_3/var/lib/faunadb:/var/lib/faunadb" \
  -v "$(pwd)/replica_1/node_3/var/log/faunadb:/var/log/faunadb" \
  -v "$(pwd)/replica_1/node_3/etc/faunadb.yml:/etc/faunadb.yml" \
  fauna/faunadb --config /etc/faunadb.yml --join 172.17.0.2
```

### Replica 2

#### Replica 2: Node 1

```
docker run --rm --name replica2_node1 \
  -v "$(pwd)/replica_2/node_1/var/lib/faunadb:/var/lib/faunadb" \
  -v "$(pwd)/replica_2/node_1/var/log/faunadb:/var/log/faunadb" \
  -v "$(pwd)/replica_2/node_1/etc/faunadb.yml:/etc/faunadb.yml" \
  fauna/faunadb --config /etc/faunadb.yml --join 172.17.0.3
```

#### Replica 2: Node 2

```
docker run --rm --name replica2_node2 \
  -v "$(pwd)/replica_2/node_2/var/lib/faunadb:/var/lib/faunadb" \
  -v "$(pwd)/replica_2/node_2/var/log/faunadb:/var/log/faunadb" \
  -v "$(pwd)/replica_2/node_2/etc/faunadb.yml:/etc/faunadb.yml" \
  fauna/faunadb --config /etc/faunadb.yml --join 172.17.0.3
```

#### Replica 2: Node 3

```
docker run --rm --name replica2_node3 \
  -v "$(pwd)/replica_2/node_3/var/lib/faunadb:/var/lib/faunadb" \
  -v "$(pwd)/replica_2/node_3/var/log/faunadb:/var/log/faunadb" \
  -v "$(pwd)/replica_2/node_3/etc/faunadb.yml:/etc/faunadb.yml" \
    fauna/faunadb --config /etc/faunadb.yml --join 172.17.0.2
```

### Replica 3

#### Replica 3: Node 1

```
docker run --rm --name replica3_node1 \
  -v "$(pwd)/replica_3/node_1/var/lib/faunadb:/var/lib/faunadb" \
  -v "$(pwd)/replica_3/node_1/var/log/faunadb:/var/log/faunadb" \
  -v "$(pwd)/replica_3/node_1/etc/faunadb.yml:/etc/faunadb.yml" \
  fauna/faunadb --config /etc/faunadb.yml --join 172.17.0.2
```

#### Replica 3: Node 2

```
docker run --rm --name replica3_node2 \
  -v "$(pwd)/replica_3/node_2/var/lib/faunadb:/var/lib/faunadb" \
  -v "$(pwd)/replica_3/node_2/var/log/faunadb:/var/log/faunadb" \
  -v "$(pwd)/replica_3/node_2/etc/faunadb.yml:/etc/faunadb.yml" \
  fauna/faunadb --config /etc/faunadb.yml --join 172.17.0.2
```

#### Replica 3: Node 3

```
docker run --rm --name replica3_node3 \
  -v "$(pwd)/replica_3/node_3/var/lib/faunadb:/var/lib/faunadb" \
  -v "$(pwd)/replica_3/node_3/var/log/faunadb:/var/log/faunadb" \
  -v "$(pwd)/replica_3/node_3/etc/faunadb.yml:/etc/faunadb.yml" \
  fauna/faunadb --config /etc/faunadb.yml --join 172.17.0.2
```

### Update Replication

```
docker exec replica1_node1 faunadb-admin status
```

```
docker exec replica1_node1 faunadb-admin update-replication replica_1 replica_2 replica_3
```

### Load Balancer
```
docker run --rm --name load_balancer -p 8080:8080 \
  -v "$(pwd)/load_balancer/etc/nginx/nginx.conf:/etc/nginx/nginx.conf" \
  nginx
```