## Setup Cluster

### Replica 1

#### Replica 1: Node 1

```
docker run --rm --name replica1_node1 -p 8443:8443 \
    -v "$(pwd)/replica_1/node_1/var/lib/faunadb:/var/lib/faunadb" \
    -v "$(pwd)/replica_1/node_1/var/log/faunadb:/var/log/faunadb" \
    -v "$(pwd)/replica_1/node_1/etc/faunadb.yml:/etc/faunadb.yml" \
    fauna/faunadb --config /etc/faunadb.yml
```

#### Replica 1: Node 2

```
docker run --rm --name replica1_node2 -p 8444:8443 \
    -v "$(pwd)/replica_1/node_2/var/lib/faunadb:/var/lib/faunadb" \
    -v "$(pwd)/replica_1/node_2/var/log/faunadb:/var/log/faunadb" \
    -v "$(pwd)/replica_1/node_2/etc/faunadb.yml:/etc/faunadb.yml" \
    fauna/faunadb --config /etc/faunadb.yml --join 172.17.0.2
```

#### Replica 1: Node 3

```
docker run --rm --name replica1_node3 -p 8445:8443 \
    -v "$(pwd)/replica_1/node_3/var/lib/faunadb:/var/lib/faunadb" \
    -v "$(pwd)/replica_1/node_3/var/log/faunadb:/var/log/faunadb" \
    -v "$(pwd)/replica_1/node_3/etc/faunadb.yml:/etc/faunadb.yml" \
    fauna/faunadb --config /etc/faunadb.yml --join 172.17.0.2
```

### Replica 2

#### Replica 2: Node 1

```
docker run --rm --name replica2_node1 -p 8446:8443 \
    -v "$(pwd)/replica_2/node_1/var/lib/faunadb:/var/lib/faunadb" \
    -v "$(pwd)/replica_2/node_1/var/log/faunadb:/var/log/faunadb" \
    -v "$(pwd)/replica_2/node_1/etc/faunadb.yml:/etc/faunadb.yml" \
    fauna/faunadb --config /etc/faunadb.yml --join 172.17.0.2
```

#### Replica 2: Node 2

```
docker run --rm --name replica2_node2 -p 8447:8443 \
    -v "$(pwd)/replica_2/node_2/var/lib/faunadb:/var/lib/faunadb" \
    -v "$(pwd)/replica_2/node_2/var/log/faunadb:/var/log/faunadb" \
    -v "$(pwd)/replica_2/node_2/etc/faunadb.yml:/etc/faunadb.yml" \
    fauna/faunadb --config /etc/faunadb.yml --join 172.17.0.2
```

#### Replica 2: Node 3

```
docker run --rm --name replica2_node3 -p 8448:8444 \
    -v "$(pwd)/replica_2/node_3/var/lib/faunadb:/var/lib/faunadb" \
    -v "$(pwd)/replica_2/node_3/var/log/faunadb:/var/log/faunadb" \
    -v "$(pwd)/replica_2/node_3/etc/faunadb.yml:/etc/faunadb.yml" \
    fauna/faunadb --config /etc/faunadb.yml --join 172.17.0.2
```

### Replica 3

#### Replica 3: Node 1

```
docker run --rm --name replica3_node1 -p 8449:8443 \
    -v "$(pwd)/replica_3/node_1/var/lib/faunadb:/var/lib/faunadb" \
    -v "$(pwd)/replica_3/node_1/var/log/faunadb:/var/log/faunadb" \
    -v "$(pwd)/replica_3/node_1/etc/faunadb.yml:/etc/faunadb.yml" \
    fauna/faunadb --config /etc/faunadb.yml --join 172.17.0.2
```

#### Replica 3: Node 2

```
docker run --rm --name replica3_node2 -p 8450:8443 \
    -v "$(pwd)/replica_3/node_2/var/lib/faunadb:/var/lib/faunadb" \
    -v "$(pwd)/replica_3/node_2/var/log/faunadb:/var/log/faunadb" \
    -v "$(pwd)/replica_3/node_2/etc/faunadb.yml:/etc/faunadb.yml" \
    fauna/faunadb --config /etc/faunadb.yml --join 172.17.0.2
```

#### Replica 3: Node 3

```
docker run --rm --name replica3_node3 -p 8451:8444 \
    -v "$(pwd)/replica_3/node_3/var/lib/faunadb:/var/lib/faunadb" \
    -v "$(pwd)/replica_3/node_3/var/log/faunadb:/var/log/faunadb" \
    -v "$(pwd)/replica_3/node_3/etc/faunadb.yml:/etc/faunadb.yml" \
    fauna/faunadb --config /etc/faunadb.yml --join 172.17.0.2
```

### Replication

```
docker exec replica1_node2 faunadb-admin status
```