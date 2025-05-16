package repository;

import model.User;
import java.sql.*;
import java.util.UUID;

public class UserRepository {

    private final Connection connection;

    public UserRepository() throws SQLException {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    public UUID createUser(User user) {
        UUID id = user.getId() != null ? user.getId() : UUID.randomUUID();
        user.setId(id);

        String sql = "INSERT INTO users (id, username, password, role) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id.toString());
            stmt.setString(2, user.getUsername());
            stmt.setString(3, user.getPassword());
            stmt.setString(4, user.getRole());
            stmt.executeUpdate();
            return id;
        } catch (SQLException e) {
            throw new RuntimeException("Error while creating user", e);
        }
    }

    public User getUserByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setId(UUID.fromString(rs.getString("id")));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password")); // Nur intern verwenden
                user.setRole(rs.getString("role"));
                return user;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving user", e);
        }
        return null;
    }
}
