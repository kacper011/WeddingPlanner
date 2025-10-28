package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class LoginPage {
    private WebDriver driver;

    public LoginPage(WebDriver driver) {
        this.driver = driver;
    }

    private By emailInput = By.id("email");
    private By passwordInput = By.id("password");
    private By loginButton = By.cssSelector("button[type='submit']");
    private By registerLink = By.cssSelector("a[href*='/register']");
    private By logoutButton = By.id("logoutButton");

    public WebElement getEmailInput() { return driver.findElement(emailInput); }
    public WebElement getPasswordInput() { return driver.findElement(passwordInput); }
    public WebElement getLoginButton() { return driver.findElement(loginButton); }
    public WebElement getRegisterLink() { return driver.findElement(registerLink); }
    public WebElement getLogoutButton() { return driver.findElement(logoutButton); }

    public boolean areAllElementsVisible() {
        return getEmailInput().isDisplayed() &&
                getPasswordInput().isDisplayed() &&
                getLoginButton().isDisplayed() &&
                getRegisterLink().isDisplayed();
    }

    public void enterEmail(String email) {
        getEmailInput().sendKeys(email);
    }

    public void enterPassword(String password) {
        getPasswordInput().sendKeys(password);
    }

    public void clickLogin() {
        getLoginButton().click();
    }

    public void clickLogout() {
        getLogoutButton().click();
    }
}
