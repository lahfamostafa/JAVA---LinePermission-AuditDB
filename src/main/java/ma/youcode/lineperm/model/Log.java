package ma.youcode.lineperm.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Log {

    private int id;
    private LocalDate date = LocalDate.now();
    private LocalTime heure = LocalTime.now();
    private int userId;
    private int fichierId;
    private String action;
    private Boolean resultat;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
    String heureFormat = heure.format(formatter);

    public Log(int userId, int fichierId, String action, boolean resultat) {
        this.userId = userId;
        this.action = action;
        this.fichierId = fichierId;
        this.resultat = resultat;
    }
    public Log(int id ,int userId, int fichierId, String action, boolean resultat) {
        this.id = id;
        this.userId = userId;
        this.action = action;
        this.fichierId = fichierId;
        this.resultat = resultat;
    }
    public Log(int id ,LocalDate date, LocalTime heure,int userId, int fichierId, String action, boolean resultat) {
        this.id = id;
        this.date = date;
        this.heure = heure;
        this.userId = userId;
        this.action = action;
        this.fichierId = fichierId;
        this.resultat = resultat;
    }
    public LocalDate getDate() {return date;}

    public LocalTime getHeure() {return heure;}

    public int getUserId() {return userId;}

    public String getAction() {return action;}

    public int getFichierId() {return fichierId;}

    public Boolean getResultat() {return resultat;}
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public void setDate(LocalDate date) {
        this.date = date;
    }
    public void setHeure(LocalTime heure) {
        this.heure = heure;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }
    public void setFichierId(int fichierId) {
        this.fichierId = fichierId;
    }
    public void setAction(String action) {
        this.action = action;
    }
    public void setResultat(Boolean resultat) {
        this.resultat = resultat;
    }
    public DateTimeFormatter getFormatter() {
        return formatter;
    }
    public void setFormatter(DateTimeFormatter formatter) {
        this.formatter = formatter;
    }
    public String getHeureFormat() {
        return heureFormat;
    }
    public void setHeureFormat(String heureFormat) {
        this.heureFormat = heureFormat;
    }
    
}