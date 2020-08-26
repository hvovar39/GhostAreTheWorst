Projet de programmation réseau 2018
LEMOINNE Marianne
VOVARD Hugo


Bienvenue sur notre projet de programmation réseau.

Pour compiler le serveur, veuillez vous rendre dans le dossier Serveur, et entrer la commande 'make'.
Pour recompiler, entrez 'make clean' puis 'make'.

Pour compiler le client, veuillez vous rendre dans le dossier Client, et entrer la commande 'make'.
Pour recompiler, entrez 'make clean' puis 'make'.



Pour lancer le serveur aprés l'avoir compiler, entrez la commande 'java LancementServeur'.

Pour lancer le client, vueillez tous d'abord récupérer l'adresse IP de la machine sur laquelle vous faites tourner le serveur (cf plus loin), puis entrer l'ip obtenue a la ligne 33 du fichier client.c, puis recompile.
Vous pouvez ensuite lancer le client grâce à la commande './client port' port étant le numéro du port de réception des messages udp.

Pour obtenir l'adresse IP de votre machine, rendez vous dans le dossier Projet_PR2018_LEMOINNE-VOVARD et compilez le programme GetIP grace à la commande 'javac GetIP.java'.
Entrez ensuite la commande 'java GetIp'.
