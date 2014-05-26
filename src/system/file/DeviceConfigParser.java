package system.file;

import java.util.*;
import java.io.*;

import org.xml.sax.helpers.*;
import org.xml.sax.*;

import system.file.xml.XMLReader;
import system.*;

/**
 * The device configuration parser class. This class performs the loading and validation of the XML device configuration file.
 */
public class DeviceConfigParser
{
 /** The device configuration parser handler. */
 private DeviceConfigParserHandler handler;
 /** The XML device configuration file (path and name). */
 private String deviceconfig_file;
 
 // description section  
 /** The version of the device module. */
 private String version = "";
 /** The company's device name. */
 private String company = "";
 /** The author name of the device module. */
 private String author = "";
 /** The device name. */
 private String name = "";
 /** The version date of the device module. */
 private String date = "";
 /** The observation of the device module. */
 private String obs = "";
 
 // device section
 /** The list of the with the registers name. */
 private Vector<String> reg_class;
 /** The number of registers. */
 private int number_registers;
 /** The device class name. */
 private String dev_class;
 
 /**
  * Instantiates a new device configuration parser.
  */
 public DeviceConfigParser()
 {
  handler = new DeviceConfigParserHandler();
  reg_class = new Vector<String>();
 }

 //-----------------------------------------
 // method used to load and verify xml files
 //-----------------------------------------
 
 /**
  * Loads the XML device configuration file (path and name).
  *
  * @param deviceconfig_file the XML device configuration file (path and name).
  * @throws DeviceConfigParserException if any error occur when loads the XML device configuration file.
  */
 public void load(String deviceconfig_file) throws DeviceConfigParserException
 {
  XMLReader inputXml;
  File file;
  
  this.deviceconfig_file = deviceconfig_file;
  try 
  {
   file = new File(deviceconfig_file);
   inputXml = new XMLReader(file);
   inputXml.read(handler);
   verify();
  }
  catch(DeviceConfigParserException e)
  {
   throw new DeviceConfigParserException(e.getMessage());
  }
  catch(IOException e)
  {
   throw new DeviceConfigParserException(e.getMessage());
  }
  catch(SAXException e) 
  {
   throw new DeviceConfigParserException(e.getMessage());
  }
 }

 /**
  * Verifies the validation of the content of the XML cpu configuration file.
  *
  * @throws DeviceConfigParserException if any error occur when verifies the validation of the content of the XML device configuration file.
  */
 private void verify() throws DeviceConfigParserException
 {
  int aux_a;
  int aux_b;
  
  try 
  {
   Class.forName(Configuration.devices + "." + dev_class + "." + dev_class);
  } 
  catch(ClassNotFoundException e) 
  {
   throw new DeviceConfigParserException(dev_class + ", OPB Device not exist.");
  }
  if(number_registers != reg_class.size())
	throw new DeviceConfigParserException(dev_class + ", OPB Device the number of registers is " + number_registers + " and the defined registers are " + reg_class.size() + ".");
  for(aux_a = 0;aux_a < number_registers - 1;aux_a++)
	for(aux_b = aux_a + 1;aux_b < number_registers;aux_b++)
	  if(reg_class.get(aux_a).equals(reg_class.get(aux_b)))
		throw new DeviceConfigParserException(dev_class + ", OPB Device have two or more registers with the name " + reg_class.get(aux_a) + ".");
  for(aux_a = 0;aux_a < number_registers;aux_a++)
    if(dev_class.equals(reg_class.get(aux_a)))
	  throw new DeviceConfigParserException(dev_class + ", OPB Device have one or more registers with the name of the device");
  try 
  {
   for(aux_a = 0;aux_a < number_registers;aux_a++)
     Class.forName(Configuration.devices + "." + dev_class + "." + reg_class.get(aux_a));
  } 
  catch(ClassNotFoundException e) 
  {
   throw new DeviceConfigParserException("in the " + dev_class + ", OPB Device is missing the " + reg_class.get(aux_a) + " register.");
  }
 }
 
 //--------------------------------
 // methods used for accessing data
 //--------------------------------
 
 /**
  * Returns the device name.
  *
  * @return the device name.
  */
 public String getDescription_name()
 {
  return name; 
 }
 
 /**
  * Returns the author name of the device module.
  *
  * @return the author name of the device module.
  */
 public String getDescription_author()
 {
  return author; 
 }
 
 /**
  * Returns the company's device name.
  *
  * @return the company's device name.
  */
 public String getDescription_company()
 {
  return company; 
 }
 
 /**
  * Returns the version of the device module.
  *
  * @return the version of the device module.
  */
 public String getDescription_version()
 {
  return version; 
 }
 
 /**
  * Returns the version date of the device module.
  *
  * @return the version date of the device module.
  */
 public String getDescription_date()
 {
  return date; 
 }
 
 /**
  * Returns the observation of the device module.
  *
  * @return the observation of the device module.
  */
 public String getDescription_obs()
 {
  return obs; 
 }
 
 /**
  * Returns the number of registers of the device.
  *
  * @return the number of registers of the device.
  */
 public int getNumber_registers()
 {
  return number_registers;
 }
 
 /**
  * Returns the device.
  *
  * @return the device.
  */
 public String getDev_class_name()
 {
  return dev_class;
 }

 /**
  * Returns the list with the name of the device registers.
  *
  * @return the list with the name of the device registers.
  */
 public Vector<String> getReg_class_names()
 {
  return reg_class;
 }
 
 // ----------------------------------------------
 // method for displaying the contents of xml file
 // ----------------------------------------------
 
 /**
  * Displays the content of the XML device configuration file.
  */
 public void show()
 {
  int aux;
  
  System.out.println("");
  System.out.println("OPB Device definition");
  System.out.println("");
  System.out.println("  File");
  System.out.println("   . Name: " + deviceconfig_file);
  System.out.println("");
  System.out.println("  Description");
  System.out.println("   . Name   : " + name);
  System.out.println("   . Version: " + version);
  System.out.println("   . Author : " + author);
  System.out.println("   . Company: " + company);
  System.out.println("   . Date   : " + date);
  System.out.println("   . Obs    : " + obs);
  System.out.println("");
  System.out.println("  OPB Device");
  System.out.println("   . Name: " + dev_class);
  System.out.println("");
  System.out.println("  Registers");
  System.out.println("   . Number: " + number_registers);
  if(reg_class.size() > 0)
    for(aux = 0;aux < reg_class.size();aux++)
      System.out.println("   . Register: " + reg_class.get(aux));
 }
  
 //--------------------------------------------------------
 // class used to handle xml files of the SystemConfig type
 //--------------------------------------------------------
 
 /**
  * The device configuration parser handler class.
  */
 private class DeviceConfigParserHandler extends DefaultHandler 
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
    else if(qName.equalsIgnoreCase("DEVCLASS"))
	{
	 try
	 {
	  if(attributes.getValue("name").length() == 0)
		throw new DeviceConfigParserException("the OPB Device most have a name.");
	  dev_class = attributes.getValue("name");
	  value = Integer.parseInt(attributes.getValue("number"),10);
	  number_registers = (int)value;
	  if(value < 0)
	    throw new DeviceConfigParserException("the number of registers is less then 0.");
	 }
	 catch(NumberFormatException e)
	 {
	  throw new SAXException("the number of registers is not a decimal number.");   
	 }
	 catch(DeviceConfigParserException e)
	 {
	  throw new SAXException(e.getMessage());   
	 }
	}
	else if(qName.equalsIgnoreCase("REG"))
	{
	 currentElement = qName;	
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
     else if(currentElement.equalsIgnoreCase("REG")) 
     {
      reg_class.add(string);
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
   throw new SAXException(deviceconfig_file + ",XML file don't respect the Document Type Definition.");
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
   throw new SAXException(deviceconfig_file + ",XML file fatalError.");
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
   throw new SAXException(deviceconfig_file + ",XML file warning.");
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
    fl = new File(Configuration.configuration_folder + "deviceconfig.dtd");
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

