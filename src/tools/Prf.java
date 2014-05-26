package tools;

import java.text.*;
import java.util.*;
import java.io.*;

import system.*;

/**
 * The profiler (performance analysis) mode class. This class performs the profiler (performance analysis) mode.
 */
public class Prf 
{
 /** The system. */
 private SysteM system;
 /** The system XML configuration file (path and name). */
 private String systemconfig_file;
 /** The profiler (performance analysis) output file (path and name). */
 private String profile_file;
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
  * Instantiates a new profiler (performance analysis) mode.
  *
  * @param systemconfig_file the XML system configuration file (path and name).
  * @param profile_file the profiler (performance analysis) file (path and name). Where the profile results of the simulation will be saved.
  * @param binary_file the elf binary file to be simulated (path and name).
  */
 public Prf(String systemconfig_file,String profile_file,String binary_file)
 {
  try
  {
   this.systemconfig_file = systemconfig_file; 
   this.profile_file = profile_file;
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
 
 // -----------------------------------
 // method used to generate the profile
 // -----------------------------------

 /**
  * This method loads and simulates the elf binary file. Collects the profile results of the simulation and shows the simulation results.
  */
 public final void main()
 {	  
  tab();
  time_begin = System.nanoTime();
  system.profile();
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
  
  tab();
  try
  {
   saveProfile();
   System.out.println(" " + "Profile saved in the <" + profile_file + "> file.");
   System.out.println("");
   System.exit(0);
  }
  catch(Exception e)
  {
   System.out.println(" " + "!Warning: error in <" + profile_file + "> file, " + e.getMessage());
   System.out.println("");
   System.exit(0);
  }
 }
 
 // -----------------------------------------------
 // method used to create and save the profile file
 // -----------------------------------------------
 
 /**
  * This method saves the profile results of the simulation in the output file.
  *
  * @throws Exception if any error occurs when save to the output file.
  */
 private final void saveProfile() throws Exception
 {
  FileOutputStream file_output;
  PrintStream output;
  File file;
  
  Map<Integer,Map<String,Long>> log_function_inst;
  Map<Integer,Long> log_function_execute;
  Map<Integer,Long> log_function_cycle;
  Map<Integer,Long> log_function_call;
  Map<Integer,String> function_name;
  
  long instructions;
  float frequency;
  long cycles;
  
  Vector<Integer> keys;
  Vector<Long> values;
  long swap_values;
  int swap_keys;
  boolean order;
  float time;
 
  long total_instructions;
  long total_executes;
  long total_cycles;
  long total_calls;
  long execute;
  long cycle;
  long call;
  
  Map<String,Long> function_inst_temp;
  Map<String,Long> function_inst;
  Iterator<String> name;
 
  DecimalFormat decimal;
  String result_a;
  String result_b;
   
  String name_aux[];
  String name_temp;
  
  long value; 
  int aux_a;
  int aux_b;
  
  
  try
  {
   file = new File(profile_file);
   file_output = new FileOutputStream(file);
   output = new PrintStream(file_output); 
   
   log_function_execute = system.getLog_function_execute();
   log_function_cycle = system.getLog_function_cycle();
   log_function_call = system.getLog_function_call();
   log_function_inst = system.getLog_func_inst();
   
   function_name = system.getFunction_name();
   frequency = system.getFrequency();
   
   instructions = system.getCPUClass().getNumberOfInstructions();
   cycles = system.getNumberOfCycles();
   
   
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
   output.println(" " + "instructions: " + Util.toDecStringSpace(instructions,12));
   output.println(" " + "cycles      : " + Util.toDecStringSpace(cycles,12));
   output.println(" " + "cpi         : " + Util.toDecStringSpace(cpi,12));
   output.println(" " + "<frequency> : " + Util.toDecStringSpace((float)(frequency / 1E6),12) + "MHz");
   output.println(" " + "simulation  : " + Util.toDecStringSpace(time_simulation,12) + "s");
   output.println(" " + "real        : " + Util.toDecStringSpace(time_real,12)      + "s");
   output.println(" " + "ratio       : " + Util.toDecStringSpace(ratio,12));
   
   
   output.println("");
   output.println("");
     
   // order the functions in descending order the number of cycles
   keys = new Vector<Integer>();
   values = new Vector<Long>();
   
   for(aux_a = 0;aux_a < log_function_cycle.size();aux_a++)
   {
    values.add(log_function_cycle.get(aux_a));	  
    keys.add(aux_a);
   }
   do
   {
    order = true;
    for(aux_a = 0;aux_a < values.size() - 1;aux_a++)
    {
 	 if(values.get(aux_a) < values.get(aux_a + 1))
  	 {
  	  order = false;
  	  swap_values = values.get(aux_a + 1);
  	  values.set(aux_a + 1,values.get(aux_a));
  	  values.set(aux_a,swap_values);
  	  swap_keys = keys.get(aux_a + 1);
  	  keys.set(aux_a + 1,keys.get(aux_a));
 	  keys.set(aux_a,swap_keys);
  	 }
    }
   }
   while(order == false);
   
   output.println("Flat profile");
   output.println("");
   output.println("    %       cumulative          self                  self       ");
   output.println("   time       cycles           cycles    calls      cycles/call  function");
   
   decimal = new DecimalFormat("0.00");
   total_cycles = 0;
   for(aux_a = 0;aux_a < keys.size();aux_a++)
   {
    time = (float)log_function_cycle.get(keys.get(aux_a)) / (float)cycles * 100;
    result_a = decimal.format(time);
    total_cycles = total_cycles + log_function_cycle.get(keys.get(aux_a));
    if(log_function_call.get(keys.get(aux_a)) != 0)
    {
 	 time = (float)log_function_cycle.get(keys.get(aux_a)) / (float)log_function_call.get(keys.get(aux_a));
     result_b = decimal.format(time);
     output.println(" " + Util.toStringSpace(result_a,6) + " " + Util.toDecStringSpace(total_cycles,14) + " " + Util.toDecStringSpace(log_function_cycle.get(keys.get(aux_a)),14) + " " + Util.toDecStringSpace(log_function_call.get(keys.get(aux_a)),8) + " " + Util.toStringSpace(result_b,16) + "  " + function_name.get(keys.get(aux_a)));	  
    }
    else
      output.println(" " + Util.toStringSpace(result_a,6) + " " + Util.toDecStringSpace(total_cycles,14) + Util.toStringSpace(" ",42) + " " + function_name.get(keys.get(aux_a)));   
   }
   
    
   output.println("");
   output.println("");
   
   
   instructions = 0;
   total_instructions = 0;
   total_executes = 0;
   total_cycles = 0;
   total_calls = 0;
   output.println("General profile ");
   output.println("");
   output.println(" " + Util.toStringSpace("cycles",12) + "  " + Util.toStringSpace("instructions",12) + "  " + Util.toStringSpace("execute",7) + "  " + Util.toStringSpace("calls",5) + "  " +  Util.toStringSpace("function",8));
   for(aux_a = 0;aux_a < function_name.size();aux_a++)
   {
    call = log_function_call.get(aux_a);
    total_calls = total_calls + call;
    
    cycle = log_function_cycle.get(aux_a);
    total_cycles = total_cycles + cycle;
    
    execute = log_function_execute.get(aux_a);
    total_executes = total_executes + execute;
    
    function_inst = log_function_inst.get(aux_a);
    name = function_inst.keySet().iterator();
    for(aux_b = 0;aux_b < function_inst.size();aux_b++)
 	 instructions = function_inst.get(name.next()) + instructions;
    output.println(" " + Util.toDecStringSpace(cycle,12) + "  " + Util.toDecStringSpace(instructions,12) + "  " + Util.toDecStringSpace(execute,7) + "  " + Util.toDecStringSpace(call,5) + "  " + function_name.get(aux_a));
    total_instructions = total_instructions + instructions;
    instructions = 0;
   }
   output.println(" " + Util.toDecStringSpace(total_cycles,12) + "  " + Util.toDecStringSpace(total_instructions,12) + "  " + Util.toDecStringSpace(total_executes,7) + "  " + Util.toDecStringSpace(total_calls,5));
    
   
   output.println("");
   output.println("");
   
   // sum total of each instruction, starting from subtotals of each function
   function_inst_temp = system.getCPU_instruction();
   function_inst = new HashMap<String,Long>(function_inst_temp);
   for(aux_a = 0;aux_a < function_name.size();aux_a++)
   {
	function_inst_temp = log_function_inst.get(aux_a);
    name = function_inst_temp.keySet().iterator();
    for(aux_b = 0;aux_b < function_inst_temp.size();aux_b++)
    {
 	 name_temp = name.next();
 	 value = function_inst.get(name_temp) + function_inst_temp.get(name_temp);
 	 function_inst.put(name_temp,value); 
    }
   }
   
   // sum total of all instructions
   total_instructions = 0;
   name = function_inst.keySet().iterator();
   for(aux_a = 0;aux_a < function_inst.size();aux_a++)
   {
    name_temp = name.next();
    total_instructions = total_instructions + function_inst.get(name_temp);	  
   }
   name_aux = new String[function_inst.keySet().size()];
   function_inst.keySet().toArray(name_aux);
   java.util.Arrays.sort(name_aux);
   
   output.println("Instruction profile");
   output.println("");
   output.println(" " + Util.toStringSpace("instruction",12)+ "  " + Util.toStringSpace("#",12) + "  " + Util.toStringSpace("%",7));
   
   decimal = new DecimalFormat("0.000");
   for(aux_a = 0;aux_a < name_aux.length;aux_a++)
   {
	if(function_inst.get(name_aux[aux_a]) == 0)
	  continue;
	time = (float)function_inst.get(name_aux[aux_a]) / (float)total_instructions * 100;
	result_a = decimal.format(time);
    output.println(" " + Util.toStringSpace(name_aux[aux_a],12) + "  " + Util.toDecStringSpace(function_inst.get(name_aux[aux_a]),12) + "  " + Util.toStringSpace(result_a,7));
   }
   output.println(" " + Util.toDecStringSpace(total_instructions,26));
   
   
   output.println("");
   output.println("");
   
   
   output.println("Function profile"); 
   output.println("");
   for(aux_a = 0;aux_a < log_function_inst.size();aux_a++)
   {
	total_instructions = 0;
	function_inst_temp = log_function_inst.get(aux_a);
	
	if(log_function_cycle.get(aux_a) == 0)
      continue;
	output.println(" " + Util.toStringSpace("cycles",12) + "  " + Util.toStringSpace("instructions",12) + "  " + Util.toStringSpace("execute",7) + "  " + Util.toStringSpace("calls",5) + "  " +  Util.toStringSpace("function",8));	
    
	for(aux_b = 0;aux_b < function_inst_temp.size();aux_b++)
    {	  
     value = function_inst_temp.get(name_aux[aux_b]);
     total_instructions = total_instructions + value;
    }
	
	output.println(" " + Util.toDecStringSpace(log_function_cycle.get(aux_a),12) + "  " + Util.toDecStringSpace(total_instructions,12) + "  " + Util.toDecStringSpace(log_function_execute.get(aux_a),7) + "  " + Util.toDecStringSpace(log_function_call.get(aux_a),5) + "  " + function_name.get(aux_a));
    output.println("");
    output.println(" " + Util.toStringSpace("instruction",12)+ "  " + Util.toStringSpace("#",12) + "  " + Util.toStringSpace("%",7));
    
    name_aux = new String[function_inst_temp.keySet().size()];
    function_inst_temp.keySet().toArray(name_aux);
    java.util.Arrays.sort(name_aux);
        
    for(aux_b = 0;aux_b < function_inst_temp.size();aux_b++)
    {	  
     value = function_inst_temp.get(name_aux[aux_b]);
     if(value == 0)
       continue;
     time = (float)value / (float)total_instructions * 100;
 	 result_a = decimal.format(time);
     output.println(" " + Util.toStringSpace(name_aux[aux_b],12) + "  " + Util.toDecStringSpace(value,12) + "  " +Util.toStringSpace(result_a,7));
    }
    
    
    output.println("");
    output.println("");
   }
   file_output.close();	
  }
  catch(IOException e) 
  {
   throw new Exception(e.getMessage());
  }	 
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
