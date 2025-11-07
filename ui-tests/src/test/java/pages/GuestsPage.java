package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import javax.sound.sampled.Line;
import java.util.List;

public class GuestsPage {

    private WebDriver driver;

    private By navbar = By.cssSelector(".navbar");
    private By guestsTable = By.id("guestsTable");
    private By logoutButton = By.id("logoutButton");
    private By statsBoxes = By.xpath("//h5[contains(text(),'goście') or contains(text(),'Potwierdzeni') or contains(text(),'Niepotwierdzeni') or contains(text(),'Na poprawinach')]");
    private By addGuestButton = By.linkText("Dodaj gościa");

    public GuestsPage(WebDriver driver) {
        this.driver = driver;
    }

    public boolean isNavbarVisible() {
        return driver.findElement(navbar).isDisplayed();
    }

    public boolean isGuestsTableVisible() {
        return driver.findElement(guestsTable).isDisplayed();
    }

    public boolean isStatsVisible() {
        return driver.findElement(statsBoxes).isDisplayed();
    }

    public void clickLogout() {
        driver.findElement(logoutButton).click();
    }

    public void clickAddGuestButton() {
        driver.findElement(addGuestButton).click();
    }
    public boolean arePresenceButtonsClickable() {
        List<WebElement> yesButtons = driver.findElements(By.xpath("//button[contains(text(),'TAK')]"));
        List<WebElement> noButtons = driver.findElements(By.xpath("//button[contains(text(),'NIE')]"));
        return !yesButtons.isEmpty() && yesButtons.get(0).isEnabled() && !noButtons.isEmpty() && noButtons.get(0).isEnabled();
    }


}
