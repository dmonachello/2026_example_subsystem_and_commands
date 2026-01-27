// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import frc.robot.Constants;
import frc.robot.commands.IntakeFalconCommand;
import frc.robot.commands.IntakeSparkCommand;
import frc.robot.commands.ShooterCommand;
import frc.robot.subsystems.IntakeFalconSubsystem;
import frc.robot.subsystems.IntakeSparkSubsystem;
import frc.robot.subsystems.ShooterSubsystem;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;

import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;

/**
 * This class is where the bulk of the robot should be declared. Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of the robot (including
 * subsystems, commands, and trigger mappings) should be declared here.
 */
public class RobotContainer {
  DigitalInput upperLimitSw = new DigitalInput(0);

  // The robot's subsystems and commands are defined here...
  private final ShooterSubsystem m_ShooterSubsystem = new ShooterSubsystem();
  //private final IntakeFalconSubsystem m_intakeFalconSubsystem = new IntakeFalconSubsystem();
  private final IntakeSparkSubsystem m_intakeSparkSubsystem = new IntakeSparkSubsystem();

  // Replace with CommandPS4Controller or CommandJoystick if needed
  private final CommandXboxController m_driverController =
      new CommandXboxController(Constants.DriverControllerPort);
      
  private final DoubleSupplier m_shooterSpeedSupplier = new ShooterSpeedSupplier();
  //private final DoubleSupplier m_intakeFalconSpeedSupplier = new IntakeFalconSpeedSupplier();
  private final DoubleSupplier m_intakeSparkSpeedSupplier = new IntakeSparkSpeedSupplier();

  private class ShooterSpeedSupplier implements DoubleSupplier {
    @Override
    public double getAsDouble() {
      return Constants.ShooterSpeed;
    }
  }

  private class IntakeFalconSpeedSupplier implements DoubleSupplier {
    @Override
    public double getAsDouble() {
      return -MathUtil.applyDeadband(m_driverController.getLeftY(), 0.1);
    }
  }

  private class IntakeSparkSpeedSupplier implements DoubleSupplier {
    @Override
    public double getAsDouble() {
      return -MathUtil.applyDeadband(m_driverController.getRightY(), 0.1);
    }
  }

  public boolean isUpperLimitPressed()
  {
    return upperLimitSw.get();
  }

  /** The container for the robot. Contains subsystems, OI devices, and commands. */
  public RobotContainer() {
    System.out.println("Initializing Robot Container");
    // Configure the trigger bindings
    configureBindings();
  }

  /**
   * Use this method to define your trigger->command mappings. Triggers can be created via the
   * {@link Trigger#Trigger(java.util.function.BooleanSupplier)} constructor with an arbitrary
   * predicate, or via the named factories in {@link
   * edu.wpi.first.wpilibj2.command.button.CommandGenericHID}'s subclasses for {@link
   * CommandXboxController Xbox}/{@link edu.wpi.first.wpilibj2.command.button.CommandPS4Controller
   * PS4} controllers or {@link edu.wpi.first.wpilibj2.command.button.CommandJoystick Flight
   * joysticks}.
   */
  private void configureBindings() {
    System.out.println("called configure bindings");

    // m_driverController.leftStick().whileTrue(new ShooterCommand(m_ShooterSubsystem, m_driverController.getLeftY()));
    // m_driverController.leftStick().whileTrue(m_ShooterSubsystem.shootBallCommand(m_driverController.getLeftY()));
    

    m_intakeSparkSubsystem.setDefaultCommand(
      new IntakeSparkCommand(m_intakeSparkSubsystem, m_intakeSparkSpeedSupplier));
  
    // when b is released calls the command end with interrupted true
    m_driverController.b().whileTrue(
      new ShooterCommand(m_ShooterSubsystem, m_shooterSpeedSupplier));

    // try using a timeout decorator - when timer expires calls the command end with interrupted true
    m_driverController.x().onTrue(
      new ShooterCommand(m_ShooterSubsystem, m_shooterSpeedSupplier)
        .withTimeout(0.5));

    // try using a until decorator - when limit switch is hit it calls the command end with interrupted true
    m_driverController.y().onTrue(
      new ShooterCommand(m_ShooterSubsystem, m_shooterSpeedSupplier)
        .until(this::isUpperLimitPressed));
    
    // try using a until decorator - when limit switch is hit it calls the command end with interrupted true
    m_driverController.a().onTrue(
      new ShooterCommand(m_ShooterSubsystem, m_shooterSpeedSupplier)
        .onlyWhile(this::isUpperLimitPressed));
    
        // TBD - add decorator examples for onlyWhile,...

  }

}
