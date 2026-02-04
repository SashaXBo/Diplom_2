package ru.practicum.stellar.utils;

import java.util.UUID;

public class TestDataGenerator {
    
    public static String generateUniqueEmail() {
        return "test_" + UUID.randomUUID().toString().substring(0, 8) + "@qa.test";
    }

    public static String generatePassword() {
        return "Password123!";
    }

    public static String generateName() {
        return "QA Test User " + UUID.randomUUID().toString().substring(0, 4);
    }
}
