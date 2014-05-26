package tools;

import java.io.*;

import system.memory.*;
import system.*;

/**
 * The instruction trace mode class. This class implements the instruction trace mode. 
 */
public class Trc 
{
 /** The system. */
 private SysteM system;
 /** The system XML configuration file (path and name). */
 private String systemconfig_file;
 /** The instruction trace output file (path and name). */
 private String trace_file;
 /** The elf binary file (path and name). */
 private String binary_file;
 /** The simulation time. The time spent by the computer to perform the simulation. */
 private float time_simulation;
 /** The begin time. When starts counting the simulation time. */
 private long  time_begin;
 /** The real time. The time spent by the real system to execute the software. */
 private float time_real;
 /** The end time. When stops counting the simulation time. */
 private long  time_end;
 /** The ratio value between the simulation time and the real time. */
 private float ratio;
 /** The cycles per instruction value of the simulation. */
 private float cpi;
 
 /**
  * Instantiates a new instruction trace mode.
  *
  * @param systemconfig_file the XML system configuration file (path and name).
  * @param trace_file the instruction trace file (path and name). Where the results of the instruction trace simulation will be saved.
  * @param binary_file the elf binary file (path and name).
  */
 public Trc(String systemconfig_file,String trace_file,String binary_file)
 {
  try
  {
   this.systemconfig_file = systemconfig_file; 
   this.trace_file = trace_file;
   this.binary_file = binary_file;
   system = new SysteM(systemconfig_file,binary_file,false,true);
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
 // method used to perform the trace path
 // -------------------------------------

 /**
  * This method loads and simulates the elf binary file. Saves in the output file the results of the instruction trace simulation and shows the simulation results. 
  */
 public final void main()
 {	  
  FileOutputStream file_output;
  PrintStream output;
  File file;

  file = new File(trace_file);
  try
  {
   file_output = new FileOutputStream(file);
   output = new PrintStream(file_output);
   output.println("Trace");
   output.println("");
   
   tab();
   time_begin = System.nanoTime();
   saveTrace(output);
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
 
   output.println("");
   output.println("");
   
   output.println("System configuration file");
   output.println("");
   output.println(" " + systemconfig_file);  
   
   output.println("");
   output.println("");
   
   output.println("Binary file");
   output.println("");
   output.println(" " + binary_file);
   
   output.println("");
   output.println("");
   
   output.println("Performance");
   output.println("");
   output.println(" " + "instructions: " + Util.toDecStringSpace(system.getCPUClass().getNumberOfInstructions(),12));
   output.println(" " + "cycles      : " + Util.toDecStringSpace(system.getNumberOfCycles(),12));
   output.println(" " + "cpi         : " + Util.toDecStringSpace(cpi,12));
   output.println(" " + "<frequency> : " + Util.toDecStringSpace((float)(system.getFrequency() / 1E6),12) + "MHz");
   output.println(" " + "simulation  : " + Util.toDecStringSpace(time_simulation,12) + "s");
   output.println(" " + "real        : " + Util.toDecStringSpace(time_real,12) + "s");
   output.println(" " + "ratio       : " + Util.toDecStringSpace(ratio,12));
   
   output.println("");
   output.println("");
  
   file_output.close();
   
   tab();
   System.out.println(" " + "Trace saved in the <" + trace_file + "> file.");
   System.out.println("");
   System.exit(0);
  }
  catch(FileNotFoundException e)
  {
   tab();
   System.out.println(" " + "!Warning: error in <" + trace_file + "> file, " + e.getMessage());
   System.out.println("");
   System.exit(0);
  }
  catch(MemoryException e)
  {
   tab();
   System.out.println(" " + "!Warning: instruction not exist in memory " + e.getMessage());
   System.out.println("");
   System.exit(0);	  
  }
  catch(IOException e)
  {
   tab();
   System.out.println(" " + "!Warning: error in <" + trace_file + "> file, " + e.getMessage());
   System.out.println("");
   System.exit(0);	  
  } 
 }
 
 // ---------------------------------------------
 // method used to create and save the trace file
 // ---------------------------------------------
 
 /**
  * Saves the executed the disassembled instruction in the output file.
  *
  * @param output the output file stream.
  * @throws MemoryException if any error occurs when accessing to the memory system.  
  * @throws IOException if any error occurs when save to the output file.
  */
 private final void saveTrace(PrintStream output) throws MemoryException, IOException
 { 
  String instruction;
  int value;
  int pc;
  
  int sys_status;
   
  do
  {
   pc = system.getCPUClass().getStageInstruction(3).getPC();
   value = system.getMemoryClass().getMemoryWord(pc);
   instruction = system.getCPUClass().getInstructionSetArchitectureClass().toString(value);
   output.println("0x" + Util.toHexString(pc,8) + "  " + instruction);
   sys_status = system.step();
  }
  while(sys_status == Sys_Status.NORMAL); 
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
