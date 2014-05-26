package system.file;

/**
 * The ELF binary parser exception class.
 */
public class ElfBinaryParserException extends Exception 
{
 /** The serialVersionUID. */
 private static final long serialVersionUID = 1L;
 /** The error message. */
 private String message;
  
 /**
  * Instantiates a new Elf binary parser exception.
  *
  * @param s the error message.
  */
 public ElfBinaryParserException(String s)
 {
  message = s;	
 }
 
 /**
  * Returns the error message.
  *
  * @return the error message.
  * @see java.lang.Throwable#getMessage()
  */
 public String getMessage()
 {
  return message;
 }
}
