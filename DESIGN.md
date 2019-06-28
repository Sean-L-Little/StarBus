@TODO finish UML
Pour l'application:

*****************************************************************************************************************************
MainActivity: 

Notre activité principal, où on affiche les differents requêtes et les boutons pour acceder aux paramètres
Pour chacun des 3 requêtes possible (slots) on a 3 boutons: SUPPRIMER, MODIFIER et ENVOYER
On utilise AsyncTask pour envoyer les données au serveur

SUPPRIMER: Supprime la requête et envoi au serveur un message disant de supprimer de l'affichage
MODIFIER: Lance LignesActivity afin de modifier une requête
ENVOYER: Envoi la requête au serveur pour qu'il l'afficher, sauvgarde aussi la requête dans les Préferences
PARAMETRES: Lance SettingsActivity afin de modifier l'IP ou Port

*****************************************************************************************************************************
LignesActivity: 

Cette activité permets de modifier les requêtes qu'on envoi au server, ceci se fait
grâce à l'API de la Star de Rennes (data.explore.star.fr).
Ici on utilise Volley pour faire la communication pour récuperer les données de l'API 
ensuite on utilise JSON pour parser la réponse afin d'en tirer les informations désirées

Nous avons 3 Listes déroulant RecyclerView:
Lignes: Celle-çi affiche les lignes, besoin d'aucun Input de l'utilisateur. On fait une requête Asynchrone vers
l'API de la Star, une fois qu'on reçoit notre réponse. On la parse et on l'ajoute dans le RecyclerView afin
de l'afficher.

Directions: A partir du choix de l'utilisateur sur 'Lignes' on fait une nouvelle requête vers la BDD Star
précisant les deux directions "Principales" Les differents lignes peuvent avoir plus de 2 directions.

Arrets: Ensuite avec la 'Ligne' et la 'Direction' on peut afficher les differents arrets sur le chemin,
encore une fois on fait une requête qu'on reçoit et qu'on parse et qu'on affiche.

Nous avons aussi 2 boutons: REINITIALISER et SELECTIONNER

REINITIALISER: Permets de mettre la requête et les RecyclerViews à zéro si on le désire

SELECTIONNER: Prends la requête en mémoire et la renvoi à MainActivity afin que celle-çi l'affiche,
cela ferme LignesActivity et on se retrouve en MainActivity

*****************************************************************************************************************************
SettingsActivity:

Cette activité permets de modifier l'IP et le Port sur laquel on envoi les données. Notre serveur écoute sur un port et renvoi les données
qu'elle reçoit à la Raspberry Pi pour qu'elle l'affiche.

Nous avons deux EditTexts qui permettent de modifier ces paramètres qui sont ensuite enregistrés dans les préferences pour qu'on ne doive pas
les remettre à chaque fois qu'on ouvre l'appli