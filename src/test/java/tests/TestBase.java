package tests;

import com.microsoft.playwright.*;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Properties;

public class TestBase {

    Playwright playwright;
    Browser browser;
    Page page;
    String base_url;
    String booking_reference;
    BrowserContext context;

    @BeforeMethod(alwaysRun = true)
    public void setUp() throws IOException {

        Properties prop = new Properties();
        FileInputStream fis = new FileInputStream("src/test/resources/config.properties");
        prop.load(fis);
        //mvn test -PFramework -Dbrowser=chrome -Denv=dev
        String browserName = System.getProperty("browser") != null ? System.getProperty("browser") : prop.getProperty("browser");
        String envName = System.getProperty("env") != null ? System.getProperty("env") : prop.getProperty("env");
        // String browserName = prop.getProperty("browser");
        playwright = Playwright.create();

        if ("firefox".equals(browserName)) {
            browser = playwright.firefox().launch();
        } else if ("safari".equals(browserName)) {
            browser = playwright.webkit().launch();
        } else {
            browser=playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
            //browser = playwright.chromium().launch();
        }
        context = browser.newContext();
        context.tracing().start(new Tracing.StartOptions()
                .setScreenshots(true)
                .setSnapshots(true)
                .setSources(true));
        page = context.newPage();
        //page = browser.newPage(); //browser.newContext();
        page.setDefaultTimeout(5000);

        base_url = prop.getProperty(envName + ".base_url");

    }
    @AfterMethod()
    public void tearDown()
    {
        context.tracing().stop(new Tracing.StopOptions()
                .setPath(Paths.get("trace.zip")));
    }
}