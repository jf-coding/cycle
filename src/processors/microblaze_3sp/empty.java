package processors.microblaze_3sp;

import system.cpu.*;

/**
 * The empty special instruction class.
 */
public final class empty extends Instruction
{
 /**
  * Instantiates a new empty special instruction.
  */
 public empty()
 {
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
  * @return the cpu status EMPTY. 
  * @see system.cpu.Instruction#Stage(int)
  */
 public final int Stage(int stage)
 {
  return cpu_status.EMPTY;
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
  return "";
 }
}
