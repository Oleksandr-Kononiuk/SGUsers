package model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

//Entity for DAO pattern
@XmlType(name = "player")   //xml
@JsonAutoDetect             //json
public class Player implements Serializable {

    public static final String BM_DEFAULT_ID = "0";

    @JsonProperty("current_nickname")           //json
    private String tempNickName;
    @JsonProperty("main_nickname")              //json
    private String mainNickName;
    private String family;
    @JsonProperty("profile_link")               //json
    private String profileLink;
    private String SGID;
    private String BMID = BM_DEFAULT_ID;
    private boolean isAdmin;

    private static final long serialVersionUID = 4L;

    /*
    Constructor, Getters and Setters
    */

    public Player() {}

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

    @XmlElement(name = "current_nickname")                      //xml
    public String getTempNickName() {
        return tempNickName;
    }

    @XmlElement(name = "main_nickname")                         //xml
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
