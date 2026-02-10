# DonorSearch

**Component selection and hardware usage statistics analysis module.**

This project is designed to automate computer hardware repair, identify unused equipment ("donors"), and verify component compatibility. The system aims to reduce costs for purchasing new equipment by effectively reusing existing resources.

## 🚀 Planned Features

- **Smart Donor Search:** A hybrid search algorithm that considers both physical compatibility and actual equipment downtime.
- **Network Scanner:** Multi-threaded data collection on workstation activity (via SSH/WinRM) to generate usage statistics.
- **Knowledge Base:** A normalized database of component specifications (based on PCPartPicker and Kaggle data).
- **Compatibility Check:** Algorithms based on SpEL (Spring Expression Language) to verify compatibility of processors, motherboards, RAM, and other components.
- **Integration:** Capable of being embedded into existing enterprise inventory systems.

## 🛠 Tech Stack

- **Backend:** Java 17+, Spring Boot.
- **Frontend:** React, TypeScript.
- **Database:** PostgreSQL.
- **Data Gathering:** Python (for parsing PCPartPicker).
- **Tools:** Docker, Maven, Node.js, Vite.

---

🇷🇺 **Русская версия:** [README.md](README.md)
