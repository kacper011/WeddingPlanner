package tests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.GuestsPage;
import pages.LoginPage;
import utils.BaseTest;

import java.time.Duration;

public class GuestsPageTest extends BaseTest {

    private GuestsPage guestsPage;
    private WebDriverWait wait;

    @BeforeEach
    public void loginBeforeEachTest() {
        LoginPage loginPage = new LoginPage(driver);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        loginPage.enterEmail("test@gmail.com");
        loginPage.enterPassword("test1234");
        loginPage.clickLogin();

        wait.until(ExpectedConditions.urlContains("/guests"));
        guestsPage = new GuestsPage(driver);

        Assertions.assertTrue(driver.getCurrentUrl().contains("/guests"),
                "Login failed — user was not redirected to guests page!");
    }

    @Test
    public void testGuestsPageElementsVisible() {

        Assertions.assertTrue(guestsPage.isNavbarVisible(), "Navbar is not visible!");
        Assertions.assertTrue(guestsPage.isGuestsTableVisible(), "Guest table is not visible!");
        Assertions.assertTrue(guestsPage.isStatsVisible(), "Stats section not visible!");
    }

    @Test
    public void testLogoutButtonWorks() {

        guestsPage.clickLogout();

        wait.until(ExpectedConditions.urlContains("/login"));

        Assertions.assertTrue(driver.getCurrentUrl().contains("/login"), "Logout did not redirect to login page!");
    }

    @Test
    public void testPresenceButtonsClickable() {

        Assertions.assertTrue(guestsPage.arePresenceButtonsClickable(), "Presence buttons are not clickable!");
    }
    

    @AfterEach
    public void cleanup() {
        try {
            if (!driver.getCurrentUrl().contains("/login")) {
                guestsPage.clickLogout();
                wait.until(ExpectedConditions.urlContains("/login"));
            }
        } catch (Exception e) {
            System.out.println("⚠️ Logout not performed — maybe already logged out.");
        }
    }
}
