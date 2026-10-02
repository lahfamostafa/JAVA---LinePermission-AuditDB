package ma.youcode.lineperm.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ma.youcode.lineperm.model.Log;

public class LogDao extends AbstractDao<Log> {

    @Override
    public boolean save(Log log) throws SQLException {
        String sql = "INSERT INTO logs (user_id, fichier_id, action, resultat, date, heure) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, log.getUserId());
            stmt.setInt(2, log.getFichierId());
            stmt.setString(3, log.getAction());
            stmt.setBoolean(4, log.getResultat());
            stmt.setDate(5, Date.valueOf(log.getDate()));
            stmt.setTime(6,Time.valueOf(log.getHeure()));
            // stmt.setTimestamp(6, Timestamp.valueOf(log.getHeureFormat()));
            // LocalDateTime localDateTime = LocalDateTime.of(log.getDate(), log.getHeure());

            int rows = stmt.executeUpdate();
            return rows > 0;
        }
    }

    /**
     * Surcharge pour enregistrer un log en laissant SQLite/SQL attribuer le CURRENT_TIMESTAMP
     */
    public boolean save(int userId, int fichierId, String action, boolean resultat) throws SQLException {
        String sql = "INSERT INTO logs (user_id, fichier_id, action, resultat) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.setInt(2, fichierId);
            stmt.setString(3, action);
            stmt.setBoolean(4, resultat);

            int rows = stmt.executeUpdate();
            return rows > 0;
        }
    }

    @Override
    public Log findById(int id) throws SQLException {
        String query = "SELECT * FROM logs WHERE id = ?";
        Connection conn = getConnection();
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setInt(1, id);
        
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return mapResultSetToLog(rs);
        }
        return null;
    }

    @Override
    public boolean delete(int id) throws SQLException {
        // Les logs d'audit sont immuables
        return false;
    }

    @Override
    public List<Log> findAll() throws SQLException {
        List<Log> list = new ArrayList<>();
        String query = "SELECT * FROM logs ORDER BY date DESC";
        
        Connection conn = getConnection();
        PreparedStatement stmt = conn.prepareStatement(query);
        ResultSet rs = stmt.executeQuery();
        
        while (rs.next()) {
            list.add(mapResultSetToLog(rs));
        }
        return list;
    }

    // 1. Total des actions
    public int totalActions() throws SQLException {
        String query = "SELECT COUNT(*) FROM logs";
        Connection conn = getConnection();
        PreparedStatement stmt = conn.prepareStatement(query);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return rs.getInt(1);
        }
        return 0;
    }

    // 2. Accès refusés
    public int totalRefuse() throws SQLException {
        String query = "SELECT COUNT(*) FROM logs WHERE resultat = 'false' OR resultat = '0' OR resultat = 'REFUSEE'";
        Connection conn = getConnection();
        PreparedStatement stmt = conn.prepareStatement(query);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return rs.getInt(1);
        }
        return 0;
    }

    // 3. Utilisateurs distincts (u.user au lieu de u.login)
    public List<String> usersDistinct() throws SQLException {
        List<String> liste = new ArrayList<>();
        String query = "SELECT DISTINCT u.user FROM logs l JOIN users u ON l.user_id = u.id";
        
        Connection conn = getConnection();
        PreparedStatement stmt = conn.prepareStatement(query);
        ResultSet rs = stmt.executeQuery();
        
        while (rs.next()) {
            liste.add(rs.getString("user"));
        }
        return liste;
    }

    // 4. Nombre d'actions par utilisateur (u.user)
    public Map<String, Long> actionParUser() throws SQLException {
        Map<String, Long> map = new HashMap<>();
        String query = "SELECT u.user, COUNT(*) AS total FROM logs l JOIN users u ON l.user_id = u.id GROUP BY u.user";
        
        Connection conn = getConnection();
        PreparedStatement stmt = conn.prepareStatement(query);
        ResultSet rs = stmt.executeQuery();
        
        while (rs.next()) {
            map.put(rs.getString("user"), rs.getLong("total"));
        }
        return map;
    }

    // 5. Top 3 des fichiers les plus consultés
    public Map<String, Long> top3Files() throws SQLException {
        Map<String, Long> map = new HashMap<>();
        String query = "SELECT f.name, COUNT(*) AS total FROM logs l JOIN fichiers f ON l.fichier_id = f.id GROUP BY f.name ORDER BY total DESC LIMIT 3";
        
        Connection conn = getConnection();
        PreparedStatement stmt = conn.prepareStatement(query);
        ResultSet rs = stmt.executeQuery();
        
        while (rs.next()) {
            map.put(rs.getString("name"), rs.getLong("total"));
        }
        return map;
    }

    // 6. Liste des accès refusés pour un utilisateur donné (u.user)
    public List<Log> userRefuse(String nomUtilisateur) throws SQLException {
        List<Log> liste = new ArrayList<>();
        String query = "SELECT l.* FROM logs l JOIN users u ON l.user_id = u.id WHERE u.user = ? AND (l.resultat = 'false' OR l.resultat = '0' OR l.resultat = 'REFUSEE')";
        
        Connection conn = getConnection();
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setString(1, nomUtilisateur);
        ResultSet rs = stmt.executeQuery();
        
        while (rs.next()) {
            liste.add(mapResultSetToLog(rs));
        }
        return liste;
    }

    // 7. L'utilisateur le plus actif (u.user)
    public String userActif() throws SQLException {
        String query = "SELECT u.user FROM logs l JOIN users u ON l.user_id = u.id GROUP BY u.user ORDER BY COUNT(*) DESC LIMIT 1";
        
        Connection conn = getConnection();
        PreparedStatement stmt = conn.prepareStatement(query);
        ResultSet rs = stmt.executeQuery();
        
        if (rs.next()) {
            return rs.getString("user");
        }
        return "Aucun utilisateur";
    }

    // 8. Répartition des actions par type (READ, WRITE, DELETE, etc.)
    public Map<String, Long> actionsParType() throws SQLException {
        Map<String, Long> map = new HashMap<>();
        String query = "SELECT action, COUNT(*) AS total FROM logs GROUP BY action";
        
        Connection conn = getConnection();
        PreparedStatement stmt = conn.prepareStatement(query);
        ResultSet rs = stmt.executeQuery();
        
        while (rs.next()) {
            map.put(rs.getString("action"), rs.getLong("total"));
        }
        return map;
    }

    /**
     * Méthode utilitaire interne pour convertir la ligne SQL en objet Log
     */
    private Log mapResultSetToLog(ResultSet rs) throws SQLException {
        Timestamp ts = rs.getTimestamp("date");
        LocalDateTime ldt = (ts != null) ? ts.toLocalDateTime() : LocalDateTime.now();
        
        // String resStr = rs.getBoolean("resultat");
        // boolean boolRes = "1".equals(resStr) || "true".equalsIgnoreCase(resStr) || "OK".equalsIgnoreCase(resStr);

        return new Log(
            rs.getInt("id"),
            ldt.toLocalDate(),
            ldt.toLocalTime(),
            rs.getInt("user_id"),
            rs.getInt("fichier_id"),
            rs.getString("action"),
            rs.getBoolean("resultat")
            // boolRes,
        );
    }
}