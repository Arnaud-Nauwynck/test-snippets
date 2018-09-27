package testspringbootprofile;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import testspringbootprofile.config.AppPlaceholderService;
import testspringbootprofile.config.Ctx1Config;
import testspringbootprofile.impl.MockedBean;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(
		classes=Ctx1Config.class,
		properties= {"JUnit-AppProfile1Ctx1Test"}
		)
@ActiveProfiles({"profile1", 
	"profile1a", // <= ?? not read from spring.profiles.active=profile1a in aplication-profile1.yml  
	"other", 
	"ctx1config"}) // to 
public class AppProfile1Ctx1Test extends AbstractTest {

	static {
		System.out.println();
		System.out.println("###### app.AppProfile1Test.cinit");
	}

	@Autowired // not @MockBean !
	private MockedBean mockedBean;

	@Autowired
	private AppPlaceholderService placeholders;

	@Test
	public void testDumpPlaceholders() {
		System.out.println("### app.AppProfile1Test.testDumpPlaceholders");
		placeholders.dumpPlaceholders();
	}
	
	@Test
	public void testFoo() {
		System.out.println("### app.testFoo");
		mockedBean.foo();
	}

}
