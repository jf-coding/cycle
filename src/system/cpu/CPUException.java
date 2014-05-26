package system.cpu;

/**
 * The cpu exception class.
 */
public class CPUException extends Exception
{
 /** The serialVersionUID. */
 private static final long serialVersionUID = 1L; 
 /** The error message. */
 private String message;
 
 /**
  * Instantiates a new cpu exception.
  *
  * @param s the error message.
  */
 public CPUException(String s)
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
