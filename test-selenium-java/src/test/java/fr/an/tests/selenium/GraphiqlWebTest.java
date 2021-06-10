package fr.an.tests.selenium;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class GraphiqlWebTest {
	
	static {
		String propKey = "webdriver.chrome.driver";
		if (null == System.getProperty(propKey)) {
			System.out.println("prop " + propKey + " NOT set ... using default");
			System.setProperty(propKey, "C:/apps/tools/selenium/chromedriver_win32/chromedriver.exe");
		}
	}
	
	
	private WebDriver driver;
	private Map<String, Object> vars;
	JavascriptExecutor js;

	@Before
	public void setUp() {
		driver = new ChromeDriver();
		js = (JavascriptExecutor) driver;
		vars = new HashMap<String, Object>();
	}

	@After
	public void tearDown() {
		driver.quit();
	}

	@Test
	public void test1() {
		driver.get("http://localhost:8093/graphiql");
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		driver.manage().window().setSize(new Dimension(876, 859));
//		WebElement codeMirrorButton = driver.findElement(By.cssSelector(".CodeMirror-focused .CodeMirror-lines > div"));
//		codeMirrorButton.click();

		js.executeScript("window.scrollTo(0,0)");
		
		WebElement prettifyButton = driver.findElement(By.linkText("Prettify"));
		prettifyButton.click();
		
		WebElement executeButton = driver.findElement(By.cssSelector(".execute-button"));
		{
			WebElement element = executeButton;
			Actions builder = new Actions(driver);
			builder.moveToElement(element).perform();
		}
		{
			WebElement element = driver.findElement(By.tagName("body"));
			Actions builder = new Actions(driver);
			builder.moveToElement(element, 0, 0).perform();
		}
		executeButton.click();

//		driver.findElement(By.cssSelector(".result-window:nth-child(1) div:nth-child(7) > .CodeMirror-line:nth-child(2)")).click();
//		driver.findElement(By.cssSelector(".CodeMirror-wrap .CodeMirror-lines > div")).click();
//		js.executeScript("window.scrollTo(0,0)");
//		driver.findElement(By.cssSelector(".CodeMirror-wrap .CodeMirror-lines")).click();
//		driver.findElement(By.cssSelector("input")).click();
//		driver.findElement(By.cssSelector("input")).sendKeys("a");
//		driver.findElement(By.linkText("Srv3A")).click();
//		driver.findElement(By.linkText("Int")).click();
	}
}
