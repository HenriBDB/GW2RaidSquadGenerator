# GW2 Squad Generator

Created for the Crossroads Inn GW2 raid training server. It sorts signed up players into squads that conform to the required setup for training.

## Installation:

Make sure you have java and gradle installed. This project was made with JDK 11.

`gradle build`: Allows you to build a .jar file. File is generated in build/libs/*.jar.\
`gradle run`: Will launch the application.
`gradle jlink`: Will create a runnable image of the application in 'build/image/bin/'. The 'build/image/' directory contains all dependencies needed to run the image.


## CSV Files Structure:

Please contact dev for CSV file structure as the latter is tightly couple with Crossroads Inn requirements and may change based on their needs.

## Settings Location:

Linux:
`~/.java/.userPrefs/Squad-Planner/prefs.xml`

Windows:
`HKEY_CURRENT_USER\SOFTWARE\JavaSoft\Prefs\Squad-Planner\prefs.xml`