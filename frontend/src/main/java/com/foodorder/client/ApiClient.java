package com.foodorder.client;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ApiClient {
    private static final String BASE_URL = "http://localhost:8080/api";
    private static final Gson GSON = new Gson();

    public <T> T get(String path, Class<T> responseType) {
        return request("GET", path, null, responseType);
    }

    public <T> T post(String path, Object body, Class<T> responseType) {
        return request("POST", path, body, responseType);
    }

    public <T> T put(String path, Object body, Class<T> responseType) {
        return request("PUT", path, body, responseType);
    }

    public <T> T patch(String path, Object body, Class<T> responseType) {
        return request("PATCH", path, body, responseType);
    }

    public void delete(String path) {
        request("DELETE", path, null, Void.class);
    }

    private <T> T request(String method, String path, Object body, Class<T> responseType) {
        HttpURLConnection connection = null;
        try {
            URI uri = URI.create(BASE_URL + path);
            connection = (HttpURLConnection) uri.toURL().openConnection();
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(15000);

            // For requests with body, set doOutput before setRequestMethod
            if (body != null) {
                connection.setDoOutput(true);
            }

            connection.setRequestMethod(method);
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");

            if (body != null) {
                String jsonBody = GSON.toJson(body);
                try (OutputStream os = connection.getOutputStream()) {
                    os.write(jsonBody.getBytes(StandardCharsets.UTF_8));
                }
            }

            int statusCode = connection.getResponseCode();
            InputStream responseStream = statusCode >= 200 && statusCode < 300
                    ? connection.getInputStream()
                    : connection.getErrorStream();
            String responseBody = readResponse(responseStream);

            if (statusCode < 200 || statusCode >= 300) {
                throw new RuntimeException(extractErrorMessage(responseBody, statusCode));
            }

            if (responseType == Void.class || responseBody == null || responseBody.isBlank()) {
                return null;
            }

            return GSON.fromJson(responseBody, responseType);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Không thể kết nối backend: " + e.getMessage(), e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private String extractErrorMessage(String responseBody, int statusCode) {
        if (responseBody == null || responseBody.isBlank()) {
            return "Lỗi API: HTTP " + statusCode;
        }

        try {
            JsonElement json = JsonParser.parseString(responseBody);
            if (json.isJsonObject()) {
                JsonObject object = json.getAsJsonObject();
                if (object.has("message") && !object.get("message").isJsonNull()) {
                    return object.get("message").getAsString();
                }
                if (object.has("error") && !object.get("error").isJsonNull()) {
                    return object.get("error").getAsString();
                }
            }
        } catch (Exception ignored) {
        }

        return responseBody;
    }

    private String readResponse(InputStream stream) {
        if (stream == null) {
            return "";
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            return builder.toString();
        } catch (Exception e) {
            throw new RuntimeException("Không thể đọc dữ liệu phản hồi", e);
        }
    }
}
