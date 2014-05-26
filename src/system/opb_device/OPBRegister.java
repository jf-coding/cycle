package system.opb_device;

/**
 * The OPBRegister class. This class implements the OPB Device registers.
 */
public abstract class OPBRegister 
{ 
 // -----------------------------------
 // methods used to handle the register
 // -----------------------------------	
	 
 /**
  * Returns the current value of the register.
  *
  * @return the current value of the register.
  */
 public abstract int get();
	 
 /**
  * Sets the temporary value of the register.
  *
  * @param value the temporary value of the register.
  */
 public abstract void put(int value);
 
 /**
  * Updates the register value after completion of the cycle. Checks if the register needs to be updated, if true uses temporary value to update the register value.
  */
 public abstract void update();
 
 // -------------------
 // methods auxiliaries
 // -------------------
 
 /**
  * Returns the device to which the register belongs.
  *
  * @return the device to which the register belongs.
  */
 public abstract OPBDevice getOPBDeviceClass();

 // -----------------------------------
 // methods used to display information
 // -----------------------------------
 
 /**
  * Returns the register name.
  * 
  * @return the register name.
  */
 public abstract String deviceName();
}
