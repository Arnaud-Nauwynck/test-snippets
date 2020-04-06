package testspringbootprofile;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import testspringbootprofile.config.AppPlaceholderService;
import testspringbootprofile.impl.MockedBean;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(
		properties= {"JUnit-AppTest"}
		)
public class AppTest extends AbstractTest {

	static {
		System.out.println("###### app.AppTest.cinit");
	}
	
	@Autowired
	private MockedBean mockedBean;
	
	@Autowired
	private AppPlaceholderService placeholders;

	@Test
	public void testDumpPlaceholders() {
		System.out.println("### app.testDumpPlaceholders");
		placeholders.dumpPlaceholders();
	}

	@Test
	public void testFoo() {
		System.out.println("### app.test1");
		mockedBean.foo();
	}
}
