#! /bin/bash

port=$1
poolSize=$2
batchSize=$3
batchTime=$4

java -jar build/libs/cs455-hw2.jar cs455.scaling.main.Main Server $port $poolSize $batchSize $batchTime
