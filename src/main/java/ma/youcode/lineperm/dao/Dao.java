package ma.youcode.lineperm.dao;

import java.sql.SQLException;
import java.util.List;

public interface Dao<T> {
    boolean save(T t) throws SQLException;
    T findById(int id) throws SQLException;
    boolean delete(int id) throws SQLException;
    List<T> findAll() throws SQLException;
}