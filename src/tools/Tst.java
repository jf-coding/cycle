package tools;

import java.text.*;
import java.util.*;
import java.io.*;


import system.memory.*;
import system.file.*;
import system.cpu.*;
import system.*;

/**
 * The test mode class. This class performs the test mode, only used for debug purposes.
 */
public class Tst
{
 /** The input buffered reader. */
 private BufferedReader in;
 /** The system XML configuration file (path and name). */
 private String systemconfig_file;
 /** The profiler (performance analysis) output file (path and name). */
 private String profile_file;
 /** The instruction trace output file (path and name). */
 private String trace_file;
 /** The elf binary file (path and name). */
 private String binary_file;
 /** The system. */
 private SysteM system;
 /** The status of the system. */
 private int status;
 /** The simulation flag.<p> 
  *  false - not operating in simulation mode.<br>
  *  true - operating in simulation mode.
  */
 private boolean simulation;
 /** The profiler (performance analysis) flag.<p> 
  *  false - not operating in profiler (performance analysis) mode.<br>
  *  true - operating in profiler (performance analysis) mode.
  */
 private boolean profile;
 /** The instruction trace flag.<p> 
  *  false - not operating in instruction trace mode.<br>
  *  true - operating in instruction trace mode.
  */
 private boolean trace;
 /** The simulation time. The time spent by the computer to perform the simulation. */
 private float time_simulation;
 /** The real time. The time spent by the real system to execute the software. */
 private float time_real;
 /** The ratio value between the simulation time and the real time. */
 private float ratio;
 /** The system frequency value. */
 private float freq;
 /** The begin time. When starts counting the simulation time. */
 private long time_begin;
 /** The end time. When stops counting the simulation time. */
 private long time_end;
 /** The cycles per instruction value of the simulation. */
 private float cpi;
 /** The number of cells in a frame. */
 private long number_cells_view = 400;
 /** The cells bytes in a frame. */
 private long cells_bytes = number_cells_view * 4;
 /** The number of adjacent positions. */
 private long dump_number_elements = 10;
 /** The highest frame. */
 private long dump_frame_high;
 /** The lowest frame. */
 private long dump_frame = -1;
 /** The highest memory address. Used to calculate the frame */
 private long dump_high;
 /** The lowest memory address. Used to calculate the frame. */
 private long dump_low;
 /** The number of pipeline stages. */
 private int pipeline_stages;
 /** The execute stage number. */
 private int execute_stage;
 
 /**
  * Instantiates a new test mode, only used for debug purposes.
  *
  * @param systemconfig_file the XML system configuration file (path and name).
  * @param profile_file the profiler (performance analysis) file (path and name). Where the profile results of the simulation will be saved.
  * @param trace_file the instruction trace file (path and name). Where the results of the instruction trace simulation will be saved.
  * @param binary_file the elf binary file (path and name).
  * @param events the cpu events flag.<p>
  * false - doesn't notify if any cpu events occur.<br>
  * true - notify if any cpu events occur.
  */
 public Tst(String systemconfig_file,String profile_file,String trace_file,String binary_file,boolean events)
 {
  File file;
  
  try
  {
   this.systemconfig_file = systemconfig_file;
   this.profile_file = profile_file;
   this.trace_file = trace_file;
   this.binary_file = binary_file;	
   file = new File(binary_file);
   binary_file = file.getCanonicalPath();  
   in = new BufferedReader(new InputStreamReader(System.in));
   system = new SysteM(systemconfig_file,binary_file,events,false);
   pipeline_stages = system.getPipeline_stages();
   execute_stage = system.getExecute_stage();
   freq = system.getFrequency();
  }
  catch(SysteMException e)
  {
   System.out.println("");
   System.out.println(" " + "!Warning: " + e.getMessage());
   System.out.println("");
   System.exit(0);
  }
  catch(IOException e) 
  {
   System.out.println("");
   System.out.println(" " + "!Warning: " + e.getMessage());
   System.out.println("");
   System.exit(0);
  }	 
 }
 
 // -----------------------------
 // methods used in the main menu
 // -----------------------------
 
 /**
  * Main menu.
  */
 public final void main()
 {
  int selection;
  
  for(;;)
  {
   try
   {
	tab();
    System.out.println("Test Menu - [Main]");
    System.out.println("");
    System.out.println(Util.toStringFixed("[1] - Memory",25)     + " " + Util.toStringFixed("[2] - CPU",25)     + " " + "[3] - File");
    System.out.println(Util.toStringFixed("[4] - Simulation",25) + " " + Util.toStringFixed("[5] - Profile",25) + " " + "[6] - Trace");
    System.out.println(Util.toStringFixed("[7] - ",25)           + " " + Util.toStringFixed("[8] - ",25)        + " " + "[9] - Exit");
    selection = Keyin.inInt("> ");
    switch(selection)
    {
     case 1:menu_memory();
            break;
     case 2:menu_cpu();
            break;
     case 3:menu_file();
            break;
     case 4:menu_simulation();
            break;
     case 5:menu_profile();
            break;
     case 6:menu_trace();
            break;
     case 9:System.exit(0);
     default:tab();
    	     System.out.println(" " + "!Warning: wrong option.");
    }
   }
   catch(Exception e)
   {
	tab();
    System.out.println(" " + "!Warning: wrong option.");
   }
  }
 }
 
 // -----------------------------------------
 // methods used to access the memory content 
 // -----------------------------------------

 /**
  * System memory menu.
  */
 private final void menu_memory()
 {
  int selection;
  
  for(;;)
  {
   try
   {
	tab();
	System.out.println("Test Menu - [Memory]");
    System.out.println("");
    System.out.println(Util.toStringFixed("[1] - All (decimal signed)",26) + " " + Util.toStringFixed("[2] - All (decimal unsigned)",28) + " " + "[3] - ");
    System.out.println(Util.toStringFixed("[4] - One (decimal signed)",26) + " " + Util.toStringFixed("[5] - One (decimal unsigned)",28) + " " + "[6] - ");
    System.out.println(Util.toStringFixed("[7] - ",26)                     + " " + Util.toStringFixed("[8] - ",28)                       + " " + "[9] - Back");
    selection = Keyin.inInt("> ");
    switch(selection)
    {
     case 1:menu_memory_all_signed();
    	    break;
     case 2:menu_memory_all_unsigned();
	        break;
     case 4:menu_memory_one_signed();
    	    break;
     case 5:menu_memory_one_unsigned();
	        break;
     case 9:main();
    	    break;
     default:tab();
    	     System.out.println(" " + "!Warning: wrong option.");
    }
   }
   catch(Exception e)
   {
	tab();
    System.out.println(" " + "!Warning: wrong option.");
   }
  }
 }

 /**
  * System memory menu, the contents of the system memory by frames, the decimal numeric system is in signed format.  
  */
 private final void menu_memory_all_signed()
 {
  int selection;
  
  for(;;)
  {
   try
   {
	tab();
    System.out.println("Test Menu - [Memory, All (decimal signed)]");
    System.out.println("");
    System.out.println(Util.toStringFixed("[1] - Up",25) + " " + Util.toStringFixed("[2] - Down",25) + " " + "[3] - ");
	System.out.println(Util.toStringFixed("[4] - ",25)   + " " + Util.toStringFixed("[5] - ",25)     + " " + "[6] - ");
	System.out.println(Util.toStringFixed("[7] - ",25)   + " " + Util.toStringFixed("[8] - ",25)     + " " + "[9] - Back");
	selection = Keyin.inInt("> ");
    switch(selection)
	{
	 case 1:tab();
	        showAll_signed(true);
	        break;
	 case 2:tab();
	        showAll_signed(false);
	    	break;
     case 9:menu_memory();
            break;
	 default:tab();
	         System.out.println(" " + "!Warning: wrong option.");
    }
   }
   catch(Exception e)
   {
	tab();
    System.out.println(" " + "!Warning: wrong option.");
   }
  } 
 }
 
 /**
  * System memory menu, the contents of the system memory by frames, the decimal numeric system is in unsigned format.
  */
 private final void menu_memory_all_unsigned()
 {
  int selection;
  
  for(;;)
  {
   try
   {
	tab();
    System.out.println("Test Menu - [Memory, All (decimal unsigned)]");
    System.out.println("");
    System.out.println(Util.toStringFixed("[1] - Up",25) + " " + Util.toStringFixed("[2] - Down",25) + " " + "[3] - ");
	System.out.println(Util.toStringFixed("[4] - ",25)   + " " + Util.toStringFixed("[5] - ",25)     + " " + "[6] - ");
	System.out.println(Util.toStringFixed("[7] - ",25)   + " " + Util.toStringFixed("[8] - ",25)     + " " + "[9] - Back");
	selection = Keyin.inInt("> ");
    switch(selection)
	{
	 case 1:tab();
	        showAll_unsigned(true);
	        break;
	 case 2:tab();
	        showAll_unsigned(false);
	    	break;
     case 9:menu_memory();
            break;
	 default:tab();
	         System.out.println(" " + "!Warning: wrong option.");
    }
   }
   catch(Exception e)
   {
	tab();
    System.out.println(" " + "!Warning: wrong option.");
   }
  } 
 }
 
 /**
  * System memory menu, the content of the selected memory address and of adjacent memory addresses, the decimal numeric system is in signed format. 
  */
 private final void menu_memory_one_signed()
 {
  int selection;
  
  for(;;)
  {
   try
   {
    tab();
    System.out.println("Test Menu - [Memory, One (decimal signed)]");
    System.out.println("");
    System.out.println(Util.toStringFixed("[1] - Dec",25) + " " + Util.toStringFixed("[2] - Hex",25) + " " + "[3] - ");
    System.out.println(Util.toStringFixed("[4] - ",25)    + " " + Util.toStringFixed("[5] - ",25)    + " " + "[6] - ");
    System.out.println(Util.toStringFixed("[7] - ",25)    + " " + Util.toStringFixed("[8] - ",25)    + " " + "[9] - Back");
    selection = Keyin.inInt("> ");
    switch(selection)
    {
	 case 1:menu_memory_one_dec_signed();
			break;
	 case 2:menu_memory_one_hex_signed();
			break;
     case 9:menu_memory();
		    break;    
	 default:tab();
	        System.out.println(" " + "!Warning: wrong option.");
	}
   }
   catch(Exception e)
   {
    tab();
	System.out.println(" " + "!Warning: wrong option.");
   }
  }   
 }

 /**
  * System memory menu, the content of the selected memory address and of adjacent memory addresses, the decimal numeric system is in signed format. The memory address select by the user is in decimal format. 
  */
 private final void menu_memory_one_dec_signed()
 {
  long address;
  
  try
  {
   tab();
   System.out.println("Test Menu - [Memory, One (decimal signed)]");
   System.out.println("");
   System.out.println("Insert the address in decimal base.");
   System.out.print("> ");
   System.out.flush();
   address = Long.parseLong(input().readLine(),10);
   tab();
   showOne_signed(address);
  }
  catch(Exception e)
  {
   tab();
	System.out.println(" " + "!Warning: the address must be a decimal number.");
  }
 }
 
 /**
  * System memory menu, the content of the selected memory address and of adjacent memory addresses, the decimal numeric system is in signed format. The memory address select by the user is in hexadecimal format.
  */
 private final void menu_memory_one_hex_signed()
 {
  long address;
  
  try
  {
   tab();
   System.out.println("Test Menu - [Memory, One (decimal signed)]");
   System.out.println("");
   System.out.println("Insert the address in hexadecimal base.");
   System.out.print("> ");
   System.out.flush();
   address = Long.parseLong(input().readLine(),16);
   tab();
   showOne_signed(address);
  }
  catch(Exception e)
  {
   tab();
   System.out.println(" " + "!Warning: the address must be a hexadecimal number.");
  }
 }
 
 /**
  * System memory menu, the content of the selected memory address and of adjacent memory addresses, the decimal numeric system is in unsigned format. 
  */
 private final void menu_memory_one_unsigned()
 {
  int selection;
  
  for(;;)
  {
   try
   {
    tab();
    System.out.println("Test Menu - [Memory, One (decimal unsigned)]");
    System.out.println("");
    System.out.println(Util.toStringFixed("[1] - Dec",25) + " " + Util.toStringFixed("[2] - Hex",25) + " " + "[3] - ");
    System.out.println(Util.toStringFixed("[4] - ",25)    + " " + Util.toStringFixed("[5] - ",25)    + " " + "[6] - ");
    System.out.println(Util.toStringFixed("[7] - ",25)    + " " + Util.toStringFixed("[8] - ",25)    + " " + "[9] - Back");
    selection = Keyin.inInt("> ");
    switch(selection)
    {
	 case 1:menu_memory_one_dec_unsigned();
			break;
	 case 2:menu_memory_one_hex_unsigned();
			break;
     case 9:menu_memory();
		    break;    
	 default:tab();
	        System.out.println(" " + "!Warning: wrong option.");
	}
   }
   catch(Exception e)
   {
    tab();
	System.out.println(" " + "!Warning: wrong option.");
   }
  }   
 }

 /**
  * System memory menu, the content of the selected memory address and of adjacent memory addresses, the decimal numeric system is in unsigned format. The memory address select by the user is in decimal format.  
  */
 private final void menu_memory_one_dec_unsigned()
 {
  long address;
  
  try
  {
   tab();
   System.out.println("Test Menu - [Memory, One (decimal unsigned)]");
   System.out.println("");
   System.out.println("Insert the address in decimal base.");
   System.out.print("> ");
   System.out.flush();
   address = Long.parseLong(input().readLine(),10);
   tab();
   showOne_unsigned(address);
  }
  catch(Exception e)
  {
   tab();
	System.out.println(" " + "!Warning: the address must be a decimal number.");
  }
 }
 
 /**
  * System memory menu, the content of the selected memory address and of adjacent memory addresses, the decimal numeric system is in unsigned format. The memory address select by the user is in hexadecimal format. 
  */
 private final void menu_memory_one_hex_unsigned()
 {
  long address;
  
  try
  {
   tab();
   System.out.println("Test Menu - [Memory, One (decimal unsigned)]");
   System.out.println("");
   System.out.println("Insert the address in hexadecimal base.");
   System.out.print("> ");
   System.out.flush();
   address = Long.parseLong(input().readLine(),16);
   tab();
   showOne_unsigned(address);
  }
  catch(Exception e)
  {
   tab();
   System.out.println(" " + "!Warning: the address must be a hexadecimal number.");
  }
 }
 
 /**
  * Displays the contents of the system memory by frames, the decimal numeric system is in signed format.
  *
  * @param up the up flag.<p>
  * false - down a frame.<br>
  * true - up a frame.
  */
 private final void showAll_signed(boolean up)
 {
  Instruction instruction;
  long initial_cell;
  long final_cell;
  Integer value;
  long aux;
  
  dump_low  = 0x0;
  dump_high = 0xffffffffL;
  if((dump_high - dump_low) % cells_bytes == 0)
    dump_frame_high = (dump_high - dump_low + 1) / cells_bytes;
  else
	dump_frame_high = (dump_high - dump_low + 1) / cells_bytes + 1;
  System.out.println("Memory Dump");
  System.out.println("");
  if(up)
  {
   if(dump_frame < 0)
   {	
	System.out.println("Nothing to show, memory out of range !");
	return;
   }
   dump_frame--;
   if(dump_frame < 0)
   {
	System.out.println("Nothing to show, memory out of range !");
	return;
   }
  }
  else 
  {
   if(dump_frame >= dump_frame_high)
   {
	System.out.println("Nothing to show, memory out of range !");
	return;
   }
   dump_frame++;
   if(dump_frame >= dump_frame_high)
   {
	System.out.println("Nothing to show, memory out of range !");
	return;
   }	
  }
  initial_cell = cells_bytes * (dump_frame) + dump_low;
  final_cell = cells_bytes * (dump_frame + 1) + dump_low;
  for(aux = initial_cell;aux < final_cell;aux = aux + 4)
  {
   if(aux >= dump_high)
	 continue; 
   try
   {
    value = system.getMemoryClass().getMemoryWord((int)aux);
    instruction = system.getMemoryClass().getMemoryInstruction((int)aux);
    System.out.println("[0x" + Util.toHexString(aux,8) + "/" + Util.toDecStringZero(aux,10) + "] 0x" + Util.toHexString(value,8) + " " + Util.toDecStringSpace(value,11) + " " + Util.toASCIIVisible(value) + " <0x" + Util.toHexString(instruction.getPC(),8) + "> " + system.getCPUClass().getInstructionSetArchitectureClass().toString(value));
   }
   catch(Exception e)
   {
    System.out.println("[0x" + Util.toHexString(aux,8) + "/" + Util.toDecStringZero(aux,10) + "]" );
   }
  }		 
 } 

 /**
  * Displays the contents of the system memory by frames, the decimal numeric system is in unsigned format.
  *
  * @param up the up flag.<p>
  * false - down a frame.<br>
  * true - up a frame.
  */
 private final void showAll_unsigned(boolean up)
 {
  Instruction instruction;
  long initial_cell;
  long final_cell;
  Integer value;
  long aux;
  
  dump_low  = 0x0;
  dump_high = 0xffffffffL;
  if((dump_high - dump_low) % cells_bytes == 0)
    dump_frame_high = (dump_high - dump_low + 1) / cells_bytes;
  else
	dump_frame_high = (dump_high - dump_low + 1) / cells_bytes + 1;
  System.out.println("Memory Dump");
  System.out.println("");
  if(up)
  {
   if(dump_frame < 0)
   {	
	System.out.println("Nothing to show, memory out of range !");
	return;
   }
   dump_frame--;
   if(dump_frame < 0)
   {
	System.out.println("Nothing to show, memory out of range !");
	return;
   }
  }
  else 
  {
   if(dump_frame >= dump_frame_high)
   {
	System.out.println("Nothing to show, memory out of range !");
	return;
   }
   dump_frame++;
   if(dump_frame >= dump_frame_high)
   {
	System.out.println("Nothing to show, memory out of range !");
	return;
   }	
  }
  initial_cell = cells_bytes * (dump_frame) + dump_low;
  final_cell = cells_bytes * (dump_frame + 1) + dump_low;
  for(aux = initial_cell;aux < final_cell;aux = aux + 4)
  {
   if(aux >= dump_high)
	 continue; 
   try
   {
    value = system.getMemoryClass().getMemoryWord((int)aux);
    instruction = system.getMemoryClass().getMemoryInstruction((int)aux);
    System.out.println("[0x" + Util.toHexString(aux,8) + "/" + Util.toDecStringZero(aux,10) + "] 0x" + Util.toHexString(value,8) + " " + Util.toDecStringSpace((long)value & 0xffffffffL,11) + " " + Util.toASCIIVisible(value) + " <0x" + Util.toHexString(instruction.getPC(),8) + "> " + system.getCPUClass().getInstructionSetArchitectureClass().toString(value));
   }
   catch(Exception e)
   {
    System.out.println("[0x" + Util.toHexString(aux,8) + "/" + Util.toDecStringZero(aux,10) + "]" );
   }
  }		 
 } 
 
 /**
  * Displays the content of the selected memory address and of adjacent memory addresses, the decimal numeric system is in signed format. The attribute value dump_number_elements determines the number of adjacent positions.
  * @param address the memory address to be displayed.
  */
 private final void showOne_signed(long address)
 {
  long number_to_show = dump_number_elements * 4;
  Instruction instruction;
  Integer value;
  long aux;
   
  if(address % 4 != 0)
  {
   System.out.println(" " + "!Warning:  the address is not an ligned memory word.");
   return;
  }
  if(address < 0 || address > 0xffffffffL)
  {
   System.out.println(" " + "!Warning:  the address is out of range.");
   return;
  }
  System.out.println("Memory Dump");
  System.out.println("");
  for(aux = address - number_to_show;aux < address;aux = aux + 4)
  {
   if(aux < 0)
	 continue;
   try
   {
    value = system.getMemoryClass().getMemoryWord((int)aux);
    instruction = system.getMemoryClass().getMemoryInstruction((int)aux);
    System.out.println("[0x" + Util.toHexString(aux,8) + "/" + Util.toDecStringZero(aux,10) + "] 0x" + Util.toHexString(value,8) + " " + Util.toDecStringSpace(value,11) + " " + Util.toASCIIVisible(value) + " <0x" + Util.toHexString(instruction.getPC(),8) + "> " + system.getCPUClass().getInstructionSetArchitectureClass().toString(value));
   }
   catch(Exception e)
   {
    System.out.println("[0x" + Util.toHexString(aux,8) + "/" + Util.toDecStringZero(aux,10) + "]" );
   }   
  }
  System.out.println("");
  try
  {
   value = system.getMemoryClass().getMemoryWord((int)address);
   instruction = system.getMemoryClass().getMemoryInstruction((int)address);
   System.out.println("[0x" + Util.toHexString(aux,8) + "/" + Util.toDecStringZero(aux,10) + "] 0x" + Util.toHexString(value,8) + " " + Util.toDecStringSpace(value,11) + " " + Util.toASCIIVisible(value) + " <0x" + Util.toHexString(instruction.getPC(),8) + "> " + system.getCPUClass().getInstructionSetArchitectureClass().toString(value));
  }
  catch(Exception e)
  {
   System.out.println("[0x" + Util.toHexString(address,8) + "/" + Util.toDecStringZero(address,10) + "]" );
  }
  System.out.println("");
  for(aux = address + 4;aux < address + number_to_show + 4;aux = aux + 4)
  {
   if(aux >= 0xffffffffL)
	 continue;
   try
   {
    value = system.getMemoryClass().getMemoryWord((int)aux);
    instruction = system.getMemoryClass().getMemoryInstruction((int)aux);
    System.out.println("[0x" + Util.toHexString(aux,8) + "/" + Util.toDecStringZero(aux,10) + "] 0x" + Util.toHexString(value,8) + " " + Util.toDecStringSpace(value,11) + " " + Util.toASCIIVisible(value) + " <0x" + Util.toHexString(instruction.getPC(),8) + "> " + system.getCPUClass().getInstructionSetArchitectureClass().toString(value));
   }
   catch(Exception e)
   {
	System.out.println("[0x" + Util.toHexString(aux,8) + "/" + Util.toDecStringZero(aux,10) + "]" );
   }
  }
 }
  
 /**
  * Displays the content of the selected memory address and of adjacent memory addresses, the decimal numeric system is in unsigned format. The attribute value dump_number_elements determines the number of adjacent positions.
  * @param address the memory address to be displayed.
  */
 private final void showOne_unsigned(long address)
 {
  long number_to_show = dump_number_elements * 4;
  Instruction instruction;
  Integer value;
  long aux;
   
  if(address % 4 != 0)
  {
   System.out.println(" " + "!Warning:  the address is not an ligned memory word.");
   return;
  }
  if(address < 0 || address > 0xffffffffL)
  {
   System.out.println(" " + "!Warning:  the address is out of range.");
   return;
  }
  System.out.println("Memory Dump");
  System.out.println("");
  for(aux = address - number_to_show;aux < address;aux = aux + 4)
  {
   if(aux < 0)
	 continue;
   try
   {
    value = system.getMemoryClass().getMemoryWord((int)aux);
    instruction = system.getMemoryClass().getMemoryInstruction((int)aux);
    System.out.println("[0x" + Util.toHexString(aux,8) + "/" + Util.toDecStringZero(aux,10) + "] 0x" + Util.toHexString(value,8) + " " + Util.toDecStringSpace((long)value & 0xffffffffL,11) + " " + Util.toASCIIVisible(value) + " <0x" + Util.toHexString(instruction.getPC(),8) + "> " + system.getCPUClass().getInstructionSetArchitectureClass().toString(value));
   }
   catch(Exception e)
   {
    System.out.println("[0x" + Util.toHexString(aux,8) + "/" + Util.toDecStringZero(aux,10) + "]" );
   }   
  }
  System.out.println("");
  try
  {
   value = system.getMemoryClass().getMemoryWord((int)address);
   instruction = system.getMemoryClass().getMemoryInstruction((int)address);
   System.out.println("[0x" + Util.toHexString(aux,8) + "/" + Util.toDecStringZero(aux,10) + "] 0x" + Util.toHexString(value,8) + " " + Util.toDecStringSpace((long)value & 0xffffffffL,11) + " " + Util.toASCIIVisible(value) + " <0x" + Util.toHexString(instruction.getPC(),8) + "> " + system.getCPUClass().getInstructionSetArchitectureClass().toString(value));
  }
  catch(Exception e)
  {
   System.out.println("[0x" + Util.toHexString(address,8) + "/" + Util.toDecStringZero(address,10) + "]" );
  }
  System.out.println("");
  for(aux = address + 4;aux < address + number_to_show + 4;aux = aux + 4)
  {
   if(aux >= 0xffffffffL)
	 continue;
   try
   {
    value = system.getMemoryClass().getMemoryWord((int)aux);
    instruction = system.getMemoryClass().getMemoryInstruction((int)aux);
    System.out.println("[0x" + Util.toHexString(aux,8) + "/" + Util.toDecStringZero(aux,10) + "] 0x" + Util.toHexString(value,8) + " " + Util.toDecStringSpace((long)value & 0xffffffffL,11) + " " + Util.toASCIIVisible(value) + " <0x" + Util.toHexString(instruction.getPC(),8) + "> " + system.getCPUClass().getInstructionSetArchitectureClass().toString(value));
   }
   catch(Exception e)
   {
	System.out.println("[0x" + Util.toHexString(aux,8) + "/" + Util.toDecStringZero(aux,10) + "]" );
   }
  }
 }
 
 // ------------------------------------------
 // methods used to access the cpu information
 // ------------------------------------------
 
 /**
  * Processor menu.
  */
 private final void menu_cpu() 
 {
  int selection;
  
  for(;;)
  {
   try
   {
    tab();
    System.out.println("Test Menu - [CPU]");
    System.out.println("");
    System.out.println(Util.toStringFixed("[1] - Description",26)          + " " + Util.toStringFixed("[2] - Pipeline",28)               + " " + "[3] - Status");
    System.out.println(Util.toStringFixed("[4] - General hex",26)          + " " + Util.toStringFixed("[5] - General bin",28)            + " " + "[6] - Special");
    System.out.println(Util.toStringFixed("[7] - General dec (signed)",26) + " " + Util.toStringFixed("[8] - General dec (unsigned)",28) + " " + "[9] - Back");
    selection = Keyin.inInt("> ");
    switch(selection)
    {
     case 1:tab();
            showCPU();
            break;
     case 2:tab();
            showCPUStatus();
            showPipeline();
            break;
     case 3:tab();
            System.out.println("Status Register");
            System.out.println("");
            showStatus();
            break;
     case 4:tab();
            System.out.println("General Register Hexadecimal");
            System.out.println("");
            showGeneralHex();
            break;
     case 5:tab();
            System.out.println("General Register Binary");
            System.out.println("");
            showGeneralBin();
            break;       
     case 6:tab();
            System.out.println("Special Register");
            System.out.println("");
            showSpecial();
            break;  
     case 7:tab();
            System.out.println("General Register Decimal (signed)");
            System.out.println("");
    	    showGeneralDec_signed();
            break;
     case 8:tab();
            System.out.println("General Register Decimal (unsigned)");
            System.out.println("");
	        showGeneralDec_unsigned();
            break;
     case 9:main();
    	    break;    
     default:tab();
    	     System.out.println(" " + "!Warning: wrong option.");
    }
   }
   catch(Exception e)
   {
    tab();
	System.out.println(" " + "!Warning: wrong option.");
   }
  }
 }
 
 /**
  * Displays the processor details.
  */
 private final void showCPU()
 {
  System.out.println("CPU Description");
  System.out.println("");
  System.out.println(" " + "Name           : " + system.getCPUClass().toString()); 
  System.out.println(" " + "Pipeline stages: " + pipeline_stages);
  System.out.println(" " + "Execute stage  : " + execute_stage);
  System.out.println(" " + "Frequency      : " + (freq / 1E6) + "MHz");
  System.out.println(" " + "GCC id         : 0x" + Integer.toHexString(system.getGCC_id()));
 }
 
 /**
  * Displays the general registers content in the decimal signed format.
  */
 private final void showGeneralDec_signed()
 {
  int a;
  
  for(a = 0;a < 2;a++)
	System.out.println("r" + a + ":" + Util.toDecStringSpace(system.getCPUClass().getRegister(a),11) + " r" + (a + 8) + " :" + Util.toDecStringSpace(system.getCPUClass().getRegister(a + 8),11) + " r" + (a + 16) + ":" + Util.toDecStringSpace(system.getCPUClass().getRegister(a + 16),11) + " r" + (a + 24) + ":" + Util.toDecStringSpace(system.getCPUClass().getRegister(a + 24),11));
  for(a = 2;a < 8;a++)
    System.out.println("r" + a + ":" + Util.toDecStringSpace(system.getCPUClass().getRegister(a),11) + " r" + (a + 8) + ":"  + Util.toDecStringSpace(system.getCPUClass().getRegister(a + 8),11) + " r" + (a + 16) + ":" + Util.toDecStringSpace(system.getCPUClass().getRegister(a + 16),11) + " r" + (a + 24) + ":" + Util.toDecStringSpace(system.getCPUClass().getRegister(a + 24),11));
 }
 
 /**
  * Displays the general registers content in the hexadecimal signed format.
  */
 private final void showGeneralDec_unsigned()
 {
  int a;
  
  for(a = 0;a < 2;a++)
	System.out.println("r" + a + ":" + Util.toDecStringSpace((long)system.getCPUClass().getRegister(a) & 0xffffffffL,11) + " r" + (a + 8) + " :" + Util.toDecStringSpace((long)system.getCPUClass().getRegister(a + 8) & 0xffffffffL,11) + " r" + (a + 16) + ":" + Util.toDecStringSpace((long)system.getCPUClass().getRegister(a + 16) & 0xffffffffL,11) + " r" + (a + 24) + ":" + Util.toDecStringSpace((long)system.getCPUClass().getRegister(a + 24) & 0xffffffffL,11));
  for(a = 2;a < 8;a++)
    System.out.println("r" + a + ":" + Util.toDecStringSpace((long)system.getCPUClass().getRegister(a) & 0xffffffffL,11) + " r" + (a + 8) + ":"  + Util.toDecStringSpace((long)system.getCPUClass().getRegister(a + 8) & 0xffffffffL,11) + " r" + (a + 16) + ":" + Util.toDecStringSpace((long)system.getCPUClass().getRegister(a + 16) & 0xffffffffL,11) + " r" + (a + 24) + ":" + Util.toDecStringSpace((long)system.getCPUClass().getRegister(a + 24) & 0xffffffffL,11));
 }
 
 /**
  * Displays the general registers content in the hexadecimal format.
  */
 private final void showGeneralHex()
 {
  int a;

  for(a = 0;a < 2;a++)
	System.out.println("r" + a + ": 0x" + Util.toHexString(system.getCPUClass().getRegister(a),8) + " r" + (a + 8) + " : 0x" + Util.toHexString(system.getCPUClass().getRegister(a + 8),8) + " r" + (a + 16) + ": 0x" + Util.toHexString(system.getCPUClass().getRegister(a + 16),10) + " r" + (a + 24) + ": 0x" + Util.toHexString(system.getCPUClass().getRegister(a + 24),8));
  for(a = 2;a < 8;a++)
    System.out.println("r" + a + ": 0x" + Util.toHexString(system.getCPUClass().getRegister(a),8) + " r" + (a + 8) + ": 0x"  + Util.toHexString(system.getCPUClass().getRegister(a + 8),8) + " r" + (a + 16) + ": 0x" + Util.toHexString(system.getCPUClass().getRegister(a + 16),10) + " r" + (a + 24) + ": 0x" + Util.toHexString(system.getCPUClass().getRegister(a + 24),8));	  
 }
 
 /**
  * Displays the general registers content in the elf binary format.
  */
 private final void showGeneralBin()
 {
  int a;

  for(a = 0;a < 10;a++)
    System.out.println("r" + a + " : 0b" + Util.toBinString(system.getCPUClass().getRegister(a),32) + " r" + (a + 16) + ": 0b" + Util.toBinString(system.getCPUClass().getRegister(a + 16),32));
  for(a = 10;a < 16;a++)
    System.out.println("r" + a + ": 0b"  + Util.toBinString(system.getCPUClass().getRegister(a),32) + " r" + (a + 16) + ": 0b" + Util.toBinString(system.getCPUClass().getRegister(a + 16),32));	  
 }
 	 
 /**
  * Displays all processor registers special excluding the general registers.
  */
 private final void showSpecial()
 {
  System.out.println("msr: 0b" + Util.toBinString(system.getCPUClass().getRegister(33),32)      + " esr: 0b" + Util.toBinString(system.getCPUClass().getRegister(35),32));
  System.out.println("btr: "   + Util.toDecStringSpace(system.getCPUClass().getRegister(37),10) + " btr: 0x" + Util.toHexString(system.getCPUClass().getRegister(37),8) + "         ear:"   + Util.toDecStringSpace(system.getCPUClass().getRegister(34),11) + " ear: 0x" +  Util.toHexString(system.getCPUClass().getRegister(34),8));
  System.out.println("pc : "   + Util.toDecStringSpace(system.getCPUClass().getRegister(32),10) + " pc : 0x" + Util.toHexString(system.getCPUClass().getRegister(32),8) + "         fsr: 0b" + Util.toBinString(system.getCPUClass().getRegister(36),32));
 }
 
 /**
  * Displays the processor pipeline content.
  */
 private final void showPipeline()
 {
  Instruction instruction;
  int aux;
  
  for(aux = pipeline_stages;aux > 0;aux--)
  {
  try
  {
   instruction = system.getCPUClass().getStageInstruction(aux);
   if(instruction != null)
   {
	if(instruction.toString().equals("illegal"))
	  System.out.println("<" + Util.toStringFixed(system.getCPUClass().getStageName(aux),12) + ">: [" + Util.toStringFixed("illegal",24) + "] <Ox" + Util.toHexString(instruction.getPC(),8) + ">");
	else if(instruction.toString().equals("mapped"))
	  System.out.println("<" + Util.toStringFixed(system.getCPUClass().getStageName(aux),12) + ">: [" + Util.toStringFixed("mapped",24) + "] <Ox" + Util.toHexString(instruction.getPC(),8) + ">");	
	else if(instruction.toString().equals("break point"))
	  System.out.println("<" + Util.toStringFixed(system.getCPUClass().getStageName(aux),12) + ">: [" + Util.toStringFixed("break point",24) + "] <Ox" + Util.toHexString(instruction.getPC(),8) + ">");	
	else if(!instruction.toString().equals(""))
      System.out.println("<" + Util.toStringFixed(system.getCPUClass().getStageName(aux),12) + ">: [" + Util.toStringFixed(system.getCPUClass().getInstructionSetArchitectureClass().toString(system.getMemoryClass().getMemoryWord(instruction.getPC())),24) + "] <Ox" + Util.toHexString(instruction.getPC(),8) + ">");
    else
      System.out.println("<" + Util.toStringFixed(system.getCPUClass().getStageName(aux),12) + ">: [" + Util.toStringFixed(" ",24) + "] <>");	  
   }
  }
  catch(MemoryException e)
  {
   System.out.println("<" + Util.toStringFixed(system.getCPUClass().getStageName(aux),12) + ">: [" + Util.toStringFixed("null",24) + "] <>");	  
  }
  }
 }
 
 /**
  * Displays internal registers and events flags of the processor.
  */
 private final void showStatus()
 {
  Instruction execute;
  
  execute = system.getCPUClass().getStageInstruction(system.getExecute_stage());
  System.out.println("pcG: " + Util.toDecStringSpace(execute.getPC(),10) + " pcG: 0x" + Util.toHexString(execute.getPC(),8));
  System.out.println("pcN: " + Util.toDecStringSpace(system.getCPUClass().getStatus(0),10) + " pcN: 0x" + Util.toHexString(system.getCPUClass().getStatus(0),8) + "         <imm>: " + system.getCPUClass().getStatus(2) + " <exc>: " + system.getCPUClass().getStatus(3) + " <brk>: " + system.getCPUClass().getStatus(4) + " <int>: " + system.getCPUClass().getStatus(5));
  System.out.println("imm:"  + Util.toDecStringSpace(system.getCPUClass().getStatus(1),11) + " imm: 0x" + Util.toHexString(system.getCPUClass().getStatus(1),8) + "         imm: 0b" + Util.toBinString(system.getCPUClass().getStatus(1),32));  	 
 }
 
 /**
  * Displays the number of cycles and instructions perform by the processor.
  */
 private final void showCPUStatus()
 {
  System.out.println("cycles #" + system.getNumberOfCycles() + " instructions #" + system.getCPUClass().getNumberOfInstructions());
 }
 
 /**
  * Displays the cycles per instruction value of the simulation.
  */
 private final void showCPI()
 {
  time_real = system.getNumberOfCycles() / system.getFrequency();
  cpi = (float)system.getNumberOfCycles() / (float)system.getCPUClass().getNumberOfInstructions(); 
  System.out.println("Results");
  System.out.println("");
  System.out.println(" " + "instructions: " + Util.toDecStringSpace(system.getCPUClass().getNumberOfInstructions(),12));
  System.out.println(" " + "cycles      : " + Util.toDecStringSpace(system.getNumberOfCycles(),12));
  System.out.println(" " + "cpi         : " + Util.toDecStringSpace(cpi,12));
  System.out.println(" " + "<frequency> : " + Util.toDecStringSpace((float)(system.getFrequency() / 1E6),12) + "MHz");
  System.out.println(" " + "simulation  : " + Util.toDecStringSpace(time_simulation,12) + "s");
  System.out.println(" " + "real        : " + Util.toDecStringSpace(time_real,12)      + "s");
  System.out.println(" " + "ratio       : " + Util.toDecStringSpace(ratio,12));
 }
 
 // -----------------------------------
 // methods used in the simulation menu
 // -----------------------------------
 
 /**
  * Simulation menu.
  */
 private final void menu_simulation()
 {
  int selection; 
	 
  for(;;)
  {
   try
   {
    tab();
	System.out.println("Test Menu - [Simulation]");
	System.out.println("");
	System.out.println(Util.toStringFixed("[1] - Continue",25) + " " + Util.toStringFixed("[2] - To memory address number",30) + " " + "[3] - ");
	System.out.println(Util.toStringFixed("[4] - Step",25)     + " " + Util.toStringFixed("[5] - To instruction number",30)    + " " + "[6] - Reload");
	System.out.println(Util.toStringFixed("[7] - Cycle",25)    + " " + Util.toStringFixed("[8] - To cycle number",30)          + " " + "[9] - Back");
	selection = Keyin.inInt("> ");
    switch(selection)
	{ 
	 case 1:tab();
            if(simulation == true || profile == true || trace == true)
            {
             System.out.println(" " + "!Warning: system stoped.");
             break;
            }
            time_begin = System.nanoTime();
            status = system.continue_();
            time_end  = System.nanoTime();            
            time_simulation = (time_end - time_begin) / 1E9f;
            time_real = system.getNumberOfCycles() / system.getFrequency();
            ratio = (time_simulation - time_real) / time_real;
            if(status == Sys_Status.STOP)
            {
             simulation = true;
             tab();
             showCPI();
             tab();
             System.out.println(" " + "!Warning: system stoped.");
            } 
            break;
	 case 2:tab();   
	        if(simulation == true || profile == true || trace == true)
            {
             System.out.println(" " + "!Warning: system stoped.");
             break;
            }
            menu_simulation_toMemoryAddress();
            break;
	 case 4:tab();
		    if(simulation == true || profile == true || trace == true)
            {
	         System.out.println(" " + "!Warning: system stoped.");
             break;
            }
            status = system.step();
            tab();
            showCPUStatus();
            showPipeline();
            showGeneralDec_signed();
            showSpecial();
            showStatus();
            if(status == Sys_Status.STOP)
            {
             simulation = true;
             tab();
	         showCPI();
             tab();
	         System.out.println(" " + "!Warning: system stoped.");
	        } 
            break;
	 case 5:tab();   
	        if(simulation == true || profile == true || trace == true)
            {
             System.out.println(" " + "!Warning: system stoped.");
             break;
            }
            menu_simulation_toInstruction();
            break;
	 case 6:tab();
		    try
		    {
		     reload();
		     System.out.println(" " + "File reloaded !!!");
		    }
		    catch(SysteMException e)
		    {
		     System.out.println("");
		     System.out.println(" " + "!Warning: " + e.getMessage());
		     System.out.println("");
		     System.exit(0);
		    }
            break;
	 case 7:tab();
            if(simulation == true || profile == true || trace == true)
            {
             System.out.println(" " + "!Warning: system stoped.");
             break;
            }
            status = system.cycle();
            tab();
            showCPUStatus();
            showPipeline();
            showGeneralDec_signed();
            showSpecial();
            showStatus();
            if(status == Sys_Status.STOP)
            {
             simulation = true;
             tab();
	         showCPI();
             tab();
	         System.out.println(" " + "!Warning: system stoped.");
            } 
	        break;
	 case 8:tab();   
            if(simulation == true || profile == true || trace == true)
            {
             System.out.println(" " + "!Warning: system stoped.");
             break;
            }
            menu_simulation_toCycle();
           break;
	 case 9:main();
		    break;
	 default:tab();
		     System.out.println(" " + "!Warning: wrong option.");
	}
   }
   catch(Exception e)
   {
	tab();
	System.out.println(" " + "!Warning: wrong option.");
   }
  } 	 
 }
 
 /**
  * Simulation menu, user selects the memory address where the simulation should stop.
  */
 private final void menu_simulation_toMemoryAddress()
 {
  int selection;
  
  for(;;)
  {
   try
   {
    System.out.println("Test Menu - [Simulation, To the memory addres]");
    System.out.println("");
    System.out.println(Util.toStringFixed("[1] - Dec",25) + " " + Util.toStringFixed("[2] - Hex",25) + " " + "[3] - ");
    System.out.println(Util.toStringFixed("[4] - ",25)    + " " + Util.toStringFixed("[5] - ",25)    + " " + "[6] - ");
    System.out.println(Util.toStringFixed("[7] - ",25)    + " " + Util.toStringFixed("[8] - ",25)    + " " + "[9] - Back");
    selection = Keyin.inInt("> ");
    switch(selection)
    {
	 case 1:menu_simulation_runto_dec();
			return;
	 case 2:menu_simulation_runto_hex();
			return;
     case 9:menu_simulation();
		    break;    
	 default:tab();
	         System.out.println(" " + "!Warning: wrong option.");
	}
   }
   catch(Exception e)
   {
    tab();
	System.out.println(" " + "!Warning: wrong option.");
   }
  }
 }

 /**
  * Simulation menu, simulates to the instruction number selected by the user.
  */
 private final void menu_simulation_toInstruction()
 {
  long instructions_aux;
  
  try
  {
   System.out.println("Test Menu - [Simulation, To the instruction number]");
   System.out.println("");
   System.out.println("Insert the number in decimal base. (" + system.getCPUClass().getNumberOfInstructions() + " instruction)");
   System.out.print("> ");
   System.out.flush();
   instructions_aux = Long.parseLong(input().readLine(),10);
   if(instructions_aux < system.getCPUClass().getNumberOfInstructions())
	 throw new Exception();
   
   System.out.println("instruction: " + instructions_aux);
   
   tab();
   status = system.toInstructionNumber(instructions_aux);
   tab();
   showCPUStatus();
   showPipeline();
   showGeneralDec_signed();
   showSpecial();
   showStatus();
   if(status == Sys_Status.STOP)
   {
    simulation = true;
    tab();
    showCPI();
    tab();
    System.out.println(" " + "!Warning: system stoped.");
   }
   menu_simulation();
  }
  catch(Exception e)
  {
   tab();
   System.out.println(" " + "!Warning: the instruction number must be a decimal number and higher than the present cycle value.");
  }
 } 
 
 /**
  * Simulation menu, simulates to the cycle number selected by the user.
  */
 private final void menu_simulation_toCycle()
 {
  long cycles_aux;
  
  try
  {
   System.out.println("Test Menu - [Simulation, To the cycle number]");
   System.out.println("");
   System.out.println("Insert the number in decimal base. (" + system.getNumberOfCycles() + " cycle)");
   System.out.print("> ");
   System.out.flush();
   cycles_aux = Long.parseLong(input().readLine(),10);
   if(cycles_aux < system.getNumberOfCycles())
	 throw new Exception();
   
   System.out.println("cycle: " + cycles_aux);
   
   tab();
   status = system.toCycleNumber(cycles_aux);
   tab();
   showCPUStatus();
   showPipeline();
   showGeneralDec_signed();
   showSpecial();
   showStatus();
   if(status == Sys_Status.STOP)
   {
    simulation = true;
    tab();
    showCPI();
    tab();
    System.out.println(" " + "!Warning: system stoped.");
   }
   menu_simulation();
  }
  catch(Exception e)
  {
   tab();
   System.out.println(" " + "!Warning: the cycle number  must be a decimal number and higher than the present cycle value.");
  }
 }
 
 /**
  * Simulation menu, simulates to the decimal format memory address selected by the user.
  */
 private final void menu_simulation_runto_dec()
 {
  long address;
  
  try
  {
   tab();
   System.out.println("Test Menu - [Simulation, To memory address]");
   System.out.println("");
   System.out.println("Insert the address in decimal base and multiple of 4.");
   System.out.print("> ");
   System.out.flush();
   address = Long.parseLong(input().readLine(),10);
   if((address % 4) != 0)
	 throw new Exception();
   tab();
   status = system.toMemoryAddress((int)address);
   tab();
   showCPUStatus();
   showPipeline();
   showGeneralDec_signed();
   showSpecial();
   showStatus();
   if(status == Sys_Status.STOP)
   {
    simulation = true;
    tab();
    showCPI();
    tab();
    System.out.println(" " + "!Warning: system stoped.");
   }
   menu_simulation();
  }
  catch(Exception e)
  {
   tab();
   System.out.println(" " + "!Warning: the address must be a decimal number and multiple of 4.");
  }
 }
 
 /**
  * Simulation menu, simulates to the hexadecimal format memory address selected by the user.
  */
 private final void menu_simulation_runto_hex()
 {
  long address;
  
  try
  {
   tab();
   System.out.println("Test Menu - [Simulation, To memory address]");
   System.out.println("");
   System.out.println("Insert the address in hexadecimal base and multiple of 4.");
   System.out.print("> ");
   System.out.flush();
   address = Long.parseLong(input().readLine(),16);
   if((address % 4) != 0)
	 throw new Exception();
   tab();
   status = system.toMemoryAddress((int)address);
   tab();
   showCPUStatus();
   showPipeline();
   showGeneralDec_signed();
   showSpecial();
   showStatus();
   if(status == Sys_Status.STOP)
   {
    simulation = true;
    tab();
    showCPI();
    tab();
    System.out.println(" " + "!Warning: system stoped.");
   }
   menu_simulation();
  }
  catch(Exception e)
  {
   tab();
   System.out.println(" " + "!Warning: the address must be a hexadecimal number and multiple of 4.");
  }
 }
 
 // ---------------------------------------------------------
 // methods used to access the information of the binary file
 // ---------------------------------------------------------
 
 /**
  * Binary file menu.
  */
 private final void menu_file()
 {
  int selection;
	 
  for(;;)
  {
   try
   {
    tab();
	System.out.println("Test Menu - [File]");
	System.out.println("");
	System.out.println(Util.toStringFixed("[1] - Name",25) + " " + Util.toStringFixed("[2] - Reload",25) + " " + "[3] - ");
	System.out.println(Util.toStringFixed("[4] - ",25)     + " " + Util.toStringFixed("[5] - ",25)       + " " + "[6] - ");
	System.out.println(Util.toStringFixed("[7] - ",25)     + " " + Util.toStringFixed("[8] - ",25)       + " " + "[9] - Back");
	selection = Keyin.inInt("> ");
    switch(selection)
	{
     case 1:tab();
            System.out.println("File name");
            System.out.println("");
            System.out.println("  " + binary_file);
    	    break;
	 case 2:tab();
	        try
	        {
	         reload();
	         System.out.println(" " + "File reloaded !!!");
	        }
	        catch(SysteMException e)
	        {
	         tab();
	         System.out.println(" " + "!Warning: " + e.getMessage());
	         System.out.println("");
	         System.exit(0);
	        }
		    break;
	 case 9:main();
		    break;
	 default:tab();
		     System.out.println(" " + "!Warning: wrong option.");
	}
   }
   catch(Exception e)
   {
	tab();
	System.out.println(" " + "!Warning: wrong option.");
   }
  } 	  
 }
 
 /**
  * Reloads the elf binary file in to the system memory.
  *
  * @throws SysteMException if any error occurs when loads the elf binary file.
  */
 private final void reload() throws SysteMException
 {
  ElfBinaryParser elf_binary_parser;
  Map<Long,Integer> binary_data;
  Iterator<Long> address;
  long address_aux;
  long exit;
  
  simulation = false;
  profile = false;
  trace = false;
  try
  {
   elf_binary_parser = new ElfBinaryParser();
   elf_binary_parser.load(binary_file,system.getGCC_id());
   elf_binary_parser.loadBinaryData();
   system.reset();
   binary_data = elf_binary_parser.getBinaryData();
   do
   {
	address = binary_data.keySet().iterator();
	address_aux = address.next();	
	system.getMemoryClass().putMemoryWord((int)address_aux,binary_data.get(address_aux));
    binary_data.remove(address_aux);
   }
   while(binary_data.size() > 0);
   
   if(elf_binary_parser.getFunction_exit_address() != -1)
   {
    exit = elf_binary_parser.getFunction_exit_address();
    system.getCPUClass().createProgram_Exit(exit);
   }
  }
  catch(CPUException e)
  {	 
   throw new SysteMException(e.getMessage());
  }
  catch(ElfBinaryParserException e)
  {	 
   throw new SysteMException(e.getMessage());
  } 
  catch(MemoryException e) 
  {
   throw new SysteMException(e.getMessage());
  }
 }

 // --------------------------------
 // methods used in the profile menu
 // --------------------------------
 
 /**
  * Profiler (performance analysis) menu.
  */
 private final void menu_profile()
 {
  int selection;
  int status;
	 
  for(;;)
  {
   try
   {
    tab();
	System.out.println("Test Menu - [Profile]");
	System.out.println("");
	System.out.println(Util.toStringFixed("[1] - Functions",25)    + " " + Util.toStringFixed("[2] - Instructions",25) + " " + "[3] - Save Profile");
	System.out.println(Util.toStringFixed("[4] - Func Profile",25) + " " + Util.toStringFixed("[5] - Inst Profile",25) + " " + "[6] - Profile");
	System.out.println(Util.toStringFixed("[7] - Run",25)          + " " + Util.toStringFixed("[8] - Reload",25)       + " " + "[9] - Back");
	selection = Keyin.inInt("> ");
    switch(selection)
	{
     case 1:tab();
            showFunctions();
            break;            
	 case 2:tab();
            showInst();
            break;
	 case 3:tab();
	        if(profile == true)
	        {
             saveProfile();
	         System.out.println(" " + "Profile file saved !!!");
	        }
	        else
	          System.out.println(" " + "!Warning: no data to save, you have to run the program.");
	        break;
	 case 4:tab();
	        if(profile == true)
              menu_log_func();
            else
              System.out.println(" " + "!Warning: no data to show, you have to run the program.");	
            break;
	 case 5:tab();
            if(profile == true)
              showLog_inst();
            else
              System.out.println(" " + "!Warning: no data to show, you have to run the program.");	
            break;
	 case 6:tab();
            if(profile == true)
              showProfile();
            else
              System.out.println(" " + "!Warning: no data to show, you have to run the program.");	
            break;
	 case 7:tab();
            if(simulation == true || profile == true || trace == true)
            {
             System.out.println(" " + "!Warning: system stoped.");
             break;
            }
            time_begin = System.nanoTime();
            status = system.profile();
            time_end  = System.nanoTime();            
            time_simulation = (time_end - time_begin) / 1E9f;
            time_real = system.getNumberOfCycles() / system.getFrequency();
            ratio = (time_simulation - time_real) / time_real;
            if(status == Sys_Status.STOP)
            {
             profile = true;
             tab();
             showCPI();
             tab();
             System.out.println(" " + "!Warning: system stoped.");
            } 
            break;
	 case 8:tab();
            try
            {
             reload();
             System.out.println(" " + "File reloaded !!!");
            }
            catch(SysteMException e)
            {
             System.out.println(" " + "!Warning: " + e.getMessage());
             System.out.println("");
             System.exit(0);
            }
	        break;
	 case 9:main();
		    break;
	 default:tab();
		     System.out.println(" " + "!Warning: wrong option.");
	}
   }
   catch(Exception e)
   {
	tab();
	System.out.println(" " + "!Warning: wrong option.");
   }
  } 	  
 }

 /**
  * Profiler (performance analysis) menu. The function is selected by the user.
  */
 private final void menu_log_func()
 {
  Map<Integer,String> function_name;
  int function;
  int aux;
	
  function_name = system.getFunction_name();
  if(function_name.size() < 1)
  {
   tab();
   System.out.println(" " + "!Warning: no functions to show.");
   return;
  }
  try
  {
   tab();
   System.out.println("Test Menu - [Profile, Function profile]");
   System.out.println("");
   System.out.println(" " + "nr." + "  " + "function");
   for(aux = 0;aux < function_name.size();aux++)
	 System.out.println(" " + Util.toDecStringSpace(aux,3) + "  " + function_name.get(aux));
   System.out.println("");
   System.out.println("Insert the function in decimal base.");
   System.out.print("> ");
   System.out.flush();
   function = Integer.parseInt(input().readLine(),10);
   if(function < 0 || function > (function_name.size() - 1))
   {
	tab();
	System.out.println(" " + "!Warning: no functions with this number exist.");
    return;
   }
   tab();
   showLog_func(function);
  }
  catch(Exception e)
  {
   tab();
   System.out.println(" " + "!Warning: the function must be a decimal number.");
  } 
 }
 
 /**
  * Displays function profile.
  *
  * @param function the number of the function to be displayed. 
  */
 private final void showLog_func(int function)
 {
  Map<Integer,Map<String,Long>> log_function_inst;
  Map<String,Long> instructions_temp;
  Map<Integer,String> function_name;
  Map<Integer,Long> log_function_execute;
  Map<Integer,Long> log_function_cycle;
  Map<Integer,Long> log_function_call;
  String name_aux[];
  
  DecimalFormat decimal;
  String result_a;
  float time;
  
  long total = 0;
  long temp;
  int aux;

  log_function_inst = system.getLog_func_inst(); 
  instructions_temp = log_function_inst.get(function); 
  function_name = system.getFunction_name();
  log_function_execute = system.getLog_function_execute();
  log_function_cycle = system.getLog_function_cycle();
  log_function_call = system.getLog_function_call();
  
  System.out.println("Function profile"); 
  System.out.println("");
  System.out.println(" " + Util.toStringSpace("cycles",12) + "  " + Util.toStringSpace("instructions",12) + "  " + Util.toStringSpace("execute",7) + "  " + Util.toStringSpace("calls",5) + "  " +  Util.toStringSpace("function",8));	
 
  name_aux = new String[instructions_temp.keySet().size()];
  instructions_temp.keySet().toArray(name_aux);
  java.util.Arrays.sort(name_aux);
  for(aux = 0;aux < instructions_temp.size();aux++)
    total = total + instructions_temp.get(name_aux[aux]);
	
  System.out.println(" " + Util.toDecStringSpace(log_function_cycle.get(function),12) + "  " + Util.toDecStringSpace(total,12) + "  " + Util.toDecStringSpace(log_function_execute.get(function),7) + "  " + Util.toDecStringSpace(log_function_call.get(function),5) + "  " + function_name.get(function));
  System.out.println("");
  System.out.println(" " + Util.toStringSpace("instruction",12)+ "  " + Util.toStringSpace("#",12) + "  " + Util.toStringSpace("%",7));
  
  decimal = new DecimalFormat("0.000");
  for(aux = 0;aux < instructions_temp.size();aux++)
  {	  
   temp = instructions_temp.get(name_aux[aux]);
   if(temp == 0)
	 continue;
   time = (float)instructions_temp.get(name_aux[aux]) / (float)total * 100;
   result_a = decimal.format(time);
   
   System.out.println(" " + Util.toStringSpace(name_aux[aux],12) + "  " + Util.toDecStringSpace(temp,12) + "  " + Util.toStringSpace(result_a,7));
  }
 }
 
 /**
  * Displays the functions list of the elf binary file and its memory address range.
  */
 private final void showFunctions()
 {
  Map<Integer,String> function_name;
  Map<Integer,Long> function_begin;
  Map<Integer,Long> function_end;
  int aux;
  
  function_name = system.getFunction_name();
  function_begin = system.getFunction_begin();
  function_end = system.getFunction_end();
  
  System.out.println("Functions");
  System.out.println("");
  System.out.println(" " + Util.toStringSpace("from",10) + "  " + Util.toStringSpace("to",10) + "  " + Util.toStringSpace("size",12) + "  " + "function");
  
  for(aux = 0;aux < function_name.size();aux++)
	System.out.println(" " + "0x" + Util.toHexString(function_begin.get(aux),8) + "  " + "0x" + Util.toHexString(function_end.get(aux),8) + "  " + Util.toDecStringSpace(function_end.get(aux) - function_begin.get(aux) + 1,6) + " bytes" + "  " + function_name.get(aux));
 }

 /**
  * Displays the instruction's profile.
  */
 private final void showLog_inst()
 {
  Map<Integer,Map<String,Long>> log_function_inst;
  Map<String,Long> instructions_temp;
  Map<String,Long> instructions_name;
  Map<String,Long> instructions;
  Iterator<String> name;
  String name_aux[];
  String name_temp;
  
  DecimalFormat decimal;
  String result_a;
  float time;
  
  long total = 0;
  long temp;
  int aux_a;
  int aux_b;
  
  log_function_inst = system.getLog_func_inst();
  instructions_name = system.getCPU_instruction();
  instructions = new HashMap<String,Long>(instructions_name);
  for(aux_a = 0;aux_a < log_function_inst.size();aux_a++)
  {
   instructions_temp = log_function_inst.get(aux_a);
   name = instructions_temp.keySet().iterator();
   for(aux_b = 0;aux_b < instructions_temp.size();aux_b++)
   {
	name_temp = name.next();
	temp = instructions.get(name_temp) + instructions_temp.get(name_temp);
	instructions.put(name_temp,temp); 
   }
  }
  name = instructions.keySet().iterator();
  for(aux_a = 0;aux_a < instructions.size();aux_a++)
  {
   name_temp = name.next();
   total = total + instructions.get(name_temp);	  
  }
  name_aux = new String[instructions.keySet().size()];
  instructions.keySet().toArray(name_aux);
  java.util.Arrays.sort(name_aux);
  
  System.out.println("Instruction profile");
  System.out.println("");
  System.out.println(" " + Util.toStringSpace("instruction",12)+ "  " + Util.toStringSpace("#",12) + "  " + Util.toStringSpace("%",7));
  
  decimal = new DecimalFormat("0.000");
  for(aux_a = 0;aux_a < name_aux.length;aux_a++)
  {
   if(instructions.get(name_aux[aux_a]) == 0)
	 continue;
   time = (float)instructions.get(name_aux[aux_a]) / (float)total * 100;
   result_a = decimal.format(time);
   System.out.println(" " + Util.toStringSpace(name_aux[aux_a],12) + "  " + Util.toDecStringSpace(instructions.get(name_aux[aux_a]),12) + "  " + Util.toStringSpace(result_a,7));
  }
  System.out.println(" " + Util.toDecStringSpace(total,26));
 }
  
 /**
  * Displays function's profile (like the gprof program, with instruction's profile and simulation information).
  */
 private final void showProfile()
 {
  Map<Integer,Map<String,Long>> log_function_inst;
  Map<Integer,Long> log_function_execute;
  Map<Integer,Long> log_function_cycle;
  Map<Integer,Long> log_function_call;
  Map<Integer,String> function_name;
		  
  long instructions;
  float frequency;
  long cycles;
		  
  Vector<Integer> keys;
  Vector<Long> values;
  long swap_values;
  int swap_keys;
  boolean order;
  float time;
		 
  long total_instructions;
  long total_executes;
  long total_cycles;
  long total_calls;
  long execute;
  long cycle;
  long call;
		  
  Map<String,Long> function_inst;
  Iterator<String> name;
	 
  DecimalFormat decimal;
  String result_a;
  String result_b;
	   
  int aux_a;
  int aux_b;
		  
  try
  {
   log_function_execute = system.getLog_function_execute();
   log_function_cycle = system.getLog_function_cycle();
   log_function_call = system.getLog_function_call();
   log_function_inst = system.getLog_func_inst();
		   
   function_name = system.getFunction_name();
   frequency = system.getFrequency();
	   
   instructions = system.getCPUClass().getNumberOfInstructions();
   cycles = system.getNumberOfCycles();
	   
		   
   System.out.println("System configuration file");
   System.out.println("");
   System.out.println(" " + systemconfig_file);
		   
		   
   System.out.println("");
   System.out.println("");
	   
	   
   System.out.println("Binary file");
   System.out.println("");
   System.out.println(" " + binary_file);
		   
		   
   System.out.println("");
   System.out.println("");
		   
		   
   System.out.println("Performance");
   System.out.println("");
   System.out.println(" " + "instructions: " + Util.toDecStringSpace(instructions,12));
   System.out.println(" " + "cycles      : " + Util.toDecStringSpace(cycles,12));
   System.out.println(" " + "cpi         : " + Util.toDecStringSpace(cpi,12));
   System.out.println(" " + "<frequency> : " + Util.toDecStringSpace((float)(frequency / 1E6),12) + "MHz");
   System.out.println(" " + "simulation  : " + Util.toDecStringSpace(time_simulation,12) + "s");
   System.out.println(" " + "real        : " + Util.toDecStringSpace(time_real,12)      + "s");
   System.out.println(" " + "ratio       : " + Util.toDecStringSpace(ratio,12));
	   
		   
   System.out.println("");
   System.out.println("");
		   
		  
   // order the functions in descending order the number of cycles   
   keys = new Vector<Integer>();
   values = new Vector<Long>();
	   
   for(aux_a = 0;aux_a < log_function_cycle.size();aux_a++)
   {
    values.add(log_function_cycle.get(aux_a));	  
    keys.add(aux_a);
   }
   do
   {
    order = true;
    for(aux_a = 0;aux_a < values.size() - 1;aux_a++)
    {
 	 if(values.get(aux_a) < values.get(aux_a + 1))
  	 {
  	  order = false;
  	  swap_values = values.get(aux_a + 1);
  	  values.set(aux_a + 1,values.get(aux_a));
  	  values.set(aux_a,swap_values);
  	  swap_keys = keys.get(aux_a + 1);
  	  keys.set(aux_a + 1,keys.get(aux_a));
 	  keys.set(aux_a,swap_keys);
  	 }
    }
   }
   while(order == false);
		   
   System.out.println("Flat profile");
   System.out.println("");
   System.out.println("    %       cumulative          self                  self       ");
   System.out.println("   time       cycles           cycles    calls      cycles/call  function");
	   
   decimal = new DecimalFormat("0.00");
   total_cycles = 0;
   for(aux_a = 0;aux_a < keys.size();aux_a++)
   {
    time = (float)log_function_cycle.get(keys.get(aux_a)) / (float)cycles * 100;
    result_a = decimal.format(time);
    total_cycles = total_cycles + log_function_cycle.get(keys.get(aux_a));
    if(log_function_call.get(keys.get(aux_a)) != 0)
    {
 	 time = (float)log_function_cycle.get(keys.get(aux_a)) / (float)log_function_call.get(keys.get(aux_a));
     result_b = decimal.format(time);
     System.out.println(" " + Util.toStringSpace(result_a,6) + " " + Util.toDecStringSpace(total_cycles,14) + " " + Util.toDecStringSpace(log_function_cycle.get(keys.get(aux_a)),14) + " " + Util.toDecStringSpace(log_function_call.get(keys.get(aux_a)),8) + " " + Util.toStringSpace(result_b,16) + "  " + function_name.get(keys.get(aux_a)));	  
    }
    else
      System.out.println(" " + Util.toStringSpace(result_a,6) + " " + Util.toDecStringSpace(total_cycles,14) + Util.toStringSpace(" ",42) + " " + function_name.get(keys.get(aux_a)));   
   }
		   
		    
   System.out.println("");
   System.out.println("");
		   
		   
   instructions = 0;
   total_instructions = 0;
   total_executes = 0;
   total_cycles = 0;
   total_calls = 0;
   System.out.println("General profile ");
   System.out.println("");
   System.out.println(" " + Util.toStringSpace("cycles",12) + "  " + Util.toStringSpace("instructions",12) + "  " + Util.toStringSpace("execute",7) + "  " + Util.toStringSpace("calls",5) + "  " +  Util.toStringSpace("function",8));
   for(aux_a = 0;aux_a < function_name.size();aux_a++)
   {
    call = log_function_call.get(aux_a);
    total_calls = total_calls + call;
	    
    cycle = log_function_cycle.get(aux_a);
    total_cycles = total_cycles + cycle;
	    
    execute = log_function_execute.get(aux_a);
    total_executes = total_executes + execute;
	    
    function_inst = log_function_inst.get(aux_a);
    name = function_inst.keySet().iterator();
    for(aux_b = 0;aux_b < function_inst.size();aux_b++)
 	  instructions = function_inst.get(name.next()) + instructions;
    System.out.println(" " + Util.toDecStringSpace(cycle,12) + "  " + Util.toDecStringSpace(instructions,12) + "  " + Util.toDecStringSpace(execute,7) + "  " + Util.toDecStringSpace(call,5) + "  " + function_name.get(aux_a));
    total_instructions = total_instructions + instructions;
    instructions = 0;
   }
   System.out.println(" " + Util.toDecStringSpace(total_cycles,12) + "  " + Util.toDecStringSpace(total_instructions,12) + "  " + Util.toDecStringSpace(total_executes,7) + "  " + Util.toDecStringSpace(total_calls,5));
		    
		   
   System.out.println("");
   System.out.println("");
  }
  catch(Exception e) 
  {
   tab();
   System.out.println(" " + "!Warning: " + e.getMessage());
   System.out.println("");
   System.exit(0);
  }	 
 }
 
 /**
  * Displays the instructions list of the processor.
  */
 private final void showInst()
 {
  Map<String,Long> instruction;
  String name_aux[];
  int aux;
	
  instruction = system.getCPU_instruction();
  name_aux = new String[instruction.keySet().size()];
  instruction.keySet().toArray(name_aux);
  java.util.Arrays.sort(name_aux);
  System.out.println("Instructions");
  System.out.println("");
  for(aux = 0;aux < name_aux.length;aux++)
	System.out.println("  " + name_aux[aux]);
 }
 
 /**
  * Saves the profile results of the simulation in the output file.
  */
 private final void saveProfile()
 {
  FileOutputStream file_output;
  PrintStream output;
  File file;
	  
  Map<Integer,Map<String,Long>> log_function_inst;
  Map<Integer,Long> log_function_execute;
  Map<Integer,Long> log_function_cycle;
  Map<Integer,Long> log_function_call;
  Map<Integer,String> function_name;
	  
  long instructions;
  float frequency;
  long cycles;
	  
  Vector<Integer> keys;
  Vector<Long> values;
  long swap_values;
  int swap_keys;
  boolean order;
  float time;
	 
  long total_instructions;
  long total_executes;
  long total_cycles;
  long total_calls;
  long execute;
  long cycle;
  long call;
	  
  Map<String,Long> function_inst_temp;
  Map<String,Long> function_inst;
  Iterator<String> name;
 
  DecimalFormat decimal;
  String result_a;
  String result_b;
   
  String name_aux[];
  String name_temp;
	  
  long value; 
  int aux_a;
  int aux_b;
	  
	  
  try
  {
   file = new File(profile_file);
   file_output = new FileOutputStream(file);
   output = new PrintStream(file_output); 
   
   log_function_execute = system.getLog_function_execute();
   log_function_cycle = system.getLog_function_cycle();
   log_function_call = system.getLog_function_call();
   log_function_inst = system.getLog_func_inst();
	   
   function_name = system.getFunction_name();
   frequency = system.getFrequency();
   
   instructions = system.getCPUClass().getNumberOfInstructions();
   cycles = system.getNumberOfCycles();
   
	   
   output.println("System configuration file");
   output.println("");
   output.println(" " + systemconfig_file);
	   
	   
   output.println("");
   output.println("");
   
   
   output.println("Binary file");
   output.println("");
   output.println(" " + binary_file);
	   
	   
   output.println("");
   output.println("");
	   
	   
   output.println("Performance");
   output.println("");
   output.println(" " + "instructions: " + Util.toDecStringSpace(instructions,12));
   output.println(" " + "cycles      : " + Util.toDecStringSpace(cycles,12));
   output.println(" " + "cpi         : " + Util.toDecStringSpace(cpi,12));
   output.println(" " + "<frequency> : " + Util.toDecStringSpace((float)(frequency / 1E6),12) + "MHz");
   output.println(" " + "simulation  : " + Util.toDecStringSpace(time_simulation,12) + "s");
   output.println(" " + "real        : " + Util.toDecStringSpace(time_real,12)      + "s");
   output.println(" " + "ratio       : " + Util.toDecStringSpace(ratio,12));
   
	   
   output.println("");
   output.println("");
	   
	  
   // order the functions in descending order the number of cycles   
   keys = new Vector<Integer>();
   values = new Vector<Long>();
   
   for(aux_a = 0;aux_a < log_function_cycle.size();aux_a++)
   {
    values.add(log_function_cycle.get(aux_a));	  
    keys.add(aux_a);
   }
   do
   {
    order = true;
    for(aux_a = 0;aux_a < values.size() - 1;aux_a++)
    {
 	 if(values.get(aux_a) < values.get(aux_a + 1))
  	 {
  	  order = false;
  	  swap_values = values.get(aux_a + 1);
  	  values.set(aux_a + 1,values.get(aux_a));
  	  values.set(aux_a,swap_values);
  	  swap_keys = keys.get(aux_a + 1);
  	  keys.set(aux_a + 1,keys.get(aux_a));
 	  keys.set(aux_a,swap_keys);
  	 }
    }
   }
   while(order == false);
	   
   output.println("Flat profile");
   output.println("");
   output.println("    %       cumulative          self                  self       ");
   output.println("   time       cycles           cycles    calls      cycles/call  function");
	   
   decimal = new DecimalFormat("0.00");
   total_cycles = 0;
   for(aux_a = 0;aux_a < keys.size();aux_a++)
   {
    time = (float)log_function_cycle.get(keys.get(aux_a)) / (float)cycles * 100;
    result_a = decimal.format(time);
    total_cycles = total_cycles + log_function_cycle.get(keys.get(aux_a));
    if(log_function_call.get(keys.get(aux_a)) != 0)
    {
 	 time = (float)log_function_cycle.get(keys.get(aux_a)) / (float)log_function_call.get(keys.get(aux_a));
     result_b = decimal.format(time);
     output.println(" " + Util.toStringSpace(result_a,6) + " " + Util.toDecStringSpace(total_cycles,14) + " " + Util.toDecStringSpace(log_function_cycle.get(keys.get(aux_a)),14) + " " + Util.toDecStringSpace(log_function_call.get(keys.get(aux_a)),8) + " " + Util.toStringSpace(result_b,16) + "  " + function_name.get(keys.get(aux_a)));	  
    }
    else
      output.println(" " + Util.toStringSpace(result_a,6) + " " + Util.toDecStringSpace(total_cycles,14) + Util.toStringSpace(" ",42) + " " + function_name.get(keys.get(aux_a)));   
   }
	   
	    
   output.println("");
   output.println("");
	   
	   
   instructions = 0;
   total_instructions = 0;
   total_executes = 0;
   total_cycles = 0;
   total_calls = 0;
   output.println("General profile ");
   output.println("");
   output.println(" " + Util.toStringSpace("cycles",12) + "  " + Util.toStringSpace("instructions",12) + "  " + Util.toStringSpace("execute",7) + "  " + Util.toStringSpace("calls",5) + "  " +  Util.toStringSpace("function",8));
   for(aux_a = 0;aux_a < function_name.size();aux_a++)
   {
    call = log_function_call.get(aux_a);
    total_calls = total_calls + call;
    
    cycle = log_function_cycle.get(aux_a);
    total_cycles = total_cycles + cycle;
    
    execute = log_function_execute.get(aux_a);
    total_executes = total_executes + execute;
    
    function_inst = log_function_inst.get(aux_a);
    name = function_inst.keySet().iterator();
    for(aux_b = 0;aux_b < function_inst.size();aux_b++)
 	  instructions = function_inst.get(name.next()) + instructions;
    output.println(" " + Util.toDecStringSpace(cycle,12) + "  " + Util.toDecStringSpace(instructions,12) + "  " + Util.toDecStringSpace(execute,7) + "  " + Util.toDecStringSpace(call,5) + "  " + function_name.get(aux_a));
    total_instructions = total_instructions + instructions;
    instructions = 0;
   }
   output.println(" " + Util.toDecStringSpace(total_cycles,12) + "  " + Util.toDecStringSpace(total_instructions,12) + "  " + Util.toDecStringSpace(total_executes,7) + "  " + Util.toDecStringSpace(total_calls,5));
	    
	   
   output.println("");
   output.println("");
	   
	   
   // sum total of each instruction, starting from subtotals of each function
   function_inst_temp = system.getCPU_instruction();
   function_inst = new HashMap<String,Long>(function_inst_temp);
   for(aux_a = 0;aux_a < function_name.size();aux_a++)
   {
	function_inst_temp = log_function_inst.get(aux_a);
    name = function_inst_temp.keySet().iterator();
    for(aux_b = 0;aux_b < function_inst_temp.size();aux_b++)
    {
 	 name_temp = name.next();
 	 value = function_inst.get(name_temp) + function_inst_temp.get(name_temp);
 	 function_inst.put(name_temp,value); 
    }
   }
	   
   // sum total of all instructions
   total_instructions = 0;
   name = function_inst.keySet().iterator();
   for(aux_a = 0;aux_a < function_inst.size();aux_a++)
   {
    name_temp = name.next();
    total_instructions = total_instructions + function_inst.get(name_temp);	  
   }
   name_aux = new String[function_inst.keySet().size()];
   function_inst.keySet().toArray(name_aux);
   java.util.Arrays.sort(name_aux);
   
   output.println("Instruction profile");
   output.println("");
   output.println(" " + Util.toStringSpace("instruction",12)+ "  " + Util.toStringSpace("#",12) + "  " + Util.toStringSpace("%",7));
   
   decimal = new DecimalFormat("0.000");
   for(aux_a = 0;aux_a < name_aux.length;aux_a++)
   {
	if(function_inst.get(name_aux[aux_a]) == 0)
	  continue;
	time = (float)function_inst.get(name_aux[aux_a]) / (float)total_instructions * 100;
	result_a = decimal.format(time);
    output.println(" " + Util.toStringSpace(name_aux[aux_a],12) + "  " + Util.toDecStringSpace(function_inst.get(name_aux[aux_a]),12) + "  " + Util.toStringSpace(result_a,7));
   }
   output.println(" " + Util.toDecStringSpace(total_instructions,26));
   
	   
   output.println("");
   output.println("");
	   
	   
   output.println("Function profile"); 
   output.println("");
   for(aux_a = 0;aux_a < log_function_inst.size();aux_a++)
   {
	total_instructions = 0;
	function_inst_temp = log_function_inst.get(aux_a);
		
	if(log_function_cycle.get(aux_a) == 0)
      continue;
	output.println(" " + Util.toStringSpace("cycles",12) + "  " + Util.toStringSpace("instructions",12) + "  " + Util.toStringSpace("execute",7) + "  " + Util.toStringSpace("calls",5) + "  " +  Util.toStringSpace("function",8));	
	    
	for(aux_b = 0;aux_b < function_inst_temp.size();aux_b++)
    {	  
     value = function_inst_temp.get(name_aux[aux_b]);
     total_instructions = total_instructions + value;
    }
		
	output.println(" " + Util.toDecStringSpace(log_function_cycle.get(aux_a),12) + "  " + Util.toDecStringSpace(total_instructions,12) + "  " + Util.toDecStringSpace(log_function_execute.get(aux_a),7) + "  " + Util.toDecStringSpace(log_function_call.get(aux_a),5) + "  " + function_name.get(aux_a));
    output.println("");
    output.println(" " + Util.toStringSpace("instruction",12)+ "  " + Util.toStringSpace("#",12) + "  " + Util.toStringSpace("%",7));
	    
    name_aux = new String[function_inst_temp.keySet().size()];
    function_inst_temp.keySet().toArray(name_aux);
    java.util.Arrays.sort(name_aux);
	        
    for(aux_b = 0;aux_b < function_inst_temp.size();aux_b++)
    {	  
     value = function_inst_temp.get(name_aux[aux_b]);
     if(value == 0)
       continue;
     time = (float)value / (float)total_instructions * 100;
 	 result_a = decimal.format(time);
     output.println(" " + Util.toStringSpace(name_aux[aux_b],12) + "  " + Util.toDecStringSpace(value,12) + "  " +Util.toStringSpace(result_a,7));
    }
	    
	    
    output.println("");
    output.println("");
   }
   file_output.close();	
  }
  catch(IOException e) 
  {
   System.out.println(" " + "!Warning: " + e.getMessage());
   System.out.println("");
   System.exit(0);
  }	 
 }
 
 // ------------------------------
 // methods used in the trace menu
 // ------------------------------
 
 /**
  * Instruction trace menu.
  */
 private final void menu_trace()
 {
  int selection;
  int status;
	 
  for(;;)
  {
   try
   {
    tab();
	System.out.println("Test Menu - [Trace]");
	System.out.println("");
	System.out.println(Util.toStringFixed("[1] - ",25)    + " " + Util.toStringFixed("[2] - ",25)       + " " + "[3] - ");
	System.out.println(Util.toStringFixed("[4] - ",25)    + " " + Util.toStringFixed("[5] - ",25)       + " " + "[6] - ");
	System.out.println(Util.toStringFixed("[7] - Run",25) + " " + Util.toStringFixed("[8] - Reload",25) + " " + "[9] - Back");
	selection = Keyin.inInt("> ");
    switch(selection)
	{
	 case 7:tab();
	        if(simulation == true || profile == true || trace == true)
            {
             System.out.println(" " + "!Warning: system stoped.");
             break;
            }
	        
	        status = saveTrace();
	        
            if(status == Sys_Status.STOP)
            {
             trace = true;
             tab();
             showCPI();
             tab();
             System.out.println(" " + "!Warning: system stoped.");
            } 
            break;
	 case 8:tab();
		    try
            {
             reload();
             System.out.println(" " + "File reloaded !!!");
            }
            catch(SysteMException e)
            {
             System.out.println(" " + "!Warning: " + e.getMessage());
             System.out.println("");
             System.exit(0);
            }
	        break;
	 case 9:main();
		    break;
	 default:tab();
		     System.out.println(" " + "!Warning: wrong option.");
	}
   }
   catch(Exception e)
   {
	tab();
	System.out.println(" " + "!Warning: wrong option.");
   }
  } 	  
 }
 
 /**
  * Simulates the elf binary file and saves in the output file the results of the instruction trace simulation and displays the simulation results.
  *
  * @return the status menu.
  */
 private final int saveTrace()
 { 
  FileOutputStream file_output;
  PrintStream output;
  File file;
	  
  String instruction;
  int value;
  int pc;
  
  int sys_status;
   
  try
  {
   file = new File(trace_file);
   file_output = new FileOutputStream(file);
   output = new PrintStream(file_output);
   output.println("Trace");
   output.println("");
   
   time_begin = System.nanoTime();
   do
   {
    pc = system.getCPUClass().getStageInstruction(3).getPC();
    value = system.getMemoryClass().getMemoryWord(pc);
    instruction = system.getCPUClass().getInstructionSetArchitectureClass().toString(value);
    output.println("0x" + Util.toHexString(pc,8) + "  " + instruction);
    sys_status = system.step();
   }
   while(sys_status == Sys_Status.NORMAL);
   time_end  = System.nanoTime();
 
   time_simulation = (time_end - time_begin) / 1E9f;
   time_real = system.getNumberOfCycles() / system.getFrequency();
   ratio = (time_simulation - time_real) / time_real;
   cpi = (float)system.getNumberOfCycles() / (float)system.getCPUClass().getNumberOfInstructions();
 
   output.println("");
   output.println("");
   
   output.println("System configuration file");
   output.println("");
   output.println(" " + systemconfig_file);  
   
   output.println("");
   output.println("");
   
   output.println("Binary file");
   output.println("");
   output.println(" " + binary_file);
   
   output.println("");
   output.println("");
   
   output.println("Performance");
   output.println("");
   output.println(" " + "instructions: " + Util.toDecStringSpace(system.getCPUClass().getNumberOfInstructions(),12));
   output.println(" " + "cycles      : " + Util.toDecStringSpace(system.getNumberOfCycles(),12));
   output.println(" " + "cpi         : " + Util.toDecStringSpace(cpi,12));
   output.println(" " + "<frequency> : " + Util.toDecStringSpace((float)(system.getFrequency() / 1E6),12) + "MHz");
   output.println(" " + "simulation  : " + Util.toDecStringSpace(time_simulation,12) + "s");
   output.println(" " + "real        : " + Util.toDecStringSpace(time_real,12) + "s");
   output.println(" " + "ratio       : " + Util.toDecStringSpace(ratio,12));
   
   output.println("");
   output.println("");
  
   file_output.close();
   
   tab();
   System.out.println(" " + "Trace saved in the <" + trace_file + "> file.");
   return sys_status;
  }
  catch(FileNotFoundException e)
  {
   tab();
   System.out.println(" " + "!Warning: error in <" + trace_file + "> file, " + e.getMessage());
   System.out.println("");
   System.exit(0);
  }
  catch(MemoryException e)
  {
   tab();
   System.out.println(" " + "!Warning: instruction not exist in memory " + e.getMessage());
   System.out.println("");
   System.exit(0);	  
  }
  catch(IOException e)
  {
   tab();
   System.out.println(" " + "!Warning: error in <" + trace_file + "> file, " + e.getMessage());
   System.out.println("");
   System.exit(0);	  
  }
  return 0;
 }
 
 // -------------------
 // methods auxiliaries
 // -------------------
 
 /**
  * Returns the input buffered reader.
  *
  * @return the input buffered reader.
  */
 private final BufferedReader input()
 {
  return in;
 }
 
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