package tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.GuestsPage;
import pages.LoginPage;
import utils.BaseTest;

import java.time.Duration;

public class GuestsPageTest extends BaseTest {

    @Test
    public void testGuestsPageElementsVisible() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.enterEmail("test@gmail.com");
        loginPage.enterPassword("test1234");
        loginPage.clickLogin();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.urlContains("/guests"));

        GuestsPage guestsPage = new GuestsPage(driver);

        Assertions.assertTrue(guestsPage.isNavbarVisible(), "Navbar is not visible!");
        Assertions.assertTrue(guestsPage.isGuestsTableVisible(), "Guest table is not visible!");
        Assertions.assertTrue(guestsPage.isStatsVisible(), "Stats section not visible!");
    }

    @Test
    public void testLogoutButtonWorks() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.enterEmail("test@gmail.com");
        loginPage.enterPassword("test1234");
        loginPage.clickLogin();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.urlContains("/guests"));

        GuestsPage guestsPage = new GuestsPage(driver);
        guestsPage.clickLogout();

        wait.until(ExpectedConditions.urlContains("/login"));

        Assertions.assertTrue(driver.getCurrentUrl().contains("/login"), "Logout did not redirect to login page!");
    }

    @Test
    public void testPresenceButtonsClickable() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.enterEmail("test@gmail.com");
        loginPage.enterPassword("test1234");
        loginPage.clickLogin();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.urlContains("/guests"));

        GuestsPage guestsPage = new GuestsPage(driver);

        Assertions.assertTrue(guestsPage.arePresenceButtonsClickable(), "Presence buttons are not clickable!");
    }
}
