# Smart Home Simulator 🏠🔌

A Java-based simulation tool that models a smart home ecosystem. It allows users to manage and monitor various smart devices (plugs, cameras, white lamps, and color lamps) and track their energy/storage consumption over time based on structured commands.

---

## 📜 Assignment Specification
For detailed assignment requirements, device parameters, and input/output commands, refer to the official PDF:
👉 **[Smart Home Specification (PDF)](./BBM104_S26_PA2_v1.pdf)**

---

## 💡 Supported Devices & Features

The system manages the state and usage metrics of the following smart devices:

- **🔌 Smart Plug:**
  - Connects/disconnects appliances.
  - Measures power rating (Ampere) and tracks total energy consumption (in Kilowatt-hours / kWh) when active.

- **📷 Smart Camera:**
  - Records video and tracks storage usage (in Megabytes / MB).
  - Calculates storage based on video recording duration and megabytes consumed per minute.

- **💡 Smart Lamp (White):**
  - Adjusts brightness (kelvin rating) and tracks status.
  
- **🌈 Smart Lamp (Color):**
  - Adjusts brightness and switches color via hex color codes or color names.

---

## 🛠️ Project Structure

- **`SmartHomeSystem.java`**: The main entry point. Initializes the `InputProcessor` and `OutputWriter` and triggers command execution.
- **`Smart.java`**: Abstract base class defining common properties for all smart devices (e.g., name, status, switch time, description).
- **`Plug.java` / `Camera.java` / `Lamp.java` / `ColorLamp.java`**: Device-specific concrete classes implementing individual consumption calculation rules.
- **`InputProcessor.java`**: Parses incoming simulation commands (e.g., adding devices, switching states, scheduling time steps, calculating consumption).
- **`OutputWriter.java`**: Formats and writes the simulation reports into the output text files.

---
