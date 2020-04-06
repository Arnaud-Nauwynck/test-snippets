package testspringbootprofile.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AppPlaceholderService {

	@Value("${app.key1}")
	private String key1;

	@Value("${app.key2}")
	private String key2;

	@Value("${app.main-resource-key3:default}")
	private String mainResourceKey3;

	@Value("${app.main-and-config-resource-key3:default}")
	private String mainAndConfigResourceKey3;

	@Value("${app.basedir-and-config-key3:default}")
	private String basedirAndConfigKey3;

	@Value("${app.test-resource-key4:default}")
	private String testResourceKey4;
	
	@Value("${app.test-and-config-resource-key4:default}")
	private String testAndConfigResourceKey4;
	
	@Value("${app.profile1a-key5:default}")
	private String profile1aKey5;

	@Value("${app.profile2a-key6:default}")
	private String profile2aKey6;

	@Value("${app.profileshared-key7:default}")
	private String profileSharedKey7;
	
	@Value("${app.other-key8:default}")
	private String profileOtherKey8;
	
	
	  
	public void dumpPlaceholders() {
		System.out.println("app.key1:" + key1);
		System.out.println("app.key2:" + key2);
		System.out.println("app.main-resource-key3:" + mainResourceKey3);
		System.out.println("app.main-and-config-resource-key3: " + mainAndConfigResourceKey3);
		System.out.println("app.basedir-and-config-key3: " + basedirAndConfigKey3);
		System.out.println("app.test-resource-key4:" + testResourceKey4);
		System.out.println("app.test-and-config-resource-key4: " + testAndConfigResourceKey4);
		System.out.println("app.profile1a-key5:" + profile1aKey5);
		System.out.println("app.profile2a-key6:" + profile2aKey6);
		System.out.println("app.profileshared-key7:" + profileSharedKey7);
		System.out.println("app.other-key8: " + profileOtherKey8);
	}

}
