package services;

import models.User;
import java.sql.*;

public class AuthService {
    public User login(String username, String password, String role) {
        String sql = "SELECT user_id, username, role, email FROM users " +
                     "WHERE username = ? AND password_hash = ? AND role = ?";
        try (Connection conn = DatabaseService.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, role);

            System.out.println("Trying login:");
            System.out.println("Username: " + username);
            System.out.println("Password: " + password);
            System.out.println("Role: " + role);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new User(
                    rs.getInt("user_id"),
                    rs.getString("username"),
                    rs.getString("role"),
                    rs.getString("email")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}