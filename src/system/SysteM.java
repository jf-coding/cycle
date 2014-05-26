package system;

import java.util.*;

import system.opb_device.*;
import system.memory.*;
import system.cpu.*;

/**
 * The system class. This class implements the system.
 */
public class SysteM
{
 /** The array with the devices of the system. */
 private OPBDevice[] opb_device;
 /** The memory system. */
 private Memory memory;
 /** The system processor. */
 private CPU cpu;
 /** The list with the functions name. */
 private Map<Integer,String> function_name;
 /** The list with the first position memory address of the functions. */
 private Map<Integer,Long> function_begin;
 /** The list with the last position memory address of the functions. */
 private Map<Integer,Long> function_end;
 /** The list with the name of instructions processor. */
 private Map<String,Long> instruction;
 /** The report of the name and number of instructions executed in each function in the simulation. */
 private Map<Integer,Map<String,Long>> log_function_inst;
 /** The report of the executed functions in the simulation. */
 private Map<Integer,Long> log_function_execute;
 /** The report of the number of cycles performed by the functions in the simulation. */
 private Map<Integer,Long> log_function_cycle;
 /** The report of the functions call in the simulation. */
 private Map<Integer,Long> log_function_call;
 /** The number of pipeline stages. */
 private int pipeline_stages;
 /** The execute stage number. */
 private int execute_stage;
 /** The system frequency. */
 private float freq;
 /** The gcc id code of the processor. */
 private int gcc_id;
 /** The device number. */
 private int devices = 0;
 /** The number of cycles. */
 private long number_of_cycles = 0;
 /** The interrupt status. */
 private int interrupt;
 
 /**
  * Instantiates a new System.
  *
  * @param systemconfig_file the XML system configuration file (path and name).
  * @param binary_file the elf binary file (path and name).
  * @param debug the debug mode flag.<p> 
  * false - normal mode. The events of the processor aren't notified.<br>
  * true - debug mode. The events of the processor are notified.
  * @param function_exit the exit function flag.<p>
  * false - doesn't find the exit function.<br>
  * true - find the exit function.
  * @throws SysteMException if any error occur in the creation of the system.
  */
 public SysteM(String systemconfig_file,String binary_file,boolean debug,boolean function_exit) throws SysteMException
 {
  SystemConfiguration system_configuration;
  LoadProgram load_program;
 
  try
  {
   opb_device = new OPBDevice[0];
   system_configuration = new SystemConfiguration(debug);	  
   system_configuration.load(systemconfig_file,false,false,false);
   system_configuration.config(this);
   if(binary_file != null)
   {
	load_program = new LoadProgram();
	load_program.load(cpu,memory,binary_file,gcc_id,function_exit);
	function_name  = load_program.getFunction_name();
	function_begin = load_program.getFunction_begin();
	function_end   = load_program.getFunction_end();
   }
  }
  catch(LoadProgramException e)
  {
   throw new SysteMException(e.getMessage());  
  }
  catch(SystemConfigurationException e)
  {
   throw new SysteMException(e.getMessage());
  }
 }
 
 // ------------------------------------
 // methods used for auxiliary functions
 // ------------------------------------
 
 /**
  * Registers the device in the system (add the device to the system). 
  *
  * @param opb_device the device to be registered.
  */
 public final void register(OPBDevice opb_device)
 {
  OPBDevice[] device_aux;
	 
  device_aux = new OPBDevice[this.opb_device.length + 1];
  System.arraycopy(this.opb_device,0,device_aux,0,this.opb_device.length);
  device_aux[devices] = opb_device;
  this.opb_device = device_aux;
  devices++;
 }

 /**
  * Resets the all system.
  */
 public final void reset()
 {
  int aux;
	 
  number_of_cycles = 0;
  cpu.reset();
  memory.reset();
  for(aux = 0;aux < devices;aux++)
   opb_device[aux].reset();
 }

 // -------------------------------------
 // methods used for system configuration 
 // ------------------------------------- 

 /**
  * Sets the list with the name of instructions processor.
  *
  * @param instruction the list with the name of instructions processor.
  */
 public void setInstruction(Map<String,Long> instruction)
 {
  this.instruction = instruction;
 }
 
 /**
  * Sets the number of pipeline stages.
  *
  * @param pipeline_stages the number of pipeline stages.
  */
 public void setPipeline_stages(int pipeline_stages)
 {
  this.pipeline_stages = pipeline_stages;
 }
 
 /**
  * Sets the execute stage number.
  *
  * @param execute_stage the execute stage number.
  */
 public void setExecute_stage(int execute_stage)
 {
  this.execute_stage = execute_stage;
 }
 
 /**
  * Sets the memory system.
  *
  * @param memory the memory system. 
  */
 public void setMemory(Memory memory)
 {
  this.memory = memory;
 }

 /**
  * Sets the system frequency.
  *
  * @param freq the system frequency.
  */
 public void setFrequency(float freq)
 {
  this.freq = freq;
 }
 
 /**
  * Sets the gcc id code of the processor.
  *
  * @param gcc_id the gcc id code of the processor.
  */
 public void setGCC_id(int gcc_id)
 {
  this.gcc_id = gcc_id;
 }

 /**
  * Sets the system processor.
  *
  * @param cpu the system processor.
  */
 public void setCPU(CPU cpu)
 {
  this.cpu = cpu;
 }

 // -----------------------------------------------
 // methods used to access the system configuration 
 // -----------------------------------------------
 
 /**
  * Returns the number of pipeline stages.
  *
  * @return the number of pipeline stages.
  */
 public int getPipeline_stages()
 {
  return pipeline_stages;
 }
 
 /**
  * Returns the execute stage number.
  *
  * @return the execute stage number.
  */
 public int getExecute_stage()
 {
  return execute_stage;
 }
 
 /**
  * Returns the system frequency.
  *
  * @return the system frequency.
  */
 public float getFrequency()
 {
  return freq;
 }

 /**
  * Returns the gcc id code of the processor.
  *
  * @return the gcc id code of the processor.
  */
 public int getGCC_id()
 {
  return gcc_id;
 }
 
 // ------------------------------------------
 // methods used to access the simulation data 
 // ------------------------------------------
 
 /**
  * Returns the number of cycles performed.
  *
  * @return the number of cycles performed.
  */
 public final long getNumberOfCycles()
 {
  return number_of_cycles;
 }
 
 // --------------------------------------
 // methods used to control the simulation
 // --------------------------------------
  
 /**
  * Performs the simulation of an cycle.
  *
  * @return the system status.
  */
 public final int cycle()
 {
  int sys_status;
  int aux;
  
  number_of_cycles++;
  sys_status = cpu.cycle(interrupt);
  interrupt = 0;
  for(aux = 0;aux < devices;aux++)
	interrupt = interrupt + opb_device[aux].cycle();
  return sys_status;
 }

 /**
  * Performs the simulation until the cycle number given by the parameter cycle_number.
  *
  * @param cycle_number the cycle number where the simulation must stop.
  * @return the system status.
  */
 public final int toCycleNumber(long cycle_number)
 {
  int sys_status;
  int aux;
  
  do
  {
   number_of_cycles++;
   sys_status = cpu.cycle(interrupt);
   interrupt = 0;
   for(aux = 0;aux < devices;aux++)
	 interrupt = interrupt + opb_device[aux].cycle();
  }
  while(number_of_cycles < cycle_number);
  return sys_status;
 }
 
 /**
  * Performs the simulation until the instruction number given by the parameter instruction_number.
  *
  * @param instruction_number the cycle number where the simulation must stop.
  * @return the system status.
  */
 public final int toInstructionNumber(long instruction_number)
 {
  int sys_status;
  int aux;
  
  do
  {
   number_of_cycles++;
   sys_status = cpu.cycle(interrupt);
   interrupt = 0;
   for(aux = 0;aux < devices;aux++)
	 interrupt = interrupt + opb_device[aux].cycle();
  }
  while(cpu.getNumberOfInstructions() < instruction_number);
  return sys_status;
 }
 
 /**
  * Performs the simulation of an instruction.
  *
  * @return the system status.
  */
 public final int step()
 {
  long number_instructions_aux;
  long number_instructions;
  int sys_status;
  int aux;
  
  number_instructions = cpu.getNumberOfInstructions();
  do
  {
   number_of_cycles++;
   sys_status = cpu.cycle(interrupt);
   interrupt = 0;
   for(aux = 0;aux < devices;aux++)
	 interrupt = interrupt + opb_device[aux].cycle();
   number_instructions_aux = cpu.getNumberOfInstructions();
  }
  while(number_instructions == number_instructions_aux || cpu.getStageInstruction(execute_stage).toString().equals(""));
  return sys_status;	 
 }
 
 /**
  * Performs the simulation until the memory address given by the parameter address.
  *
  * @param address the memory address where the simulation must stop.
  * @return the system status.
  */
 public final int toMemoryAddress(int address)
 {
  int sys_status;
  int aux;
  int pc;
	  
  for(;;)
  {
   number_of_cycles++;
   sys_status = cpu.cycle(interrupt);
   interrupt = 0;
   for(aux = 0;aux < devices;aux++)
	 interrupt = interrupt + opb_device[aux].cycle();
   pc = cpu.getStageInstruction(execute_stage).getPC();
   if(sys_status > Sys_Status.NORMAL || pc == address)
     return sys_status;
  }	 
 }
 
 /**
  * Performs the simulation.
  *
  * @return the system status.
  */
 public final int continue_()
 {
  int sys_status;
  int aux;
	  
  do
  {
   number_of_cycles++;
   sys_status = cpu.cycle(interrupt);
   interrupt = 0;
   for(aux = 0;aux < devices;aux++)
	 interrupt = interrupt + opb_device[aux].cycle();
  }
  while(sys_status == Sys_Status.NORMAL);
  return sys_status;
 }
 
 /**
  * Performs the profile simulation.
  *
  * @return the system status.
  */
 public final int profile()
 {
  Map<String,Long> function_inst;
  long instructions_aux = 0;
  String instruction_name;
  long instructions = 0;
  long cycles_aux = 0;
  int interrupt = 0;	 
  int sys_status; 
  int function;
  long value;
  long pc;
  int aux;

  log_function_inst = new HashMap<Integer,Map<String,Long>>();
  for(aux = 0;aux < function_name.size();aux++)
    log_function_inst.put(aux,new HashMap<String,Long>(instruction));
  log_function_execute = new HashMap<Integer,Long>();
  for(aux = 0;aux < function_name.size();aux++)
    log_function_execute.put(aux,0L);
  log_function_cycle = new HashMap<Integer,Long>(log_function_execute);
  log_function_call = new HashMap<Integer,Long>(log_function_execute);
  
  function_inst = log_function_inst.get(0);
  value = log_function_execute.get(0) + 1;
  log_function_execute.put(0,value);
  value = log_function_call.get(0) + 1;
  log_function_call.put(0,value);
  function = 0;
   
  do
  {
   number_of_cycles++;
   cycles_aux++;
   sys_status = cpu.cycle(interrupt);
   interrupt = 0;
   for(aux = 0;aux < devices;aux++)
	 opb_device[aux].cycle();
   instruction_name = cpu.getStageInstruction(execute_stage).toString();
   instructions_aux = cpu.getNumberOfInstructions();
   if(sys_status > Sys_Status.NORMAL)
   {
	value = log_function_cycle.get(function) + cycles_aux;
    log_function_cycle.put(function,value);
    cycles_aux = 0;
    value = function_inst.get(instruction_name) + 1;
    function_inst.put(instruction_name,value);
    return sys_status;
   }
  }
  while(instruction_name.equals(""));  

  for(;;)
  {
   value = function_inst.get(instruction_name) + 1;
   function_inst.put(instruction_name,value);
	   
   do
   {
	number_of_cycles++;
    cycles_aux++;
    sys_status = cpu.cycle(interrupt);
    interrupt = 0;
    for(aux = 0;aux < devices;aux++)
      interrupt = interrupt + opb_device[aux].cycle();
    instruction_name = cpu.getStageInstruction(execute_stage).toString();
    instructions_aux = cpu.getNumberOfInstructions();
    if(sys_status > Sys_Status.NORMAL)
    {
     value = log_function_cycle.get(function) + cycles_aux;
     log_function_cycle.put(function,value);
     cycles_aux = 0;
     return sys_status;
    }
   }
   while(instructions == instructions_aux || instruction_name.equals("") || instruction_name.equals("mapped") || instruction_name.equals("illegal"));
   
   pc = (long)cpu.getStageInstruction(execute_stage).getPC() & 0xffffffffL;
   instructions = instructions_aux;

   if(!(pc >= function_begin.get(function) && pc <= function_end.get(function)))
   {
    for(aux = 0;aux < function_name.size();aux++)
    {  
	 if(pc == function_begin.get(aux))
	 {
	  function_inst = log_function_inst.get(aux);
	  value = log_function_execute.get(aux) + 1;
	  log_function_execute.put(aux,value);
	  value = log_function_cycle.get(function) + cycles_aux;
      log_function_cycle.put(function,value);
      cycles_aux = 0;
      value = log_function_call.get(aux) + 1;
      log_function_call.put(aux,value);
      function = aux;
      break;
	 }
	 else if(pc >= function_begin.get(aux) && pc <= function_end.get(aux))
	 {
	  function_inst = log_function_inst.get(aux);
	  value = log_function_execute.get(aux) + 1;
	  log_function_execute.put(aux,value);
	  value = log_function_cycle.get(function) + cycles_aux;
      log_function_cycle.put(function,value);
      cycles_aux = 0;
	  function = aux;
	  break;
	 }
	}
   }
  }	 
 }
 
 // ------------------------------------
 // methods used to access other objects
 // ------------------------------------
 
 /**
  * Returns the memory system.
  *
  * @return the memory system.
  */
 public final Memory getMemoryClass()
 {
  return memory;	 
 }
  
 /**
  * Returns the system processor.
  *
  * @return the system processor.
  */
 public final CPU getCPUClass()
 {
  return cpu;	 
 }
 
 // -----------------------------------
 // methods used to display information
 // -----------------------------------
 
 /**
  * Returns the list with the name of instructions processor.
  *
  * @return the list with the name of instructions processor.
  */
 public final Map<String,Long> getCPU_instruction()
 {
  return instruction;
 }

 /**
  * Returns the list of the with the functions name.
  *
  * @return the list of the with the functions name.
  */
 public final Map<Integer,String> getFunction_name()
 {
  return function_name;	 
 }
 
 /**
  * Returns the list with the first position memory address of the functions.
  *
  * @return the list with the first position memory address of the functions.
  */
 public final Map<Integer,Long> getFunction_begin()
 {
  return function_begin;
 }
 
 /**
  * Returns the list with the last position memory address of the functions.
  *
  * @return the list with the last position memory address of the functions.
  */
 public final Map<Integer,Long> getFunction_end()
 {
  return function_end;
 }
 
 /**
  * Returns the report of the name and number of instructions executed in each function in the simulation.
  *
  * @return the report of the name and number of instructions executed in each function in the simulation.
  */
 public final Map<Integer,Map<String,Long>> getLog_func_inst()
 {
  return log_function_inst;	 
 }
 
 /**
  * Returns the report of the executed functions in the simulation.
  *
  * @return the report of the executed functions in the simulation.
  */
 public final Map<Integer,Long> getLog_function_execute()
 {
  return log_function_execute;	 
 }
 
 /**
  * Returns the report of the number of cycles performed by the functions in the simulation.
  *
  * @return the report of the number of cycles performed by the functions in the simulation.
  */
 public final Map<Integer,Long> getLog_function_cycle()
 {
  return log_function_cycle;	 
 }
 
 /**
  * Returns the report of the functions call in the simulation.
  *
  * @return the report of the functions call in the simulation.
  */
 public final Map<Integer,Long> getLog_function_call()
 {
  return log_function_call;	 
 }
}
