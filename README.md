# Pokémon Battle Simulator

A JavaFX-based Pokémon battle simulator that fetches live data from the PokeAPI and simulates turn-based battles using type effectiveness, status conditions, stat changes, healing moves, and switching mechanics.

This project was originally created for an Object-Oriented Programming course and expanded into a playable battle system. The app uses the public PokeAPI for Pokémon and move data and stores local team data in JSON/text files to reduce repeated API calls.

The program includes several main features:

- Pokémon search and data lookup
- Team creation and saving
- Battle simulation with turn order and battle log
- Type advantages and weaknesses
- Status effects and stat-changing moves
- Healing and draining abilities
- Team switching mechanics

The program fetches data from the PokeAPI: https://pokeapi.co/?ref=public-apis

The program consists of different parts:
<img width="478" height="283" alt="{70477667-FC2A-43ED-81DE-A4ACE7566D52}" src="https://github.com/user-attachments/assets/b1572116-f626-4923-bee1-14f98ec7d435" />

**Pokémon Search** is a simple lookup that fetches data form the API about a Pokémon, most used for testing the API. The game does **NOT** store any concrete data from a Pokémon, and everything is fetched from the API.
<img width="975" height="818" alt="image" src="https://github.com/user-attachments/assets/5c3ecec9-3faa-4054-8950-109b006c80b9" />

**Create Team** is a team creator allowing players to create their own teams and store them locally for battle
<img width="975" height="763" alt="image" src="https://github.com/user-attachments/assets/d502e955-7b6d-4b1e-9466-c5812d692ecd" />

**Battle** is the **main** part of the program. It plays out as a singles Pokémon battle with the perspective switching based on turn. 
Pokemon is a complex game so not everything is implemented, but here are some of the features implemented:
 - Turn based combat with speed affecting turn order
 - Type effectiveness and resistence, including immunities
 - Same type attack bonus(STAB)
 - Stat affecting moves, both positive and negative. (ex. Dragon Dance and Growl)
 - Status effects and their effect (ex. Sleep, Paralysis, Burn, Poison)
 - Healing and Draining moves (ex. Giga Drain, Recover)
 - Accurate dmg calculation using all factors listed and the stats of the pokemon
 - Accuracy and PP on moves
 - Switching mechanics(Set mode)
 - Battle log

**Some gameplay:**
<img width="975" height="532" alt="image" src="https://github.com/user-attachments/assets/d333e726-fac3-4a6d-b5a5-b77da7e45f65" />
<img width="960" height="523" alt="{8015A5D4-CDE4-4572-B2DC-46B6B14E95FC}" src="https://github.com/user-attachments/assets/899ae379-29b7-4566-8e64-0d5b838bff56" />
<img width="960" height="520" alt="{32FE90EE-36B6-4839-95B2-C668875BF61F}" src="https://github.com/user-attachments/assets/fa3cceb0-def3-45d8-ba6c-70609296dd8f" />
<img width="960" height="523" alt="{B7E7D21D-047C-4767-A0F2-C05114C30698}" src="https://github.com/user-attachments/assets/243c263a-48fb-447f-a3f6-e4c28a973381" />
<img width="479" height="159" alt="{BE30C603-09F6-4A07-BD76-9DAED467BF97}" src="https://github.com/user-attachments/assets/c088e19c-8eec-46cf-8ce8-88e08c0c5fda" />


## Requirements

- Java 21 JDK: https://adoptium.net/temurin/releases/?version=21
- Maven: https://maven.apache.org/download.cgi

JavaFX, Gson, and JUnit are managed by Maven through [pom.xml](pom.xml), so they are downloaded automatically when you build the project.

## Setup and installation

1. Install Java 21 JDK and make sure `JAVA_HOME` is set.
2. Install Maven and add it to your system `PATH`.
3. Clone the project:

   ```bash
   git clone https://github.com/your-username/Java-Pokemon-battle-simulator.git
   cd Java-Pokemon-battle-simulator
   ```

4. Download or ensure the required dependencies are available. The project uses Maven, so JavaFX, Gson, and JUnit are downloaded automatically when you build the project.
5. Verify the project builds:

   ```bash
   mvn clean test
   ```

## How to run the application

From the project root, run:

```bash
mvn javafx:run
```

This project is configured with the JavaFX Maven plugin and points to the main application class:

```text
com.nikolai.ui.App
```

If you want to run the app from an IDE instead, use the project as a Maven JavaFX project and configure the main class as `com.nikolai.ui.App`.

## Dependency summary

| Dependency | Version | Download |
| --- | --- | --- |
| Java JDK | 21 | https://adoptium.net/temurin/releases/?version=21 |
| Maven | Latest stable | https://maven.apache.org/download.cgi |
| JavaFX | 17.0.2 | https://gluonhq.com/products/javafx/ |
| Gson | 2.10.1 | https://repo1.maven.org/maven2/com/google/code/gson/gson/2.10.1/ |
| JUnit Jupiter | 5.11.0 | https://repo1.maven.org/maven2/org/junit/jupiter/ |



