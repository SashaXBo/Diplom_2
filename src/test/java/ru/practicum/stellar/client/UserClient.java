package ru.practicum.stellar.client;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class UserClient {
    private static final String BASE_URL = "https://stellarburgers.education-services.ru";

    @Step("Создание пользователя")
    public static Response createUser(String email, String password, String name) {
        return given()
                .contentType("application/json")
                .body("{\"email\":\"" + email + "\",\"password\":\"" + password + "\",\"name\":\"" + name + "\"}")
                .when()
                .post(BASE_URL + "/api/auth/register")
                .then()
                .extract()
                .response();
    }

    @Step("Логин пользователя")
    public static Response loginUser(String email, String password) {
        return given()
                .contentType("application/json")
                .body("{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}")
                .when()
                .post(BASE_URL + "/api/auth/login")
                .then()
                .extract()
                .response();
    }

    @Step("Обновление данных пользователя")
    public static Response updateUser(String token, String email, String password, String name) {
        return given()
                .contentType("application/json")
                .header("Authorization", token)
                .body("{\"email\":\"" + email + "\",\"password\":\"" + password + "\",\"name\":\"" + name + "\"}")
                .when()
                .patch(BASE_URL + "/api/auth/user")
                .then()
                .extract()
                .response();
    }

    @Step("Обновление данных пользователя без авторизации")
    public static Response updateUserWithoutAuth(String email, String password, String name) {
        return given()
                .contentType("application/json")
                .body("{\"email\":\"" + email + "\",\"password\":\"" + password + "\",\"name\":\"" + name + "\"}")
                .when()
                .patch(BASE_URL + "/api/auth/user")
                .then()
                .extract()
                .response();
    }

    @Step("Удаление пользователя")
    public static Response deleteUser(String token) {
        return given()
                .contentType("application/json")
                .header("Authorization", token)
                .when()
                .delete(BASE_URL + "/api/auth/user")
                .then()
                .extract()
                .response();
    }
}
