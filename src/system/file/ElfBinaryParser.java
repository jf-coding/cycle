package system.file;

import java.nio.channels.*;
import java.nio.*;
import java.util.*;
import java.io.*;

import system.*;

/**
 * The executable and linking format (ELF) file parser class. This class performs the loading of the executable and linking format (ELF) file.
 */
public class ElfBinaryParser 
{
 /** The string table section names. */
 private String string_table_section_names;
 /** The string table symbol names. */
 private String string_table_symbol_names;
 /** The number of symbol table headers. */
 private int number_symbol_table_headers;
 /** The random access file stream, is used to read from file. */
 private RandomAccessFile file = null;
 /** The file channel (has a current position). */
 private FileChannel channel = null;
 /** The executable and linking format (ELF) file name (path and name). */
 private String file_name;
 /** The Section Header (Elf32_Shdr). */
 private Elf32_Shdr elf32_shdr[];
 /** The Program Header (Elf32_Phdr). */
 private Elf32_Phdr elf32_phdr[];
 /** The Symbol Table Entry (Elf32_Sym). */
 private Elf32_Sym  elf32_sym[];
 /** The ELF Header (Elf32_Ehdr). */
 private Elf32_Ehdr elf32_ehdr;
 /** The ELF Identification (e_ident[]). */
 private Elf_Ident ident;
 /** The list with the first position memory address of the physical program, is related to data in the on-chip peripheral memory. */
 private Map<Integer,Long> program_physaddr_high;
 /** The list with the last position memory address of the physical program, is related to data in the on-chip peripheral memory. */
 private Map<Integer,Long> program_physaddr_low;
 /** The binary data of the executable and linking format (ELF) file. */
 private Map<Long,Integer> binary_data;
 /** The list with the functions name. */
 private Map<Integer,String> function_name;
 /** The list with the first position memory address of the functions. */
 private Map<Integer,Long> function_begin;
 /** The list with the last position memory address of the functions. */
 private Map<Integer,Long> function_end;
 /** The memory address of the function exit. */
 private long exit = -1;
 /** The text section size. */
 private long text_size = 0;
 /** The data section size. */
 private long data_size = 0;
 /** The bss section size. */
 private long bss_size = 0;
 /** The ev_current. */
 private long ev_current;
 /** The ei_class. */
 private int  ei_class;
 /** The elf_file_header_read. */
 private boolean elf_file_header_read = false;
 /** The elf_file_ident_read. */
 private boolean elf_file_ident_read  = false;
 /** The data flag. */
 private boolean data;
 /** The shn_undef. */
 private final int SHN_UNDEF = 0;
 /** The shn_abs. */
 private final int SHN_ABS   = 0xfff1;
 
 // ------------------------------------------
 // methods used to read and load the elf file
 // ------------------------------------------
  
 /**
  * Loads the executable and linking format (ELF) file.
  *
  * @param file_name the executable and linking format (ELF) file name (path and name).
  * @param gcc_id the gcc id code of the processor.
  * @throws ElfBinaryParserException if any error occur when loads the executable and linking format (ELF) file.
  */
 public void load(String file_name,int gcc_id) throws ElfBinaryParserException
 {
  try
  {
   this.file_name = file_name;
   file = new RandomAccessFile(file_name,"r");
   channel = file.getChannel();
   loadHeader(gcc_id);
  }
  catch(FileNotFoundException e)
  {
   throw new ElfBinaryParserException("file not found.");
  }
  catch(SecurityException e)
  {
   throw new ElfBinaryParserException("don't have permission to access file.");
  }
  catch(ElfBinaryParserException e)
  {
   try
   {
	channel.close();
	file.close();
   }
   catch(Exception f)
   {
	throw new ElfBinaryParserException(e.getMessage() + " and can't close the file.");
   }      
   throw new ElfBinaryParserException(e.getMessage());
  }
 }

 /**
  * Loads the headers of the executable and linking format (ELF) file.
  *
  * @param gcc_id the gcc id code of the processor.
  * @throws ElfBinaryParserException if any error occur when loads the executable and linking format (ELF) file.
  */
 private void loadHeader(int gcc_id) throws ElfBinaryParserException
 {
  char string_table_section_names_aux[];
  char string_table_symbol_names_aux[];
  Vector<Integer> executable_sections;
  Elf32_Sym functions_order_temp[];
  Vector<Elf32_Sym> functions_temp;
  Map<Integer,String> function_name_temp;
  Map<Integer,Long> function_begin_temp;
  Map<Integer,Long> function_end_temp;
  int number_program_headers;
  int number_section_headers;
  MappedByteBuffer buffer;
  Elf32_Sym swap;
  boolean order;
  int aux_a;
  int aux_b;
	
  try
  {
   // load elf ident of the elf header	 
   ident = new Elf_Ident();
   ident.read(file);
   elf_file_ident_read = true;
   if((ident.e_ident[ident.EI_MAG0] != ident.ELFMAG0) || (ident.e_ident[ident.EI_MAG1] != ident.ELFMAG1) || (ident.e_ident[ident.EI_MAG2] != ident.ELFMAG2) || (ident.e_ident[ident.EI_MAG3] != ident.ELFMAG3))
	 throw new ElfBinaryParserException("not an ELF file type");
   if(ident.e_ident[ident.EI_CLASS] == ident.ELFCLASSNONE)
	 throw new ElfBinaryParserException("an ELF invalid class");
   if(ident.e_ident[ident.EI_DATA] != ident.ELFDATA2MSB)
	 throw new ElfBinaryParserException("not an 2's complement, big endian");
     
   
   
   if(ident.e_ident[ident.EI_CLASS] == ident.ELFCLASS32)
   {
	// load elf header
	elf32_ehdr = new Elf32_Ehdr();
	elf32_ehdr.read(file);
	if(elf32_ehdr.e_type != elf32_ehdr.ET_EXEC)
	  throw new ElfBinaryParserException("not an executable file");
	
	if(gcc_id != 0)
	{
	 if(elf32_ehdr.e_machine != gcc_id)
	   throw new ElfBinaryParserException("the elf file is implement for cpu with GCC id 0x" + Integer.toHexString(elf32_ehdr.e_machine) + " and the system cpu GCC id is 0x" + Integer.toHexString(gcc_id));
	}
	
	if(elf32_ehdr.e_version != 1)
	  throw new ElfBinaryParserException("not a valid version");
	if(elf32_ehdr.e_phnum < 1)
	  throw new ElfBinaryParserException("don't have program headers"); 
    if(elf32_ehdr.e_shnum < 1)
      throw new ElfBinaryParserException("don't have section headers");
    
   
    // load program headers
    file.seek(elf32_ehdr.e_phoff);
    elf32_phdr = new Elf32_Phdr[elf32_ehdr.e_phnum];
    program_physaddr_high = new HashMap<Integer,Long>();
    program_physaddr_low  = new HashMap<Integer,Long>();
    for(number_program_headers = 0;number_program_headers < elf32_ehdr.e_phnum;number_program_headers++)
    {
     elf32_phdr[number_program_headers] = new Elf32_Phdr();
 	 elf32_phdr[number_program_headers].read(file); 
 	 program_physaddr_low.put(number_program_headers,elf32_phdr[number_program_headers].p_paddr);
 	 program_physaddr_high.put(number_program_headers,elf32_phdr[number_program_headers].p_memsz + elf32_phdr[number_program_headers].p_paddr - 1);
    }
    for(aux_a = 0;aux_a < elf32_ehdr.e_phnum - 1;aux_a++)
      for(aux_b = aux_a + 1;aux_b < elf32_ehdr.e_phnum;aux_b++)
      {  
       if(program_physaddr_low.get(aux_b) >= program_physaddr_low.get(aux_a) && program_physaddr_low.get(aux_b) <= program_physaddr_high.get(aux_a))
         throw new ElfBinaryParserException("one section is in the range of other.");
       if(program_physaddr_high.get(aux_b) >= program_physaddr_low.get(aux_a) && program_physaddr_high.get(aux_b) <= program_physaddr_high.get(aux_a))
    	 throw new ElfBinaryParserException("one section is in the range of other.");
      }
    
    
    // load section headers
    executable_sections = new Vector<Integer>();
    file.seek(elf32_ehdr.e_shoff);
    elf32_shdr = new Elf32_Shdr[elf32_ehdr.e_shnum];
    for(number_section_headers = 0;number_section_headers < elf32_ehdr.e_shnum;number_section_headers++)
    {
     elf32_shdr[number_section_headers] = new Elf32_Shdr();
     elf32_shdr[number_section_headers].read(file);
     if((elf32_shdr[number_section_headers].sh_flags & elf32_shdr[number_section_headers].SHF_EXECINSTR) == elf32_shdr[number_section_headers].SHF_EXECINSTR)
       executable_sections.add(number_section_headers);
    }

    
    // load string table
    if(elf32_ehdr.e_shstrndx != SHN_UNDEF)
    {
     string_table_section_names_aux = new char[(int)elf32_shdr[elf32_ehdr.e_shstrndx].sh_size];
	 buffer = channel.map(FileChannel.MapMode.READ_ONLY,elf32_shdr[elf32_ehdr.e_shstrndx].sh_offset,elf32_shdr[elf32_ehdr.e_shstrndx].sh_size);
	 for(aux_a = 0;aux_a < elf32_shdr[elf32_ehdr.e_shstrndx].sh_size;aux_a++)
	   string_table_section_names_aux[aux_a] = (char)buffer.get(aux_a);
	 string_table_section_names = new String(string_table_section_names_aux);
    }
    else
      string_table_section_names = new String("");
   
    
    // load symbol table
    number_symbol_table_headers = 0;
    for(number_section_headers = 0;number_section_headers < elf32_ehdr.e_shnum;number_section_headers++)
    {
     if(elf32_shdr[number_section_headers].sh_type == Elf32_Shdr.SHT_SYMTAB)
     {
      if(elf32_shdr[number_section_headers].sh_addralign != 0x4)
    	throw new ElfBinaryParserException("the align of section .symtab is not a word"); 
      if(Math.IEEEremainder(elf32_shdr[number_section_headers].sh_size,elf32_shdr[number_section_headers].sh_entsize) != 0)
    	throw new ElfBinaryParserException("wrong number of sections on .symtab");  
      number_symbol_table_headers = (int)(elf32_shdr[number_section_headers].sh_size/elf32_shdr[number_section_headers].sh_entsize);
      if(number_symbol_table_headers == 0)
        break;
      elf32_sym = new Elf32_Sym[number_symbol_table_headers];
      file.seek(elf32_shdr[number_section_headers].sh_offset);
      for(aux_a = 0;aux_a < number_symbol_table_headers;aux_a++)
      {
       elf32_sym[aux_a] = new Elf32_Sym();
       elf32_sym[aux_a].read(file);  
      }
      if(elf32_shdr[number_section_headers].sh_link >= elf32_ehdr.e_shnum) 
    	throw new ElfBinaryParserException("wrong number section reference of .strtab on .symtab");
      if(elf32_shdr[(int)elf32_shdr[number_section_headers].sh_link].sh_type != Elf32_Shdr.SHT_STRTAB)
    	throw new ElfBinaryParserException("the reference .strtab section on .symtab is not a strtab section type"); 
      if((int)elf32_shdr[(int)elf32_shdr[number_section_headers].sh_link].sh_size < 1)
    	throw new ElfBinaryParserException("the reference .strtab section on .symtab is empty");
      string_table_symbol_names_aux = new char[(int)elf32_shdr[(int)elf32_shdr[number_section_headers].sh_link].sh_size];
  	  buffer = channel.map(FileChannel.MapMode.READ_ONLY,elf32_shdr[(int)elf32_shdr[number_section_headers].sh_link].sh_offset,elf32_shdr[(int)elf32_shdr[number_section_headers].sh_link].sh_size);
  	  for(aux_a = 0;aux_a < elf32_shdr[(int)elf32_shdr[number_section_headers].sh_link].sh_size;aux_a++)
  	    string_table_symbol_names_aux[aux_a] = (char)buffer.get(aux_a);
  	  string_table_symbol_names = new String(string_table_symbol_names_aux);
  	  break;
     }
    }
    
    
    // load the functions of symbol table
    functions_temp = new Vector<Elf32_Sym>();
	  for(aux_a = 0;aux_a < number_symbol_table_headers;aux_a++)
	    if((elf32_sym[aux_a].st_info & 0xf) == 0)
		  for(aux_b = 0;aux_b < executable_sections.size();aux_b++)
		    if(elf32_sym[aux_a].st_shndx == executable_sections.get(aux_b)) 
		      functions_temp.add(elf32_sym[aux_a]);
	functions_order_temp = new Elf32_Sym[functions_temp.size()];
	functions_temp.copyInto(functions_order_temp);
	do
 	{
 	 order = true;
 	 for(aux_a = 0;aux_a < functions_order_temp.length - 1;aux_a++)
 	 {
 	  if(functions_order_temp[aux_a].st_value > functions_order_temp[aux_a + 1].st_value)
   	  {
   	   order = false;
   	   swap = functions_order_temp[aux_a + 1];
   	   functions_order_temp[aux_a + 1] = functions_order_temp[aux_a];
   	   functions_order_temp[aux_a] = swap;
   	  }
 	 }
 	}
 	while(order == false);
	
	
	// load the names and functions limits of symbol table
	function_name  = new HashMap<Integer,String>();
 	function_begin = new HashMap<Integer,Long>();
 	function_end   = new HashMap<Integer,Long>();
 	for(aux_a = 0;aux_a < functions_order_temp.length;aux_a++)
 	{
 	 try
 	 {
 	  function_name.put(aux_a,string_table_symbol_names.substring((int)functions_order_temp[aux_a].st_name,string_table_symbol_names.indexOf('\0',(int)functions_order_temp[aux_a].st_name)));
 	 }
 	 catch(IndexOutOfBoundsException e)
	 {   
 	  throw new ElfBinaryParserException("the reference Symbol Table string is out of bounds");
	 }
 	 function_begin.put(aux_a,functions_order_temp[aux_a].st_value);
     if((functions_order_temp.length - 1) > aux_a)
     {
   	  if(functions_order_temp[aux_a].st_shndx != functions_order_temp[aux_a + 1].st_shndx)
   	    function_end.put(aux_a,elf32_shdr[functions_order_temp[aux_a].st_shndx].sh_size + elf32_shdr[functions_order_temp[aux_a].st_shndx].sh_addr - 1);
   	  else
   	    function_end.put(aux_a,functions_order_temp[aux_a + 1].st_value - 1);	
     }
     else
     {
   	  function_end.put(aux_a,elf32_shdr[functions_order_temp[aux_a].st_shndx].sh_size + functions_order_temp[aux_a].st_value - 1); 
     }
 	}
	
 	
 	// filter the valid functions
 	aux_b = 0;
 	function_name_temp = new HashMap<Integer,String>();
 	function_begin_temp = new HashMap<Integer,Long>();
 	function_end_temp = new HashMap<Integer,Long>();
 	for(aux_a = 0;aux_a < function_name.size();aux_a++)
 	{
 	 if((function_end.get(aux_a) - function_begin.get(aux_a) - 1) > 0)
 	 {
 	  function_name_temp.put(aux_b,function_name.get(aux_a));
 	  function_begin_temp.put(aux_b,function_begin.get(aux_a));
 	  function_end_temp.put(aux_b,function_end.get(aux_a));
 	  aux_b++;
 	 }
 	}
 	function_name = new HashMap<Integer,String>(function_name_temp);
 	function_begin = new HashMap<Integer,Long>(function_begin_temp);
 	function_end = new HashMap<Integer,Long>(function_end_temp);
 	
 	for(aux_a = 0;aux_a < function_name.size();aux_a++)
 	  if(function_name.get(aux_a).equals("exit"))	
 	  {
 	   exit = function_begin.get(aux_a); 	  
 	   break;
 	  }
 	
    // calculates the size of the program and sections
    for(number_section_headers = 0;number_section_headers < elf32_ehdr.e_shnum;number_section_headers++)
    {
	 if((elf32_shdr[number_section_headers].sh_flags & elf32_shdr[number_section_headers].SHF_WRITE) != 0 || (elf32_shdr[number_section_headers].sh_flags & elf32_shdr[number_section_headers].SHF_ALLOC) != 0 || (elf32_shdr[number_section_headers].sh_flags & elf32_shdr[number_section_headers].SHF_EXECINSTR) != 0)
     {	  
	  if((elf32_shdr[number_section_headers].sh_flags & elf32_shdr[number_section_headers].SHF_EXECINSTR) !=0 || (elf32_shdr[number_section_headers].sh_flags & elf32_shdr[number_section_headers].SHF_WRITE) ==0)
	    text_size = elf32_shdr[number_section_headers].sh_size + text_size;
	  else if(elf32_shdr[number_section_headers].sh_type != elf32_shdr[number_section_headers].SHT_NOBITS)
        data_size = elf32_shdr[number_section_headers].sh_size + data_size;
	  else
	    bss_size = elf32_shdr[number_section_headers].sh_size + bss_size; 
	 }
    }
    elf_file_header_read = true;
   }
   else if(ident.e_ident[4] == ident.ELFCLASS64)
	 throw new ElfBinaryParserException("the ELF 64-bit class not inplement");
   else
	 throw new ElfBinaryParserException("unknow ELF file class");
  }
  catch(ElfBinaryParserException e)
  {
   throw new ElfBinaryParserException(e.getMessage());
  }
  catch(EOFException e)
  {
   throw new ElfBinaryParserException("end of file");
  }
  catch(IOException e)
  {
   throw new ElfBinaryParserException(e.getMessage());
  }
 }
 
 /**
  * Loads binary data of executable and linking format (ELF) file.
  *
  * @throws ElfBinaryParserException if any error occur when loads the executable and linking format (ELF) file.
  */
 public void loadBinaryData() throws ElfBinaryParserException
 {
  MappedByteBuffer buffer;
  int  number_program_headers;
  int  memory_position;
  long address_aux;
  int  remainder;
  long address;
  int  value;
  
  try
  {
   if(elf_file_header_read)
   {
	// validate the program headers
	for(number_program_headers = 1;number_program_headers < elf32_ehdr.e_phnum;number_program_headers++)
	{
     if(elf32_phdr[number_program_headers].p_offset < (elf32_phdr[number_program_headers - 1].p_offset + elf32_phdr[number_program_headers - 1].p_filesz))
       throw new ElfBinaryParserException("the Program Header number " + (number_program_headers - 1) + " have the offset in the range of the Program Header number " + number_program_headers);
     if(elf32_phdr[number_program_headers].p_vaddr < (elf32_phdr[number_program_headers - 1].p_vaddr + elf32_phdr[number_program_headers - 1].p_memsz))
       throw new ElfBinaryParserException("the Program Header number " + (number_program_headers - 1) + " have the address in the range of the Program Header number " + number_program_headers);
	}   
	for(number_program_headers = 0;number_program_headers < elf32_ehdr.e_phnum;number_program_headers++)
	{ 
	 if(Math.IEEEremainder(elf32_phdr[number_program_headers].p_vaddr,4) != 0)
	  throw new ElfBinaryParserException("the Program Header number " + number_program_headers + " the begining address is not a 32bit word");	 
	 if(alignProgramHeader(elf32_phdr[number_program_headers].p_align) != 2)
	   throw new ElfBinaryParserException("the Program Header number " + number_program_headers + " have a wrong align");
	 if(elf32_phdr[number_program_headers].p_memsz < elf32_phdr[number_program_headers].p_filesz)
	   throw new ElfBinaryParserException("the Program Header number " + number_program_headers + " have memory size lower then file size");
	}   
	
	
	// load the program to the memory
 	binary_data = new HashMap<Long,Integer>();
    for(number_program_headers = 0;number_program_headers < elf32_ehdr.e_phnum;number_program_headers++)
    {
 	 address = elf32_phdr[number_program_headers].p_paddr;
 	 buffer = channel.map(FileChannel.MapMode.READ_ONLY,elf32_phdr[number_program_headers].p_offset,elf32_phdr[number_program_headers].p_filesz);
 	 if(elf32_phdr[number_program_headers].p_align == 0x4)
 	 {
 	  if(elf32_phdr[number_program_headers].p_filesz != 0)
 	  {
 	   address_aux = elf32_phdr[number_program_headers].p_vaddr + elf32_phdr[number_program_headers].p_filesz;
 	   remainder = (int)Math.IEEEremainder(address_aux,4);  
 	   address_aux = address_aux - remainder;
 	   memory_position = 0;
 	   while(address < address_aux)
 	   {
 		value = ((buffer.get(memory_position * 4) & 0xff) << 24) | ((buffer.get((memory_position * 4) + 1) & 0xff) << 16) | ((buffer.get((memory_position * 4) + 2) & 0xff) << 8) | (buffer.get((memory_position * 4) + 3) & 0xff); 	 
 		binary_data.put(address,value); 	   
 	 	address = address + 4;
 	 	memory_position++;
 	   }
 	   switch(remainder)
 	   {
 	    case 0:break;
 	    case 1:value = ((buffer.get(memory_position * 4) & 0xff) << 24);
 	           binary_data.put(address,value);
 	           address = address + 4;
 	           break;
 	    case 2:value = ((buffer.get(memory_position * 4) & 0xff) << 24) | ((buffer.get((memory_position * 4) + 1) & 0xff) << 16);
 	    	   binary_data.put(address,value);
               address = address + 4;
               break;
 	    case 3:value = ((buffer.get(memory_position * 4) & 0xff) << 24) | ((buffer.get((memory_position * 4) + 1) & 0xff) << 16) | ((buffer.get((memory_position * 4) + 2) & 0xff) << 8);
 	    	   binary_data.put(address,value);
	           address = address + 4;
	           break;
 	   }
 	  }
 	 }
 	 else if(elf32_phdr[number_program_headers].p_align == 0x1)
 	 {
 	  continue;
 	 }
 	 else
 	   throw new ElfBinaryParserException("the value to which the segments are aligned in memory and in the file isn't a word");
    }
    channel.close();
	file.close();
   }
   else
	 throw new ElfBinaryParserException("no elf binary file loaded.");
  }
  catch(EOFException e)
  {
   throw new ElfBinaryParserException("end of file");
  }
  catch(IOException e)
  {
   throw new ElfBinaryParserException("i/o file");
  }
  catch(ElfBinaryParserException e)
  {
   throw new ElfBinaryParserException(e.getMessage());
  }
 }
 
 // -------------------
 // methods auxiliaries
 // -------------------

 /**
  * Returns the Program Header (Elf32_Phdr) pseudo alignment.
  *
  * @param number the alignment of the Program Header (Elf32_Phdr).
  * @return the Program Header (Elf32_Phdr) pseudo alignment.<p>
  * equal to 2 - the Program Header (Elf32_Phdr) are align.<br>
  * less then 2 - the Program Header (Elf32_Phdr) aren't align.
  */
 private long alignProgramHeader(long number)
 {
  if(number == 0 || number == 1)
	return 2;
  while(number != 2)
	number = number - 2;
  return number;
 }
 
 //--------------------------------
 // methods used for accessing data
 //--------------------------------
 
 /**
  * Returns the list with the first position memory address of the physical program, is related to data in the on-chip peripheral memory.
  *
  * @return the list with the first position memory address of the physical program, is related to data in the on-chip peripheral memory.
  * @throws ElfBinaryParserException if any executable and linking format (ELF) file wasn't loaded.
  */
 public Map<Integer,Long> getProgram_PhysAddr_low() throws ElfBinaryParserException
 {
  if(program_physaddr_low == null)
	throw new ElfBinaryParserException("no file loaded.");
  return program_physaddr_low; 
 }
 
 /**
  * Returns the list with the last position memory address of the physical program, is related to data in the on-chip peripheral memory.
  *
  * @return the list with the last position memory address of the physical program, is related to data in the on-chip peripheral memory.
  * @throws ElfBinaryParserException if any executable and linking format (ELF) file wasn't loaded.
  */
 public Map<Integer,Long> getProgram_PhysAddr_high() throws ElfBinaryParserException
 {
  if(program_physaddr_high == null)
	throw new ElfBinaryParserException("no file loaded."); 
  return program_physaddr_high;	 
 }
 
 /**
  * Returns the binary data of the executable and linking format (ELF) file.
  *
  * @return the binary data of the executable and linking format (ELF) file.
  * @throws ElfBinaryParserException if any executable and linking format (ELF) file wasn't loaded.
  */
 public Map<Long,Integer> getBinaryData() throws ElfBinaryParserException
 {
  if(binary_data == null)
	throw new ElfBinaryParserException("no binary data loaded."); 
  return binary_data;	 
 }
 
 /**
  * Returns the list with the functions name.
  *
  * @return the list with the functions name.
  * @throws ElfBinaryParserException if any executable and linking format (ELF) file wasn't loaded.
  */
 public Map<Integer,String> getFunction_name() throws ElfBinaryParserException
 {
  if(function_name == null)
	throw new ElfBinaryParserException("no file header loaded."); 
  return function_name;	 
 }
 
 /**
  * Returns the list with the first position memory address of the functions.
  *
  * @return the list with the first position memory address of the functions.
  * @throws ElfBinaryParserException if any executable and linking format (ELF) file wasn't loaded.
  */
 public Map<Integer,Long> getFunction_begin() throws ElfBinaryParserException
 {
  if(function_begin == null)
	throw new ElfBinaryParserException("no file header loaded."); 
  return function_begin;
 }
 
 /**
  * Returns the list with the last position memory address of the functions.
  *
  * @return the list with the last position memory address of the functions.
  * @throws ElfBinaryParserException if any executable and linking format (ELF) file wasn't loaded.
  */
 public Map<Integer,Long> getFunction_end() throws ElfBinaryParserException
 {
  if(function_end == null)
	throw new ElfBinaryParserException("no file header loaded."); 
  return function_end; 
 }
 
 /**
  * Returns the memory address of the function exit.
  *
  * @return the memory address of the function exit.
  */
 public long getFunction_exit_address()
 {
  return exit;	 
 }
 
 // -----------------------------------
 // methods used to display information
 // -----------------------------------
 
 /**
  * Displays the headers contents of the executable and linking format (ELF) file.
  */
 public void showHeader()
 {
  int number_program_headers;
  int number_section_headers;
  int aux;
	 
  if(elf_file_ident_read)
  {
   System.out.println("ELF Header:");
   System.out.println(ident.toString());
   
   if(ei_class == ident.ELFCLASS32)
   {
    System.out.println(elf32_ehdr.toString());
	   
    if(elf32_ehdr.e_shnum > 0)
    {
     System.out.println("");
     System.out.println("Section Headers:");
     System.out.println("  [Nr] Name              Type            Addr     Off    Size   ES Flg Lk Inf Al");
	   
     for(number_section_headers = 0;number_section_headers < elf32_ehdr.e_shnum;number_section_headers++)
	   System.out.println("  [" + Util.toDecStringSpace(number_section_headers,2) + "] " + elf32_shdr[number_section_headers]);
     System.out.println("Key to Flags:");
     System.out.println("  W (write), A (alloc), X (execute), M (merge), S (strings)");
     System.out.println("  I (info), L (link order), G (group), x (unknown)");
     System.out.println("  O (extra OS processing required) o (OS specific), p (processor specific)");
    }
    else
    {
     System.out.println("");
     System.out.println("There are no section headers in this file.");
    }
    if(elf32_ehdr.e_phnum > 0)
    {
     System.out.println("");
     System.out.println("Program Headers:");
     System.out.println("  Type           Offset   VirtAddr   PhysAddr   FileSiz  MemSiz   Flg Align");
   
     for(number_program_headers = 0;number_program_headers < elf32_ehdr.e_phnum;number_program_headers++)
	   System.out.println(elf32_phdr[number_program_headers]);
	   
     System.out.println("");
     System.out.println(" Section to Segment mapping:");
     System.out.println("  Segment Sections...");

     for(number_program_headers = 0;number_program_headers < elf32_ehdr.e_phnum;number_program_headers++)
     {
	  System.out.print("   " + Util.toDecStringZero(number_program_headers,2) + "     ");
	  for(number_section_headers = 0;number_section_headers < elf32_ehdr.e_shnum;number_section_headers++)
	  {
       if((elf32_shdr[number_section_headers].sh_flags & elf32_shdr[number_section_headers].SHF_WRITE) != 0 || (elf32_shdr[number_section_headers].sh_flags & elf32_shdr[number_section_headers].SHF_ALLOC) != 0 || (elf32_shdr[number_section_headers].sh_flags & elf32_shdr[number_section_headers].SHF_EXECINSTR) != 0)
	   {
	    if(number_program_headers == elf32_ehdr.e_phnum - 1)
	    {
	     if(elf32_shdr[number_section_headers].sh_addr >= elf32_phdr[number_program_headers].p_paddr && elf32_shdr[number_section_headers].sh_size != 0)
	     {
	      try
	      {
	       System.out.print(string_table_section_names.substring((int)elf32_shdr[number_section_headers].sh_name,string_table_section_names.indexOf('\0',(int)elf32_shdr[number_section_headers].sh_name)));
	      }
	      catch(IndexOutOfBoundsException e)
	  	  {   
	       System.out.println("!ELF error");
	  	  }
	      System.out.print(" ");
	     }
	    }
	    else if(elf32_shdr[number_section_headers].sh_addr >= elf32_phdr[number_program_headers].p_paddr && elf32_shdr[number_section_headers].sh_addr < elf32_phdr[number_program_headers + 1].p_paddr && elf32_shdr[number_section_headers].sh_size != 0)	  
	    {
	     try
	     {
	      System.out.print(string_table_section_names.substring((int)elf32_shdr[number_section_headers].sh_name,string_table_section_names.indexOf('\0',(int)elf32_shdr[number_section_headers].sh_name)));
	     }
	     catch(IndexOutOfBoundsException e)
	 	 {   
	      System.out.println("!ELF error");	 
	 	 }
	     System.out.print(" ");
	    }
	    else	   
		  continue;	    
	   }
	  }
	  System.out.print("\n");
     }
    }
    else
    {
     System.out.println("");
     System.out.println("There are no program headers in this file.");
    }
    System.out.println("");
    System.out.println("Symbol table '.symtab' contains " + number_symbol_table_headers + " entries:");
    System.out.println("   Num:    Value  Size Type    Bind   Vis      Ndx Name");
    
    for(aux = 0;aux < number_symbol_table_headers;aux++)
    {
     System.out.println(Util.toDecStringSpace(aux,6) + ":" + elf32_sym[aux].toString());
    }
   }
  }
 }

 /**
  * Displays the size of the components of the executable and linking format (ELF) file.
  */
 public void showSize()
 {
  if(elf_file_header_read)
  {
   System.out.println("The sizes of sections inside binary file:");
   System.out.println("   text    data     bss     dec     hex filename");
   System.out.println(Util.toDecStringSpace(text_size,7) + Util.toDecStringSpace(data_size,8) + Util.toDecStringSpace(bss_size,8) + Util.toDecStringSpace((text_size + data_size + bss_size),8) + Util.toHexStringSpace((text_size + data_size + bss_size),8) + " " + file_name);
  }
 }
 
 // -----------------------------------------------
 // methods used to access the data of the elf file
 // -----------------------------------------------
 
 /**
  * Reads an unsigned half data type (32 bit data type) from the executable and linking format (ELF) file.
  *
  * @param file the random access file stream of the executable and linking format (ELF) file.
  * @return the unsigned half data type (32 bit data type).
  * @throws IOException if any error occur when reads the executable and linking format (ELF) file.
  */
 private int readElf32_Half(RandomAccessFile file) throws IOException
 {
  if(data)
	return file.readUnsignedShort();
  else
    return file.readUnsignedByte() | (file.readUnsignedByte() << 8);
 }

 /**
  * Reads an unsigned word data type (32 bit data type) from the executable and linking format (ELF) file.
  *
  * @param file the random access file stream of the executable and linking format (ELF) file.
  * @return the unsigned word data type (32 bit data type).
  * @throws IOException if any error occur when reads the executable and linking format (ELF) file.
  */
 private long readElf32_Word(RandomAccessFile file) throws IOException
 {
  if(data)
	return (long) file.readInt() & 0xFFFFFFFFL;
  else 
  {
   long aux = file.readUnsignedByte() | (file.readUnsignedByte() << 8) | (file.readUnsignedByte() << 16) | (file.readUnsignedByte() << 24);
   return aux & 0xFFFFFFFFL;
  }
 }

 /**
  * Reads an 32 bit memory address from the executable and linking format (ELF) file.
  *
  * @param file the random access file stream of the executable and linking format (ELF) file.
  * @return the 32 bit memory address.
  * @throws IOException if any error occur when reads the executable and linking format (ELF) file.
  */
 private long readElf32_Addr(RandomAccessFile file) throws IOException
 {
  if(data)
	return (long) file.readInt() & 0xFFFFFFFFL;
  else 
  {
   long aux = file.readUnsignedByte() | (file.readUnsignedByte() << 8) | (file.readUnsignedByte() << 16) | (file.readUnsignedByte() << 24);
   return (aux & 0xFFFFFFFFL);
  }
 }

 /**
  * Reads an 32 bit file offset from the executable and linking format (ELF) file.
  *
  * @param file the random access file stream of the executable and linking format (ELF) file.
  * @return the 32 bit file offset.
  * @throws IOException if any error occur when reads the executable and linking format (ELF) file.
  */
 private long readElf32_Off(RandomAccessFile file) throws IOException
 {
  if(data)
	return (long) file.readInt() & 0xFFFFFFFFL;
  else 
  {
   long aux = file.readUnsignedByte() | (file.readUnsignedByte() << 8) | (file.readUnsignedByte() << 16) | (file.readUnsignedByte() << 24);
   return (aux & 0xFFFFFFFFL);
  }
 }

 // -------------------------------------------------------
 // classes used to load and access the headers of elf file
 // -------------------------------------------------------
 
 /**
  * The ELF Identification (e_ident[]) class.
  */
 private class Elf_Ident
 {
  /** The EI_MAG0. */
  private final int EI_MAG0 = 0;
  /** The EI_MAG1. */
  private final int EI_MAG1 = 1;
  /** The EI_MAG2. */
  private final int EI_MAG2 = 2;
  /** The EI_MAG3. */
  private final int EI_MAG3 = 3;
  /** The EI_CLASS. */
  private final int EI_CLASS = 4;
  /** The EI_DATA. */
  private final int EI_DATA = 5;
  /** The EI_VERSION. */
  private final int EI_VERSION = 6;
  /** The EI_OSABI. */
  private final int EI_OSABI = 7;
  /** The EI_ABIVERSION. */
  private final int EI_ABIVERSION = 8;
  /** The EI_NIDENT. */
  private final int EI_NIDENT = 16;
  /** The e_ident. */
  private final byte[] e_ident = new byte[EI_NIDENT];
  /** The ELFMAG0. */
  private final byte ELFMAG0 = 0x7f;
  /** The ELFMAG1. */
  private final byte ELFMAG1 = (byte) 'E';
  /** The ELFMAG2. */
  private final byte ELFMAG2 = (byte) 'L';
  /** The ELFMAG3. */
  private final byte ELFMAG3 = (byte) 'F';
  /** The ELFCLASSNONE. */
  private final int ELFCLASSNONE = 0;
  /** The ELFCLASS32. */
  private final int ELFCLASS32 = 1;
  /** The ELFCLASS64. */
  private final int ELFCLASS64 = 2;
  /** The ELFDATANONE. */
  private final int ELFDATANONE = 0;
  /** The ELFDATA2LSB. */
  private final int ELFDATA2LSB = 1; 
  /** The ELFDATA2MSB. */
  private final int ELFDATA2MSB = 2;
  /** The ELFOSABI_SYSV. */
  private final int ELFOSABI_SYSV =	0;
  /** The ELFOSABI_HPUX. */
  private final int ELFOSABI_HPUX =	1;
  /** The ELFOSABI_LINUX. */
  private final int ELFOSABI_LINUX = 3;
  /** The ELFOSABI_STANDALONE. */
  private final int ELFOSABI_STANDALONE = 255;

  // ------------------------------------------
  // methods used to read and load the elf file
  // ------------------------------------------
  
  /**
   * Reads the ELF Identification (e_ident[]).
   *
   * @param file the random access file stream of the executable and linking format (ELF) file.
   * @throws IOException if any error occur when reads the executable and linking format (ELF) file.
   */
  private void read(RandomAccessFile file) throws IOException 
  {
   file.readFully(e_ident);
   if(e_ident[EI_MAG0] != ELFMAG0 || e_ident[EI_MAG1] != ELFMAG1 || e_ident[EI_MAG2] != ELFMAG2 || e_ident[EI_MAG3] != ELFMAG3)
   {
    file.close();
    throw new IOException("not an ELF file");
   }
   ei_class = e_ident[EI_CLASS];
   if(e_ident[EI_DATA] == 1)
	 data = false;
   if(e_ident[EI_DATA] == 2)
	 data = true;
  }
  
  // -----------------------------------
  // methods used to display information
  // -----------------------------------
  
  /**
   * Returns the content of the ELF Identification (e_ident[]).
   *
   * @return the content of the ELF Identification (e_ident[]).
   * @see java.lang.Object#toString()
   */
  public String toString() 
  {
   StringBuffer string = new StringBuffer();

   string.append("  Magic:   " + Util.toHexString(e_ident[EI_MAG0], 2) + " " + Util.toHexString(e_ident[EI_MAG1], 2) + " " + Util.toHexString(e_ident[EI_MAG2], 2) + " " + Util.toHexString(e_ident[EI_MAG3], 2));
   string.append(" " + Util.toHexString(e_ident[EI_CLASS], 2) + " " + Util.toHexString(e_ident[EI_DATA], 2) + " " + Util.toHexString(e_ident[EI_VERSION], 2) + " " + Util.toHexString(e_ident[EI_OSABI], 2));
   string.append(" " + Util.toHexString(e_ident[EI_ABIVERSION], 2) + " " + Util.toHexString(e_ident[9], 2) + " " + Util.toHexString(e_ident[10], 2) + " " + Util.toHexString(e_ident[11], 2));
   string.append(" " + Util.toHexString(e_ident[12], 2) + " " + Util.toHexString(e_ident[13], 2) + " " + Util.toHexString(e_ident[14], 2) + " " + Util.toHexString(e_ident[15], 2) + "\n");
   string.append("  Class:                             ");
   switch(e_ident[EI_CLASS]) 
   {
	case ELFCLASSNONE: string.append("Invalid class\n"); break;
	case ELFCLASS32:   string.append("ELF32\n");         break;
	case ELFCLASS64:   string.append("ELF64\n");         break;
	default:           string.append(e_ident[EI_CLASS] + "\n");
   }
   string.append("  Data:                              ");
   switch(e_ident[EI_DATA]) 
   {
	case ELFDATANONE: string.append("Invalid data encoding\n");         break;
	case ELFDATA2LSB: string.append("2's complement, little endian\n"); break;
	case ELFDATA2MSB: string.append("2's complement, big endian\n");    break;
	default:          string.append(e_ident[EI_DATA] + "\n");
   }
   string.append("  Version:                           " + e_ident[EI_VERSION]);
   if(e_ident[EI_VERSION] == 0)
	 string.append(" (invalid)\n");
   else if(e_ident[EI_VERSION] == ev_current)
	 string.append(" (current)\n");
   else
	 string.append(" <unknown: 0x" + Integer.toHexString(e_ident[EI_VERSION]) +">\n");  
   string.append("  OS/ABI:                            ");
   switch((int)e_ident[EI_OSABI]) 
   {
	case ELFOSABI_SYSV:       string.append("UNIX - System V\n");                   break;
	case ELFOSABI_HPUX:       string.append("HP-UX operating system\n");            break;
	case ELFOSABI_LINUX:      string.append("Gnu/Linux\n");                         break;
	case ELFOSABI_STANDALONE: string.append("Standalone (embedded) application\n"); break;
	default:                  string.append(e_ident[EI_OSABI] + "\n");
   }
   string.append("  ABI Version:                       " + e_ident[EI_ABIVERSION]);

   return string.toString();
  }
 }

 /**
  * The ELF Header (Elf32_Ehdr) class.
  */
 private class Elf32_Ehdr
 {
  /** The e_type. */
  private int e_type;
  /** The e_machine. */
  private int e_machine;
  /** The e_version. */
  private long e_version;
  /** The e_entry. */
  private long e_entry;
  /** The e_phoff. */
  private long e_phoff;
  /** The e_shoff. */
  private long e_shoff;
  /** The e_flags. */
  private long e_flags;
  /** The e_ehsize. */
  private int e_ehsize;
  /** The e_phentsize. */
  private int e_phentsize;
  /** The e_phnum. */
  private int e_phnum;
  /** The e_shentsize. */
  private int e_shentsize;
  /** The e_shnum. */
  private int e_shnum;
  /** The e_shstrndx. */
  private int e_shstrndx;
  /** The ET_NONE. */
  private final int ET_NONE = 0;
  /** The ET_REL. */
  private final int ET_REL  = 1;
  /** The ET_EXEC. */
  private final int ET_EXEC = 2;
  /** The ET_DYN. */
  private final int ET_DYN  = 3;
  /** The ET_CORE. */
  private final int ET_CORE = 4;
  /** The EM_NONE. */
  private final int EM_NONE = 0;
  /** The EM_386. */
  private final int EM_386  = 3;
  /** The EM_XILINX_MICROBLAZE. */
  private final int EM_XILINX_MICROBLAZE = 0xbaab;

  // ------------------------------------------
  // methods used to read and load the elf file
  // ------------------------------------------

  /**
   * Reads the content of the ELF Header (Elf32_Ehdr).
   *
   * @param file the random access file stream of the executable and linking format (ELF) file.
   * @throws IOException if any error occur when reads the executable and linking format (ELF) file.
   */
  private void read(RandomAccessFile file) throws IOException 
  {
   e_type =      readElf32_Half(file);
   e_machine =   readElf32_Half(file);
   e_version =   readElf32_Word(file);
   e_entry =     readElf32_Addr(file);
   e_phoff =     readElf32_Off(file);
   e_shoff =     readElf32_Off(file);
   e_flags =     readElf32_Word(file);
   e_ehsize =    readElf32_Half(file);
   e_phentsize = readElf32_Half(file);
   e_phnum =     readElf32_Half(file);
   e_shentsize = readElf32_Half(file);
   e_shnum =     readElf32_Half(file);
   e_shstrndx =  readElf32_Half(file); 
   ev_current =  e_version; 
  }

  // -----------------------------------
  // methods used to display information
  // -----------------------------------
  
  /**
   * Returns the content of the ELF Header (Elf32_Ehdr).
   *
   * @return the content of the ELF Header (Elf32_Ehdr).
   * @see java.lang.Object#toString()
   */
  public String toString() 
  {
   StringBuffer string = new StringBuffer();

   string.append("  Type:                              ");
   switch(e_type) 
   {
	case ET_NONE: string.append("NONE").append(" (No file type)\n");      break;
	case ET_REL:  string.append("REL").append(" (Relocatable file)\n");   break;
	case ET_EXEC: string.append("EXEC").append(" (Executable file)\n");   break;
	case ET_DYN:  string.append("DYN").append(" (Shared object file)\n"); break;
	case ET_CORE: string.append("CORE").append(" (Core file)\n");         break;
	default:      string.append("<unknow>: " + e_type + "\n");
   }
   string.append("  Machine:                           ");
   switch(e_machine) 
   {
    case EM_NONE:              string.append("No machine\n");        break;
	case EM_386:               string.append("Intel 80386\n");       break;
	case EM_XILINX_MICROBLAZE: string.append("Xilinx MicroBlaze\n"); break;
	default:                   string.append("<unknow>: " + e_machine + "\n");
   }
   string.append("  Version:                           0x" + Long.toHexString(e_version) + "\n");
   string.append("  Entry point address:               0x" + Long.toHexString(e_entry) + "\n");
   string.append("  Start of program headers:          "   + e_phoff + " (bytes into file)\n");
   string.append("  Start of section headers:          "   + e_shoff + " (bytes into file)\n");
   string.append("  Flags:                             0x" + Long.toHexString(e_flags) + "\n");
   string.append("  Size of this header:               "   + e_ehsize + " (bytes)\n");
   string.append("  Size of program headers:           "   + e_phentsize + " (bytes)\n");
   string.append("  Number of program headers:         "   + e_phnum + "\n");
   string.append("  Size of section headers:           "   + e_shentsize + " (bytes)\n");
   string.append("  Number of section headers:         "   + e_shnum + "\n");
   string.append("  Section header string table index: "   + e_shstrndx);

   return string.toString();
  }
 }

 /**
  * The Section Header (Elf32_Shdr) class.
  */
 private class Elf32_Shdr
 {
  /** The sh_name. */
  private long sh_name;
  /** The sh_type. */
  private long sh_type;
  /** The sh_flags. */
  private long sh_flags;
  /** The sh_addr. */
  private long sh_addr;
  /** The sh_offset. */
  private long sh_offset;
  /** The sh_size. */
  private long sh_size;
  /** The sh_link. */
  private long sh_link;
  /** The sh_info. */
  private long sh_info;
  /** The sh_addralign. */
  private long sh_addralign;
  /** The sh_entsize. */
  private long sh_entsize;
  /** The SHT_NULL. */
  private final int SHT_NULL = 0;
  /** The SHT_PROGBITS. */
  private final int SHT_PROGBITS = 1;
  /** The Constant SHT_SYMTAB. */
  private static final int SHT_SYMTAB = 2;
  /** The Constant SHT_STRTAB. */
  private static final int SHT_STRTAB = 3;
  /** The SHT_RELA. */
  private final int SHT_RELA = 4;
  /** The SHT_HASH. */
  private final int SHT_HASH = 5;
  /** The SHT_DYNAMIC. */
  private final int SHT_DYNAMIC = 6;
  /** The SHT_NOTE. */
  private final int SHT_NOTE = 7;
  /** The SHT_NOBITS. */
  private final int SHT_NOBITS = 8;
  /** The SHT_REL. */
  private final int SHT_REL = 9;
  /** The SHT_SHLIB. */
  private final int SHT_SHLIB = 10;
  /** The SHT_DYNSYM. */
  private final int SHT_DYNSYM = 11;
  /** The SHF_WRITE. */
  private final int SHF_WRITE = 1 << 0;
  /** The SHF_ALLOC. */
  private final int SHF_ALLOC = 1 << 1 ; 
  /** The SHF_EXECINSTR. */
  public  final int SHF_EXECINSTR = 1 << 2;
  /** The SHF_MERGE. */
  private final int SHF_MERGE = 1 << 4;
  /** The SHF_STRINGS. */
  private final int SHF_STRINGS = 1 << 5;
  /** The SHF_INFO_LINK. */
  private final int SHF_INFO_LINK = 1 << 6;
  /** The SHF_LINK_ORDER. */
  private final int SHF_LINK_ORDER = 1 << 7;
  /** The SHF_OS_NONCONFORMING. */
  private final int SHF_OS_NONCONFORMING = 1 << 8;
  /** The SHF_MASKOS. */
  private final int SHF_MASKOS = 0x0f000000;
  /** The SHF_MASKPROC. */
  private final int SHF_MASKPROC = 0xf0000000;
 
  // ------------------------------------------
  // methods used to read and load the elf file
  // ------------------------------------------

  /**
   * Reads the Section Header (Elf32_Shdr).
   *
   * @param file the random access file stream of the executable and linking format (ELF) file.
   * @throws IOException if any error occur when reads the executable and linking format (ELF) file.
   */
  private void read(RandomAccessFile file) throws IOException 
  {
   sh_name =      readElf32_Word(file);
   sh_type =      readElf32_Word(file);
   sh_flags =     readElf32_Word(file);
   sh_addr =      readElf32_Addr(file);
   sh_offset =    readElf32_Off(file);
   sh_size =      readElf32_Word(file);
   sh_link =      readElf32_Word(file);
   sh_info =      readElf32_Word(file);
   sh_addralign = readElf32_Word(file);
   sh_entsize =   readElf32_Word(file);
  }
  
  // -----------------------------------
  // methods used to display information
  // -----------------------------------

  /**
   * Returns the content of the Section Header (Elf32_Shdr).
   *
   * @return the content of the Section Header (Elf32_Shdr).
   * @see java.lang.Object#toString()
   */
  public String toString() 
  {
   StringBuffer string_flag = new StringBuffer();
   StringBuffer string = new StringBuffer();
   long sh_flags_aux;
   long flag;

   try
   {
    string.append(Util.toStringFixed(string_table_section_names.substring((int)sh_name,string_table_section_names.indexOf('\0',(int)sh_name)),17));
   }
   catch(IndexOutOfBoundsException e)
   {
	string.append(Util.toStringFixed("!ELF error",17)); 
   }
   string.append(" ");
   switch((int)sh_type) 
   {
	case SHT_NULL:     string.append("NULL       "); break;
	case SHT_PROGBITS: string.append("PROGBITS   "); break;
	case SHT_SYMTAB:   string.append("SYMTAB     "); break;
	case SHT_STRTAB:   string.append("STRTAB     "); break;
	case SHT_RELA:     string.append("RELA       "); break;
	case SHT_HASH:     string.append("HASH       "); break;
	case SHT_DYNAMIC:  string.append("DYNAMIC    "); break;
	case SHT_NOTE:     string.append("NOTE       "); break;
	case SHT_NOBITS:   string.append("NOBITS     "); break;
	case SHT_REL:      string.append("REL        "); break;
	case SHT_SHLIB:    string.append("SHLIB      "); break;
	case SHT_DYNSYM:   string.append("DYNSYM     ");
   }
   string.append("     ");
   string.append(Util.toHexString(sh_addr,8));
   string.append(" ");
   string.append(Util.toHexString(sh_offset,6));
   string.append(" ");
   string.append(Util.toHexString(sh_size,6));
   string.append(" ");
   string.append(Util.toHexString(sh_entsize,2));
   string.append(" ");
   sh_flags_aux = sh_flags;
   while(sh_flags_aux != 0)
   {
    flag = sh_flags_aux & - sh_flags_aux;
    sh_flags_aux &= ~ flag;
    switch((int)flag)
	{
	 case SHF_WRITE:            string_flag.append("W"); break;
	 case SHF_ALLOC:            string_flag.append("A"); break;
	 case SHF_EXECINSTR:        string_flag.append("X"); break;
	 case SHF_MERGE:            string_flag.append("M"); break;
	 case SHF_STRINGS:          string_flag.append("S"); break;
	 case SHF_INFO_LINK:        string_flag.append("I"); break;
	 case SHF_LINK_ORDER:       string_flag.append("L"); break;
	 case SHF_OS_NONCONFORMING: string_flag.append("O"); break;
	 default:if((flag & SHF_MASKOS) > 0)
	         {
		      string_flag.append("o");
		      sh_flags_aux &= ~ SHF_MASKOS;
	         }
	         else if((flag & SHF_MASKPROC) > 0)
	         {
	          string_flag.append("p");
	          sh_flags_aux &= ~ SHF_MASKPROC;
	         }
	         else
	           string_flag.append("x");
	}
   }  
   switch(string_flag.length())
   {
    case 0: string.append("   ");              break;
    case 1: string.append("  " + string_flag); break;
    case 2: string.append(" " + string_flag);  break;
    case 3: string.append(string_flag);
   }
   string.append(" ");
   string.append(Util.toDecStringSpace(sh_link,2));
   string.append(" ");
   string.append(Util.toHexStringSpace(sh_info,3));
   string.append(" ");
   string.append(Util.toDecStringSpace(sh_addralign,2));

   return string.toString();
  }
 }
 
 /**
  * The Program Header (Elf32_Phdr) class.
  */
 private class Elf32_Phdr
 { 
  /** The p_type. */
  private long p_type;
  /** The p_offset. */
  private long p_offset;
  /** The p_vaddr. */
  private long p_vaddr;
  /** The p_paddr. */
  private long p_paddr;
  /** The p_filesz. */
  private long p_filesz;
  /** The p_memsz. */
  private long p_memsz;
  /** The p_flags. */
  private long p_flags;
  /** The p_align. */
  private long p_align;
  /** The PT_NULL. */
  private final int PT_NULL = 0;
  /** The PT_LOAD. */
  private final int PT_LOAD = 1;
  /** The PT_DYNAMIC. */
  private final int PT_DYNAMIC = 2;
  /** The PT_INTERP. */
  private final int PT_INTERP = 3;
  /** The PT_NOTE. */
  private final int PT_NOTE = 4;
  /** The PT_SHLIB. */
  private final int PT_SHLIB = 5;
  /** The PT_PHDR. */
  private final int PT_PHDR = 6;
  /** The PT_LOOS. */
  private final int PT_LOOS = 0x60000000;
  /** The PT_HIOS. */
  private final int PT_HIOS = 0x6fffffff;
  /** The PT_LOPROC. */
  private final int PT_LOPROC = 0x70000000;
  /** The PT_HIPROC. */
  private final int PT_HIPROC = 0x7FFFFFFF;
  /** The PF_X. */
  private final int PF_X = 0x1;
  /** The PF_W. */
  private final int PF_W = 0x2;
  /** The PF_R. */
  private final int PF_R = 0x4; 	
  
  // ------------------------------------------
  // methods used to read and load the elf file
  // ------------------------------------------

  /**
   * Reads the Program Header (Elf32_Phdr).
   *
   * @param file the random access file stream of the executable and linking format (ELF) file.
   * @throws IOException if any error occur when reads the executable and linking format (ELF) file.
   */
  private void read(RandomAccessFile file) throws IOException
  {
   p_type =   readElf32_Word(file);
   p_offset = readElf32_Off(file);
   p_vaddr =  readElf32_Addr(file);
   p_paddr =  readElf32_Addr(file);
   p_filesz = readElf32_Word(file);
   p_memsz =  readElf32_Word(file);
   p_flags =  readElf32_Word(file);
   p_align =  readElf32_Word(file);
  }
  
  // -----------------------------------
  // methods used to display information
  // -----------------------------------
  
  /**
   * Returns the content of the Program Header (Elf32_Phdr).
   *
   * @return the content of the Program Header (Elf32_Phdr).
   * @see java.lang.Object#toString()
   */
  public String toString() 
  {
   StringBuffer string = new StringBuffer();

   string.append("  ");
   switch((int)p_type) 
   {
	case PT_NULL:    string.append("NULL          "); break;
	case PT_LOAD:    string.append("LOAD          "); break;
	case PT_DYNAMIC: string.append("DYNAMIC       "); break;
	case PT_INTERP:  string.append("INTERP        "); break;
	case PT_NOTE:    string.append("NOTE          "); break;
	case PT_SHLIB:   string.append("SHLIB         "); break;
	case PT_PHDR:    string.append("PHDR          "); break;
	default:if((p_type >= PT_LOPROC) && (p_type <= PT_HIPROC))
		      string.append("LOPROC        ");
		    else if((p_type >= PT_LOOS) && (p_type <= PT_HIOS))
		      string.append("LOOS          ");
		    else
		      string.append("<unknown>: 0x" + Long.toHexString(p_type));
   }
   string.append(" ");
   string.append("0x").append(Util.toHexString(p_offset,6));
   string.append(" ");
   string.append("0x").append(Util.toHexString(p_vaddr,8));
   string.append(" ");
   string.append("0x").append(Util.toHexString(p_paddr,8));
   string.append(" ");
   string.append("0x").append(Util.toHexString(p_filesz,6));
   string.append(" ");
   string.append("0x").append(Util.toHexString(p_memsz,6));
   string.append(" ");
   if((p_flags & PF_R) != 0)
	 string.append("R");
   else
	 string.append(" ");
   if((p_flags & PF_W) != 0)
	 string.append("W");
   else
	 string.append(" ");
   if((p_flags & PF_X) != 0)
	 string.append("E");
   else
	 string.append(" ");
   string.append(" ");
   string.append("0x").append(Long.toHexString(p_align));
   return string.toString();
  }
 }
 
 /**
  * The Symbol Table Entry (Elf32_Sym) class.
  */
 private class Elf32_Sym
 {
  /** The st_name. */
  private long st_name;
  /** The st_value. */
  private long st_value;
  /** The st_size. */
  private long st_size;
  /** The st_info. */
  private byte st_info;
  /** The st_other. */
  private byte st_other;
  /** The st_shndx. */
  private int  st_shndx;
  /** The STT_NOTYPE. */
  private final int STT_NOTYPE  = 0;
  /** The STT_OBJECT. */
  private final int STT_OBJECT  = 1;
  /** The STT_FUNC. */
  private final int STT_FUNC    = 2;
  /** The STT_SECTION. */
  private final int STT_SECTION = 3;
  /** The STT_FILE. */
  private final int STT_FILE    = 4;
  /** The STB_LOCAL. */
  private final int STB_LOCAL  = 0;
  /** The STB_GLOBAL. */
  private final int STB_GLOBAL = 1;
  /** The STB_WEAK. */
  private final int STB_WEAK   = 2;
  
  // ------------------------------------------
  // methods used to read and load the elf file
  // ------------------------------------------

  /**
   * Reads the Symbol Table Entry (Elf32_Sym).
   *
   * @param file the random access file stream of the executable and linking format (ELF) file.
   * @throws IOException if any error occur when reads the executable and linking format (ELF) file.
   */
  private void read(RandomAccessFile file) throws IOException
  {
   byte aux[] = new byte[2];
   
   st_name  = readElf32_Word(file);
   st_value = readElf32_Addr(file);
   st_size  = readElf32_Word(file);
   file.readFully(aux);
   st_info  = aux[0];
   st_other = aux[1];
   st_shndx = readElf32_Half(file);
  }
  
  // -----------------------------------
  // methods used to display information
  // -----------------------------------
  
  /**
   * Returns the content of the Symbol Table Entry (Elf32_Sym).
   *
   * @return the content of the Symbol Table Entry (Elf32_Sym).
   * @see java.lang.Object#toString()
   */
  public String toString()
  {
   StringBuffer string = new StringBuffer();
   
   string.append(" ");
   string.append(Util.toHexString(st_value,8));
   string.append(" ");
   string.append(Util.toDecStringSpace(st_size,5));
   string.append(" ");
   switch(st_info & 0xf)
   {
    case STT_NOTYPE:  string.append("NOTYPE "); break;
    case STT_OBJECT:  string.append("OBJECT "); break;
    case STT_FUNC:    string.append("FUNC   "); break;
    case STT_SECTION: string.append("SECTION"); break;
    case STT_FILE:    string.append("FILE   "); break;
    default:          string.append(Util.toDecStringSpace(st_info & 0xf,7));
   }
   string.append(" ");
   switch(st_info >> 4)
   {
    case STB_LOCAL:  string.append("LOCAL "); break;
    case STB_GLOBAL: string.append("GLOBAL"); break;
    case STB_WEAK:   string.append("WEAK  "); break;
    default:         string.append(Util.toDecStringSpace(st_info >> 4,6));
   }
   string.append(" ");
   switch(st_other)
   {
    case 0:  string.append("DEFAULT"); break;
    case 2:  string.append("HIDDEN "); break;
    default: string.append(Util.toDecStringSpace(st_other,7));
   }
   string.append(" ");
   switch(st_shndx)
   {
    case SHN_UNDEF: string.append(" UND"); break;
    case SHN_ABS:   string.append(" ABS"); break;
    default:        string.append(Util.toDecStringSpace(st_shndx,4));
   }
   string.append(" ");
   if(st_name == 0)
	 return string.toString();
   else
   {
	try
	{
     string.append(Util.toStringFixed(string_table_symbol_names.substring((int)st_name,string_table_symbol_names.indexOf('\0',(int)st_name)),25));
	}
	catch(IndexOutOfBoundsException e)
	{   
	 string.append(Util.toStringFixed("!ELF error",25));
	}
    return string.toString();
   }
  }
 }
}
