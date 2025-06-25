# algo2-project

## Description

Ce projet implémente un calculateur d’itinéraires multimodal basé sur l’algorithme A* pour les réseaux de transports publics belges (STIB, TEC, SNCB, De Lijn). Il lit les données GTFS, construit un graphe de transport, puis permet de rechercher le chemin le plus rapide entre deux arrêts à une heure donnée, en tenant compte des horaires, correspondances, bonus/malus selon les options, et de la marche à pied.

⚠️ **Attention : Ce projet nécessite au moins 5 Go de mémoire vive (RAM) pour fonctionner correctement, en raison de la taille des données GTFS et des structures utilisées.**

## Fonctionnalités principales

- Lecture et parsing des fichiers GTFS (routes, stops, trips, stop_times)
- Construction d’un graphe orienté où chaque nœud est un arrêt et chaque arête un trajet ou une marche à pied
- Recherche de chemin optimal avec A* : prise en compte des horaires, temps d’attente, bonus/malus selon les préférences de transport
- Affichage détaillé de l’itinéraire (mode, ligne, horaires, correspondances)
- Options de personnalisation : préférences ou exclusions de modes de transport via la ligne de commande

## Prérequis

- Java 11 ou supérieur
- Au moins 5 Go de RAM disponible
- Les fichiers GTFS doivent être présents dans le dossier `GTFS/`

## Structure du projet

```
algo2-project/
├── bin/                # Fichiers .class générés 
├── GTFS/               # Données GTFS (STIB, TEC, SNCB, De Lijn)
├── src/                # Code source Java
├── team                # consigne du projet
├── Makefile
└── README.md
```

## Compilation

Pour compiler le projet, placez-vous dans le dossier du projet et lancez :

```sh
make
```

## Exécution

Pour exécuter le projet, utilisez :

```sh
make ARGS='"<Départ>" "<Arrivée>" <Heure> [options]'
```

Exemple :

```sh
make ARGS='"Alveringem Nieuwe Herberg" "Aubange" 10:40:00'
```

- `<Départ>` : nom de l’arrêt de départ (ex : `"Alveringem Nieuwe Herberg"`)
- `<Arrivée>` : nom de l’arrêt d’arrivée (ex : `"Aubange"`)
- `<Heure>` : heure de départ au format HH:mm:ss (ex : `10:40:00`)
- `[options]` : options supplémentaires (préférences de modes de transport, etc.)

## Options de personnalisation

Vous pouvez personnaliser la recherche d’itinéraire avec des options :

- **Priviligié un mode de transport** : Ajoutez le nom du mode précédé de `-` pour priviligier un mode (ex : `-TRAM` pour priviligié le tram).
  ```sh
  make ARGS='"Alveringem Nieuwe Herberg" "Aubange" 10:40:00 -TRAM'
  ```
- **Éviter mode** : Utilisez `-N` suivi du mode pour éviter ce mode (ex : `-NTRAM` pour éviter le tram).
  ```sh
  make ARGS='"Alveringem Nieuwe Herberg" "Aubange" 10:40:00 -NTRAM'
  ```
- **Combinaison d’options** : Vous pouvez combiner plusieurs options pour affiner la recherche selon vos préférences.

**Remarque** : Les options sont à placer après l’heure dans la commande d’exécution.

## Tester le code

1. Vérifiez que les fichiers GTFS sont bien présents dans le dossier `GTFS/`.
2. Compilez le projet avec `make`.
3. Lancez la commande d’exemple ci-dessus pour générer un itinéraire.
4. Le résultat s’affichera dans la console avec le détail du trajet.

## Nettoyer les fichiers compilés

Pour supprimer les fichiers compilés :

```sh
make clean
```

## Remarques

- Le projet a été testé sous MacOS et Linux.
- Si vous manquez de mémoire, augmentez la valeur de `-Xmx` dans le Makefile.