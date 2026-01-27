// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Timer;
import frc.robot.subsystems.IntakeSparkSubsystem;
import java.util.function.DoubleSupplier;

public class IntakeSparkCommand extends Command {
  private final IntakeSparkSubsystem m_subsystem;
  private final DoubleSupplier m_speedSupplier;
  private int m_executeCount = 0;
  private int m_initializeCount = 0;
  private int m_cmdCancelled = 0;
  private int m_endCount = 0;
  private final Timer m_activeTimer = new Timer();

  public IntakeSparkCommand(IntakeSparkSubsystem subsystem, DoubleSupplier speedSupplier) {
    m_subsystem = subsystem;
    m_speedSupplier = speedSupplier;
    addRequirements(subsystem);
  }

  @Override
  public void execute() {
    double speed = m_speedSupplier.getAsDouble();
    //System.out.println(speed);
    m_subsystem.setOutput(speed);
    m_executeCount++;
    SmartDashboard.putNumber("IntakeSparkCommand/ExecuteCount", m_executeCount);
    SmartDashboard.putNumber("IntakeSparkCommand/LastSpeed", speed);
    SmartDashboard.putNumber("IntakeSparkCommand/ActiveSeconds", m_activeTimer.get());
  }

  @Override
  public void end(boolean interrupted) {
    m_endCount++;
    SmartDashboard.putNumber("IntakeSparkCommand/EndCount", m_endCount);
    m_activeTimer.stop();
    SmartDashboard.putNumber("IntakeSparkCommand/ActiveSeconds", m_activeTimer.get());
    SmartDashboard.putBoolean("IntakeSparkCommand/IsScheduled", false);
    if (interrupted) {
      m_cmdCancelled += 1;
    }
    SmartDashboard.putNumber("IntakeSparkCommand/cmdCancelled", m_cmdCancelled);

    m_subsystem.stop();
  }

  @Override
  public void initialize() {
    m_initializeCount++;
    SmartDashboard.putNumber("IntakeSparkCommand/InitializeCount", m_initializeCount);
    m_activeTimer.reset();
    m_activeTimer.start();
    SmartDashboard.putBoolean("IntakeSparkCommand/IsScheduled", true);
  }
}
