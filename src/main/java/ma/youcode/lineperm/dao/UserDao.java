package ma.youcode.lineperm.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import ma.youcode.lineperm.model.User;

public class UserDao extends AbstractDao<User> {
    @Override
    public boolean save(User user) {
        String query = "INSERT INTO users(user,password) VALUES(?,?);";
        try (PreparedStatement pstmt = getConnection().prepareStatement(query)){
            pstmt.setString(1, user.getUser());
            pstmt.setString(2, user.getPassword());
            int exut = pstmt.executeUpdate();
            if (exut==1) {
                return true;
            }
        } catch (SQLException e) {
            System.out.println(e.getStackTrace());
        }
        System.err.println("probleme dans save");
        return false;
    }

    @Override
    public boolean delete(int id) {
        String query = "DELETE FROM users where id = ?";
        try(PreparedStatement pstmt = getConnection().prepareStatement(query)){
            pstmt.setInt(1, id);
            pstmt.execute();
            return true;
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }
        return false;
    }

    @Override
    public User findById(int id) throws SQLException{
        String query = "SELECT*FROM users where id = ?";
        try(Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if(rs.next()){
                    return new User(rs.getInt("id"), rs.getString("user"), rs.getString("password"));
                }
            }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return null ;
    }

    @Override
    public List<User> findAll() throws SQLException {
        String query = "SELECT * FROM users";
        List<User> users = new ArrayList<>();
        try(Statement stm = getConnection().createStatement()){
            ResultSet rs = stm.executeQuery(query);
            while (rs.next()) {
                users.add(new User(rs.getInt("id"), rs.getString("user"), rs.getString("password")));
            }
        }
        return users;
    }

    public User findByUser(String user) throws SQLException{
        String query = "SELECT*FROM users where user = ?";
        try(Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, user);
            try (ResultSet rs = pstmt.executeQuery()) {
                if(rs.next()){
                    return new User(rs.getInt("id"), rs.getString("user"), rs.getString("password"));
                }
            }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return null ;
    }
}
