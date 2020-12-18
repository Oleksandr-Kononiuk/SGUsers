package model;

import java.util.List;

//DAO
public interface Model {

    void addNewPlayer(String SGProfileLink);

    void deletePlayer(String nickNameOrBMID);

    void searchPlayer(String nameOrLinkOrId);

    void updatePlayer(String nameOrLinkOrId);

    void printAllPlayers();

    void addAdmin(String name);

    void setAdmin(String nickNameOrBMID);

    void deleteAdmin(String nickNameOrBMID);

    void getBMID(String nickName);

    /**
     * Family methods
     */

    void deleteFamily(String familyName);

    void updateFamily(String oldFamilyName);

    void printFamily(String familyName);

    void printAllFamilies();

    void topFamilies();

    void searchAllAdmins();

    void totals();

    /**
     * DB methods
     */

    void clearAll();

    void backup();

    void writeToDB();

    void readFromDB();

    void fillPlayers(String cmdArgumentN1);

    void setPlayers(List<Player> players);
}
