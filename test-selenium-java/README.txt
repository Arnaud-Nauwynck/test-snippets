

https://www.selenium.dev/

selenium for Chrome: https://chrome.google.com/webstore/detail/selenium-ide/mooikfkahbdckldjjndioackbalphokd

DOC: https://www.selenium.dev/documentation/en/

Step 1/ install plugin extension in Chrome
Step 2/ record scenario using Chrome Selenium IDE 
Step 3/ export as java code
Step 4/ create java project, with maven dependency 
Step 5/ download chromedriver.exe
Step 6/ launch java test


exemple Java

	import org.openqa.selenium.By;
	import org.openqa.selenium.Keys;
	import org.openqa.selenium.WebDriver;
	import org.openqa.selenium.WebElement;
	import org.openqa.selenium.firefox.FirefoxDriver;
	import org.openqa.selenium.support.ui.WebDriverWait;
	import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;
	import java.time.Duration;

	public class HelloSelenium {

		public static void main(String[] args) {
			WebDriver driver = new FirefoxDriver();
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
			try {
				driver.get("https://google.com/ncr");
				driver.findElement(By.name("q")).sendKeys("cheese" + Keys.ENTER);
				WebElement firstResult = wait.until(presenceOfElementLocated(By.cssSelector("h3")));
				System.out.println(firstResult.getAttribute("textContent"));
			} finally {
				driver.quit();
			}
		}
	}

Maven dependency

    <dependency>
        <groupId>org.seleniumhq.selenium</groupId>
        <artifactId>selenium-java</artifactId>
        <version>4.0.0-beta-4</version>
    </dependency>
	
	<dependency>
	  <groupId>org.seleniumhq.selenium</groupId>
	  <artifactId>selenium-chrome-driver</artifactId>
	  <version>4.0.0-beta-4</version>
	</dependency>

	<dependency>
        <groupId>org.seleniumhq.selenium</groupId>
        <artifactId>selenium-firefox-driver</artifactId>
  	    <version>4.0.0-beta-4</version>
    </dependency>
	
	
	
First execution ... without -Dwebdriver.chrome.driver=...chromedriver.exe


java.lang.IllegalStateException: The path to the driver executable The path to the driver executable must be set by the webdriver.chrome.driver system property; for more information, see https://github.com/SeleniumHQ/selenium/wiki/ChromeDriver. The latest version can be downloaded from http://chromedriver.storage.googleapis.com/index.html
	at org.openqa.selenium.internal.Require$StateChecker.nonNull(Require.java:280)
	at org.openqa.selenium.remote.service.DriverService.findExecutable(DriverService.java:152)
	at org.openqa.selenium.chrome.ChromeDriverService.access$000(ChromeDriverService.java:37)
	at org.openqa.selenium.chrome.ChromeDriverService$Builder.findDefaultExecutable(ChromeDriverService.java:222)
	at org.openqa.selenium.remote.service.DriverService$Builder.build(DriverService.java:434)
	at org.openqa.selenium.chrome.ChromeDriverService.createDefaultService(ChromeDriverService.java:119)
	at org.openqa.selenium.chrome.ChromeDriver.<init>(ChromeDriver.java:41)
	at fr.an.tests.selenium.GraphiqlWebTest.setUp(GraphiqlWebTest.java:35)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:64)
	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.base/java.lang.reflect.Method.invoke(Method.java:564)
	at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:50)
	at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:12)
	at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:47)
	at org.junit.internal.runners.statements.RunBefores.evaluate(RunBefores.java:24)
	at org.junit.internal.runners.statements.RunAfters.evaluate(RunAfters.java:27)
	at org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:325)
	at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:78)
	at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:57)
	at org.junit.runners.ParentRunner$3.run(ParentRunner.java:290)
	at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:71)
	at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:288)
	at org.junit.runners.ParentRunner.access$000(ParentRunner.java:58)
	at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:268)
	at org.junit.runners.ParentRunner.run(ParentRunner.java:363)
	at org.eclipse.jdt.internal.junit4.runner.JUnit4TestReference.run(JUnit4TestReference.java:89)
	at org.eclipse.jdt.internal.junit.runner.TestExecution.run(TestExecution.java:40)
	at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.runTests(RemoteTestRunner.java:541)
	at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.runTests(RemoteTestRunner.java:768)
	at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.run(RemoteTestRunner.java:464)
	at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.main(RemoteTestRunner.java:210)
..
java.lang.NullPointerException: Cannot invoke "org.openqa.selenium.WebDriver.quit()" because "this.driver" is null
	at fr.an.tests.selenium.GraphiqlWebTest.tearDown(GraphiqlWebTest.java:42)



Second execution with not up-to-date Chrome application

org.openqa.selenium.SessionNotCreatedException: Could not start a new session. Response code 500. Message: session not created: 
This version of ChromeDriver only supports Chrome version 91
Current browser version is 90.0.4430.212 with binary path C:\Program Files\Google\Chrome\Application\chrome.exe
Build info: version: '4.0.0-beta-4', revision: '29f46d02dd'
System info: host: 'DESKTOP-2EGCC8R', ip: '192.168.0.50', os.name: 'Windows 10', os.arch: 'amd64', os.version: '10.0', java.version: '15.0.1'
Driver info: org.openqa.selenium.chrome.ChromeDriver
Command: [null, newSession {capabilities=[Capabilities {browserName: chrome, goog:chromeOptions: {args: [], extensions: []}}], desiredCapabilities=Capabilities {browserName: chrome, goog:chromeOptions: {args: [], extensions: []}}}]
	at org.openqa.selenium.remote.ProtocolHandshake.createSession(ProtocolHandshake.java:126)
	at org.openqa.selenium.remote.ProtocolHandshake.createSession(ProtocolHandshake.java:84)
	at org.openqa.selenium.remote.ProtocolHandshake.createSession(ProtocolHandshake.java:62)
	at org.openqa.selenium.remote.HttpCommandExecutor.execute(HttpCommandExecutor.java:156)
	at org.openqa.selenium.remote.service.DriverCommandExecutor.invokeExecute(DriverCommandExecutor.java:162)
	at org.openqa.selenium.remote.service.DriverCommandExecutor.execute(DriverCommandExecutor.java:137)
	at org.openqa.selenium.remote.RemoteWebDriver.execute(RemoteWebDriver.java:612)
	at org.openqa.selenium.remote.RemoteWebDriver.startSession(RemoteWebDriver.java:244)
	at org.openqa.selenium.remote.RemoteWebDriver.<init>(RemoteWebDriver.java:165)
	at org.openqa.selenium.chromium.ChromiumDriver.<init>(ChromiumDriver.java:89)
	at org.openqa.selenium.chrome.ChromeDriver.<init>(ChromeDriver.java:99)
	at org.openqa.selenium.chrome.ChromeDriver.<init>(ChromeDriver.java:86)
	at org.openqa.selenium.chrome.ChromeDriver.<init>(ChromeDriver.java:41)
	at fr.an.tests.selenium.GraphiqlWebTest.setUp(GraphiqlWebTest.java:35)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:64)
	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.base/java.lang.reflect.Method.invoke(Method.java:564)
	at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:50)
	at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:12)
	at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:47)
	at org.junit.internal.runners.statements.RunBefores.evaluate(RunBefores.java:24)
	at org.junit.internal.runners.statements.RunAfters.evaluate(RunAfters.java:27)
	at org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:325)
	at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:78)
	at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:57)
	at org.junit.runners.ParentRunner$3.run(ParentRunner.java:290)
	at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:71)
	at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:288)
	at org.junit.runners.ParentRunner.access$000(ParentRunner.java:58)
	at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:268)
	at org.junit.runners.ParentRunner.run(ParentRunner.java:363)
	at org.eclipse.jdt.internal.junit4.runner.JUnit4TestReference.run(JUnit4TestReference.java:89)
	at org.eclipse.jdt.internal.junit.runner.TestExecution.run(TestExecution.java:40)
	at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.runTests(RemoteTestRunner.java:541)
	at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.runTests(RemoteTestRunner.java:768)
	at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.run(RemoteTestRunner.java:464)
	at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.main(RemoteTestRunner.java:210)
..
java.lang.NullPointerException: Cannot invoke "org.openqa.selenium.WebDriver.quit()" because "this.driver" is null
	at fr.an.tests.selenium.GraphiqlWebTest.tearDown(GraphiqlWebTest.java:42)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)



Third execution: failed to find button (path was registered from Selenium IDE)
	

org.openqa.selenium.NoSuchElementException: no such element: Unable to locate element: {"method":"css selector","selector":".CodeMirror-focused .CodeMirror-lines > div"}
  (Session info: chrome=91.0.4472.77)
For documentation on this error, please visit: https://selenium.dev/exceptions/#no_such_element
Build info: version: '4.0.0-beta-4', revision: '29f46d02dd'
System info: host: 'DESKTOP-2EGCC8R', ip: '192.168.0.50', os.name: 'Windows 10', os.arch: 'amd64', os.version: '10.0', java.version: '15.0.1'
Driver info: org.openqa.selenium.chrome.ChromeDriver
Command: [6e072bfb2e2bea7bff3a96c826db725b, findElement {using=css selector, value=.CodeMirror-focused .CodeMirror-lines > div}]
Capabilities {acceptInsecureCerts: false, browserName: chrome, browserVersion: 91.0.4472.77, chrome: {chromedriverVersion: 91.0.4472.19 (1bf021f248676..., userDataDir: C:\Users\arnaud\AppData\Loc...}, goog:chromeOptions: {debuggerAddress: localhost:58299}, javascriptEnabled: true, networkConnectionEnabled: false, pageLoadStrategy: normal, platform: WINDOWS, platformName: WINDOWS, proxy: Proxy(), se:cdp: ws://localhost:58299/devtoo..., se:cdpVersion: 91.0.4472.77, setWindowRect: true, strictFileInteractability: false, timeouts: {implicit: 0, pageLoad: 300000, script: 30000}, unhandledPromptBehavior: dismiss and notify, webauthn:extension:largeBlob: true, webauthn:virtualAuthenticators: true}
Session ID: 6e072bfb2e2bea7bff3a96c826db725b
	at java.base/jdk.internal.reflect.NativeConstructorAccessorImpl.newInstance0(Native Method)
	at java.base/jdk.internal.reflect.NativeConstructorAccessorImpl.newInstance(NativeConstructorAccessorImpl.java:64)
	at java.base/jdk.internal.reflect.DelegatingConstructorAccessorImpl.newInstance(DelegatingConstructorAccessorImpl.java:45)
	at java.base/java.lang.reflect.Constructor.newInstanceWithCaller(Constructor.java:500)
	at java.base/java.lang.reflect.Constructor.newInstance(Constructor.java:481)
	at org.openqa.selenium.remote.codec.w3c.W3CHttpResponseCodec.createException(W3CHttpResponseCodec.java:200)
	at org.openqa.selenium.remote.codec.w3c.W3CHttpResponseCodec.decode(W3CHttpResponseCodec.java:133)
	at org.openqa.selenium.remote.codec.w3c.W3CHttpResponseCodec.decode(W3CHttpResponseCodec.java:53)
	at org.openqa.selenium.remote.HttpCommandExecutor.execute(HttpCommandExecutor.java:184)
	at org.openqa.selenium.remote.service.DriverCommandExecutor.invokeExecute(DriverCommandExecutor.java:162)
	at org.openqa.selenium.remote.service.DriverCommandExecutor.execute(DriverCommandExecutor.java:137)
	at org.openqa.selenium.remote.RemoteWebDriver.execute(RemoteWebDriver.java:612)
	at org.openqa.selenium.remote.RemoteWebDriver$Mechanism$2.findElement(RemoteWebDriver.java:1251)
	at org.openqa.selenium.remote.RemoteWebDriver.findElement(RemoteWebDriver.java:379)
	at org.openqa.selenium.remote.RemoteWebDriver.findElement(RemoteWebDriver.java:373)
	at fr.an.tests.selenium.GraphiqlWebTest.test1(GraphiqlWebTest.java:48)

	