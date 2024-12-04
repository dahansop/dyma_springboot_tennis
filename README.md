# dyma_springboot_tennis

Projet fil rouge de la formation "Spring Boot" disponible sur Dyma : https://dyma.fr/formations/spring-boot

## la base de données

Pour démarrer la base de données PostgreSQL et l'outil d'aministration PGAdmin
Se placer dans le répertoire 'src/main/docker'

Exécuter la commande
'''docker compose up -d'''


Pour utiliser PGAdmin, ouvrir l'URL suivante dans un navigateur :
(http://localhost:8888)
login : pgadmin@pgadmin.com
password : pgadmin

Enregistrer la connection à la BDD
Clic droit sur server -> Register... -> Server
* onglet "general" dans le champs "name" : dyma-dev
* onglet "Connection" dans le champs "Host Name/address" : dyma-postgresql 
* onglet "Connection" dans le champs "Port" : 5432
* onglet "Connection" dans le champs "Maintenance database" : postgres
* onglet "Connection" dans le champs "Username" : postgres
* onglet "Connection" dans le champs "Password" : postgres
* onglet "Connection" cocher "Save password"

Pour arrêter les containers faire un ctrl+C

Pour tout détruire faire :
'''docker compose down --remove-orphans'''

## Application spring Boot

L'API est disponible : (http://localhost:9080)
