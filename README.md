# dyma_springboot_tennis

Projet fil rouge de la formation "Spring Boot" disponible sur Dyma : https://dyma.fr/formations/spring-boot

## la base de données

Pour démarrer la base de données PostgreSQL et l'outil d'aministration PGAdmin
Se placer dans le répertoire 'src/main/docker'

Exécuter la commande

```bash
docker compose up -d
```


Pour utiliser PGAdmin, ouvrir l'URL suivante dans un navigateur :

* URL : <http://localhost:8888>
* login : pgadmin@pgadmin.com
* password : pgadmin

Enregistrer la connection à la BDD DEV
Clic droit sur server -> Register... -> Server
* onglet "general" dans le champs "name" : dyma-dev
* onglet "Connection" dans le champs "Host Name/address" : dyma-postgresql-dev
* onglet "Connection" dans le champs "Port" : 5432
* onglet "Connection" dans le champs "Maintenance database" : postgres
* onglet "Connection" dans le champs "Username" : postgres
* onglet "Connection" dans le champs "Password" : postgres
* onglet "Connection" cocher "Save password"

Enregistrer la connection à la BDD PROD
Clic droit sur server -> Register... -> Server
* onglet "general" dans le champs "name" : dyma-prod
* onglet "Connection" dans le champs "Host Name/address" : dyma-postgresql-prod
* onglet "Connection" dans le champs "Port" : 5432
* onglet "Connection" dans le champs "Maintenance database" : postgres
* onglet "Connection" dans le champs "Username" : postgres
* onglet "Connection" dans le champs "Password" : 5ML^Es%4U&DK6c
* onglet "Connection" cocher "Save password"

Pour arrêter les containers faire un ctrl+C

Pour tout détruire faire :

```bash
docker compose down --remove-orphans
```

## Exécuter l'application en local

Exécuter le run **run TennisApplication - dev**

URL : <http://localhost:8080/swagger-ui/index.html>

## Déploiement de l'application Spring Boot dans un conteneur (prod)

Créer l'image **dyma-tennis-api** Docker

```bash
docker build -t dyma-tennis-api .
```

Créer un conteneur à partir de cette image

```bash
docker run --name dyma-tennis -p 8080:8080 --net dyma-network -e SPRING_DATASOURCE_URL="jdbc:postgresql://dyma-postgres-prod:5432/postgres" -e SPRING_DATASOURCE_USERNAME="postgres" -e SPRING_DATASOURCE_PASSWORD="5ML^Es%4U&DK6c" dyma-tennis-api
```

Pour arrêter le conteneur faire Ctrl + c

Pour supprimer le conteneur

```bash
docker rm dyma-tennis
```

Pour supprimer l'image

```bash
docker image rm dyma-tennis-api
```

## graylog

* URL : <http://localhost:9000>
* login : admin
* password : admin

## Actuator

* URL : <http://localhost:8080/actuator>

Pour y avoir accès, il faut se connecter en tant qu'administrateur en utilisant la fonction "login" depuis Swagger.
* DEV : admin/admin
* PROD : admin/admin-prod


