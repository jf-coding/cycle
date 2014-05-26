package system.file.xml;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * The Class XMLReader.
 */
public class XMLReader
{
 /** The file. */
 File file;

 /**
  * Instantiates a new XML reader.
  *
  * @param fich the fich
  * @throws IOException Signals that an I/O exception has occurred.
  */
 public XMLReader(File fich) throws IOException
 {
  if(fich == null) 
  {
   throw new IOException("XMLReader <No File>");
  }
  file = fich;
 }
	
 /**
  * Read.
  *
  * @param dh the dh
  * @return the object
  * @throws IOException Signals that an I/O exception has occurred.
  * @throws SAXException the SAX exception
  */
 public Object read(XMLInputHandler dh) throws IOException,SAXException
 {
  SAXParser parser = null;
  SAXParserFactory factory = SAXParserFactory.newInstance();
   	   
  factory.setValidating(true);
  //factory.setNamespaceAware(true);
    	
  try 
  {
   parser = factory.newSAXParser();
  }
  catch(Exception e) 
  {
   throw new SAXException("newSAXParser()");
  }
  parser.parse(file,dh);
  return dh.getObject();
 }
	
 /**
  * Read.
  *
  * @param dh the dh
  * @throws IOException Signals that an I/O exception has occurred.
  * @throws SAXException the SAX exception
  */
 public void read(DefaultHandler dh) throws IOException,SAXException
 {
  SAXParser parser = null;
  SAXParserFactory factory = SAXParserFactory.newInstance();
	   	   
  factory.setValidating(true);
  //factory.setNamespaceAware(true);
	    	
  try 
  {
   parser = factory.newSAXParser();
  }
  catch(Exception e) 
  {
   throw new SAXException("newSAXParser()");
  }
  parser.parse(file,dh);
 }
}
