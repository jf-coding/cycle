package processors.microblaze_3sp;

import system.cpu.*;

/**
 * The illegal special instruction class.
 */
public final class illegal extends Instruction
{
 /** The memory's address of the illegal instruction. Or memory's address of the illegal instruction in the moment it was fetched. */
 private int pc;
	
 /**
  * Instantiates a new illegal special instruction.
  *
  * @param address the memory's address of the illegal instruction.
  */
 public illegal(int address)
 {
  pc = address;
 }
 
 // -----------------------------------------------
 // methods to operate instructions in the pipeline
 // -----------------------------------------------
 
 /**
  * Performs one operation cycle of the instruction in the pipeline stage. The pipeline stage is define by the parameter stage.<br>
  * In Fireworks Three Stage Pipeline processor the instruction only operates in the execute pipeline stage. Because of that, the parameter stage in practice is never used.   
  * 
  * @param stage the pipeline stage where the instruction is operating.<p>
  * 1 - fetch pipeline stage.<br>
  * 2 - decode pipeline stage.<br>
  * 3 - execute pipeline stage.
  * @return the cpu status ILLEGAL. 
  * @see system.cpu.Instruction#Stage(int)
  */
 public final int Stage(int stage)
 {
  return cpu_status.ILLEGAL;	 
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
 public int getPC()
 {
  return pc;	 
 }
 
 // -----------------------------------
 // methods used to display information
 // -----------------------------------
 
 /**
  * Returns the special instruction name.
  * 
  * @return the special instruction name.
  * @see java.lang.Object#toString()
  */
 public String toString()
 {
  return "illegal";
 }
}
