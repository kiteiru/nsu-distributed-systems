#!/usr/bin/env bash

if [ ! -f /data/mongo-init.flag ]; then
    echo "Init replicaset"
    mongo mongodb://docker-mongodb-1:27017 mongo-setup-rs.js
    touch /data/mongo-init.flag
else
    echo "Replicaset already initialized"
fi