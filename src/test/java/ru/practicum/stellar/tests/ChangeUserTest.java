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
public class ChangeUserTest {

    private String userToken;
    private String testEmail;
    private String testPassword;
    private String testName;

    @Before
    public void setUp() {
        testEmail = TestDataGenerator.generateUniqueEmail();
        testPassword = TestDataGenerator.generatePassword();
        testName = TestDataGenerator.generateName();

        Response createResponse = UserClient.createUser(testEmail, testPassword, testName);
        createResponse.then().statusCode(200);
        userToken = createResponse.jsonPath().getString("accessToken");
    }

    @Test
    @Story("User Update")
    @Description("Изменение email авторизованным пользователем")
    public void updateUserEmailWithAuth_success() {
        String newEmail = TestDataGenerator.generateUniqueEmail();

        Response response = UserClient.updateUser(userToken, newEmail, testPassword, testName);

        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(newEmail));
    }

    @Test
    @Story("User Update")
    @Description("Изменение password авторизованным пользователем")
    public void updateUserPasswordWithAuth_success() {
        String newPassword = TestDataGenerator.generatePassword();

        Response response = UserClient.updateUser(userToken, testEmail, newPassword, testName);

        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(testEmail));
    }

    @Test
    @Story("User Update")
    @Description("Изменение name авторизованным пользователем")
    public void updateUserNameWithAuth_success() {
        String newName = TestDataGenerator.generateName();

        Response response = UserClient.updateUser(userToken, testEmail, testPassword, newName);

        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.name", equalTo(newName));
    }

    @Test
    @Story("User Update")
    @Description("Попытка изменения email без авторизации")
    public void updateUserEmailWithoutAuth_fails() {
        String newEmail = TestDataGenerator.generateUniqueEmail();

        Response response = UserClient.updateUserWithoutAuth(newEmail, testPassword, testName);

        response.then()
                .statusCode(401)
                .body("success", equalTo(false));
    }

    @Test
    @Story("User Update")
    @Description("Попытка изменения password без авторизации")
    public void updateUserPasswordWithoutAuth_fails() {
        String newPassword = TestDataGenerator.generatePassword();

        Response response = UserClient.updateUserWithoutAuth(testEmail, newPassword, testName);

        response.then()
                .statusCode(401)
                .body("success", equalTo(false));
    }

    @Test
    @Story("User Update")
    @Description("Попытка изменения name без авторизации")
    public void updateUserNameWithoutAuth_fails() {
        String newName = TestDataGenerator.generateName();

        Response response = UserClient.updateUserWithoutAuth(testEmail, testPassword, newName);

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
