#! /bin/bash

hostName=$1
hostPort=$2
sendRate=$3

java -jar build/libs/cs455-hw2.jar cs455.scaling.main.Main Client $hostName $hostPort $sendRate
