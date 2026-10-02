package ma.youcode.lineperm.model;

public class Fichier {
    private int id;
    private int userId;
    private String proprietaire;
    private String name;

    private boolean ownerWrite = true;
    private boolean ownerRead = true;
    private boolean ownerDelete = true;

    private boolean autherWrite = false;
    private boolean autherRead = false;
    private boolean autherDelete = false;

    private String content = null;

    public Fichier(){}
    public Fichier(int id ,int userId ,String proprietaire, String name, boolean ownerWrite, boolean ownerRead, boolean ownerDelete,
            boolean autherWrite, boolean autherRead, boolean autherDelete , String content) {
        this.userId = userId;
        this.id = id;
        this.proprietaire = proprietaire;
        this.name = name;
        this.ownerWrite = ownerWrite;
        this.ownerRead = ownerRead;
        this.ownerDelete = ownerDelete;
        this.autherWrite = autherWrite;
        this.autherRead = autherRead;
        this.autherDelete = autherDelete;
        this.content = content;
    }


    public Fichier(int userId ,String proprietaire, String name) {
        this.userId = userId;
        this.proprietaire = proprietaire;
        this.name = name;
    }
    

    public String getProprietaire() {
        return proprietaire;
    }

    public void setProprietaire(String proprietaire) {
        this.proprietaire = proprietaire;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isOwnerWrite() {
        return ownerWrite;
    }

    public void setOwnerWrite(boolean ownerWrite) {
        this.ownerWrite = ownerWrite;
    }

    public boolean isOwnerRead() {
        return ownerRead;
    }

    public void setOwnerRead(boolean ownerRead) {
        this.ownerRead = ownerRead;
    }

    public boolean isOwnerDelete() {
        return ownerDelete;
    }

    public void setOwnerDelete(boolean ownerDelete) {
        this.ownerDelete = ownerDelete;
    }

    public boolean isAutherWrite() {
        return autherWrite;
    }

    public void setAutherWrite(boolean autherWrite) {
        this.autherWrite = autherWrite;
    }

    public boolean isAutherRead() {
        return autherRead;
    }

    public void setAutherRead(boolean autherRead) {
        this.autherRead = autherRead;
    }

    public boolean isAutherDelete() {
        return autherDelete;
    }

    public void setAutherDelete(boolean autherDelete) {
        this.autherDelete = autherDelete;
    }

    public String getPersmission(){
        String ownerPermissions = 
            (ownerRead ? "r" : "-") +
            (ownerWrite ? "w" : "-") +
            (ownerDelete ? "d" : "-");
            
        String autherPermissions = 
            (autherRead ? "r" : "-") +
            (autherWrite ? "w" : "-") +
            (autherDelete ? "d" : "-");

        return ownerPermissions + "|" + autherPermissions;
    }
    public int getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
}