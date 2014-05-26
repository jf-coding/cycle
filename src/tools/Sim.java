package tools;

import system.*;

/**
 * The simulator mode class. This class performs the simulator mode.
 */
public class Sim
{
 /** The system. */
 private SysteM system;
 
 /**
  * Instantiates a new simulator mode.
  *
  * @param config_file the XML system configuration file (path and name).
  * @param binary_file the elf binary file to be simulated (path and name).
  * @param events the cpu events flag.<p>
  * false - doesn't notify if any cpu events occur.<br>
  * true - notify if any cpu events occur.
  */
 public Sim(String config_file,String binary_file,boolean events)
 {
  try
  {
   system = new SysteM(config_file,binary_file,events,false);
  }
  catch(SysteMException e)
  {
   System.out.println("");
   System.out.println(" " + "!Warning: " + e.getMessage());
   System.out.println("");
   System.exit(0);
  }  
 }
 
 // -------------------------------------
 // method used to perform the simulation
 // -------------------------------------
 
 /**
  * This method loads and simulates the elf binary file. And displays the simulation results.
  */
 public final void main()
 {
  float time_simulation;
  long  time_begin;
  float time_real;
  long  time_end;
  float ratio;
  float cpi;
  
  tab();
  time_begin = System.nanoTime();
  system.continue_();
  time_end  = System.nanoTime();
  
  time_simulation = (time_end - time_begin) / 1E9f;
  time_real = system.getNumberOfCycles() / system.getFrequency();
  ratio = (time_simulation - time_real) / time_real;
  cpi = (float)system.getNumberOfCycles() / (float)system.getCPUClass().getNumberOfInstructions();
  
  tab();
  System.out.println("Results");
  System.out.println("");
  System.out.println(" " + "instructions: " + Util.toDecStringSpace(system.getCPUClass().getNumberOfInstructions(),12));
  System.out.println(" " + "cycles      : " + Util.toDecStringSpace(system.getNumberOfCycles(),12));
  System.out.println(" " + "cpi         : " + Util.toDecStringSpace(cpi,12));
  System.out.println(" " + "<frequency> : " + Util.toDecStringSpace((float)(system.getFrequency() / 1E6),12) + "MHz");
  System.out.println(" " + "simulation  : " + Util.toDecStringSpace(time_simulation,12) + "s");
  System.out.println(" " + "real        : " + Util.toDecStringSpace(time_real,12)      + "s");
  System.out.println(" " + "ratio       : " + Util.toDecStringSpace(ratio,12));
  System.out.println("");
  System.exit(0);
 }
 
 // -------------------
 // methods auxiliaries
 // -------------------
 
 /**
  * Displays one line on the screen. Is used to separate contents.
  */
 private final void tab()
 {
  System.out.println("");
  System.out.print("--------------------------------------------------------------------------------");
  System.out.println("");
 }
}
