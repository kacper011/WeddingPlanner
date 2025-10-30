package tests;

import org.apache.commons.logging.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.LoginPage;
import utils.BaseTest;

import java.time.Duration;

public class LoginTest extends BaseTest {

    @Test
    public void testAllKeyElementsAreVisible() {
        LoginPage loginPage = new LoginPage(driver);

        Assertions.assertTrue(loginPage.areAllElementsVisible(), "Not all login elements are visible!");
    }

    @Test
    public void testSuccessfulLogin() {
        LoginPage loginPage = new LoginPage(driver);

        loginPage.enterEmail("test@test.com");
        loginPage.enterPassword("test123");
        loginPage.clickLogin();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.urlToBe("http://localhost:8080/guests"));

        By header = By.cssSelector("h1.text-center.mb-4");
        wait.until(ExpectedConditions.visibilityOfElementLocated(header));
        Assertions.assertTrue(driver.findElement(header).isDisplayed(), "The “Wedding Guest List” heading is not visible after logging in.");

        loginPage.clickLogout();

        wait.until(ExpectedConditions.urlToBe("http://localhost:8080/login?logout"));
        Assertions.assertEquals("http://localhost:8080/login?logout", driver.getCurrentUrl(), "You were not redirected to the login page after logging out.");
    }

    @Test
    public void testFailedLoginShowsErrorMessage() {
        LoginPage loginPage = new LoginPage(driver);

        loginPage.enterEmail("incorrect@test.com");
        loginPage.enterPassword("incorrect");
        loginPage.clickLogin();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.urlContains("error"));

        String currentUrl = driver.getCurrentUrl();
        Assertions.assertTrue(currentUrl.contains("/login?error"),
                "The user did not remain on the login page after an incorrect login.");

        By errorMsg = By.cssSelector(".alert.alert-danger");
        wait.until(ExpectedConditions.visibilityOfElementLocated(errorMsg));
        Assertions.assertTrue(driver.findElement(errorMsg).isDisplayed(),
                "The error message is not visible after an incorrect login.");

    }

    @Test
    public void testEmailValidationError() {
        LoginPage loginPage = new LoginPage(driver);

        loginPage.enterEmail("testtest.com");
        loginPage.enterPassword("test123");
        loginPage.clickLogin();

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        WebElement emailInput = driver.findElement(By.id("email"));

        String validationMessage = emailInput.getAttribute("validationMessage");

        Assertions.assertFalse(validationMessage.isEmpty(),
                "HTML5 validation did not trigger for incorrect email!");

        Assertions.assertTrue(driver.getCurrentUrl().contains("/login"),
                "The form was sent despite an incorrect email address!");
    }

    @Test
    public void testEmptyPasswordValidation() {
        LoginPage loginPage = new LoginPage(driver);

        loginPage.enterEmail("test@test.com");
        loginPage.enterPassword("");
        loginPage.clickLogin();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        WebElement passwordInput = driver.findElement(By.id("password"));

        String validationMessage = passwordInput.getAttribute("validationMessage");

        Assertions.assertFalse(validationMessage.isEmpty(),
                "HTML5 validation did not trigger for empty password!");

        System.out.println("Password validation message: " + validationMessage);

        Assertions.assertTrue(driver.getCurrentUrl().contains("/login"),
                "The form was sent despite an empty password field!");
    }
}
