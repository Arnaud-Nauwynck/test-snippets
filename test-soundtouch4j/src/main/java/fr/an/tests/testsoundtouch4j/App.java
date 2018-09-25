package fr.an.tests.testsoundtouch4j;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.soundtouch4j.SoundTouchApi;
import org.soundtouch4j.SoundTouchApiClient;
import org.soundtouch4j.SoundTouchApiException;
import org.soundtouch4j.bass.BassApi;
import org.soundtouch4j.bass.BassGetResponse;
import org.soundtouch4j.basscapabilities.BaseCapabilitiesResponse;
import org.soundtouch4j.basscapabilities.BassCapabilitiesApi;
import org.soundtouch4j.common.ContentItem;
import org.soundtouch4j.common.SourceEnum;
import org.soundtouch4j.group.GroupApi;
import org.soundtouch4j.info.InfoApi;
import org.soundtouch4j.info.InfoResponse;
import org.soundtouch4j.key.KeyApi;
import org.soundtouch4j.name.NameApi;
import org.soundtouch4j.nowplaying.NowPlayingApi;
import org.soundtouch4j.nowplaying.NowPlayingResponse;
import org.soundtouch4j.preset.Preset;
import org.soundtouch4j.preset.PresetApi;
import org.soundtouch4j.preset.PresetResponse;
import org.soundtouch4j.select.SelectApi;
import org.soundtouch4j.source.SourceApi;
import org.soundtouch4j.source.SourceItem;
import org.soundtouch4j.source.SourceResponse;
import org.soundtouch4j.volume.VolumeApi;
import org.soundtouch4j.volume.VolumeGetResponse;
import org.soundtouch4j.zone.Zone;
import org.soundtouch4j.zone.ZoneApi;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.util.Key;

import io.resourcepool.ssdp.client.SsdpClient;
import io.resourcepool.ssdp.model.DiscoveryListener;
import io.resourcepool.ssdp.model.DiscoveryRequest;
import io.resourcepool.ssdp.model.SsdpService;
import io.resourcepool.ssdp.model.SsdpServiceAnnouncement;

/**
 * Hello world!
 *
 */
public class App  {
	
	
    public static void main( String[] args ) {
    	App app = new App();
    	app.run();
    }
    
    public void run() {
    	System.out.println("scan Boses devices:");
    	List<SsdpService> devices = scanDevices(10000);
    	System.out.println("scan => found " + devices.size() + " device(s)");

    	if (! devices.isEmpty()) {
	    	for(SsdpService device: devices) {
	    		String basePath = "http://" + device.getRemoteIp();
				testDeviceApi(basePath);
	    	}
    	} else {
    		System.out.println();
    		testDeviceApi("http://192.168.0.39:8090");
    	}
    		
    }

    public static class NameResponse {
    	@Key("text()")
    	private String value;

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}
    	
    }
	private void testDeviceApi(String baseURL) {
		System.out.println("device baseURL: " + baseURL);
		URL basePath;
		try {
			basePath = new URL(baseURL);
		} catch (MalformedURLException e) {
			throw new RuntimeException("Failed", e);
		}
		
		NetHttpTransport httpTransport = new NetHttpTransport();
		SoundTouchApi api = new SoundTouchApi(basePath, httpTransport);
		SoundTouchApiClient soundTouchApiClient = api.getSoundTouchApiClient();
		
		// "/info" api
		InfoApi infoApi = api.getInfoApi();
		InfoResponse info;
		try {
			info = infoApi.getInfo();
		} catch (SoundTouchApiException ex) {
			info = null;
			System.err.println("Failed get info " + ex.getMessage());
		}
		System.out.println("info => " + info);
		
		// "/name" api
		NameApi nameApi = api.getNameApi();
		String deviceName = null;
		try {
			// TODO .. add GET in api!
			NameResponse nameResponse = soundTouchApiClient.get("name", NameResponse.class);
			deviceName = nameResponse.getValue();
			System.out.println("name => " + deviceName);
		} catch (SoundTouchApiException e) {
			e.printStackTrace();
		}
		if (deviceName == null) {
			try {
				nameApi.setName(info.getName());
			} catch (SoundTouchApiException e) {
				System.err.println("Failed to set name");
				e.printStackTrace();
			}
		}
		
		// "volume" api
		VolumeApi volumeApi = api.getVolumeApi();
		try {
			VolumeGetResponse volume = volumeApi.getVolume();
			System.out.println("volume => " + volume);
			int targetVol = Math.max(10, volume.getTargetVolume());
			volumeApi.setVolume(targetVol);
		} catch (SoundTouchApiException e) {
			e.printStackTrace();
		}
			
		
		// "nowPlaying" api 
		NowPlayingApi nowPlayingApi = api.getNowPlayingApi();
		try {
			NowPlayingResponse nowPlaying = nowPlayingApi.nowPlaying();
			System.out.println("nowPlaying => " + nowPlaying);
		} catch (SoundTouchApiException e) {
			System.err.println("Failed to GET nowPlaying");
			e.printStackTrace();
		}
		
		// "bassCapabilities" api
		BassCapabilitiesApi bassCapabilitiesApi = api.getBassCapabilitiesApi();
		BaseCapabilitiesResponse bassCapabilities = null;
		try {
			bassCapabilities = bassCapabilitiesApi.getBassCapabilities();
			System.out.println("bassCapabilities => " + bassCapabilities);
		} catch (SoundTouchApiException e) {
			e.printStackTrace();
		}

		// "bass" api
		if (bassCapabilities != null && bassCapabilities.isBassAvailable()) {
			BassApi bassApi = api.getBassApi();
			try {
				BassGetResponse bassResp = bassApi.getBass();
				System.out.println("bass => " + bassResp);
				
				bassApi.setBass(bassResp.getTargetBass()+1);
				bassApi.setBass(bassResp.getTargetBass());
				
			} catch (SoundTouchApiException e) {
				System.err.println("Failed to GET bass");
				e.printStackTrace();
			}
		}
		
		// "group" api
		GroupApi groupApi = api.getGroupApi();
//		try {
			//TODO Read timed out ...
//			Group group = groupApi.getGroup();
//			System.out.println("group => " + group);
//		} catch (SoundTouchApiException e) {
//			e.printStackTrace();
//		}
		
		// "Zone" api
		ZoneApi zoneApi = api.getZoneApi();
		try {
			Zone zone = zoneApi.getZone();
			System.out.println("zone => " + zone);
			
			// TODO 
//			zoneApi.addZoneSlave(zone);
//			zoneApi.removeZoneSlave(zone);
//			zoneApi.setZone(zone);
			
		} catch (SoundTouchApiException e) {
			e.printStackTrace();
		}
		
		// "key" api  (emulate key press / key release of a key ..)
		KeyApi keyApi = api.getKeyApi();
//		try {
//			System.out.println("key press 'mute'");
//			keyApi.mute(); 
//			System.out.println("key press 'mute'");
//			keyApi.mute();
//		
//			keyApi.power(); keyApi.power();
//		} catch (SoundTouchApiException e) {
//			e.printStackTrace();
//		}

		// "source" api
		SourceApi sourceApi = api.getSourceApi(); 
		try {
			SourceResponse sources = sourceApi.getSources();
			System.out.println("sources => deviceId:" + sources.getDeviceID() + ", " + sources.getSourceItems().size() + " item(s)");
			for (SourceItem sourceItem : sources.getSourceItems()) {
				System.out.println("sourceItem: " + sourceItem); 
			}
		} catch (SoundTouchApiException e) {
			e.printStackTrace();
		}
		
		// "preset" api
		ContentItem selectedContentItem = null;
		PresetApi presetApi = api.getPresetApi();
		try {
			PresetResponse presets = presetApi.getPresets();
			System.out.println("presetList: " + presets.getPresetList().size() + " elt(s)");
			int presetIdx = 0;
			for (Preset preset : presets.getPresetList()) {
				System.out.println("preset[" + (presetIdx++) + "]: " + preset);
				ContentItem contentItem = preset.getContentItem();
				
				if (contentItem.getSource() == SourceEnum.INTERNET_RADIO) {
					selectedContentItem = contentItem; 
				}
				
			}
		} catch (SoundTouchApiException e) {
			e.printStackTrace();
		}

		
		// "select" api
		SelectApi selectApi = api.getSelectApi();
		try {
//			ContentItem content = new ContentItem();
//			source=INTERNET_RADIO, isPresetable=true, location='2808', sourceAccount='', itemName='Latina (France)', containerArt='http://item.radio456.com/007452/logo/logo-2808.jpg'
		
			if (selectedContentItem != null) {
				System.out.println("select " + selectedContentItem);
				selectApi.select(selectedContentItem);
				// selectApi.select(null);
			}
		} catch (SoundTouchApiException e) {
			e.printStackTrace();
		}


		// finally switch off ...
		try {
			Thread.sleep(1000);
			
			System.out.println("power off !");
			keyApi.power();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
    
    public List<SsdpService> scanDevices(final int timeToScanInMilliseconds) {
    	  final String boseUrn = "urn:schemas-upnp-org:device:MediaRenderer:1";
    	  
    	  final SsdpClient client = SsdpClient.create();
    	  final List<SsdpService> res = new ArrayList<SsdpService>();

    	  final DiscoveryRequest networkStorageDevice = DiscoveryRequest.builder()
    	      .serviceType(boseUrn)
    	      .build();

    	  client.discoverServices(networkStorageDevice, new DiscoveryListener() {
    	    @Override
    	    public void onServiceDiscovered(final SsdpService service) {
    	      System.out.println("Found service at IP: " + service.getRemoteIp());
    	      res.add(service);
    	    }

    	    @Override
    	    public void onServiceAnnouncement(final SsdpServiceAnnouncement announcement) {
    	      System.out.println("Service announced something: " + announcement);
    	    }

    	    @Override
    	    public void onFailed(final Exception ex) {
    	      System.out.println("Service onFailed: " + ex.getMessage());
    	    }
    	  });

    	  // ... wait 

    	  System.out.println("Discovery Stopped and Serives Found: " + res.size());
    	  client.stopDiscovery();
    	  return res;
    	}
    
}
