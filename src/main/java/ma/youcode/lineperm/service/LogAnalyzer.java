package ma.youcode.lineperm.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import ma.youcode.lineperm.dao.FileDao;
import ma.youcode.lineperm.dao.LogDao;
import ma.youcode.lineperm.dao.UserDao;
import ma.youcode.lineperm.model.Fichier;
import ma.youcode.lineperm.model.Log;
import ma.youcode.lineperm.model.User;

public class LogAnalyzer {

    private LogDao logDao;
    private UserDao uDao = new UserDao();
    private FileDao fDao = new FileDao();

    public LogAnalyzer() {
        this.logDao = new LogDao();
    }

    // Sauvegarder un log en base de données
    public void saveLog(String user, String action, String fichier, boolean resultat) throws SQLException{
        User use = uDao.findByUser(user);
        Fichier f = fDao.findByName(fichier);
        try {
            Log log = new Log(use.getId(), f.getId(), action, resultat);
            logDao.save(log);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Afficher tous les logs
    public void afficherLogs() {
        try {
            List<Log> logs = logDao.findAll();
            for (Log log : logs) {
                System.out.println(log.getAction() + " - " + (log.getResultat() ? "OK" : "REFUSEE"));
            }
        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }

    // 1. Total des actions
    public void totalActions() {
        try {
            int total = logDao.totalActions();
            System.out.println("\nTotal des actions : " + total);
        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }

    // 2. Accès refusés
    public void totalRefuse() {
        try {
            int total = logDao.totalRefuse();
            System.out.println("\nAcces refuses : " + total);
        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }

    // 3. Utilisateurs distincts
    public void usersDistinct() {
        try {
            List<String> users = logDao.usersDistinct();
            System.out.println("\nTotal des utilisateurs distincts : ");
            for (String user : users) {
                System.out.println(user);
            }
        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }

    // 4. Actions par utilisateur
    public void actionParUser() {
        try {
            Map<String, Long> map = logDao.actionParUser();
            System.out.println("\nActions par utilisateur : \n" + map);
        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }

    // 5. Top 3 des fichiers consultés
    public void top3Files() {
        try {
            Map<String, Long> top3 = logDao.top3Files();
            System.out.println("\nTop 3 des fichiers consultes : ");
            System.out.println(top3);
        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }

    // 6. Accès refusés pour un utilisateur donné
    public void userRefuse() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Saisir le nom de l'utilisateur : ");
        String nom = scanner.nextLine();

        try {
            List<Log> logsRefus = logDao.userRefuse(nom);
            System.out.println("\n==================================================");
            System.out.println("ACCES REFUSES POUR L'UTILISATEUR : " + nom);
            System.out.println("==================================================");
            System.out.println("Date       | Heure | Action   | Statut");
            System.out.println("--------------------------------------------------");

            for (Log log : logsRefus) {
                System.out.printf("%-10s | %-5s | %-8s | %-10s%n",
                        log.getDate(), log.getHeure(), log.getAction(), log.getResultat() ? "OK" : "REFUSEE");
            }
        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }

    // 7. Utilisateur le plus actif
    public void userActif() {
        try {
            String user = logDao.userActif();
            System.out.println("\nUtilisateur le plus actif : " + user);
        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }

    // 8. Répartition des actions par type
    public void actionsParType() {
        try {
            Map<String, Long> actions = logDao.actionsParType();
            System.out.println("\nRepartition des actions par type : " + actions);
        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }
}