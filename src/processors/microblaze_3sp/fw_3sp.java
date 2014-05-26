package processors.microblaze_3sp;

import java.util.*;

import system.memory.*;
import system.cpu.*;
import system.*;

/**
 * The Fireworks Three Stage Pipeline processor class. This class implements the FireWorks Three Stage Pipeline processor. 
 */
public final class fw_3sp extends CPU
{
 /** The instruction set architecture decoder. */
 private instsetarq inst_set_arq;
 /** An generic empty instruction. */
 private Instruction empty;
 /** The memory system. */
 private Memory memory;
 /** The instruction in the execute stage. */
 private Instruction execute;
 /** The instruction in the decode stage. */
 private Instruction decode;
 /** The instruction in the fetch stage. */
 private Instruction fetch;
 /** The number of instructions execute by the cpu. */
 private long instructions; 
 /** The number of general registers. */
 private int number_general = 32;
 /** The general registers. */
 private int[] general;
 /** The program counter register (PC) of the fetch stage instruction. */
 private int pc;
 /** The machine status register (MSR). */
 private int msr;
 /** The exception address register (EAR). */
 private int ear;
 /** The exception status register (ESR). */
 private int esr;
 /** The branch target register (BTR). */
 private int btr;
 /** The floating point status register (FSR). */
 private int fsr;
 /** The number of processor version registers (PVR). */
 private int number_pvr = 12;
 /** The processor version registers (PVR). */
 private int[] pvr;
 /** The delay slot flag.<p> 
  *  false - delay slot instruction disabled.<br>
  *  true - delay slot instruction enabled.
  */
 private boolean imm_flag;
 /** The next value of the program counter register. This value represent the memory's address of the next instruction will be fetched. */
 private int pc_next;
 /** The immediate value. */
 private int imm;
 /** The debug mode flag.<p> 
  *  false - normal mode. The events of the processor aren't notified.<br>
  *  true - debug mode. The events of the processor are notified.
  */
 private boolean debug;
 
 /**
  * Instantiates a new Fireworks Three Stage Pipeline processor.
  *
  * @param memory the memory system.
  * @param inst_stalls the list with the number of cycles that each instruction spends to be executed in the execute stage. 
  * @param inst_cond_stalls the list with the number of cycles that each branch instruction spends to perform a conditional branch.
  * @param debug the debug mode flag.
  * @throws CPUException if exist any problem with the parameters of the cpu.
  */
 public fw_3sp(Memory memory,Map<String,Integer> inst_stalls,Map<String,Integer> inst_cond_stalls,boolean debug) throws CPUException
 {
  try
  {
   if(memory == null)
	 throw new MemoryException("memory not initialize.");
   this.debug = debug; 
   this.memory = memory;
   empty = new empty();
   fetch = empty;
   decode = empty;
   execute = empty;
   general = new int[number_general];
   pvr = new int[number_pvr];
   inst_set_arq = new instsetarq(memory,this,inst_stalls,inst_cond_stalls);
   reset();
  }
  catch(CPUException e)
  {
   throw new CPUException(e.getMessage());
  }
  catch(MemoryException e)
  {
   throw new CPUException(e.getMessage());
  }
 }
 
 // --------------------------------------
 // methods used to control the simulation
 // --------------------------------------
 
 /**
  * Performs one operation cycle of the cpu.
  *
  * @param interrupt the interrupt
  * @return the cpu status after one cycle operation of the cpu. 
  * @see system.cpu.CPU#cycle(int)
  */
 public final int cycle(int interrupt)
 { 
  int execute_status;
  
  execute_status = execute.Stage(3);
  switch(execute_status)
  {
   case cpu_status.NORMAL:instructions++;
                          if(interrupt == 1)
                          {
                           if((msr & 0x20a) == 0x2 && imm_flag == false && decode != empty)
                           {
                            if(debug)
                            {
                             System.out.println("");
                             System.out.println("! Interrupt.");
                             System.out.println("");
                            }

                            general[14] = decode.getPC();
                            
                            msr = msr & 0xfffffffd;
                            execute = (Instruction)inst_set_arq.getJump();
                            decode  = fetch;
                            fetch   = memory.getInstruction(pc + 4);
                            return Sys_Status.NORMAL;
                           }
                          }
                          pc = pc_next;
                          pc_next = pc + 4;
                          execute = decode;
                          decode  = fetch;
                          fetch   = memory.getInstruction(pc);                           
                          return Sys_Status.NORMAL;
   case cpu_status.MEM_ACCESS:if(decode == empty)
                              {
                               pc = pc_next;
                               pc_next = pc + 4;
                               decode  = fetch;
                               fetch   = memory.getInstruction(pc);
                              } 
	                          return Sys_Status.NORMAL;
   case cpu_status.DELAY_SLOT:instructions++;
                              pc_next = pc + 4;
                              execute = decode;
                              decode  = empty;
                              fetch   = memory.getInstruction(pc);
                              return Sys_Status.NORMAL;
   case cpu_status.JUMP:instructions++; 
	                    pc_next = pc + 4;
                        execute = empty;
                        decode  = empty;
                        fetch   = memory.getInstruction(pc);
                        return Sys_Status.NORMAL;
   case cpu_status.STALL:if(decode == empty)
	                     {
	                      pc = pc_next;
	                      pc_next = pc + 4;
	                      decode  = fetch;
	                      fetch   = memory.getInstruction(pc);
	                     } 	   
	                     return Sys_Status.NORMAL;
   case cpu_status.EMPTY:if(interrupt == 1)
                         {
                          if((msr & 0x20a) == 0x2 && imm_flag == false && decode != empty)
                          {
                           if(debug)
                           {
                            System.out.println("");
                            System.out.println("! Interrupt.");
                            System.out.println("");
                           }

                           general[14] = decode.getPC();
        
                           msr = msr & 0xfffffffd;
                           execute = (Instruction)inst_set_arq.getJump();
                           decode  = fetch;
                           fetch   = memory.getInstruction(pc + 4);
                           return Sys_Status.NORMAL;
                          }
                         }

	                     pc = pc_next;
                         pc_next = pc + 4;
                         execute = decode;
                         decode  = fetch;
                         fetch   = memory.getInstruction(pc);
                         return Sys_Status.NORMAL;
   case cpu_status.BREAKPOINT:return Sys_Status.BREAKPOINT;                         
   case cpu_status.STOP:instructions++;
                        return Sys_Status.STOP;
   case cpu_status.DBZ:if((msr & 0x300) == 0x100)
                       {
	                    if(debug)
  	                    {
  	                     System.out.println("");
  	                     System.out.println("! Divide By Zero Exception, at address 0x" + Util.toHexString(execute.getPC(),8) + ".");
  	                     System.out.println("");
  	                    }
                        general[17] = execute.getPC() + 4;
                        esr = 0x5; 
                      
                        msr = msr | 0x200;
                        msr = msr & 0xfffffeff;
       
                        pc = 0x20;
                        execute = empty;
                        decode  = empty;
                        fetch   = memory.getInstruction(pc);
                        return Sys_Status.NORMAL;       
                       }
                       else
	                   {
	                    instructions++;
	                    
                    	pc = pc_next;
                        pc_next = pc + 4;
                        execute = decode;
                        decode  = fetch;
                        fetch   = memory.getInstruction(pc);
   	                    return Sys_Status.NORMAL;
                       }                             
   case cpu_status.MEM_MAPPED:if((msr & 0x300) == 0x100)
                              {
	                           if(debug)
 	                           {
 	                            System.out.println("");
 	                            System.out.println("! Data Bus Exception, at address 0x" + Util.toHexString(execute.getPC(),8) + ".");
 	                            System.out.println("");
 	                           }
                               general[17] = execute.getPC() + 4;
                               esr = 0x4; 
                               
                               msr = msr | 0x200;
                               msr = msr & 0xfffffeff;
      
                               pc = 0x20;
                               execute = empty;
                               decode  = empty;
                               fetch   = memory.getInstruction(pc);
                               return Sys_Status.NORMAL;
                              }
                              else
                              {
	                           instructions++;
                           
                               pc = pc_next;
                               pc_next = pc + 4;
                               execute = decode;
                               decode  = fetch;
                               fetch   = memory.getInstruction(pc);
         	                   return Sys_Status.NORMAL;
                              }                          
   case cpu_status.MEM_UNALIGNED:if((msr & 0x300) == 0x100)
                                 {
	                              if(debug)
  	                              {
  	                               System.out.println("");
  	                               System.out.println("! Unaligned Exception, at address 0x" + Util.toHexString(execute.getPC(),8) + ".");
  	                               System.out.println("");
  	                              }
                                  general[17] = execute.getPC() + 4;
                                  esr = esr | 0x1;	  
                                  
                                  msr = msr | 0x200;
                                  msr = msr & 0xfffffeff;
       
                                  pc = 0x20;
                                  execute = empty;
                                  decode  = empty;
                                  fetch   = memory.getInstruction(pc);
                                  return Sys_Status.NORMAL;
                                 }
                                 else
                                 {
	                              instructions++;
	                       
                                  pc = pc_next;
                                  pc_next = pc + 4;
                                  execute = decode;
                                  decode  = fetch;
                                  fetch   = memory.getInstruction(pc);
         	                      return Sys_Status.NORMAL;
                                 }
   case cpu_status.ILLEGAL:if((msr & 0x300) == 0x100)
                           {
                            if(debug)
                            {
                             System.out.println("");
                             System.out.println("! Illegal Opcode Exception, at address 0x" + Util.toHexString(decode.getPC(),8) + ".");
                             System.out.println("");
                            }
                            general[17] = execute.getPC() + 4;
                            esr = esr | 0x2;	  
                            
                            msr = msr | 0x200;
                            msr = msr & 0xfffffeff;
       
                            pc = 0x20;
                            execute = empty;
                            decode  = empty;
                            fetch   = memory.getInstruction(pc);
                            return Sys_Status.NORMAL;
                           }
                           else
                           {
                            instructions++;
                         
                            pc = pc_next;
                            pc_next = pc + 4;
                            execute = decode;
                            decode  = fetch;
                            fetch   = memory.getInstruction(pc);
                            return Sys_Status.NORMAL;
                           }
      case cpu_status.MAPPED:if((msr & 0x300) == 0x100)
                             {    	  
                              if(debug)
                              {
                               System.out.println("");
                               System.out.println("! Instruction Bus Exception, at address 0x" + Util.toHexString(pc,8) + ".");
                               System.out.println("");
                              }
                              general[17] = execute.getPC() + 4;
                              esr = esr | 0x3;	  
                              
                              msr = msr | 0x200;
                              msr = msr & 0xfffffeff;

                              pc = 0x20;
                              execute = empty;
                              decode  = empty;
                              fetch   = memory.getInstruction(pc);
                              return Sys_Status.NORMAL;
                             }
                             else
                             {
	                          instructions++;
	                       
                              pc = pc_next;
                              pc_next = pc + 4;
                              execute = decode;
                              decode  = fetch;
                              fetch   = memory.getInstruction(pc);
                              return Sys_Status.NORMAL;
                             }
   default:return 0;
  }
 }

 // --------------------------------
 // methods used to access registers
 // --------------------------------
 
 /**
  * Returns an register of the cpu. 
  *
  * @param register identify what register must return.<p>
  * 0 to 31 - general registers.<br>
  * 32 - program counter register (PC).<br>
  * 33 - machine status register (MSR).<br>
  * 34 - exception address register (EAR).<br>
  * 35 - exception status register (ESR).<br>
  * 36 - branch target register (BTR).<br>
  * 37 - floating point status register (FSR).<br>
  * 38 to 50 - processor version registers (PVR).
  * @return the register value with number equal to the parameter register. 
  * @see system.cpu.CPU#getRegister(int)
  */
 public int getRegister(int register)
 {
  if(register >= 0 && register <= 31)
	return general[register];
  switch(register)
  {
   case 32:return pc;
   case 33:return msr;
   case 34:return ear;
   case 35:return esr;
   case 36:return fsr;
   case 37:return btr;
  }
  if(register >= 38 && register < 50)
	return pvr[register - 38];
  return 0; 
 }
 
 /**
  * Sets an register of the cpu. 
  *
  * @param register identify what register must set.<p>
  * 0 to 31 - general registers.<br> 
  * 32 - program counter register (PC).<br>
  * 33 - machine status register (MSR).<br>
  * 34 - exception address register (EAR).<br>
  * 35 - exception status register (ESR).<br>
  * 36 - branch target register (BTR).<br>
  * 37 - floating point status register (FSR).<br>
  * 38 to 50 - processor version registers (PVR).
  * @param value the register value.
  * @see system.cpu.CPU#putRegister(int, int)
  */
 public void putRegister(int register,int value)
 {
  if(register > 0 && register <= 31)
	general[register] = value;
  switch(register)
  {
   case 32:pc  = value;
           break;
   case 33:msr = value;
           break;
   case 34:ear = value;
           break;
   case 35:esr = value;
           break;
   case 36:fsr = value;
           break;
   case 37:btr = value;
  }
  if(register >= 38 && register < 50)
	pvr[register - 38] = value;
 }
 
 /**
  * Returns an general register.
  *
  * @param register identify the general register.
  * @return the value of the general register.
  */
 public final int getGeneral(int register)
 {
  return general[register];
 }
 
 /**
  * Sets an general register.
  *
  * @param register identify the general register.
  * @param value the value of the general register.
  */
 public final void putGeneral(int register,int value)
 {
  if(register == 0)
	return;
  general[register] = value;
 }

 /**
  * Sets program counter register (PC).
  *
  * @param pc the program counter register (PC) value.
  */
 public final void putPC(int pc)
 {
  this.pc = pc;
 }
 
 /**
  * Returns the machine status register (MSR).
  *
  * @return the machine status register (MSR) value.
  */
 public final int getMSR()
 {
  return msr;
 }
 
 /**
  * Sets the machine status register (MSR).
  *
  * @param msr the machine status register (MSR) value.
  */
 public final void putMSR(int msr)
 {
  this.msr = msr;
 }
 
 /**
  * Returns the exception address register (EAR).
  *
  * @return the exception address register (EAR) value.
  */
 public final int getEAR()
 {
  return ear;
 }
 
 /**
  * Sets the exception address register (EAR).
  *
  * @param ear the exception address register (EAR) value.
  */
 public final void putEAR(int ear)
 {
  this.ear = ear;	 
 }
 
 /**
  * Returns the exception status register (ESR).
  *
  * @return the exception status register (ESR) value.
  */
 public final int getESR()
 {
  return esr;
 }

 /**
  * Sets exception status register (ESR).
  *
  * @param esr the exception status register (ESR) value.
  */
 public final void putESR(int esr)
 {
  this.esr = esr;	 
 }
 
 /**
  * Returns the branch target register (BTR).
  *
  * @return the branch target register (BTR) value.
  */
 public final int getBTR()
 {
  return btr;
 }
 
 /**
  * Sets branch target register (BTR).
  *
  * @param btr the branch target register (BTR) value.
  */
 public final void putBTR(int btr)
 {
  this.btr = btr;	 
 }
 
 /**
  * Returns the floating point status register (FSR).
  *
  * @return the floating point status register (FSR) value.
  */
 public final int getFSR()
 {
  return fsr;
 }
 
 /**
  * Sets the floating point status register (FSR).
  *
  * @param fsr the floating point status register (FSR) value.
  */
 public final void putFSR(int fsr)
 {
  this.fsr = fsr;	 
 }
 
 /**
  * Returns an processor version register (PVR).
  *
  * @param register identify the general register.
  * @return the processor version register (PVR).
  */
 public final int getPVR(int register)
 {
  return pvr[register];	  
 }

 /**
  * Sets an processor version register (PVR).
  *
  * @param register identify the general register.
  * @param value the processor version register (PVR) value.
  */
 public final void putPVR(int register,int value)
 {
  pvr[register] = value;
 }
 
 //-----------------------------------------------
 // methods used to manipulate the registers state
 // ----------------------------------------------
 
 /**
  * Sets the machine status register (MSR) carry bits (the bit 0 and the bit 22). Uses the value A and the value B to calculate the carry bit.<p>
  * Value A[bit 0] = bit 0 of the value A.<br>
  * Value B[bit 0] = bit 0 of the value B.<br>
  * (value A + value B)[bit 0] = bit 0 of the sum of value A with value B.<p> 
  * 0 - no carry (borrow); (value A[bit 0] = 0) and (value B[bit 0] = 0).<br>
  * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
  * ; (value A[bit 0] xor value B[bit 0] = 1) and ((value A + value B)[bit 0] = 1).<br>
  * 1 - carry (no borrow); (value A[bit 0] = 1) and (value B[bit 0] = 1).<br>
  * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
  * ; (value A[bit 0] xor value B[bit 0] = 1) and ((value A + value B)[bit 0] != 1).
  * @param value_a the value A.
  * @param value_b the value B.
  */
 public final void setMSRCarry(int value_a,int value_b)
 {
  if((value_a & 0x80000000) == 0x00000000 && (value_b & 0x80000000) == 0x00000000) // no carry
  {
   msr = msr & 0x7FFFFFFB;
   return;
  }
  if((value_a & 0x80000000) == 0x80000000 && (value_b & 0x80000000) == 0x80000000) // carry
  {
   msr = msr | 0x80000004;
   return;
  }
  if(((value_a & 0x80000000) ^ (value_b & 0x80000000)) == 0x80000000)
  {
   if(((value_a + value_b) & 0x80000000) == 0x80000000) // no carry
	 msr = msr & 0x7FFFFFFB;
   else // carry
	 msr = msr | 0x80000004;
  }  
 }

 /**
  * Sets the machine status register (MSR) carry bits (the bit 0 and the bit 22).
  *
  * @param carry the carry bit value.<p>
  * 0 - no carry (borrow).<br>
  * 1 - carry (no borrow). 
  */
 public final void setMSRCarry(int carry)
 {
  switch(carry & 0x00000001)
  {
   case 0x00000001:msr = msr | 0x80000004;
                   break;
   case 0x00000000:msr = msr & 0x7FFFFFFB;
  }
 }
 
 /**
  * Returns the machine status register (MSR) carry bit.
  *
  * @return the machine status register (MSR) carry bit.<p>
  * 0 - no carry (borrow).<br>
  * 1 - carry (no borrow). 
  */
 public final int getMSRCarry()
 {
  if((msr & 0x00000004) == 0x00000004)
    return 1;
  return 0;  
 }
  
 /**
  * Returns the immediate value.
  *
  * @return the immediate value.
  */
 public final int getIMM()
 {
  return imm;
 }

 /**
  * Sets the immediate value.
  *
  * @param imm the immediate value.
  */
 public final void putIMM(int imm)
 {
  this.imm = imm;  
 }

 /**
  * Sets the immediate flag.
  * 
  * @param imm_flag the immediate flag value.<p>
  * false - if the last execute instruction wasn't a imm instruction.<br>
  * true - if the last execute instruction was a imm instruction.
  */
 public final void setIMMFlag(boolean imm_flag)
 {
  this.imm_flag = imm_flag;  
 }

 /**
  * Returns the value of some machine status register (MSR) bits, flags or internal register of the cpu which the user don't have access in the real cpu.
  * This record is used mostly for debugging purposes.
  * 
  * @param register identify the machine status register (MSR) bits, flags or internal register of the cpu which the user don't have access in the real cpu.<p>
  * 0 - the next value of the program counter register. This value represent the memory's address of the next instruction will be fetched.<br>
  * 1 - the immediate value.<br>
  * 2 - the immediate flag.<br>
  * 3 - the machine status register (MSR) exception in progress bit.<br>
  * 4 - the machine status register (MSR) break in progress bit.<br>
  * 5 - the machine status register (MSR) interrupt enable bit.<br>
  * @return the value of the machine status register (MSR) bits, flags or internal register of the cpu which the user don't have access in the real cpu.
  * @see system.cpu.CPU#getStatus(int)
  */
 public final int getStatus(int register)
 {
  switch(register)
  {
   case 0:return pc_next;
   case 1:return imm;
   case 2:if(imm_flag)
	        return 1;
          else
        	return 0;
   case 3:if((msr & 0x200) != 0)
            return 1;
          else
 	        return 0;
   case 4:if((msr & 0x8) != 0)
            return 1;
          else
            return 0;
   case 5:if((msr & 0x2) == 0)
            return 1;
          else
            return 0;
  } 
  return 0;
 }
 
 // -------------------
 // methods auxiliaries
 // -------------------
 
 /**
  * Inserts the stop control instruction in the memory's address where the program exit.
  *
  * @param address the memory's address where the stop control instruction will be placed.
  * @throws CPUException if any error occurs when try to placed the instruction in the memory.
  * @see system.cpu.CPU#createProgram_Exit(long)
  */
 public final void createProgram_Exit(long address) throws CPUException
 {
  Instruction instruction;
  
  try
  {
   instruction = memory.getMemoryInstruction((int)address);
   memory.putMemoryInstruction(new stop(instruction),(int)address);
  }
  catch(MemoryException e)
  {
   throw new CPUException(" can't create the program exit because " + e.getMessage());
  }
 }

 /**
  * Returns the instruction in the stage defined by the parameter stage.
  *
  * @param stage the stage number.
  * @return the instruction in the stage defined by the parameter stage.
  * @see system.cpu.CPU#getStageInstruction(int)
  */
 public Instruction getStageInstruction(int stage)
 {
  switch(stage)
  {
   case 3:return execute;
   case 2:return decode;
   case 1:return fetch;
  }
  return null;
 }
 
 /**
  * Extends immediate value.
  *
  * @param imm the immediate value.
  * @return the immediate value extended.
  */
 public final int signExtendedIMM(int imm)
 {
  if(imm_flag)
  {
   this.imm = this.imm | imm;
   imm_flag = false;
   return this.imm;
  }
  else
  {
   if((imm & 0x8000) == 0x8000)
	 imm = imm | 0xffff0000;
   this.imm = imm;
   return imm;
  }
 }

 /**
  * Returns the stage name.
  *
  * @param stage the stage number.
  * @return the stage name.
  * @see system.cpu.CPU#getStageName(int)
  */
 public String getStageName(int stage)
 {
  switch(stage)
  {
   case 3:return "execute";
   case 2:return "decode";
   case 1:return "fetch";
  }
  return null;
 }
  
 /**
  * Returns the number of instructions execute by the cpu.
  *
  * @return the number of instructions execute by the cpu.
  * @see system.cpu.CPU#getNumberOfInstructions()
  */
 public long getNumberOfInstructions()
 {
  return instructions;	 
 }
 
 /**
  * Resets the cpu. Puts the cpu in the initial state.
  *
  * @see system.cpu.CPU#reset()
  */
 public final void reset()
 {
  instructions = 0;
  
  fetch   = empty;
  decode  = empty;
  execute = empty;
  
  general = new int[number_general];
  pc  = 0;
  msr = 0;
  ear = 0;
  esr = 0; 
  btr = 0;
  fsr = 0;
  pvr = new int[number_pvr];
  
  pc_next = 0;
  imm = 0;
  
  imm_flag = false;
 }
 
 // -------------------
 // methods used by GDB
 // ------------------- 

 /**
  * Sets an register of the cpu in GDB mode. 
  *
  * @param register identify what register must set.<p>
  * 0 to 31 - general registers.<br> 
  * 32 - program counter register (PC). In this case the pipeline is flushed and the instructions loaded again with the program counter register (PC) value has reference.<br>
  * 33 - machine status register (MSR).<br>
  * 34 - exception address register (EAR).<br>
  * 35 - exception status register (ESR).<br>
  * 36 - branch target register (BTR).<br>
  * 37 - floating point status register (FSR).<br>
  * 38 to 50 - processor version registers (PVR).
  * @throws CPUException if any error occurs when sets the program counter register (PC) value. The program counter register (PC) value may point to a memory's address not mapped. 
  * @see system.cpu.CPU#putRegisterGDB(int, int)
  */
 public void putRegisterGDB(int register,int value) throws CPUException
 {
  if(register > 0 && register <= 31)
	general[register] = value;
  switch(register)
  {
   case 32:try
           {
	        memory.cleanStatus();
	        execute = memory.getMemoryInstruction(value);
	        value = value + 4;
	        decode = memory.getMemoryInstruction(value);
	        value = value + 4;
	        fetch = memory.getMemoryInstruction(value);
	        pc = value;
           }
           catch(MemoryException e)
           {
        	throw new CPUException(e.getMessage());
           }
           break;
   case 33:msr = value;
           break;
   case 34:ear = value;
           break;
   case 35:esr = value;
           break;
   case 36:fsr = value;
           break;
   case 37:btr = value;
  }
  if(register >= 38 && register < 50)
	pvr[register - 38] = value; 
 }
 
 /**
  * Creates a control instruction breakpoint that contains the instruction that is in the same memory address where it will be placed.
  *
  * @param cell the instruction in the memory address where the breakpoint control instruction that will be placed.
  * @return the breakpoint control instruction that will be placed in the same memory address of the instruction in the parameter cell. 
  * @see system.cpu.CPU#createBreakPoint(system.cpu.Instruction)
  */
 public final Instruction createBreakPoint(Instruction cell)
 {
  return (Instruction)(new breakpoint(cell,this));
 }
 
 /**
  * Inserts the breakpoint in the pipeline if is the case.
  *
  * @param address the memory's address where the breakpoint should be placed.
  * @return 1 - if the breakpoint was inserted.<br>
  *         -1 - if the breakpoint wasn't inserted.
  * @see system.cpu.CPU#insertBreakPoint(int)
  */
 public final int insertBreakPoint(int address)
 {
  if(address == decode.getPC())	 
  {
   if(decode.toString().equals("breakpoint") || fetch.toString().equals(""))
     return -1;
   decode = new breakpoint(decode,this);
   return 1;
  }
  if(address == fetch.getPC())	 
  {
   if(fetch.toString().equals("breakpoint") || fetch.toString().equals(""))
     return -1;
   fetch = new breakpoint(fetch,this);
   return 1;  
  }
  return -1;	 
 }
 
 /**
  * Removes the breakpoint in the pipeline if is the case.
  *
  * @param address the address where the breakpoint should be remove.
  * @return 1 - if the breakpoint was removed.<br>
  *         -1 - if the breakpoint wasn't removed.
  * @see system.cpu.CPU#removeBreakPoint(int)
  */
 public final int removeBreakPoint(int address)
 {
  if(address == execute.getPC())	 
  {
   if(execute.toString().equals("breakpoint"))
   {
    execute = execute.getInstruction();
	return 1;
   }
   return -1;
  }
  if(address == decode.getPC())	 
  {
   if(decode.toString().equals("breakpoint"))
   {
    decode = decode.getInstruction();
    return 1;
   }
   return -1;
  }
  if(address == fetch.getPC())	 
  {
   if(fetch.toString().equals("breakpoint"))
   {
    fetch = fetch.getInstruction();
	return 1;
   }
   return -1;
  }
  return -1;
 }
 
 /**
  * Returns an register of the cpu in GDB mode. 
  *
  * @param register identify what register must return.<p>
  * 0 to 31 - general registers.<br> 
  * 32 - program counter register (PC) in the execute stage.<br> 
  * 33 - machine status register (MSR).<br>
  * 34 - exception address register (EAR).<br>
  * 35 - exception status register (ESR).<br>
  * 36 - branch target register (BTR).<br>
  * 37 - floating point status register (FSR).<br>
  * 38 to 50 - processor version registers (PVR).
  * @return the register value with number equal to the parameter register.
  * @see system.cpu.CPU#getRegisterGDB(int)
  */
 public int getRegisterGDB(int register)
 {
  if(register >= 0 && register <= 31)
	return general[register];
  switch(register)
  {
   case 32:return execute.getPC();
   case 33:return msr;
   case 34:return ear;
   case 35:return esr;
   case 36:return fsr;
   case 37:return btr;
  }
  if(register >= 38 && register < 50)
	return pvr[register - 38];
  return 0; 
 }
 
 // ------------------------------------
 // methods used to access other objects
 // ------------------------------------
 
 /**
  * Returns the instruction set architecture decoder of the Fireworks Three Stage Pipeline processor.
  *
  * @return the instruction set architecture decoder of the Fireworks Three Stage Pipeline processor.
  * @see system.cpu.CPU#getInstructionSetArchitectureClass()
  */
 public InstructionSetArchitecture getInstructionSetArchitectureClass()
 {
  return inst_set_arq;
 }
 
 // -----------------------------------
 // methods used to display information
 // -----------------------------------
 
 /**
  * Returns the cpu name.
  * 
  * @return the cpu name.
  * @see java.lang.Object#toString()
  */
 public String toString()
 {
  return "FireWorks three stage pipeline";	 
 }
}
