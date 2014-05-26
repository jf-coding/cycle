package system;

/**
 * The utilities class. This class implements methods to manipulate strings and conversions between formats types.
 */
public class Util 
{
 // --------------------------------------------------------------
 // methods used for conversion to hexadecimal base in string form
 // --------------------------------------------------------------
	
 /**
  * Converts a long literal to hexadecimal string, the parameter maximum number define the number of characters that string will have. If the string converted doesn't have the necessary number of characters they are filled with the space character in the left side of the string converted.  
  *
  * @param number the number to be converted.
  * @param character the number of character that string returned will have.
  * @return the string converted.
  */
 public static String toHexStringSpace(long number,int character) 
 {
  String aux;
  
  aux = Long.toHexString(number);
  if(aux.length() > character)
	return aux.substring(aux.length() - character);
  else if(aux.length() == character)
	return aux;
  else
  {
   while(aux.length() < character) 
     aux = " " + aux;
   return aux;
  }
 }	
	
 /**
  * Converts a long literal to hexadecimal string, the parameter maximum number define the number of characters that string will have. If the string converted doesn't have the necessary number of characters they are filled with the zero character in the left side of the string converted.
  *
  * @param number number the number to be converted.
  * @param character the number of character that string returned will have.
  * @return the string converted.
  */
 public static String toHexString(long number,int character) 
 {
  String aux;
  
  aux = Long.toHexString(number);
  if(aux.length() > character)
    return aux.substring(aux.length() - character);
  else if(aux.length() == character)
	return aux;
  else
  {
   while(aux.length() < character) 
     aux = "0" + aux;
   return aux;
  }
 }
	 
 /**
  * Converts a int literal to hexadecimal string, the parameter maximum number define the number of characters that string will have. If the string converted doesn't have the necessary number of characters they are filled with the zero character in the left side of the string converted.
  *
  * @param number number the number to be converted.
  * @param character the number of character that string returned will have.
  * @return the string converted.
  */
 public static String toHexString(int number,int character) 
 {
  String aux;
  
  aux = Integer.toHexString(number);
  if(aux.length() > character)
	return aux.substring(aux.length() - character);
  else if(aux.length() == character)
	return aux;
  else
  {
   while(aux.length() < character) 
     aux = "0" + aux;
   return aux;
  }
 }

 /**
  * Converts a byte literal to hexadecimal string, the parameter maximum number define the number of characters that string will have. If the string converted doesn't have the necessary number of characters they are filled with the zero character in the left side of the string converted.
  *
  * @param number number the number to be converted.
  * @param character the number of character that string returned will have.
  * @return the string converted.
  */
 public static String toHexString(byte number,int character) 
 {
  String aux;
  
  aux = Integer.toHexString((int) number & 0xFF);
  if(aux.length() > character)
	return aux.substring(aux.length() - character);
  else if(aux.length() == character)
	return aux;
  else
  {
   while(aux.length() < character) 
     aux = "0" + aux;
   return aux;
  }
 }

 // ----------------------------------------------------------
 // methods used for conversion to decimal base in string form
 // ----------------------------------------------------------
 
 /**
  * Converts a float literal to decimal string, the parameter maximum number define the number of characters that string will have. If the string converted doesn't have the necessary number of characters they are filled with the space character in the left side of the string converted.
  *
  * @param number number the number to be converted.
  * @param character the number of character that string returned will have.
  * @return the string converted.
  */
 public static String toDecStringSpace(float number,int character) 
 {
  String aux;
  
  aux = "" + number;
  if(aux.length() > character)
	return aux.substring(aux.length() - character);
  else if(aux.length() == character)
	return aux;
  else
  {
   while(aux.length() < character) 
     aux = " " + aux;
   return aux;
  }
 }
 
 /**
  * Converts a long literal to decimal string, the parameter maximum number define the number of characters that string will have. If the string converted doesn't have the necessary number of characters they are filled with the space character in the left side of the string converted.
  *
  * @param number number the number to be converted.
  * @param character the number of character that string returned will have.
  * @return the string converted.
  */
 public static String toDecStringSpace(long number,int character) 
 {
  String aux;
  
  aux = "" + number;
  if(aux.length() > character)
	return aux.substring(aux.length() - character);
  else if(aux.length() == character)
	return aux;
  else
  {
   while(aux.length() < character) 
     aux = " " + aux;
   return aux;
  }
 }
 
 /**
  * Converts a long literal to decimal string, the parameter maximum number define the number of characters that string will have. If the string converted doesn't have the necessary number of characters they are filled with the zero character in the left side of the string converted.
  *
  * @param number number the number to be converted.
  * @param character the number of character that string returned will have.
  * @return the string converted.
  */
 public static String toDecStringZero(long number,int character) 
 {
  String string_number;
  String aux;

  aux = "";
  string_number = "" + number;
  if(string_number.length() > character)
    return aux.substring(string_number.length() - character);
  else if(string_number.length() == character)
	return string_number;
  else
  {
   while(string_number.length() + aux.length() < character) 
     aux = "0" + aux;
   return aux + number;
  }
 }
 
 /**
  * Converts a int literal to decimal string, the parameter maximum number define the number of characters that string will have. If the string converted doesn't have the necessary number of characters they are filled with the zero character in the left side of the string converted.
  *
  * @param number number the number to be converted.
  * @param character the number of character that string returned will have.
  * @return the string converted.
  */
 public static String toDecStringZero(int number,int character) 
 {
  String string_number;
  String aux;

  aux = "";
  string_number = "" + number;
  if(string_number.length() > character)
	return aux.substring(string_number.length() - character);
  else if(string_number.length() == character)
	return string_number;
  else
  {
   while(string_number.length() + aux.length() < character) 
     aux = "0" + aux;
   return aux + number;
  }
 }
 
 // ---------------------------------------------------------
 // methods used for conversion to binary base in string form
 // ---------------------------------------------------------
 
 /**
  * Converts a int literal to binary string, the parameter maximum number define the number of characters that string will have. If the string converted doesn't have the necessary number of characters they are filled with the zero character in the left side of the string converted.
  *
  * @param number number the number to be converted.
  * @param character the number of character that string returned will have.
  * @return the string converted.
  */
 public static String toBinString(int number,int character) 
 {
  String aux;
  
  aux = Integer.toBinaryString(number);
  if(aux.length() > character)
    return aux.substring(aux.length() - character);
  else if(aux.length() == character)
	return aux;
  else
  {
   while(aux.length() < character) 
     aux = "0" + aux;
   return aux;
  }
 }
 
 // --------------------------------
 // methods for manipulating strings
 // --------------------------------
 
 /**
  * Converts a string to a string with the maximum number of characters give by the value of parameter number, if the string doesn't have the number of characters necessary they will filled with space character in the right side.
  *
  * @param string number the number to be converted.
  * @param character the number of character that string returned will have.
  * @return the string converted.
  */
 public static String toStringFixed(String string,int character)
 {
  if(string.length() < character)
  {
   while((string.length()) < character) 
     string = string + " ";
  }  
  else if(string.length() > character)
  {
   return string.substring(0,character);	 
  }
  return string;
 }

 /**
  * Converts a string to a string with the maximum number of characters give by the value of parameter number, if the string doesn't have the number of characters necessary they will filled with space character in the left side.
  *
  * @param string number the number to be converted.
  * @param character the number of character that string returned will have.
  * @return the string converted.
  */
 public static String toStringSpace(String string,int character) 
 {
  String aux;
  
  aux = "";
  if(string.length() > character)
	return string.substring(string.length() - character);
  else if(string.length() == character)
	return string;
  else
  {
   while((string.length() + aux.length()) < character) 
     aux = " " + aux;
   return aux + string;
  }
 }
 
 // -----------------------------------------
 // methods for manipulating ASCII characters
 // -----------------------------------------
 
 /**
  * Converts a int literal to a string with ascii characters.
  *
  * @param number number the number to be converted.
  * @return the string converted.
  */
 public static String toASCIIChar(int number)
 { 
  return "" + (char)(number >> 24) + (char)((number >> 16) & 0x00ff) + (char)((number >> 8) & 0x0000ff) + (char)(number & 0x000000ff) ;
 }
 
 /**
  * Converts a int literal to a string with visible ascii characters.
  *
  * @param number number the number to be converted.
  * @return the string converted.
  */
 public static String toASCIIVisible(int number)
 {
  String string_aux;
  byte byte_aux;
  int aux;
	 
  string_aux = "";
  for(aux = 0;aux < 4;aux++)
  {
   byte_aux = (byte)(number >> (24 - (8 * aux)));
   if(byte_aux < 0x20)
	 string_aux = string_aux + ".";
   else
	 string_aux = string_aux + (char)byte_aux;
  }
  return string_aux;
 }
 
 /**
  * Converts a int literal to a visible ascii char.
  *
  * @param number number the number to be converted.
  * @return the char converted.
  */
 public static char toASCIIVisible(byte number)
 {
  if(number < 0x20)
	return '.';
  else
	return (char)number;
 }
}

