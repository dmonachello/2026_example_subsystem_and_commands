# UI Bindings Guidelines (Command-Based)

This doc is a practical guide for building a driver/operator UI using
WPILib command-based bindings. It is written for our robot style and the
subsystems we typically use (drive, intake, shooter, climber).

## Goals
- Make robot behavior predictable under pressure.
- Keep control mappings simple to learn and hard to misuse.
- Let the scheduler handle command conflicts safely.
- Keep code organized: small commands, clean bindings.

## Core ideas to teach
- A command runs when it is scheduled; the scheduler runs every 20 ms.
- Default commands run whenever a subsystem is idle.
- Button commands override defaults, then defaults resume automatically.
- `end(true)` means the command was interrupted (button release, conflict, or decorator).
- If a command does not override `isFinished()`, it never ends on its own.

## Recommended default commands
Use defaults where you want continuous background control or a safe idle state.

- Drive train: default command reads joysticks and drives.
- Intake: optional default that either:
  - sets motor to 0 each loop (safe idle), or
  - reads an axis for manual control.
- Shooter: usually no default (run only by explicit button commands).
- Climber: no default (safety; only move on explicit input).

## Binding patterns (use the right trigger)
- `onTrue(cmd)`: run once when the button becomes pressed.
- `whileTrue(cmd)`: run as long as the button is held (cancels on release).
- `toggleOnTrue(cmd)`: press once to start, press again to stop.
- `onFalse(cmd)`: run when the button is released (useful for "stop" actions).

Rule of thumb:
- Use `whileTrue` for "hold to run" actions.
- Use `onTrue` for "tap to do a short action" commands.
- Use `toggleOnTrue` for "mode" commands (spin up shooter, climb mode).

Important nuance:
- `whileTrue` reschedules the command every loop while held. If the command
  ends quickly (like a short timeout), it will restart immediately and look
  continuous. Use `onTrue` when you want a single, short action.

## Safety and interlocks
- Prefer sensor-based guards with `until(...)` and `onlyWhile(...)`.
- Example: `intakeCmd.until(beamBreakTripped)` to auto-stop intake.
- Example: `shooterCmd.onlyWhile(shooterClear)` to prevent unsafe run.
- Decorators like `until(...)`, `onlyWhile(...)`, `withTimeout(...)`, and
  `withInterrupt(...)` end a command by interruption, so `end(true)` runs.

## Command design guidelines
- Keep `execute()` lightweight (set motors, read inputs).
- Use `end()` to stop motors and leave the subsystem safe.
- Use `isFinished()` only for commands that should end on their own.
- Avoid long "do everything" commands; use command groups instead.
- Always plan for the interruption path in `end(true)`.

## Why a command is interrupted (`end(true)`)
- Button release on a `whileTrue` binding.
- Another command requiring the same subsystem starts.
- A decorator condition becomes true (`until`, `onlyWhile`, `withTimeout`).
- A command group ends and interrupts its members (race/parallel groups).

## Suggested controller layout (example)
Driver:
- Left stick: drive (default command).
- Right stick: turn/aim (default command).
- Left bumper: intake in (whileTrue).
- Right bumper: eject (whileTrue).

Operator:
- A: spin up shooter (toggleOnTrue or whileTrue).
- B: shoot sequence (onTrue, command group).
- X: climb up (whileTrue).
- Y: climb down (whileTrue).

## Example bindings (pseudo-code)
```
// Drive default
driveSubsystem.setDefaultCommand(new DriveCommand(driveSubsystem, driverStick));

// Intake (hold to run)
driver.leftBumper().whileTrue(new IntakeCommand(intakeSubsystem, intakeSpeed));

// Eject (hold to run reverse)
driver.rightBumper().whileTrue(new EjectCommand(intakeSubsystem, ejectSpeed));

// Shooter spin up (toggle)
operator.a().toggleOnTrue(new ShooterSpinUpCommand(shooterSubsystem, rpm));

// Shoot sequence (tap)
operator.b().onTrue(new ShootSequenceCommand(shooterSubsystem, feederSubsystem));

// Climb (safety: only while held)
operator.x().whileTrue(new ClimbUpCommand(climberSubsystem));
operator.y().whileTrue(new ClimbDownCommand(climberSubsystem));
```

## Example from our current code
- `B` uses `whileTrue` to run the shooter only while held.
- `X` uses `onTrue` with `withTimeout(0.5)` to show a short, timed run.

## Appendix: How the Scheduler Works (and Its API)
The CommandScheduler runs every 20 ms in the robot loop. Each cycle, it:
- Polls triggers and schedules any commands that should start.
- Runs `execute()` on all scheduled commands.
- Checks `isFinished()` and ends commands that are done.
- Starts default commands for any idle subsystems.

Key behaviors:
- Only one command can require a subsystem at a time. If a new command is
  scheduled with the same requirement, the old one is canceled (`end(true)`).
- Default commands automatically resume after an interrupting command ends.
- Commands that never finish (`isFinished()` false) run until canceled.

Scheduler API that affects behavior:
- `CommandScheduler.getInstance().schedule(cmd)`: start a command now.
- `CommandScheduler.getInstance().cancel(cmd)`: stop a specific command
  (calls `end(true)`).
- `CommandScheduler.getInstance().cancelAll()`: stop everything.
- `CommandScheduler.getInstance().isScheduled(cmd)`: check if running.
- `CommandScheduler.getInstance().run()`: called in robot periodic to
  advance the scheduler (this is where all scheduling happens).

API related to defaults and requirements:
- `subsystem.setDefaultCommand(cmd)`: defines the idle command for that
  subsystem.
- `command.addRequirements(subsystem)`: declares what the command uses.

Decorator API (adds interrupt conditions):
- `cmd.until(condition)`: cancel when condition becomes true.
- `cmd.onlyWhile(condition)`: cancel when condition becomes false.
- `cmd.withTimeout(seconds)`: cancel after time expires.
- `cmd.withInterrupt(condition)`: cancel when condition becomes true.

Command group notes:
- Parallel race groups end when any member finishes; the others are
  interrupted (`end(true)`).
- Sequential groups run commands one after another; each must finish
  before the next starts.

## Testing checklist
- Verify defaults resume after button commands end.
- Confirm `end()` stops motors every time.
- Check that `whileTrue` commands cancel on release.
- Test sensor interlocks (beam break, limits) to prevent damage.
- Log key states to SmartDashboard for debugging.

## Teaching tips (students)
- Use the SmartDashboard counters to show the command life cycle.
- Demonstrate default commands by showing huge ExecuteCount values.
- Demonstrate button commands by pressing/releasing and watching EndCount.
