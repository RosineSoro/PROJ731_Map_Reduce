# PROJ731_Map_Reduce 

# SORO GNIDANHAN ET KOALAGA LAURIANE

L'objectif du projet est de reproduire le comportement d’Hadoop sur l’exemple de comptage de mots avec en entrée : un ensemble de fichiers texte contenant des mots et en sortie : un dictionnaire comportant l’ensemble des mots associés au compteur de leur nombre d’occurrences. 
Dans une première phase, une tâche par fichier va compter le nombre d’occurrence des mots du fichier qu’elle a reçue (MAP), dans un deuxième temps un nombre fixé de tâche « reduce » vont récupérer chez chacunes des taches map le sous ensemble des mots dont elle est « responsable » et comptabiliser pour chacun des ces mots, le nombre total d’occurrences avec l'utilisation d'un nœud coordinateur pour lancer les taches map et reduce.

Nous avons fait un multithread centralisé avec le nombre de mapper et de reducer à ajuster en fonction du nombre qu'on veut sinon par défaut il y'a 3 mappers et 2 reducers.


# Shuffle ( noeud coordianateur):

Le shuffle est celui qui se charge de la lecture du fichier et de sa répartition en sous-parties.
Il attribue ensuite chaque sous-partie à un mapper puis lance les tâches MAP.
Il utilise un compte à rebours pour s'assurer que tous les threads aient terminé avant de lancer les threads Reducer.
Toujours avec un compte à rebours, il s'assure que tous les reducers aient fini avant de stocker le résultat final dans un fichier 'resultat_mapReduce.txt' dans le répertoire Textes.

# Tâche MAP :

Lancé par le Shuffle.
Lit la sous-partie qui lui est attribuée par le shuflle.
Remplit son dictionnaire de mots-clés en comptant pour chaque mot de sa sous partie son occurence.
Remplit la liste 'resultats' qui sera envoyée au reducer par le shuffle pour le comptage final(ref ![Alt text](<Exemple du resultat founi par les mappers au shuffle-1.jpg>)). 
Pour le remplissage de la liste, une fonction de hashage est utilisée pour savoir à quel reducer est associé chaque mot-clé. Lors de cette étape, il y'a un compte à rebours qui se lance (initialisé avec le nombre de mappers et qui est décrémenté lorsque que chaque mapper finit sa tâche) et lorsque le compte à rebours passe à 0 (quand tous les mappers ont terminé), le shuffle est notifié.


# Tâche Reduce:

Lancé par le Shuffle après que celui-ci ait reçu la notification que les mappers ont terminé.
Parcout le dictionnaire qui lui est assigné par le shuffle (ce dictionnaire est issu du résultat renvoyé par les mappers)
On parcourt les clés du dictionnaire et pour chaque clé, on additionne les occurences contenues dans la liste des occurences pour avoir l'occurence finale du mot clé. 
Puis stocke le mot clé et son occurence finale dans un dictionnaire final.
Le même processus de compte_à_rebours est appliqué et quand le compte_à_rebours passe à 0, une notification est envoyée au shuffle qui à son tour récupère le dictionnaire final et le stocke dans un fichier.txt 'resultat_mapReduce.txt'.

# Main:
lance le shuffle. 
Permet d'avoir le temps d'exécution du programme. 

