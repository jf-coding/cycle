package opbdevices.opbuartlite;

// ------------------------------
// Java Programming for Engineers
// Julio Sanchez
// Maria P. Canton
//
// ISBN: 0849308100
// Publisher: CRC Press
// ------------------------------

// ------------------------
// File name: MenuDemo.java
// Reference: Chapter 9
// ------------------------

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
  */
 public static void inputFlush() 
 {
  //int dummy;
  //int bAvail;

  try 
  {
   while((System.in.available()) != 0)
    // dummy = 
	 System.in.read();
  } 
  catch (java.io.IOException e) 
  {
   System.out.println("Input error");
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
  */
 public static String inString(String prompt) 
 {
  inputFlush();
  printPrompt(prompt);
  return inString();
 }

 /**
  * In string.
  *
  * @return the string
  */
 public static String inString() 
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
	System.out.println("Input error");
	finished = true;
   }
  }
  return s;
 }

 /**
  * In int.
  *
  * @param prompt the prompt
  * @return the int
  */
 public static int inInt(String prompt) 
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
	System.out.println("Invalid input. Not an integer");
   }
  }
 }

 /**
  * In char.
  *
  * @param prompt the prompt
  * @return the char
  */
 public static char inChar(String prompt) 
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
   System.out.println("Input error");
  }
  inputFlush();
  return (char) aChar;
 }

 /**
  * In double.
  *
  * @param prompt the prompt
  * @return the double
  */
 public static double inDouble(String prompt) 
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
	System.out.println("Invalid input. Not a floating point number");
   }
  }
 }
}
