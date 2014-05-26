package processors.microblaze_3sp;

import system.cpu.*;

/**
 * The stop control instruction class.
 */
public final class stop extends Instruction
{
 /** The instruction where the stop was placed. */
 private Instruction instruction;
 
 /**
  * Instantiates a new stop control instruction.
  *
  * @param instruction the instruction where the stop was placed.
  */
 public stop(Instruction instruction)
 {
  this.instruction = instruction;
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
  * @return the cpu status STOP. 
  * @see system.cpu.Instruction#Stage(int)
  */
 public final int Stage(int stage)
 {
  return cpu_status.STOP;
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
  return instruction.getPC();	 
 }
 
 // -----------------------------------
 // methods used to display information
 // -----------------------------------
 
 /**
  * Returns the control instruction name.
  * 
  * @return the control instruction name.
  * @see java.lang.Object#toString()
  */
 public String toString()
 {
  return instruction.toString();
 }
}
