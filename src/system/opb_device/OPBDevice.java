package system.opb_device;

/**
 * The OPBDevice class. This class implements the OPB Devices.
 */
public abstract class OPBDevice
{	
 // --------------------------------------
 // methods used to control the simulation
 // --------------------------------------
 
 /**
  * Performs one operation cycle of the device.
  *
  * @return the interrupt state.
  */
 public abstract int cycle();
 
 // -------------------
 // methods auxiliaries
 // -------------------
 
 /**
  * Returns the read latency value.
  *
  * @return the read latency value.
  */
 public abstract int getReadLatency();
 
 /**
  * Returns the write latency value.
  *
  * @return the write latency value.
  */
 public abstract int getWriteLatency();
 
 /**
  * Returns the interrupt status of the device.
  *
  * @return the interrupt status of the device.
  */
 public abstract boolean getInterrupt();
 
 /**
  * Sets the interrupt status of the device to disabled.
  */
 public abstract void clearInterrupt();
 
 /**
  * Clears all registers of the device.
  */
 public abstract void reset();
}
