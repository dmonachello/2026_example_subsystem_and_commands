// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Timer;
import frc.robot.subsystems.IntakeFalconSubsystem;
import java.util.function.DoubleSupplier;

public class IntakeFalconCommand extends Command {
  private final IntakeFalconSubsystem m_subsystem;
  private final DoubleSupplier m_speedSupplier;
  private int m_executeCount = 0;
  private int m_initializeCount = 0;
  private int m_endCount = 0;
  private final Timer m_activeTimer = new Timer();

  public IntakeFalconCommand(IntakeFalconSubsystem subsystem, DoubleSupplier speedSupplier) {
    m_subsystem = subsystem;
    m_speedSupplier = speedSupplier;
    addRequirements(subsystem);
  }

  @Override
  public void execute() {
    double speed = m_speedSupplier.getAsDouble();
    m_subsystem.setOutput(speed);
    m_executeCount++;
    SmartDashboard.putNumber("IntakeFalconCommand/ExecuteCount", m_executeCount);
    SmartDashboard.putNumber("IntakeFalconCommand/LastSpeed", speed);
    SmartDashboard.putNumber("IntakeFalconCommand/ActiveSeconds", m_activeTimer.get());
  }

  @Override
  public void end(boolean interrupted) {
    m_endCount++;
    SmartDashboard.putNumber("IntakeFalconCommand/EndCount", m_endCount);
    m_activeTimer.stop();
    SmartDashboard.putNumber("IntakeFalconCommand/ActiveSeconds", m_activeTimer.get());
    SmartDashboard.putBoolean("IntakeFalconCommand/IsScheduled", false);
    m_subsystem.stop();
  }

  @Override
  public void initialize() {
    m_initializeCount++;
    SmartDashboard.putNumber("IntakeFalconCommand/InitializeCount", m_initializeCount);
    m_activeTimer.reset();
    m_activeTimer.start();
    SmartDashboard.putBoolean("IntakeFalconCommand/IsScheduled", true);
  }
}
