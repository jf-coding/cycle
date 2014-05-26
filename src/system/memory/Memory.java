package system.memory;

import java.util.*;

import system.opb_device.*;
import system.cpu.*;
import system.*;

/**
 * The memory system class. This class implements the memory system, includes the emulation in the functional point of view 
 * of the buses supported by the system, the access to the internal an external memory and the access to the devices registers. 
 */
public final class Memory 
{
 /** The instruction set architecture decoder of the cpu. */
 private InstructionSetArchitecture inst_set_arq; 
 /** The devices registers list. */
 private Map<Integer,OPBRegister> devices_registers;
 /** The instruction memory. */
 private Map<Integer,Instruction> memory_inst;
 /** The data memory. */
 private Map<Integer,Integer> memory_data;
 /** The read latency value of the local memory bus. */
 private int lmb_read;
 /** The write latency value of the local memory bus. */
 private int lmb_write;
 /** The lowest memory address mapped in the local memory bus value. */
 private int lmb_begin;
 /** The highest memory address mapped in the local memory bus value. */
 private int lmb_end;
 /** The on-chip memory bus flag.<p>
  *  false - the on-chip peripheral bus doesn't have memory mapped.<br>
  *  true - the on-chip peripheral bus have memory mapped.
  */
 private boolean opb;
 /** The read latency value of the on-chip peripheral bus. */
 private int opb_read;
 /** The write latency value of the on-chip peripheral bus. */
 private int opb_write;
 /** The lowest memory address mapped in the on-chip peripheral bus value. */
 private int opb_begin;
 /** The highest memory address mapped in the on-chip peripheral bus value. */
 private int opb_end;
 /** The devices flag.<p>
  *  false - don't exist devices mapped.<br>
  *  true - exist devices mapped.
  */
 private boolean dev;
 /** The read latency value of the devices registers. */
 private int dev_read;
 /** The write latency value of the devices registers. */
 private int dev_write;
 /** The on-chip peripheral bus latency value in the case of an access to a memory address not mapped. */
 private int mapped;
 /** The data status STANBY. */
 private static final int STANBY    = 1;
 /** The data status UNALIGNED. */
 private static final int UNALIGNED = 2;
 /** The data status ACCESS. */
 private static final int ACCESS    = 3;
 /** The data status MAPPED. */
 private static final int MAPPED    = 4;
 /** The data device LMB. */
 private static final int LMB = 1;
 /** The data device OPB. */
 private static final int OPB = 2;
 /** The data device DEV. */
 private static final int DEV = 3;
 /** The device or memory of the current data access memory. Attribute used to simulate the data memory latency.<p> 
  *  LMB - access to an memory address in the local memory bus.<br>
  *  OPB - access to an memory address in the on-chip peripheral bus.<br>
  *  DEV - access to an register in the device.
  */
 private int data_device;
 /** The status value of the current data access memory. Attribute used to simulate the data memory latency.<p>
  *  STANBY - the first cycle perform by an instruction accessing to the data memory.<br> 
  *  UNALIGNED - memory access unaligned.<br>  
  *  ACCESS - data memory ready to be read or write.<br>
  *  MAPPED - access to a memory address not mapped.
  */
 private int data_status;
 /** The cycles value of the current data access memory. Attribute used to simulate the data memory latency. */
 private int data_cycles;
 /** The address auxiliary for internal proposes. */
 private int address_aux;
 /** the data memory value of the methods which simulates the latency memory access and used by the instruction and cpu to access the memory. */
 private Integer data;
 /** Define the byte number of an word.<p>
  *  0 - the byte 0 (MSB).<br>
  *  1 - the byte 1.<br>
  *  2 - the byte 2.<br>
  *  3 - the byte 3 (LSB).
  */
 private int group;
 
 /**
  * Instantiates a new memory system.
  *
  * @throws MemoryException if an error occurs in memory system initiation.
  */
 public Memory() throws MemoryException
 {
  devices_registers = new HashMap<Integer,OPBRegister>();
  memory_inst = new HashMap<Integer,Instruction>();
  memory_data = new HashMap<Integer,Integer>();

  data_status = STANBY;
  data_cycles = 1;
 }
 
 // ------------------------------------
 // methods used for auxiliary functions
 // ------------------------------------

 /**
  * Maps an device register in the on-chip peripheral bus.
  *
  * @param address the memory address where the register will be mapped.
  * @param opb_register the device register that will be mapped.
  * @throws MemoryException if an error occurs in the mapping of the register device.
  */
 public final void register(int address,OPBRegister opb_register) throws MemoryException
 {
  int address_aux;
  
  address_aux = address >>> 2;
  if(devices_registers.get(address_aux) != null)
    throw new MemoryException("can't map " + opb_register.toString() + " from " + opb_register.deviceName() + ", address in use");	  
  devices_registers.put(address_aux,opb_register);
  dev = true;
 }
  
 /**
  * Resets data memory status.
  */
 public final void cleanStatus()
 { 
  data_status = STANBY;
 }
 
 /**
  * Reset the memory system.
  */
 public final void reset()
 {
  memory_data = new HashMap<Integer,Integer>();
  memory_inst = new HashMap<Integer,Instruction>();
	    
  data_status = STANBY;
  data_cycles = 1;
 }
 
 // -----------------------------------
 // methods used to display information
 // -----------------------------------
 
 /**
  * Shows the memory system configuration including the registers peripheral address.
  */
 public final void show()
 {
  double lmb_size;
  double opb_size;
	  
  System.out.println("");
  System.out.println("  Memory Latency");   
  System.out.println("   . LMB, Read: " + Util.toDecStringSpace(lmb_read,2) + Util.toStringSpace("Write: ",10) + Util.toDecStringSpace(lmb_write,2));  
  if(opb == true)
	System.out.println("   . OPB, Read: " + Util.toDecStringSpace(opb_read,2) + Util.toStringSpace("Write: ",10) + Util.toDecStringSpace(opb_write,2));
  System.out.println("   . Mapped: " + Util.toDecStringSpace(mapped,2));
  
  System.out.println("");
  System.out.println("  Memory Range"); 
  
  lmb_size = (getLMB_end() + 1 - getLMB_begin()) / 1024d;
  System.out.println("   . LMB, Begin: 0x" + Util.toHexString(lmb_begin << 2,8) + Util.toStringSpace("End: 0x",9) + Util.toHexString((lmb_end << 2) + 3,8));
	   
  if(opb == true)
  {
   System.out.println("   . OPB, Begin: 0x" + Util.toHexString(opb_begin << 2,8) + Util.toStringSpace("End: 0x",9) + Util.toHexString((opb_end << 2) + 3,8));
  }
  
  System.out.println("");
  System.out.println("  Memory Size"); 
  
  System.out.println("   . LMB, Size: " + (getLMB_end() + 1 - getLMB_begin()) + "bytes   Size: " + lmb_size + "kbytes");

  if(opb == true)
  {
   opb_size = (getOPB_end() + 1 - getOPB_begin()) / 1024d;
   System.out.println("   . OPB, Size: " + (getOPB_end() + 1 - getOPB_begin()) + "bytes   Size: " + opb_size + "kbytes");
  }
 }
 
 // -------------------------------------
 // methods used for memory configuration 
 // -------------------------------------
 
 /**
  * Sets the instruction set architecture decoder of the cpu.
  *
  * @param inst_set_arq the instruction set architecture decoder of the cpu.
  */
 public final void setInstructionSetArchitecture(InstructionSetArchitecture inst_set_arq)
 {
  this.inst_set_arq = inst_set_arq;
 }
 
 /**
  * Sets the memory latency of the memory in the local memory bus. Method used for memory configuration.
  *
  * @param lmb_read the read memory latency value in the local memory bus.
  * @param lmb_write the write memory latency value in the local memory bus.
  */
 public final void setLMB_latency(int lmb_read,int lmb_write)
 {
  this.lmb_read = lmb_read;
  this.lmb_write = lmb_write;
 }
 
 /**
  * Sets the memory latency of the memory in the on-chip peripheral bus. Method used for memory configuration.
  *
  * @param opb_read the read memory latency value in the on-chip peripheral bus.
  * @param opb_write the write memory latency value in the on-chip peripheral bus.
  */
 public final void setOPB_latency(int opb_read,int opb_write)
 {
  this.opb_read = opb_read;
  this.opb_write = opb_write;
 }
 
 /**
  * Sets the limits range of the memory mapped in the local memory bus. Method used for memory configuration.
  *
  * @param lmb_begin the lowest memory address of the memory in the local memory bus.
  * @param lmb_end the highest memory address of the memory in the local memory bus.
  */
 public final void setLMB_range(int lmb_begin,int lmb_end)
 {
  this.lmb_begin = lmb_begin >>> 2;
  this.lmb_end = lmb_end >>> 2;
 }
 
 /**
  * Sets the limits range of the memory mapped in the on-chip peripheral bus. Method used for memory configuration.
  *
  * @param opb_begin the lowest memory address of the memory in the on-chip peripheral bus.
  * @param opb_end the highest memory address of the memory in the on-chip peripheral bus.
  */
 public final void setOPB_range(int opb_begin,int opb_end)
 {
  this.opb_begin = opb_begin >>> 2;
  this.opb_end = opb_end >>> 2;
 }
 
 /**
  * Sets on-chip peripheral bus latency in the case of an access to a memory address not mapped.
  *
  * @param mapped on-chip peripheral bus latency value in the case of an access to a memory address not mapped.
  */
 public final void setMapped_latency(int mapped)
 {
  this.mapped = mapped;
 }
  
 /**
  * Sets the on-chip peripheral bus flag. Method used for memory configuration.<p>
  * false - the on-chip peripheral bus doesn't have memory mapped.<br>
  * true - the on-chip peripheral bus have memory mapped.
  * @param opb the on-chip peripheral bus flag value.
  */
 public final void setOPB(boolean opb)
 {
  this.opb = opb;	 
 }
 
 // -----------------------------------------------
 // methods used to access the memory configuration
 // -----------------------------------------------

 /**
  * Returns the lowest mapped memory address of the local memory bus. Method used to access the memory configuration.
  *
  * @return the lowest mapped memory address of the local memory bus.
  */
 public final long getLMB_begin()
 {
  return ((long)lmb_begin << 2);
 }
 
 /**
  * Returns the lowest mapped memory address of the on-chip peripheral bus. Method used to access the memory configuration.
  *
  * @return the lowest mapped memory address of the on-chip peripheral bus.
  */
 public final long getOPB_begin()
 {
  return ((long)opb_begin << 2);
 }
 
 /**
  * Returns the highest mapped memory address of the local memory bus. Method used to access the memory configuration.
  *
  * @return the highest mapped memory address of the local memory bus.
  */
 public final long getLMB_end()
 {
  return ((long)lmb_end << 2) + 3;
 }
 
 /**
  * Returns the highest mapped memory address of the on-chip peripheral bus. Method used to access the memory configuration.
  *
  * @return the highest mapped memory address of the on-chip peripheral bus.
  */
 public final long getOPB_end()
 {
  return ((long)opb_end << 2) + 3;
 }
 
 /**
  * Returns the on-chip peripheral bus flag. Method used to access the memory configuration.<p>
  * false - the on-chip peripheral bus doesn't have memory mapped.<br>
  * true - the on-chip peripheral bus have memory mapped.
  * @return the on-chip peripheral bus flag.
  */
 public final boolean getOPB()
 {
  return opb;	 
 }
 
 // -------------------------------------------------------
 // methods used by the GDB and the system to access memory
 // -------------------------------------------------------

 /**
  * Reads an instruction from the memory. This method is used by the GDB mode. The system uses this method to load the program in the memory when this simulator starts.
  *
  * @param address the memory address where the byte will be read.
  * @return the word value.
  * @throws MemoryException if the memory address is not mapped.
  */
 public final Instruction getMemoryInstruction(int address) throws MemoryException
 {
  Instruction instruction;
  int address_aux;
  Integer data;
  
  if((address & 0x00000003) != 0)
	throw new MemoryException("the address 0x" + Util.toHexString(address,8) + " is un unligned adress");
  address_aux = address >>> 2;
  if(address_aux >= lmb_begin && address_aux <= lmb_end)
  {
   instruction = memory_inst.get(address_aux);
   if(instruction == null)
	 instruction = inst_set_arq.getIllegal(address);
   return instruction;
  }
  if(opb)
  {
   if(address_aux >= opb_begin && address_aux <= opb_end)
   {
	instruction = memory_inst.get(address_aux);
	if(instruction == null)
	  instruction = inst_set_arq.getIllegal(address);
	return instruction;
   }
  }
  if(dev)
  {
   if(devices_registers.containsKey(address_aux))
   {
	data = devices_registers.get(address_aux).get();
	instruction = inst_set_arq.decode(address,data);
	return instruction;
   }
  }
  throw new MemoryException("the address 0x" + Util.toHexString(address,8) + " isn't mapped");
 }
 
 /**
  * Writes an instruction in the memory. This method is used by the GDB mode. The system uses this method to load the program in the memory when this simulator starts.
  *
  * @param instruction the instruction that will be wrote.
  * @param address the memory address where the word will be wrote.
  * @throws MemoryException if the memory address is not mapped.
  */
 public final void putMemoryInstruction(Instruction instruction,int address) throws MemoryException
 {
  int address_aux;
	 
  if((address & 0x00000003) != 0)
	throw new MemoryException("the address 0x" + address + " is an unligned adress");
  address_aux = address >>> 2;
  if(address_aux >= lmb_begin && address_aux <= lmb_end)	 
  {
   memory_inst.put(address_aux,instruction);
   return;
  }
  if(opb)
  {
   if(address_aux >= opb_begin && address_aux <= opb_end)
   {
	memory_inst.put(address_aux,instruction);
    return;
   }
  }
  if(dev)
  {
   if(devices_registers.containsKey(address_aux))
   {
	return;
   }
  }
  throw new MemoryException("the address 0x" + (address << 2) + " isn't mapped");
 }
 
 /**
  * Reads an word from the memory. This method is used by the GDB mode. The system uses this method to load the program in the memory when this simulator starts.
  *
  * @param address the memory address where the byte will be read.
  * @return the word value.
  */
 public final int getMemoryWord(int address) throws MemoryException
 {
  int address_aux;
  Integer data;
	 
  if((address & 0x00000003) != 0)
	throw new MemoryException("the address 0x" + Util.toHexString(address,8) + " is un unligned adress");
  address_aux = address >>> 2;
  if(address_aux >= lmb_begin && address_aux <= lmb_end)	 
  {
   data = memory_data.get(address_aux);
   if(data == null)
     return 0;
   return data;
  }
  if(opb)
  {
   if(address_aux >= opb_begin && address_aux <= opb_end)
   {
	data = memory_data.get(address_aux);
	if(data == null)
	  return 0;
	return data;	
   }
  }
  if(dev)
  {
   if(devices_registers.containsKey(address_aux))
   {
	data = devices_registers.get(address_aux).get();
    return data;
   }
  }
  return 0;
 }
 
 /**
  * Writes an word in the memory. This method is used by the GDB mode. The system uses this method to load the program in the memory when this simulator starts.
  * The data and the instruction in the memory address are changed.
  * 
  * @param address the memory address where the word will be wrote.
  * @param data the word value.
  * @throws MemoryException if the memory address is not mapped.
  */
 public final void putMemoryWord(int address,int data) throws MemoryException
 {
  Instruction instruction;
  int address_aux;
  
  if((address & 0x00000003) != 0)
    throw new MemoryException("the address 0x" + Util.toHexString(address,8) + " is un unligned adress");
  address_aux = address >>> 2;
  if(address_aux >= lmb_begin && address_aux <= lmb_end)	 
  {
   memory_data.put(address_aux,data);
   instruction = inst_set_arq.decode(address,data);
   memory_inst.put(address_aux,instruction);
   return;
  }
  if(opb)
  {
   if(address_aux >= opb_begin && address_aux <= opb_end)
   {
	memory_data.put(address_aux,data);
	instruction = inst_set_arq.decode(address,data);
	memory_inst.put(address_aux,instruction);    
    return;
   }
  }
  if(dev)
  {
   if(devices_registers.containsKey(address_aux))
   {
	devices_registers.get(address_aux).put(data);
	return;
   }
  }
  throw new MemoryException("the address 0x" + Util.toHexString(address,8) + " isn't mapped");
 }
 
 /**
  * Reads an byte from the memory. This method is used by the GDB mode. The system uses this method to load the program in the memory when this simulator starts.
  *
  * @param address the memory address where the byte will be read.
  * @return the byte value.
  */
 public final int getMemoryByte(int address)
 {
  int  address_aux;
  Integer data;
  int  group;
		 
  group = address & 0x00000003; 
  address_aux = address >>> 2;
  if(address_aux >= lmb_begin && address_aux <= lmb_end)	 
  {
   data = memory_data.get(address_aux);
   if(data == null)
	 return 0;	
   switch(group)
   {
	case 0:data = data >>> 24;          break;
	case 1:data = (data >>> 16) & 0xff; break;
	case 2:data = (data >>> 8) & 0xff;  break;
	case 3:data = data & 0xff;
   }
   return data;
  }
  if(opb)
  {
   if(address_aux >= opb_begin && address_aux <= opb_end)
   {
	data = memory_data.get(address_aux);
	if(data == null)
	  return 0;	
	switch(group)
	{
	 case 0:data = data >>> 24;          break;
	 case 1:data = (data >>> 16) & 0xff; break;
	 case 2:data = (data >>> 8) & 0xff;  break;
	 case 3:data = data & 0xff;
    }
    return data;
   }
  }
  if(dev)
  {
   if(devices_registers.containsKey(address_aux))
   {
	data = devices_registers.get(address_aux).get();
	switch(group)
	{
	 case 0:data = data >>> 24;          break;
	 case 1:data = (data >>> 16) & 0xff; break;
	 case 2:data = (data >>> 8) & 0xff;  break;
	 case 3:data = data & 0xff;
    }
    return data;
   }
  }
  return 0;
 }
 
 /**
  * Writes an byte in the memory. This method is used by the GDB mode. The system uses this method to load the program in the memory when this simulator starts.
  * The data and the instruction in the memory address are changed.
  * 
  * @param address the memory address where the byte will be wrote.
  * @param data the byte value.
  * @throws MemoryException if the memory address is not mapped.
  */
 public final void putMemoryByte(int address,int data) throws MemoryException
 {
  Instruction instruction;
  int address_aux;
  Integer data_aux;
  int group;
	 
  group = address & 0x00000003; 
  address_aux = address >>> 2;
  if(address_aux >= lmb_begin && address_aux <= lmb_end)	 
  { 
   data_aux = memory_data.get(address_aux);
   if(data_aux == null)
   {
	switch(group)
	{
	 case 0:data_aux = (data & 0xffff) << 24; break;
	 case 1:data_aux = (data & 0xffff) << 16; break;
	 case 2:data_aux = (data & 0xffff) << 8;  break;
	 case 3:data_aux = data & 0xffff;
	}
	memory_data.put(address_aux,data_aux);
    instruction = inst_set_arq.decode(address_aux << 2,data_aux);
    memory_inst.put(address_aux,instruction);
	return;
   }
   else
   {
    switch(group)
	{
	 case 0:data_aux = (data_aux & 0x00ffffff) | ((data & 0xff) << 24); break;
	 case 1:data_aux = (data_aux & 0xff00ffff) | ((data & 0xff) << 16); break;
	 case 2:data_aux = (data_aux & 0xffff00ff) | ((data & 0xff) << 8);  break;
	 case 3:data_aux = (data_aux & 0xffffff00) | (data & 0xff);
	}
    memory_data.put(address_aux,data_aux);
    instruction = inst_set_arq.decode(address_aux << 2,data_aux);
    memory_inst.put(address_aux,instruction);
	return;
   }
  }
  if(opb)
  {
   if(address_aux >= opb_begin && address_aux <= opb_end)
   {
	data_aux = memory_data.get(address_aux);
	if(data_aux == null)
	{
	 switch(group)
	 {
	  case 0:data_aux = (data & 0xffff) << 24; break;
	  case 1:data_aux = (data & 0xffff) << 16; break;
	  case 2:data_aux = (data & 0xffff) << 8;  break;
	  case 3:data_aux = data & 0xffff;
	 }
	 memory_data.put(address_aux,data_aux);
	 instruction = inst_set_arq.decode(address_aux << 2,data_aux);
	 memory_inst.put(address_aux,instruction);
	 return;
	}
	else
	{
	 switch(group)
	 {
	  case 0:data_aux = (data_aux & 0x00ffffff) | ((data & 0xff) << 24); break;
	  case 1:data_aux = (data_aux & 0xff00ffff) | ((data & 0xff) << 16); break;
	  case 2:data_aux = (data_aux & 0xffff00ff) | ((data & 0xff) << 8);  break;
	  case 3:data_aux = (data_aux & 0xffffff00) | (data & 0xff);
	 }
	 memory_data.put(address_aux,data_aux);
	 instruction = inst_set_arq.decode(address_aux << 2,data_aux);
	 memory_inst.put(address_aux,instruction);
	 return;
	}
   }
  }
  if(dev)
  {
   if(devices_registers.containsKey(address_aux))
   {
	data_aux = devices_registers.get(address_aux).get();
	switch(group)
	{
	 case 0:data_aux = (data_aux & 0x00ffffff) | ((data & 0xff) << 24); break;
	 case 1:data_aux = (data_aux & 0xff00ffff) | ((data & 0xff) << 16); break;
	 case 2:data_aux = (data_aux & 0xffff00ff) | ((data & 0xff) << 8);  break;
	 case 3:data_aux = (data_aux & 0xffffff00) | (data & 0xff);
	}
	devices_registers.get(address_aux).put(data_aux);
	return;
   }
  }
  throw new MemoryException("the address 0x" + Util.toHexString(address,8) + " isn't mapped");
 }
 
 // -----------------------------------------------------------------
 // methods used by the simulator for accessing data and instructions
 // -----------------------------------------------------------------
 
 /**
  * Fetches an instruction from the memory. This method simulates the latency memory access and used by the instruction and cpu to access the memory.
  *
  * @param address the memory address where the half word will be fetched. 
  * @return the instruction fetched.
  */
 public final Instruction getInstruction(int address)
 {
  Instruction instruction;
  int address_aux;
  Integer data;

  address_aux = address >>> 2;
  if(address_aux >= lmb_begin && address_aux <= lmb_end)	 
  {
   instruction = memory_inst.get(address_aux);
   if(instruction == null)
     instruction = inst_set_arq.getIllegal(address);
   return instruction;
  }
  if(opb)
  {
   if(address_aux >= opb_begin && address_aux <= opb_end)
   {
    instruction = memory_inst.get(address_aux);
    if(instruction == null)
      instruction = inst_set_arq.getIllegal(address);
    return instruction;
   }
  }
  if(dev)
  {
   if(devices_registers.containsKey(address_aux))
   {
    data = devices_registers.get(address_aux).get();
	instruction = inst_set_arq.decode(address,data);
	return instruction;
   } 
  }
  return inst_set_arq.getMapped(address);
 }
 
 /**
  * Reads an word from the memory. This method simulates the latency memory access and used by the instruction and cpu to access the memory.
  *
  * @param address the memory address where the word will be read.
  * @return the memory status.
  */
 public final int getWord(int address)
 { 
  switch(data_status)
  {
   case ACCESS:data_cycles++;
	           switch(data_device)
               {
	            case LMB:if(data_cycles >= lmb_read)
	                     {     	  
	            	      data = memory_data.get(address_aux);
	            	      data_cycles = 1;
	            	      data_status = STANBY;
	            	      if(data != null)
	            	        return Mem_Status.READY;
	            	      else
	            	      {
	            	       data = 0;
                           return Mem_Status.READY;
	            	      }
	                     }
	                     return Mem_Status.ACCESS;
	            case OPB:if(data_cycles >= opb_read)
	                     {     	  
          	              data = memory_data.get(address_aux);
          	              data_cycles = 1;
          	              data_status = STANBY;
          	              if(data != null)
          	                return Mem_Status.READY;
          	              else
          	              {
          	               data = 0;
                           return Mem_Status.READY;
          	              }
                         }
                         return Mem_Status.ACCESS;
	            case DEV:if(data_cycles >= dev_read)
                         {
          	              data = devices_registers.get(address_aux).get();
	                      data_cycles = 1;
                          data_status = STANBY;
                          return Mem_Status.READY;
                         }
                         return Mem_Status.ACCESS;
               }
   case STANBY:address_aux = address >>> 2;
               if(address_aux >= lmb_begin && address_aux <= lmb_end)	 
               {
	            if((address & 0x00000003) != 0)
	            {
	             data_device = LMB;
                 data_status = UNALIGNED;
                 return Mem_Status.ACCESS;
	            }
	            if(lmb_read == 1)
                {     	  
       	         data = memory_data.get(address_aux);
       	         data_cycles = 1;
       	         data_status = STANBY;
       	         if(data != null)
       	           return Mem_Status.READY;
       	         else
       	         {
       	          data = 0;
                  return Mem_Status.READY;
       	         }
                }
	            data_device = LMB;
	            data_status = ACCESS;
	            return Mem_Status.ACCESS;
               }
               if(opb)
               {
                if(address_aux >= opb_begin && address_aux <= opb_end)
                {
	             if((address & 0x00000003) != 0)
	             {
	              data_device = OPB;
                  data_status = UNALIGNED;
                  return Mem_Status.ACCESS;
	             }
	             if(opb_read == 1)
                 {     	  
  	              data = memory_data.get(address_aux);
  	              data_cycles = 1;
  	              data_status = STANBY;
  	              if(data != null)
  	                return Mem_Status.READY;
  	              else
  	              {
  	               data = 0;
                   return Mem_Status.READY;
  	              }
                 }
	             data_device = OPB;
	             data_status = ACCESS;
	             return Mem_Status.ACCESS; 
                }
               }
               if(dev)
               {
                if(devices_registers.containsKey(address_aux))
                {
            	 if((address & 0x00000003) != 0)
	             {
            	  data_device = DEV;
                  data_status = UNALIGNED;
                  return Mem_Status.ACCESS;
	             }
            	 dev_read = devices_registers.get(address_aux).getOPBDeviceClass().getReadLatency();
            	 if(dev_read == 1)
                 {
  	              data = devices_registers.get(address_aux).get();
                  data_cycles = 1;
                  data_status = STANBY;
                  return Mem_Status.READY;
                 }
            	 data_device = DEV;
            	 data_status = ACCESS;
	             return Mem_Status.ACCESS; 
                }
               }
               data_status = MAPPED;
               return Mem_Status.ACCESS;
   case MAPPED:data_cycles++;
               if(data_cycles > mapped)
               {
            	data_cycles = 1;
            	data_status = STANBY;
                return Mem_Status.MAPPED;
               }
               return Mem_Status.ACCESS;
   case UNALIGNED:data_cycles = 1;
                  data_status = STANBY;
                  return Mem_Status.UNALIGNED;
  }
  return -1;
 }
 
 /**
  * Writes an word in the memory. This method simulates the latency memory access and used by the instruction and cpu to access the memory.
  * The data and the instruction in the memory address are changed.
  *
  * @param address the memory address where the word will be wrote.
  * @param data the word value.
  * @return the memory status.
  */
 public final int putWord(int address,int data)
 {
  Instruction instruction;
	  
  switch(data_status)
  {
   case ACCESS:data_cycles++;
	           switch(data_device)
               {
	            case LMB:if(data_cycles >= lmb_write)
	                     {     	  
	            	      memory_data.put(address_aux,data);
          	              instruction = inst_set_arq.decode(address,data);
          	              memory_inst.put(address_aux,instruction);
          	              data_cycles = 1;
          	              data_status = STANBY;
          	              return Mem_Status.READY;
	                     }
	                     return Mem_Status.ACCESS;
	            case OPB:if(data_cycles >= opb_write)
	                     {     	  
	            	      memory_data.put(address_aux,data);
     	                  instruction = inst_set_arq.decode(address,data);
     	                  memory_inst.put(address_aux,instruction);
     	                  data_cycles = 1;
     	                  data_status = STANBY;
     	                  return Mem_Status.READY;
                         }
                         return Mem_Status.ACCESS;
                case DEV:if(data_cycles >= dev_write)
	                     {
	            	      devices_registers.get(address_aux).put(data);
     	                  data_cycles = 1;
                          data_status = STANBY;
                          return Mem_Status.READY;
	                     }
	                     return Mem_Status.ACCESS;
               }
   case STANBY:address_aux = address >>> 2;
               if(address_aux >= lmb_begin && address_aux <= lmb_end)	 
               {
                if((address & 0x00000003) != 0)
	            {
	             data_device = LMB;
	             data_status = UNALIGNED;
                 return Mem_Status.ACCESS;
	            }	            
                if(lmb_write == 1)
                {     	  
       	         memory_data.put(address_aux,data);
 	             instruction = inst_set_arq.decode(address,data);
 	             memory_inst.put(address_aux,instruction);
 	             data_cycles = 1;
 	             data_status = STANBY;
 	             return Mem_Status.READY;
                }
	            data_device = LMB;
	            data_status = ACCESS;
	            return Mem_Status.ACCESS;
               }
               if(opb)
               {
                if(address_aux >= opb_begin && address_aux <= opb_end)
                {
                 if((address & 0x00000003) != 0)
	             {
	              data_device = OPB;
                  data_status = UNALIGNED;
                  return Mem_Status.ACCESS;
	             }
                 if(opb_write == 1)
                 {     	  
        	      memory_data.put(address_aux,data);
	              instruction = inst_set_arq.decode(address,data);
	              memory_inst.put(address_aux,instruction);
	              data_cycles = 1;
	              data_status = STANBY;
	              return Mem_Status.READY;
                 }
	             data_device = OPB;
	             data_status = ACCESS;
	             return Mem_Status.ACCESS; 
                }
               }
               if(dev)
               {
                if(devices_registers.containsKey(address_aux))
                {
            	 if((address & 0x00000003) != 0)
	             {
                  data_device = DEV;
                  data_status = UNALIGNED;
                  return Mem_Status.ACCESS;
	             }
            	 dev_write = devices_registers.get(address_aux).getOPBDeviceClass().getWriteLatency();
            	 if(dev_write == 1)
                 {
        	      devices_registers.get(address_aux).put(data);
                  data_cycles = 1;
                  data_status = STANBY;
                  return Mem_Status.READY;
                 }
             	 data_device = DEV;
            	 data_status = ACCESS;
	             return Mem_Status.ACCESS; 
                }
               }
               data_status = MAPPED;
               return Mem_Status.ACCESS;
   case MAPPED:data_cycles++;
               if(data_cycles > mapped)
               {
	            data_cycles = 1;
	            data_status = STANBY;
                return Mem_Status.MAPPED;
               }
               return Mem_Status.ACCESS;
   case UNALIGNED:data_cycles = 1;
                  data_status = STANBY;
                  return Mem_Status.UNALIGNED;
  }
  return -1;
 }
 
 /**
  * Reads an half word from the memory. This method simulates the latency memory access and used by the instruction and cpu to access the memory.
  *
  * @param address the memory address where the half word will be read.
  * @return the memory status.
  */
 public final int getHalfWord(int address)
 {  
  switch(data_status)
  {
   case ACCESS:data_cycles++;
	           switch(data_device)
               {
	            case LMB:if(data_cycles >= lmb_read) 	
	                     {     	  
          	              data = memory_data.get(address_aux);
          	              data_cycles = 1;
          	              data_status = STANBY;
          	              if(data != null)
          	              {
          	               switch(group)
  	            	       {
  	            	        case 0:data = data >>> 16;   break;
  	            	        case 2:data = data & 0xffff;
  	            	       }
          	               return Mem_Status.READY;
          	              }
          	              else
          	              {
          	               data = 0;
                           return Mem_Status.READY;
          	              }
                         }
	                     return Mem_Status.ACCESS;
	            case OPB:if(data_cycles >= opb_read)
	                     {     	  
    	                  data = memory_data.get(address_aux);
    	                  data_cycles = 1;
    	                  data_status = STANBY;
    	                  if(data != null)
    	                  {
    	                   switch(group)
            	           {
            	            case 0:data = data >>> 16;   break;
            	            case 2:data = data & 0xffff;
            	           }
    	                   return Mem_Status.READY;
    	                  }
    	                  else
    	                  {
    	                   data = 0;
                           return Mem_Status.READY;
    	                  }
                         }
                         return Mem_Status.ACCESS;
	            case DEV:if(data_cycles >= dev_read)
                         { 
	        	          data = devices_registers.get(address_aux).get();
	        	          data_cycles = 1;
                          data_status = STANBY;
	        	          switch(group)
        	              {
        	               case 0:data = data >>> 16;   break;
        	               case 2:data = data & 0xffff;
        	              }
                          return Mem_Status.READY;
                         }
                         return Mem_Status.ACCESS;
               }
   case STANBY:address_aux = address >>> 2;
              if(address_aux >= lmb_begin && address_aux <= lmb_end)	 
              {
           	   group = address & 0x00000003;
           	   if(group == 1 || group == 3)
           	   {
           		data_device = LMB;
                data_status = UNALIGNED;
                return Mem_Status.ACCESS;  
           	   }
           	   if(lmb_read == 1) 	
               {     	  
	            data = memory_data.get(address_aux);
	            data_cycles = 1;
	            data_status = STANBY;
	            if(data != null)
	            {
	             switch(group)
     	         {
     	          case 0:data = data >>> 16;   break;
     	          case 2:data = data & 0xffff;
     	         }
	             return Mem_Status.READY;
	            }
	            else
	            {
	             data = 0;
                 return Mem_Status.READY;
	            }
               }
           	   data_device = LMB;
	           data_status = ACCESS;
	           return Mem_Status.ACCESS;
              }
              if(opb)
              {
               if(address_aux >= opb_begin && address_aux <= opb_end)
               {
           	    group = address & 0x00000003;
           	    if(group == 1 || group == 3)
        	    {
           	     data_device = OPB;
                 data_status = UNALIGNED;
                 return Mem_Status.ACCESS;  
        	    }
           	    if(opb_read == 1)
                {     	  
                 data = memory_data.get(address_aux);
                 data_cycles = 1;
                 data_status = STANBY;
                 if(data != null)
                 {
                  switch(group)
	              {
	               case 0:data = data >>> 16;   break;
	               case 2:data = data & 0xffff;
	              }
                  return Mem_Status.READY;
                 }
                 else
                 {
                  data = 0;
                  return Mem_Status.READY;
                 }
                }
           	    data_device = OPB;
	            data_status = ACCESS;
	            return Mem_Status.ACCESS; 
               }
              }
              if(dev)
              {
               if(devices_registers.containsKey(address_aux))
               {
           	    group = address & 0x00000003;
           	    if(group == 1 || group == 3)
        	    {
           	     data_device = DEV;
                 data_status = UNALIGNED;
                 return Mem_Status.ACCESS;  
        	    }
           	    dev_read = devices_registers.get(address_aux).getOPBDeviceClass().getReadLatency();
           	    if(dev_read == 1)
                { 
	             data = devices_registers.get(address_aux).get();
	             data_cycles = 1;
                 data_status = STANBY;
	             switch(group)
                 {
                  case 0:data = data >>> 16;   break;
                  case 2:data = data & 0xffff;
                 }
                 return Mem_Status.READY;
                }
           	    data_device = DEV;
           	    data_status = ACCESS;
	            return Mem_Status.ACCESS; 
               }
              }
              data_status = MAPPED;
              return Mem_Status.ACCESS;
   case MAPPED:data_cycles++;
               if(data_cycles > mapped)
               {
	            data_cycles = 1;
	            data_status = STANBY;
                return Mem_Status.MAPPED;
               }
               return Mem_Status.ACCESS;
   case UNALIGNED:data_cycles = 1;
                  data_status = STANBY;
                  return Mem_Status.UNALIGNED;

  }
  return -1;	
 }
 
 /**
  * Writes an half word in the memory. This method simulates the latency memory access and used by the instruction and cpu to access the memory.
  * The data and the instruction in the memory address are changed.
  *
  * @param address the memory address where the half word will be wrote.
  * @param data the half word value.
  * @return the memory status.
  */
 public final int putHalfWord(int address,int data)
 {
  Instruction instruction;
			  
  switch(data_status)
  {
   case ACCESS:data_cycles++;
	           switch(data_device)
	           {
		        case LMB:if(data_cycles >= lmb_write)
		                 {     	  
		        	      this.data = memory_data.get(address_aux);
		        	      data_cycles = 1;
    	                  data_status = STANBY;  
    	                  if(this.data != null)
          	              {
    	                   switch(group)
    		        	   {
    		        	    case 0:this.data = (this.data & 0x0000ffff) | ((data & 0xffff) << 16); break;
    		        	    case 2:this.data = (this.data & 0xffff0000) | (data & 0xffff);
    		        	   }	
    	                   memory_data.put(address_aux,this.data);
     	                   instruction = inst_set_arq.decode(address_aux << 2,this.data);
     	                   memory_inst.put(address_aux,instruction);
     	                   return Mem_Status.READY;
          	              }
    	                  else
    	                  {
    	                   switch(group)
   		        		   {
   		        		    case 0:this.data = (data & 0xffff) << 16; break;
   		        		    case 2:this.data = data & 0xffff;
   		        	       }	
    	                   memory_data.put(address_aux,this.data);
     	                   instruction = inst_set_arq.decode(address_aux << 2,this.data);
     	                   memory_inst.put(address_aux,instruction);
     	                   return Mem_Status.READY;
    	                  }
		                 }
		                 return Mem_Status.ACCESS;
		        case OPB:if(data_cycles >= opb_write)
		                 {     	  
		        	      this.data = memory_data.get(address_aux);
	        	          data_cycles = 1;
	                      data_status = STANBY;  
	                      if(this.data != null)
    	                  {
	                       switch(group)
		        	       {
		        	        case 0:this.data = (this.data & 0x0000ffff) | ((data & 0xffff) << 16); break;
		        	        case 2:this.data = (this.data & 0xffff0000) | (data & 0xffff);
		        	       }	
	                       memory_data.put(address_aux,this.data);
	                       instruction = inst_set_arq.decode(address_aux << 2,this.data);
	                       memory_inst.put(address_aux,instruction);
	                       return Mem_Status.READY;
    	                  }
	                      else
	                      {
	                       switch(group)
		        		   {
		        		    case 0:this.data = (data & 0xffff) << 16; break;
		        		    case 2:this.data = data & 0xffff;
		        	       }	
	                       memory_data.put(address_aux,this.data);
	                       instruction = inst_set_arq.decode(address_aux << 2,this.data);
	                       memory_inst.put(address_aux,instruction);
	                       return Mem_Status.READY;
	                      }
	                     }
	                     return Mem_Status.ACCESS;
		        case DEV:if(data_cycles >= dev_write)
                         {
    	                  this.data = devices_registers.get(address_aux).get();
    	                  switch(group)
	                      {
	                       case 0:this.data = (this.data & 0x0000ffff) | ((data & 0xffff) << 16); break;
	                       case 2:this.data = (this.data & 0xffff0000) | (data & 0xffff);
	                      }
    	                  devices_registers.get(address_aux).put(this.data);
                          data_cycles = 1;
                          data_status = STANBY;
                          return Mem_Status.READY;
                         }
                         return Mem_Status.ACCESS;
               }
   case STANBY:address_aux = address >>> 2;
               if(address_aux >= lmb_begin && address_aux <= lmb_end)	 
	           {
	            group = address & 0x00000003;
	            if(group == 1 || group == 3)
	            {
	             data_device = LMB;
	             data_status = UNALIGNED;
	             return Mem_Status.ACCESS;
	            }
	            if(lmb_write == 1)
                {     	  
       	         this.data = memory_data.get(address_aux);
       	         data_cycles = 1;
                 data_status = STANBY;  
                 if(this.data != null)
 	             {
                  switch(group)
	        	  {
	        	   case 0:this.data = (this.data & 0x0000ffff) | ((data & 0xffff) << 16); break;
	        	   case 2:this.data = (this.data & 0xffff0000) | (data & 0xffff);
	        	  }	
                  memory_data.put(address_aux,this.data);
                  instruction = inst_set_arq.decode(address_aux << 2,this.data);
                  memory_inst.put(address_aux,instruction);
                  return Mem_Status.READY;
 	             }
                 else
                 {
                  switch(group)
	        	  {
	        	   case 0:this.data = (data & 0xffff) << 16; break;
	        	   case 2:this.data = data & 0xffff;
	        	  }	
                  memory_data.put(address_aux,this.data);
                  instruction = inst_set_arq.decode(address_aux << 2,this.data);
                  memory_inst.put(address_aux,instruction);
                  return Mem_Status.READY;
                 }
                }
	            data_device = LMB;
		        data_status = ACCESS;
		        return Mem_Status.ACCESS;
	           }
	           if(opb)
	           {
	            if(address_aux >= opb_begin && address_aux <= opb_end)
	            {
	             group = address & 0x00000003;
	             if(group == 1 || group == 3)
	             {
	              data_device = OPB;
	              data_status = UNALIGNED;
	              return Mem_Status.ACCESS;
	             }
	             if(opb_write == 1)
                 {     	  
        	      this.data = memory_data.get(address_aux);
    	          data_cycles = 1;
                  data_status = STANBY;  
                  if(this.data != null)
                  {
                   switch(group)
        	       {
        	        case 0:this.data = (this.data & 0x0000ffff) | ((data & 0xffff) << 16); break;
        	        case 2:this.data = (this.data & 0xffff0000) | (data & 0xffff);
        	       }	
                   memory_data.put(address_aux,this.data);
                   instruction = inst_set_arq.decode(address_aux << 2,this.data);
                   memory_inst.put(address_aux,instruction);
                   return Mem_Status.READY;
                  }
                  else
                  {
                   switch(group)
        		   {
        		    case 0:this.data = (data & 0xffff) << 16; break;
        		    case 2:this.data = data & 0xffff;
        	       }	
                   memory_data.put(address_aux,this.data);
                   instruction = inst_set_arq.decode(address_aux << 2,this.data);
                   memory_inst.put(address_aux,instruction);
                   return Mem_Status.READY;
                  }
                 }
	             data_device = OPB;
		         data_status = ACCESS;
		         return Mem_Status.ACCESS; 
	            }
	           }
	           if(dev)
	           {
                if(devices_registers.containsKey(address_aux))
                {
            	 group = address & 0x00000003;
            	 if(group == 1 || group == 3)
	             {
            	  data_device = DEV;
	              data_status = UNALIGNED;
	              return Mem_Status.ACCESS;
	             }
            	 dev_write = devices_registers.get(address_aux).getOPBDeviceClass().getWriteLatency();
            	 if(dev_write == 1)
                 {
                  this.data = devices_registers.get(address_aux).get();
                  switch(group)
                  {
                   case 0:this.data = (this.data & 0x0000ffff) | ((data & 0xffff) << 16); break;
                   case 2:this.data = (this.data & 0xffff0000) | (data & 0xffff);
                  }
                  devices_registers.get(address_aux).put(this.data);
                  data_cycles = 1;
                  data_status = STANBY;
                  return Mem_Status.READY;
                 }
            	 data_device = DEV;
            	 data_status = ACCESS;
	             return Mem_Status.ACCESS; 
                }
	           }
	           data_status = MAPPED;
	           return Mem_Status.ACCESS;
   case MAPPED:data_cycles++;
	           if(data_cycles > mapped)
	           {
	          	data_cycles = 1;
	          	data_status = STANBY;
	            return Mem_Status.MAPPED;
	           }
	           return Mem_Status.ACCESS;
   case UNALIGNED:data_cycles = 1;
                  data_status = STANBY;
	              return Mem_Status.UNALIGNED;

  }
  return -1;	 	
 }
  
 /**
  * Reads an byte from the memory. This method simulates the latency memory access and used by the instruction and cpu to access the memory.
  *
  * @param address the memory address where the byte will be read.
  * @return the memory status.
  */
 public final int getByte(int address)
 { 
  switch(data_status)
  {
   case ACCESS:data_cycles++;
	           switch(data_device)
               {
	            case LMB:if(data_cycles >= lmb_read)
	                     {     	  
    	                  data = memory_data.get(address_aux);
    	                  data_cycles = 1;
    	                  data_status = STANBY;
    	                  if(data != null)
    	                  {
    	                   switch(group)
    	            	   {
    	            		case 0: data = data >>> 24;          break;
    	            		case 1: data = (data >>> 16) & 0xff; break;
    	            		case 2: data = (data >>> 8) & 0xff;  break;
    	            		default:data = data & 0xff;
    	            	   }
    	                   return Mem_Status.READY;
    	                  }
    	                  else
    	                  {
    	                   data = 0;
                           return Mem_Status.READY;
    	                  }
                         }
	                     return Mem_Status.ACCESS;
	            case OPB:if(data_cycles >= opb_read)
	                     {     	  
	                      data = memory_data.get(address_aux);
	                      data_cycles = 1;
	                      data_status = STANBY;
	                      if(data != null)
	                      {
	                       switch(group)
	            	       {
	            		    case 0: data = data >>> 24;          break;
	            		    case 1: data = (data >>> 16) & 0xff; break;
	            		    case 2: data = (data >>> 8) & 0xff;  break;
	            		    default:data = data & 0xff;
	            	       }
	                       return Mem_Status.READY;
	                      }
	                      else
	                      {
	                       data = 0;
                           return Mem_Status.READY;
	                      }
                         }
                         return Mem_Status.ACCESS;
	            case DEV:if(data_cycles >= dev_read)
                         {
                          data = devices_registers.get(address_aux).get();
                          data_cycles = 1;
                          data_status = STANBY;
                          switch(group)
                          {
                           case 0: data = data >>> 24;          break;
         		           case 1: data = (data >>> 16) & 0xff; break;
         		           case 2: data = (data >>> 8) & 0xff;  break;
         		           default:data = data & 0xff;
                          }
                          return Mem_Status.READY;
                         }
                         return Mem_Status.ACCESS;
               }
   case STANBY:address_aux = address >>> 2;
               if(address_aux >= lmb_begin && address_aux <= lmb_end)	 
               {
            	group = address & 0x00000003;   
            	if(lmb_read == 1)
                {     	  
                 data = memory_data.get(address_aux);
                 data_cycles = 1;
                 data_status = STANBY;
                 if(data != null)
                 {
                  switch(group)
           	      {
           		   case 0: data = data >>> 24;          break;
           		   case 1: data = (data >>> 16) & 0xff; break;
           		   case 2: data = (data >>> 8) & 0xff;  break;
           		   default:data = data & 0xff;
           	      }
                  return Mem_Status.READY;
                 }
                 else
                 {
                  data = 0;
                  return Mem_Status.READY;
                 }
                }     
            	data_device = LMB;
	            data_status = ACCESS;
	            return Mem_Status.ACCESS;
               }
               if(opb)
               {
                if(address_aux >= opb_begin && address_aux <= opb_end)
                {
            	 group = address & 0x00000003;
            	 if(opb_read == 1)
                 {     	  
                  data = memory_data.get(address_aux);
                  data_cycles = 1;
                  data_status = STANBY;
                  if(data != null)
                  {
                   switch(group)
        	       {
        		    case 0: data = data >>> 24;          break;
        		    case 1: data = (data >>> 16) & 0xff; break;
        		    case 2: data = (data >>> 8) & 0xff;  break;
        		    default:data = data & 0xff;
        	       }
                   return Mem_Status.READY;
                  }
                  else
                  {
                   data = 0;
                   return Mem_Status.READY;
                  }
                 }
            	 data_device = OPB;
	             data_status = ACCESS;
	             return Mem_Status.ACCESS; 
                }
               }
               if(dev)
               {
                if(devices_registers.containsKey(address_aux))
                {
            	 group = address & 0x00000003;
            	 dev_read = devices_registers.get(address_aux).getOPBDeviceClass().getReadLatency();
            	 if(dev_read == 1)
                 {
                  data = devices_registers.get(address_aux).get();
                  data_cycles = 1;
                  data_status = STANBY;
                  switch(group)
                  {
                   case 0: data = data >>> 24;          break;
 		           case 1: data = (data >>> 16) & 0xff; break;
 		           case 2: data = (data >>> 8) & 0xff;  break;
 		           default:data = data & 0xff;
                  }
                  return Mem_Status.READY;
                 }
            	 data_device = DEV;
            	 data_status = ACCESS;
	             return Mem_Status.ACCESS; 
                }
               }
               data_status = MAPPED;
               return Mem_Status.ACCESS;
   case MAPPED:data_cycles++;
               if(data_cycles > mapped)
               {
	            data_cycles = 1;
	            data_status = STANBY;
                return Mem_Status.MAPPED;
               }
               return Mem_Status.ACCESS;
  }
  return -1;	 
 }	 
 
 /**
  * Writes an byte in the memory. This method simulates the latency memory access and used by the instruction and cpu to access the memory.
  * The data and the instruction in the memory address are changed.
  *
  * @param address the memory address where the byte will be wrote.
  * @param data the byte value.
  * @return the memory status.
  */
 public final int putByte(int address,int data)
 {
  Instruction instruction;
		  
  switch(data_status)
  {
   case ACCESS:data_cycles++;
	           switch(data_device)
	           {
		        case LMB:if(data_cycles >= lmb_write)
		                 {     	  
	        	          this.data = memory_data.get(address_aux);
	        	          data_cycles = 1;
	                      data_status = STANBY;  
	                      if(this.data != null)
    	                  {
	                       switch(group)
			        	   {
			        		case 0: this.data = (this.data & 0x00ffffff) | ((data & 0xff) << 24); break;
			        		case 1: this.data = (this.data & 0xff00ffff) | ((data & 0xff) << 16); break;
			        		case 2: this.data = (this.data & 0xffff00ff) | ((data & 0xff) << 8);  break;
			        		default:this.data = (this.data & 0xffffff00) | (data & 0xff);
			        	   }	
	                       memory_data.put(address_aux,this.data);
	                       instruction = inst_set_arq.decode(address_aux << 2,this.data);
	                       memory_inst.put(address_aux,instruction);
	                       return Mem_Status.READY;
    	                  }
	                      else
	                      {
	                       switch(group)
			        	   {
			        	    case 0: this.data = (data & 0xffff) << 24; break;
			        	    case 1: this.data = (data & 0xffff) << 16; break;
			        	    case 2: this.data = (data & 0xffff) << 8;  break;
			        	    default:this.data = data & 0xffff;
			        	   }	
	                       memory_data.put(address_aux,this.data);
	                       instruction = inst_set_arq.decode(address_aux << 2,this.data);
	                       memory_inst.put(address_aux,instruction);
	                       return Mem_Status.READY;
	                      }
	                     }		        	
		                 return Mem_Status.ACCESS;
		        case OPB:if(data_cycles >= opb_write)
		                 {     	  
      	                  this.data = memory_data.get(address_aux);
      	                  data_cycles = 1;
                          data_status = STANBY;  
                          if(this.data != null)
	                      {
                           switch(group)
		        	       {
		        		    case 0: this.data = (this.data & 0x00ffffff) | ((data & 0xff) << 24); break;
		        		    case 1: this.data = (this.data & 0xff00ffff) | ((data & 0xff) << 16); break;
		        		    case 2: this.data = (this.data & 0xffff00ff) | ((data & 0xff) << 8);  break;
		        		    default:this.data = (this.data & 0xffffff00) | (data & 0xff);
		        	       }	
                           memory_data.put(address_aux,this.data);
                           instruction = inst_set_arq.decode(address_aux << 2,this.data);
                           memory_inst.put(address_aux,instruction);
                           return Mem_Status.READY;
	                      }
                          else
                          {
                           switch(group)
		        	       {
		        	        case 0: this.data = (data & 0xffff) << 24; break;
		        	        case 1: this.data = (data & 0xffff) << 16; break;
		        	        case 2: this.data = (data & 0xffff) << 8;  break;
		        	        default:this.data = data & 0xffff;
		        	       }	
                           memory_data.put(address_aux,this.data);
                           instruction = inst_set_arq.decode(address_aux << 2,this.data);
                           memory_inst.put(address_aux,instruction);
                           return Mem_Status.READY;
                          }
                         }		     
	                     return Mem_Status.ACCESS;
		        case DEV:if(data_cycles >= dev_write)
                         {
                          this.data = devices_registers.get(address_aux).get();
                          switch(group)
                          {
                           case 0: this.data = (this.data & 0x00ffffff) | ((data & 0xff) << 24); break;
	        		       case 1: this.data = (this.data & 0xff00ffff) | ((data & 0xff) << 16); break;
	        		       case 2: this.data = (this.data & 0xffff00ff) | ((data & 0xff) << 8);  break;
	        		       default:this.data = (this.data & 0xffffff00) | (data & 0xff);
                          }
                          devices_registers.get(address_aux).put(this.data);
                          data_cycles = 1;
                          data_status = STANBY;
                          return Mem_Status.READY;
                         }
                         return Mem_Status.ACCESS;
	             }
   case STANBY:address_aux = address >>> 2;
               if(address_aux >= lmb_begin && address_aux <= lmb_end)	 
	           {
            	group = address & 0x00000003;
            	if(lmb_write == 1)
                {     	  
   	             this.data = memory_data.get(address_aux);
   	             data_cycles = 1;
                 data_status = STANBY;  
                 if(this.data != null)
                 {
                  switch(group)
	        	  {
	        	   case 0: this.data = (this.data & 0x00ffffff) | ((data & 0xff) << 24); break;
	        	   case 1: this.data = (this.data & 0xff00ffff) | ((data & 0xff) << 16); break;
	        	   case 2: this.data = (this.data & 0xffff00ff) | ((data & 0xff) << 8);  break;
	        	   default:this.data = (this.data & 0xffffff00) | (data & 0xff);
	        	  }	
                  memory_data.put(address_aux,this.data);
                  instruction = inst_set_arq.decode(address_aux << 2,this.data);
                  memory_inst.put(address_aux,instruction);
                  return Mem_Status.READY;
                 }
                 else
                 {
                  switch(group)
	        	  {
	        	   case 0: this.data = (data & 0xffff) << 24; break;
	        	   case 1: this.data = (data & 0xffff) << 16; break;
	        	   case 2: this.data = (data & 0xffff) << 8;  break;
	        	   default:this.data = data & 0xffff;
	        	  }	
                  memory_data.put(address_aux,this.data);
                  instruction = inst_set_arq.decode(address_aux << 2,this.data);
                  memory_inst.put(address_aux,instruction);
                  return Mem_Status.READY;
                 }
                }		        	
	            data_device = LMB;
		        data_status = ACCESS;
		        return Mem_Status.ACCESS;
	           }
	           if(opb)
	           {
	            if(address_aux >= opb_begin && address_aux <= opb_end)
	            {
	             group = address & 0x00000003;
	             if(opb_write == 1)
                 {     	  
	              this.data = memory_data.get(address_aux);
	              data_cycles = 1;
                  data_status = STANBY;  
                  if(this.data != null)
                  {
                   switch(group)
        	       {
        		    case 0: this.data = (this.data & 0x00ffffff) | ((data & 0xff) << 24); break;
        		    case 1: this.data = (this.data & 0xff00ffff) | ((data & 0xff) << 16); break;
        		    case 2: this.data = (this.data & 0xffff00ff) | ((data & 0xff) << 8);  break;
        		    default:this.data = (this.data & 0xffffff00) | (data & 0xff);
        	       }	
                   memory_data.put(address_aux,this.data);
                   instruction = inst_set_arq.decode(address_aux << 2,this.data);
                   memory_inst.put(address_aux,instruction);
                   return Mem_Status.READY;
                  }
                  else
                  {
                   switch(group)
        	       {
        	        case 0: this.data = (data & 0xffff) << 24; break;
        	        case 1: this.data = (data & 0xffff) << 16; break;
        	        case 2: this.data = (data & 0xffff) << 8;  break;
        	        default:this.data = data & 0xffff;
        	       }	
                   memory_data.put(address_aux,this.data);
                   instruction = inst_set_arq.decode(address_aux << 2,this.data);
                   memory_inst.put(address_aux,instruction);
                   return Mem_Status.READY;
                  }
                 }		
	             data_device = OPB;
		         data_status = ACCESS;
		         return Mem_Status.ACCESS; 
	            }
	           }
	           if(dev)
	           {
	            if(devices_registers.containsKey(address_aux))
                {
            	 group = address & 0x00000003;
            	 dev_write = devices_registers.get(address_aux).getOPBDeviceClass().getWriteLatency();
            	 if(dev_write == 1)
                 {
                  this.data = devices_registers.get(address_aux).get();
                  switch(group)
                  {
                   case 0: this.data = (this.data & 0x00ffffff) | ((data & 0xff) << 24); break;
    		       case 1: this.data = (this.data & 0xff00ffff) | ((data & 0xff) << 16); break;
    		       case 2: this.data = (this.data & 0xffff00ff) | ((data & 0xff) << 8);  break;
    		       default:this.data = (this.data & 0xffffff00) | (data & 0xff);
                  }
                  devices_registers.get(address_aux).put(this.data);
                  data_cycles = 1;
                  data_status = STANBY;
                  return Mem_Status.READY;
                 }
            	 data_device = DEV;
            	 data_status = ACCESS;
	             return Mem_Status.ACCESS; 
                }
	           }
	           data_status = MAPPED;
	           return Mem_Status.ACCESS;
   case MAPPED:data_cycles++;
	           if(data_cycles > mapped)
	           {
	        	data_cycles = 1;
	        	data_status = STANBY;
	            return Mem_Status.MAPPED;
	           }
	           return Mem_Status.ACCESS;
  }
  return -1;	 	 
 }	 
 
 /**
  * Returns the data value of the methods which simulates the latency memory access and used by the instruction and cpu to access the memory.
  *
  * @return the data value of the methods which simulates the latency memory access.
  */
 public final int getData()
 {
  return data;	 
 }
}