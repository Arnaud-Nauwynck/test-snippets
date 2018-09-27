package testspringbootprofile;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import testspringbootprofile.config.AppPlaceholderService;
import testspringbootprofile.config.Ctx2Config;
import testspringbootprofile.impl.MockedBean;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(
		classes=Ctx2Config.class,
		properties= {"JUnit-AppProfile2Ctx2Test"}
		)
@ActiveProfiles({"profile2", "other", "ctx2config"})
public class AppProfile2Ctx2Test extends AbstractTest {

	static {
		System.out.println();
		System.out.println("###### app.AppProfile2Test.cinit");
	}

	@Autowired // not @MockBean !
	private MockedBean mockedBean;

	@Autowired
	private AppPlaceholderService placeholders;

	@Test
	public void testDumpPlaceholders() {
		System.out.println("### app.AppProfile2Test.testDumpPlaceholders");
		placeholders.dumpPlaceholders();
	}
	
	@Test
	public void testFoo() {
		System.out.println("### app.testFoo");
		mockedBean.foo();
	}

}
