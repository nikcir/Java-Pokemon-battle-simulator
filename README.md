A pokemon battle simulator made using javaFX, fetching data using an API

Origin:
originally made for the subject Object oriented programming made by me

The program fetches data from the pokeAPI: https://pokeapi.co/?ref=public-apis
And stores the needed data locally using JSON to minimize latensy.

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
<img width="960" height="524" alt="{A80869A6-B65D-4AEE-8470-C8061D6C372A}" src="https://github.com/user-attachments/assets/35412406-16d7-4bec-a4a5-fa757b00773a" />
<img width="479" height="159" alt="{BE30C603-09F6-4A07-BD76-9DAED467BF97}" src="https://github.com/user-attachments/assets/c088e19c-8eec-46cf-8ce8-88e08c0c5fda" />









