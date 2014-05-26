package processors.microblaze_3sp.instruction;

import processors.microblaze_3sp.*;
import system.memory.*;
import system.cpu.*;

/**
 * The shi instruction class.
 */
public class shi extends Instruction
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
 /** The immediate value. */
 private int imm;
 /** The instruction's latency in the execute stage. */
 private int latency;
 /** The number of cycles performed in the execute stage. */
 private int cycles = 1;
 
 /**
  * Instantiates a new shi instruction.
  *
  * @param memory the memory system.
  * @param cpu the Fireworks Three Stage Pipeline processor to which instruction belongs.
  * @param address the memory's address of the instruction.
  * @param rD the destination register.
  * @param rA the source register A.
  * @param imm the immediate value.
  * @param latency the instruction's latency in the execute stage.
  */
 public shi(Memory memory,fw_3sp cpu,int address,int rD,int rA,int imm,int latency)
 {
  this.cpu = cpu;
  this.memory = memory;
  pc = address; 
  this.rD = rD;
  this.rA = rA;
  this.imm = imm;
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
  int aux_a;
  int aux_d;
  int status;
  int esr;
  
  if(cycles < latency)
  {
   cycles++;
   return cpu_status.STALL;
  }
  aux_a = cpu.getGeneral(rA);
  imm = cpu.signExtendedIMM(imm);
  address = aux_a + imm;
  aux_d = cpu.getGeneral(rD);
  status = memory.putHalfWord(address,aux_d);
  switch(status)
  {
   case Mem_Status.ACCESS:return cpu_status.MEM_ACCESS;
   case Mem_Status.READY:cycles = 1;
	                     return cpu_status.NORMAL;
   case Mem_Status.UNALIGNED:esr = 0x400 | (rD << 5);
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
  return "shi";
 }
}
