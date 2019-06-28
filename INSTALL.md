*******************************************************************************************************************************
**Pour l'application:** 

Il y a un fichier APK qui permets l'installation soit à partir d'une Machine Virtuel sur 
l'ordinateur ou bien à partir d'un appareil android mobile. Qui se trouve a cette adresse 

   **/StarBus/tree/master/app/release/app-release.apk**

Sur Android, suffit de le charger sur l'appareil et de cliquer dessus pour l'installer

Sur machine virtuel avec Android Studio il faut d'abord mettre le fichier dans le dossier "tools" du SDK

Qui se trouve à une adresse comme ceci:

   **C:\Users\UserName\AppData\Local\Android\Sdk\tools**

Une fois chargé taper la ligne de commande

   **adb install filename.apk**
  
Pour la charger du la machine virtuel
*******************************************************************************************************************************

**Pour le Server:**

On peut éxecuter le code du Raspberry Pi direct sur un ordinateur, tout les fichiers le concernant se trouvent dans l'archive ZIP nommé

**STARASPBERRY.zip**

à la racine du projet. Il faut la dézipper ensuite 
ouvrir le terminal et se placer dans le dossier STARASPBERRY

pour compiler le programme, entrez la commande:

**javac -cp .:json-simple-1.1.jar**

pour lancer le programme compilé, entrez la commande:

**java -cp .:json-simple-1.1.jar Main**

