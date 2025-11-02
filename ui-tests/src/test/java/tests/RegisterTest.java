package tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
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
}
