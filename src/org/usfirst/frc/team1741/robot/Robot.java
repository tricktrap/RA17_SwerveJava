
package org.usfirst.frc.team1741.robot;

import java.io.File;
import java.util.Calendar;

import edu.wpi.first.wpilibj.AnalogGyro;
import edu.wpi.first.wpilibj.BuiltInAccelerometer;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends IterativeRobot {

    final String defaultAuto = "Default";
    final String customAuto = "My Auto";
    String autoSelected;
    SendableChooser chooser;
    
	AnalogGyro gyro;
	BuiltInAccelerometer acceler;
	public static Logger logger;
	Timer timer;
	SwerveDrive drive;
	Gamepad driver;
	EdgeDetect driveMode;
	
	double x;
	double y;
	double twist;
	boolean fieldOrient;
    
	
    public Robot()
    {
    	chooser = null;
		gyro = null;
		acceler = null;
		logger = null;
		timer = null;
		drive = null;
		driver = null;
		driveMode = null;
		x = 0;
		y = 0;
		twist = 0;
		fieldOrient = true;
    }

    public void robotInit() 
    {
    	Config.LoadFromFile("/home/lvuser/config.txt");
    	chooser = new SendableChooser();
        chooser.addDefault("Default Auto", defaultAuto);
        chooser.addObject("My Auto", customAuto);
        SmartDashboard.putData("Auto choices", chooser);
        
		////////////////////////////////////////////////
		gyro = new AnalogGyro(1);
		gyro.setSensitivity(0.007);
		gyro.reset();
		gyro.calibrate();
		acceler = new BuiltInAccelerometer();
		////////////////////////////////////////////////
		logger = new Logger();
		
		timer = new Timer();
		////////////////////////////////////////////////
		drive = new SwerveDrive(1, 2, 0, 3, 4, 1, 5, 6, 2, 7, 8, 3);
		////////////////////////////////////////////////
		driver = new Gamepad(0);
		////////////////////////////////////////////////
		driveMode = new EdgeDetect();
    }

	public void autonomousInit() 
    {
    	autoSelected = (String) chooser.getSelected();
//		autoSelected = SmartDashboard.getString("Auto Selector", defaultAuto);
		System.out.println("Auto selected: " + autoSelected);
    }

    public void autonomousPeriodic() 
    {
    	switch(autoSelected) {
    	case customAuto:
        //Put custom auto code here   
            break;
    	case defaultAuto:
    	default:
    	//Put default auto code here
            break;
    	}
    }

    public void teleopInit()
    {
    	StartLogging("teleop",logger);
    	SetupLogging();
    	ReloadConfig();
    	timer.reset();
    	timer.start();
    }
    
    public void teleopPeriodic() 
    {
    	System.out.println(gyro.getAngle());
    	
    	x = driver.GetRightX();
    	y = driver.GetRightY();
    	twist = driver.GetLeftX();
    	
    	if(x >= -0.1 && x <= 0.1){x=0;}
    	if(y >= -0.1 && y <= 0.1){y=0;}
    	if(twist >= -0.1 && twist <= 0.1){twist=0;}
    	if(driveMode.Check(driver.GetStart()))
    	{
    		fieldOrient = !fieldOrient;
    	}
    	
    	drive.Swerve(x,y,twist,gyro.getAngle(),fieldOrient);
    	
    	Log(timer.get());
    }
    
    public void testInit() 
    {
    	
    }
    
    public void testPeriodic() 
    {
    	
    }
    
    public void disabledInit() 
    {
    
    }
    
    public void disabledPeriodic() 
    {
    
    }
    
	void StartLogging(String mode, Logger l)
	{
		String robot = !(Config.GetSetting("isPrototype", 0) == 0) ? "_proto" : "_comp";
		l.Close();
		Calendar calendar = Calendar.getInstance();
		String dir = "/home/lvuser";
		if(new File("/media/sda").exists())
		{
			dir = "/media/sda";
		}
		String name = dir + "/log-" + calendar.get(Calendar.YEAR) + "-" +
				calendar.get(Calendar.MONTH) + "-" + calendar.get(Calendar.DAY_OF_MONTH) + "_" +
				calendar.get(Calendar.HOUR_OF_DAY) + "-" + calendar.get(Calendar.MINUTE) + "-" +
				calendar.get(Calendar.SECOND) + "_" + mode + robot + ".csv";
		System.out.println(name);
		l.Open(name);
	}

	void SetupLogging()
	{
		logger.AddAttribute("Time");
		logger.AddAttribute("AccX");
		logger.AddAttribute("AccY");
		logger.AddAttribute("AccZ");
		drive.SetupLogging(logger);
		logger.WriteAttributes();
	}

	void Log(double d)
	{
		logger.Log("Time", d);
		logger.Log("AccX", acceler.getX());
		logger.Log("AccY", acceler.getY());
		logger.Log("AccZ", acceler.getZ());
		drive.Log();
		logger.WriteLine();
	}

	void ReloadConfig()
	{
		Config.LoadFromFile("/home/lvuser/config.txt");
		drive.ReloadConfig();
	}
}
