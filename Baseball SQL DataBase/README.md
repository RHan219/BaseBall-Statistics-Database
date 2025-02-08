 The purpose of this baseball league database is to manage, analyze, and report comprehensive information about players, teams, games, and performance statistics in an organized and efficient manner. It is designed to support anybody that takes part in the sport, whether that be team managers, coaches, analysts, and fans by tracking essential details about players and teams, recording game results, logging player transfers, and calculating key performance metrics.


 To recreate the database execute the queries that in the folder called "Creating tables" to implement all the tables in the Database.

 Execute the trigger needed to popluate the tables. As of right now there are two triggers, one in the "playerTransaction folder" and the other in "teamStats" folder. Please execute both. After that insert the data for each folder that located in their respective folder. 

 In the BaseballApp.java file, there is a line with "jdbc:sqlserver:<Insert Database Name>;". This should be line 6.
 Replace "jdbc:sqlserver:<Insert Database Name>;" with the name of your database so that the code can connect to it properly.

 create a new user for the database called adminUser which it creation is located in the "User" folder and is the user we would be connecting in the JDBC code.
Make sure you have the correct crosspath  and JSON information for the database to work.
