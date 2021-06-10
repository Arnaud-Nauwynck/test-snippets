package fr.an.tests.selenium;


import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;

import java.io.File;
import java.time.Duration;
import java.util.List;

public class SeleniumApp {
	static {
		String propKey = "webdriver.chrome.driver";
		if (null == System.getProperty(propKey)) {
			System.out.println("prop " + propKey + " NOT set ... using default");
			System.setProperty(propKey, "C:/apps/tools/selenium/chromedriver_win32/chromedriver.exe");
		}
	}

	WebDriver driver;
	WebDriverWait wait;
	
	public static void main(String[] args) {
		SeleniumApp app = new SeleniumApp();
		app.doMain();
	}

	private void doMain() {
		this.driver = // new FirefoxDriver();
				new ChromeDriver();
		this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		try {
			// testGoogle();
			testWwwSncfFr();
		} finally {
			driver.quit();
		}
	}

	private void testWwwSncfFr() {
		driver.get("https://www.sncf.com/fr");
		// wait.until(() -> )
		sleep(5000);
		List<WebElement> acceptCookieButtonLs = driver.findElements(By.id("CybotCookiebotDialogBodyLevelButtonLevelOptinAllowAll"));
		if (!acceptCookieButtonLs.isEmpty()) {
			WebElement acceptCookieButton = acceptCookieButtonLs.get(0);
			acceptCookieButton.click();
			sleep(2000);
		}
		testSncfPage();
		
	}

	private void testSncfPage() {
		WebElement departurePlace = driver.findElement(By.id("departure-place"));
		departurePlace.sendKeys("Paris (Toutes gares)");
		sleep(1000);
		departurePlace.sendKeys("\t"); // auto complete first choice
		sleep(1000);
		
		WebElement arrivalPlace = driver.findElement(By.id("arrival-place"));
		arrivalPlace.sendKeys("Lyon (Toutes gares)");
		sleep(1000);
		arrivalPlace.sendKeys("\t"); // auto complete first choice
		sleep(1000);
				
		WebElement searchButton = driver.findElement(By.className("miv-tab-btn-search"));
		searchButton.click();
		sleep(1000);
		
//		WebElement firstResult = driver.findElement(By.cssSelector(
//				"#app > div > div.container.container-no-padding-tiny > section > div:nth-child(3) > ul > li > a > h3"));
//		firstResult.click();
//		sleep(1000);
		
		WebElement mainContentElt = driver.findElement(By.id("main-content"));
		File screenshotAs = mainContentElt.getScreenshotAs(OutputType.FILE);
		System.out.println("saved screenshot to File: " + screenshotAs);
	}

	private void testGoogle() {
		driver.get("https://google.com/ncr");
		driver.findElement(By.name("q")).sendKeys("cheese" + Keys.ENTER);
		WebElement firstResult = wait.until(presenceOfElementLocated(By.cssSelector("h3")));
		String firstAttrContent = firstResult.getAttribute("textContent");
		System.out.println(firstAttrContent);
	}

	private void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
		}
	}
}

