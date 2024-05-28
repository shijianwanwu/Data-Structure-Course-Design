import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        try {
            // 替换为你的数据库连接信息
            String url = "jdbc:mysql://localhost:3306/your_database";
            String user = "username";
            String password = "password";
            Connection conn = DriverManager.getConnection(url, user, password);

            while (true) {
                System.out.println("请输入起点:");
                int startPoint = scanner.nextInt();
                System.out.println("请输入终点:");
                int endPoint = scanner.nextInt();
                System.out.println("请输入距离:");
                double distance = scanner.nextDouble();
                System.out.println("请输入拥挤度:");
                double congestion = scanner.nextDouble();

                double time = distance / congestion;

                String sql = "INSERT INTO SchoolRoads (start_point, end_point, distance, congestion, time) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, startPoint);
                pstmt.setInt(2, endPoint);
                pstmt.setDouble(3, distance);
                pstmt.setDouble(4, congestion);
                pstmt.setDouble(5, time);

                int rowsInserted = pstmt.executeUpdate();
                if (rowsInserted > 0) {
                    System.out.println("数据插入成功!");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }
}