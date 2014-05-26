package processors.microblaze_3sp.instruction;

import processors.microblaze_3sp.*;
import system.cpu.*;

/**
 * The mfs instruction class.
 */
public class mfs extends Instruction
{
 /** The Fireworks Three Stage Pipeline processor to which instruction belongs. */
 private fw_3sp cpu;
 /** The memory's address of the instruction. Or memory's address of the instruction in the moment it was fetched. */
 private int pc;
 /** The destination register. */
 private int rD;
 /** The special purpose register. */
 private int rS;
 /** The instruction's latency in the execute stage. */
 private int latency;
 /** The number of cycles performed in the execute stage. */
 private int cycles = 1;
 
 /**
  * Instantiates a new mfs instruction.
  *
  * @param cpu the Fireworks Three Stage Pipeline processor to which instruction belongs.
  * @param address the memory's address of the instruction.
  * @param rD the destination register.
  * @param rS the special purpose register.
  * @param latency the instruction's latency in the execute stage.
  */
 public mfs(fw_3sp cpu,int address,int rD,int rS,int latency)
 {
  this.cpu = cpu;
  pc = address;
  this.rD = rD;
  this.rS = rS;
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
  int msr;
  int ear;
  int esr;
  int fsr;
  int btr;
  int pvr;

  if(cycles < latency)
  {
   cycles++;
   return cpu_status.STALL;
  }
  cycles = 1;
  switch(rS)
  {
   case 0x0000:cpu.putGeneral(rD,pc);
               break;
   case 0x0001:msr = cpu.getMSR();
	           cpu.putGeneral(rD,msr);
	           break;
   case 0x0003:ear = cpu.getEAR();
               cpu.putGeneral(rD,ear);
               break;
   case 0x0005:esr = cpu.getESR();
               cpu.putGeneral(rD,esr);
               break;
   case 0x0007:fsr = cpu.getFSR();
               cpu.putGeneral(rD,fsr);
               break;
   case 0x000B:btr = cpu.getBTR();
               cpu.putGeneral(rD,btr);
               break;
   case 0x2000:pvr = cpu.getPVR(0);
               cpu.putGeneral(rD,pvr);
               break;
   case 0x2001:pvr = cpu.getPVR(1);
               cpu.putGeneral(rD,pvr);
               break;               
   case 0x2002:pvr = cpu.getPVR(2);
               cpu.putGeneral(rD,pvr);
               break;
   case 0x2003:pvr = cpu.getPVR(3);
               cpu.putGeneral(rD,pvr);
               break;
   case 0x2004:pvr = cpu.getPVR(4);
               cpu.putGeneral(rD,pvr);
               break;
   case 0x2005:pvr = cpu.getPVR(5);
               cpu.putGeneral(rD,pvr);
               break;
   case 0x2006:pvr = cpu.getPVR(6);
               cpu.putGeneral(rD,pvr);
               break;
   case 0x2007:pvr = cpu.getPVR(7);
               cpu.putGeneral(rD,pvr);
               break;
   case 0x2008:pvr = cpu.getPVR(8);
               cpu.putGeneral(rD,pvr);
               break;
   case 0x2009:pvr = cpu.getPVR(9);
               cpu.putGeneral(rD,pvr);
               break;
   case 0x200a:pvr = cpu.getPVR(10);
               cpu.putGeneral(rD,pvr);
               break;
   case 0x200b:pvr = cpu.getPVR(11);
               cpu.putGeneral(rD,pvr);
               break;
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
  return "mfs";
 }
}
