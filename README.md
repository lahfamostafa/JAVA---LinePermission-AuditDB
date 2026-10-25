# LinePerm — gestion de fichiers et contrôle d’accès

LinePerm est une application Java en ligne de commande qui simule la gestion de
fichiers protégés par des droits de lecture, d’écriture et de suppression. Les
utilisateurs, les métadonnées et le contenu des fichiers simulés sont stockés
dans une base SQLite locale. L’application journalise certaines opérations et
propose des statistiques sur ces journaux.

> Les fichiers gérés par `touch`, `cat`, `nano` et `rm` sont des enregistrements
> dans la base de données : ces commandes ne manipulent pas directement les
> fichiers du système d’exploitation.

## Fonctionnalités présentes

- Inscription et connexion à un compte ; les mots de passe enregistrés sont
  hachés avec BCrypt.
- Création d’un enregistrement de fichier, affichage de la liste et consultation
  du contenu.
- Ajout de texte au contenu d’un fichier et suppression d’un enregistrement.
- Modification des droits accordés aux autres utilisateurs.
- Journalisation des opérations de lecture, d’écriture et de suppression, avec
  leur résultat.
- Consultation de statistiques : volume d’actions, accès refusés, utilisateurs,
  fichiers les plus consultés et répartition des opérations.

Ces fonctionnalités sont implémentées dans la console et les couches DAO/service.
Le dépôt ne contient pas de tests automatisés permettant de vérifier leur
comportement complet.

## Technologies et prérequis

- Java 17 ou version compatible.
- SQLite, via le pilote JDBC `sqlite-jdbc-3.36.0.3.jar`.
- jBCrypt `jbcrypt-0.4.jar`.
- Une console ; les commandes ci-dessous sont données pour PowerShell sous
  Windows.

Il n’y a pas de fichier Maven ou Gradle. Les deux bibliothèques JAR sont présentes
dans `lib/` dans l’environnement analysé, mais ce dossier est ignoré par Git.
Un clone GitHub ne les récupérera donc pas : placez les JAR requis dans `lib/`
avant de compiler. Les outils SQLite présents dans ce dossier ne sont pas
nécessaires au lancement de l’application.

## Préparer, compiler et lancer

Exécutez les commandes depuis la racine du dépôt. Vérifiez d’abord que les deux
JAR requis et `audit_db.db` sont présents. Le chemin de la base est relatif au
répertoire courant.

```powershell
$sources = Get-ChildItem .\src\main\java -Recurse -Filter *.java |
    ForEach-Object { $_.FullName }

javac -encoding UTF-8 `
    -cp ".\lib\sqlite-jdbc-3.36.0.3.jar;.\lib\jbcrypt-0.4.jar" `
    -d .\target\classes $sources

java -cp ".\target\classes;.\lib\sqlite-jdbc-3.36.0.3.jar;.\lib\jbcrypt-0.4.jar" `
    ma.youcode.lineperm.Main
```

Le schéma SQLite attendu comprend les tables `users`, `fichiers` et `logs`.
L’application active les clés étrangères à l’ouverture de la connexion, mais le
code ne contient pas de mécanisme de création ou de migration du schéma. Une base
déjà initialisée est donc nécessaire.

La base `audit_db.db` contient les données de l’application et peut être modifiée
pendant son utilisation. Faites-en une copie avant toute expérimentation avec des
données à conserver.

## Utilisation de la console

Les commandes sont saisies une par une. L’interface demande interactivement le
nom d’utilisateur et le mot de passe après `signup` ou `login`.

| Commande | Fonction observée |
| --- | --- |
| `signup` | Créer un compte puis tenter de l’ouvrir automatiquement. |
| `login` | Ouvrir une session. |
| `logout` | Fermer la session courante. |
| `ls` | Afficher les fichiers enregistrés et leurs droits. |
| `touch <nom>` | Créer un enregistrement de fichier appartenant à l’utilisateur connecté. |
| `cat <nom>` | Afficher le contenu si la lecture est autorisée. |
| `nano <nom>` | Saisir du texte et l’ajouter au contenu ; entrer une ligne contenant `EOF` pour terminer. |
| `chmod <droits> <nom>` | Ajouter ou retirer des droits accordés aux autres utilisateurs. |
| `rm <nom>` | Supprimer un enregistrement si l’utilisateur en a le droit. |
| `stats` | Ouvrir le menu de statistiques des journaux. |
| `help` | Afficher le texte d’aide actuel (le code affiche seulement `help`). |
| `exit` | Quitter l’application. |

Les commandes sur les fichiers (`ls`, `touch`, `cat`, `nano`, `chmod` et `rm`)
nécessitent une connexion. `stats` est accessible même sans connexion. Les
droits sont représentés par `r` (lecture), `w` (écriture) et `d` (suppression).
À la création, le propriétaire dispose des trois droits et les autres
utilisateurs n’en ont aucun. Par exemple :

```text
chmod rw rapport.txt
chmod -w rapport.txt
```

Dans `chmod`, les lettres indiquées sont activées ; si l’argument commence par
`-`, les lettres indiquées sont désactivées. Seul le propriétaire peut changer
ces droits. Les noms sont séparés par des espaces lors de l’analyse des
commandes ; les noms contenant des espaces ne sont donc pas pris en charge par
la console.

## Architecture et données

Le point d’entrée est `Main`, qui démarre `ConsoleApp`. La console interprète les
commandes et délègue les opérations aux services. Les services utilisent les DAO
pour accéder à SQLite ; les classes `model` représentent les utilisateurs,
fichiers et journaux.

```text
src/main/java/ma/youcode/lineperm/
├── Main.java                 Point d’entrée
├── ui/ConsoleApp.java        Boucle interactive et commandes
├── service/                  Comptes, fichiers, droits et statistiques
├── dao/                      Requêtes et accès aux données
├── database/DBConnection.java Connexion JDBC SQLite
├── model/                    User, Fichier et Log
├── files/                    Exemples de fichiers présents dans le dépôt
├── Files.txt                 Données textuelles annexes
└── Logs.log                  Journal textuel annexe
```

La base relationnelle contient :

- `users` : identifiant, nom d’utilisateur unique et mot de passe haché ;
- `fichiers` : nom unique, propriétaire, identifiant utilisateur, droits et
  contenu ;
- `logs` : utilisateur, fichier, action, résultat et horodatage, avec des clés
  étrangères vers les deux autres tables.

Les accès SQL des DAO utilisent généralement des requêtes préparées. La
connexion SQLite est partagée statiquement par `DBConnection` et activée avec
`PRAGMA foreign_keys = ON`.

## Vérifications effectuées

- `java -version` et `javac -version` : Java 17.0.12 est disponible.
- Compilation de toutes les sources avec les deux JAR présents : réussie.
- Lancement de l’application compilée, saisie de `exit` : démarrage et sortie
  réussis.
- Schéma SQLite consulté en lecture seule : les tables `users`, `fichiers` et
  `logs` correspondent aux accès observés dans les DAO.
- Aucun répertoire ou fichier de test automatisé, ni configuration de build ou
  CI, n’a été trouvé dans le dépôt.

La vérification d’exécution n’a pas testé les opérations qui écrivent dans la
base. Aucun audit de dépendances ni test de sécurité automatisé n’a été effectué.

## Limites et points d’attention

Les constats ci-dessous proviennent du code et du schéma examinés ; ils ne
constituent pas un audit de sécurité exhaustif.

| Priorité | Constat et impact |
| --- | --- |
| Élevée | `FileDao.delete` intercepte une erreur SQL puis renvoie `true`. Or la clé étrangère de `logs.fichier_id` ne prévoit pas de suppression en cascade : la suppression d’un fichier référencé par un journal peut échouer tout en étant annoncée comme réussie. |
| Moyenne | `stats` est accessible sans connexion et expose les statistiques globales des journaux, notamment les noms d’utilisateurs. Dans un usage multi-utilisateur, cela révèle des informations d’activité. |
| Moyenne | `rm` ne vérifie pas si le fichier existe avant de consulter ses droits ; un nom inconnu peut provoquer une erreur d’exécution plutôt qu’un message contrôlé. |
| Moyenne | `FileDao.save` utilise `PreparedStatement.execute()` pour un `INSERT`, puis traite son résultat booléen comme un indicateur de succès. `FileService.touchCommande` ne vérifie pas le retour du DAO et peut afficher un succès indépendamment de ce résultat. |
| Faible | `catCommande` imprime le contenu deux fois lorsqu’il n’est pas nul. |
| Faible | La commande `help` ne présente pas encore la liste des commandes, et l’analyse de `chmod` attend trois éléments sans vérifier que le troisième est présent. Certaines saisies incomplètes peuvent donc déclencher une erreur. |

Les droits applicatifs ne remplacent pas les permissions du système de fichiers.
Le contenu est stocké dans SQLite sans chiffrement applicatif visible. Les mots
de passe sont hachés, mais l’entrée console ne masque pas leur saisie. N’utilisez
pas cette application comme mécanisme de protection de données sensibles sans
renforcer et valider ces contrôles.

## Améliorations recommandées

1. Corriger les retours de succès et la gestion des erreurs dans les DAO et les
   services, notamment lors des suppressions contraintes par les journaux.
2. Vérifier explicitement les fichiers inexistants et toutes les formes de
   commandes avant d’accéder aux tableaux d’arguments.
3. Restreindre les statistiques aux utilisateurs autorisés et préciser le
   modèle d’accès attendu.
4. Ajouter des tests automatisés pour les droits, les cas limites des commandes,
   les opérations SQLite et les journaux.
5. Ajouter un système de build reproductible qui déclare les dépendances au lieu
   de dépendre de JAR locaux ignorés par Git.
6. Définir une stratégie de création et de migration du schéma SQLite.

## Contribution, licence et déploiement

Aucune licence, procédure de contribution, pipeline CI/CD ou procédure de
déploiement n’a été identifiée dans le dépôt. Ces éléments restent à définir si
le projet est destiné à être distribué ou maintenu par plusieurs contributeurs.
