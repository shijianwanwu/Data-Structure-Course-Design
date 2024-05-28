package com.byr.project;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class JsonToMysql {
    public static void main(String[] args) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // 解析JSON文件
            List<Map<String, Object>> buildingsList = objectMapper.readValue(new File("src/main/java/com/byr/project/景区建筑物.json"), new TypeReference<List<Map<String, Object>>>() {});

            // 连接到MySQL数据库
            Connection connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/project?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&serverTimezone=Asia/Shanghai&rewriteBatchedStatements=true", "root", "lrp123456");
            String sql = "INSERT INTO scenebuildings (category, name) VALUES (?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);

            // 遍历数据并插入到数据库
            for (Map<String, Object> building : buildingsList) {
                statement.setString(1, (String) building.get("所属类别"));
                statement.setString(2, (String) building.get("建筑物名称"));
                statement.executeUpdate();
            }

            statement.close();
            connection.close();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }
}
