package com.byr.project;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Insert {
    public static void main(String[] args) {
        try {
            // 替换为你的数据库连接信息
            String url = "jdbc:mysql://localhost:3306/project";
            String user = "root";
            String password = "lrp123456";
            Connection conn = DriverManager.getConnection(url, user, password);

            String selectSql = "SELECT start_point, end_point, distance FROM newschoolroads WHERE id = ?";
            PreparedStatement selectPstmt = conn.prepareStatement(selectSql);

            String updateSql = "UPDATE newschoolroads SET start_point = ?, end_point = ?, distance = ? WHERE id = ?";
            PreparedStatement updatePstmt = conn.prepareStatement(updateSql);

            for (int id = 1; id <= 40; id++) {
                selectPstmt.setInt(1, id);
                ResultSet rs = selectPstmt.executeQuery();

                if (rs.next()) {
                    int start_point = rs.getInt("start_point");
                    int end_point = rs.getInt("end_point");
                    double distance = rs.getDouble("distance");

                    updatePstmt.setInt(1, start_point);
                    updatePstmt.setInt(2, end_point);
                    updatePstmt.setDouble(3, distance);
                    updatePstmt.setInt(4, id + 416);  // 更新的数据id为原id+416

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