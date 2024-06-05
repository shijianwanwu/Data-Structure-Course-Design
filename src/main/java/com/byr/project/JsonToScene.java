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

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/project?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&serverTimezone=Asia/Shanghai&rewriteBatchedStatements=true", "root", "lrp123456")) { // 替换为你的数据库连接信息
            String sql = "DELETE FROM schoolbuildings WHERE id > 125";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
}}