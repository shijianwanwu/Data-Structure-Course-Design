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

public class JsonToScene {
    public static void main(String[] args) {
        String filePath = "src/main/java/com/byr/project/graph.json";

        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> map = mapper.readValue(Paths.get(filePath).toFile(), new TypeReference<Map<String, Object>>() {
            });
            Map<String, List<Map<String, Map<String, Object>>>> adjList = (Map<String, List<Map<String, Map<String, Object>>>>) map.get("adjList");

            try (Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/project?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&serverTimezone=Asia/Shanghai&rewriteBatchedStatements=true", "root", "lrp123456")) { // 替换为你的数据库连接信息
                String sql = "UPDATE newschoolbuildings SET  x = ?, y = ? ,name= ? WHERE id = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);

                for (String key : adjList.keySet()) {
                    for (Map<String, Map<String, Object>> edge : adjList.get(key)) {
                        Map<String, Object> destination = edge.get("destination");
                        int x = ((Integer) destination.get("x"));
                        int y = ((Integer) destination.get("y"));
                        String name = (String) destination.get("name");
                        int id = ((Integer) destination.get("index")) + 1;

                        System.out.println(name + " " + x + " " + y);
                        stmt.setInt(1, x);
                        stmt.setInt(2, y);
                        stmt.setString(3, name);
                        stmt.setInt(4, id);
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