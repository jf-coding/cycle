package processors.microblaze_3sp.instruction;

import processors.microblaze_3sp.*;
import system.cpu.*;

/**
 * The pcmpbf instruction class.
 */
public class pcmpbf extends Instruction
{
 /** The Fireworks Three Stage Pipeline processor to which instruction belongs. */
 private fw_3sp cpu;
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
  * Instantiates a new pcmpbf instruction.
  *
  * @param cpu the Fireworks Three Stage Pipeline processor to which instruction belongs.
  * @param address the memory's address of the instruction.
  * @param rD the destination register.
  * @param rA the source register A.
  * @param rB the source register B.
  * @param latency the instruction's latency in the execute stage.
  */
 public pcmpbf(fw_3sp cpu,int address,int rD,int rA,int rB,int latency)
 {
  this.cpu = cpu;
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
  int aux_a;
  int aux_b;
  int aux_c;
  int aux_d;
  int aux_e;
  int aux_f;
  int aux_g;
  int aux_h;
  int aux_i;
  int aux_j;
  
  if(cycles < latency)
  {
   cycles++;
   return cpu_status.STALL;
  }
  cycles = 1;
  aux_a = cpu.getGeneral(rA);
  aux_b = cpu.getGeneral(rB);
  aux_c = aux_a & 0xff000000;
  aux_d = aux_b & 0xff000000;
  if(aux_c == aux_d)
	cpu.putGeneral(rD,1);
  else
  {
   aux_e = aux_a & 0x00ff0000;
   aux_f = aux_b & 0x00ff0000;
   if(aux_e == aux_f)
     cpu.putGeneral(rD,2);
   else
   {
	aux_g = aux_a & 0x0000ff00;
	aux_h = aux_b & 0x0000ff00;
	if(aux_g == aux_h)
	  cpu.putGeneral(rD,3);
    else
    {
     aux_i = aux_a & 0x000000ff;
     aux_j = aux_b & 0x000000ff;
     if(aux_i == aux_j)
       cpu.putGeneral(rD,4);
     else
       cpu.putGeneral(rD,0);
    }
   }
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
  return "pcmpbf";
 }
}
