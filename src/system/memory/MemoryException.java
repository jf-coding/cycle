package system.memory;

/**
 * The memory exception class.
 */
public class MemoryException extends Exception 
{
 /** The Constant serialVersionUID. */
 private static final long serialVersionUID = 1L;
 /** The message. */
 private String message;
 
 /**
  * Instantiates a new memory exception.
  *
  * @param s the error message.
  */
 public MemoryException(String s)
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
