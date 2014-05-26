package tools;

import java.util.Map;

import system.Util;
import system.file.*;

/**
 * The executable and linking format (ELF) mode class. This class performs the executable and linking format (ELF) mode, only used for debug purposes
 */
public class Elf
{
 /** The elf binary file (path and name). */
 private String elfbinary_file; 	
	
 /**
  * Instantiates a new executable and linking format (ELF) mode, only used for debug purposes.
  *
  * @param elfbinary_file the elf binary file (path and name).
  */
 public Elf(String elfbinary_file)
 {
  this.elfbinary_file = elfbinary_file; 
 }

 // ----------------------------------------------------
 // method used to display information from the ELF file
 // ----------------------------------------------------
 
 /**
  * This method loads the elf binary file and displays its content.
  */
 public final void main()
 {
  ElfBinaryParser elf_binary_parser;
  Map<Integer,String> function_name;
  Map<Integer,Long> function_begin;
  Map<Integer,Long> function_end;
  int aux;
  
  elf_binary_parser = new ElfBinaryParser();
  try
  {
   elf_binary_parser.load(elfbinary_file,0);
   System.out.println("");
   elf_binary_parser.showHeader();
   
   tab();
   System.out.println("");
   elf_binary_parser.showSize();
   
   tab();
   System.out.println("");
   function_name = elf_binary_parser.getFunction_name();
   function_begin = elf_binary_parser.getFunction_begin();
   function_end = elf_binary_parser.getFunction_end();
   System.out.println("Functions");
   System.out.println("");
   System.out.println(" " + Util.toStringSpace("from",10) + "  " + Util.toStringSpace("to",10) + "  " + Util.toStringSpace("size",12) + "  " + "function");
   for(aux = 0;aux < function_name.size();aux++)
 	System.out.println(" " +"0x" + Util.toHexString(function_begin.get(aux),8) + "  0x" + Util.toHexString(function_end.get(aux),8) + "  " + Util.toDecStringSpace(function_end.get(aux) - function_begin.get(aux) + 1,6) + " bytes  " + function_name.get(aux));
   
   tab();
   System.out.println("");
   System.out.println("Function <exit>");
   System.out.println("");
   if(elf_binary_parser.getFunction_exit_address() == -1)
	 System.out.println(" " + ". not found");
   else
	 System.out.println(" " + ". address: 0x" + Util.toHexString((int)elf_binary_parser.getFunction_exit_address(),8));
  }
  catch(ElfBinaryParserException e)
  {
   System.out.println(""); 
   System.out.println(" " + "!Warning: " + e.getMessage());  
  }
  System.out.println("");
  System.exit(0);
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
