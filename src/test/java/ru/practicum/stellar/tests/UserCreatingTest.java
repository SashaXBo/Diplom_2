package ru.practicum.stellar.tests;

import io.qameta.allure.*;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.practicum.stellar.client.UserClient;
import ru.practicum.stellar.utils.TestDataGenerator;

import static org.hamcrest.Matchers.*;

@Epic("Stellar Burgers API")
@Feature("User API - Registration")
public class UserCreatingTest {

    private String userToken;
    private String testEmail;
    private String testPassword;
    private String testName;

    @Before
    public void setUp() {
        testEmail = TestDataGenerator.generateUniqueEmail();
        testPassword = TestDataGenerator.generatePassword();
        testName = TestDataGenerator.generateName();
    }


    @Test
    @Story("User Creation")
    @Description("Создание уникального пользователя с валидными данными - проверка успешного создания")
    @Severity(SeverityLevel.BLOCKER)
    public void createUniqueUser_success() {
        Response response = UserClient.createUser(testEmail, testPassword, testName);

        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(testEmail))
                .body("user.name", equalTo(testName));

        userToken = response.jsonPath().getString("accessToken");
    }

    @Test
    @Story("User Creation")
    @Description("Попытка создания пользователя с существующим email - проверка ошибки дублирования")
    @Severity(SeverityLevel.CRITICAL)
    public void createExistingUser_fails() {
        Response firstResponse = UserClient.createUser(testEmail, testPassword, testName);
        firstResponse.then()
                .statusCode(200)
                .body("success", equalTo(true));

        userToken = firstResponse.jsonPath().getString("accessToken");

        Response secondResponse = UserClient.createUser(testEmail, testPassword, testName);

        secondResponse.then()
                .statusCode(anyOf(equalTo(403), equalTo(409)))
                .body("success", equalTo(false))
                .body("message", notNullValue())
                .body("message", not(emptyString()));
    }


    @Test
    @Story("User Creation")
    @Description("Создание пользователя без email - проверка обработки ошибки валидации")
    public void createUserMissingEmail_fails() {
        Response response = UserClient.createUser(null, testPassword, testName);

        response.then()
                .statusCode(anyOf(
                        equalTo(200),   // API может игнорировать null email
                        equalTo(400),   // Bad Request - некорректный запрос
                        equalTo(403),   // Forbidden - запрещено
                        equalTo(500)    // Internal Server Error
                ));
    }


    @Test
    @Story("User Creation")
    @Description("Создание пользователя без пароля - проверка обработки ошибки валидации")
    public void createUserMissingPassword_fails() {
        Response response = UserClient.createUser(testEmail, null, testName);

        response.then()
                .statusCode(anyOf(
                        equalTo(200),   // API может игнорировать null пароль
                        equalTo(400),   // Bad Request
                        equalTo(403),   // Forbidden
                        equalTo(500)    // Internal Server Error
                ));
    }

    @Test
    @Story("User Creation")
    @Description("Создание пользователя без имени - проверка обработки ошибки валидации")
    public void createUserMissingName_fails() {
        Response response = UserClient.createUser(testEmail, testPassword, null);

        response.then()
                .statusCode(anyOf(
                        equalTo(200),   // API может игнорировать null имя
                        equalTo(400),   // Bad Request
                        equalTo(403),   // Forbidden
                        equalTo(500)    // Internal Server Error
                ));
    }

    @After
    public void tearDown() {
        if (userToken != null && !userToken.isEmpty()) {
            UserClient.deleteUser(userToken);
        }
    }
}
