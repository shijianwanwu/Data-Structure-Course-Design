package com.byr.project;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class JsonToSceneRoad {
    public static void main(String[] args) {
        String filePath = "src/main/java/com/byr/project/graph.json";

        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> map = mapper.readValue(Paths.get(filePath).toFile(), new TypeReference<Map<String, Object>>() {
            });
            Map<String, List<Map<String, Object>>> adjList = (Map<String, List<Map<String, Object>>>) map.get("adjList");

            try (Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/project?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&serverTimezone=Asia/Shanghai&rewriteBatchedStatements=true", "root", "lrp123456")) { // 替换为你的数据库连接信息
                String sql = "INSERT INTO sceneroads (start_point, end_point, distance) VALUES (?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);

                for (String key : adjList.keySet()) {
                    String[] parts = key.split(", ");
                    int startPoint = Integer.parseInt(parts[1].split("=")[1]);

                    for (Map<String, Object> edge : adjList.get(key)) {
                        Map<String, Object> destination = (Map<String, Object>) edge.get("destination");
                        int endPoint = ((Integer) destination.get("index"));
                        double distance = ((Integer) edge.get("weight")).doubleValue();

                        stmt.setInt(1, startPoint + 1);
                        stmt.setInt(2, endPoint + 1);
                        stmt.setDouble(3, distance);
                        stmt.executeUpdate();
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}