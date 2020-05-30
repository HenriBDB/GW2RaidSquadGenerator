# GW2 Squad Generator

Created for the Crossroads Inn GW2 raid training server. It sorts signed up players into squads that conform to the required setup for training.

## Installation:

Make sure you have java and gradle installed. This project was made with JDK 11.

`gradle build`: Allows you to build a .jar file. File is generated in build/libs/*.jar.\
`gradle run`: Will build the jar and run it.

## Sample CSV Files:

The CSV files must contain the following provided columns but cant contain any other extra columns like timestamps. Column names can have slight variations as long as they conform to the string matching provided in `src/main/java/signups/SignupsParser` lines 89 - 100.

### Commanders and Aides:

|GW2 Account|Discord Account|Role|Additional comments:|Tank|Healer - Druid|Healer - Offheal|Boons - Chrono|Boons - Alacrigade|Boons - Quickbrand|Banners|DPS|
|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|
|Commander1||Commander||Tank|||chrono||||Power|
|Aide1||Aide|||Druid||||DPS, Healer||Condition|

### Single-Tier Sign-ups:

|GW2 Account|Discord Account|Tier|Additional comments:|Tank|	Healer - Druid|Healer - Offheal|Boons - Chrono|Boons - Alacrigade|Boons - Quickbrand|Banners|DPS|
|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|
|Player1.9010|Player1#1111|0|I want to be with Eren in the squad.|Tank|Druid|Tempest, Firebrand|Offchrono|DPS, Healer|DPS, Healer|Banners|Power, Condition|
### Multi-Tier Sign-ups;

(Coming Soon...)