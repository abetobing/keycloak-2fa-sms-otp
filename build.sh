#!/bin/bash

#mvn clean package -DskipTests
cp target/*.jar ../keycloak-compose/providers/
cd ../keycloak-compose
docker-compose restart keycloak
#docker exec -it keycloak-compose_keycloak_1 /bin/bash /opt/keycloak/bin/kc.sh build
#docker exec -it keycloak-compose_keycloak_1 /bin/bash /opt/keycloak/bin/kc.sh start-dev --auto-build
echo 'done!'
