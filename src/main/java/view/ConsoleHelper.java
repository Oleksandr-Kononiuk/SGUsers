package view;

import model.BMUtils;
import model.Player;

import java.util.List;

public class ConsoleHelper {
    private BMUtils battleMetrics = new BMUtils();

    public void printPlayer(Player p) {
        System.out.println("Player {" +
                " tempNickName='" + p.getTempNickName() + '\'' +
                ", mainNickName='" + p.getMainNickName() + '\'' +
                ", family='" + p.getFamily() + '\'' +
                ", isAdmin=" + p.isAdmin() +
                ", SGID=" + p.getSGID() +
                ", BattleMetricID=" + p.getBMID() +
                ", profileLink=" + p.getProfileLink() +
                '}');
    }

    public void printPlayerWithOnlineStatus(Player p) {
        System.out.println("Player {" +
                " tempNickName='" + p.getTempNickName() + '\'' +
                ", mainNickName='" + p.getMainNickName() + '\'' +
                ", family='" + p.getFamily() + '\'' +
                ", isAdmin=" + p.isAdmin() +
                ", isOnline=" + battleMetrics.isOnline(p.getTempNickName()) +
                ", SGID=" + p.getSGID() +
                ", BattleMetricID=" + p.getBMID() +
                ", profileLink=" + p.getProfileLink() +
                '}');
    }

    public void printFamily(List<Player> f) {
        if (f.size() > 0) {
            StringBuilder out = new StringBuilder("Family \'" + f.get(0).getFamily() + "\' size= " + f.size() + " {\n");
            for (Player p : f) {
                out.append("     [ tempNickName= '" + p.getTempNickName() + '\'' +
                        ", mainNickName= '" + p.getMainNickName() + '\'' +
                        ", isAdmin= " + p.isAdmin() +
                        ", SGID=" + p.getSGID() +
                        ", BattleMetricID=" + p.getBMID() +
                        ", SG link=" + p.getProfileLink() + " ]\n");
            }
            out.append("}");
            System.out.println(out.toString());
        } else {
            System.out.println("No family!");
        }
    }

    public void printFamilyWithOnline(List<Player> f) {
        if (f.size() > 0) {
            StringBuilder out = new StringBuilder("Family \'" + f.get(0).getFamily() + "\' size= " + f.size() + " {\n");
            for (Player p : f) {
                out.append("    [ tempNickName= '" + p.getTempNickName() + '\'' +
                        ", mainNickName= '" + p.getMainNickName() + '\'' +
                        ", isAdmin= " + p.isAdmin() +
                        ", isOnline=" + battleMetrics.isOnline(p.getTempNickName()) +
                        ", SGID=" + p.getSGID() +
                        ", BattleMetricID=" + p.getBMID() +
                        ", SG link=" + p.getProfileLink() + " ]\n");

            }
            out.append("}");
            System.out.println(out.toString());
        } else {
            System.out.println("No family!");
        }
    }

    public void printMessage(String s) {
        System.out.println(s);
    }

    public void printMessage(String s, Integer i) {
        System.out.printf(s, i);
        System.out.println();
    }

    public void printMessage(String s, String s1) {
        System.out.printf(s, s1);
        System.out.println();
    }

    public void printMessage(String s, String s1, String s2) {
        System.out.printf(s, s1, s2);
        System.out.println();
    }
}
