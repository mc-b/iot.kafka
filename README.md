IoT und Kafka Beispiele
-----------------------

Für die Beispiele werden folgende Teile benötigt:
* [IoTKit V3](https://github.com/mc-b/iotkitv3)
* [lernkube Umgebung](https://github.com/mc-b/lernkube)

### Builden

Am besten in der [lernkube Umgebung](https://github.com/mc-b/lernkube) mit gestartetem [Maven Container](https://github.com/mc-b/duk/tree/master/compiler).

Im gestarteten Maven Container:

	cd /src
	git clone https://github.com/mc-b/iot.kafka
	mvn clean package
	exit
	
Auf der Git/Bash (ausserhalb des Containers)

	cd data/src/iot.kafka	
	docker build -f Dockerfile.pipe -t misegr/iot-kafka-pipe .
	docker build -f Dockerfile.consumer -t misegr/iot-kafka-consumer .
	docker build -f Dockerfile.alert -t misegr/iot-kafka-alert .
	
Auf das [IoTKit](https://github.com/mc-b/iotkitv3) ist das [MQTTPublish](https://os.mbed.com/teams/IoTKitV3/code/MQTTPublish/) Programm zu laden. Vor dem Compilieren sind die Einträge `host` und ggf. `port` anzupassen. Der `host` entspricht der IP-Adresse der VM die mittels [lernkube](https://github.com/mc-b/lernkube) erstellt wurde.
	
### Funktionsweise

* [IoTKit](https://github.com/mc-b/iotkitv3) --> MQTT-Protokoll --> [MQTT Broker](https://mosquitto.org/) --> [MQTT-Kafka-Bridge](https://github.com/jacklund/mqttKafkaBridge) 
--> [Pipe](https://github.com/mc-b/iot.kafka/blob/master/src/main/java/ch/mc_b/iot/kafka/Pipe.java) und [CSVConsumer](https://github.com/mc-b/iot.kafka/blob/master/src/main/java/ch/mc_b/iot/kafka/CSVConsumer.java) und [Alert](https://github.com/mc-b/iot.kafka/blob/master/src/main/java/ch/mc_b/iot/kafka/AlertConsumer.java) 

### Starten

	kubectl create -f duk/iot/mosquitto.yaml
	kubectl create -f duk/kafka
	kubectl create -f iot.kafka
	
In den Entsprechenden Logs der Container stehen die Meldungen drin, z.B.:

	logs iot-kafka-pipe