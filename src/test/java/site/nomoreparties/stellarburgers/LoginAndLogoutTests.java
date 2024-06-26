package site.nomoreparties.stellarburgers;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import site.nomoreparties.stellarburgers.api.client.UserClient;
import site.nomoreparties.stellarburgers.api.generationtestdata.UserGenerator;
import site.nomoreparties.stellarburgers.api.pojo.User;
import site.nomoreparties.stellarburgers.drivers.WebDriverConstructor;
import site.nomoreparties.stellarburgers.pages.HomePage;

import static org.junit.Assert.assertTrue;

@DisplayName("Вход и выход из аккаунта")
public class LoginAndLogoutTests {
    private WebDriver driver;
    private User user;
    private final UserClient userClient = new UserClient();
    private ValidatableResponse createResponse;
    private HomePage homePage;
    boolean checkOutOrderButtonIsDisplayed;

    @Before
    public void setUp() {
        WebDriverConstructor webDriverConstructor = new WebDriverConstructor();
        driver = webDriverConstructor.getWebDriver();
        user = UserGenerator.randomUser();
        createResponse = userClient.createUser(user);
        homePage = new HomePage(driver)
                .open()
                .waitLoadHomePage();
    }

    @After
    public void tearDown() {
        driver.quit();
        String accessToken = userClient.getAccessToken(createResponse);
        userClient.deleteUser(accessToken);
    }

    @Test
    @DisplayName("Вход по кнопке <<Войти в аккаунт>> на главной")
    public void loginUsingTheLoginButtonTest() {
        checkOutOrderButtonIsDisplayed = homePage
                .clickLoginButton()
                .waitLoadPageAuthorizationForm()
                .fillInTheEmailField(user.getEmail())
                .fillInThePasswordField(user.getPassword())
                .clickEnterButton()
                .waitLoadHomePage()
                .checkOutOrderButtonIsDisplayed();
        assertTrue(checkOutOrderButtonIsDisplayed); // Проверили, что на главной странице, после регистрации отображается кнопка <<Оформить заказ>>
    }

    @Test
    @DisplayName("Вход через кнопку <Личный кабинет>")
    public void loginUsingThePersonalAccountButtonTest() {
        checkOutOrderButtonIsDisplayed = homePage
                .clickPersonalAccountButtonWithoutAuthorization()
                .waitLoadPageAuthorizationForm()
                .fillInTheEmailField(user.getEmail())
                .fillInThePasswordField(user.getPassword())
                .clickEnterButton()
                .waitLoadHomePage()
                .checkOutOrderButtonIsDisplayed();
        assertTrue(checkOutOrderButtonIsDisplayed); // Проверили, что на главной странице, после регистрации отображается кнопка <<Оформить заказ>>
    }

    @Test
    @DisplayName("Вход через кнопку в форме регистрации")
    public void loginUsingTheButtonInTheRegistrationFormTest() {
        checkOutOrderButtonIsDisplayed = homePage
                .clickPersonalAccountButtonWithoutAuthorization()
                .clickRegisterLink()
                .waitLoadRegistrationPage()
                .clickLoginLink()
                .fillInTheEmailField(user.getEmail())
                .fillInThePasswordField(user.getPassword())
                .clickEnterButton()
                .waitLoadHomePage()
                .checkOutOrderButtonIsDisplayed();
        assertTrue(checkOutOrderButtonIsDisplayed); // Проверили, что на главной странице, после регистрации отображается кнопка <<Оформить заказ>>
    }

    @Test
    @DisplayName("Вход через кнопку в форме восстановления пароля")
    public void loginUsingTheButtonThePasswordRecoveryFormTest() {
        checkOutOrderButtonIsDisplayed = homePage
                .clickPersonalAccountButtonWithoutAuthorization()
                .clickPasswordRecoveryLink()
                .clickLoginLink()
                .fillInTheEmailField(user.getEmail())
                .fillInThePasswordField(user.getPassword())
                .clickEnterButton()
                .waitLoadHomePage()
                .checkOutOrderButtonIsDisplayed();
        assertTrue(checkOutOrderButtonIsDisplayed); // Проверили, что на главной странице, после регистрации отображается кнопка <<Оформить заказ>>
    }

    @Test
    @DisplayName("Выход по кнопке <<Выйти>> в личном кабинете")
    public void logoutClickExitButtonInPersonalAccountTest() {
        boolean titleLoginIsDisplayed = homePage
                .clickPersonalAccountButtonWithoutAuthorization()
                .waitLoadPageAuthorizationForm()
                .fillInTheEmailField(user.getEmail())
                .fillInThePasswordField(user.getPassword())
                .clickEnterButton()
                .waitLoadHomePage()
                .clickPersonalAccountButtonWithAuthorization()
                .waitLoadPagePersonalAccount()
                .clickExitButton()
                .waitLoadPageAuthorizationForm()
                .titleLoginIsDisplayed();
        assertTrue(titleLoginIsDisplayed); // Проверили, что вернулись на страницу авторизации после выхода из аккаунта
    }


}

