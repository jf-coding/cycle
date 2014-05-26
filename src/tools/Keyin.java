package tools;

// ------------------------------
// Java Programming for Engineers
// Julio Sanchez
// Maria P. Canton
//
// ISBN: 0849308100
// Publisher: CRC Press
// ------------------------------

// ------------------------------
// File name: MenuDemo.java
// Reference: Chapter 9
// -------------------------------

// -------------------------------
// Modified by Joao Ferreira
// Date 04/05/2008
// -------------------------------

/**
 * The Keyin class. This class is only used to solve the problem with acquisition of the enter key in the terminal.
 */
public class Keyin 
{
 // ------------------------------------------
 // Method to display the user's prompt string
 // ------------------------------------------	
	
 /**
  * Prints the prompt.
  *
  * @param prompt the prompt
  */
 public static void printPrompt(String prompt) 
 {
  System.out.print(prompt);
  System.out.flush();
 }

 // ------------------------------------------------------------
 // Method to make sure no data is available in the input stream
 // ------------------------------------------------------------
  
 /**
  * Input flush.
  *
  * @throws Exception the Exception
  */
 public static void inputFlush() throws Exception 
 {
  try 
  {
   while((System.in.available()) != 0) 
	 System.in.read();
  } 
  catch (java.io.IOException e) 
  {
   throw new Exception();
  }
 }

 // ----------------------------------------------------
 // Data input methods for string, int, char, and double
 // ----------------------------------------------------
  
 /**
  * In string.
  *
  * @param prompt the prompt
  * @return the string
  * @throws Exception the Exception
  */
 public static String inString(String prompt) throws Exception 
 {
  inputFlush();
  printPrompt(prompt);
  return inString();
 }

 /**
  * In string.
  *
  * @return the string
  * @throws Exception the Exception
  */
 public static String inString() throws Exception 
 {
  int aChar;
  String s = "";
  boolean finished = false;

  while (!finished) 
  {
   try 
   {
    aChar = System.in.read();
	if(aChar < 0 || (char) aChar == '\n')
      finished = true;
	else if((char) aChar != '\r')
	  s = s + (char) aChar; // Enter into string
   }
   catch(java.io.IOException e) 
   {
	finished = true;
	throw new Exception();
   }
  }
  return s;
 }

 /**
  * In int.
  *
  * @param prompt the prompt
  * @return the int
  * @throws Exception the Exception
  */
 public static int inInt(String prompt) throws Exception 
 {
  while (true) 
  {
   inputFlush();
   printPrompt(prompt);
   try 
   {
	return Integer.valueOf(inString().trim()).intValue();
   }
   catch (NumberFormatException e) 
   {
	throw new Exception();
   }
  }
 }

 /**
  * In char.
  *
  * @param prompt the prompt
  * @return the char
  * @throws Exception the Exception
  */
 public static char inChar(String prompt) throws Exception 
 {
  int aChar = 0;

  inputFlush();
  printPrompt(prompt);

  try 
  {
   aChar = System.in.read();
  }
  catch (java.io.IOException e) 
  {
   throw new Exception();
  }
  inputFlush();
  return (char) aChar;
 }

 /**
  * In double.
  *
  * @param prompt the prompt
  * @return the double
  * @throws Exception the Exception
  */
 public static double inDouble(String prompt) throws Exception 
 {
  while (true) 
  {
   inputFlush();
   printPrompt(prompt);
   try 
   {
	return Double.valueOf(inString().trim()).doubleValue();
   }
   catch (NumberFormatException e) 
   {
	throw new Exception();
   }
  }
 }
}
