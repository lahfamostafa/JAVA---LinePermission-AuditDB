package ma.youcode.lineperm.service;

import java.sql.SQLException;
import java.util.Scanner;

import org.mindrot.jbcrypt.BCrypt;

import ma.youcode.lineperm.dao.UserDao;
import ma.youcode.lineperm.model.User;

public class UserService {

    Scanner scanner = new Scanner(System.in);
    UserDao userDao = new UserDao();

    public UserService(){
    }

    public boolean login(String user, String password)throws SQLException{
        User userFind = userDao.findByUser(user);
        if(userFind != null){
            if(BCrypt.checkpw(password,userFind.getPassword())){
                System.out.println("Vous ete conncte ");
                return true;
            }
        }
        System.out.println("Mot de pass ou user incorect !");
        return false;
    }
    
    public boolean signup(String user , String password) throws SQLException{
        User userFind = userDao.findByUser(user);
        if(userFind != null){
            System.out.println("Ce nom d'utilisateur est deja pris !");
            return false;
        }
        String passwordHashed = BCrypt.hashpw(password, BCrypt.gensalt()) ;
        User saveUser = new User(user, passwordHashed);

        if(userDao.save(saveUser)){
            System.out.println("inscription avec succes");
            login(user, password);
            return true;
        }
        return false;
    }
}