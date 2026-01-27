// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants;

import com.revrobotics.spark.SparkMax;

import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

import com.revrobotics.spark.SparkLowLevel.MotorType;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;

public class ShooterSubsystem extends SubsystemBase {
  /** Creates a new ExampleSubsystem. */
  private final SparkMax mShooterMotor = new SparkMax(Constants.ShooterCanID, MotorType.kBrushless);

  public ShooterSubsystem() {}

  /**
   * Example command factory method.
   * @return a command
   */
  private double mShooterSpeed = 0;

  public double getShooterSpeed() {
        // Query some boolean state, such as a digital sensor.
    return mShooterSpeed;
    }
      
  public void setShooterSpeed(double motorSpeed) {
    mShooterSpeed = motorSpeed;
  }

  // create the periodic as a fail-safe
  public void periodic() {
    mShooterMotor.set(mShooterSpeed);
   }

  // public Command shootBallCommand(DoubleSupplier speed) {
  //   System.out.println("COMMAND SCHEDULED");
  //   return run(() -> {
  //     // setShooterSpeed(speed);
  //     // mShooterMotor.setVoltage(0.5);
  //     mShooterMotor.set(speed.getAsDouble());
  //   }).finallyDo(() -> mShooterMotor.set((0.0)));
  // }

  }
