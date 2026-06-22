# Dungeon Simulation 🗡️🏰

This Java project simulates a player's journey through a grid-based dungeon. The player must navigate through various types of rooms, manage their health and shield, acquire a key, and safely reach the exit room.

The simulation utilizes pathfinding algorithms to determine the best path to escape the dungeon while satisfying all constraints.

---

## 📜 Assignment Specification
For detailed assignment rules, input/output formats, and constraints, refer to the official PDF:
👉 **[Dungeon Simulation Specification (PDF)](./dungeon_simulation.pdf)**

---

## 🎮 Room Types & Game Rules

The dungeon is represented as a grid where each cell corresponds to a specific room type:

| Room Code | Room Type | Description / Effect |
| :---: | :--- | :--- |
| **`E`** | **Empty Room** | Safe room with no effect. |
| **`M`** | **Monster Room** | Inflicts **30 damage** to the player. |
| **`T`** | **Trap Room** | Inflicts **20 damage** to the player. |
| **`H`** | **Healing Room** | Heals the player by **15 HP**. |
| **`L`** | **Relic Room (Heal)** | Heals the player by **10 HP**. |
| **`A`** | **Shield Room (Type A)** | Grants a **6 HP shield**. |
| **`B`** | **Shield Room (Type B)** | Grants a **4 HP shield**. |
| **`K`** | **Key Room** | Grants the **dungeon key** (required to exit). |
| **`X`** | **Exit Room** | The dungeon exit. Reaching this room with the key completes the run. |

### Core Mechanics
- **Health System:** The player starts with a maximum health value. If health drops to `0` or below, the player dies.
- **Shield System:** Shields absorb incoming damage before health is reduced.
- **Victory Condition:** The player must reach the Exit Room (`X`) while holding the key and having a health status greater than `0`.

---

## 🛠️ Project Structure

- **`Main.java`**: Entry point of the application. Reads command-line arguments for input/output files and initializes the grid and pathfinding.
- **`Room.java`**: Defines the abstract base class `Room` and its object-oriented subclasses representing different room types (e.g., `MonsterRoom`, `HealingRoom`, etc.).
- **`Player.java`**: Manages player state including current health, shield power, key status, coordinates, and health operations.
- **`FindPath.java`**: Contains the pathfinding algorithm to solve the dungeon and find the best path to the exit.
- **`ReadInput.java`** / **`WriteOutput.java`**: Handles reading the input grid and writing the path results to an output file.

---

## 🚀 How to Run

Compile and run the program using standard Java commands. The program accepts the input grid path and the desired output path as command-line arguments:

```bash
# Compile the project
javac Main.java

# Run the project with inputs and outputs
java Main inputs/input6.txt output.txt
```
