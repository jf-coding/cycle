package processors.microblaze_3sp;

/**
 * The cpu status class.
 */
public final class cpu_status 
{
 // -------------------
 // instructions status
 // -------------------
	
 /** The cpu status NORMAL, the cpu is ready to perform the next instruction. */
 public static final int NORMAL     = 1;
 /** The cpu status DELAY_SLOT, the cpu is performing an delay slot. */
 public static final int DELAY_SLOT = 2;
 /** The cpu status JUMP_FLUSH, the cpu is performing an jump and flush the pipeline. */
 public static final int JUMP_FLUSH = 3;
 /** The cpu status JUMP, the cpu is performing an jump. */
 public static final int JUMP       = 4;
 /** The cpu status STALL, the cpu is performing an stall. */
 public static final int STALL      = 5;
 /** The cpu status EMPTY, the cpu is performing an empty instruction. */
 public static final int EMPTY      = 6;
 /** The cpu status DBZ, the instruction operation causes an divide by zero exception. */
 public static final int DBZ        = 7;
 /** The cpu status ILLEGAL, an illegal special instruction is execute and causes an illegal opcode exception. */
 public static final int ILLEGAL    = 8;
 /** The cpu status MAPPED, the cpu try execute an instruction of an memory address not mapped and causes an instruction bus exception. */
 public static final int MAPPED     = 9;
 /** The cpu status STOP, the cpu is performing the program exit. */
 public static final int STOP       = 10;
 /** The cpu status BREAKPOINT, the cpu is performing an breakpoint. */
 public static final int BREAKPOINT = 11;
 
 // -------------
 // memory status
 // -------------
 
 /** The cpu status MEM_ACCESS, the cpu is accessing to the data memory. */
 public static final int MEM_ACCESS    = 12;
 /** The cpu status MEM_MAPPED, the cpu is accessing to an memory data address not mapped and causes an data bus exception. */
 public static final int MEM_MAPPED    = 13;
 /** The cpu status MEM_UNALIGNED, the cpu is accessing to an unaligned data memory address and causes an unaligned exception. */
 public static final int MEM_UNALIGNED = 14; 
}
