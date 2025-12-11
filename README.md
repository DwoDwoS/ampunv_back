# Ancien Meuble Pour Une Nouvelle Vie (AMPUNV)

Backend du projet **AMPUNV – Ancien Meuble Pour Une Nouvelle Vie**, une plateforme de vente et de revalorisation de meubles anciens.  
Ce backend est développé en **Java / Spring Boot**, utilise **Flyway** pour la gestion des migrations et une base PostgreSQL hébergée sur **Neon**.
C'est un projet d'un mois en solo dans le cadre de ma formation à ADA TECH SCHOOL.
Il reste quelques éléments à corriger et à adapter, mais c'est un projet dont je suis plutôt fier !

---

## Table des matières

1. [Présentation](#-présentation)
2. [Stack Technique](#-stack-technique)
3. [Architecture](#-architecture)
4. [Prérequis](#-prérequis)
5. [Installation](#-installation)
6. [Configuration](#-configuration)
7. [Migrations Flyway](#-migrations-flyway)
8. [Lancement du projet](#-lancement-du-projet)
9. [Structure du projet](#-structure-du-projet)
10. [Endpoints API (exemple)](#-endpoints-api-exemple)

---

## Présentation

**AMPUNV** est une plateforme permettant :

- La gestion et la mise en vente de meubles anciens  
- La valorisation de meubles restaurés  
- La consultation et l’achat de meubles uniques  
- Une gestion sécurisée des données via PostgreSQL hébergé sur **Neon**

Ce dépôt contient uniquement la partie **backend**.

---

## Stack Technique

- **Java 17+**
- **Spring Boot 3**
  - Spring Web  
  - Spring Data JPA  
  - Spring Validation  
- **PostgreSQL (Neon.tech)**
- **Flyway**
- **Maven**
- **Lombok**

---

## Architecture

L’application suit une architecture simple et claire inspirée du modèle MVC :

```
src/main/java/com/ampunv/
├── controller/
├── service/
├── repository/
├── model/
└── dto/
```


---

## Prérequis

- Java **17+**
- Maven 3.8+
- Un compte **Neon.tech**
- Un IDE (IntelliJ recommandé)

---

## Installation

1. **Cloner le projet**
   ```bash
   git clone https://github.com/DwoDwoS/ampunv_back.git
   cd ampunv
   ```

2. **Installer les dépendances**
   ```bash
   mvn clean install
   ```
## Configuration

Créer un fichier ```src/main/resources/application.properties``` :
```yaml
spring:
  datasource:
    url: jdbc:postgresql://<HOST>.neon.tech/<DATABASE>?sslmode=require
    username: <USERNAME>
    password: <PASSWORD>
  jpa:
    hibernate:
      ddl-auto: validate
    properties:
      hibernate:
        format_sql: true
    show-sql: true

  flyway:
    enabled: true
    baseline-on-migrate: true
```

## Récupérer les informations Neon

1. Créer un projet sur Neon.tech

2. Aller dans Connection Details

3. Copier :

  - Host
  
  - Database name
  
  - User
  
  - Password
  
  - URL JDBC

<i>Attention : ne jamais versionner vos identifiants.
Utilisez un .env ou des variables d’environnement pour le déploiement.</i>

## Migrations Flyway

Les migrations SQL doivent être placées dans :
```
src/main/resources/db/migration/
```

Exemple de fichier :

```sql
V1__create_meuble_table.sql
```

Flyway exécute automatiquement les scripts dans l'ordre croissant.
Il est nécessaire de faire **attention** au nommage, toujours V puis __ (il en faut deux et pas 1 seul).

## Lancement du projet

Démarrer l'application :
```bash
mvn spring-boot:run
```


Ou depuis votre IDE.

Il vous faudra forker ampunv_front également : 
- https://github.com/DwoDwoS/ampunv_front

## Structure du projet
```
AMPUNV/
 ├── src/
 │   ├── main/
 │   │   ├── java/com/ampunv/
 │   │   └── resources/
 │   │       └── db/migration/
 │   └── test/
 ├── pom.xml
 └── README.md
```

## Endpoints API (exemple)
**Meubles** 

| Méthode |	Endpoint	| Description |
| --- | --- | --- |
| GET	| ```/api/meubles```	| Liste des meubles |
| GET	| ```/api/meubles/{id}``` |	Détails d’un meuble |
| POST |	```/api/meubles```  |	Ajout d’un meuble |
| PUT |	```/api/meubles/{id}``` |	Mise à jour d’un meuble |
| DELETE |	```/api/meubles/{id}``` |	Suppression d’un meuble |

Les endpoints réels peuvent varier selon votre implémentation.

