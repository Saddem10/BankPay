# BankPay 🏦

**Contexte :**  
Le département de paiement de la banque est amené à recevoir des messages de la part des applications Back Office via une file IBM MQ Series. Ces messages vont transiter dans une application de routage pour être transférés vers d’autres destinations. Les utilisateurs ont aussi la possibilité d’ajouter des partenaires pour configurer les MQ.

## Technologies
- Java 17
- Spring Boot 3
- Spring Data JPA
- Spring Web
- Cache caffeine
- Spring Actuator
- Swagger
- IBM MQ
- Liquibase

**Base de données:**  
PostgreSQL  
nom de la base: bankDataBase  
nom du schéma: bank_schema  
Liquibase génère automatiquement les tables et gère les changements  

**Queue IBM:** 
BANK.QUEUE

**URL doc Swagger:**  
http://localhost:8080/swagger-ui.html

**URL actuator:**   
http://localhost:8080/actuator

❗ **NB: **   
l'API sendMessage(/mq/send) n'est pas supposée être dans cette application mais plutôt dans un autre système (celui qui va mettre les messages dans la queue), il est là juste pour simuler l'opération d'envoi des messages dans la queue, cette application présume que le contrat fait entre les deux équipes est de déposer les messages sous la forme: **expéditeur|contenu du message|date d'envoi**   
-> il existe un bouton sur le front appelé "mettre un message dans la queue" pour simuler l'opération

