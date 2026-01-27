// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class IntakeSparkSubsystem extends SubsystemBase {
  private final SparkMax m_motor = new SparkMax(Constants.IntakeSparkCanId, MotorType.kBrushless);
  
  public void setOutput(double output) {
    m_motor.set(output);
  }

  public void stop() {
    m_motor.set(0.0);
  }
}
