# Command-Based Programming Explained (Text Only)

This document explains how our FRC robot program works using WPILib command-based programming. It is written for high school students and uses the **example project code** (not the main robot repo). The example code is at https://github.com/dmonachello/2026_example_subsystem_and_commands. When you run this example, the SmartDashboard counters and command behavior should match the descriptions below. The goal is to understand what the code does, why it is structured this way, and how the same approach will be used on the competition robot.

## 1) The Big Picture: Subsystems, Commands, and the Scheduler

In command-based programming, we separate the robot into three ideas: subsystems, commands, and the scheduler.

- A **subsystem** is a hardware group like the shooter or intake.
- A **command** is a unit of robot behavior like "spin the shooter" or "run the intake."
- The **scheduler** runs every 20 ms and decides which commands should start, run, or end.

This separation makes our code easier to read, test, and expand. Instead of one giant method with lots of if-statements, we have many small commands that each do one thing. The scheduler handles the timing and prevents conflicting commands from running on the same subsystem.

## 2) Where This Shows Up in the Example Code

In `RobotContainer.java` (example repo), we create subsystems and define button bindings. This is the control center where we say which button runs which command and which commands are default commands.

Current example bindings:

- `IntakeSparkCommand` is the **default** command for `IntakeSparkSubsystem`.
- `ShooterCommand` is scheduled by multiple buttons to demonstrate different command endings:
  - **B button**: `whileTrue(...)` runs while held.
  - **X button**: `onTrue(...withTimeout(0.5))` ends after 0.5 seconds.
  - **Y button**: `onTrue(...until(this::isUpperLimitPressed))` ends when the limit switch is hit.
  - **A button**: `onTrue(...onlyWhile(this::isUpperLimitPressed))` only runs while the switch is pressed.

Default command binding in `RobotContainer`:

```java
m_intakeSparkSubsystem.setDefaultCommand(
    new IntakeSparkCommand(m_intakeSparkSubsystem, m_intakeSparkSpeedSupplier)
);
```

This line means the intake subsystem always has a command running when nothing else is using it. It also shows where the speed supplier is connected: the command receives a `DoubleSupplier`, then calls it each loop to get the latest joystick-based speed.

How the speed suppliers work (step by step):

- `DoubleSupplier` is a tiny object that answers one question: "what is the speed right now?"
- We pass a `DoubleSupplier` into the command so the command can ask for the latest speed every 20 ms instead of using a fixed value.

In the example code:

- `ShooterSpeedSupplier` always returns `Constants.ShooterSpeed`. That means the shooter runs at a fixed speed whenever the command is scheduled.
- `IntakeSparkSpeedSupplier` reads the **right joystick Y value** from the Xbox controller and applies two changes:
  1. **Deadband**: `MathUtil.applyDeadband(..., 0.1)` makes small joystick noise count as zero, so the motor does not twitch.
  2. **Invert**: the value is multiplied by `-1` so pushing the stick forward gives a positive speed.

Where the value is used:

1. The command is created with the supplier: `new IntakeSparkCommand(m_intakeSparkSubsystem, m_intakeSparkSpeedSupplier)`.
2. Inside `execute()`, the command calls `m_speedSupplier.getAsDouble()` every scheduler loop to read the joystick speed at that moment.
3. That current value is sent to the subsystem (`setOutput(speed)`), so the motor follows the live joystick position.

So the data flow is: **Xbox controller → supplier → command `execute()` → subsystem motor output**.

Note: This example uses small `DoubleSupplier` classes to make the flow easier to understand and because the Java lambda syntax used for this is often confusing at first. Think of the supplier as a pointer to a little function; the command calls it any time it needs the current joystick value. In other code you will often see the same value provider concept implemented inline using a lambda.

Example using a lambda in `RobotContainer` (same behavior as `IntakeSparkSpeedSupplier`):

```java
m_intakeSparkSubsystem.setDefaultCommand(
    new IntakeSparkCommand(
        m_intakeSparkSubsystem,
        () -> -MathUtil.applyDeadband(m_driverController.getRightY(), 0.1)
    )
);
```

Key files in the example repo:

- `RobotContainer.java`: creates subsystems and connects inputs to commands.
- `ShooterCommand.java`: runs the shooter motor and logs counters to SmartDashboard.
- `IntakeSparkCommand.java`: default command for the intake subsystem.

## Demo Board Hardware and Wiring (example repo)

This section describes the demo board used with this **example repo**. It is not the same as the main robot wiring.

Hardware:

- roboRIO
- 2x REV Spark MAX motor controllers
- 2x NEO brushless motors
- REV Robotics PDP (power distribution)
- Battery + main breaker/switch
- Xbox controller
- One digital limit switch (DIO)

Wiring and IDs (from the example code):

- Shooter Spark MAX CAN ID: `Constants.ShooterCanID` = `9`
- Intake Spark MAX CAN ID: `Constants.IntakeSparkCanId` = `24`
- Limit switch DIO port: `0` (`new DigitalInput(0)` in `RobotContainer`)
- Xbox controller USB port: `Constants.DriverControllerPort` = `0`

Motor mapping in this example:

- Shooter motor uses the Spark MAX at CAN ID 9 (`ShooterSubsystem`)
- Intake motor uses the Spark MAX at CAN ID 24 (`IntakeSparkSubsystem`)

Wiring diagram image:

`docs/demo_board_wiring.png`

## 3) The Command Life Cycle

Every command has the same life cycle. The scheduler calls these methods in order:

- `initialize()`: runs once when the command is first scheduled.
- `execute()`: runs every scheduler loop while the command is scheduled.
- `end(boolean interrupted)`: runs once when the command stops, either normally or by interruption.
- `isFinished()`: returns true when the command should stop on its own.

In `ShooterCommand.java` and `IntakeSparkCommand.java`, we publish counters to SmartDashboard (`InitializeCount`, `ExecuteCount`, `EndCount`) and also show `IsScheduled` and `ActiveSeconds`. That makes the life cycle visible and concrete.

Scheduler hooks (dashboard telemetry):

The example `RobotContainer` registers three scheduler callbacks to record which command most recently changed state:

- `onCommandInitialize`: fires when a command is scheduled.
- `onCommandFinish`: fires when a command ends normally (`isFinished()` returns true).
- `onCommandInterrupt`: fires when a command ends because it was interrupted.

Each callback writes the command name to SmartDashboard:

- `Cmd/LastInit`
- `Cmd/LastFinish`
- `Cmd/LastInterrupt`

To reduce overhead, the code only publishes when the value changes (it compares to the last name it posted).

## 4) Default Commands vs Non-Default Commands

Default commands are special commands assigned to subsystems. They automatically run when no other command is using that subsystem. This means the subsystem always has a command controlling it.

In the example repo, `IntakeSparkCommand` is a default command, so it starts when the robot is enabled and keeps running unless another command needs the intake.

`ShooterCommand` is **not** a default command. It only runs when you press a button, and it stops when:

- the button is released (`whileTrue`),
- the timeout expires (`withTimeout`),
- the limit switch is pressed (`until`), or
- the limit switch is no longer pressed (`onlyWhile`).

Because `ShooterCommand.isFinished()` always returns false, the command ends only when the trigger or decorator causes an interruption.

## 5) Why the Dashboard Counters Look the Way They Do

The counters prove the behavior of default vs non-default commands:

- **Default intake command**: `InitializeCount` is usually 1, `ExecuteCount` becomes very large, and `EndCount` stays low because the command almost never ends.
- **Shooter command**: `InitializeCount` and `EndCount` increase each time a button starts and ends the command. `ExecuteCount` only increases while the command is scheduled.
- **cmdCancelled** increases when a command ends due to an interruption (button release, timeout, or limit switch condition).

This is the key lesson: default commands are "always-on" background behaviors, while non-default commands are "on-demand" behaviors tied to triggers.

## 6) How the Scheduler Prevents Conflicts

Each command declares which subsystem it requires. The scheduler uses this to prevent two commands from controlling the same subsystem at the same time. If a new command is scheduled that needs a subsystem already in use, the scheduler cancels the old command (calling `end(true)`) and gives control to the new one.

This is why default commands are so useful: when a temporary command ends, the scheduler automatically returns control to the default command without any extra code.

## 7) How This Scales to the Competition Robot

The exact same structure will be used on the competition robot. The drivebase will have a default command that reads the joysticks. The shooter, intake, and scoring mechanisms will have button commands for specific actions like "spin up," "shoot," or "score."

The scheduler guarantees that only one command runs each subsystem at a time and returns to the default behavior when the action finishes. This approach makes it easy to add features: create a new command, bind it to a button, and let the scheduler handle the switching automatically.

## 8) Key Takeaways

- Commands define actions; subsystems define hardware; the scheduler runs everything.
- Default commands run when nothing else is using a subsystem.
- Button commands run only when scheduled by a trigger.
- `end(true)` means the command was interrupted (button release or another command).
- Decorators like `withTimeout`, `until`, and `onlyWhile` provide controlled ways to stop commands.
- The SmartDashboard counters in the example commands make the system visible and easy to explain.
