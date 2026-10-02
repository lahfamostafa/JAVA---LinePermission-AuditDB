package ma.youcode.lineperm.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnection {
    private static final String URL = "jdbc:sqlite:audit_db.db";
    private static Connection connection;

    private DBConnection() {}

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("org.sqlite.JDBC");
                connection = DriverManager.getConnection(URL);
                try (Statement statement = connection.createStatement();) {
                    statement.execute("PRAGMA foreign_keys = ON;");
                }
            }catch(ClassNotFoundException e){
                throw new SQLException("Fichier JAR du driver SQLite manquant dans lib/", e);
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                throw e;
            }
        }
        return connection;

    }
}