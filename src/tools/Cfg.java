package tools;

import system.*;

/**
 * The configuration mode class. This class performs the configuration mode.
 */
public class Cfg 
{
 /** The XML system configuration file (path and name). */
 private String systemconfig_file;
 /** The devices debug flag.<p> 
  *  false - doesn't show the devices configuration.<br>
  *  true - show the devices configuration.
  */
 private boolean devices_debug;
 /** The system debug flag.<p> 
  *  false - don't show the system configuration.<br>
  *  true - show the system configuration.
  */
 private boolean system_debug;
 /** The cpu debug flag.<p> 
  *  false - doesn't show the cpu configuration.<br> 
  *  true - show the cpu configuration.
  */
 private boolean cpu_debug;
	
 /**
  * Instantiates a new configuration mode.
  *
  * @param systemconfig_file the XML system configuration file name (path and name).
  * @param system_debug the system debug flag.<p> 
  * false - doesn't show the system configuration.<br>
  * true - show the system configuration.
  * @param cpu_debug the cpu debug flag.<p> 
  * false - doesn't show the cpu configuration.<br> 
  * true - show the cpu configuration.
  * @param devices_debug the devices debug flag.<p> 
  * false - doesn't show the system configuration.<br>
  * true - show the system configuration.
  */
 public Cfg(String systemconfig_file,boolean system_debug,boolean cpu_debug,boolean devices_debug)
 {
  this.systemconfig_file = systemconfig_file;
  this.devices_debug = devices_debug;
  this.system_debug = system_debug;
  this.cpu_debug = cpu_debug;
 }
 
 // ----------------------------------------------------------------
 // method used to verify and validate the system configuration file
 // ----------------------------------------------------------------
 
 /**
  * This method verifies and validates the XML system configuration files.
  */
 public final void main()
 {
  SystemConfiguration system_configuration;
  
  System.out.println("");
  system_configuration = new SystemConfiguration(false);
  try
  {
   system_configuration.load(systemconfig_file,system_debug,cpu_debug,devices_debug);
   System.out.println("");
   System.out.println(" . Valid Configuration");
  }
  catch(SystemConfigurationException e)
  {
   System.out.println("");
   System.out.println(" " + "!Warning: " + e.getMessage());
   System.out.println(""); 
   System.out.println(" . Not valid Configuration");	 
  }
  System.out.println("");
  System.exit(0);
 }
}
