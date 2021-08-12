# Zeebe-demo-dpc

Created as an introduction to Zeebe for the MIFOS initiative, this is a 
zeebe demo project intended to demonstrate the use and implementation 
of a simple bpmn, hello-process. This bpmn consists of service and 
receive tasks, an exclusive gateway containing a conditional and 
default path, and a timeout event.


Find out more about the [MIFOS initiative](https://mifos.org/)

##Set up your enviroment

1. Make sure you have [Docker](https://www.docker.com/) installed. If you don't, you can install it 
by following [their installation guide](https://docs.readthedocs.io/en/latest/development/install.html)


2. Please ensure you can use docker compose


3. Clone the [<b> zeebe-docker-compose</b> repository](https://github.com/camunda-community-hub/zeebe-docker-compose) from github. This will 
allow you to run zeebe, camunda operate, kafka and elasticsearch in 
a docker container
```bash
$ git clone https://github.com/camunda-community-hub/zeebe-docker-compose.git
```

4. Clone the [<b> ph-ee-exporter</b> repository](https://github.com/openMF/ph-ee-exporter) from github. This will
allow you to send data to Kafka
```bash
$ git clone https://github.com/openMF/ph-ee-exporter.git```
```

5. Run maven package in the ph-ee-exporter project, to generate a jar 
exporter-1.0.0-SNAPSHOT.jar and note the absolute path of this jar
and kafka-clients-2.4.0.jar on your computer


6. Navigate to zeebe-docker-compose/operate
```bash
$ cd zeebe-docker-compose/operate
```

7. Replace the contents of the docker-compose.yml file with the following:
```bash
version: "2"

networks:
  zeebe_network:

volumes:
  zeebe_data:
  zeebe_elasticsearch_data:
  zookeeper_data:
    driver: local
  kafka_data:
    driver: local

services:
  zeebe:
    image: camunda/zeebe:1.1.0
    environment:
      - ZEEBE_LOG_LEVEL=debug
      - ZEEBE_BROKER_EXPORTERS_KAFKA_CLASSNAME=hu.dpc.rt.kafkastreamer.exporter.KafkaExporter
      - ZEEBE_BROKER_EXPORTERS_KAFKA_JARPATH=/exporter.jar
    ports:
      - "26500:26500"
      - "9600:9600"
    volumes:
      - zeebe_data:/usr/local/zeebe/data
      - ./application.yaml:/usr/local/zeebe/config/application.yaml
      - [YOUR PATH FOR exporter-1.0.0-SNAPSHOT.jar GOES HERE]:/exporter.jar
      - [YOUR PATH FOR kafka-clients-2.4.0.jar GOES HERE]:/usr/local/zeebe/lib/kafka-clients-2.4.0.jar
    depends_on:
      - elasticsearch
    networks:
      - zeebe_network
  operate:
    image: camunda/operate:1.1.0
    ports:
      - "8080:8080"
    depends_on:
      - zeebe
      - elasticsearch
    volumes:
      - ../lib/application.yml:/usr/local/operate/config/application.yml
    networks:
      - zeebe_network
  elasticsearch:
    container_name: elasticsearch
    image: docker.elastic.co/elasticsearch/elasticsearch-oss:7.10.2
    ports:
      - "9200:9200"
    environment:
      - discovery.type=single-node
      - cluster.name=elasticsearch
      - "ES_JAVA_OPTS=-Xms512m -Xmx512m"
    volumes:
      - zeebe_elasticsearch_data:/usr/share/elasticsearch/data
    networks:
      - zeebe_network
  zookeeper:
    image: docker.io/bitnami/zookeeper:3.7
    ports:
      - "2181:2181"
    volumes:
      - "zookeeper_data:/bitnami"
    environment:
      - ALLOW_ANONYMOUS_LOGIN=yes
    networks:
      - zeebe_network
  kafka:
    container_name: kafka
    image: docker.io/bitnami/kafka:2
    ports:
      - "9092:9092"
    volumes:
      - "kafka_data:/bitnami"
    environment:
      - KAFKA_CFG_ZOOKEEPER_CONNECT=zookeeper:2181
      - ALLOW_PLAINTEXT_LISTENER=yes
    depends_on:
      - zookeeper
    networks:
      - zeebe_network
```

8. Replace lines 27 and 28 with the absolute path of exporter-1.0.0-
SNAPSHOT.jar and kafka-clients-2.4.0.jar on your computer, in the 
indicated positions.


9. Start docker within zeebe-docker-compose/operate with the 
force-recreate option enabled to ensure docker runs with the new docker-
compose.yml file
```bash
$ docker-compose up --force-recreate
```

<b>The following steps are only necessary if you wish to use kafka-tool to view kafka messages:</b>

10. Install [Kafka Tool](https://www.kafkatool.com/)
11. Add a new cluster, with Kafka Cluster Version 2.4. Zookeeper Host should be localhost and Zookeeper Port should be 2181
12. You will now be able to connect to the server, but not the broker. To solve this update you etc/hosts file to look like this:
```bash
127.0.0.1       localhost kafka <ENTER THE BROKER ID HERE>
```


## Running the Zeebe demo

1. Clone the repository for the zeebe demo application
```bash
$ git clone https://github.com/peterforis/zeebe-demo-dpc.git
```
2. Navigate to zeebe-docker-compose/operate
```bash
$ cd zeebe-docker-compose/operate
```
3. Start docker within zeebe-docker-compose/operate 
```bash
$ docker-compose up 
```
4. Navigate to zeebe-docker-compose/bin
```bash
$ cd zeebe-docker-compose/operate
```

5. Note the absolute path of hello-process.bpmn in the zeebe-demo-dpc project on your computer


7. Deploy bpmn:
```bash
$ ./zbctl deploy --insecure <ABSOLUTE PATH OF hello-process.bpmn GOES HERE>
```

8. Run ZeebeDemoApplication


For further documentation, about this project, and Payment Hub EE please visit the [MIFOS gitbook documentation](https://mifos.gitbook.io/docs/)

License
-------

The project is licensed under the BSD license.