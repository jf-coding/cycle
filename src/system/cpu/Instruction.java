package system.cpu;

/**
 * The instruction class. This class implements the instructions processor.
 */
public class Instruction 
{
 // -----------------------------------------------
 // methods to operate instructions in the pipeline
 // -----------------------------------------------
 
 /**
  * Performs one operation cycle of the instruction in the pipeline stage. The pipeline stage is define by the parameter stage.<br>
  *
  * @param stage the pipeline stage where the instruction is operating.
  * @return the cpu status. 
  */
 public int Stage(int stage)
 {
  return 0;
 }
 
 // ------------------------------------
 // methods used to access other objects
 // ------------------------------------
 
 /**
  * Returns The instruction where the control instruction was placed. This method is only used by the control instructions (breakpoint and stop).
  *
  * @return the instruction where the control instruction was placed.
  */
 public Instruction getInstruction()
 {
  return null;	 
 }
 
 // -------------------
 // methods auxiliaries
 // -------------------
 
 /**
  * Returns the Program Counter (PC) of the instruction.<p>
  * If the instruction is accessed from the memory returns the address of its position in the memory. <br>
  * If the instruction is accessed from any pipeline stage returns the address of its position in the memory on the moment it was fetched.
  * @return the Program Counter (PC) of the instruction.
  */
 public int getPC()
 {
  return 0;	 
 }
}
