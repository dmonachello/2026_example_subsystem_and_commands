// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.ctre.phoenix6.controls.DutyCycleOut;
import com.ctre.phoenix6.hardware.TalonFX;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class IntakeFalconSubsystem extends SubsystemBase {
  private final TalonFX m_motor = new TalonFX(Constants.IntakeFalconCanId);
  private final DutyCycleOut m_output = new DutyCycleOut(0);

  public void setOutput(double output) {
    m_motor.setControl(m_output.withOutput(output));
  }

  public void stop() {
    m_motor.setControl(m_output.withOutput(0.0));
  }
}
