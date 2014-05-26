package system.file;

import java.text.DecimalFormat;
import java.util.*;
import java.io.*;

import org.xml.sax.helpers.*;
import org.xml.sax.*;

import system.file.xml.XMLReader;
import system.*;

/**
 * The system configuration parser class. This class performs the loading and validation of the XML system configurationfile.
 */
public class SystemConfigParser
{
 /** The system configuration parser handler. */
 private SystemConfigParserHandler handler;
 /** The XML system configuration file (path and name). */
 private String systemconfig_file;
 
 // description section 
 /** The version of the system . */
 private String version = "";
 /** The company's system name. */
 private String company = "";
 /** The author name of the system. */
 private String author = "";
 /** The system name. */
 private String name = "";
 /** The version date of the system. */
 private String date = "";
 /** The observation of the device module. */
 private String obs = "";
 
 // memory section
 /** The write latency value of the local memory bus. */
 private int lmb_write;
 /** The read latency value of the local memory bus. */
 private int lmb_read;
 /** The lowest memory address mapped in the local memory bus value. */
 private long lmb_begin;
 /** The highest memory address mapped in the local memory bus value. */
 private long lmb_end;
 /** The on-chip memory bus flag.<p>
  *  false - the on-chip peripheral bus doesn't have memory mapped.<br>
  *  true - the on-chip peripheral bus have memory mapped.
  */
 private boolean opb = false;
 /** The write latency value of the on-chip peripheral bus. */
 private int opb_write;
 /** The read latency value of the on-chip peripheral bus. */
 private int opb_read;
 /** The lowest memory address mapped in the on-chip peripheral bus value. */
 private long opb_begin;
 /** The highest memory address mapped in the on-chip peripheral bus value. */
 private long opb_end;
 /** The on-chip peripheral bus latency value in the case of an access to a memory address not mapped. */
 private int mapped;
 
 // device section
 /** The list with the memory address of the registers of all devices. */
 private Vector<Vector<Long>> device_registers_address;
 /** The list with the memory address of the registers of all devices. */
 private Vector<Vector<String>> device_registers_name;
 /** The list of the memory address of the registers of an device. */
 private Vector<Long> registers_address;
 /** The list with the registers name. */
 private Vector<String> registers_name;
 /** The list with the names of the classes of the devices. */
 private Vector<String> device_class;
 /** The list with the value of the read latency of the devices registers. */
 private Vector<Integer> device_read;
 /** The list with the value of the write latency of the devices registers. */
 private Vector<Integer> device_write;
 
 // cpu section
 /** The list with the number of cycles that each branch instruction spends to perform a conditional branch. */
 private Map<String,Integer> inst_cond_latency;
 /** The list with the number of cycles that each instruction spends to be executed in the execute stage. */
 private Map<String,Integer> inst_latency;
 /** The list of the with the instructions name. */
 private Map<String,Long> instruction;
 /** The number of instructions with conditional latency. */
 private int number_instructions_cond_latency = 0;
 /** The number of instructions with latency. */
 private int number_instructions_latency = 0;
 /** The cpu class name. */
 private String cpu_class;
 /** The system frequency. */
 private float freq = 50000000f;
 
 /**
  * Instantiates a new system config parser.
  */
 public SystemConfigParser()
 {
  handler = new SystemConfigParserHandler();
  device_class = new Vector<String>();
  device_registers_address = new Vector<Vector<Long>>();
  device_registers_name = new Vector<Vector<String>>();
  device_read = new Vector<Integer>();
  device_write = new Vector<Integer>();
  
  inst_cond_latency = new HashMap<String,Integer>();
  inst_latency = new HashMap<String,Integer>();
  instruction = new HashMap<String,Long>();
 }

 //-----------------------------------------
 // method used to load and verify xml files
 //-----------------------------------------
 
 /**
  * Loads the XML system configuration file (path and name).
  *
  * @param systemconfig_file the XML system configuration file (path and name).
  * @throws SystemConfigParserException if any error occur when loads the XML system configuration file.
  */
 public void load(String systemconfig_file) throws SystemConfigParserException
 {
  XMLReader inputXml;
  File file;
  
  this.systemconfig_file = systemconfig_file;
  try 
  {
   file = new File(systemconfig_file);
   inputXml = new XMLReader(file);
   inputXml.read(handler);
   verify();
  }
  catch(SystemConfigParserException e) 
  {
   throw new SystemConfigParserException(e.getMessage());
  }
  catch(IOException e)
  {
   throw new SystemConfigParserException(e.getMessage());
  }
  catch(SAXException e) 
  {
   throw new SystemConfigParserException(e.getMessage());
  }
 }
  
 /**
  * Verifies the validation of the content of the XML system configuration file.
  *
  * @throws SystemConfigParserException if any error occur when verifies the validation of the content of the XML system configuration file.
  */
 private void verify() throws SystemConfigParserException
 {
  Iterator<String> instructions_name;
  int number_devices;
  String name;
  int aux_a;
  int aux_b;
  int aux_c;
  int aux_d;
  
  // verify memory
  if(lmb_begin >= lmb_end)
	 throw new SystemConfigParserException("LMB memory have a wrong range.");	 
  if(opb == true)
  {
   if(opb_begin >= lmb_begin && opb_begin <= lmb_end)
	 throw new SystemConfigParserException("OPB memory in the range of the LMB memory range.");
   if(opb_end >= lmb_begin && opb_end <= lmb_end)
	 throw new SystemConfigParserException("OPB memory in the range of the LMB memory range.");   
  }
  
  // verify opb devices
  number_devices = device_class.size();
  if(number_devices != 0)
  {
   for(aux_a = 0;aux_a < number_devices;aux_a++)
     for(aux_b = 0;aux_b < device_registers_address.get(aux_a).size();aux_b++)
	   if(device_registers_address.get(aux_a).get(aux_b) >= lmb_begin &&  device_registers_address.get(aux_a).get(aux_b) <= lmb_end)
	     throw new SystemConfigParserException(device_class.get(aux_a) + " " + aux_a + ", OPB Device have " + device_registers_name.get(aux_a).get(aux_b) + " register in the range of LMB memory.");
   if(opb == true)
	 for(aux_a = 0;aux_a < number_devices;aux_a++)
	   for(aux_b = 0;aux_b < device_registers_address.get(aux_a).size();aux_b++)
	     if(device_registers_address.get(aux_a).get(aux_b) >= opb_begin && device_registers_address.get(aux_a).get(aux_b) <= opb_end)
	       throw new SystemConfigParserException(device_class.get(aux_a) + " " + aux_a + ", OPB Device have " + device_registers_name.get(aux_a).get(aux_b) + " register in the range of OPB memory.");
   for(aux_a = 0;aux_a < number_devices;aux_a++)
	 for(aux_b = 0;aux_b < device_registers_name.get(aux_a).size() - 1;aux_b++)
	   for(aux_c = aux_b + 1;aux_c < device_registers_name.get(aux_a).size();aux_c++)  
	     if(device_registers_name.get(aux_a).get(aux_b).equals(device_registers_name.get(aux_a).get(aux_c)))
	       throw new SystemConfigParserException(device_class.get(aux_a) + " " + aux_a + ", OPB Device have two or more registers with the name " + device_registers_name.get(aux_a).get(aux_b) + ".");   
   for(aux_a = 0;aux_a < number_devices;aux_a++)
	 for(aux_b = 0;aux_b < device_registers_address.get(aux_a).size() - 1;aux_b++)
	   for(aux_c = aux_b + 1;aux_c < device_registers_address.get(aux_a).size();aux_c++)  
	     if(device_registers_address.get(aux_a).get(aux_b).equals(device_registers_address.get(aux_a).get(aux_c)))
	       throw new SystemConfigParserException(device_class.get(aux_a) + " " + aux_a + ", OPB Device have two or more registers with the address 0x" + Util.toHexString(device_registers_address.get(aux_a).get(aux_b),8) + ".");	    	 
   for(aux_a = 0;aux_a < number_devices - 1;aux_a++)
	 for(aux_b = aux_a + 1;aux_b < number_devices;aux_b++)
	   for(aux_c = 0;aux_c < device_registers_address.get(aux_a).size();aux_c++)
		 for(aux_d = 0;aux_d < device_registers_address.get(aux_b).size();aux_d++)
		   if(device_registers_address.get(aux_a).get(aux_c).equals(device_registers_address.get(aux_b).get(aux_d)))
		     throw new SystemConfigParserException("the address 0x" + Util.toHexString(device_registers_address.get(aux_a).get(aux_c),8) + " is mapped in two or more OPB Devices.");    
   for(aux_a = 0;aux_a < number_devices;aux_a++)
	 for(aux_b = 0;aux_b < device_registers_name.get(aux_a).size();aux_b++)
	   if(device_class.get(aux_a).equals(device_registers_name.get(aux_a).get(aux_b)))
		 throw new SystemConfigParserException(device_class.get(aux_a) + " " + aux_a + ", OPB Device have one or more registers with the same name of the device.");
  }

  // verify cpu instructions
  if(inst_latency.containsKey(cpu_class))
    throw new SystemConfigParserException(cpu_class + ", CPU have one or more instructions with the same name of the CPU.");
  
  if(inst_cond_latency.containsKey(cpu_class))
	throw new SystemConfigParserException(cpu_class + ", CPU have one or more instructions with the same name of the CPU.");
  
  if(number_instructions_latency != number_instructions_cond_latency)
	throw new SystemConfigParserException(cpu_class + ", CPU have diferent number of instructions in the configuration of the latency and conditional branch latency."); 
   
  instructions_name = inst_latency.keySet().iterator();
  for(aux_a = 0;aux_a < inst_latency.size();aux_a++)
  {	   
   name = instructions_name.next();
   instruction.put(name,0L);
  }
 }
 
 //--------------------------------
 // methods used for accessing data
 //--------------------------------
 
 /**
  * Returns the system name.
  *
  * @return the system name.
  */
 public String getDescription_name()
 {
  return name; 
 }
 
 /**
  * Returns the author name of the system.
  *
  * @return the author name of the system.
  */
 public String getDescription_author()
 {
  return author; 
 }
 
 /**
  * Returns the company's system name.
  *
  * @return the company's system name.
  */
 public String getDescription_company()
 {
  return company; 
 }
 
 /**
  * Returns the version of the system.
  *
  * @return the version of the system.
  */
 public String getDescription_version()
 {
  return version; 
 }
 
 /**
  * Returns the version date of the system.
  *
  * @return the version date of the system.
  */
 public String getDescription_date()
 {
  return date; 
 }
 
 /**
  * Returns the observation of the system.
  *
  * @return the observation of the system.
  */
 public String getDescription_obs()
 {
  return obs; 
 }
 
 /**
  * Returns the on-chip memory bus flag.<p>
  * false - the on-chip peripheral bus doesn't have memory mapped.<br>
  * true - the on-chip peripheral bus have memory mapped.
  *
  * @return the on-chip memory bus flag.
  */
 public boolean getOPB()
 {
  return opb;
 }
 
 /**
  * Returns the read latency value of the local memory bus.
  *
  * @return the read latency value of the local memory bus.
  */
 public int getLMB_read()
 {
  return lmb_read; 
 }
 
 /**
  * Returns the write latency value of the local memory bus.
  *
  * @return the write latency value of the local memory bus.
  */
 public int getLMB_write()
 {
  return lmb_write; 
 }
 
 /**
  * Returns the lowest memory address mapped in the local memory bus value.
  *
  * @return the lowest memory address mapped in the local memory bus value.
  */
 public int getLMB_begin()
 {
  return (int)lmb_begin; 
 }
 
 /**
  * Returns the highest memory address mapped in the local memory bus value.
  *
  * @return the highest memory address mapped in the local memory bus value.
  */
 public int getLMB_end()
 {
  return (int)lmb_end; 
 }
 
 /**
  * Returns the read latency value of the on-chip peripheral bus.
  *
  * @return the read latency value of the on-chip peripheral bus.
  */
 public int getOPB_read()
 {
  return opb_read; 
 }
 
 /**
  * Returns the write latency value of the on-chip peripheral bus.
  *
  * @return the write latency value of the on-chip peripheral bus.
  */
 public int getOPB_write()
 {
  return opb_write; 
 }
 
 /**
  * Returns the lowest memory address mapped in the on-chip peripheral bus value.
  *
  * @return the lowest memory address mapped in the on-chip peripheral bus value.
  */
 public int getOPB_begin()
 {
  return (int)opb_begin; 
 }
 
 /**
  * Returns the highest memory address mapped in the on-chip peripheral bus value.
  *
  * @return the highest memory address mapped in the on-chip peripheral bus value.
  */
 public int getOPB_end()
 {
  return (int)opb_end; 
 }
 
 /**
  * Returns the on-chip peripheral bus latency value in the case of an access to a memory address not mapped.
  *
  * @return the on-chip peripheral bus latency value in the case of an access to a memory address not mapped.
  */
 public int getMapped()
 {
  return mapped;
 }
 
 /**
  * Returns the list with the names of the classes of the devices.
  *
  * @return the list with the names of the classes of the devices.
  */
 public Vector<String> getDevice_class()
 {
  return device_class;
 }
  
 /**
  * Returns the list with the memory address of the registers of all devices.
  *
  * @return the list with the memory address of the registers of all devices.
  */
 public Vector<Vector<Long>> getDevice_registers_address()
 {
  return device_registers_address;
 }
 
 /**
  * Returns the list with the name of the registers of all devices.
  *
  * @return the list with the name of the registers of all devices.
  */
 public Vector<Vector<String>> getDevice_registers_name()
 {
  return device_registers_name;	 
 }
 
 /**
  * Returns the list with the value of the read latency of the device registers.
  *
  * @return the list with the value of the read latency of the device registers.
  */
 public Vector<Integer> getDevice_read_latency()
 {
  return device_read;	 
 }
 
 /**
  * Returns the list with the value of the write latency of the device registers.
  *
  * @return the list with the value of the write latency of the device registers.
  */
 public Vector<Integer> getDevice_write_latency()
 {
  return device_write;	 
 }

 /**
  * Returns the cpu class name.
  *
  * @return the cpu class name.
  */
 public String getCPU_class()
 {
  return cpu_class;
 }
 
 /**
  * Returns the system frequency.
  *
  * @return the system frequency.
  */
 public float getCPU_freq()
 {
  return freq;
 }
 
 /**
  * Returns the list with the number of cycles that each branch instruction spends to perform a conditional branch.
  *
  * @return the list with the number of cycles that each branch instruction spends to perform a conditional branch.
  */
 public Map<String,Integer> getCPU_inst_cond_latency()
 {
  return inst_cond_latency;
 }
 
 /**
  * Returns the list with the number of cycles that each instruction spends to be executed in the execute stage.
  *
  * @return the list with the number of cycles that each instruction spends to be executed in the execute stage.
  */
 public Map<String,Integer> getCPU_inst_latency()
 {
  return inst_latency;
 }
 
 /**
  * Returns the list of the with the instructions name.
  *
  * @return the list of the with the instructions name. 
  */
 public Map<String,Long> getCPU_instruction()
 {
  return instruction;
 }

 // ----------------------------------------------
 // method for displaying the contents of xml file
 // ----------------------------------------------
 
 /**
  * Displays the content of the XML system configuration file.
  */
 public void show()
 {
  String name_aux[];
  
  Vector<Long> reg_address;
  Vector<String> reg_name;
  
  int instruction_number;
  int register_number;
  int device_number;
  
  String decimal_string;
  DecimalFormat decimal;
  
  System.out.println("");
  System.out.println("System configuration");
  System.out.println("");
  System.out.println("  File");
  System.out.println("   . Name: " + systemconfig_file);
  
  System.out.println("");
  System.out.println("  Description");
  System.out.println("   . Name   : " + name);
  System.out.println("   . Version: " + version);
  System.out.println("   . Author : " + author);
  System.out.println("   . Company: " + company);
  System.out.println("   . Date   : " + date);
  System.out.println("   . Obs    : " + obs);
  
  System.out.println("");
  System.out.println("  Memory Latency");
  System.out.println("   . LMB, Read: " + Util.toDecStringSpace(lmb_read,2) + Util.toStringSpace("Write: ",10) + Util.toDecStringSpace(lmb_write,2));
  if(opb == true)
    System.out.println("   . OPB, Read: " + Util.toDecStringSpace(opb_read,2) + Util.toStringSpace("Write: ",10) + Util.toDecStringSpace(opb_write,2));		
  
  System.out.println("   . Not mapped: " + Util.toDecStringSpace(mapped,2));  
  
  System.out.println("");
  System.out.println("  Memory Range");
  decimal = new DecimalFormat("0.000");
  decimal_string = decimal.format((lmb_end + 1 - lmb_begin) / 1024d);
  System.out.println("   . LMB, Begin: 0x" + Util.toHexString(lmb_begin,8) + Util.toStringSpace("End: 0x",9) + Util.toHexString(lmb_end,8) + Util.toStringSpace("Size: ",8) + decimal_string + "kbytes");
  if(opb == true)
  {
   decimal = new DecimalFormat("0.000");
   decimal_string = decimal.format((opb_end + 1 - opb_begin) / 1024d);	   	  
   System.out.println("   . OPB, Begin: 0x" + Util.toHexString(opb_begin,8) + Util.toStringSpace("End: 0x",9) + Util.toHexString(opb_end,8) + Util.toStringSpace("Size: ",8) + decimal_string + "kbytes");		
  }
  
  System.out.println("");
  System.out.println("  Device");
  if(device_class.size() != 0)
  {	  
   for(device_number = 0;device_number < device_class.size();device_number++)
   {
    try
    {
     System.out.println("   . Device: " + Util.toStringFixed(device_class.get(device_number),20) + " Number: " + Util.toDecStringSpace(device_number,2));
     System.out.println("     - Read latency: " + Util.toDecStringSpace(device_read.get(device_number),2) + Util.toStringSpace(" Write latency: ",21) + Util.toDecStringSpace(device_write.get(device_number),2));
     reg_name = device_registers_name.get(device_number);
     reg_address = device_registers_address.get(device_number);
     if(reg_address != null && reg_name != null)
     {
      for(register_number = 0;register_number < reg_address.size();register_number++)
        System.out.println("     - Register: " + Util.toStringFixed(reg_name.get(register_number),11) + " Address: 0x" + Util.toHexString(reg_address.get(register_number),8));
     }
     else
       System.out.println("     - Registers not found.");
    }
    catch(ArrayIndexOutOfBoundsException e)
    {
     System.out.println("   . No data to display.");
    }
   }
  }
  else
	System.out.println("   . Devices not found.");
  System.out.println("");
  System.out.println("  CPU");
  if(cpu_class != null)
    System.out.println("   . Name: " + cpu_class);
  else
	System.out.println("   . No data to display.");  
  
  System.out.println("   . Frequency: " + (freq / 1E6) + "MHz");
  
  if(inst_latency != null)
  {
   name_aux = new String[inst_latency.size()];
   inst_latency.keySet().toArray(name_aux);
   java.util.Arrays.sort(name_aux);
   for(instruction_number = 0;instruction_number < name_aux.length;instruction_number++)
   {
	if(inst_cond_latency.get(name_aux[instruction_number]) > 0)
      System.out.println("   . Instruction: " + Util.toStringFixed(name_aux[instruction_number],10) + " Latency: " + Util.toDecStringSpace(inst_latency.get(name_aux[instruction_number]),2) + "  Conditional branch latency: " + Util.toDecStringSpace(inst_cond_latency.get(name_aux[instruction_number]),2));
	else
   	  System.out.println("   . Instruction: " + Util.toStringFixed(name_aux[instruction_number],10) + " Latency: " + Util.toDecStringSpace(inst_latency.get(name_aux[instruction_number]),2));	
   }
  }
 }
 
 //--------------------------------------------------------
 // class used to handle xml files of the SystemConfig type
 //--------------------------------------------------------
 
 /**
  * The system configuration parser handler class.
  */
 private class SystemConfigParserHandler extends DefaultHandler 
 {
  /** The current element. */
  private String currentElement = null;	 
	
  //---------------------------------
  // methods used to process the tags
  //---------------------------------
  
  /**
   * Receive notification of the beginning of an element.
   *
   * @param uri the Namespace URI, or the empty string if the element has no Namespace URI or if Namespace processing is not being performed.
   * @param localName the local name (without prefix), or the empty string if Namespace processing is not being performed.
   * @param qName the qualified name (with prefix), or the empty string if qualified names are not available.
   * @param attributes the attributes attached to the element. If there are no attributes, it shall be an empty Attributes object. The value of this object after startElement returns is undefined.
   * @throws SAXException any SAX exception, possibly wrapping another exception.
   * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
   */
  public void startElement(String uri, String localName, String qName,Attributes attributes) throws SAXException
  {
   long value;
   
   try 
   {
	if(qName.equalsIgnoreCase("NAME"))
	{
	 currentElement = qName;	
	}
	else if(qName.equalsIgnoreCase("AUTHOR"))
	{
	 currentElement = qName;	
	}
	else if(qName.equalsIgnoreCase("VERSION"))
	{
	 currentElement = qName;	
	}
	else if(qName.equalsIgnoreCase("COMPANY"))
	{
	 currentElement = qName;	
	}
	else if(qName.equalsIgnoreCase("DATE"))
	{
	 currentElement = qName;	
	}
	else if(qName.equalsIgnoreCase("OBS"))
	{
	 currentElement = qName;	
	}
	else if(qName.equalsIgnoreCase("LMB"))
	{
	 try
	 {
	  value = Integer.parseInt(attributes.getValue("read"),10);
	  lmb_read = (int)value;
	  if(value < 1)
	    throw new SystemConfigParserException("LMB read cycles is less then 1.");
	 }
	 catch(NumberFormatException e)
	 {
	  throw new SAXException("LMB read cycles is not a decimal number.");   
	 }
	 catch(SystemConfigParserException e)
	 {
	  throw new SAXException(e.getMessage());   
	 }
	 
	 try
	 {
	  value = Integer.parseInt(attributes.getValue("write"),10);
	  lmb_write = (int)value;
	  if(value < 1)
	    throw new SystemConfigParserException("LMB write cycles is less then 1.");
	 }
	 catch(NumberFormatException e)
	 {
	  throw new SAXException("LMB write cycles is not a decimal number.");   
	 }
	 catch(SystemConfigParserException e)
	 {
	  throw new SAXException(e.getMessage());   
	 }
	 
	 try
	 {
	  value = Long.parseLong(attributes.getValue("end"),16);
	  lmb_end = value;
	  if(value < 0 || value > 0xffffffffL)
		throw new SystemConfigParserException("is a 64 bits word.");  
	  if((((value & 0xffffffffL) + 1) % 4) != 0)
		throw new SystemConfigParserException("is not a 32 bits word.");
	 }
	 catch(NumberFormatException e)
	 {
	  throw new SAXException("LMB memory end is not a hexadecimal number.");   
	 }
	 catch(SystemConfigParserException e)
	 {
	  throw new SAXException("LMB memory end " + e.getMessage());   
	 }
	}
	else if(qName.equalsIgnoreCase("OPB"))
	{
	 opb = true;
	 
	 try
	 {
	  value = Integer.parseInt(attributes.getValue("read"),10);
	  opb_read = (int)value;
	  if(value < 1)
	    throw new SystemConfigParserException("OPB read cycles is less then 1.");
	 }
	 catch(NumberFormatException e)
	 {
	  throw new SAXException("OPB read cycles is not a decimal number.");   
	 }
	 catch(SystemConfigParserException e)
	 {
	  throw new SAXException(e.getMessage());   
	 }
	 
	 try
	 {
	  value = Integer.parseInt(attributes.getValue("write"),10);
	  opb_write = (int)value;
	  if(value < 1)
	    throw new SystemConfigParserException("OPB write cycles is less then 1.");
	 }
	 catch(NumberFormatException e)
	 {
	  throw new SAXException("OPB write cycles is not a decimal number.");   
	 }
	 catch(SystemConfigParserException e)
	 {
	  throw new SAXException(e.getMessage());   
	 }
	 
	 try
	 {
	  value = Long.parseLong(attributes.getValue("begin"),16);
	  opb_begin = value;
	  if(value < 0 || value > 0xffffffffL)
		throw new SystemConfigParserException("is a 64 bits word.");  
	  if(((value & 0xffffffffL) % 4) != 0)
		throw new SystemConfigParserException("is not a 32 bits word.");
	 }
	 catch(NumberFormatException e)
	 {
	  throw new SAXException("OPB memory begin is not a hexadecimal number.");   
	 }
	 catch(SystemConfigParserException e)
	 {
	  throw new SAXException("OPB memory begin " + e.getMessage());   
	 }
	 
	 try
	 {
	  value = Long.parseLong(attributes.getValue("end"),16);
	  opb_end = value;
	  if(value < 0 || value > 0xffffffffL)
		throw new SystemConfigParserException("is a 64 bits word.");  
	  if((((value & 0xffffffffL) + 1) % 4) != 0)
		throw new SystemConfigParserException("is not a 32 bits word.");
	 }
	 catch(NumberFormatException e)
	 {
	  throw new SAXException("OPB memory end is not a hexadecimal number.");   
	 }
	 catch(SystemConfigParserException e)
	 {
	  throw new SAXException("OPB memory end " + e.getMessage());   
	 }
	}
	else if(qName.equalsIgnoreCase("MAPPED"))
	{
	 try
	 {
	  value = Integer.parseInt(attributes.getValue("cycle"),10);
	  mapped = (int)value;
	  if(value < 1)
	    throw new SystemConfigParserException("");
	 }
	 catch(NumberFormatException e)
	 {
	  throw new SAXException("memory not mapped cycles is not a decimal number.");   
	 }
	 catch(SystemConfigParserException e)
	 {
	  throw new SAXException("memory not mapped cycles is less then 1.");   
	 } 
	}
	else if(qName.equalsIgnoreCase("DEVICE"))
	{
     registers_address = new Vector<Long>();
     registers_name = new Vector<String>();
	}
	else if(qName.equalsIgnoreCase("DEVCLASS"))
	{
	 currentElement = qName;	
	}
	else if(qName.equalsIgnoreCase("LATENCY"))
	{
	 try
	 {
	  value = Integer.parseInt(attributes.getValue("read"),10);
	  if(value < 1)
	    throw new SystemConfigParserException("OPB read cycles is less then 1.");
	  device_read.add((int)value);
	 }
	 catch(NumberFormatException e)
	 {
	  throw new SAXException("OPB read cycles is not a decimal number.");   
	 }
	 catch(SystemConfigParserException e)
	 {
	  throw new SAXException(e.getMessage());   
	 }
	 
	 try
	 {
	  value = Integer.parseInt(attributes.getValue("write"),10);
	  if(value < 1)
	    throw new SystemConfigParserException("OPB write cycles is less then 1.");
      device_write.add((int)value);	  
	 }
	 catch(NumberFormatException e)
	 {
	  throw new SAXException("OPB write cycles is not a decimal number.");   
	 }
	 catch(SystemConfigParserException e)
	 {
	  throw new SAXException(e.getMessage());   
	 }
	}
	else if(qName.equalsIgnoreCase("REG"))
	{
	 try
	 {
	  registers_name.add(attributes.getValue("name"));
	  if(attributes.getValue("name").length() == 0)
	    throw new SystemConfigParserException("the register of " + device_class.get(device_class.size() - 1) + " " + (device_class.size() - 1) + ", OPB Device in the position " + (registers_address.size() + 1) + " most have name."); 
	  value = Long.parseLong(attributes.getValue("address"),16);
	  registers_address.add(value);
	  if(value < 0 || value > 0xffffffffL)
		throw new SystemConfigParserException("OPB device  register " + attributes.getValue("name") + "with the address 0x" + Util.toHexString(value,16) + "is a 64 bits word.");  
	  if(((value & 0xffffffffL) % 4) != 0)
		throw new SystemConfigParserException("OPB device  register " + attributes.getValue("name") + "with the address 0x" + Util.toHexString(value,8) +  "is not a 32 bits word.");
	 }
	 catch(NumberFormatException e)
	 {
	  throw new SAXException("OPB device register " + attributes.getValue("name") + " the addres is not a hexadecimal number.");   
	 }
	 catch(SystemConfigParserException e)
	 {
	  throw new SAXException(e.getMessage());   
	 }
	}
	else if(qName.equalsIgnoreCase("CPUCLASS"))
	{
	 currentElement = qName;
	}
	else if(qName.equalsIgnoreCase("FREQ"))
	{
	 currentElement = qName;
	}
	else if(qName.equalsIgnoreCase("INST"))
	{
	 try
	 {
	  if(attributes.getValue("name").length() == 0)
		throw new SystemConfigParserException("the instruction in the position " + (inst_latency.size() + 1) +" most have name.");
	 }
	 catch(SystemConfigParserException e)
	 {
	  throw new SAXException(e.getMessage());   
	 }
		
	 try
	 {
	  value = Integer.parseInt(attributes.getValue("latency"),10);
	  if(value < 1)
	    throw new SystemConfigParserException("CPU instruction " + attributes.getValue("name") + " have a wrong latency value.");
	 }
	 catch(NumberFormatException e)
	 {
	  throw new SAXException("CPU instruction " + attributes.getValue("name") + " the cycles is not a decimal number.");   
	 }
	 catch(SystemConfigParserException e)
	 {
	  throw new SAXException(e.getMessage());   
	 }
	 inst_latency.put(attributes.getValue("name"),(int)value);
	 number_instructions_latency++;
	 if(number_instructions_latency != inst_latency.size())
	   throw new SystemConfigParserException("the CPU have two or more instructions with the name " + attributes.getValue("name") + ".");
	
	 try
	 {
	  value = Integer.parseInt(attributes.getValue("cond_latency"),10);
	  if(value < 0)
	    throw new SystemConfigParserException("CPU instruction " + attributes.getValue("name") + " have a wrong conditional latency value.");
	 }
	 catch(NumberFormatException e)
	 {
	  throw new SAXException("CPU instruction " + attributes.getValue("name") + " the cycles is not a decimal number.");   
	 }
	 catch(SystemConfigParserException e)
	 {
	  throw new SAXException(e.getMessage());   
	 } 	
	 inst_cond_latency.put(attributes.getValue("name"),(int)value);
	 number_instructions_cond_latency++;
	 if(number_instructions_cond_latency != inst_cond_latency.size())
	   throw new SystemConfigParserException("the CPU have two or more instructions with the name " + attributes.getValue("name") + ".");
	}
   }
   catch(Exception e) 
   {
	throw new SAXException(e.getMessage());
   }
  }
	 
  /**
   * Receive notification of the end of an element.
   *
   * @param namespaceURI the Namespace URI, or the empty string if the element has no Namespace URI or if Namespace processing is not being performed.
   * @param localName the local name (without prefix), or the empty string if Namespace processing is not being performed.
   * @param qName the qualified XML name (with prefix), or the empty string if qualified names are not available.
   * @throws SAXException any SAX exception, possibly wrapping another exception.
   * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
   */
  public void endElement(String namespaceURI, String localName,String qName) throws SAXException
  {	
   try
   {
	if (qName.equalsIgnoreCase("DEVICE"))
	{
	 device_registers_address.add(registers_address);
     device_registers_name.add(registers_name);
	}
   }
   catch(Exception e) 
   {
	throw new SAXException(e.getMessage());
   }
  }

  /**
   * Receive notification of character data.
   *
   * @param ch the characters from the XML document.
   * @param start the start position in the array.
   * @param length the number of characters to read from the array.
   * @throws SAXException any SAX exception, possibly wrapping another exception.
   * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
   */
  public void characters(char ch[],int start,int length) throws SAXException
  {
   String string;
   
   try
   {
    string = new String(ch,start,length);
    if(!string.trim().equals("")) 
    {
     if(currentElement.equalsIgnoreCase("NAME")) 
     {
      name = string;
     }
     else if(currentElement.equalsIgnoreCase("VERSION"))
	 {
      version = string;	
	 }
     else if(currentElement.equalsIgnoreCase("AUTHOR"))
	 {
      author = string;	
	 }
     else if(currentElement.equalsIgnoreCase("COMPANY"))
	 {
      company = string;
	 }
     else if(currentElement.equalsIgnoreCase("DATE"))
	 {
      date = string;	
	 }
     else if(currentElement.equalsIgnoreCase("OBS"))
	 {
      obs = string;	
	 }
     else if(currentElement.equalsIgnoreCase("DEVCLASS"))
	 {
      if(string.length() == 0)
        throw new SystemConfigParserException("the OPB Device number " + device_class.size() + " most have name."); 
      device_class.add(string);	
	 }
     else if(currentElement.equalsIgnoreCase("CPUCLASS"))
	 {
      if(string.length() == 0)
      {
       cpu_class = "";
       throw new SystemConfigParserException("the CPU most have name.");
      }
      cpu_class = string;	
	 }
     else if(currentElement.equalsIgnoreCase("FREQ"))
     {
      if(string.length() != 0)
      {
       try
       {
     	freq = Float.parseFloat(string);
     	if(freq < 5000000f)
     	  throw new SystemConfigParserException("");  
       }	
       catch(NumberFormatException e)
       {
     	throw new SAXException("CPU frequency is not a floating point number.");   
       }
       catch(SystemConfigParserException e)
       {
        throw new SAXException(e.getMessage());   
       }	  
      }
     }
    }
   }
   catch(Exception e) 
   {
	throw new SAXException(e.getMessage());
   }
  }
  
  //----------------------------
  // methods used to deal errors
  //----------------------------
	 
  /**
   * Receive notification of a recoverable parser error.
   *
   * @param exception the error information encoded as an exception.
   * @throws SAXException any SAX exception, possibly wrapping another exception.
   * @see org.xml.sax.helpers.DefaultHandler#error(org.xml.sax.SAXParseException)
   */
  public void error(SAXParseException exception) throws SAXException 
  {
   throw new SAXException(systemconfig_file + ",XML file don't respect the Document Type Definition.");
  }

  /**
   * Report a fatal XML parsing error. 
   *
   * @param exception the error information encoded as an exception. 
   * @throws SAXException any SAX exception, possibly wrapping another exception.
   * @see org.xml.sax.helpers.DefaultHandler#fatalError(org.xml.sax.SAXParseException)
   */
  public void fatalError(SAXParseException exception) throws SAXException 
  {
   throw new SAXException(systemconfig_file + ",XML file fatalError.");
  }

  /**
   * Receive notification of a parser warning. 
   *
   * @param exception the warning information encoded as an exception. 
   * @throws SAXException any SAX exception, possibly wrapping another exception.
   * @see org.xml.sax.helpers.DefaultHandler#warning(org.xml.sax.SAXParseException)
   */
  public void warning(SAXParseException exception) throws SAXException 
  {
   throw new SAXException(systemconfig_file + ",XML file warning.");
  }

  //----------------------------------------------------------
  // method used when can not open o DTD in the program folder
  //----------------------------------------------------------
	 
  /**
   * Allow the application to resolve external entities. 
   *
   * @param publicId the public identifier of the external entity being referenced, or null if none was supplied.
   * @param systemId the system identifier of the external entity being referenced.
   * @return an InputSource object describing the new input source, or null to request that the parser open a regular URI connection to the system identifier.
   * @throws SAXException any SAX exception, possibly wrapping another exception.
   * @see org.xml.sax.helpers.DefaultHandler#resolveEntity(java.lang.String, java.lang.String)
   */
  public InputSource resolveEntity(String publicId, String systemId) throws SAXException 
  {
   FileInputStream fis;
   File fl;
   
   try 
   {
    fl = new File(Configuration.configuration_folder + "systemconfig.dtd");
	fis = new FileInputStream(fl);
	return new InputSource(fis);
   } 
   catch(Exception e) 
   {
    throw new SAXException("missing the file Document Type Definition.");
   }
  }
 }
}

