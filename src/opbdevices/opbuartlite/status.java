package opbdevices.opbuartlite;

import system.opb_device.*;

/**
 *  The Status register class.
 */
public final class status extends OPBRegister
{ 
 /** The OPB UART Lite device to which the register belongs. */
 private opbuartlite device;
 
 /** The value of the register. By default equal to 0x5. */
 private int status = 0x5;	
	 
 /**
  * Instantiates a new Status register.
  *
  * @param device the OPB UART Lite device which the register will belong.
  */
 public status(opbuartlite device)
 {
  this.device = device;
 }
 
 // -----------------------------------
 // methods used to handle the register
 // -----------------------------------	
	
 /**
  * Returns the current value of the register.
  *
  * @return the current value of the register.
  * @see system.opb_device.OPBRegister#get()
  */
 public final int get()
 {
  return status;
 }

 /**
  * Sets the temporary value of the register.
  *
  * @param value the temporary value of the register.
  * @see system.opb_device.OPBRegister#put(int)
  */
 public final void put(int value)
 {
 }
 
 /**
  * Updates the register value after completion of the cycle. Checks if the register needs to be updated, if true uses temporary value to update the register value.
  *
  * @see system.opb_device.OPBRegister#update()
  */
 public final void update()
 {
 }
 
 // -------------------
 // methods auxiliaries
 // -------------------
 
 /**
  * Returns the OPB UART Lite device to which the register belongs.
  *
  * @return the OPB UART Lite device to which the register belongs.
  * @see system.opb_device.OPBRegister#getOPBDeviceClass()
  */
 public OPBDevice getOPBDeviceClass()
 {
  return device;	 
 }
 
 // -----------------------------------
 // methods used to display information
 // -----------------------------------
 
 /**
  * Returns the device name to which the register belongs.
  *
  * @return the device name to which the register belongs.
  * @see system.opb_device.OPBRegister#deviceName()
  */
 public String deviceName()
 {
  return device.toString();	 
 }
 
 /**
  * Returns the register name.
  * 
  * @return the register name.
  * @see java.lang.Object#toString()
  */
 public String toString()
 {
  return "STATUS";	 
 }
}
