package ma.youcode.lineperm.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import ma.youcode.lineperm.model.Fichier;

public class FileDao extends AbstractDao<Fichier> {

    @Override
    public boolean save(Fichier fichier) throws SQLException {
        String query = "INSERT INTO fichiers (user_id, name, proprietaire, ownerWrite, ownerRead, ownerDelete, autherWrite, autherRead, autherDelete, content) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstm = getConnection().prepareStatement(query)) {
            pstm.setInt(1, fichier.getUserId());
            pstm.setString(2, fichier.getName());
            pstm.setString(3, fichier.getProprietaire());

            pstm.setBoolean(4, fichier.isOwnerWrite());
            pstm.setBoolean(5, fichier.isOwnerRead());
            pstm.setBoolean(6, fichier.isOwnerDelete());

            pstm.setBoolean(7, fichier.isAutherWrite());
            pstm.setBoolean(8, fichier.isAutherRead());
            pstm.setBoolean(9, fichier.isAutherDelete());

            pstm.setString(10, fichier.getContent());

            boolean exec = pstm.execute();
            if (exec) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Fichier findById(int id) throws SQLException {
        String query = "SELECT * FROM fichiers WHERE id = ?";
        try (PreparedStatement pstm = getConnection().prepareStatement(query)) {
            pstm.setInt(0, id);
            try (ResultSet rs = pstm.executeQuery()) {
                if (rs.next()) {
                    Fichier f = new Fichier();
                    f.setId(rs.getInt("id"));
                    f.setUserId(rs.getInt("user_id"));
                    f.setName(rs.getString("name"));

                    f.setOwnerWrite(rs.getBoolean("ownerWrite"));
                    f.setOwnerRead(rs.getBoolean("ownerRead"));
                    f.setOwnerDelete(rs.getBoolean("ownerDelete"));

                    f.setAutherWrite(rs.getBoolean("autherWrite"));
                    f.setAutherRead(rs.getBoolean("autherRead"));
                    f.setAutherDelete(rs.getBoolean("autherDelete"));

                    f.setContent(rs.getString("content"));
                    return f;
                }
            }
        }
        return null;
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String query = "DELETE FROM fichiers where id=?";
        try (PreparedStatement pstm = getConnection().prepareStatement(query)){
            pstm.setInt(1, id);
            
            if (pstm.executeUpdate()!=0) {
                return true;
            }return false;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return true;
    }

    @Override
    public List<Fichier> findAll() throws SQLException {
        String query = "SELECT * FROM fichiers";
        List<Fichier> fichiersList = new ArrayList<>();
        try (Statement pstm = getConnection().createStatement(); 
            ResultSet rs = pstm.executeQuery(query)) {
                while (rs.next()) {
                    Fichier f = new Fichier();
                    f.setId(rs.getInt("id"));
                    f.setUserId(rs.getInt("user_id"));
                    f.setName(rs.getString("name"));
                    f.setProprietaire(rs.getString("proprietaire"));

                    f.setOwnerWrite(rs.getBoolean("ownerWrite"));
                    f.setOwnerRead(rs.getBoolean("ownerRead"));
                    f.setOwnerDelete(rs.getBoolean("ownerDelete"));

                    f.setAutherWrite(rs.getBoolean("autherWrite"));
                    f.setAutherRead(rs.getBoolean("autherRead"));
                    f.setAutherDelete(rs.getBoolean("autherDelete"));

                    f.setContent(rs.getString("content"));
                    fichiersList.add(f);
                }
            }
        return fichiersList;
    }

    public Fichier findByName(String fileName) throws SQLException {
        String query = "SELECT * FROM fichiers WHERE name = ?";
        try (PreparedStatement pstm = getConnection().prepareStatement(query)) {
            pstm.setString(1, fileName);
            try (ResultSet rs = pstm.executeQuery()) {
                if (rs.next()) {
                    Fichier f = new Fichier();
                    f.setId(rs.getInt("id"));
                    f.setUserId(rs.getInt("user_id"));
                    f.setName(rs.getString("name"));
                    f.setProprietaire(rs.getString("proprietaire"));

                    f.setOwnerWrite(rs.getBoolean("ownerWrite"));
                    f.setOwnerRead(rs.getBoolean("ownerRead"));
                    f.setOwnerDelete(rs.getBoolean("ownerDelete"));

                    f.setAutherWrite(rs.getBoolean("autherWrite"));
                    f.setAutherRead(rs.getBoolean("autherRead"));
                    f.setAutherDelete(rs.getBoolean("autherDelete"));

                    f.setContent(rs.getString("content"));
                    return f;
                }
            }
        }
        return null;
    }

    public boolean modifierContent(Fichier fichier, String content) throws SQLException {
        String query = "UPDATE fichiers SET content = ? WHERE name = ?";
        try (PreparedStatement pstm = getConnection().prepareStatement(query)) {
            pstm.setString(1, content);
            pstm.setString(2, fichier.getName());
            pstm.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return false;
    }

    public String getContent(String file)throws SQLException{
        String query = "SELECT content From fichiers where name = ?";
        try (PreparedStatement pstm = getConnection().prepareStatement(query)){
            pstm.setString(1, file);
            try(ResultSet rs = pstm.executeQuery()){
                String content = rs.getString("content");
                return (content != null) ? content : "";
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return "";
    }

    public boolean updateDroits(Fichier fichier){
        String query = "UPDATE fichiers SET autherWrite = ? , autherRead = ? , autherDelete = ? WHERE id = ?";
        try (PreparedStatement pstm = getConnection().prepareStatement(query)) {
            pstm.setBoolean(1, fichier.isAutherWrite());
            pstm.setBoolean(2, fichier.isAutherRead());
            pstm.setBoolean(3, fichier.isAutherDelete());

            pstm.setInt(4, fichier.getId());
            pstm.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }
}
