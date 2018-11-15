package fr.an.testsprintboottest;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fr.an.testsprintboottest.config.Ctx2AppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(
		webEnvironment= WebEnvironment.NONE,
		properties= {"testmode=true"}
		, classes= {Ctx2AppConfiguration.class}
		)
@ActiveProfiles({"profile2"})
public abstract class AbstractCtx2Test {

}
