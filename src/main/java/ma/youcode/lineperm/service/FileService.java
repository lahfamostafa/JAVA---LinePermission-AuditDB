package ma.youcode.lineperm.service;

import java.sql.SQLException;
import java.util.Scanner;
import java.util.List;

import ma.youcode.lineperm.dao.FileDao;
import ma.youcode.lineperm.dao.UserDao;
import ma.youcode.lineperm.model.Fichier;
import ma.youcode.lineperm.model.User;

public class FileService {
    private LogAnalyzer logA = new LogAnalyzer();
    private UserDao uDao = new UserDao();
    private FileDao fDao = new FileDao();

    public FileService() {}

    public void lsCommande() throws SQLException {
        List<Fichier> fichiersList = fDao.findAll();
        for (Fichier f : fichiersList) {
            System.out.printf("%-12s %-18s %s%n", f.getPersmission(), f.getName(), f.getProprietaire());
        }
    }

    public void touchCommande(String FileName, String propr) {
        try {
            if (fDao.findByName(FileName) != null) {
                System.out.println("Le fichier " + FileName + " existe deja !");
                return;
            }

            User user = uDao.findByUser(propr);
            Fichier fichier = new Fichier(user.getId(),propr, FileName);
            
            fDao.save(fichier);
            System.out.println("Fichier " + FileName + " cree avec succes.");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void catCommande(String file, String user) throws SQLException {
        if (user == null) {
            System.out.println("Tu dois se connecter d'abord");
            return;
        }
        User use = uDao.findByUser(user);
        Fichier fichier = fDao.findByName(file);
        if (fichier == null) {
            System.out.println("Le fichier est introuvable");
            return;
        }
        if (fichier.isAutherRead() || (use.getUser().equals(fichier.getProprietaire()))) {
            try {
                String lines = fichier.getContent();
                System.out.println(lines);
                if (lines == null) {
                    System.out.println("(fichier vide)");
                    return;
                }
                System.out.println(lines);
                logA.saveLog(user, "LECTURE", file, true);
                return;
            } catch (Exception e) {
                logA.saveLog(user, "LECTURE", file, false);
                System.out.println(e.getMessage());
                return;
            }
        }
        logA.saveLog(user, "LECTURE", file, false);
        System.out.println("vous n'avez pas la permission de lire le fichier : " + file);
    }

    public void nanoCommande(String file, String user) throws SQLException {
        if (user == null) {
            System.out.println("Tu dois se connecter d'abord");
            return;
        }
        Fichier fichier = fDao.findByName(file);
        if (fichier == null) {
            System.out.println("Le fichier est introuvable");
            return;
        }
        if (fichier.isAutherWrite() || (user.equals(fichier.getProprietaire()))) {
            System.out.println("=== Contenu actuel de " + file + " ===");
            System.out.println("--- Entrez votre texte (tapez 'EOF' sur une nouvelle ligne pour enregistrer) ---");
            catCommande(file, user);

            String oldText = fDao.getContent(file);
            Scanner scanner = new Scanner(System.in);
            StringBuilder sb = new StringBuilder();

            while (true) {
                String ligne = scanner.nextLine();
                if (ligne.contains("EOF"))
                    break;
                sb.append(ligne).append(System.lineSeparator());
            }

            String newContent = oldText + sb.toString();

            if (fDao.modifierContent(fichier, newContent)) {
                System.out.println("Modifications enregistrees dans " + file);
                logA.saveLog(user, "ECRITURE", file, true);
                return;
            }
        }
        System.err.println("Vous n'avez pas la permission d'ecrire sur : " + file);
        logA.saveLog(user, "ECRITURE", file, false);
        return;
    }

    public boolean rmCommande(String file, String user) throws SQLException {
        Fichier fichier = fDao.findByName(file);
        if (fichier.isAutherDelete() || (user.equals(fichier.getProprietaire()))) {
            if (fDao.delete(fichier.getId())) {
                logA.saveLog(user, "SUPPRIMER", file, true);
                System.out.println("Fichier supprime avec succes");
                return true;
            }
        }
        logA.saveLog(user, "SUPPRIMER", file, false);
        System.err.println("Vous n'avez pas la permission de supprimer " + file);
        return false;
    }

    public void chmodCommande(String file, String user, String newPerm) throws SQLException {
        if (user == null) {
            System.out.println("Tu dois se connecter d'abord");
            return;
        }
        Fichier fichier = fDao.findByName(file);
        if (fichier == null) {
            System.out.println("Le fichier est introuvable");
            return;
        }
        if (user.equals(fichier.getProprietaire())) {
            String ancientPerm = fichier.getPersmission();

            if (newPerm.startsWith("-")) {
                if (newPerm.contains("w"))
                    fichier.setAutherWrite(false);
                if (newPerm.contains("r"))
                    fichier.setAutherRead(false);
                if (newPerm.contains("d"))
                    fichier.setAutherDelete(false);
            } else {
                if (newPerm.contains("w"))
                    fichier.setAutherWrite(true);
                if (newPerm.contains("r"))
                    fichier.setAutherRead(true);
                if (newPerm.contains("d"))
                    fichier.setAutherDelete(true);
            }

            if(fDao.updateDroits(fichier)){
                System.out.println(file + " : " + ancientPerm + " -> " + fichier.getPersmission());
                return ;
            }
        }
        System.out.println("vous n'avez pas la permission de modifier les permssions: " + file);
    }

}