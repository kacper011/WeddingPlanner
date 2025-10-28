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
}
