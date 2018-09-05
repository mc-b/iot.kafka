#!/bin/bash
#
#	Installationsscript iot.kafka

# Abhaengige Images
docker pull confluentinc/cp-kafka:latest
docker pull confluentinc/cp-zookeeper:latest
docker pull devicexx/mqtt-kafka-bridge 

