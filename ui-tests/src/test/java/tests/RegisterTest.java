package tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.LoginPage;
import pages.RegisterPage;
import utils.BaseTest;

import java.time.Duration;

public class RegisterTest extends BaseTest {

    @Test
    public void testAllRegisterElementsAreVisible() {
        RegisterPage registerPage = new RegisterPage(driver);
        Assertions.assertTrue(registerPage.areAllElementsVisible(),
                "Not all registration elements are visible!");
    }

    @Test
    public void testSuccessfulRegistration() {
        RegisterPage registerPage = new RegisterPage(driver);
        String email = "jan" + System.currentTimeMillis() + "@example.com";

        registerPage.enterFirstName("Jan");
        registerPage.enterEmail(email);
        registerPage.enterPassword("Test1234");
        registerPage.clickRegister();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.urlContains("/login?success"));

        LoginPage loginPage = new LoginPage(driver);
        loginPage.enterEmail(email);
        loginPage.enterPassword("Test1234");
        loginPage.clickLogin();

        wait.until(ExpectedConditions.urlToBe("http://localhost:8080/guests"));
        Assertions.assertTrue(driver.getCurrentUrl().contains("/guests"),
                "User was not redirected to the guests page after login!");
    }

    @Test
    public void testRegistrationWithExistingEmailShowsError() {
        RegisterPage registerPage = new RegisterPage(driver);

        registerPage.enterFirstName("Jan");
        registerPage.enterEmail("test@test.com");
        registerPage.enterPassword("Test1234");
        registerPage.clickRegister();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("error")));

        Assertions.assertTrue(registerPage.isErrorVisible(), "Error message is not visible");
        Assertions.assertTrue(registerPage.getErrorMessage().toLowerCase().contains("email"),
                "Unexpected error message text: " + registerPage.getErrorMessage());
    }

    @Test
    public void testInvalidEmailValidation() {
        RegisterPage registerPage = new RegisterPage(driver);

        registerPage.enterFirstName("Jan");
        registerPage.enterEmail("invallidEmailWithoutAt");
        registerPage.enterPassword("Test1234");
        registerPage.clickRegister();

        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        WebElement emailInput = driver.findElement(By.id("email"));
        String validationMessage = emailInput.getAttribute("validationMessage");

        Assertions.assertFalse(validationMessage.isEmpty(), "HTML5 validation did not trigger for invalid email format!");

        Assertions.assertTrue(validationMessage.toLowerCase().contains("email") ||
                validationMessage.toLowerCase().contains("@"),
                "Validation message does not mention email format! Got: " + validationMessage);

        Assertions.assertTrue(driver.getCurrentUrl().contains("/register") ,
                "The form was sent despite invalid email format!");
    }

    @Test
    public void testEmptyPasswordValidation() {
        RegisterPage registerPage = new RegisterPage(driver);

        registerPage.enterFirstName("Jan");
        registerPage.enterEmail("test" + System.currentTimeMillis() + "@example.com");
        registerPage.enterPassword("");
        registerPage.clickRegister();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace(); }

        WebElement passwordInput = driver.findElement(By.id("password"));
        String validationMessage = passwordInput.getAttribute("validationMessage");

        Assertions.assertFalse(validationMessage.isEmpty(),
                "HTML5 validation did not trigger for empty password!");

        Assertions.assertTrue(driver.getCurrentUrl().contains("/register"),
                "The form was sent despite an empty password field!");
    }
}
