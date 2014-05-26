package opbdevices.opbuartlite;

import system.opb_device.*;

/**
 * The Rx register class.
 */
public final class rx extends OPBRegister
{
 /** The OPB UART Lite device to which the register belongs. */
 private opbuartlite device;
 
 /**
  * Instantiates a new Rx register.
  *
  * @param device the OPB UART Lite device to which the register will belong.
  */
 public rx(opbuartlite device)
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
  return Keyin.inChar("");
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
  return "RX";	 
 }
}
