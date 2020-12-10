package model;

import view.ConsoleHelper;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

public class TxtModel implements Externalizable, Model {
    private List<Player> players = new ArrayList<>();
    private ConsoleHelper view = new ConsoleHelper();

    private SGUtils SG = new SGUtils();
    private BMUtils BattleMetrics = new BMUtils();

    private static final String CHERNO_DB_PATH = "src/main/java/model/db/txt/cherno.txt";
    private static final String CHERNO_BACKUP_PATH = "src/main/java/model/db/txt/backup/cherno.txt";

    //private static final String ALTIS_DB_PATH = "src\\db\\altis_db.txt";
    //private static final String ALTIS_BACKUP_PATH = "src\\backup\\altis_backup.txt";

    //private static final String CHERNO_BM_SERVER_LINK = "https://www.battlemetrics.com/servers/arma3/1447962";
    //private static final String ALTIS_BM_SERVER_LINK = "https://www.battlemetrics.com/servers/arma3/2364650";

    private static String CURRENT_DB_PATH = CHERNO_DB_PATH;
    private static String CURRENT_BACKUP_PATH = CHERNO_BACKUP_PATH;

    private static final long serialVersionUID = 4L;

    public TxtModel() {
    }

    /**
     * Player methods
     */

    public void addNewPlayer(String SGProfileLink) {
        try {
            String SGID = SGProfileLink.substring(SGProfileLink.length() - 17);
            Player newPlayer = SG.getNewPlayer(SGID);

            if (newPlayer == null) {
                view.printMessage("Player '%s' not founded.", SGProfileLink);
            } else {
                players.add(newPlayer);
                view.printMessage("New player '%s' was added.", newPlayer.getTempNickName());
                writeToDB();
            }
        } catch (StringIndexOutOfBoundsException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void deletePlayer(String nickNameOrBMID) {
        Player p;

        if ((p = getPlayer(nickNameOrBMID)) != null) {
            view.printMessage("Player '%s' was deleted from data base.", p.getTempNickName());
            players.remove(p);
            writeToDB();
        } else {
            view.printMessage("Player wasn`t deleted from data base or not found.");
        }
    }

    public void updatePlayer(String nameOrLinkOrId) {
        Player p = getPlayer(nameOrLinkOrId);
        updatePlayer(p);
    }

    public void searchPlayer(String nameOrLinkOrId) {
        List<Player> s = new ArrayList<>();

        if (players.size() == 0) {
            view.printMessage("Players data base is empty.");
        } else {
            for (Player p : players) {

                if (nameOrLinkOrId.contains("spartangaming.co.uk") && p.getProfileLink().equals(nameOrLinkOrId)) {  //searching by SG profile link
                    s.add(p);
                } else if (nameOrLinkOrId.matches("\\d{17}") && p.getSGID().equals(nameOrLinkOrId)) {  //searching by SG ID
                    s.add(p);
                } else if (p.getTempNickName().startsWith(nameOrLinkOrId)) {  //searching by temp nick name
                    updatePlayer(p);
                    s.add(p);
                }
            }
        }
        if (s.size() == 0) {
            view.printMessage("Player '%s' not found", nameOrLinkOrId);
        } else {
            for (Player p : s)
                view.printPlayerWithOnlineStatus(p);
        }
    }

    public void printAllPlayers() {
        if (players.size() == 0) {
            view.printMessage("Players data base is empty.");
        } else {
            view.printMessage("Players count = '%d'.", players.size());
            for (Player p : players) {
                view.printPlayer(p);
            }
        }
    }

    public void addAdmin(String name) {
        Player admin = new Player();
        admin.setAdmin(true);
        admin.setMainNickName(name);
        admin.setTempNickName(name);

        players.add(admin);
        writeToDB();
        view.printMessage("Admin '%s' is created and added in database.", admin.getMainNickName());
    }

    public void setAdmin(String nickNameOrBMID) {
        Player p = getPlayer(nickNameOrBMID);
        if (p != null) {
            p.setAdmin(true);
            writeToDB();
            view.printMessage("Player '%s' is admin now.", p.getMainNickName());
        } else
            view.printMessage("Player '%s' not found.", nickNameOrBMID);
    }

    public void deleteAdmin(String nickNameOrBMID) {
        Player p = getPlayer(nickNameOrBMID);
        if (p != null) {
            p.setAdmin(false);
            writeToDB();
            view.printMessage("Player '%s' is not admin now.", p.getMainNickName());
        } else
            view.printMessage("Player '%s' not found", nickNameOrBMID);
    }

    public void getBMID(String nickNameOrBMID) {
        Player player = getPlayer(nickNameOrBMID);
        if (player != null) {
            if (BattleMetrics.isOnline(player.getTempNickName())) {
                player.setBMID(BattleMetrics.getBMID(player));
                writeToDB();
                view.printMessage("'%s' battlemetrics id changed on %s", player.getTempNickName(), player.getBMID());
            } else {
                view.printMessage("Player '%s' must be online", nickNameOrBMID);
            }
        } else {
            view.printMessage("Player '%s' not found", nickNameOrBMID);
        }
    }

    private Player getPlayer(String nameOrLinkOrId) {
        for (Player p : players) {
            if (nameOrLinkOrId.contains("https://spartangaming.co.uk") && p.getProfileLink().startsWith(nameOrLinkOrId)) {
                return p;
            } else if (nameOrLinkOrId.matches("^\\d+$") && p.getBMID().equals(nameOrLinkOrId)) {
                return p;
            } else if (nameOrLinkOrId.matches("\\d{17}$") && p.getSGID().equals(nameOrLinkOrId)) {
                return p;
            } else if (p.getTempNickName().toLowerCase().startsWith(nameOrLinkOrId.toLowerCase())) {
                return p;
            }
        }
        return null;
    }

    private void updatePlayer(Player p) {
        SG.updatePlayer(p);
        writeToDB();
    }

    /**
     * Family methods
     */

    public void deleteFamily(String familyName) {
        List<Player> f = getFamilyMembers(familyName);
        if (f.size() > 0) {
            players.removeAll(f);
            view.printMessage("Family '%s' was deleted.", familyName);
            writeToDB();
        } else {
            view.printMessage("Family '%s' was`n founded in data base.", familyName);
        }
    }

    public void updateFamily(String familyName) {
        List<Player> f = getFamilyMembers(familyName);

        if (f.size() > 0) {
            for (Player m : f) {
                SG.updatePlayer(m);
            }
            writeToDB();
            view.printMessage("Players with family name starting on '%s' was updated.", familyName);
        } else {
            view.printMessage("Family '%s' not found.", familyName);
        }
    }

    //print family with online status of members
    public void printFamily(String familyName) {
        List<Player> f = getFamilyMembers(familyName);
        if (f.size() > 0) {
            for (Player m : f) {
                SG.updatePlayer(m);
            }
            view.printFamilyWithOnline(f);
        } else {
            view.printMessage("Family '%s' not found.", familyName);
        }
    }

    //print all family without online status of members
    public void printAllFamilies() {
        Set<String> familiesName = new HashSet<>();

        for (Player p : players) {
            familiesName.add(p.getFamily());
        }
        view.printMessage("Families count = '%d'.", familiesName.size());

        for (String s : familiesName) {
            view.printFamily(getFamilyMembers(s));
        }
    }

    public void topFamilies() {
        int topTen = 10;
        for (Map.Entry<List<Player>, String> entry : getAllFamilies().entrySet()) {
            if (topTen == 1) break;
            view.printFamily(entry.getKey());
            topTen--;
        }
    }

    public void searchAllAdmins() {
        List<Player> admins = getAdmins();

        if (admins.size() != 0) {
            for (Player admin : admins) {
                view.printPlayerWithOnlineStatus(admin);
            }
        } else {
            view.printMessage("Admins list is empty.");
        }
    }

    public void totals() {
        view.printMessage("Total players in data base is '%d'.", players.size());
        view.printMessage("Total families in data base is '%d'.", getAllFamilies().size());
        view.printMessage("Total admins in data base is '%d'.", getAdmins().size());
    }

    private List<Player> getAdmins() {
        List<Player> admins = new ArrayList<>();
        for (Player player : players) {
            if (player.isAdmin()) {
                admins.add(player);
            }
        }
        return admins;
    }

    private List<Player> getFamilyMembers(String familyName) {
        List<Player> f = new ArrayList<>();

        if (players.size() > 0) {
            for (Player p : players) {
                if (p.getFamily().equals(familyName)) {
                    f.add(p);
                }
            }
        } else {
            view.printMessage("Data base is empty.");
        }
        return f;
    }

    private Map<List<Player>, String> getAllFamilies() {
        Set<String> familiesName = new HashSet<>();
        Map<List<Player>, String> families = new TreeMap<>((o1, o2) -> {
            if (o1.size() < o2.size()) {
                return -1;
            } else if (o1.size() > o2.size()) {
                return 1;
            } else
                return 0;
        });

        for (Player p : players) {
            familiesName.add(p.getFamily());
        }
        for (String s : familiesName) {
            families.put(getFamilyMembers(s), s);
        }
        return families;
    }

    /**
     * DB methods
     */

    public void clearAll() {
        players.clear();
        view.printMessage("Database was cleared.");
        //writeToDB(); //todo when all will works fine, delete comment
    }

    public void backup() {
        try (ObjectInputStream objectInputStream =
                     new ObjectInputStream(new FileInputStream(CURRENT_BACKUP_PATH))) {

            players = (List<Player>) objectInputStream.readObject();

            view.printMessage("Database is updated from backup.");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void writeToDB() {
        //create backup
        try {
            Files.copy(Paths.get(CURRENT_DB_PATH), Paths.get(CURRENT_BACKUP_PATH), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (ObjectOutputStream objectOutputStream =
                     new ObjectOutputStream(new FileOutputStream(CURRENT_DB_PATH, false))) {

            objectOutputStream.writeObject(players);
            objectOutputStream.flush();
            view.printMessage("Database are updated.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readFromDB() {
        try (ObjectInputStream objectInputStream =
                     new ObjectInputStream(new FileInputStream(CURRENT_DB_PATH))) {

            players = (List<Player>) objectInputStream.readObject();

            view.printMessage("Database was read success.");
        } catch (IOException | ClassNotFoundException e) {
            view.printMessage("Data base was not found. " +
                    "Please use command !fill-players and !rebuild-families to create new Data base.");
            e.printStackTrace();
        }
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(players);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.players = (List<Player>) in.readObject();
    }

    /**
     * Geters and Seters
     */
    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public List<Player> getPlayers() {
        return players;
    }
}