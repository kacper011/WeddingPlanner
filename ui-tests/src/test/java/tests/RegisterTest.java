package tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pages.RegisterPage;
import utils.BaseTest;

public class RegisterTest extends BaseTest {

    @Test
    public void testAllRegisterElementsAreVisible() {
        RegisterPage registerPage = new RegisterPage(driver);
        Assertions.assertTrue(registerPage.areAllElementsVisible(),
                "Not all registration elements are visible!");
    }
}
