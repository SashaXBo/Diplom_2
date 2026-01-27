package ru.practicum.stellar.tests;

import io.qameta.allure.*;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import ru.practicum.stellar.client.OrderClient;
import ru.practicum.stellar.client.UserClient;
import ru.practicum.stellar.utils.TestDataGenerator;

import static org.hamcrest.Matchers.*;

@Epic("Stellar Burgers API")
@Feature("Order API")
public class GetOrdersTest {
    private String token;

    @Before
    public void setUp() {
        String email = TestDataGenerator.generateUniqueEmail();
        String password = TestDataGenerator.generatePassword();
        String name = TestDataGenerator.generateName();

        Response createResponse = UserClient.createUser(email, password, name);
        createResponse.then().statusCode(200);

        this.token = createResponse.jsonPath().getString("accessToken");
    }

    @Test
    @Story("Order Get")
    @Description("Получение заказов авторизованного пользователя")
    public void getUserOrdersWithAuth_success() {
        Response response = OrderClient.getUserOrders(token);
        response.then().statusCode(200).body("success", equalTo(true));
    }

    @Test
    @Story("Order Get")
    @Description("Получение заказов без авторизации")
    public void getUserOrdersWithoutAuth_fails() {
        Response response = OrderClient.getUserOrdersWithoutAuth();
        response.then().statusCode(401).body("success", equalTo(false));
    }
}
