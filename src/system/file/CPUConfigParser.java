package system.file;

import java.util.*;
import java.io.*;

import org.xml.sax.helpers.*;
import org.xml.sax.*;

import system.file.xml.XMLReader;
import system.*;

/**
 * The cpu configuration parser class. This class performs the loading and validation of the XML processor configuration file.
 */
public class CPUConfigParser
{
 /** The cpu configuration parser handler. */
 private CPUConfigParserHandler handler;
 /** The XML cpu configuration file (path and name). */
 private String cpuconfig_file;

 // description section 
 /** The version of the processor module. */
 private String version = "";
 /** The company's processor name. */
 private String company = "";
 /** The author name of the processor module. */
 private String author = "";
 /** The processor name. */
 private String name = "";
 /** The version date of the processor module. */
 private String date = "";
 /** The observation of the processor module. */
 private String obs = "";
 
 // cpu section
 /** The list of the with the instructions name. */
 private Map<String,Integer> instruction;
 /** The number of instructions of the processor counted from the XML cpu configuration file, a auxiliary attribute. */
 private int number_instructions_aux = 0;
 /** The number of instructions of the processor. */
 private int number_instructions;
 /** The system processor class name. */
 private String cpu_class;
 /** The gcc id code of the processor. */
 private int gcc_id;
 /** The number of pipeline stages. */
 private int pipeline_stages;
 /** The execute stage number. */
 private int execute_stage;
 
 /**
  * Instantiates a new cpu configuration parser.
  */
 public CPUConfigParser()
 {
  handler = new CPUConfigParserHandler();
  instruction = new HashMap<String,Integer>();
 }

 //-----------------------------------------
 // method used to load and verify xml files
 //-----------------------------------------
 
 /**
  * Loads the XML cpu configuration file (path and name).
  *
  * @param cpuconfig_file the XML cpu configuration file (path and name).
  * @throws CPUConfigParserException if any error occur when loads the XML cpu configuration file.
  */
 public void load(String cpuconfig_file) throws CPUConfigParserException
 {
  XMLReader inputXml;
  File file;
  
  this.cpuconfig_file = cpuconfig_file;
  try 
  {
   file = new File(cpuconfig_file);
   inputXml = new XMLReader(file);
   inputXml.read(handler);
   verify();
  }
  catch(CPUConfigParserException e)
  {
   throw new CPUConfigParserException(e.getMessage());	  
  }
  catch(IOException e)
  {
   throw new CPUConfigParserException(e.getMessage());
  }
  catch(SAXException e) 
  {
   throw new CPUConfigParserException(e.getMessage());
  }
 }

 /**
  * Verifies the validation of the content of the XML cpu configuration file.
  *
  * @throws CPUConfigParserException if any error occur when verifies the validation of the content of the XML cpu configuration file.
  */
 private void verify() throws CPUConfigParserException
 {
  Iterator<String> instructions_name;
  String name;
  int aux_a;
  
  try 
  {
   Class.forName(Configuration.processors + "." + cpu_class + "." + cpu_class);
  } 
  catch(ClassNotFoundException e) 
  {
   throw new CPUConfigParserException(cpu_class + ", CPU not exist.");
  }
  
  try 
  {
   Class.forName(Configuration.processors + "." + cpu_class + ".instsetarq");
  } 
  catch(ClassNotFoundException e) 
  {
   throw new CPUConfigParserException(cpu_class + ", CPU the InstSet class not exist.");
  }
  
  try 
  {
   Class.forName(Configuration.processors + "." + cpu_class + ".cpu_status");
  } 
  catch(ClassNotFoundException e) 
  {
   throw new CPUConfigParserException(cpu_class + ", CPU the CPU_Status class not exist.");
  }
  
  try 
  {
   Class.forName(Configuration.processors + "." + cpu_class + ".breakpoint");
  } 
  catch(ClassNotFoundException e) 
  {
   throw new CPUConfigParserException(cpu_class + ", CPU the breakpoint class not exist.");
  }
  
  try 
  {
   Class.forName(Configuration.processors + "." + cpu_class + ".empty");
  } 
  catch(ClassNotFoundException e) 
  {
   throw new CPUConfigParserException(cpu_class + ", CPU the empty class not exist.");
  }
  
  try 
  {
   Class.forName(Configuration.processors + "." + cpu_class + ".illegal");
  } 
  catch(ClassNotFoundException e) 
  {
   throw new CPUConfigParserException(cpu_class + ", CPU the illegal class not exist.");
  }
  
  try 
  {
   Class.forName(Configuration.processors + "." + cpu_class + ".mapped");
  } 
  catch(ClassNotFoundException e) 
  {
   throw new CPUConfigParserException(cpu_class + ", CPU the mapped class not exist.");
  }
  
  try 
  {
   Class.forName(Configuration.processors + "." + cpu_class + ".stop");
  } 
  catch(ClassNotFoundException e) 
  {
   throw new CPUConfigParserException(cpu_class + ", CPU the stop class not exist.");
  }
  
  if(number_instructions != number_instructions_aux)
	throw new CPUConfigParserException(cpu_class + ", CPU the number of instructions are " + number_instructions + " and the defined instructions are " + instruction.size() + ".");
 
  if(instruction.containsKey("breakpoint"))
		throw new CPUConfigParserException(cpu_class + ", CPU have one instruction with name breakpoint.");
  
  if(instruction.containsKey("empty"))
	throw new CPUConfigParserException(cpu_class + ", CPU have one instruction with name empty.");
		    
  if(instruction.containsKey("illegal"))
	throw new CPUConfigParserException(cpu_class + ", CPU have one instruction with name illegal.");
	  
  if(instruction.containsKey("mapped"))
	throw new CPUConfigParserException(cpu_class + ", CPU have one instruction with name mapped.");

  if(instruction.containsKey("stop"))
	throw new CPUConfigParserException(cpu_class + ", CPU have one instruction with name stop.");
	  
  if(instruction.containsKey(cpu_class))
	throw new CPUConfigParserException(cpu_class + ", CPU have one or more instructions with the name of the processor.");
	  
  instructions_name = instruction.keySet().iterator();
  for(aux_a = 0;aux_a < number_instructions;aux_a++)
  {	   
   name = instructions_name.next();
   try
   {
    Class.forName(Configuration.processors + "." + cpu_class + ".instruction." + name);
   }
   catch(ClassNotFoundException e) 
   {
	throw new CPUConfigParserException("in the " + cpu_class + ", CPU the " + name + " instruction is missing.");
   }
  }
 }
 
 //--------------------------------
 // methods used for accessing data
 //--------------------------------
 
 /**
  * Returns the processor name.
  *
  * @return the processor name.
  */
 public String getDescription_name()
 {
  return name; 
 }
 
 /**
  * Returns the author name of the processor module.
  *
  * @return the author name of the processor module.
  */
 public String getDescription_author()
 {
  return author; 
 }
 
 /**
  * Returns the company's processor name.
  *
  * @return the company's processor name.
  */
 public String getDescription_company()
 {
  return company; 
 }
 
 /**
  * Returns the version of the processor module.
  *
  * @return the version of the processor module.
  */
 public String getDescription_version()
 {
  return version; 
 }
 
 /**
  * Returns the version date of the processor module.
  *
  * @return the version date of the processor module.
  */
 public String getDescription_date()
 {
  return date; 
 }
 
 /**
  * Returns the observation of the processor module.
  *
  * @return the observation of the processor module.
  */
 public String getDescription_obs()
 {
  return obs; 
 }
 
 /**
  * Returns the number of instructions of the processor.
  *
  * @return the number of instructions of the processor.
  */
 public int getNumber_instructions()
 {
  return number_instructions;
 }
 
 /**
  * Returns the system processor.
  *
  * @return the system processor.
  */
 public String getCPU_class_name()
 {
  return cpu_class;
 }

 /**
  * Returns the gcc id code of the processor.
  *
  * @return the gcc id code of the processor.
  */
 public int getCPU_gcc_id()
 {
  return gcc_id;	 
 }
 
 /**
  * Returns the number of pipeline stages.
  *
  * @return The number of pipeline stages.
  */
 public int getCPU_pipeline_stages()
 {
  return pipeline_stages;	 
 }
 
 /**
  * Returns the execute stage number.
  *
  * @return the execute stage number.
  */
 public int getCPU_execute_stage()
 {
  return execute_stage;	 
 }
 
 /**
  * Returns the list with the name of the processor instructions.
  *
  * @return the list with the name of the processor instructions.
  */
 public Map<String,Integer> getInst_names()
 {
  return instruction;
 }
 
 // ----------------------------------------------
 // method for displaying the contents of xml file
 // ----------------------------------------------
 
 /**
  * Displays the content of the XML cpu configuration file.
  */
 public void show()
 {
  String name_aux[];
  int aux;
  
  System.out.println("");
  System.out.println("Processor definition");
  System.out.println("");
  System.out.println("  File");
  System.out.println("   . Name: " + cpuconfig_file);
  System.out.println("");
  System.out.println("  Description");
  System.out.println("   . Name   : " + name);
  System.out.println("   . Version: " + version);
  System.out.println("   . Author : " + author);
  System.out.println("   . Company: " + company);
  System.out.println("   . Date   : " + date);
  System.out.println("   . Obs    : " + obs);
  System.out.println("");
  System.out.println("  CPU");
  System.out.println("   . Name           : " + cpu_class);
  System.out.println("   . GCC id         : 0x" + Integer.toHexString(gcc_id));
  System.out.println("   . Pipeline stages: " + pipeline_stages);
  System.out.println("   . Execute stage  : " + execute_stage);
  System.out.println("");
  System.out.println("  Instructions");
  System.out.println("   . Number: " + number_instructions);
  if(number_instructions > 0)
  {
   name_aux = new String[instruction.keySet().size()];
   instruction.keySet().toArray(name_aux);
   java.util.Arrays.sort(name_aux);
   for(aux = 0;aux < name_aux.length;aux++)
   {
	if(instruction.get(name_aux[aux]) > 0)
      System.out.println("   . Instruction: " + Util.toStringFixed(name_aux[aux],8) + " Conditional branch: " + instruction.get(name_aux[aux]));
	else
	  System.out.println("   . Instruction: " + Util.toStringFixed(name_aux[aux],8));
   }
  }
 }
  
 //--------------------------------------------------------
 // class used to handle xml files of the SystemConfig type
 //--------------------------------------------------------
 
 /**
  * The cpu config parser handler class.
  */
 private class CPUConfigParserHandler extends DefaultHandler 
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
   int value;
   
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
    else if(qName.equalsIgnoreCase("CPUCLASS"))
	{
	 try
	 {
	  if(attributes.getValue("name").length() == 0)
		throw new CPUConfigParserException("the CPU most have a name.");
	  cpu_class = attributes.getValue("name");
	 } 
	 catch(CPUConfigParserException e)
	 {
	  throw new SAXException(e.getMessage());   
	 }
	 
	 try
	 {
	  if(attributes.getValue("gcc_id").length() == 0)
			throw new CPUConfigParserException("the CPU most have a GCC id.");
	  gcc_id = Integer.parseInt(attributes.getValue("gcc_id"),16);
	 }
	 catch(NumberFormatException e)
	 {
	  throw new SAXException("the GCC id is not a hexadecimal number.");   
	 }
	 catch(CPUConfigParserException e)
	 {
	  throw new SAXException(e.getMessage());   
	 }
	 
	 try
	 { 
	  value = Integer.parseInt(attributes.getValue("number"),10);
	  number_instructions = value;
	  if(value < 1)
	    throw new CPUConfigParserException("the number of instructions is less then 1.");
	 }
	 catch(NumberFormatException e)
	 {
	  throw new SAXException("the number of instructions is not a decimal number.");   
	 }
	 catch(CPUConfigParserException e)
	 {
	  throw new SAXException(e.getMessage());   
	 }
	}
    else if(qName.equalsIgnoreCase("PIPELINE"))
	{
	 try
	 {
	  if(attributes.getValue("stages").length() == 0)
		throw new CPUConfigParserException("the CPU most have stages.");
	  pipeline_stages = Integer.parseInt(attributes.getValue("stages"),10);
	  if(pipeline_stages < 1)
	    throw new CPUConfigParserException("the pipeline stages is less then 1.");  
	 }
	 catch(NumberFormatException e)
	 {
	  throw new SAXException("the pipeline stages is not a decimal number.");   
	 }
	 catch(CPUConfigParserException e)
	 {
	  throw new SAXException(e.getMessage());   
	 }
	 
	 try
	 {
	  if(attributes.getValue("execute").length() == 0)
		throw new CPUConfigParserException("the CPU most have execute stage.");
	  execute_stage = Integer.parseInt(attributes.getValue("execute"),10);
	  if(execute_stage < 1)
		throw new CPUConfigParserException("the execute stage is less then 1.");  
	 }
	 catch(NumberFormatException e)
	 {
	  throw new SAXException("the execute stage is not a decimal number.");   
	 }
	 catch(CPUConfigParserException e)
	 {
	  throw new SAXException(e.getMessage());   
	 }
	}
	else if(qName.equalsIgnoreCase("INST"))
	{
	 try
	 {
	  if(attributes.getValue("name").length() == 0)
		throw new CPUConfigParserException("the instruction most have a name.");
	  value = Integer.parseInt(attributes.getValue("cond"),10);
	  if(value != 1 && value != 0)
	    throw new CPUConfigParserException("the instruction most be conditional or not [0;1].");
	  instruction.put(attributes.getValue("name"),value);
	  number_instructions_aux++;
	  if(number_instructions_aux != instruction.size())
		throw new CPUConfigParserException(cpu_class + ", CPU have two or more instructions with the name " + attributes.getValue("name") + ".");  
	 }
	 catch(CPUConfigParserException e)
	 {
	  throw new SAXException(e.getMessage());   
	 }
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
  public void characters(char ch[],int start,int length)  throws SAXException
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
   throw new SAXException(cpuconfig_file + ",XML file don't respect the Document Type Definition.");
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
   throw new SAXException(cpuconfig_file + ",XML file fatalError.");
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
   throw new SAXException(cpuconfig_file + ",XML file warning.");
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
    fl = new File(Configuration.configuration_folder + "cpuconfig.dtd");
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

