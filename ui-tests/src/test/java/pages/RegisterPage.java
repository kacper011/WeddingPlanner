package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class RegisterPage {
    private WebDriver driver;

    private By firstNameInput = By.id("firstName");
    private By emailInput = By.id("email");
    private By passwordInput = By.id("password");
    private By confirmPasswordInput = By.id("confirmPassword");
    private By registerButton = By.cssSelector("button[type='submit']");
    private By errorMsg = By.id("error");

    public RegisterPage(WebDriver driver) {
        this.driver = driver;
        driver.get("http://localhost:8080/register");
    }

    public void enterFirstName(String firstName) {
        driver.findElement(firstNameInput).sendKeys(firstName);
    }

    public void enterEmail(String email) {
        driver.findElement(emailInput).sendKeys(email);
    }

    public void enterPassword(String password) {
        driver.findElement(passwordInput).sendKeys(password);
    }

    public void enterConfirmPassword(String confirmPassword) {
        driver.findElement(confirmPasswordInput).sendKeys(confirmPassword);
    }


    public void clickRegister() {
        driver.findElement(registerButton).click();
    }

    public boolean isErrorVisible() {
        return driver.findElements(errorMsg).size() > 0 && driver.findElement(errorMsg).isDisplayed();
    }

    public String getErrorMessage() {
        return driver.findElements(errorMsg).size() > 0 ? driver.findElement(errorMsg).getText() : "";
    }

    public boolean areAllElementsVisible() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(firstNameInput));
            wait.until(ExpectedConditions.visibilityOfElementLocated(emailInput));
            wait.until(ExpectedConditions.visibilityOfElementLocated(passwordInput));
            wait.until(ExpectedConditions.visibilityOfElementLocated(confirmPasswordInput));
            wait.until(ExpectedConditions.visibilityOfElementLocated(registerButton));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
