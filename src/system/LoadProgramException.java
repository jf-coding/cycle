package system;

/**
 * The load program exception class.
 */
public class LoadProgramException extends Exception 
{
 /** The serialVersionUID. */
 private static final long serialVersionUID = 1L;
 /** The error message. */
 private String message;
 
 /**
  * Instantiates a new load program exception.
  *
  * @param s the error message.
  */
 public LoadProgramException(String s)
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
