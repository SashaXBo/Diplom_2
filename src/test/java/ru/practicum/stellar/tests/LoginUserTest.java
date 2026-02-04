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
@Feature("User API")
public class LoginUserTest {

    private String userToken;
    private String testEmail;
    private String testPassword;
    private String testName;

    @Before
    public void setUp() {
        testEmail = TestDataGenerator.generateUniqueEmail();
        testPassword = TestDataGenerator.generatePassword();
        testName = TestDataGenerator.generateName();

        Response createResponse = (Response) UserClient.createUser(testEmail, testPassword, testName);
        createResponse.then().statusCode(200);
        userToken = createResponse.jsonPath().getString("accessToken");
    }

    @Test
    @Story("User Login")
    @Description("Логин с корректными email и password")
    public void loginExistingUser_success() {
        Response response = UserClient.loginUser(testEmail, testPassword);

        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("accessToken", notNullValue())
                .body("user.email", equalTo(testEmail));
    }

    @Test
    @Story("User Login")
    @Description("Логин с неверным password")
    public void loginWithWrongPassword_fails() {
        Response response = UserClient.loginUser(testEmail, "wrongPassword123");

        response.then()
                .statusCode(401)
                .body("success", equalTo(false));
    }

    @Test
    @Story("User Login")
    @Description("Логин с неверным email")
    public void loginWithWrongEmail_fails() {
        Response response = UserClient.loginUser("wrong@email.com", testPassword);

        response.then()
                .statusCode(401)
                .body("success", equalTo(false));
    }

    @Test
    @Story("User Login")
    @Description("Логин с неверными email и password")
    public void loginWithWrongCredentials_fails() {
        Response response = UserClient.loginUser("wrong@email.com", "wrongPassword");

        response.then()
                .statusCode(401)
                .body("success", equalTo(false));
    }

    @After
    public void tearDown() {
        if (userToken != null && !userToken.isEmpty()) {
            UserClient.deleteUser(userToken);
        }
    }
}
