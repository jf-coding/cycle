package opbdevices.opbtimercounter;

import system.opb_device.*;

/**
 * The TLR0 register class.
 */
public final class tlr0 extends OPBRegister
{
 /** The OPB Timer/Counter device to which the register belongs. */
 private opbtimercounter device;
 /** The value of the  register. */
 private int tlr0;
 
 /** The update flag. Is verified after the completion of the cycle to check if the register value needs to be updated.<p>
  *  false - the register value don't need to be updated.<br>
  *  true - the register value needs to be updated. */
 private boolean update;
 /** The temporary value of the register. After the completion of the cycle this value is used to update the register value. */
 private int value;
 
 /**
  * Instantiates a new TLR0 register.
  *
  * @param device the OPB Timer/Counter device to which the register will belong.
  */
 public tlr0(opbtimercounter device)
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
  return tlr0;
 }
 
 /**
  * Sets the temporary value of the register.
  *
  * @param value the temporary value of the register.
  * @see system.opb_device.OPBRegister#put(int)
  */
 public final void put(int value)
 {
  this.value = value;
  update = true;
 }
 
 /**
  * Updates the register value after completion of the cycle.
  *
  * @see system.opb_device.OPBRegister#update()
  */
 public final void update()
 {
  if(update == true)
  {
   update = false;
   tlr0 = value;
  }
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
  return "TLR0";	 
 }
}
