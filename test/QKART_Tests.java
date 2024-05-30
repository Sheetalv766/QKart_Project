package QKART_TESTNG;

import QKART_TESTNG.pages.Checkout;
import QKART_TESTNG.pages.Home;
import QKART_TESTNG.pages.Login;
import QKART_TESTNG.pages.Register;
import QKART_TESTNG.pages.SearchResult;

import static org.testng.Assert.*;

import java.io.File;
import java.io.ObjectInputFilter.Status;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.hc.core5.http.message.StatusLine.StatusClass;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.*;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;


@Listeners(ListenerClass.class)
public class QKART_Tests extends ListenerClass {

    static RemoteWebDriver driver;
    public static String lastGeneratedUserName;

    @BeforeSuite(alwaysRun = true)
    public static void createDriver() throws MalformedURLException {
        // Launch Browser using Zalenium
        final DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setBrowserName(BrowserType.CHROME);
        driver = new RemoteWebDriver(new URL("http://localhost:8082/wd/hub"), capabilities);
        driver.manage().window().maximize();
        System.out.println("createDriver()");
    }

    /*
     * Testcase01: Verify a new user can successfully register
     */
    @Test(description = "Verify registration happens correctly", priority = 1, groups = {"Sanity"})
    @Parameters({"username", "password"})
    public void TestCase01(String username, String password) throws InterruptedException {

        Boolean status;
        // logStatus("Start TestCase", "Test Case 1: Verify User Registration", "DONE");

        // Visit the Registration page and register a new user
        Register registration = new Register(driver);
        registration.navigateToRegisterPage();
        status = registration.registerUser("testUser", "abc@123", true);
        assertTrue(status, "Failed to register new user");
        Thread.sleep(2000);

        // Save the last generated username
        lastGeneratedUserName = registration.lastGeneratedUsername;

        // System.out.println("Last Generated username : " + lastGeneratedUserName);
        // System.out.println("Login attempt with username : " + username + " and password : " +
        // password);

        // Visit the login page and login with the previuosly registered user
        Login login = new Login(driver);
        login.navigateToLoginPage();
        status = login.PerformLogin(lastGeneratedUserName, "abc@123");
        // logStatus("Test Step", "User Perform Login: ", status ? "PASS" : "FAIL");
        assertTrue(status, "Failed to login with registered user");
        Thread.sleep(2000);

        // Visit the home page and log out the logged in user
        Home home = new Home(driver);
        status = home.PerformLogout();

        // logStatus("End TestCase", "Test Case 1: Verify user Registration : ",
        // status ? "PASS" : "FAIL");
        // takeScreenshot(driver, "EndTestCase", "TestCase1");
    }

    @AfterSuite
    public static void quitDriver() {
        System.out.println("quit()");
        driver.quit();
    }

    /*
     * Verify that an existing user is not allowed to re-register on QKart
     */

    @Test(description = "Verify re-registering an already registered user fails", priority = 2,groups = {"Sanity"})
    @Parameters({"Tusername", "Tpassword"})
    public void TestCase02(String username, String password) throws InterruptedException {
        Boolean status;
        // logStatus("Start Testcase",
        // "Test Case 2: Verify User Registration with an existing username ", "DONE");

        // Visit the Registration page and register a new user
        Register registration = new Register(driver);
        registration.navigateToRegisterPage();
        status = registration.registerUser("testUser", "abc@123", true);
        Assert.assertTrue("Unable to register the user", status);
        Thread.sleep(2000);

        // logStatus("Test Step", "User Registration : ", status ? "PASS" : "FAIL");
        // if (!status) {
        // logStatus("End TestCase", "Test Case 2: Verify user Registration : ", status ? "PASS" :
        // "FAIL");
        // // return false;

        // }

        // Save the last generated username
        lastGeneratedUserName = registration.lastGeneratedUsername;

        // Visit the Registration page and try to register using the previously
        // registered user's credentials
        registration.navigateToRegisterPage();
        status = registration.registerUser(lastGeneratedUserName, "abc@123", false);

        // If status is true, then registration succeeded, else registration has
        // failed. In this case registration failure means Success
        // logStatus("End TestCase", "Test Case 2: Verify user Registration : ",
        // status ? "FAIL" : "PASS");
        // return !status;--> false , !---> true
        Assert.assertFalse("Verify user Registration", status);
        Thread.sleep(2000);
    }


    /*
     * Verify the functinality of the search text box
     */
    @Test(description = "Verify the functionality of search text box", priority = 3,
            groups = {"Sanity"})
    @Parameters({"product"})
    public void TestCase03(String product) throws InterruptedException {
        // logStatus("TestCase 3", "Start test case : Verify functionality of search box ", "DONE");

        // use soft assert
        SoftAssert softAssert = new SoftAssert();

        boolean status;

        // Visit the home page
        Home homePage = new Home(driver);
        homePage.navigateToHome();

        // Search for the "yonex" product
        status = homePage.searchForProduct("YONEX");
        softAssert.assertTrue(status, "Unable to search for given product");
        Thread.sleep(2000);
        // if (!status) {
        // logStatus("TestCase 3", "Test Case Failure. Unable to search for given product", "FAIL");
        // return false;
        // }

        // Fetch the search results
        List<WebElement> searchResults = homePage.getSearchResults();

        // Verify the search results are available
        // if (searchResults.size() == 0) {
        // logStatus("TestCase 3",
        // "Test Case Failure. There were no results for the given search string", "FAIL");
        // return false;
        // }
        softAssert.assertFalse(searchResults.isEmpty(), "No search result found");
        Thread.sleep(2000);

        for (WebElement webElement : searchResults) {
            // Create a SearchResult object from the parent element
            SearchResult resultelement = new SearchResult(webElement);

            // Verify that all results contain the searched text
            String elementText = resultelement.getTitleofResult();
            // if (!elementText.toUpperCase().contains("YONEX")) {
            // logStatus("TestCase 3",
            // "Test Case Failure. Test Results contains un-expected values: "
            // + elementText,
            // "FAIL");
            // return false;
            // }
            softAssert.assertTrue(elementText.toUpperCase().contains("YONEX"),
                    "Test result contains un-expected values : " + elementText);
            Thread.sleep(2000);
        }

        // logStatus("Step Success", "Successfully validated the search results ", "PASS");

        // Search for product
        status = homePage.searchForProduct("Gesundheit");
        // if (!status) {
        // logStatus("TestCase 3", "Test Case Failure. Invalid keyword returned results", "FAIL");
        // return false;
        // }
        softAssert.assertFalse(status, "Test Case Failure. Invalid keyword returned results");
        Thread.sleep(2000);
        // Verify no search results are found
        searchResults = homePage.getSearchResults();
        if (searchResults.size() == 0) {
            if (homePage.isNoResultFound()) {
                logStatus("Step Success",
                        "Successfully validated that no products found message is displayed",
                        "PASS");
            }
            // logStatus("TestCase 3",
            // "Test Case PASS. Verified that no search results were found for the given text",
            // "PASS");
        } else {
            // logStatus("TestCase 3",
            // "Test Case Fail. Expected: no results , actual: Results were available",
            // "FAIL");
            // return false;
            // }

            // return true;
            softAssert.fail("Expected: no results , actual: Results were available");
            Thread.sleep(2000);
        }
        softAssert.assertAll();
    }


    /*
     * Verify the presence of size chart and check if the size chart content is as expected
     */
    @Test(description = "Verify the existence of size chart for certain items and validate contents of size chart",
            priority = 4, groups = {"Regression"})
    @Parameters({"product1"})
    public void TestCase04(String product1) throws InterruptedException {
        // logStatus("TestCase 4", "Start test case : Verify the presence of size Chart", "DONE");
        boolean status = false;

        // Visit home page
        Home homePage = new Home(driver);
        homePage.navigateToHome();

        // Search for product and get card content element of search results
        status = homePage.searchForProduct("Running Shoes");
        List<WebElement> searchResults = homePage.getSearchResults();

        // Create expected values
        List<String> expectedTableHeaders = Arrays.asList("Size", "UK/INDIA", "EU", "HEEL TO TOE");
        List<List<String>> expectedTableBody = Arrays.asList(Arrays.asList("6", "6", "40", "9.8"),
                Arrays.asList("7", "7", "41", "10.2"), Arrays.asList("8", "8", "42", "10.6"),
                Arrays.asList("9", "9", "43", "11"), Arrays.asList("10", "10", "44", "11.5"),
                Arrays.asList("11", "11", "45", "12.2"), Arrays.asList("12", "12", "46", "12.6"));

        // Verify size chart presence and content matching for each search result
        for (WebElement webElement : searchResults) {
            SearchResult result = new SearchResult(webElement);

            // Verify if the size chart exists for the search result
            if (result.verifySizeChartExists()) {
                // logStatus("Step Success", "Successfully validated presence of Size Chart Link",
                // "PASS");

                // Verify if size dropdown exists
                status = result.verifyExistenceofSizeDropdown(driver);
                // logStatus("Step Success", "Validated presence of drop down", status ? "PASS" :
                // "FAIL");

                Assert.assertTrue("Validated presence of drop down", status);
                Thread.sleep(2000);

                // Open the size chart
                if (result.openSizechart()) {
                    // Verify if the size chart contents matches the expected values
                    if (result.validateSizeChartContents(expectedTableHeaders, expectedTableBody,
                            driver)) {
                        // logStatus("Step Success",
                        // "Successfully validated contents of Size Chart Link", "PASS");
                    } else {
                        // logStatus("Step Failure", "Failure while validating contents of Size
                        // Chart Link", "FAIL");
                        // status = false;
                        Assert.fail("Failure while validating contents of Size Chart Link");
                        Thread.sleep(2000);
                    }

                    // Close the size chart modal
                    status = result.closeSizeChart(driver);

                } else {
                    // logStatus("TestCase 4", "Test Case Fail. Failure to open Size Chart",
                    // "FAIL");
                    // return false;
                    Assert.fail("Failure to open Size Chart");
                    Thread.sleep(2000);

                }

            } else {
                // logStatus("TestCase 4", "Test Case Fail. Size Chart Link does not exist",
                // "FAIL");
                // return false;
                Assert.fail("Size Chart Link does not exist");
                Thread.sleep(2000);

            }
        }
        // logStatus("TestCase 4", "End Test Case: Validated Size Chart Details",
        // status ? "PASS" : "FAIL");
        // return status;
        Assert.assertTrue("Validated Size Chart Details", status);
        Thread.sleep(2000);
    }


    /*
     * Verify the complete flow of checking out and placing order for products is working correctly
     */
    @Test(description = "Verify that a new user can add multiple products in to the cart and Checkout",
            priority = 5, groups = {"Sanity"})
    @Parameters({"products1", "products2", "address"})
    public void TestCase05(String products1, String products2, String address)
            throws InterruptedException {
        Boolean status;
        // logStatus("Start TestCase", "Test Case 5: Verify Happy Flow of buying products", "DONE");

        // Go to the Register page
        Register registration = new Register(driver);
        registration.navigateToRegisterPage();

        // Register a new user
        status = registration.registerUser("testUser", "abc@123", true);
        // if (!status) {
        // logStatus("TestCase 5", "Test Case Failure. Happy Flow Test Failed", "FAIL");
        // }
        Assert.assertTrue("Registartion : Happy Flow Test Failed", status);
        Thread.sleep(2000);

        // Save the username of the newly registered user
        lastGeneratedUserName = registration.lastGeneratedUsername;

        // Go to the login page
        Login login = new Login(driver);
        login.navigateToLoginPage();

        // Login with the newly registered user's credentials
        status = login.PerformLogin(lastGeneratedUserName, "abc@123");
        // if (!status) {
        // logStatus("Step Failure", "User Perform Login Failed", status ? "PASS" : "FAIL");
        // logStatus("End TestCase", "Test Case 5: Happy Flow Test Failed : ", status ? "PASS" :
        // "FAIL");
        // }
        Assert.assertTrue("User Perform Login : Happy Flow Test Failed", status);
        Thread.sleep(2000);

        // Go to the home page
        Home homePage = new Home(driver);
        homePage.navigateToHome();

        // Find required products by searching and add them to the user's cart
        status = homePage.searchForProduct("YONEX");
        homePage.addProductToCart("YONEX Smash Badminton Racquet");
        status = homePage.searchForProduct("Tan");
        homePage.addProductToCart("Tan Leatherette Weekender Duffle");

        // Click on the checkout button
        homePage.clickCheckout();

        // Add a new address on the Checkout page and select it
        Checkout checkoutPage = new Checkout(driver);
        checkoutPage.addNewAddress("Addr line 1 addr Line 2 addr line 3");
        checkoutPage.selectAddress("Addr line 1 addr Line 2 addr line 3");

        // Place the order
        checkoutPage.placeOrder();

        WebDriverWait wait = new WebDriverWait(driver, 30);
        wait.until(ExpectedConditions.urlToBe("https://crio-qkart-frontend-qa.vercel.app/thanks"));

        // Check if placing order redirected to the Thansk page
        status = driver.getCurrentUrl().endsWith("/thanks");

        // Go to the home page
        homePage.navigateToHome();

        // Log out the user
        homePage.PerformLogout();

        // logStatus("End TestCase", "Test Case 5: Happy Flow Test Completed : ",
        // status ? "PASS" : "FAIL");
        // return status;
        Assert.assertTrue("Happy Flow Test Completed", status);
        Thread.sleep(2000);
    }


    /*
     * Verify the quantity of items in cart can be updated
     */
    @Test(description = "Verify that the contents of the cart can be edited", priority = 6,
            groups = {"Regression"})
    @Parameters({"productt1", "productt2"})
    public void TestCase06(String productt1, String productt2) throws InterruptedException {
        Boolean status;
        // logStatus("Start TestCase", "Test Case 6: Verify that cart can be edited", "DONE");
        Home homePage = new Home(driver);
        Register registration = new Register(driver);
        Login login = new Login(driver);

        registration.navigateToRegisterPage();
        status = registration.registerUser("testUser", "abc@123", true);
        // if (!status) {
        // logStatus("Step Failure", "User Perform Register Failed", status ? "PASS" : "FAIL");
        // logStatus("End TestCase", "Test Case 6: Verify that cart can be edited: ", status ?
        // "PASS" : "FAIL");
        // return false;
        // }
        Assert.assertTrue("User Perform Register Failed", status);
        Thread.sleep(2000);

        lastGeneratedUserName = registration.lastGeneratedUsername;

        login.navigateToLoginPage();
        status = login.PerformLogin(lastGeneratedUserName, "abc@123");
        // if (!status) {
        // logStatus("Step Failure", "User Perform Login Failed", status ? "PASS" : "FAIL");
        // logStatus("End TestCase", "Test Case 6: Verify that cart can be edited: ", status ?
        // "PASS" : "FAIL");
        // return false;
        // }
        Assert.assertTrue("User Perform Login Failed", status);
        Thread.sleep(2000);

        homePage.navigateToHome();
        status = homePage.searchForProduct("Xtend");
        homePage.addProductToCart("Xtend Smart Watch");

        status = homePage.searchForProduct("Yarine");
        homePage.addProductToCart("Yarine Floor Lamp");

        // update watch quantity to 2
        homePage.changeProductQuantityinCart("Xtend Smart Watch", 2);

        // update table lamp quantity to 0
        homePage.changeProductQuantityinCart("Yarine Floor Lamp", 0);

        // update watch quantity again to 1
        homePage.changeProductQuantityinCart("Xtend Smart Watch", 1);

        homePage.clickCheckout();

        Checkout checkoutPage = new Checkout(driver);
        checkoutPage.addNewAddress("Addr line 1 addr Line 2 addr line 3");
        checkoutPage.selectAddress("Addr line 1 addr Line 2 addr line 3");

        checkoutPage.placeOrder();

        try {
            WebDriverWait wait = new WebDriverWait(driver, 30);
            wait.until(
                    ExpectedConditions.urlToBe("https://crio-qkart-frontend-qa.vercel.app/thanks"));
        } catch (TimeoutException e) {
            System.out.println("Error while placing order in: " + e.getMessage());
            // return false;
            Assert.assertTrue("Error while placing order", status);
            Thread.sleep(2000);
        }

        status = driver.getCurrentUrl().endsWith("/thanks");

        homePage.navigateToHome();
        homePage.PerformLogout();

        // logStatus("End TestCase", "Test Case 6: Verify that cart can be edited: ", status ?
        // "PASS" : "FAIL");
        // return status;
        Assert.assertTrue("Verify that cart can be edited", status);
        Thread.sleep(2000);
    }


    @Test(description = "Verify that insufficient balance error is thrown when the wallet balance is not enough",
            priority = 7, groups = {"Sanity"})
    @Parameters({"productss", "QTY"})
    public void TestCase07(@Optional("Stylecon 9 Seater RHS Sofa Set ")String productss, String QTY) throws InterruptedException {
        Boolean status;
        // logStatus("Start TestCase",
        // "Test Case 7: Verify that insufficient balance error is thrown when the wallet balance is
        // not enough",
        // "DONE");

        Register registration = new Register(driver);
        registration.navigateToRegisterPage();

        status = registration.registerUser("testUser", "abc@123", true);
        // if (!status) {
        // logStatus("Step Failure", "User Perform Registration Failed", status ? "PASS" : "FAIL");
        // logStatus("End TestCase",
        // "Test Case 7: Verify that insufficient balance error is thrown when the wallet balance is
        // not enough: ",
        // status ? "PASS" : "FAIL");
        // return false;
        // }
        Assert.assertTrue(
                "Verify that insufficient balance error is thrown when the wallet balance is not enough",
                status);
        Thread.sleep(2000);

        lastGeneratedUserName = registration.lastGeneratedUsername;

        Login login = new Login(driver);
        login.navigateToLoginPage();
        status = login.PerformLogin(lastGeneratedUserName, "abc@123");
        // if (!status) {
        // logStatus("Step Failure", "User Perform Login Failed", status ? "PASS" : "FAIL");
        // logStatus("End TestCase",
        // "Test Case 7: Verify that insufficient balance error is thrown when the wallet balance is
        // not enough: ",
        // status ? "PASS" : "FAIL");
        // return false;
        // }
        Assert.assertTrue("Login Failed", status);
        Thread.sleep(2000);

        Home homePage = new Home(driver);
        homePage.navigateToHome();
        status = homePage.searchForProduct("Stylecon");
        homePage.addProductToCart("Stylecon 9 Seater RHS Sofa Set ");

        homePage.changeProductQuantityinCart("Stylecon 9 Seater RHS Sofa Set ", 10);

        homePage.clickCheckout();

        Checkout checkoutPage = new Checkout(driver);
        checkoutPage.addNewAddress("Addr line 1 addr Line 2 addr line 3");
        checkoutPage.selectAddress("Addr line 1 addr Line 2 addr line 3");

        checkoutPage.placeOrder();
        Thread.sleep(3000);

        status = checkoutPage.verifyInsufficientBalanceMessage();

        // logStatus("End TestCase",
        // "Test Case 7: Verify that insufficient balance error is thrown when the wallet balance is
        // not enough: ",
        // status ? "PASS" : "FAIL");

        // return status;
        Assert.assertTrue("VerifyingInsufficientBalanceMessage", status);
        Thread.sleep(2000);

    }

    // convert into testcase
    @Test(description = "Verify that a product added to a cart is available when a new tab is added",
            priority = 8, groups = {"Regression"})
    public void TestCase08() throws InterruptedException {
        Boolean status = false;

        // logStatus("Start TestCase",
        // "Test Case 8: Verify that product added to cart is available when a new tab is opened",
        // "DONE");
        // takeScreenshot(driver, "StartTestCase", "TestCase09");

        Register registration = new Register(driver);
        registration.navigateToRegisterPage();
        status = registration.registerUser("testUser", "abc@123", true);
        // if (!status) {
        // logStatus("TestCase 8",
        // "Test Case Failure. Verify that product added to cart is available when a new tab is
        // opened",
        // "FAIL");
        // // takeScreenshot(driver, "Failure", "TestCase09");
        // }
        Assert.assertTrue("User Registrataion Failed", status);
        Thread.sleep(2000);

        lastGeneratedUserName = registration.lastGeneratedUsername;

        Login login = new Login(driver);
        login.navigateToLoginPage();
        status = login.PerformLogin(lastGeneratedUserName, "abc@123");
        // if (!status) {
        // logStatus("Step Failure", "User Perform Login Failed", status ? "PASS" : "FAIL");
        // takeScreenshot(driver, "Failure", "TestCase9");
        // logStatus("End TestCase",
        // "Test Case 8: Verify that product added to cart is available when a new tab is opened",
        // status ? "PASS" : "FAIL");
        // }
        Assert.assertTrue("user Login Failed", status);
        Thread.sleep(2000);

        Home homePage = new Home(driver);
        homePage.navigateToHome();

        status = homePage.searchForProduct("YONEX");
        homePage.addProductToCart("YONEX Smash Badminton Racquet");

        String currentURL = driver.getCurrentUrl();

        driver.findElement(By.linkText("Privacy policy")).click();
        Set<String> handles = driver.getWindowHandles();
        driver.switchTo().window(handles.toArray(new String[handles.size()])[1]);

        driver.get(currentURL);
        Thread.sleep(2000);

        List<String> expectedResult = Arrays.asList("YONEX Smash Badminton Racquet");
        status = homePage.verifyCartContents(expectedResult);

        driver.close();

        driver.switchTo().window(handles.toArray(new String[handles.size()])[0]);

        // logStatus("End TestCase",
        // "Test Case 8: Verify that product added to cart is available when a new tab is opened",
        // status ? "PASS" : "FAIL");
        // takeScreenshot(driver, "EndTestCase", "TestCase08");

        // return status;
        Assert.assertTrue("product added to cart is available when a new tab is opened", status);
        Thread.sleep(2000);

    }


    @Test(description = "Verify that privacy policy and about us links are working fine",
            priority = 9, groups = {"Regression"})
    public void TestCase09() throws InterruptedException {
        Boolean status = false;

        // logStatus("Start TestCase",
        // "Test Case 09: Verify that the Privacy Policy, About Us are displayed correctly ",
        // "DONE");
        // takeScreenshot(driver, "StartTestCase", "TestCase09");

        SoftAssert softAssert = new SoftAssert();

        Register registration = new Register(driver);
        registration.navigateToRegisterPage();
        status = registration.registerUser("testUser", "abc@123", true);
        // if (!status) {
        // logStatus("TestCase 09",
        // "Test Case Failure. Verify that the Privacy Policy, About Us are displayed correctly ",
        // "FAIL");
        // takeScreenshot(driver, "Failure", "TestCase09");
        // }
        softAssert.assertTrue(status, "User Registration failed");
        Thread.sleep(2000);

        lastGeneratedUserName = registration.lastGeneratedUsername;

        Login login = new Login(driver);
        login.navigateToLoginPage();
        status = login.PerformLogin(lastGeneratedUserName, "abc@123");
        // if (!status) {
        // logStatus("Step Failure", "User Perform Login Failed", status ? "PASS" : "FAIL");
        // takeScreenshot(driver, "Failure", "TestCase09");
        // logStatus("End TestCase",
        // "Test Case 9: Verify that the Privacy Policy, About Us are displayed correctly ",
        // status ? "PASS" : "FAIL");
        // }

        softAssert.assertTrue(status, "Login failed");
        Thread.sleep(2000);

        Home homePage = new Home(driver);
        homePage.navigateToHome();

        String basePageURL = driver.getCurrentUrl();

        driver.findElement(By.linkText("Privacy policy")).click();
        status = driver.getCurrentUrl().equals(basePageURL);

        // if (!status) {
        // logStatus("Step Failure", "Verifying parent page url didn't change on privacy policy link
        // click failed", status ? "PASS" : "FAIL");
        // takeScreenshot(driver, "Failure", "TestCase09");
        // logStatus("End TestCase",
        // "Test Case 9: Verify that the Privacy Policy, About Us are displayed correctly ",
        // status ? "PASS" : "FAIL");
        // }

        softAssert.assertTrue(status,
                "Verifying parent page url didn't change on privacy policy link click");
        Thread.sleep(2000);

        Set<String> handles = driver.getWindowHandles();
        driver.switchTo().window(handles.toArray(new String[handles.size()])[1]);
        WebElement PrivacyPolicyHeading =
                driver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/h2"));
        status = PrivacyPolicyHeading.getText().equals("Privacy Policy");
        // if (!status) {
        // logStatus("Step Failure", "Verifying new tab opened has Privacy Policy page heading
        // failed", status ? "PASS" : "FAIL");
        // takeScreenshot(driver, "Failure", "TestCase9");
        // logStatus("End TestCase",
        // "Test Case 9: Verify that the Privacy Policy, About Us are displayed correctly ",
        // status ? "PASS" : "FAIL");
        // }

        softAssert.assertTrue(status, "new tab opened has Privacy Policy page heading");
        Thread.sleep(2000);

        driver.switchTo().window(handles.toArray(new String[handles.size()])[0]);
        driver.findElement(By.linkText("Terms of Service")).click();

        handles = driver.getWindowHandles();
        driver.switchTo().window(handles.toArray(new String[handles.size()])[2]);
        WebElement TOSHeading = driver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/h2"));
        status = TOSHeading.getText().equals("Terms of Service");
        // if (!status) {
        // logStatus("Step Failure", "Verifying new tab opened has Terms Of Service page heading
        // failed", status ? "PASS" : "FAIL");
        // takeScreenshot(driver, "Failure", "TestCase9");
        // logStatus("End TestCase",
        // "Test Case 9: Verify that the Privacy Policy, About Us are displayed correctly ",
        // status ? "PASS" : "FAIL");
        // }
        softAssert.assertTrue(status, " new tab opened has Terms Of Service page heading");
        Thread.sleep(2000);

        driver.close();
        driver.switchTo().window(handles.toArray(new String[handles.size()])[1]).close();
        driver.switchTo().window(handles.toArray(new String[handles.size()])[0]);

        // logStatus("End TestCase",
        // "Test Case 9: Verify that the Privacy Policy, About Us are displayed correctly ",
        // "PASS");
        // takeScreenshot(driver, "EndTestCase", "TestCase9");

        // return status;
        softAssert.assertAll();
    }


    @Test(description = "Verify that the contact us dialog works fine", priority = 10,
            groups = {"Regression"})
    @Parameters({"user", "gmail_id", "message"})
    public void TestCase10(String user, String gmail_id, String messages)
            throws InterruptedException {
        boolean status = true;
        // logStatus("Start TestCase",
        // "Test Case 10: Verify that contact us option is working correctly ",
        // "DONE");
        // takeScreenshot(driver, "StartTestCase", "TestCase10");
        Assert.assertTrue("Verify that contact us option is working correctly", status);
        Thread.sleep(2000);

        Home homePage = new Home(driver);
        homePage.navigateToHome();

        driver.findElement(By.xpath("//*[text()='Contact us']")).click();

        WebElement name = driver.findElement(By.xpath("//input[@placeholder='Name']"));
        name.sendKeys("crio user");
        WebElement email = driver.findElement(By.xpath("//input[@placeholder='Email']"));
        email.sendKeys("criouser@gmail.com");
        WebElement message = driver.findElement(By.xpath("//input[@placeholder='Message']"));
        message.sendKeys("Testing the contact us page");

        WebElement contactUs = driver.findElement(By.xpath(
                "/html/body/div[2]/div[3]/div/section/div/div/div/form/div/div/div[4]/div/button"));

        contactUs.click();

        WebDriverWait wait = new WebDriverWait(driver, 30);
        wait.until(ExpectedConditions.invisibilityOf(contactUs));

        // logStatus("End TestCase",
        // "Test Case 10: Verify that contact us option is working correctly ",
        // "PASS");

        // takeScreenshot(driver, "EndTestCase", "TestCase10");

        // return true;
        Assert.assertTrue("The contact us details should be updated successfully", status);
        Thread.sleep(2000);

    }

    @Test(description = "Ensure that the Advertisement Links on the QKART page are clickable",
            priority = 11, groups = "Sanity")
    @Parameters({"productY", "addresses"})
    public void TestCase11(String productY, String addresses) throws InterruptedException {
        Boolean status = false;
        // logStatus("Start TestCase",
        // "Test Case 11: Ensure that the links on the QKART advertisement are clickable",
        // "DONE");
        // takeScreenshot(driver, "StartTestCase", "TestCase11");

        SoftAssert softAssert = new SoftAssert();

        Register registration = new Register(driver);
        registration.navigateToRegisterPage();
        status = registration.registerUser("testUser", "abc@123", true);
        // if (!status) {
        // logStatus("TestCase 11",
        // "Test Case Failure. Ensure that the links on the QKART advertisement are clickable",
        // "FAIL");
        // takeScreenshot(driver, "Failure", "TestCase11");
        // }
        softAssert.assertTrue(status, "Verify user registration is successful");
        Thread.sleep(2000);

        lastGeneratedUserName = registration.lastGeneratedUsername;

        Login login = new Login(driver);
        login.navigateToLoginPage();
        status = login.PerformLogin(lastGeneratedUserName, "abc@123");
        // if (!status) {
        // logStatus("Step Failure", "User Perform Login Failed", status ? "PASS" : "FAIL");
        // takeScreenshot(driver, "Failure", "TestCase 11");
        softAssert.assertTrue(status, "Verify User Perform Login");
        Thread.sleep(2000);

        // logStatus("End TestCase",
        // "Test Case 11: Ensure that the links on the QKART advertisement are clickable",
        // status ? "PASS" : "FAIL");
        // }
        softAssert.assertTrue(status,
                "Ensure that the links on the QKART advertisement are clickable");
        Thread.sleep(2000);

        Home homePage = new Home(driver);
        homePage.navigateToHome();

        status = homePage.searchForProduct("YONEX Smash Badminton Racquet");
        homePage.addProductToCart("YONEX Smash Badminton Racquet");
        homePage.changeProductQuantityinCart("YONEX Smash Badminton Racquet", 1);
        homePage.clickCheckout();

        Checkout checkoutPage = new Checkout(driver);
        checkoutPage.addNewAddress("Addr line 1  addr Line 2  addr line 3");
        checkoutPage.selectAddress("Addr line 1  addr Line 2  addr line 3");
        checkoutPage.placeOrder();
        Thread.sleep(3000);

        String currentURL = driver.getCurrentUrl();

        List<WebElement> Advertisements = driver.findElements(By.xpath("//iframe"));

        // status = Advertisements.size() == 3;
        // logStatus("Step ", "Verify that 3 Advertisements are available", status ? "PASS" :
        // "FAIL");
        softAssert.assertTrue(Advertisements.size() == 3,
                "Verify that 3 Advertisements are available");
        Thread.sleep(2000);

        WebElement Advertisement1 =
                driver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/div/iframe[1]"));
        driver.switchTo().frame(Advertisement1);
        driver.findElement(By.xpath("//button[text()='Buy Now']")).click();
        driver.switchTo().parentFrame();

        // status = !driver.getCurrentUrl().equals(currentURL);
        // logStatus("Step ", "Verify that Advertisement 1 is clickable ", status ? "PASS" :
        // "FAIL");
        softAssert.assertTrue(!driver.getCurrentUrl().equals(currentURL),
                "Verify that Advertisement 1 is clickable");
        Thread.sleep(2000);

        driver.get(currentURL);
        Thread.sleep(3000);

        WebElement Advertisement2 =
                driver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/div/iframe[2]"));
        driver.switchTo().frame(Advertisement2);
        driver.findElement(By.xpath("//button[text()='Buy Now']")).click();
        driver.switchTo().parentFrame();

        // status = !driver.getCurrentUrl().equals(currentURL);
        // logStatus("Step ", "Verify that Advertisement 2 is clickable ", status ? "PASS" :
        // "FAIL");
        softAssert.assertTrue(!driver.getCurrentUrl().equals(currentURL),
                "Verify that Advertisement 2 is clickable");
        Thread.sleep(2000);


        // logStatus("End TestCase",
        // "Test Case 11: Ensure that the links on the QKART advertisement are clickable",
        // status ? "PASS" : "FAIL");
        // return status;
        softAssert.assertAll();
    }

    public static void logStatus(String type, String message, String status) {

        System.out.println(String.format("%s |  %s  |  %s | %s",
                String.valueOf(java.time.LocalDateTime.now()), type, message, status));
    }

    public static void takeScreenshot(WebDriver driver, String screenshotType, String description) {
        try {
            File theDir = new File("/screenshots");
            if (!theDir.exists()) {
                theDir.mkdirs();
            }
            String timestamp = String.valueOf(java.time.LocalDateTime.now());
            String fileName = String.format("screenshot_%s_%s_%s.png", timestamp, screenshotType,
                    description);
            TakesScreenshot scrShot = ((TakesScreenshot) driver);
            File SrcFile = scrShot.getScreenshotAs(OutputType.FILE);
            File DestFile = new File("screenshots/" + fileName);
            FileUtils.copyFile(SrcFile, DestFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
