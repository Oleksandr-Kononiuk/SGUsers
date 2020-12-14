package view;

import model.BMUtils;
import model.Player;

import java.util.List;

public class ConsoleHelper {
    private BMUtils battleMetrics = new BMUtils();

    public void printPlayer(Player p) {
        System.out.println(String.format("Player {" +
                        " tempNickName='%-15.15s' " +
                        "mainNickName='%-15.15s' " +
                        "family='%-15.15s' " +
                        "isAdmin=%b, SGID=%s " +
                        "BattleMetricID=%-15s " +
                        "Profile link=%s",
                p.getTempNickName(), p.getMainNickName(), p.getFamily(),
                p.isAdmin(), p.getSGID(), p.getBMID(), p.getProfileLink()));
    }

    public void printPlayerWithOnlineStatus(Player p) {
        System.out.println(String.format("Player {" +
                        " tempNickName='%-15.15s' " +
                        "mainNickName='%-15.15s' " +
                        "family='%-10.10s' " +
                        "isAdmin=%b, " +
                        "isOnline=%b, " +
                        "SGID=%s " +
                        "BattleMetricID=%-15s " +
                        "Profile link=%s",
                p.getTempNickName(), p.getMainNickName(), p.getFamily(), p.isAdmin(),
                battleMetrics.isOnline(p.getTempNickName()), p.getSGID(), p.getBMID(), p.getProfileLink()));
    }

    public void printFamily(List<Player> f) {
        StringBuilder out = new StringBuilder(String.format("Family '%-15.15s' size= %d {%n", f.get(0).getFamily(), f.size()));
        for (Player p : f) {
            out.append("  [ " +
                    String.format("tempNickName='%-15.15s' " +
                                    "mainNickName='%-15.15s' " +
                                    "isAdmin=%b, " +
                                    "SGID=%s " +
                                    "BattleMetricID=%-15s " +
                                    "Profile link=%s",
                            p.getTempNickName(), p.getMainNickName(), p.getFamily(),
                            p.isAdmin(), p.getSGID(), p.getBMID(), p.getProfileLink()) +
                    " ]\n");
        }
        out.append("}");
        System.out.println(out.toString());
    }

    public void printFamilyWithOnline(List<Player> f) {
        StringBuilder out = new StringBuilder("Family \'" + f.get(0).getFamily() + "\' size= " + f.size() + " {\n");
        for (Player p : f) {
            out.append("  [ " +
                    String.format("tempNickName='%-15.15s' " +
                                    "mainNickName='%-15.15s' " +
                                    "family='%-15.15s' " +
                                    "isAdmin=%b, " +
                                    "isOnline=%b, " +
                                    "SGID=%s " +
                                    "BattleMetricID=%-15s " +
                                    "Profile link=%s",
                            p.getTempNickName(), p.getMainNickName(), p.getFamily(), p.isAdmin(),
                            battleMetrics.isOnline(p.getTempNickName()), p.getSGID(), p.getBMID(), p.getProfileLink()) +
                    " ]\n");
        }
        out.append("}");
        System.out.println(out.toString());
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
