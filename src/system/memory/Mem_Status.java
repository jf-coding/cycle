package system.memory;

/**
 * The data memory status class.
 */
public class Mem_Status 
{
 /** The data memory status STANBY, the first cycle perform by an instruction accessing to the data memory. */
 public static final int STANBY    = 1;
 /** The data memory status ACCESS, cpu accessing to the data memory. */
 public static final int ACCESS    = 2;
 /** The data memory status READY, data memory ready to be read or write. */
 public static final int READY     = 3;
 /** The data memory status MAPPED, access to an data memory address not mapped. */
 public static final int MAPPED    = 4;
 /** The data memory status UNALIGNED, unaligned access to the data memory. */
 public static final int UNALIGNED = 5; 	 
}
