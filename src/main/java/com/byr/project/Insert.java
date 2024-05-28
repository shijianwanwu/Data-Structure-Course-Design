package com.byr.project;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Random;

public class Insert {
    public static void main(String[] args) {
        try {
            // 替换为你的数据库连接信息
            String url = "jdbc:mysql://localhost:3306/project";
            String user = "root";
            String password = "lrp123456";
            Connection conn = DriverManager.getConnection(url, user, password);

            Random random = new Random();
            double[] congestions = {0.5, 0.2,1.0};

            String selectSql = "SELECT distance FROM sceneroads WHERE id = ?";
            PreparedStatement selectPstmt = conn.prepareStatement(selectSql);

            String updateSql = "UPDATE sceneroads SET congestion = ?, time = ?, vehicle = ? WHERE id = ?";
            PreparedStatement updatePstmt = conn.prepareStatement(updateSql);

            for (int id = 1; id <= 10000; id++) {
                selectPstmt.setInt(1, id);
                ResultSet rs = selectPstmt.executeQuery();

                if (rs.next()) {
                    double distance = rs.getDouble("distance");
                    double congestion = congestions[random.nextInt(congestions.length)];
                    double time = distance / congestion;

                    updatePstmt.setDouble(1, congestion);
                    updatePstmt.setDouble(2, time);
                    updatePstmt.setString(3, "Walking");
                    updatePstmt.setInt(4, id);

                    int rowsUpdated = updatePstmt.executeUpdate();
                    if (rowsUpdated > 0) {
                        System.out.println("数据更新成功!");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}