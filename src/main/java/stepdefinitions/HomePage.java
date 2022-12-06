package stepdefinitions;

import io.cucumber.java.After;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class HomePage {

    private WebDriver driver;

    By searchBox = By.xpath("//input[@id='twotabsearchtextbox']");

    By firstProduct = By.xpath("//div[@data-component-type='s-search-result'][1]");

    By productPrice = By.xpath("//div[@id='corePrice_feature_div']");

    By addToCart = By.xpath("//input[@id='add-to-cart-button']");

    By goToCart = By.xpath("//span[@id='sw-gtc']//a");

    By cartTotalPrice = By.xpath("//span[@id='sc-subtotal-amount-activecart']");

    By cartTotalQty = By.xpath("//span[@id='sc-subtotal-label-activecart']");

    By productQuantity = By.xpath("//select[@id='quantity']//following::span[@class='a-button-text a-declarative'][1]");

    private By selectQuantity(int quantity) {
        return By.xpath(String.format("//li[@aria-labelledby='quantity_%d']", quantity));
    }

    private double totalPrice;

    private int totalQty;

    //@AfterStep
    public void addScreenshot(Scenario scenario) throws IOException {
        File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        byte[] fileContent = FileUtils.readFileToByteArray(src);
        scenario.attach(fileContent, "image/jpeg", "image");
    }

    @Given("the amazon site {string} for online shopping")
    public void launchPage(String url) {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.get(url);
        driver.manage().window().maximize();
    }

    @When("customer search for product {string} in home page")
    public void searchForProduct(String product) {
        driver.findElement(searchBox).sendKeys(product);
        driver.findElement(searchBox).sendKeys(Keys.ENTER);
    }

    @Then("user wants to add first product to cart with quantity as {int}")
    public void addProductToCart(Integer quantity) {
        driver.findElement(firstProduct).click();
        selectProductQuantity(quantity, false);
        totalPrice = Double.parseDouble(driver.findElement(productPrice).getText().replace("\n",".").replace("$", ""));
        driver.findElement(addToCart).click();
    }

    @Then("user navigates to Cart page")
    public void userClickGoToCartButton() {
        driver.findElement(goToCart).click();
    }

    @And("user check the total price and quantity of product in cart")
    public void checkTotalPriceAndQuantity() throws InterruptedException {
        Thread.sleep(2000);
        String actualCartPrice = driver.findElement(cartTotalPrice).getText().trim().replace("$", "");
        int actualCartQuantity = Integer.parseInt(driver.findElement(cartTotalQty).getText().trim().replaceAll("\\D+", ""));
        assertThat(actualCartQuantity)
                .withFailMessage("Qty in cart overview is incorrect")
                .isEqualTo(totalQty);
        assertThat(Double.parseDouble(actualCartPrice))
                .withFailMessage("Total price in cart overview is incorrect")
                .isEqualTo(expectedProductPrice());
    }

    @When("user reduces the quantity as {int}")
    public void userUpdateProductQuantity(Integer quantity) {
        selectProductQuantity(quantity, true);
    }

    private double expectedProductPrice() {
        return totalPrice * totalQty;
    }

    private void selectProductQuantity(Integer quantity, boolean shoppingCart) {
        int selectQty;
        totalQty = quantity;
        driver.findElement(productQuantity).click();
        if(!shoppingCart){
            selectQty =  quantity - 1;
        } else selectQty = quantity;
        driver.findElement(selectQuantity(selectQty)).click();
    }

    @After
    public void teardown() {
        driver.quit();
    }

}
