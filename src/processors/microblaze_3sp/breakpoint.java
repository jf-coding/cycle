package processors.microblaze_3sp;

import system.cpu.*;

/**
 * The breakpoint control instruction class.
 */
public final class breakpoint extends Instruction
{ 
 /** The Fireworks Three Stage Pipeline processor to which instruction belongs. */
 private fw_3sp cpu;
 /** The instruction where the breakpoint was placed. */
 private Instruction instruction;

 /**
  * Instantiates a new breakpoint control instruction.
  *
  * @param instruction the instruction where the breakpoint was placed.
  * @param cpu the Fireworks Three Stage Pipeline processor to which instruction belongs.
  */
 public breakpoint(Instruction instruction,fw_3sp cpu)
 {
  this.instruction = instruction;
  this.cpu = cpu;
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
  * @return the cpu status BREAKPOINT. 
  * @see system.cpu.Instruction#Stage(int)
  */
 public final int Stage(int stage)
 {
  int aux;
  
  aux = instruction.getPC();
  cpu.putGeneral(16,aux);
  return cpu_status.BREAKPOINT;
 }

 // ------------------------------------
 // methods used to access other objects
 // ------------------------------------
 
 /**
  * Returns The instruction where the breakpoint was placed.
  *
  * @return the instruction where the breakpoint was placed.
  * @see system.cpu.Instruction#getInstruction()
  */
 public final Instruction getInstruction()
 {
  return instruction;	 
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
  return "breakpoint";
 }
}
