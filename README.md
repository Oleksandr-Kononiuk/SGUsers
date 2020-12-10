# SGUsers
This is my little pet project which mostly using for studying and trying on practice some libs and my knowleges.
---
So what this program doing. In simply this program will parse spartangaming.co (Exile mod for ArmA 3), get some info about players from player liderbord page. This info uses for getting info about player clan, and asosiate his current nickname with permanent nickname. Server provide changing players nickname when they want it, so we can monitoring who playing on server. In addition we can monitoryng which admins play on the server, but they must added handly. Alse we can check who in online, program use the site battlemetrics.com for it.
***

### Current status.
#### Completed base backend (which partically can be use in discord bot, for exemple).
#### Release - no.
#### Support only Chernarus server.
***

### Used libraries
#### jsoup:1.9.2 - HTML parser
#### slf4j:slf4j-api:1.7.25 - for logging
#### slf4j:slf4j-log4j12:1.7.25 - for logging

## Commands list
---
### Note: after command can be written **only** two arguments.
---
>#### Player commands
>
>* **!add-player** - looking for a player using the link on the site passed to the method and adds it to the database. For example: !add-player player_profile_link.
>* **!delete-player** - removes a player from the database using the link to his profile or battlemetric ID.
>* **!set-admin** - sets the admin flag to the player. Usually admins do not show on the site. Such a player must be created manually.
>* **!delete-admin** - removes a admin status from player.
>* **!print-players** - to bring to the screen all the players in the database.
>* **!search-player** - looks for a player in the database and displays it on the screen. Search is carried out by the link to the profile or by the first letters in the nickname or by spartangaming ID.

>#### Family commands
>
>* **!add-family** - a new family is being created. A maximum of two words separated by a space in the family name.
>* **!delete-family** - remove family from the base. Searches by the first letters in the name. The first found one will be deleted.
>* **!print-families** - to bring to the screen all the families in the database.
>* **!search-family** - looking for the first letters of the family. Displays the first one found.
>* **!add-member** - adds a new family member. Looking for a Player and Family by the first word in the name. Adds the first found player to the first found family.
>* **!delete-member** - removes a member from the family. Searches and deletes by the first letters found.
>* **!change-family-name** - changes the name of the family to another. The first argument is the name of the family to be changed. Searches for a family by the first letters in the name. The second argument is the new name but ONLY ONE WORD.

>#### Database commands
>
>* **!save** - saves the current database and makes a backup.
>* **!get** - reads data from the current database.
>* **!change-server** - the command changes the server for display. Argument \"altis\" loads a database for the Altis server. Argument \"cherno\" loads a database for the Chernarus server. By default or incorrect input of the argument, the Chernorus server database is loaded.
>* **!fill-players** - reads player data from X pages. The process is long and may fail.
>* **!rebuild-families** - rebuilds all families based on the list of players received.

>#### Other commands
>
>* **!help** - displays a list of commands with a description.
---
