package system.cpu;

/**
 * The cpu class. This class implements the processor. 
 */
public abstract class CPU 
{
 // --------------------------------------
 // methods used to control the simulation
 // --------------------------------------
 
 /**
  * Performs one operation cycle of the cpu.
  *
  * @param interrupt the interrupt status.
  * @return the cpu status after one cycle operation of the cpu.
  */
 public abstract int cycle(int interrupt);
 
 // --------------------------------
 // methods used to access registers
 // --------------------------------

 /**
  * Returns an register of the cpu. 
  *
  * @param register identify what register must return.
  * @return the register value with number equal to the parameter register.
  */
 public abstract int getRegister(int register);
 
 /**
  * Sets an register of the cpu. 
  *
  * @param register identify what register must set.
  * @param value the register value.
  */
 public abstract void putRegister(int register,int value);
 
 /**
  * Returns the value of some registers bits, flags or internal registers of the cpu which the user don't have access in the real cpu.
  * This record is used mostly for debugging purposes.
  * 
  * @param register identify registers bits, flags or internal registers of the cpu which the user don't have access in the real cpu.
  * @return the value of the registers bits, flags or internal registers of the cpu which the user don't have access in the real cpu.
  */
 public abstract int getStatus(int register);
 
 // -------------------
 // methods auxiliaries
 // -------------------

 /**
  * Inserts the stop control instruction in the memory's address where the program exit.
  *
  * @param address the memory's address where the stop control instruction will be placed.
  * @throws CPUException if any error occurs when try to placed the instruction in the memory.
  */
 public abstract void createProgram_Exit(long address) throws CPUException;
 
 /**
  * Returns the instruction in the stage defined by the parameter stage.
  *
  * @param stage the stage number.
  * @return the instruction in the stage defined by the parameter stage.
  */
 public abstract Instruction getStageInstruction(int stage);
 
 /**
  * Returns the stage name.
  *
  * @param stage the stage number.
  * @return the stage name.
  */
 public abstract String getStageName(int stage);
 
 /**
  * Returns the number of instructions execute by the cpu.
  *
  * @return the number of instructions execute by the cpu.
  */
 public abstract long getNumberOfInstructions();
 
 /**
  * Resets the cpu. Puts the cpu in the initial state.
  */
 public abstract void reset();
 
 // -------------------
 // methods used by GDB
 // ------------------- 

 /**
  * Returns an register of the cpu in GDB mode. 
  *
  * @param register identify what register must return.
  * @return the register value with number equal to the parameter register.
  */
 public abstract int getRegisterGDB(int register);
 
 /**
  * Sets an register of the cpu in GDB mode. 
  *
  * @param value the register value.
  * @throws CPUException if any error occurs when sets the program counter register (PC) value. The program counter register (PC) value may point to a memory's address not mapped.
  */
 public abstract void putRegisterGDB(int register,int value) throws CPUException;
 
 /**
  * Creates a control instruction breakpoint that contains the instruction that is in the same memory address where it will be placed.
  *
  * @param instruction the instruction in the memory address where the breakpoint control instruction that will be placed.
  * @return the breakpoint control instruction that will be placed in the same memory address of the instruction in the parameter cell.
  */
 public abstract Instruction createBreakPoint(Instruction instruction);
 
 /**
  * Inserts the breakpoint in the pipeline if is the case.
  *
  * @param address the memory's address where the breakpoint should be placed.
  * @return 1 - if the breakpoint was inserted.<br>
  *         -1 - if the breakpoint wasn't inserted.
  */
 public abstract int insertBreakPoint(int address);
 
 /**
  * Removes the breakpoint in the pipeline if is the case.
  *
  * @param address the address where the breakpoint should be remove.
  * @return 1 - if the breakpoint was removed.<br>
  *         -1 - if the breakpoint wasn't removed.
  */
 public abstract int removeBreakPoint(int address);

 // ------------------------------------
 // methods used to access other objects
 // ------------------------------------
 
 /**
  * Returns the instruction set architecture decoder of the cpu.
  *
  * @return the instruction set architecture decoder of the cpu.
 */
 public abstract InstructionSetArchitecture getInstructionSetArchitectureClass();
}
