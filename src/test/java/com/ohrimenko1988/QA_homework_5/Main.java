package com.ohrimenko1988.QA_homework_5;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static org.openqa.selenium.remote.CapabilityType.PLATFORM;
import static org.openqa.selenium.support.ui.ExpectedConditions.*;

/**
 * Created by Igor on 22.12.2016.
 */
public class Main
{
    static EventFiringWebDriver driver;
    static DesiredCapabilities capabilities = new DesiredCapabilities();
    PageObjects page;
    JavascriptExecutor jse;
    WebDriverWait explicitWait;


    @BeforeClass
        @Parameters({"driverName" , "platformName" , "hostAndPort"})
    public void initialize (String driverName , String platformName , String hostAndPort)
    {
        log(">-->> start initialize elements");

        getDriver(driverName , platformName , hostAndPort);

        jse = ((JavascriptExecutor)driver);
        page = new PageObjects(driver);
        explicitWait = new WebDriverWait(driver,15);
        driver.register(new AutoLogListener());
        driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
        driver.manage().timeouts().pageLoadTimeout(15,TimeUnit.SECONDS);
        driver.manage().timeouts().setScriptTimeout(15,TimeUnit.SECONDS);
        Reporter.setEscapeHtml(false);
        log(">-->> initialize is completed");
    }

    @Test
    public  void goToPageAndCheckIt()
    {
        log(">-->> go to URL");
        driver.get("https://www.bing.com/");
        log(">-->> wait the logo visibility");
        explicitWait.until(visibilityOf(page.logoBing));
        log(">-->> wait the search field visibility");
        explicitWait.until(visibilityOf(page.searchField));
        log(">-->> wait the search button visibility");
        explicitWait.until(visibilityOf(page.searchButton));


//-----------------------  logo checkings  -----------------------------------

        log(">-->> check that logo is enabled");
        Assert.assertTrue(page.logoBing.isEnabled(),"logo is disabled");
        log(">-->> check that logo is clickable");
        Assert.assertTrue(elementToBeClickable(page.logoBing).
                toString().contains("element to be clickable:"),"logo is not clickable");

//-----------------------  search field checkings  --------------------------

        log(">-->> check that search field is enabled");
        Assert.assertTrue(page.searchField.isEnabled(),"element is disabled");
        log(">-->> check that search field is clickable");
        Assert.assertTrue(elementToBeClickable(page.searchField).toString()
                .contains("element to be clickable:"),"element does not clickable");
        page.searchField.clear();
        page.searchField.sendKeys("Automation");
        log(">-->> check input to search field");
        Assert.assertTrue(explicitWait.until(attributeToBe(page.searchField
                ,"value","Automation")),"can't input expression in the search field");
        page.searchField.clear();

//-----------------------  search button checkings  --------------------------


        log(">-->> chack that search button is enabled");
        Assert.assertTrue(page.searchField.isEnabled(),"element is disabled");
        log(">-->> check that search button is clickable");
        Assert.assertTrue(elementToBeClickable(page.searchButton).toString()
                .contains("element to be clickable:"),"element does not clickable");
        log("-----------------------------  test completed ------------------------------");
    }


    @Test(dependsOnMethods ="goToPageAndCheckIt"  ,dataProvider = "fromTextFileToDataProvider")
    public void searchCheckAndSave(String inputString , String fullString)
    {
        log("---------------------  search by '" + fullString + "' expression  -----------------");

        log(">-->> compare variants with key expression and take last part of element");
        int index = inputString.length();
        String lastPartExpression = fullString.substring(index);
        log(">-->> go to URL");
        driver.get("https://www.bing.com/");
        log(">-->> wait the logo visibility");
        explicitWait.until(visibilityOf(page.logoBing));
        log(">-->> wait the search field visibility");
        explicitWait.until(visibilityOf(page.searchField));
        log(">-->> wait the search button visibility");
        explicitWait.until(visibilityOf(page.searchButton));
        log(">-->> clear search field");
        page.searchField.clear();
        log(">-->> input part of the search expression");
        page.searchField.sendKeys(inputString);
        log(">-->> wait until part of the expression will be displayed in the search field");
        explicitWait.until(attributeToBe(page.searchField,"value",inputString));
        log(">-->> wait until variants of the full expression will be displayed");
        explicitWait.until(visibilityOf(page.fullExpression(driver,lastPartExpression,fullString)));
        log(">-->> click on it");
        WebElement fullText = page.fullExpression(driver,lastPartExpression,fullString);
        fullText.click();
        log(">-->> wait until result will be displayed");
        explicitWait.until(visibilityOf(page.titleRezultSerch));
        log(">-->> get URL from result title");
        String titleURL = page.titleRezultSerch.getAttribute("href");
        log(">-->> click on title");
        page.titleRezultSerch.click();
        log(">-->> check on match the title URL and the page URL");
        Assert.assertTrue(explicitWait.until(urlToBe(titleURL)),"URL not match");
        log("-----------------------------  test completed ------------------------------");

    }

    @DataProvider
    public Object[][] fromTextFileToDataProvider()throws FileNotFoundException, IOException
    {
        ArrayList<String> notFullSting = new ArrayList<String>();
        ArrayList<String> fullString = new ArrayList<String>();

        try
        {
            BufferedReader reader = new BufferedReader(new FileReader("input.txt"));
            String buferString = "";
            while (true)
            {
                buferString = reader.readLine();
                if(buferString == null)
                {
                    break;
                }
                String []splitedBuferString = buferString.split(" ");
                notFullSting.add(splitedBuferString[0]);
                fullString.add(splitedBuferString[1]);
            }
        }
        catch(FileNotFoundException ex)
        {
            log("text file not found");
        }
        catch(IOException ex)
        {
            log("some input / output error");
        }

        int arraySize = notFullSting.size();
        Object[][] rezult = new Object[arraySize][2];

        for(int i = 0; i < arraySize; i++)
        {
           rezult[i][0]= notFullSting.get(i);
           rezult[i][1]= fullString.get(i);

        }

        return rezult;
    }


    public void getDriver (String driverName , String platformName , String hostAndPort)
    {

        try
        {
            capabilities.setBrowserName(driverName);

            capabilities.setCapability(PLATFORM , platformName );
            URL url = new URL("http://"+hostAndPort+"/wd/hub");
            driver = new EventFiringWebDriver(new RemoteWebDriver(url, capabilities));
        }
        catch(Exception e)
        {
            e.printStackTrace();
            throw new SkipException("Unable to create RemoteWebdriver instance!");
        }



    }


    @AfterClass
    public void tearDown()
    {
        driver.quit();
    }

    public void log(String expression)
    {
        Reporter.log(expression+"<br>");
    }


}
