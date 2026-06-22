# 2D Platform Game (JavaFX) 🎮🏃💨

A 2D side-scrolling platformer game developed in Java using the **JavaFX** framework. The game implements character physics, collision detection, sprite animations, dynamic spawning, and camera tracking.

---

## 📜 Assignment Specification
For detailed rules, physics constants, scoring details, and requirements, refer to the official PDF:
👉 **[2D Platform Game Specification (PDF)](./platform_game.pdf)**

---

## 🕹️ Gameplay Mechanics & Rules

The game is set on a grid map with platforms (terrains and blocks) where the player must gather apples while avoiding and destroying deadly spikes.

- **🏃 Controls:**
  - **`A` / `Left Arrow`**: Move Left (1 pixel/frame).
  - **`D` / `Right Arrow`**: Move Right (1 pixel/frame).
  - **`W` / `Up Arrow`**: Jump (uses gravity calculations, ~55-60px height limit).
  - **`Spacebar`**: Use Skill (Spike Destroyer).

- **🍎 Apple Collection & Spawning:**
  - **2 apples** and **1 spike** spawn randomly on platforms every **3 seconds**.
  - Collecting **2 apples** awards **1 score point** and removes **1 random spike** from the map.

- **🌵 Spike Hazards & Skills:**
  - Colliding with a spike resets the player to the start position and restarts the game.
  - **Spike Destroyer Skill:** If the player has **5 or more score points**, pressing `Space` destroys the nearest spike at the cost of **5 points**.

- **🎥 Camera Tracking:**
  - The camera horizontally follows the player to keep them centered on the screen, stopping dynamically at the borders of the game world.

---

## 🛠️ Project Structure

- **`GameEngine.java`**: The main controller handling the JavaFX window, game loop, physics update tick, input processing, spawning, and camera scrolling.
- **`GameObject.java`**: An abstract base class for all entities, rendering sprite sheets and handling frame-based animations (`animate()` utility).
- **`Player.java`**: Implements the player character, managing movement states, sprite sheets, jumping/falling physics, and skill activation.
- **`Apple.java`** / **`Spike.java`**: Game objects representing collectible items and obstacles.
- **`Terrain.java`** / **`Block.java`**: Static platforms that make up the level bounds and collision floors.
- **`level.txt`**: Configuration file containing the initial placement and coordinates of the platforms.

---

## 🚀 How to Run

Compile and launch the game using Java 8:

```bash
# Compile all source files
javac GameEngine.java

# Run the game
java GameEngine
```
