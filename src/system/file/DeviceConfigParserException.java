package system.file;

/**
 * The device configuration parser exception class.
 */
public class DeviceConfigParserException extends Exception 
{
 /** The serialVersionUID. */
 private static final long serialVersionUID = 1L;
 /** The error message. */
 private String message;
 
 /**
  * Instantiates a new device configuration parser exception.
  *
  * @param s the error message.
  */
 public DeviceConfigParserException(String s)
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
