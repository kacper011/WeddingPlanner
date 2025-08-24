package com.kacper.wedding_planner.e2e;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.security.core.parameters.P;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class RegistrationPageTest {

    private WebDriver driver;

    @BeforeEach
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.get("http://localhost:8080/register");
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void testRegistrationFormElementsAreVisible() {
        WebElement nameField = driver.findElement(By.id("firstName"));
        WebElement emailField = driver.findElement(By.id("email"));
        WebElement passwordField = driver.findElement(By.id("password"));
        WebElement registerButton = driver.findElement(By.cssSelector("button[type='submit']"));

        assertTrue(nameField.isDisplayed());
        assertTrue(emailField.isDisplayed());
        assertTrue(passwordField.isDisplayed());
        assertTrue(registerButton.isDisplayed());
    }

    @Test
    public void testSuccessfulRegistration() {

        driver.findElement(By.id("firstName")).sendKeys("Jan");
        driver.findElement(By.id("email")).sendKeys("jan@gmail.com"); //uniqe email
        driver.findElement(By.id("password")).sendKeys("Jan123");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        assertTrue(driver.getCurrentUrl().contains("/login?success"));
    }
}
