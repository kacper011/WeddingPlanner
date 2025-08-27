package com.kacper.wedding_planner.e2e;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GuestPageTest {

    private WebDriver driver;

    @BeforeEach
    void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();

        driver.get("http://localhost:8080/login");
        driver.findElement(By.id("email")).sendKeys("example.test@gmail.com");
        driver.findElement(By.id("password")).sendKeys("Test123");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        driver.get("http://localhost:8080/guests");
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void testGuestListIsVisible() {
        WebElement guestTable = driver.findElement(By.id("guestsTable"));
        assertTrue(guestTable.isDisplayed());
    }

    @Test
    void testGuestCountersVisible() {

        WebElement totalGuestsElement = driver.findElement(By.xpath("//h5[text()='Wszyscy goście']/following-sibling::h3"));
        int totalGuests = Integer.parseInt(totalGuestsElement.getText());

        WebElement confirmedGuestsElement = driver.findElement(By.xpath("//h5[text()='Potwierdzeni']/following-sibling::h3"));
        int confirmedGuests = Integer.parseInt(confirmedGuestsElement.getText());

        WebElement notConfirmedGuestsElement = driver.findElement(By.xpath("//h5[text()='Niepotwierdzeni']/following-sibling::h3"));
        int notConfirmedGuests = Integer.parseInt(notConfirmedGuestsElement.getText());

        WebElement receptionGuestsElement = driver.findElement(By.xpath("//h5[text()='Na poprawinach']/following-sibling::h3"));
        int receptionGuests = Integer.parseInt(receptionGuestsElement.getText());

        assertTrue(totalGuests >= 0);
        assertTrue(confirmedGuests >= 0);
        assertTrue(notConfirmedGuests >= 0);
        assertTrue(receptionGuests >= 0);

        assertEquals(totalGuests, confirmedGuests + notConfirmedGuests);
    }
}
