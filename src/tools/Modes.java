package tools;

import java.io.*;

import system.*;

/**
 * The development tools modes class. This class performs the decoding of the command line arguments and initializes the respective development tools mode.
 */
public class Modes
{
 // -------------------------------------------
 // method used to determine the operating mode
 // -------------------------------------------

 /**
  * This method is used to decode the command line arguments and initializes the development tools modes.
  *
  * @param args the command line arguments.
  */
 public static void main(String[] args)
 {
  String systemconfig_file = null;
  String profile_file = null;
  String binary_file = null;
  String trace_file = null;
  String port_number = null;
  boolean events = false;
  boolean debug = false;
  boolean devices_debug = false;
  boolean system_debug = false;
  boolean cpu_debug = false;
  int arguments = 1;
  int aux;
  
  if(args.length > 0)
  {
   if(args[0].equals("-tst"))
   {
    if(args.length >=2 && args.length <= 10)
    {
     for(aux = 1;aux < args.length;aux++)
     {
	  if(args[aux].equals("--syscf") && systemconfig_file == null)
	  {
	   systemconfig_file = args[aux + 1];
	   arguments = arguments + 2;
	   aux++;
      }
 	  else if(args[aux].equals("--prff") && profile_file == null)
	  {
 	   profile_file = args[aux + 1];
 	   arguments = arguments + 2;
	   aux++;
	  }
 	  else if(args[aux].equals("--trcf") && trace_file == null)
      {
       trace_file = args[aux + 1];
       arguments = arguments + 2;
       aux++;
      }
 	  else if(args[aux].equals("--eventv") && events == false)
      {
       events = true;
       arguments++;
      }
 	  else if(binary_file == null)
      {
 	   binary_file = args[aux];
 	   arguments++;
 	  }                            
     }
    } 
    
    if(args.length == arguments && binary_file != null)
      tst(systemconfig_file,profile_file,trace_file,binary_file,events);
    
   }
   else if(args[0].equals("-sim"))
   {
    if(args.length >=2 && args.length <= 5)
    {
     for(aux = 1;aux < args.length;aux++)
     {
      if(args[aux].equals("--syscf") && systemconfig_file == null)
      {
       systemconfig_file = args[aux + 1];
       arguments = arguments + 2;
       aux++;
      }
      else if(args[aux].equals("--eventv") && events == false)
      {
       events = true;
       arguments++;
      }
      else if(binary_file == null)
      {
 	   binary_file = args[aux];
 	   arguments++;
      }     
     }
    } 
    
    if(args.length == arguments && binary_file != null)
      sim(systemconfig_file,binary_file,events);
     
   }
   else if(args[0].equals("-gdb"))
   {
    for(aux = 1;aux < args.length;aux++)
    {
     if(args[aux].equals("--syscf") && systemconfig_file == null)
     {
      systemconfig_file = args[aux + 1];
      aux++;
      arguments = arguments + 2;
     }
     else if(args[aux].equals("--portc") && port_number == null)
     {
      port_number = args[aux + 1];
      aux++;
      arguments = arguments + 2;
     }
     else if(args[aux].equals("--eventv") && events == false)
     {
      events = true;
      arguments++;
     }
     else if(args[aux].equals("--prtclv") && debug == false)
     {
      debug = true;
      arguments++;
     }
    }
    
    if(args.length == arguments)
      gdb(systemconfig_file,port_number,debug,events);
    
   }
   else if(args[0].equals("-prf"))
   {
    if(args.length >= 2 && args.length <= 8)
    {
     for(aux = 1;aux < args.length;aux++)
     {
      if(args[aux].equals("--syscf") && systemconfig_file == null)
      {
       systemconfig_file = args[aux + 1];
       arguments = arguments + 2;
       aux++;
      }
      else if(args[aux].equals("--prff") && profile_file == null)
      {
       profile_file = args[aux + 1];
       arguments = arguments + 2;
       aux++;
      }
      else if(binary_file == null)
      {
 	   binary_file = args[aux];
 	   arguments++;
      }
 	 }     
    }
     
    if(args.length == arguments && binary_file != null)
      prf(systemconfig_file,profile_file,binary_file);
 
   }
   else if(args[0].equals("-trc"))
   {
    if(args.length >= 2 && args.length <= 8)
    {
     for(aux = 1;aux < args.length;aux++)
     {
      if(args[aux].equals("--syscf") && systemconfig_file == null)
      {
       systemconfig_file = args[aux + 1];
       arguments = arguments + 2;
       aux++;
      }
      else if(args[aux].equals("--trcf") && trace_file == null)
      {
       trace_file = args[aux + 1];
       arguments = arguments + 2;
       aux++;
      }
      else if(binary_file == null)
      {
 	   binary_file = args[aux];
 	   arguments++;
      }
 	 }     
    }
     
    if(args.length == arguments && binary_file != null)
      trc(systemconfig_file,trace_file,binary_file);
 
   }
   else if(args[0].equals("-elf"))
   {
	if(args.length == 2)
      elf(args[1]);
   }
   else if(args[0].equals("-cfg"))
   {
	for(aux = 1;aux < args.length;aux++)
	{  
     if(args[aux].equals("--cpuv") && cpu_debug == false)
     {
      cpu_debug = true;
      arguments++;
     }
     else if(args[aux].equals("--devv") && devices_debug == false)
     {
      devices_debug = true;
      arguments++;
     }
     else if(args[aux].equals("--sysv") && system_debug == false)
     {
      system_debug = true;
      arguments++;
     }
     else if(systemconfig_file == null)
     {
      systemconfig_file = args[aux];
	  arguments++; 
     }
	} 
     
	if(args.length == arguments)
	  cfg(systemconfig_file,system_debug,cpu_debug,devices_debug);
  
   }
   else if(args[0].equals("--version"))
   {
    if(args.length == 1)
      version();
   }
   else if(args[0].equals("--help"))
   {	
    if(args.length == 1)
      help();
   }
   System.out.println(Configuration.command + ": missing file operand");
   System.out.println("Try `dtool --help' for more information.");
   System.exit(0);
  }
  else
  {
   System.out.println(Configuration.command + ": missing file operand");
   System.out.println("Try `dtool --help' for more information.");
   System.exit(0);	  
  }
 }
 
 // ----------------------------------------------
 // methods used to initialize the operating modes
 // ----------------------------------------------

 /**
  * This method starts the test mode. Only used for debug purposes.
  *
  * @param systemconfig_file the XML system configuration file (path and name).
  * @param profile_file the profiler (performance analysis) file (path and name). Where the profile results of the simulation will be saved.
  * @param trace_file the instruction trace file (path and name). Where the results of the instruction trace simulation will be saved.
  * @param binary_file the binary file to be simulated (path and name).
  * @param events the cpu events flag.<p>
  * false - doesn't notify if any cpu events occur.<br>
  * true - notify if any cpu events occur.
  */
 private static void tst(String systemconfig_file,String profile_file,String trace_file,String binary_file,boolean events)
 {
  File file;
  Tst tst;

  System.out.println(Configuration.program_name + " " + Configuration.version  + " (" + Configuration.command + ") [" + Configuration.date + "]");
  System.out.println("by " + Configuration.name + " (email: " + Configuration.email + ")");
  System.out.println("");
  System.out.println("Test mode:");
  if(systemconfig_file == null)
	systemconfig_file = Configuration.systemconfig_file_default_full_path;
  if(profile_file == null)
	profile_file = Configuration.profile_file_default;
  else
  {
   if(profile_file.contains("/") || profile_file.contains(":") || profile_file.contains("*") || profile_file.contains("?") || profile_file.contains("\""))
   {
	System.out.println(" !Warning: the filename <" + profile_file + "> contains unsupported characters");
	System.exit(0);
   }
	     
   try
   {
	file = new File(profile_file);
	file.createNewFile();		
   }
   catch(Exception e)
   {
	System.out.println(" !Warning: the file <" + profile_file + "> can't be created");
	System.exit(0);   
   }
  }  
  if(trace_file == null)
	trace_file = Configuration.trace_file_default;
  else
  {
   if(trace_file.contains("/") || trace_file.contains(":") || trace_file.contains("*") || trace_file.contains("?") || trace_file.contains("\""))
   {
	System.out.println(" !Warning: the filename <" + trace_file + "> contains unsupported characters");
	System.exit(0);
   }
	     
   try
   {
	file = new File(trace_file);
	file.createNewFile();		
   }
   catch(Exception e)
   {
	System.out.println(" !Warning: the file <" + trace_file + "> can't be created");
	System.exit(0);   
   }
  }
  tst = new Tst(systemconfig_file,profile_file,trace_file,binary_file,events);
  tst.main();	 
 }
 
 /**
  * This method starts the simulator mode.
  *
  * @param systemconfig_file the XML system configuration file (path and name).
  * @param binary_file the binary file to be simulated (path and name).
  * @param events the cpu events flag.<p>
  * false - doesn't notify if any cpu events occur.<br>
  * true - notify if any cpu events occur.
  */
 private static void sim(String systemconfig_file,String binary_file,boolean events)
 { 
  Sim sim;
  
  System.out.println(Configuration.program_name + " " + Configuration.version  + " (" + Configuration.command + ") [" + Configuration.date + "]");
  System.out.println("by " + Configuration.name + " (email: " + Configuration.email + ")");
  System.out.println("");
  System.out.println("Simulator mode:");
  if(systemconfig_file == null)
	systemconfig_file = Configuration.systemconfig_file_default_full_path;
  sim = new Sim(systemconfig_file,binary_file,events);
  sim.main();
 }

 /**
  * This method starts the debug (GDB server) mode.
  *
  * @param systemconfig_file the XML system configuration file (path and name).
  * @param port_number the port_number
  * @param debug the debug flag.
  * @param events the cpu events flag.<p>
  * false - doesn't notify if any cpu events occur.<br>
  * true - notify if any cpu events occur.x
  */
 private static void gdb(String systemconfig_file,String port_number,boolean debug,boolean events)
 {
  Gdb gdb;
  int port = Configuration.port_number_default;
  
  System.out.println(Configuration.program_name + " " + Configuration.version  + " (" + Configuration.command + ") [" + Configuration.date + "]");
  System.out.println("by " + Configuration.name + " (email: " + Configuration.email + ")");
  System.out.println("");
  System.out.println("GDB Server mode:");
  if(port_number != null)
  {
   try
   {
    port = Integer.valueOf(port_number,10).intValue();
    if(port < 0)
    {
	 System.out.println(" " + "!Warning: the port number is a negative number.");
	 System.out.println("");
	 System.exit(0);
	}
   }
   catch(NumberFormatException e)
   {
    System.out.println(" " + "!Warning: the port number contain a no decimal digit.");
    System.out.println("");
    System.exit(0);
   }
  }
  if(systemconfig_file == null)
	systemconfig_file = Configuration.systemconfig_file_default_full_path;
  gdb = new Gdb(systemconfig_file,port,debug,events);
  gdb.main();
 }
  
 /**
  * This method starts the profiler (performance analysis) mode.
  *
  * @param systemconfig_file the XML system configuration file (path and name).
  * @param profile_file the profiler (performance analysis) file (path and name). Where the profile results of the simulation will be saved.
  * @param binary_file the binary file to be simulated (path and name).
  */
 private static void prf(String systemconfig_file,String profile_file,String binary_file)
 {
  File file;
  Prf prf;

  System.out.println(Configuration.program_name + " " + Configuration.version  + " (" + Configuration.command + ") [" + Configuration.date + "]");
  System.out.println("by " + Configuration.name + " (email: " + Configuration.email + ")");
  System.out.println("");
  System.out.println("Profile mode:");
  if(systemconfig_file == null)
	systemconfig_file = Configuration.systemconfig_file_default_full_path;
  if(profile_file == null)
	profile_file = Configuration.profile_file_default;
  else
  {
   if(profile_file.contains("/") || profile_file.contains(":") || profile_file.contains("*") || profile_file.contains("?") || profile_file.contains("\""))
   {
	System.out.println(" !Warning: the filename <" + profile_file + "> contains unsupported characters");
	System.exit(0);
   }
	     
   try
   {
	file = new File(profile_file);
	file.createNewFile();		
   }
   catch(Exception e)
   {
	System.out.println(" !Warning: the file <" + profile_file + "> can't be created");
	System.exit(0);   
   }
  }
  prf = new Prf(systemconfig_file,profile_file,binary_file);
  prf.main();
 }
 
 /**
  * This method starts the instruction trace mode.
  *
  * @param systemconfig_file the XML system configuration file (path and name).
  * @param trace_file the instruction trace file (path and name). Where the results of the instruction trace simulation will be saved.
  * @param binary_file the binary file to be simulated (path and name).
  */
 private static void trc(String systemconfig_file,String trace_file,String binary_file)
 {
  File file;
  Trc trc;
  
  System.out.println(Configuration.program_name + " " + Configuration.version  + " (" + Configuration.command + ") [" + Configuration.date + "]");
  System.out.println("by " + Configuration.name + " (email: " + Configuration.email + ")");
  System.out.println("");
  System.out.println("Trace mode:");
  if(systemconfig_file == null)
	systemconfig_file = Configuration.systemconfig_file_default_full_path;  
  if(trace_file == null)
	trace_file = Configuration.trace_file_default;
  else
  {
   if(trace_file.contains("/") || trace_file.contains(":") || trace_file.contains("*") || trace_file.contains("?") || trace_file.contains("\""))
   {
	System.out.println(" !Warning: the filename <" + trace_file + "> contains unsupported characters");
	System.exit(0);
   }
	     
   try
   {
	file = new File(trace_file);
	file.createNewFile();		
   }
   catch(Exception e)
   {
	System.out.println(" !Warning: the file <" + trace_file + "> can't be created");
	System.exit(0);   
   }
  }  
  trc = new Trc(systemconfig_file,trace_file,binary_file);
  trc.main();
 }
 
 /**
  * This method starts the executable and linking format (ELF) mode. Only used for debug purposes.
  *
  * @param binary_file the binary file to be simulated (path and name).
  */
 private static void elf(String binary_file)
 {
  Elf elf;
	    
  System.out.println(Configuration.program_name + " " + Configuration.version  + " (" + Configuration.command + ") [" + Configuration.date + "]");
  System.out.println("by " + Configuration.name + " (email: " + Configuration.email + ")");
  System.out.println("");
  System.out.println("Elf Binary File mode:");
  elf = new Elf(binary_file);
  elf.main();
 }
 
 /**
  * This method starts the configuration mode. Only used for debug purposes.
  *
  * @param systemconfig_file the XML system configuration file (path and name).
  * @param system_debug the system debug flag.<p> 
  * false - doesn't show the system configuration.<br>
  * true - show the system configuration.
  * @param cpu_debug the cpu debug flag.<p> 
  * false - doesn't show the cpu configuration.<br> 
  * true - show the cpu configuration.
  * @param devices_debug the devices debug flag.<p> 
  * false - don't show the devices configuration.<br>
  * true - show the devices configuration.
  */
 private static void cfg(String systemconfig_file,boolean system_debug,boolean cpu_debug,boolean devices_debug)
 {
  Cfg cfg;
  
  System.out.println(Configuration.program_name + " " + Configuration.version  + " (" + Configuration.command + ") [" + Configuration.date + "]");
  System.out.println("by " + Configuration.name + " (email: " + Configuration.email + ")");
  System.out.println("");
  System.out.println("System Configuration File mode:");
  if(systemconfig_file == null)
	systemconfig_file = Configuration.systemconfig_file_default_full_path;  
  cfg = new Cfg(systemconfig_file,system_debug,cpu_debug,devices_debug);
  cfg.main();
 }

 // -----------------------------------
 // methods used to display information
 // -----------------------------------
 
 /**
  * Displays the version and configuration of the development tools in the terminal. 
  */
private static void version()
 {
  System.out.println(Configuration.program_name + " " + Configuration.version + " (" + Configuration.command + ") [" + Configuration.date + "]");
  System.out.println("by " + Configuration.name + " (email: " + Configuration.email + ")");
  System.out.println("");
  System.out.println("Instalation folders:");
  System.out.println("");
  System.out.println(" Program             : " + Configuration.root_folder);//.substring(0,Configuration.root_folder.lastIndexOf(Configuration.program_folder)));
  System.out.println(" Configuration       : " + Configuration.root_folder + Configuration.configuration);
  System.out.println(" OPB Devices         : " + Configuration.root_folder + Configuration.devices);
  System.out.println(" Processors          : " + Configuration.root_folder + Configuration.processors);
  System.out.println("");
  System.out.println("Default files:");
  System.out.println("");
  System.out.println(" <systemconfig_file> : " + Configuration.systemconfig_file_default);
  System.out.println(" <profile_file>      : " + Configuration.profile_file_default);
  System.out.println(" <trace_file>        : " + Configuration.trace_file_default);
  System.out.println("");
  System.out.println("<systemconfig_file> file path:");
  System.out.println("");
  System.out.println(" Folder              : " + Configuration.root_folder + Configuration.configuration);
  System.out.println("");
  System.out.println("Default values:");
  System.out.println("");
  System.out.println(" <port_number>       : " + Configuration.port_number_default);
  System.out.println("");
  System.exit(0); 
 }

 /**
  * Displays the development tools operation manual in the terminal.
  */
 private static void help()
 {
  System.out.println(Configuration.program_name + " " + Configuration.version + " (" + Configuration.command + ") [" + Configuration.date + "]");
  System.out.println("by " + Configuration.name + " (email: " + Configuration.email + ")");System.out.println("");
  System.out.println("Test mode:");
  System.out.println("usage: " + Configuration.command + " -tst [OPTION]... <binary_file>");
  System.out.println("[OPTION]...");
  System.out.println("  --syscf <systemconfig_file>    ,XML file with the system configuration");
  System.out.println("  --prff <profile_file>          ,output file with the simulation profile");
  System.out.println("  --trcf <trace_file>            ,output file with the simulation trace");
  System.out.println("  --eventv                       ,verbose the exceptions and interruptions");
  System.out.println("<binary_file>                    ,ELF binary file for FireWorks CPU");
  System.out.println("");
  System.out.println("");
  System.out.println("Simulator mode:");
  System.out.println("usage: " + Configuration.command + " -sim [OPTION]... <binary_file>");
  System.out.println("[OPTION]...");
  System.out.println("  --syscf <systemconfig_file>    ,XML file with the system configuration");
  System.out.println("  --eventv                       ,verbose the exceptions and interruptions");
  System.out.println("<binary_file>                    ,ELF binary file for FireWorks CPU");
  System.out.println("");
  System.out.println("");
  System.out.println("GDB Server mode:");
  System.out.println("usage: " + Configuration.command + " -gdb [OPTION]...");
  System.out.println("[OPTION]...");
  System.out.println("  --syscf <systemconfig_file>    ,XML file with the system configuration");
  System.out.println("  --portc <port_number>          ,port number for the TCP/IP protocol");
  System.out.println("  --prtclv                       ,verbose the GDB remote serial protocol");
  System.out.println("  --eventv                       ,verbose the exceptions and interruptions");
  System.out.println("");
  System.out.println("");
  System.out.println("Profile mode:");
  System.out.println("usage: " + Configuration.command + " -prf [OPTION]... <binary_file>");
  System.out.println("[OPTION]...");
  System.out.println("  --syscf <systemconfig_file>    ,XML file with the system configuration");
  System.out.println("  --prff <profile_file>          ,output file with the simulation profile");
  System.out.println("");
  System.out.println("");
  System.out.println("Trace mode:");
  System.out.println("usage: " + Configuration.command + " -trc [OPTION]... <binary_file>");
  System.out.println("[OPTION]...");
  System.out.println("  --syscf <systemconfig_file>    ,XML file with the system configuration");
  System.out.println("  --trcf <trace_file >           ,output file with the simulation trace");
  System.out.println("");
  System.out.println("");
  System.out.println("ELF binary file mode:");
  System.out.println("usage: " + Configuration.command + " -elf <binary_file>");
  System.out.println("<binary_file>                    ,ELF binary file for the system cpu");
  System.out.println("");
  System.out.println("");
  System.out.println("System configuration file mode:");
  System.out.println("usage: " + Configuration.command + " -cfg [OPTION]... <systemconfig_file>");
  System.out.println("  or:  " + Configuration.command + " -cfg [OPTION]...");
  System.out.println("[OPTION]...");
  System.out.println("  --sysv                         ,show system configuration");
  System.out.println("  --cpuv                         ,show cpu configuration");
  System.out.println("  --devv                         ,show OPB Devices configuration");
  System.out.println("<systemconfig_file>              ,XML file with the system configuration");
  System.out.println("");
  System.out.println("");
  System.out.println("Others options:");
  System.out.println("  --version                      ,output version information and defaults");
  System.out.println("  --help                         ,display this help");
  System.out.println("");
  System.exit(0);	 
 }
}