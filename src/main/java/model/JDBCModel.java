package model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import view.ConsoleHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.*;

public class JDBCModel implements Model {
    private String DB_USER = "root";
    private String DB_PASSWORD = "1111";
    private String DB_URL = "jdbc:mysql://localhost:3306/sgusers?serverTimezone=UTC";

    private static final Logger logger = LoggerFactory.getLogger(JDBCModel.class);
    private ConsoleHelper view = new ConsoleHelper();
    private SGUtils SG = new SGUtils();
    private BMUtils BattleMetrics = new BMUtils();
    private Connection connection = getConnection();

    private Connection getConnection() {
//        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
//        try {
//            DB_USER = reader.readLine();
//            DB_PASSWORD = reader.readLine();
//            DB_URL = reader.readLine();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        Connection connection = null;
        try {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            view.printMessage("Connection to database has been created.");
        } catch (SQLException e) {
            e.printStackTrace();
            //logger.error(Arrays.toString(e.getStackTrace()));
        }
        return connection;
    }

    @Override
    public void addNewPlayer(String SGID) {
        Player newPlayer = SG.getNewPlayer(SGID);

        if (newPlayer != null) {
            Savepoint sp = null;
            String sql = ("INSERT INTO player(sgid, tempNickName, mainNickName, family, isAdmin, bmid, profileLink) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?)");

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                connection.setAutoCommit(false);
                sp = connection.setSavepoint("Before_adding_new_player");

                statement.setString(1, newPlayer.getSGID());
                statement.setString(2, newPlayer.getTempNickName());
                statement.setString(3, newPlayer.getMainNickName());
                statement.setString(4, newPlayer.getFamily());
                statement.setBoolean(5, newPlayer.isAdmin());
                statement.setString(6, newPlayer.getBMID());
                statement.setString(7, newPlayer.getProfileLink());

                int isAdded = statement.executeUpdate();

                if (isAdded > 0) {
                    commit(sp);
                    view.printMessage("New player '%s' was added.", newPlayer.getTempNickName());
                } else {
                    view.printMessage("New player '%s' wasn`t added.", SGID);
                }

            } catch (SQLIntegrityConstraintViolationException e) {
                updatePlayer(newPlayer.getSGID());
            } catch (SQLException e) {
                rollback(sp);
                logger.error(Arrays.toString(e.getStackTrace()));
            }
        } else {
            view.printMessage("Player '%s' not founded.", SGID);
        }
    }

    @Override
    public void deletePlayer(String SGID) { //todo player name have 2+ words //todo refactor using PreparedStatement
        Savepoint sp = null;
        String sql = "DELETE FROM player WHERE sgid = ? LIMIT 1";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            connection.setAutoCommit(false);
            sp = connection.setSavepoint("Before_deleting_player");

            statement.setString(1, SGID);

            int isDeleted = statement.executeUpdate();

            if (isDeleted > 0) {
                commit(sp);
                view.printMessage("Player '%s' was deleted from data base.", SGID);
            } else {
                view.printMessage("Player wasn`t deleted from data base or not found.");
            }

        } catch (SQLException e) {
            rollback(sp);
            logger.error(Arrays.toString(e.getStackTrace()));
        }
    }

    @Override
    public void searchPlayer(String tempNickOrSGID) {
        String sql = "SELECT sgid, tempNickName, mainNickName, family, isAdmin, bmid, profileLink " +
                "FROM player " +
                "WHERE SGID = ? OR tempNickName = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, tempNickOrSGID);
            statement.setString(2, tempNickOrSGID);

            ResultSet resultSet = statement.executeQuery();
            List<Player> foundPlayers = new ArrayList<>();

            while (resultSet.next()) {
                Player player = new Player();

                player.setSGID(resultSet.getString("sgid"));
                player.setTempNickName(resultSet.getString("tempNickName"));
                player.setMainNickName(resultSet.getString("mainNickName"));
                player.setFamily(resultSet.getString("family"));
                player.setAdmin(resultSet.getBoolean("isAdmin"));
                player.setBMID(resultSet.getString("bmid"));
                player.setProfileLink(resultSet.getString("profileLink"));

                foundPlayers.add(player);
            }
            if (foundPlayers.size() > 0) {
                for (Player p : foundPlayers) {
                    view.printPlayerWithOnlineStatus(p);
                }
            } else {
                view.printMessage("Player '%s' not found.", tempNickOrSGID);
            }
        } catch (SQLException e) {
            logger.error(Arrays.toString(e.getStackTrace()));
        }
    }

    @Override
    public void updatePlayer(String SGID) { //todo update admin temp nickname. This can be do only using battlemetrics by BM id
        Player player = SG.getNewPlayer(SGID);
        Savepoint sp = null;
        String updateSQL = "UPDATE player " +
                        "SET tempNickName = ?, mainNickName = ?, family = ?, isAdmin = ?, bmid = ?, profileLink = ? " +
                        "WHERE sgid = ?;";

        try (PreparedStatement statement = connection.prepareStatement(updateSQL)) {
            connection.setAutoCommit(false);
            sp = connection.setSavepoint("Before_updating_player");

            statement.setString(1, player.getTempNickName());
            statement.setString(2, player.getMainNickName());
            statement.setString(3, player.getFamily());
            statement.setBoolean(4, player.isAdmin());
            statement.setString(5, player.getBMID());
            statement.setString(6, player.getProfileLink());
            statement.setString(7, player.getSGID());

            int isUpdated = statement.executeUpdate();

            if (isUpdated > 0) {
                commit(sp);
                view.printMessage("Player '%s' was updated because he is exists in database.", player.getTempNickName());
            } else {
                view.printMessage("New player '%s' wasn`t updated.", SGID);
            }
        } catch (SQLException e) {
            rollback(sp);
            logger.error(Arrays.toString(e.getStackTrace()));
        }
    }

    @Override
    public void printAllPlayers() {
        String sql = "SELECT sgid, tempNickName, mainNickName, family, isAdmin, bmid, profileLink " +
                    "FROM player";

        try (Statement statement = connection.createStatement()) {

            ResultSet resultSet = statement.executeQuery(sql);
            List<Player> foundPlayers = new ArrayList<>();

            while (resultSet.next()) {
                Player player = new Player();

                player.setSGID(resultSet.getString("sgid"));
                player.setTempNickName(resultSet.getString("tempNickName"));
                player.setMainNickName(resultSet.getString("mainNickName"));
                player.setFamily(resultSet.getString("family"));
                player.setAdmin(resultSet.getBoolean("isAdmin"));
                player.setBMID(resultSet.getString("bmid"));
                player.setProfileLink(resultSet.getString("profileLink"));

                foundPlayers.add(player);
            }
            for (Player p : foundPlayers) {
                view.printPlayer(p);
            }
        } catch (SQLException e) {
            logger.error(Arrays.toString(e.getStackTrace()));
        }
    }

    @Override
    public void addAdmin(String name) {
        //creating random SG id because its can not be get from SG site
        final String digits = "0123456789";
        StringBuilder adminRandomSGID = new StringBuilder("0000000000000");
        for(int i = 0; i < 4; i++ ) {
            adminRandomSGID.append(digits.charAt(new Random().nextInt(digits.length())));
        }
        Savepoint sp = null;
        String sql = "INSERT INTO player(sgid, tempNickName, mainNickName, family, isAdmin, bmid, profileLink) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            connection.setAutoCommit(false);
            sp = connection.setSavepoint("Before_adding_admin");

            statement.setString(1, adminRandomSGID.toString());
            statement.setString(2, name);
            statement.setString(3, name);
            statement.setString(4, "[Null]");
            statement.setBoolean(5, true);
            statement.setString(6, "0");
            statement.setString(7, "[Null]");

            int isAdded = statement.executeUpdate();

            if (isAdded > 0) {
                commit(sp);
                view.printMessage("New admin '%s' was added.", name);
            } else {
                view.printMessage("New player '%s' wasn`t added.", name);
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            logger.warn("SG id of new admin are exist in database. Trying to recursive call addAdmin(String name).");
            addAdmin(name);
        } catch (SQLException e) {
            rollback(sp);
            logger.error(Arrays.toString(e.getStackTrace()));
        }
    }

    @Override
    public void setAdmin(String nickNameOrSGID) {
        Savepoint sp = null;
        String sql = "UPDATE player " +
                        "SET isAdmin = true " +
                        "WHERE sgid = ? OR tempNickName = ?;";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            connection.setAutoCommit(false);
            sp = connection.setSavepoint("Before_changing_admin_status");

            statement.setString(1, nickNameOrSGID);
            statement.setString(2, nickNameOrSGID);

            int isAdmin = statement.executeUpdate();

            if (isAdmin > 0) {
                commit(sp);
                view.printMessage("Player '%s' is admin now.", nickNameOrSGID);
            } else {
                view.printMessage("Player '%s' not found.", nickNameOrSGID);
            }
        } catch (SQLException e) {
            rollback(sp);
            logger.error(Arrays.toString(e.getStackTrace()));
        }
    }

    @Override
    public void deleteAdmin(String nickNameOrSGID) {
        Savepoint sp = null;
        String sql = "UPDATE player " +
                "SET isAdmin = false " +
                "WHERE sgid = ? OR tempNickName = ?;";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            connection.setAutoCommit(false);
            sp = connection.setSavepoint("Before_changing_admin_status");

            statement.setString(1, nickNameOrSGID);
            statement.setString(2, nickNameOrSGID);

            int isAdmin = statement.executeUpdate();

            if (isAdmin > 0) {
                commit(sp);
                view.printMessage("Player '%s' is not admin now.", nickNameOrSGID);
            } else {
                view.printMessage("Player '%s' not found.", nickNameOrSGID);
            }
        } catch (SQLException e) {
            rollback(sp);
            logger.error(Arrays.toString(e.getStackTrace()));
        }
    }

    @Override
    public void getBMID(String nickName) {
        if (BattleMetrics.isOnline(nickName)) {
            String bmid = BattleMetrics.getBMID(nickName);
            Savepoint sp = null;
            String sql = "UPDATE player " +
                    "SET bmid = ? " +
                    "WHERE tempNickName = ?;";

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                connection.setAutoCommit(false);
                sp = connection.setSavepoint("Before_changing_bmid");

                statement.setString(1, bmid);
                statement.setString(2, nickName);

                int isChanged = statement.executeUpdate();

                if (isChanged > 0) {
                    commit(sp);
                    view.printMessage("Player '%s' BMID has been changed to '%s'", nickName, bmid);
                } else {
                    view.printMessage("Player '%s' not found.", nickName);
                }
            } catch (SQLException e) {
                rollback(sp);
                logger.error(Arrays.toString(e.getStackTrace()));
            }
        } else {
            view.printMessage("Player '%s' must be online", nickName);
        }
    }
    //todo family name have 2+ words
    @Override
    public void deleteFamily(String familyName) {
        Savepoint sp = null;
        String sql = "DELETE FROM player " +
                    "WHERE family = ?;";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            connection.setAutoCommit(false);
            sp = connection.setSavepoint("Before_deleting_family");

            statement.setString(1, familyName);

            int isDeleted = statement.executeUpdate();

            if (isDeleted > 0) {
                commit(sp);
                view.printMessage("Players from family '%s' was deleted from data base.", familyName);
            } else {
                view.printMessage("Family wasn`t deleted from data base or not found.");
            }
        } catch (SQLException e) {
            rollback(sp);
            logger.error(Arrays.toString(e.getStackTrace()));
        }
    }

    @Override
    public void updateFamily(String familyName) {
        for (Player p : getFamilyMembers(familyName)) {
            updatePlayer(p.getSGID());
        }
    }

    @Override
    public void printFamily(String familyName) {
        view.printFamilyWithOnline(getFamilyMembers(familyName));
    }

    @Override
    public void printAllFamilies() {
        Map<String, List<Player>> families = getAllFamilies();
        view.printMessage("Families count = '%d'.", families.size());

        for (Map.Entry<String, List<Player>> entry : families.entrySet()) {
            view.printFamily(entry.getValue());
        }
    }

    @Override
    public void topFamilies() {
        List<List<Player>> sortedList = new ArrayList<>();
        for (Map.Entry<String, List<Player>> entry : getAllFamilies().entrySet()) {
            sortedList.add(entry.getValue());
        }
        sortedList.sort(new Comparator<List<Player>>() {
            @Override
            public int compare(List<Player> o1, List<Player> o2) {
                if (o1.size() < o2.size()) {
                    return 1;
                } else if (o1.size() > o2.size()) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });
        int topTen = 10;
        for (List<Player> list : sortedList) {
            if (topTen == 0) break;
            view.printFamily(list);
            topTen--;
        }
    }

    @Override
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

    @Override
    public void totals() {
        int playersCount = 0;
        String sql = "SELECT tempNickName " +
                    "FROM player;";

        try (Statement statement = connection.createStatement()) {
            ResultSet rs = statement.executeQuery(sql);

            while (rs.next()) {
                playersCount++;
            }
        } catch (SQLException e) {
            logger.error(Arrays.toString(e.getStackTrace()));
        }

        view.printMessage("Total players in data base is '%d'.", playersCount);
        view.printMessage("Total families in data base is '%d'.", getAllFamilies().size());
        view.printMessage("Total admins in data base is '%d'.", getAdmins().size());
    }

    private List<Player> getAdmins() {
        List<Player> admins = new ArrayList<>();

        String sql = "SELECT sgid, tempNickName, mainNickName, family, isAdmin, bmid, profileLink " +
                    "FROM player " +
                    "WHERE isAdmin = true;";

        try (Statement statement = connection.createStatement()) {
            ResultSet rs = statement.executeQuery(sql);

            while (rs.next()) {
                Player admin = new Player();

                admin.setSGID(rs.getString("sgid"));
                admin.setTempNickName(rs.getString("tempNickName"));
                admin.setMainNickName(rs.getString("mainNickName"));
                admin.setFamily(rs.getString("family"));
                admin.setAdmin(rs.getBoolean("isAdmin"));
                admin.setBMID(rs.getString("bmid"));
                admin.setProfileLink(rs.getString("profileLink"));

                admins.add(admin);
            }
        } catch (SQLException e) {
            logger.error(Arrays.toString(e.getStackTrace()));
        }
        return admins;
    }

    private Map<String, List<Player>> getAllFamilies() {
        Set<String> familiesName = new HashSet<>();
        Map<String, List<Player>> familiesMemberList = new HashMap<>();

        String sql = "SELECT DISTINCT family " +
                    "FROM player;";

        try (Statement statement = connection.createStatement()) {
            ResultSet rs = statement.executeQuery(sql);

            while (rs.next()) {
                familiesName.add(rs.getString("family"));
            }
            for (String s : familiesName) {
                familiesMemberList.put(s, getFamilyMembers(s));
            }
        } catch (SQLException e) {
            logger.error(Arrays.toString(e.getStackTrace()));
        }
        return familiesMemberList;
    }

    private List<Player> getFamilyMembers(String familyName) {
        List<Player> members = new ArrayList<>();
        String getMembers = "SELECT sgid, tempNickName, mainNickName, family, isAdmin, bmid, profileLink " +
                            "FROM player " +
                            "WHERE family = ?;";

        try (PreparedStatement statement = connection.prepareStatement(getMembers)) {
            statement.setString(1, familyName);
            ResultSet rs = statement.executeQuery(getMembers);

            while (rs.next()) {
                Player player = new Player();

                player.setSGID(rs.getString("sgid"));
                player.setTempNickName(rs.getString("tempNickName"));
                player.setMainNickName(rs.getString("mainNickName"));
                player.setFamily(rs.getString("family"));
                player.setAdmin(rs.getBoolean("isAdmin"));
                player.setBMID(rs.getString("bmid"));
                player.setProfileLink(rs.getString("profileLink"));

                members.add(player);
            }
        } catch (SQLException e) {
            logger.error(Arrays.toString(e.getStackTrace()));
        }
        return members;
    }

    @Override
    public void clearAll() {
        Savepoint sp = null;
        String sql = "DELETE FROM player";
        try (Statement statement = connection.createStatement()) {
            connection.setAutoCommit(false);
            sp = connection.setSavepoint("Before_clean_all");

            int isDeleted = statement.executeUpdate(sql);

            if (isDeleted > 0) {
                commit(sp);
                view.printMessage("Database was cleared.");
            } else {
                view.printMessage("Database wasn`t cleared.");
            }
        } catch (SQLException e) {
            rollback(sp);
            logger.error(Arrays.toString(e.getStackTrace()));
        }
    }

    private void rollback(Savepoint sp) {
        try {
            connection.rollback(sp);
            connection.releaseSavepoint(sp);
            connection.setAutoCommit(true);
        } catch (SQLException ex) {
            logger.error(Arrays.toString(ex.getStackTrace()));
        }
    }

    private void commit(Savepoint sp) {
        try {
            connection.commit();
            connection.releaseSavepoint(sp);
            connection.setAutoCommit(true);
        } catch (SQLException ex) {
            logger.error(Arrays.toString(ex.getStackTrace()));
        }
    }

    @Override
    public void backup() {
        view.printMessage("Not supported for SQL database");
    }

    @Override
    public void writeToDB() {
        view.printMessage("Not supported for SQL database");
    }

    @Override
    public void readFromDB() {
        view.printMessage("Not supported for SQL database");
    }

    @Override
    public void fillPlayers(String cmdArgumentN1) {
        try {
            int pages = Integer.parseInt(cmdArgumentN1);
            List<Player> players = new SGUtils().getUsersFromPages(pages);

            String sql = "INSERT INTO player (sgid, tempNickName, mainNickName, family, isAdmin, bmid, profileLink) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";

            for (Player newPlayer : players) {
                try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

                    preparedStatement.setString(1, newPlayer.getSGID());
                    preparedStatement.setString(2, newPlayer.getTempNickName());
                    preparedStatement.setString(3, newPlayer.getMainNickName());
                    preparedStatement.setString(4, newPlayer.getFamily());
                    preparedStatement.setBoolean(5, newPlayer.isAdmin());
                    preparedStatement.setString(6, newPlayer.getBMID());
                    preparedStatement.setString(7, newPlayer.getProfileLink());

                    int isAdded = preparedStatement.executeUpdate();

                    if (isAdded > 0)
                        view.printMessage("New player '%s' was added.", newPlayer.getTempNickName());
                    else
                        view.printMessage("New player '%s' wasn`t added.", newPlayer.getTempNickName());

                } catch (SQLIntegrityConstraintViolationException e) {
                    updatePlayer(newPlayer.getSGID());
                    logger.warn("Duplicate entry for key 'player.PRIMARY'");
                } catch (SQLException e) {
                    logger.error(Arrays.toString(e.getStackTrace()));
                }
            }
            view.printMessage("List of players successfully completed.");
        } catch (NumberFormatException e) {
            view.printMessage("Wrong format of the page number '%s'. Please write integer page number.", cmdArgumentN1);
            logger.warn(Arrays.toString(e.getStackTrace()));
        }
    }

    @Override
    public void exit() {
        try {
            connection.close();
        } catch (SQLException e) {
            logger.warn(Arrays.toString(e.getStackTrace()));
        } finally {
            System.exit(0);
        }
    }

    @Override
    public void setPlayers(List<Player> players) {
        view.printMessage("Not supported for SQL database");
    }
}
