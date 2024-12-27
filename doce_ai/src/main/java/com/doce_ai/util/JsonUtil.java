package com.doce_ai.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public class JsonUtil {

    public static String convertMapToJson(Map<String, Object> map) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(map);
        } catch (Exception e) {
            throw new RuntimeException("Error converting Map to JSON", e);
        }
    }
}