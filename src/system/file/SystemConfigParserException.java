package system.file;

/**
 * The system configuration parser exception class.
 */
public class SystemConfigParserException extends Exception 
{
 /** The serialVersionUID. */
 private static final long serialVersionUID = 1L;
 /** The error message. */
 private String message;
 
 /**
  * Instantiates a new system configuration parser exception.
  *
  * @param s the error message.
  */
 public SystemConfigParserException(String s)
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
