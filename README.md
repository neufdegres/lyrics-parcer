# LyricsParser

Application de visualisation de paroles de chanson.

## Technologies utilisées

- Java (Android SDK)
- Côté serveur : PHP + Python

## Dépendances

- Java 17
- Python 3.10.12
- PHP 8.4.12
- Clé d'API DeepL

## Fonctionnalités de l'application

- Création de fiches de paroles de chanson manuelle dans l'application ou via l'upload d'un fichier texte
- Visualisation des paroles
- Edition ou suppression de fiches de paroles
- Traduction et "romanisation" des paroles (japonais uniquement)
- Recherche par terme de titre, paroles ou artiste
- Affichage des titres d'un même artiste
- Affichage des 5 ajouts les plus récents dans la page d'accueil

## Comment configurer l'application

- Coté serveur :
  - déplacer les fichiers du dossier `server/` dans un serveur Web (ex : Apache HTTP Server)
  - modifier le fichier [`config.properties`](android-app/config.properties) avec l'addresse du dossier contenant ces mêmes fichiers (ex: `https://0.0.0.0/LyricsParser`)
  - modifier le fichier [`deepl`](server/deepl) avec votre clé d'API générée sur le site de [DeepL](https://www.deepl.com/fr/translator)

- Coté application : ouvrir le dossier `android-app/` dans Android Studio puis exécuter

## Ajouter de nouvelles chansons via un fichier ".txt"

Un fichier [`lyrics.txt`](lyrics.txt) contenant 21 fiches de paroles est fourni dans le dépot.

Pour insérer ces fiches dans l'application, cliquer sur le bouton-texte `Ajouter de nouvelles chansons d'un fichier texte` (voir section suivante) de la page d'accueil.

Ensuite, sélectionner le fichier depuis votre téléphone puis cliquer sur "Ajouter".

## Captures d'écran de l'application

La totalité des captures d'écran se situent dans le dossier [`pics/`](pics/).

<div style="display: flex; flex-wrap: wrap; gap: 10px;">
  <img src="pics/accueil.jpg"  width="300" alt="Capture d'écran de la page d'accueil.">
  <img src="pics/traduit.jpg"  width="300" alt="Capture d'écran d'une page d'affichage des paroles, avec la traduction apparente.">
  <img src="pics/recherche.jpg"  width="300" alt="Capture d'écran de la page de recherche de terme.">
  <img src="pics/artiste.jpg"  width="300" alt="Capture d'écran d'une page d'affichage d'artiste.">
</div>
