package e2e;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class LoginPageTest {

    private WebDriver driver;

    @BeforeEach
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.get("http://localhost:8080/login");
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void testLoginPageElementsAreVisible() {
        WebElement emailField = driver.findElement(By.id("email"));
        WebElement passwordField = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.cssSelector("button[type='submit']"));



        assertTrue(emailField.isDisplayed(), "Email field must be visible");
        assertTrue(passwordField.isDisplayed(), "Password field must be visible");
        assertTrue(loginButton.isDisplayed(), "Login button must be visible");
    }

    @Test
    public void testSuccessfulLogin() {
        driver.findElement(By.id("email")).sendKeys("kacper-szabat@wp.pl");
        driver.findElement(By.id("password")).sendKeys("Kacper11");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        assertTrue(driver.getCurrentUrl().contains("/guests"), "It should redirect to the home page after logging in");
    }
}
