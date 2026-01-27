# 2026 Example Subsystems and Commands

Team 2186 Dogs of Steel

This repository is a minimal, command-based WPILib Java project meant as a reference for students who are creating new subsystems and commands. It is separate from the main robot code and is for learning and copying patterns.

## Who this is for

- Students learning command-based structure (Team 2186 Dogs of Steel)
- New subsystem and command authors
- Mentors reviewing examples and conventions

## How to use this example

Students should not add new subsystems directly to this example repo. Instead, create a branch off this season's main robot code repository and use this example project only as a reference for structure and patterns.

Recommendation: keep two VS Code windows open, one for this example repo (read-only reference) and one for the main robot repo (where you edit and commit).

## Quick start

Most tasks should be run from the WPILib Command Palette in VS Code (press `Ctrl+Shift+P`, then search for "WPILib").

1. Open the project in WPILib VS Code.
2. Let Gradle finish downloading dependencies.
3. Build with the WPILib Command Palette:
   - "WPILib: Build Robot Code"

## GitHub workflow (main robot repo)

Prefer using the VS Code UI for Git/GitHub actions. These steps happen in your local clone of the main robot repo, not in this example repo.

1. Clone the main robot repo (one-time) using the VS Code GitHub extension or "Clone Repository" command.
2. Create a new branch for your feature using the Source Control panel.
3. Make your code changes and add new files.
4. Stage your changes in the Source Control panel.
5. Commit your changes with a clear message.
6. Publish/Push the branch from VS Code.
7. Open a Pull Request on GitHub from your branch into the main branch.

If you need to update your branch with the latest main branch:
1. Pull latest changes from the main branch using VS Code.
2. Merge or rebase into your feature branch using VS Code.

If you get stuck, ask a mentor to walk through the UI the first time.

## Project structure (high level - this example repo)

- `src/main/java/frc/robot/` - robot code entry points
- `src/main/java/frc/robot/subsystems/` - subsystem classes
- `src/main/java/frc/robot/commands/` - command classes
- `src/main/java/frc/robot/Constants.java` - constants and IDs

## How to add a new subsystem (in the main robot repo)

1. In the main robot repo, create a class in `src/main/java/frc/robot/subsystems/`.
2. In this example repo, use the example subsystems in `src/main/java/frc/robot/subsystems/` (ex: `ShooterSubsystem.java`, `IntakeSparkSubsystem.java`) as reference patterns.
3. Add hardware objects and configuration in the constructor.
4. Add methods that represent actions (ex: `intake()`, `stop()`).
5. In the main robot repo, create the subsystem instance as a field in `RobotContainer` and initialize it in the constructor.

## How to add a new command (in the main robot repo)

1. In the main robot repo, create a class in `src/main/java/frc/robot/commands/`.
2. In this example repo, use the example commands in `src/main/java/frc/robot/commands/` (ex: `ShooterCommand.java`, `IntakeSparkCommand.java`) as reference patterns.
3. Inject the subsystem in the constructor.
4. Declare requirements with `addRequirements(subsystem)`.
5. Implement `initialize`, `execute`, and `end` as needed.
6. In the main robot repo, set default commands with `setDefaultCommand(...)` or bind commands to buttons in `configureBindings()`.

## Example patterns included

- Subsystems with motors/controllers
- Simple commands that call subsystem actions
- `RobotContainer` wiring

## Conventions

- Keep all hardware IDs and constants in `Constants.java`.
- Keep commands small; push logic into subsystem methods.
- Name classes by intent (`ShooterSubsystem`, `IntakeCommand`).

## Deploying to the robot

Use the WPILib VS Code deploy button or:

```bash
./gradlew deploy
```

## Notes for students

- Start by copying the closest example and modify it.
- If something is unclear, ask a mentor before guessing.
- Keep changes small and commit often.

## Advice for students

1. Build this example project and run it on a demo board.
2. Study the code. If you have questions about any part of it, ask.
3. Details matter. Really understand how it works and why it was written the way it was.
4. There are many ways to implement functions. This is just one example. If you have other ideas, talk to a mentor and experiment in your own branch.
5. Even with flexibility, we have conventions we want followed. Use this code as the guideline for those conventions.

