package tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pages.LoginPage;
import utils.BaseTest;

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

        String expectedUrl = "http://localhost:8080/guests";
        String actualUrl = driver.getCurrentUrl();
        Assertions.assertEquals(expectedUrl, actualUrl, "After logging in, I was not redirected to /guests.");

    }
}
