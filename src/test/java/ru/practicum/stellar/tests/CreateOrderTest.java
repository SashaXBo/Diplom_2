package ru.practicum.stellar.tests;

import io.qameta.allure.*;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.practicum.stellar.client.OrderClient;
import ru.practicum.stellar.client.UserClient;
import ru.practicum.stellar.utils.TestDataGenerator;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;

@Epic("Stellar Burgers API")
@Feature("Order API")
public class CreateOrderTest {

    private static List<String> ingredients;
    private String userToken;
    private String testEmail;
    private String testPassword;
    private String testName;

    @BeforeClass
    public static void setUpClass() {
        // Шаг 1: Получаем список доступных ингредиентов (один раз для всех тестов)
        Response ingredientsResponse = OrderClient.getIngredients();
        ingredientsResponse.then().statusCode(200);

        List<String> allIngredients = ingredientsResponse.jsonPath().getList("data._id");
        ingredients = new ArrayList<>();
        if (allIngredients != null && !allIngredients.isEmpty()) {
            ingredients.add(allIngredients.get(0));
        }
    }

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
    @Story("Order Creation")
    @Description("Создание заказа с авторизацией и ингредиентами")
    public void createOrderWithAuthAndIngredients_success() {
        Response response = OrderClient.createOrderWithAuth(userToken, ingredients);

        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("order.number", notNullValue());
    }

    @Test
    @Story("Order Creation")
    @Description("Создание заказа без авторизации с ингредиентами")
    public void createOrderWithoutAuthAndIngredients() {
        Response response = OrderClient.createOrderWithoutAuth(ingredients);

        response.then()
                .statusCode(anyOf(equalTo(200), equalTo(401)));
    }

    @Test
    @Story("Order Creation")
    @Description("Попытка создания заказа без ингредиентов")
    public void createOrderWithAuthAndNoIngredients_fails() {
        List<String> emptyIngredients = new ArrayList<>();

        Response response = OrderClient.createOrderWithAuth(userToken, emptyIngredients);

        response.then()
                .statusCode(anyOf(equalTo(400), equalTo(500)));
    }

    @Test
    @Story("Order Creation")
    @Description("Попытка создания заказа с неверным хешем ингредиента")
    public void createOrderWithWrongIngredientHash_fails() {
        List<String> wrongIngredients = new ArrayList<>();
        wrongIngredients.add("wrong_hash_12345");

        Response response = OrderClient.createOrderWithAuth(userToken, wrongIngredients);

        response.then()
                .statusCode(anyOf(equalTo(400), equalTo(500)));
    }

    @After
    public void tearDown() {
        if (userToken != null && !userToken.isEmpty()) {
            UserClient.deleteUser(userToken);
        }
    }
}
