package processors.microblaze_3sp.instruction;

import processors.microblaze_3sp.*;
import system.memory.*;
import system.cpu.*;

/**
 * The lhu instruction class.
 */
public class lhu extends Instruction
{
 /** The Fireworks Three Stage Pipeline processor to which instruction belongs. */
 private fw_3sp cpu;
 /** The memory system. */
 private Memory memory;
 /** The memory's address of the instruction. Or memory's address of the instruction in the moment it was fetched. */
 private int pc;
 /** The destination register. */
 private int rD;
 /** The source register A. */
 private int rA;
 /** The source register B. */
 private int rB;
 /** The instruction's latency in the execute stage. */
 private int latency;
 /** The number of cycles performed in the execute stage. */
 private int cycles = 1;
 
 /**
  * Instantiates a new lhu instruction.
  *
  * @param memory the memory system.
  * @param cpu the Fireworks Three Stage Pipeline processor to which instruction belongs.
  * @param address the memory's address of the instruction.
  * @param rD the destination register.
  * @param rA the source register A.
  * @param rB the source register B.
  * @param latency the instruction's latency in the execute stage.
  */
 public lhu(Memory memory,fw_3sp cpu,int address,int rD,int rA,int rB,int latency)
 {
  this.cpu = cpu;
  this.memory = memory;
  pc = address;
  this.rD = rD;
  this.rA = rA;
  this.rB = rB;
  this.latency = latency;
 }
 
 // -----------------------------------------------
 // methods to operate instructions in the pipeline
 // -----------------------------------------------
 
 /**
  * Performs one operation cycle of the instruction in the pipeline stage. The pipeline stage is define by the parameter stage.<br>
  * In Fireworks Three Stage Pipeline processor the instruction only operates in the execute pipeline stage. Because of that the parameter stage in practice is never used.   
  * 
  * @param stage the pipeline stage where the instruction is operating.<p>
  * 1 - fetch stage.<br>
  * 2 - decode stage.<br>
  * 3 - execute stage.
  * @return the cpu status after the instruction operates the cycle. 
  * @see system.cpu.Instruction#Stage(int)
  */
 public final int Stage(int stage)
 {
  int address;
  int data;
  int aux_a;
  int aux_b;
  int status;
  int esr;
  
  if(cycles < latency)
  {
   cycles++;
   return cpu_status.STALL;
  }
  aux_a = cpu.getGeneral(rA);
  aux_b = cpu.getGeneral(rB);
  address = aux_a + aux_b;
  status = memory.getHalfWord(address);
  switch(status)
  {
   case Mem_Status.ACCESS:return cpu_status.MEM_ACCESS;
   case Mem_Status.READY:data = memory.getData();
                         cpu.putGeneral(rD,data);
                         cycles = 1;
                         return cpu_status.NORMAL;
   case Mem_Status.UNALIGNED:esr = rD << 5;
                             cpu.putESR(esr);
	                         cpu.putEAR(address);
	                         cycles = 1;
                             return cpu_status.MEM_UNALIGNED;
   case Mem_Status.MAPPED:cpu.putEAR(address);
	                      cycles = 1;
                          return cpu_status.MEM_MAPPED;
  }
  return cpu_status.NORMAL;
 }
 
 // -------------------
 // methods auxiliaries
 // -------------------
 
 /**
  * Returns the Program Counter (PC) of the instruction.<p>
  * If the instruction is accessed from the memory returns the address of its position in the memory. <br>
  * If the instruction is accessed from any pipeline stage returns the address of its position in the memory on the moment it was fetched. 
  * @return the Program Counter (PC) of the instruction.
  * @see system.cpu.Instruction#getPC()
  */
 public final int getPC()
 {
  return pc;	 
 }
 
 // -----------------------------------
 // methods used to display information
 // -----------------------------------
  
 /**
  * Returns the instruction name.
  * 
  * @return the instruction name.
  * @see java.lang.Object#toString()
  */
 public String toString()
 {
  return "lhu";
 }
}
