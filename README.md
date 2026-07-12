<div align="center">

# 🗡️ Hollow Knight — Java/libGDX Clone

### A 2D Metroidvania built from scratch on the **MVC architecture**, featuring dynamic AI, a full boss fight, a save system, localization, and more.

<br/>

![Java](https://img.shields.io/badge/Java-8+-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![libGDX](https://img.shields.io/badge/libGDX-Game%20Framework-E10000?style=for-the-badge&logo=libgdx&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-Build-02303A?style=for-the-badge&logo=gradle&logoColor=white)
![SQLite](https://img.shields.io/badge/SQLite-Save%20System-003B57?style=for-the-badge&logo=sqlite&logoColor=white)
![Architecture](https://img.shields.io/badge/Architecture-MVC-7952B3?style=for-the-badge)
![License](https://img.shields.io/badge/License-Educational-lightgrey?style=for-the-badge)

<br/>

*A simplified, side-scrolling reimagining of Team Cherry's **Hollow Knight**, built as an academic project for the Advanced Programming course at Sharif University of Technology.*

</div>

---

## 📖 Table of Contents

- [About the Project](#-about-the-project)
- [Gameplay Overview](#-gameplay-overview)
- [Feature Highlights](#-feature-highlights)
- [Architecture](#-architecture)
- [Project Structure](#-project-structure)
- [Controls](#-controls)
- [Cheat Codes](#-cheat-codes)
- [Getting Started](#-getting-started)
- [Tech Stack](#-tech-stack)
- [Roadmap](#-roadmap)
- [Academic Context](#-academic-context)
- [Credits](#-credits)

---

## 🎮 About the Project

This repository contains a **playable, from-scratch clone of Hollow Knight**, implemented in **Java** on top of the **libGDX** framework. The player controls **the Knight** through a hand-crafted, side-scrolling world, fighting enemies with a melee weapon (**the Nail**), navigating precision platforming challenges, and ultimately facing down the game's boss — **the False Knight** — in a multi-phase encounter with its own AI decision system.

The project was designed and implemented around a strict **Model-View-Controller (MVC)** separation, with dedicated layers for game state (`models`), rendering (`views`), and game/input logic (`controllers`) — making the codebase easy to extend, test, and reason about.

> 💡 This repo currently ships the **`core`** module (shared game logic, platform-independent) of a standard libGDX multi-module project layout.

---

## 🕹️ Gameplay Overview

- Explore **hand-built, atmospheric environments** (Forgotten Crossroads, Greenpath, City of Tears, Crystal Peaks), each with its own tileset, hazards, and ambient dressing.
- Fight **ground and flying enemies**, each with unique, hand-tuned AI behaviors — patrol, ambush, ranged attacks, and stealth.
- Master **precise platforming mechanics**: jumping, air-dashing, wall-sliding, double jumping, and the signature **pogo bounce** off spikes and enemies.
- Manage your **health (Masks)** and **Soul** resources, and use **Focus** to heal mid-fight.
- Cast powerful **spells** — `Vengeful Spirit` and `Howling Wraiths` — fueled by Soul earned through combat.
- Equip a variety of **Charms** that passively modify your combat style and abilities.
- Take on **Zote**, a fully-voiced (in spirit) NPC with a branching, stateful dialogue system.
- Defeat the **False Knight** in a full boss encounter with phase transitions, a stun/vulnerability window, and a five-move decision-tree AI.
- Save and load your progress via a **persistent SQLite-backed save system**.
- Switch between **English and a second language** on the fly — every menu, HUD element, and dialogue line updates dynamically.

---

## ✨ Feature Highlights

<table>
<tr>
<td width="50%" valign="top">

### 🧭 Menus & UI
- Main Menu, Start Game (with **4 save slots**), Settings, Guide, Achievements
- In-game **Pause** and **Inventory** menus
- Dynamic keybinding display that reflects custom controls
- Animated, unlockable **Achievements** system (`Observer` pattern)
- Live **brightness**, **volume**, and **language** controls

### 🌍 World & Environments
- Multiple hand-designed biomes with unique tilesets & hazards
- Animated ambient/background objects (torches, foliage, etc.)
- Environment-specific particle effects and hazard placement
- Respawn-on-hazard logic tied to the last safe platform

</td>
<td width="50%" valign="top">

### 🧠 Enemies & AI
- **Ground enemies**: patrol-and-turn logic with cliff/wall detection
- **Flying enemies**: vision cones, ambush ("ambush ahead") ranged and melee attacks
- Two elite fixed enemies: **Husk Hornhead** (charge attack) & **Crystal Guardian** (laser + enrage)
- Enemies respawn on room re-entry / distance threshold

### ⚔️ Combat & Movement
- Nail combo attacks, air-dash, double jump, wall-slide, and **pogo bounce**
- Variable jump height (`Jump Cutoff`) for skill-based platforming
- Knockback, hit-stop, and screen-shake feedback on every hit

</td>
</tr>
<tr>
<td width="50%" valign="top">

### 👹 Boss Fight — False Knight
- Distance-based + randomized + anti-spam decision AI
- **5 unique attacks**: Mace Slam, Charge Run, Offensive Leap, Defensive Leap, Power Slam
- **Stun phase** at 50% HP with an exposed, vulnerable hitbox
- Speed/aggression scaling after the stun window
- Locked arena with camera clamping & dynamic camera shake

### 🎒 Charms & Inventory
- 8 unique charms (`Soul Catcher`, `Dashmaster`, `Unbreakable Strength`, `Quick Slash`, `Quick Focus`, `Heavy Blow`, `Sharp Shadow`, `Void Heart`)
- Notch-limited equip system with live toggling

</td>
<td width="50%" valign="top">

### 💾 Persistence & Audio
- **SQLite**-backed save/load system (game state, settings, achievements, slots, charms)
- Dynamic background music that crossfades between areas
- Contextual SFX for attacks, damage, soul gain, and focus
- Full **runtime localization** across menus, HUD, and dialogue

### 🐛 Debug / Cheat Codes
- Six `Ctrl + Fn` cheat codes for fast testing (see below)

</td>
</tr>
</table>

---

## 🏛️ Architecture

The project strictly follows the **Model–View–Controller** pattern:

```
com.hollow
├── models/          # Pure game state & data — no rendering, no input
│   ├── entities/    #   knight, enemies, boss, zote
│   ├── achievement/ #   Observer-based achievement events
│   ├── data/        #   GameData, SettingData, SlotData (save payloads)
│   └── language/    #   Observer-based localization
│
├── views/           # Everything that draws to the screen
│   ├── screen/      #   MainMenuScreen, GameScreen, LoadingScreen
│   ├── render/       #   EntityRenderer, BossRenderer
│   └── ui/          #   HUD, menus, inventory, dialogue box
│
├── controllers/     # Game loop, input handling, orchestration
│   ├── engine/      #   Game (core loop), CombatEngine
│   └── manager/     #   SaveManager, AudioManager, AchievementManager, LanguageManager
│
└── loader/          # Asset, animation, tilemap loading utilities
```

Key design decisions:

- **Observer pattern** powers both the **Achievement popup system** and **Localization**, so any number of UI components can react to state changes without tight coupling.
- **Strategy-style boss behaviors** (`BossBehavior` and its implementations — `MaceSlamBehavior`, `ChargeRunBehavior`, `OffensiveLeapBehavior`, `DefensiveLeapBehavior`, `PowerSlamBehavior`, `StunnedBehavior`, `IdleBehavior`, `DeathBehavior`) let the False Knight's AI swap tactics cleanly at runtime.
- **`GameData`** acts as a lean, serialization-friendly snapshot of the entire game state, used as the single interface between gameplay and the save system.

---

## 📁 Project Structure

```
core/
├── build.gradle
└── src/main/java/com/hollow/
    ├── HollowKnight.java                  # Application entry point
    ├── controllers/
    │   ├── ButtonController.java
    │   ├── CharmSelectionController.java
    │   ├── engine/
    │   │   ├── CombatEngine.java
    │   │   └── Game.java
    │   └── manager/
    │       ├── AchievementManager.java
    │       ├── AudioManager.java
    │       ├── LanguageManager.java
    │       └── SaveManager.java
    ├── loader/
    │   ├── AssetLoader.java
    │   ├── AudioLoader.java
    │   ├── BossAnimationLoader.java
    │   ├── EnemyAnimationLoader.java
    │   ├── KnightAnimationLoader.java
    │   ├── TiledMapHelper.java
    │   └── ZoteAnimationLoader.java
    ├── models/
    │   ├── achievement/  (Achievement, AchievementData, AchievementObserver)
    │   ├── data/          (GameData, SettingData, SlotData)
    │   ├── entities/
    │   │   ├── boss/      (FalseKnight + 8 behavior classes)
    │   │   ├── enemy/     (Crawlid, Mosscreep, Tiktik, Crystallized, Mosquito, HuskHornhead, InstantLaser, Enemy)
    │   │   ├── knight/    (Knight, KnightState, Charm, Projectile, WraithEffect)
    │   │   └── zote/      (Zote)
    │   ├── language/      (Language, LanguageObserver)
    │   ├── AmbientObject.java, BreakableWall.java, Debris.java,
    │   ├── Effect.java, SolidBlock.java, TransitionZone.java
    └── views/
        ├── render/         (EntityRenderer, BossRenderer)
        ├── screen/         (MainMenuScreen, GameScreen, LoadingScreen)
        └── ui/
            ├── hud/        (GameHud, MaskWidget, SoulVessel, DialogueBox, InventoryUI, PauseUI, EndingUI)
            └── menu/       (StartGameUI, AudioUI, ControlsUI/KeyBoardUI, BrightnessUI, LanguageUI,
                             GuideUI, AbilitiesUI, AchievementsUI, CheatUI, MenuBackground, BaseSettingUI)
```

---

## 🎹 Controls

| Action | Default Key |
|---|:---:|
| Move Left / Right | `←` `→` |
| Jump | `Z` |
| Dash | `C` |
| Attack (Nail) | `X` |
| Focus (Heal) | `A` |
| Pogo (in air) | `↓` + `X` |
| Cast Spell | context-dependent (bound alongside Focus/Attack) |
| Open Inventory | `I` |
| Pause | `Esc` |

> All movement and action keys are fully **rebindable** from the in-game **Settings → Controls** menu, with a one-click reset to defaults.

---

## 🧪 Cheat Codes

All debug codes are chorded with **`Left Ctrl`** so they never collide with normal gameplay input:

| Code | Effect |
|---|---|
| `Ctrl + F1` | Teleport instantly to the **Boss Arena** for quick False Knight testing |
| `Ctrl + F2` | Toggle **Noclip / Spectator Mode** (fly freely, no gravity, no collisions) |
| `Ctrl + F3` | **Emergency Heal** — restore a mask instantly |
| `Ctrl + F4` | **Refill Soul Vessel** to full |
| `Ctrl + F5` | Toggle **God Mode** (immune to all damage) |
| `Ctrl + F6` | Toggle **Insta-Kill Mode** (one-shot every enemy on screen) |

---

## 🚀 Getting Started

### Prerequisites

- **JDK 8** or newer
- **Gradle** (or use the included Gradle wrapper, if present in the full multi-module project)

### Running the project

This `core` module is meant to be dropped into a standard **libGDX** multi-module project (`core` / `desktop` / `android` / …), generated via the [libGDX Setup Tool](https://libgdx.com/dev/project-generation/). Once wired into a `desktop` launcher module:

```bash
# From the root of the full libGDX project
./gradlew desktop:run
```

### Building a runnable JAR

```bash
./gradlew desktop:dist
```

The compiled JAR will be available under `desktop/build/libs/`.

> 📦 **Dependencies** used by this module (see `core/build.gradle`): `libGDX` core & FreeType, `TenPatch` (for scalable 9-patch UI), and `sqlite-jdbc` for the save system.

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Language | Java |
| Game Framework | [libGDX](https://libgdx.com/) |
| Build Tool | Gradle |
| Persistence | SQLite (via `sqlite-jdbc`) |
| UI Skinning | TenPatch (9-patch UI elements) |
| Fonts | libGDX FreeType |
| Map Format | Tiled (`.tmx`, loaded via `TiledMapHelper`) |
| Architecture | MVC + Observer + Strategy patterns |

---

## 🗺️ Roadmap

- [ ] Additional Charm interactions & combos
- [ ] More secret rooms & breakable-wall reward chains
- [ ] Expanded boss move-set / second boss encounter
- [ ] Full controller/gamepad support
- [ ] Additional language packs

---

## 🎓 Academic Context

This project was developed as **Homework 2** for the **Advanced Programming** course, Department of Computer Engineering, **Sharif University of Technology** (Spring 2025/1404–05 semester), under the supervision of **Dr. Mohammadamin Fazli**. The assignment required a strict MVC implementation of a simplified Hollow Knight clone, evaluated across menus, core gameplay, cheat codes, visuals/UI, and audio/infrastructure systems.

> This is an educational, non-commercial fan project. All original *Hollow Knight* concepts, characters, and names belong to **Team Cherry**; this repository is an independent, from-scratch implementation created for learning purposes only.

---

## 🙌 Credits

- **Game Design Reference:** [Hollow Knight](https://www.hollowknight.com/) by Team Cherry
- **Framework:** [libGDX](https://libgdx.com/) open-source game development framework

<div align="center">

---

Made with ❤️, a lot of debugging, and probably too much coffee.

</div>
