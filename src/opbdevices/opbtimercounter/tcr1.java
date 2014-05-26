package opbdevices.opbtimercounter;

import system.opb_device.*;

/**
 * The TCR1 register class.
 */
public final class tcr1 extends OPBRegister
{
 /** The OPB Timer/Counter device to which the register belongs. */
 private opbtimercounter device;
 /** The value of the register. */
 private int tcr1;
 
 /**
  * Instantiates a new TCR1 register.
  *
  * @param device the OPB Timer/Counter device to which the register will belong.
  */
 public tcr1(opbtimercounter device)
 {
  this.device = device;	 
 }
 
 // -----------------------------------
 // methods used to handle the register
 // -----------------------------------
	
 /**
  * Returns the current value of the register.
  *
  * @return the current value of the register
  * @see system.opb_device.OPBRegister#get()
  */
 public final int get()
 {
  return tcr1;
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
  * Sets the value of the register. 
  *
  * @param tcr1 the value of the register.
  */
 public final void putTCR1(int tcr1)
 {
  this.tcr1 = tcr1;	 
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
  * Returns the OPB Timer/Counter device to which the register belongs.
  *
  * @return the OPB Timer/Counter device to which the register belongs.
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
  return "TCR1";	 
 }
}
