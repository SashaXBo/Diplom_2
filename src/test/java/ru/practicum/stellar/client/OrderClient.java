package ru.practicum.stellar.client;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import java.util.List;

import static io.restassured.RestAssured.given;

public class OrderClient {
    private static final String BASE_URI = "https://stellarburgers.education-services.ru";
    private static final String ORDERS_ENDPOINT = "/api/orders";

    @Step("Создание заказа с авторизацией")
    public static Response createOrderWithAuth(String token, List<String> ingredients) {
        String ingredientsList = "[\"" + String.join("\",\"", ingredients) + "\"]";
        return given()
                .contentType("application/json")
                .header("Authorization", token)
                .body(String.format("{\"ingredients\":%s}", ingredientsList))
                .when()
                .post(BASE_URI + ORDERS_ENDPOINT);
    }

    @Step("Создание заказа без авторизации")
    public static Response createOrderWithoutAuth(List<String> ingredients) {
        String ingredientsList = "[\"" + String.join("\",\"", ingredients) + "\"]";
        return given()
                .contentType("application/json")
                .body(String.format("{\"ingredients\":%s}", ingredientsList))
                .when()
                .post(BASE_URI + ORDERS_ENDPOINT);
    }

    @Step("Получение заказов авторизованного пользователя")
    public static Response getUserOrders(String token) {
        return given()
                .header("Authorization", token)
                .when()
                .get(BASE_URI + ORDERS_ENDPOINT);
    }

    @Step("Получение заказов без авторизации")
    public static Response getUserOrdersWithoutAuth() {
        return given()
                .when()
                .get(BASE_URI + ORDERS_ENDPOINT);
    }

    @Step("Получение списка ингредиентов")
    public static Response getIngredients() {
        return given()
                .when()
                .get(BASE_URI + "/api/ingredients");
    }
}
