package system;

import java.util.*;

import system.memory.*;
import system.file.*;
import system.cpu.*;

/**
 * The executable and linking format (ELF) file loader class. This class performs the binary data load in to the memory system and checks if the binary data is according to the system.   
 * The binary data is collected from the executable and linking format (ELF) file. 
 */
public class LoadProgram 
{
 /** The executable and linking format (ELF) file parser. */
 private ElfBinaryParser elf_binary_parser;
 /** The list of the with the functions name. */
 private Map<Integer,String> function_name;
 /** The list with the first position memory address of the functions. */
 private Map<Integer,Long> function_begin;
 /** The list with the last position memory address of the functions. */
 private Map<Integer,Long> function_end;
 /** The address of the exit function. */
 private long exit = -1; 

 // ---------------------------------------------------------
 // method to verify and loads the binary program into memory
 // ---------------------------------------------------------
 
 /**
  * Loads an executable and linking format (ELF) file in to the memory system.
  *
  * @param cpu the system processor.
  * @param memory the memory system.
  * @param binary_file the executable and linking format (ELF) file name.
  * @param gcc_id the gcc id code of the processor.
  * @param function_exit the exit function flag.<p>
  * false - doesn't find the exit function.<br>
  * true - find the exit function.
  * @throws LoadProgramException if any error occur when loads the executable and linking format (ELF) file.
  */
 public void load(CPU cpu,Memory memory,String binary_file,int gcc_id,boolean function_exit) throws LoadProgramException
 {
  Map<Integer,Long> program_physaddr_high;
  Map<Integer,Long> program_physaddr_low;
  Map<Long,Integer> binary_data;
  int program_number_sections;
  Iterator<Long> address;
  int number_sections = 0;
  long address_aux;
  long lmb_begin;
  long lmb_end;
  boolean opb;
  long opb_begin;
  long opb_end;
  int aux;
  
  try
  {
   elf_binary_parser = new ElfBinaryParser();
   elf_binary_parser.load(binary_file,gcc_id);
   program_physaddr_high = elf_binary_parser.getProgram_PhysAddr_high();
   program_physaddr_low  = elf_binary_parser.getProgram_PhysAddr_low();
   program_number_sections = program_physaddr_high.size();
   
   lmb_begin = memory.getLMB_begin();
   lmb_end   = memory.getLMB_end();
   opb       = memory.getOPB();
   opb_begin = memory.getOPB_begin();
   opb_end   = memory.getOPB_end();
     
   if(program_number_sections > 0)
   {
    if(opb == true)
    {
     if(opb_begin == (lmb_end + 1))
     {
      for(aux = 0;aux < program_number_sections;aux++)
        if(program_physaddr_low.get(aux) >= lmb_begin && program_physaddr_high.get(aux) <= opb_end)
          number_sections++;	 
     }
     else if(lmb_begin == (opb_end + 1))
     {
      for(aux = 0;aux < program_number_sections;aux++)
        if(program_physaddr_low.get(aux) >= opb_begin && program_physaddr_high.get(aux) <= lmb_end)
          number_sections++; 
     }
     else
     {
      for(aux = 0;aux < program_number_sections;aux++)
        if(program_physaddr_low.get(aux) >= lmb_begin && program_physaddr_high.get(aux) <= lmb_end)
    	  number_sections++;
 	 if(opb == true)
 	   for(aux = 0;aux < program_number_sections;aux++)
 		 if(program_physaddr_low.get(aux) >= opb_begin && program_physaddr_high.get(aux) <= opb_end)
 		   number_sections++; 
     }
    }
    else
    {
     for(aux = 0;aux < program_number_sections;aux++)
	   if(program_physaddr_low.get(aux) >= lmb_begin && program_physaddr_high.get(aux) <= lmb_end)
		 number_sections++;
    }
   }
   if(program_number_sections != number_sections)
	 throw new LoadProgramException("the binary program doesn't fit in the memory mapped.");
   
   if(function_exit == true)
     if(elf_binary_parser.getFunction_exit_address() == -1)
       throw new LoadProgramException("exit function not found.");
   
   elf_binary_parser.loadBinaryData();
   binary_data = elf_binary_parser.getBinaryData();

   memory.reset();
   
   do
   {
	address = binary_data.keySet().iterator();
	address_aux = address.next();	
	memory.putMemoryWord((int)address_aux,binary_data.get(address_aux));
    binary_data.remove(address_aux);
   }
   while(binary_data.size() > 0);
   
   if(elf_binary_parser.getFunction_exit_address() != -1)
   {
    exit = elf_binary_parser.getFunction_exit_address();
    cpu.createProgram_Exit(exit);
   }
   
   function_name  = elf_binary_parser.getFunction_name();
   function_begin = elf_binary_parser.getFunction_begin();
   function_end   = elf_binary_parser.getFunction_end();
  }
  catch(CPUException e)
  {
   throw new LoadProgramException(e.getMessage());	  
  }
  catch(MemoryException e)
  {
   throw new LoadProgramException(e.getMessage());	  
  }
  catch(LoadProgramException e)
  {
   throw new LoadProgramException(e.getMessage());	  
  }
  catch(ElfBinaryParserException e)
  {	 
   throw new LoadProgramException(e.getMessage());
  }
 } 

 // -----------------------------------
 // methods used to display information
 // -----------------------------------

 /**
  * Returns an list of the with the functions name.
  *
  * @return an list with the functions name.
  */
 public Map<Integer,String> getFunction_name()
 {
  return function_name;	 
 }
 
 /**
  * Returns an list with the first position memory address of the functions.
  *
  * @return an list with the first position memory address of the functions.
  */
 public Map<Integer,Long> getFunction_begin()
 {
  return function_begin;
 }
 
 /**
  * Returns an list with the last position memory address of the functions.
  *
  * @return an list with the last position memory address of the functions.
  */
 public Map<Integer,Long> getFunction_end()
 {
  return function_end; 
 }

 /**
  * Returns the memory address of the exit function.
  *
  * @return the memory address of the exit function.
  */
 public long getFunction_exit_address()
 {
  return exit;	 
 }
}
