// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import frc.robot.subsystems.ShooterSubsystem;
import edu.wpi.first.wpilibj2.command.Command;
import java.util.function.DoubleSupplier;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Timer;

/** An example command that uses an example subsystem. */
public class ShooterCommand extends Command {
  @SuppressWarnings({"PMD.UnusedPrivateField", "PMD.SingularField"})
  private final ShooterSubsystem m_subsystem;
  private final DoubleSupplier m_speedSupplier;
  private int m_executeCount = 0;
  private int m_initializeCount = 0;
  private int m_endCount = 0;
  private int m_cmdCancelled = 0;
  private double activeSecondCounter = 0.0;
  private double m_lastActiveSeconds = 0.0;
  private double m_curActiveSeconds = 0.0;

  private final Timer m_activeTimer = new Timer();

  /**
   * Creates a new ExampleCommand.
   *
   * @param subsystem The   subsystem used by this command.
   */
  public ShooterCommand(ShooterSubsystem subsystem, DoubleSupplier speedSupplier) {
    m_subsystem = subsystem;
    m_speedSupplier = speedSupplier;
    m_lastActiveSeconds = 0.0;
    m_curActiveSeconds = 0.0;

    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(subsystem);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    m_initializeCount++;
    SmartDashboard.putNumber("ShooterCommand/InitializeCount", m_initializeCount);
    m_activeTimer.reset();
    m_activeTimer.start();
    SmartDashboard.putBoolean("ShooterCommand/IsScheduled", true);
    SmartDashboard.putNumber("ShooterCommand/cmdCancelled", m_cmdCancelled);
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    double speed = m_speedSupplier.getAsDouble();
    m_subsystem.setShooterSpeed(speed);
    m_executeCount++;
    SmartDashboard.putNumber("ShooterCommand/ExecuteCount", m_executeCount);
    SmartDashboard.putNumber("ShooterCommand/LastSpeed", speed);
    SmartDashboard.putNumber("ShooterCommand/ActiveSeconds", m_activeTimer.get());
  }


  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    m_endCount++;
    SmartDashboard.putNumber("ShooterCommand/EndCount", m_endCount);
    m_activeTimer.stop();
    SmartDashboard.putNumber("ShooterCommand/ActiveSeconds", m_activeTimer.get());
    SmartDashboard.putBoolean("ShooterCommand/IsScheduled", false);
    m_subsystem.setShooterSpeed(0);

    if (interrupted) {
      m_cmdCancelled += 1;
      SmartDashboard.putNumber("ShooterCommand/cmdCancelled", m_cmdCancelled);
    }
  }

  // always returns false so the command will end when the bindings force the interrupt parameter to true
  @Override
   public boolean isFinished() {
     return false;
   }
}
