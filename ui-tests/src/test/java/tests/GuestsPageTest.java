package tests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
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

    @Test
    public void testConfirmedGuestCountIncreasesAfterClickYes() {

        WebElement confirmedCountElement = driver.findElement(By.xpath("//h5[contains(text(), 'Potwierdzeni')]/following-sibling::h3"));
        int initialCount = Integer.parseInt(confirmedCountElement.getText());

        WebElement firstYesButton = driver.findElement(By.xpath("//button[contains(text(),'TAK')]"));
        firstYesButton.click();

        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.xpath("//h5[contains(text(), 'Potwierdzeni')]/following-sibling::h3"),
                String.valueOf(initialCount + 1)));

        int updatedCount = Integer.parseInt(driver.findElement(By.xpath("//h5[contains(text(), 'Potwierdzeni')]/following-sibling::h3")).getText());

        Assertions.assertEquals(initialCount + 1, updatedCount, "Confirmed guests count did not increase after clicking YES!");
    }

    @Test
    public void testAddNewGuest() {
        guestsPage.clickAddGuestButton();

        wait.until(ExpectedConditions.urlContains("/guests/new"));

        driver.findElement(By.id("firstName")).sendKeys("Jan");
        driver.findElement(By.id("lastName")).sendKeys("Kowalski" + System.currentTimeMillis());

        Select categorySelect = new Select(driver.findElement(By.id("category")));
        categorySelect.selectByValue("FRIENDS");

        driver.findElement(By.id("contact")).sendKeys("123 412 123");
        driver.findElement(By.id("additionalInfo")).sendKeys("Testowy gość");

        driver.findElement(By.cssSelector("button[type='submit']")).click();

        wait.until(ExpectedConditions.urlContains("/guests"));
        Assertions.assertTrue(driver.getCurrentUrl().contains("/guests"),
                "Not redirected back to the guest list after adding!");

        String pageSource = driver.getPageSource();
        Assertions.assertTrue(pageSource.contains("Jan"),
                "The newly added guest did not appear on the list!");
    }

    @Test
    public void testEmptyFirstNameValidation() throws  InterruptedException {

        guestsPage.clickAddGuestButton();
        wait.until(ExpectedConditions.urlContains("/guests/new"));

        driver.findElement(By.id("firstName")).clear();
        driver.findElement(By.id("lastName")).sendKeys("Kowalski");
        new Select(driver.findElement(By.id("category"))).selectByValue("FRIENDS");
        driver.findElement(By.id("contact")).sendKeys("123 542 125");
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        Thread.sleep(1000);

        WebElement firstNameInput = driver.findElement(By.id("firstName"));
        String validationMessage = firstNameInput.getAttribute("validationMessage");

        Assertions.assertFalse(validationMessage.isEmpty(), "No validation for empty name!");
        Assertions.assertTrue(driver.getCurrentUrl().contains("/guests/new"), "The form was sent despite the name field being empty!");
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
