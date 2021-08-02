# SOAP Source Connector

## Build

```
mvn clean install
```

## Integration Test

Source code for a simple integration test is placed under IT folder.

To build and run a docker compose environment containing the basic setup for the test just run:

```
./build_and_run.sh
```

All the files needed are on a volume under host-volumes folder, so they are available and in sync with connect container.

In order to get mocked responses for the SOAP Server we set up a standalone wiremock container. All files needed to configure it are under ```/host-volumes/wiremock```

Then to start the connector just run ```./post-source-connector.sh```

To check all is working run a console consumer into kafka container

```
docker compose exec kafka /bin/bash
kafka-console-consumer --bootstrap-server localhost:9092 --topic soap.test
```