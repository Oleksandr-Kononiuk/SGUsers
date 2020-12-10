package model;

import java.io.Serializable;

public class Player implements Serializable {

    public static final String BM_DEFAULT_ID = "0";

    private String tempNickName;
    private String mainNickName;
    private String family;
    private String profileLink;
    private String SGID;
    private String BMID = BM_DEFAULT_ID;
    private boolean isAdmin;

    private static final long serialVersionUID = 4L;

    /*
    Constructor, Getters and Setters
    */

    public void setProfileLink(String profileLink) {
        this.profileLink = profileLink;
    }

    public void setTempNickName(String tempNickName) {
        this.tempNickName = tempNickName;
    }

    public void setMainNickName(String mainNickName) {
        this.mainNickName = mainNickName;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    public void setSGID(String SGID) {
        this.SGID = SGID;
    }

    public void setBMID(String BMID) {
        this.BMID = BMID;
    }

    public void setAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public String getProfileLink() {
        return profileLink;
    }

    public String getTempNickName() {
        return tempNickName;
    }

    public String getMainNickName() {
        return mainNickName;
    }

    public String getFamily() {
        return family;
    }

    public String getSGID() {
        return SGID;
    }

    public String getBMID() {
        return BMID;
    }

    public boolean isAdmin() {
        return isAdmin;
    }
}
