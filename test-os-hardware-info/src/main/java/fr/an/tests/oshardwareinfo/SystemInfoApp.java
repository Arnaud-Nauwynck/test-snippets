package fr.an.tests.oshardwareinfo;

import java.util.Arrays;

import oshi.PlatformEnum;
import oshi.SystemInfo;
import oshi.hardware.Baseboard;
import oshi.hardware.CentralProcessor;
import oshi.hardware.CentralProcessor.LogicalProcessor;
import oshi.hardware.ComputerSystem;
import oshi.hardware.Display;
import oshi.hardware.Firmware;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.PowerSource;
import oshi.hardware.SoundCard;
import oshi.hardware.UsbDevice;
import oshi.software.os.OperatingSystem;

public class SystemInfoApp {

	public static void main(String[] args) {
		PlatformEnum currentPlatform = SystemInfo.getCurrentPlatformEnum();
		System.out.println("currentPlatform:" + currentPlatform);
		
		SystemInfo systemInfo = new SystemInfo();
		
		{ HardwareAbstractionLayer hardware = systemInfo.getHardware();
			System.out.println("hardware class:" + hardware.getClass());
			
			{ // hardware.computerSystem
				ComputerSystem computerSystem = hardware.getComputerSystem();
				
			    System.out.println("manufactorer:" + computerSystem.getManufacturer());
			    System.out.println("model:" + computerSystem.getModel());
			    System.out.println("serialNumber:" + computerSystem.getSerialNumber());
		
			    { Firmware firmware  = computerSystem.getFirmware();
				    System.out.println("firmware.manufacturer:" + firmware.getManufacturer());
				    System.out.println("firmware.name:" + firmware.getName());
				    System.out.println("firmware.description:" + firmware.getDescription());
				    System.out.println("firmware.version:" + firmware.getVersion());
				    System.out.println("firmware.releaseDate:" + firmware.getReleaseDate());
			    }
			    
			    { Baseboard baseboard = computerSystem.getBaseboard();
				    System.out.println("baseboard.manufacturer:" + baseboard.getManufacturer());
				    System.out.println("baseboard.model:" + baseboard.getModel());
				    System.out.println("baseboard.version:" + baseboard.getVersion());
				    System.out.println("baseboard.serialNumber:" + baseboard.getSerialNumber());
			    }
			}
	    
			{
				System.out.println("");
				CentralProcessor centralProcessor = hardware.getProcessor();

				System.out.println("centralProcessor.vendor:" + centralProcessor.getVendor());
				System.out.println("centralProcessor.name:" + centralProcessor.getName());
				System.out.println("centralProcessor.vendorFreq:" + centralProcessor.getVendorFreq());
				System.out.println("centralProcessor.maxFreq:" + centralProcessor.getMaxFreq());
				
				long[] currentFreq = centralProcessor.getCurrentFreq();
				System.out.println("centralProcessor.currentFreq:" + Arrays.toString(currentFreq));
				
				System.out.println("centralProcessor.processorID:" + centralProcessor.getProcessorID());
				System.out.println("centralProcessor.Identifier:" + centralProcessor.getIdentifier());
				System.out.println("centralProcessor.isCpu64:" + centralProcessor.isCpu64bit());  // BUG !!
				System.out.println("centralProcessor.Stepping:" + centralProcessor.getStepping());
				System.out.println("centralProcessor.Model:" + centralProcessor.getModel());
				System.out.println("centralProcessor.Family:" + centralProcessor.getFamily());

			    LogicalProcessor[] logicalProcessors = centralProcessor.getLogicalProcessors();
			    System.out.println("centralProcessor.logicalProcessors.count:" + centralProcessor.getLogicalProcessorCount()); //logicalProcessors.length);
			    
// TODO
//			    double getSystemCpuLoadBetweenTicks(long[] oldTicks);
//
//			    long[] getSystemCpuLoadTicks();
//
//			    double getSystemCpuLoad();
//
//			    double[] getSystemLoadAverage(int nelem);
//
//			    double[] getProcessorCpuLoadBetweenTicks(long[][] oldTicks);
//
//			    long[][] getProcessorCpuLoadTicks();
//
//			    long getSystemUptime();
//
//			    int getPhysicalProcessorCount();
//
//			    int getPhysicalPackageCount();
//
//			    long getContextSwitches();
//
//			    long getInterrupts();
//
//			    void updateAttributes();

			}
			
//		    GlobalMemory getMemory();
//			TODO
//
			{
				System.out.println();
				System.out.println("PowerSources");
				PowerSource[] powerSources = hardware.getPowerSources();
			    System.out.println("powerSources.count:" + powerSources.length);
			    for (int i = 0; i < powerSources.length; i++) {
			    	PowerSource ps = powerSources[i];
			    	
			    	System.out.println("powerSource[" + i + "].name:" + ps.getName());
			    	System.out.println("powerSource[" + i + "].remainingCapacity:" + ps.getRemainingCapacity());
			    	System.out.println("powerSource[" + i + "].timeRemaining:" + ps.getTimeRemaining());
			    }
			}
			

//		    HWDiskStore[] getDiskStores();
//			TODO
//
//		    NetworkIF[] getNetworkIFs();
//			TODO
//
			{
				System.out.println();
				System.out.println("Displays");
			    Display[] displays = hardware.getDisplays();
			    System.out.println("displays.count:" + displays.length);
			    for (int i = 0; i < displays.length; i++) {
			    	Display d = displays[i];
			    	System.out.println("display[" + i + "].edid:" + Arrays.toString(d.getEdid()));
			    }
			}
			
//
//		    Sensors getSensors();
//			TODO
//
			{
				System.out.println("USD Devices");
				UsbDevice[] u = hardware.getUsbDevices(false);
				System.out.println("USD Devices.count: " + u.length);
		    
				for(int i = 0; i < u.length; i++) {
					UsbDevice usb = u[i];
					String usbi = "USBDevice[" + i + "]";
					System.out.println();
					System.out.println(usbi + ".name:" + usb.getName());
				    System.out.println(usbi + ".vendor:" + usb.getVendor());
				    System.out.println(usbi + ".vendorId:" + usb.getVendorId());
				    System.out.println(usbi + ".productId:" + usb.getProductId());
				    System.out.println(usbi + ".serialNumber:" + usb.getSerialNumber());

				    // UsbDevice[] getConnectedDevices();
				}
			}

			{
				System.out.println("SoundCards");
				SoundCard[] soundCards = hardware.getSoundCards();
				System.out.println("SoundCards.count:" + soundCards.length);
				for(int i = 0; i < soundCards.length; i++) {
					SoundCard soundCard = soundCards[i];
					String si = "SoundCard[" + i + "]";
					System.out.println();
					
					System.out.println(si + ".name:" + soundCard.getName());
					System.out.println(si + ".driverVersion:" + soundCard.getDriverVersion());
					System.out.println(si + ".codec:" + soundCard.getCodec());
				}
			}		    
		}
		
		
		{ 
			OperatingSystem operatingSystem = systemInfo.getOperatingSystem();
//			TODO
		}
		
	}
}
