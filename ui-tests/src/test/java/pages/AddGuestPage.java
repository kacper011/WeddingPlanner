package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

public class AddGuestPage {

    private WebDriver driver;

    public AddGuestPage(WebDriver driver) {
        this.driver = driver;
    }

    public void enterFirstName(String firstName) {
        driver.findElement(By.id("firstName")).clear();
        driver.findElement(By.id("firstName")).sendKeys(firstName);
    }

    public void enterLastName(String lastName) {
        driver.findElement(By.id("lastName")).clear();
        driver.findElement(By.id("lastName")).sendKeys(lastName);
    }

    public void selectCategory(String categoryValue) {
        Select select = new Select(driver.findElement(By.id("category")));
        select.selectByValue(categoryValue);
    }

    public void enterContact(String contact) {
        driver.findElement(By.id("contact")).clear();
        driver.findElement(By.id("contact")).sendKeys(contact);
    }

    public void enterAdditionalInfo(String info) {
        driver.findElement(By.id("additionalInfo")).clear();
        driver.findElement(By.id("additionalInfo")).sendKeys(info);
    }

    public void submitForm() {
        driver.findElement(By.cssSelector("button[type='submit']")).click();
    }

    public String getValidationMessageForField(String fieldId) {
        WebElement input = driver.findElement(By.id(fieldId));
        return input.getAttribute("validationMessage");
    }
}