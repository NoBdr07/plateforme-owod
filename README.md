# OWOD – Plateforme de l'AIPDA

OWOD est une application web dédiée aux designers, développée pour l'AIPDA (Association Internationale pour la Promotion du Design en Afrique). La plateforme permet aux designers de créer un compte et d'être visibles dans le catalogue afin de promouvoir leurs travaux.

## Table des Matières

    Introduction
    Installation et Configuration
        Pré-requis
        Frontend (Angular 17)
        Backend (Java 21 et Spring)
    Déploiement sur AWS
        Déploiement du Frontend
        Déploiement du Backend
    Base de Données et Stockage
    Tests et Qualité de Code
    Contribuer

## Introduction

Le projet OWOD a pour but de mettre en avant les designers travaillant en rapport avec l'art africain en leur offrant une plateforme pour créer et gérer leur profil. Les utilisateurs peuvent consulter une page d'accueil avec le "designer de la semaine", parcourir un catalogue de designers filtrable selon différents critères, consulter des profils détaillés, et créer ou modifier leur propre compte.
Architecture du Projet

### Le projet est divisé en quatres parties :

  <u>Frontend :</u>
    Développé avec Angular 17, il offre une interface moderne et intuitive pour la consultation du catalogue et la gestion des profils.
    Déploiement : AWS Amplify (déploiement automatique par git push).

  <u>Backend :</u>
    Réalisé en Java 21 avec Spring et exposant une API REST, il gère l'authentification, les opérations CRUD sur les profils et la logique métier.
    Déploiement : AWS Elastic Beanstalk.

  <u>Base de Données :</u>
    La persistance est assurée par MongoDB, hébergée sur MongoDB Atlas.

  <u>Stockage des Images :</u>
    Les images des designers (photos de profil et réalisations) sont stockées dans un bucket S3 sur AWS.

### Technologies Utilisées

  Frontend : Angular 17  
  Backend : Java 21, Spring  
  API : REST  
  BDD : MongoDB Atlas  
  Stockage : AWS S3  
  Déploiement : AWS Amplify (frontend), AWS Elastic Beanstalk (backend)  

## Installation et Configuration
### Pré-requis

  Compte AWS avec accès à Amplify, Elastic Beanstalk et S3.  
  Accès à MongoDB Atlas.  
  Node.js et npm pour le frontend.  
  Java 21, Maven pour le backend.  

### Frontend (Angular 17)

  Se positionner dans le répertoire :

`cd frontend`

Installer les dépendances :

`npm install`

Configuration :

  Les variables d'environnement sont confidentielles. Elles seront transmises aux nouveaux contributeurs séléctionnés par l'AIPDA.


### Backend (Java 21 et Spring)

   Se positionner dans le répertoire :

`cd backend`

Installation :

        mvn clean install 

## Déploiement sur AWS
### Déploiement du Frontend

  Le déploiement du frontend est entièrement automatisé.
  Il suffit d'effectuer un git push pour déclencher le déploiement via AWS Amplify.

### Déploiement du Backend

  Préparer le package :  
        Compiler avec mvn clean package en ayant le profil prod active dans application.properties.  
        Créer un fichier ZIP contenant le JAR généré ainsi que les dossiers .ebextensions et .platform (qui sont dans le dossier target).  
  Déployer via AWS Elastic Beanstalk :  
        Connecte-toi à la console AWS et accède à l'environnement Owod-api-env-3.  
        Clique sur "Charger et déployer" et dépose le fichier ZIP préparé.  

## Base de Données et Stockage

  MongoDB Atlas :
    La base de données est hébergée sur MongoDB Atlas. L'URL de connexion doit être renseignée dans la variable d'environnement MONGO_URI du backend.
    L'adresse mail et le mdp vous serons donné par l'association, vous aurez ensuite toutes les infos sur Mongo Atlas pour pouvoir vous connecter à la DB.

  AWS S3 :
    Le bucket owod-images est utilisé pour stocker les images (photos de profil et réalisations des designers).

## Tests et Qualité de Code

  Des tests unitaires et d'intégration sont présents à la fois dans le frontend et le backend.
  Le coverage actuel est d'environ 50% et il est important de l'améliorer pour garantir la robustesse de l'application.

## Contribuer

Pour toute nouvelle personne assignée par l'AIPDA sur le projet, il faudra dans un premier temps créer l'utilisateur dans AWS.  
Un identifiant et un mot de passe seront générés et transmis à ce nouvel utilisateur, lui donnant accès au projet sur AWS.
Il est important de noter que cet accès permet de faire des actions sensibles pouvant conduire à casser la version déployée du projet.  
N'effectuez sur AWS que des actions dont vous connaissez les conséquences et la portée.

Ensuite, l'accès au repository GitHub de l'association sera nécessaire.

Pour contribuer :

Forker le repository.
Créer une branche dédiée :

  `git checkout -b feature/nom-de-la-feature`

Committer tes changements :

    git commit -m "Description de la feature"

Pousser la branche et ouvrir une Pull Request.
