To install, get a copy of the source folder (either by clicking "Download ZIP" or cloning). I recommend creating a project from source by placing the folder in your Eclipse workspace and making a project with the same name.

This is an attempt at a civilization 4x style game (Civ4, Civ5, EU4, Pandora, etc). The world is grid based and every tile can contain units and improvements. Every civilization starts off with a settler, that can found a city on a tile. The city uses its population to gather resources to be used on making units or improvements.

Planned (many Civ4 style features):
Cities have population. Population grows in normal circumstances if there is enough food in the city or from the civilization's supply. Each food unit contributes to the growth rate of a city. Growth can be halted. Cities begin to starve or stagnate if there is inadequate food supply. Cities automatically stagnate during the production of workers and settlers.

Cities can work one tile for each level of population. If a city is unhealthy, it will not grow. If a city is unhappy, it will work one less tile for each level of unhappiness. This serves to limit the size of cities in the early game. Each tile has a food, metal, gold, and happiness value that adds to the total. Excess food and metal are added to the civilization's supply. Gold is always added to the supply. Happiness only applies to the city and cannot be shared.



Workers can build improvements in unoccupied tiles. These improvements take turns to build and increase the yield of a tile. The resource increases depends on the tile and improvement.

Units can be trained in cities. To prevent unit spam, every production of a unit slows the respective city's growth and takes a certain amount of food and gold each turn. Each unit stationed in a city or its borders increases happiness.

