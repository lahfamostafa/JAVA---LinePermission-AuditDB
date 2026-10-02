package ma.youcode.lineperm.model;

public class User {
    private int id;
    private  String user;
    private String password;

    public User(int id ,String user , String password){
        this.id = id;
        this.user = user;
        this.password = password;
    }
    
    public User(String user, String password){
        this.user = user;
        this.password = password;
    }

    public String getUser(){
        return user;
    }

    public String getPassword(){
        return password;
    }

    public int getId() {
        return id;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}