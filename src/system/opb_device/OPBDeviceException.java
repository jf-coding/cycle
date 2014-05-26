package system.opb_device;

/**
 * The opb device exception.
 */
public class OPBDeviceException extends Exception 
{
 /** The serialVersionUID. */
 private static final long serialVersionUID = 1L;
 /** The error message. */
 private String message;
 
  /**
   * Instantiates a new opb device exception.
   *
   * @param s the error message.
   */
  public OPBDeviceException(String s)
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
