package system.cpu;

/**
 * The instruction set architecture class. This class implements the instruction set architecture decoder of the ISA supported by the processor. 
 */
public abstract class InstructionSetArchitecture
{
 // ---------------------------------------
 // methods used to decode the instructions
 // ---------------------------------------
 
 /**
  * Returns the instruction decoded.
  *
  * @param address the memory's address where the instruction will be placed.
  * @param cell_value the value that will be placed in the memory address.
  * @return the instruction decoded.
  */
 public abstract Instruction decode(int address,int cell_value);
 
 // -------------------
 // methods auxiliaries
 // -------------------
 
 /**
  * Creates an illegal special instruction with the program counter register (PC) equal to the parameter address value.
  *
  * @param address the memory's address where the instruction will be placed.
  * @return the created illegal special instruction.
  */
 public abstract Instruction getIllegal(int address);
 
 /**
  * Creates an mapped special instruction with the program counter register (PC) equal to the parameter address value.
  *
  * @param address the memory's address where the instruction will be placed.
  * @return the created mapped special instruction.
  */
 public abstract Instruction getMapped(int address);
 
 // -----------------------------------
 // methods used to display information
 // -----------------------------------

 /**
  * Returns the instruction disassembled.
  *
  * @param value the instruction value to be disassembled.
  * @return the instruction disassembled.
  */
 public abstract String toString(int value); 
}
