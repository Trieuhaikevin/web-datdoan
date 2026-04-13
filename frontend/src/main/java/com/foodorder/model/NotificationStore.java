package com.foodorder.model;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class NotificationStore {
    private static final Gson GSON = new Gson();
    private static final String DIR = System.getProperty("user.home") + File.separator + ".foodorder";
    private static final String FILE = DIR + File.separator + "notifications.json";

    private final Set<String> acknowledged = new HashSet<>();

    public NotificationStore() {
        load();
    }

    private void load() {
        try {
            File dir = new File(DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File f = new File(FILE);
            if (!f.exists()) {
                return;
            }
            try (FileReader reader = new FileReader(f)) {
                Type type = new TypeToken<Set<String>>() {}.getType();
                Set<String> data = GSON.fromJson(reader, type);
                if (data != null) {
                    acknowledged.addAll(data);
                }
            }
        } catch (Exception ignored) {
        }
    }

    private void save() {
        try (FileWriter writer = new FileWriter(FILE)) {
            GSON.toJson(acknowledged, writer);
        } catch (Exception ignored) {
        }
    }

    private String key(Long orderId, String status) {
        return orderId + ":" + status;
    }

    public boolean isAcknowledged(Long orderId, String status) {
        if (orderId == null || status == null) return false;
        return acknowledged.contains(key(orderId, status));
    }

    public void acknowledge(Long orderId, String status) {
        if (orderId == null || status == null) return;
        acknowledged.add(key(orderId, status));
        save();
    }
}
