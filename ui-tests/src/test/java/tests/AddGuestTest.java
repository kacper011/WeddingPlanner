package tests;

import org.junit.jupiter.api.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.AddGuestPage;
import pages.GuestsPage;
import pages.LoginPage;
import utils.BaseTest;

import java.time.Duration;

public class AddGuestTest extends BaseTest {

    private GuestsPage guestsPage;
    private AddGuestPage addGuestPage;
    private WebDriverWait wait;

    @BeforeEach
    public void setup() {
        LoginPage loginPage = new LoginPage(driver);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        loginPage.enterEmail("test@gmail.com");
        loginPage.enterPassword("test1234");
        loginPage.clickLogin();

        wait.until(ExpectedConditions.urlContains("/guests"));
        guestsPage = new GuestsPage(driver);

        guestsPage.clickAddGuestButton();
        wait.until(ExpectedConditions.urlContains("/guests/new"));

        addGuestPage = new AddGuestPage(driver);
    }

    @Test
    public void testAddNewGuestSuccessfully() {
        addGuestPage.enterFirstName("Jan");
        addGuestPage.enterLastName("Kowalski" + System.currentTimeMillis());
        addGuestPage.selectCategory("FRIENDS");
        addGuestPage.enterContact("123 412 123");
        addGuestPage.enterAdditionalInfo("Testowy gość");
        addGuestPage.submitForm();

        wait.until(ExpectedConditions.urlContains("/guests"));
        Assertions.assertTrue(driver.getPageSource().contains("Jan"),
                "The newly added guest did not appear on the list!");
    }

    @Test
    public void testEmptyFirstNameValidation() {
        addGuestPage.enterFirstName("");
        addGuestPage.enterLastName("Kowalski");
        addGuestPage.selectCategory("FRIENDS");
        addGuestPage.enterContact("123 542 125");
        addGuestPage.submitForm();

        String validation = addGuestPage.getValidationMessage("firstName");
        Assertions.assertFalse(validation.isEmpty(), "No validation for empty first name!");
    }

    @Test
    public void testEmptyLastNameValidation() {
        addGuestPage.enterFirstName("Jan");
        addGuestPage.enterLastName("");
        addGuestPage.selectCategory("FRIENDS");
        addGuestPage.enterContact("123 542 125");
        addGuestPage.submitForm();

        String validation = addGuestPage.getValidationMessage("lastName");
        Assertions.assertFalse(validation.isEmpty(), "No validation for empty last name!");
    }

    @Test
    public void testEmptyCategoryValidation() {
        addGuestPage.enterFirstName("Jan");
        addGuestPage.enterLastName("Kowalski");
        addGuestPage.enterContact("123 123 123");
        addGuestPage.submitForm();

        String validation = addGuestPage.getValidationMessage("category");
        Assertions.assertFalse(validation.isEmpty(), "No validation for empty category!");
    }

    @Test
    public void testEmptyPhoneValidation() {
        addGuestPage.enterFirstName("Jan");
        addGuestPage.enterLastName("Kowalski");
        addGuestPage.selectCategory("FRIENDS");
        addGuestPage.enterContact("");
        addGuestPage.submitForm();

        String validation = addGuestPage.getValidationMessage("contact");
        Assertions.assertFalse(validation.isEmpty(), "No validation for empty phone!");
    }

    @Test
    public void testInvalidPhoneFormatValidation() {
        addGuestPage.enterFirstName("Jan");
        addGuestPage.enterLastName("Kowalski");
        addGuestPage.selectCategory("FRIENDS");
        addGuestPage.enterContact("abcd");
        addGuestPage.submitForm();

        String validation = addGuestPage.getValidationMessage("contact");
        Assertions.assertFalse(validation.isEmpty(), "No validation for invalid phone format!");
    }
}
