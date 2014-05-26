package system;

import java.lang.reflect.*;
import java.util.*;

import system.memory.*;
import system.file.*;
import system.cpu.*;

/**
 * The system configuration class. This class performs all the validation and configuration of the system.
 */
public class SystemConfiguration 
{
 /** The system configuration file parser. */
 private SystemConfigParser system_config_parser;
 /** The devices debug flag.<p> 
  *  false - don't show the devices configuration.<br>
  *  true - show the devices configuration.
  */
 private boolean devices_debug;
 /** The cpu debug flag.<p> 
  *  false - doesn't show the cpu configuration.<br> 
  *  true - show the cpu configuration.
  */
 private boolean cpu_debug;
 
 // Device
 /** The list with the memory address of the registers devices. */
 private Vector<Vector<Long>> device_registers_address;
 /** The list with the name of the registers devices. */
 private Vector<Vector<String>> device_registers_name;
 /** The list with the devices. */
 private Vector<String> device_class;
 
 // CPU
 /** The list with the conditional latency of the instructions. */
 private Map<String,Integer> inst_cond_latency;
 /** The list with the latency of the instructions. */
 private Map<String,Integer> inst_latency;
 /** The list with the name of instructions processor. */
 private Map<String,Long> instruction;
 /** The system processor. */
 private String cpu_class;
 /** The The XML system configuration file (path and name). */
 private String systemconfig_file;
 /** The number of pipeline stages. */
 private int pipeline_stages;
 /** The execute stage number. */
 private int execute_stage;
 /** The system frequency. */
 private float freq;
 /** The gcc id code of the processor. */
 private int gcc_id;
 /** The debug mode flag.<p> 
  *  false - normal mode. The events of the processor aren't notified.<br>
  *  true - debug mode. The events of the processor are notified.
  */
 private boolean debug;

 /**
  * Instantiates a new System Configuration.
  *
  * @param debug the debug mode flag.<p> 
  * false - normal mode. The events of the processor aren't notified.<br>
  * true - debug mode. The events of the processor are notified.
  */
 public SystemConfiguration(boolean debug)
 {
  this.debug = debug;
 }
 
 // ----------------------------------------------------------
 // methods used to load and verify system configuration files
 // ----------------------------------------------------------
 
 /**
  * Loads the XML system configuration files.
  *
  * @param systemconfig_file the XML system configuration file (path and name).
  * @param system_debug The system debug flag.<p> 
  * false - don't show the devices configuration.<br>
  * true - show the devices configuration.
  * @param cpu_debug  The cpu debug flag.<p> 
  * false - doesn't show the cpu configuration.<br> 
  * true - show the cpu configuration.
  * @param devices_debug  The devices debug flag.<p> 
  * false - doesn't show the system configuration.<br>
  * true - show the system configuration.
  * @throws SystemConfigurationException the SystemConfigurationException
  */
 public void load(String systemconfig_file,boolean system_debug,boolean cpu_debug,boolean devices_debug) throws SystemConfigurationException
 {
  try
  {
   this.systemconfig_file = systemconfig_file;
   this.devices_debug = devices_debug;
   this.cpu_debug = cpu_debug;
   
   system_config_parser = new SystemConfigParser();
   system_config_parser.load(systemconfig_file);
   verify();
   if(system_debug == true)
   {
	system_config_parser.show();
    tab();
   }
  }
  catch(SystemConfigParserException e)
  {
   if(system_debug == true)
   {
	system_config_parser.show();
    tab();
   }
   throw new SystemConfigurationException(e.getMessage());
  } 
 }
 
 /**
  * Verifies if the data collects from the XML system configuration files is correct.
  *
  * @throws SystemConfigurationException if any error occur with the data collects from the XML system configuration files.
  */
 private void verify() throws SystemConfigurationException
 {
  DeviceConfigParser device_config_parser;
  Iterator<String> instructions_name;
  CPUConfigParser cpu_config_parser;
  Map<String,Integer> instructions;

  boolean register_found = false;

  Vector<String> registers; 
  String name;
  int aux_a;
  int aux_b;
  int aux_c;
	 
  // verify the OPB Devices
  device_registers_address = system_config_parser.getDevice_registers_address();
  device_registers_name = system_config_parser.getDevice_registers_name();
  device_class = system_config_parser.getDevice_class();
  if(device_class != null)
  {  
   for(aux_a = 0;aux_a < device_class.size();aux_a++)
   {
	device_config_parser = new DeviceConfigParser();
 	try
 	{
     device_config_parser.load(Configuration.root_folder + Configuration.devices + "/" + device_class.get(aux_a) + "/" + device_class.get(aux_a) + ".xml");
     if(devices_debug == true)
     {
      device_config_parser.show();
      tab();
     }
     if(device_registers_address.get(aux_a).size() != device_config_parser.getNumber_registers())
       throw new SystemConfigurationException(device_class.get(aux_a) + ", OPB Device have two diferents numbers of registers assigned.");
     registers = device_config_parser.getReg_class_names();
     for(aux_b = 0;aux_b < device_registers_name.get(aux_a).size();aux_b++)
     {
      for(aux_c = 0;aux_c < device_registers_name.get(aux_a).size();aux_c++) 
        if(device_registers_name.get(aux_a).get(aux_b).equals(registers.get(aux_c)))
          register_found = true;
      if(register_found == false)
    	throw new SystemConfigurationException("the register " + device_registers_name.get(aux_a).get(aux_b) + " from " + device_class.get(aux_a) + " " + aux_a + ", OPB Device does not make part of the device.");
      register_found = false;
     }
    }
    catch(DeviceConfigParserException e)
    {
     if(devices_debug == true)
     {
      device_config_parser.show();
      tab();
     }
     throw new SystemConfigurationException(e.getMessage());
    } 
    catch(SystemConfigurationException e)
    {
     if(devices_debug == true)
     {
      device_config_parser.show();
      tab();
     }
     throw new SystemConfigurationException(e.getMessage());
    } 
    catch(Exception e)
    {
     throw new SystemConfigurationException(e.getMessage() + " OPB device type " + device_class.get(aux_a) + " not exist or cause errors");
    }
   }
  }
  
  // verify the CPU
  inst_cond_latency = system_config_parser.getCPU_inst_cond_latency();
  inst_latency = system_config_parser.getCPU_inst_latency();
  instruction = system_config_parser.getCPU_instruction();
  cpu_class = system_config_parser.getCPU_class();
  cpu_config_parser = new CPUConfigParser();
  try
  {
   cpu_config_parser.load(Configuration.root_folder + Configuration.processors + "/" + cpu_class + "/" + cpu_class + ".xml");
   
   if(cpu_debug == true)
   {
	cpu_config_parser.show();
    tab();
   }
   
   pipeline_stages = cpu_config_parser.getCPU_pipeline_stages();
   execute_stage = cpu_config_parser.getCPU_execute_stage();
   gcc_id = cpu_config_parser.getCPU_gcc_id();
   freq = system_config_parser.getCPU_freq();
   
   if(inst_latency.size() != cpu_config_parser.getNumber_instructions())
     throw new SystemConfigurationException(cpu_class + ", CPU have two diferents numbers of instructions assigned.");
  
   instructions = cpu_config_parser.getInst_names();
   instructions_name = inst_latency.keySet().iterator();
   for(aux_a = 0;aux_a < inst_latency.size();aux_a++)
   {	   
    name = instructions_name.next();
    if(!instructions.containsKey(name))
      throw new SystemConfigurationException("the instruction " + name + " from " + cpu_class + ", CPU does not make part of the processor.");
    
    if(inst_cond_latency.get(name) >= 1 && instructions.get(name) == 0)
    	throw new SystemConfigurationException("the instruction " + name + " from " + cpu_class + ", is not a conditional branch");
    if(inst_cond_latency.get(name) < 1 && instructions.get(name) == 1)
    	throw new SystemConfigurationException("the instruction " + name + " from " + cpu_class + ", is a conditional branch, the number of conditional branch latency must are higher than 0.");
   }
   
   instruction.put("empty",0L);
   instruction.put("illegal",0L);
   instruction.put("mapped",0L);
   instruction.put("breakpoint",0L);
   instruction.put("stop",0L);
  }
  catch(CPUConfigParserException e)
  {
   if(cpu_debug == true)
   {
	cpu_config_parser.show();
    tab();
   }
   throw new SystemConfigurationException(e.getMessage());
  }
  catch(SystemConfigurationException e)
  {
   if(cpu_debug == true)
   {
	cpu_config_parser.show();
    tab();
   }
   throw new SystemConfigurationException(e.getMessage());
  } 
  catch(Exception e)
  {
   throw new SystemConfigurationException(cpu_class + ", CPU not exist or cause errors");	  
  }
 }
 
 // ------------------------------------------------
 // method used for configuration and system startup
 // ------------------------------------------------
 
 /**
  * Configures the all system with the data collects from the XML system configuration files.
  *
  * @param system the system.
  * @throws SystemConfigurationException if any error occur when configure the system. 
  */
 public void config(SysteM system) throws SystemConfigurationException
 {
  Constructor<?> constructor;
  Class<?> class_name;
  Memory memory;
  CPU cpu;
  int aux;
  
  try
  {
   System.out.println("");
   System.out.println("  System");
   System.out.println("   . File: " + systemconfig_file);	  
	  
   memory = new Memory();
   
   memory.setLMB_range(system_config_parser.getLMB_begin(),system_config_parser.getLMB_end());
   memory.setLMB_latency(system_config_parser.getLMB_read(),system_config_parser.getLMB_write());
   memory.setMapped_latency(system_config_parser.getMapped());
   if(system_config_parser.getOPB() == true)
   {
	memory.setOPB(true);
	memory.setOPB_range(system_config_parser.getOPB_begin(),system_config_parser.getOPB_end());
	memory.setOPB_latency(system_config_parser.getOPB_read(),system_config_parser.getOPB_write());
   }
      
   memory.show();
   
   system.setMemory(memory);
   system.setPipeline_stages(pipeline_stages);
   system.setExecute_stage(execute_stage);
   system.setFrequency(freq);
   system.setGCC_id(gcc_id);
   
   class_name = Class.forName(Configuration.processors + "." + cpu_class + "." + cpu_class);
   constructor = class_name.getConstructor(new Class[] {Memory.class,Map.class,Map.class,boolean.class});
   cpu = (CPU)constructor.newInstance(new Object[] {memory,inst_latency,inst_cond_latency,debug});
   system.setCPU(cpu);
   
   memory.setInstructionSetArchitecture(cpu.getInstructionSetArchitectureClass());
     
   if(device_class.size() != 0)
   {
	System.out.println("");
	System.out.println("  Devices");
	for(aux = 0;aux < device_class.size();aux++)
    {
     class_name = Class.forName(Configuration.devices + "." + device_class.get(aux) + "." + device_class.get(aux));
     constructor = class_name.getConstructor(new Class[] {SysteM.class,Memory.class,Vector.class,Vector.class,int.class,int.class,int.class,boolean.class});
     constructor.newInstance(new Object[] {system,memory,system_config_parser.getDevice_registers_address().get(aux),system_config_parser.getDevice_registers_name().get(aux),system_config_parser.getDevice_read_latency().get(aux),system_config_parser.getDevice_write_latency().get(aux),aux,debug});
    }
   }
   
   system.setInstruction(instruction);
   
   System.out.println("");
   System.out.println("  CPU");
   System.out.println("   . Name           : " + cpu.toString());
   System.out.println("   . Pipeline stages: " + pipeline_stages);
   System.out.println("   . GCC id         : 0x" + Integer.toHexString(system.getGCC_id()));
   System.out.println("   . Frequency      : " + (freq / 1E6) + "MHz");
  }
  catch(InvocationTargetException e)
  {
   throw new SystemConfigurationException(e.getCause().getMessage());
  }
  catch(Exception e)
  {  
   throw new SystemConfigurationException(e.getMessage());
  }
 }
   
 // -------------------
 // methods auxiliaries
 // -------------------
 
 /**
  * Displays an line on the screen. Is used to separate contents.
  */
 private final void tab()
 {
  System.out.println("");
  System.out.print("--------------------------------------------------------------------------------");
  System.out.println("");
 }
}
