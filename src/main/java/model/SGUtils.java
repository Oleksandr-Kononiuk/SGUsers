package model;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class SGUtils {

    //get info about new player, create player and return
    public Player getNewPlayer(String id) {
        String profileLink = "https://spartangaming.co.uk/stats/userLeaderboard.php?uid=" + id; //76561198254625518
        Player player = null;
        Document SGUser = getDocument(profileLink);

        if (SGUser != null) {
            player = new Player();
            Elements elements = SGUser.getElementsByTag("td");
            elements.addAll(SGUser.getElementsByTag("h1"));
//todo maybe can do some optimization.

            if (elements.size() != 0) {
                for (int i = 0; i < elements.size(); i++) {
                    if (elements.get(i).text().equals("FAMILY")) {
                        player.setFamily(elements.get(i + 1).text());
                    }
                    player.setTempNickName(elements.get(elements.size() - 1).text());
                    player.setMainNickName(elements.get(elements.size() - 1).text());
                    player.setProfileLink(profileLink);
                    player.setSGID(id);
                }
            }
        }
        return player;
    }

    //updating some Player info and returning the same updated Player
    public void updatePlayer(Player oldPlayer) {
        Player player = getNewPlayer(oldPlayer.getSGID());
        if (player != null) {
            oldPlayer.setFamily(player.getFamily());
            oldPlayer.setTempNickName(player.getTempNickName());
        }
    }

    //get all players form 0 to page and return List of players
    public List<Player> getUsersFromPages(int page) {
        List<Player> players = new ArrayList<>();

        while(page > 0) {
            String pageLink = "https://spartangaming.co.uk/stats/playerStats.php?pageno=" + page;//https://spartangaming.co.uk/stats/playerStats.php?pageno=2
            Document SGUser = getDocument(pageLink);

            if (SGUser != null) {
                Elements elements = SGUser.select("a[href]");

                if (elements.size() != 0) {
                    for (int i = 9; i < elements.size() - 7; i++) {
                        String element = elements.get(i).toString();
                        int IDStart = element.lastIndexOf("=");
                        int IDEnd = element.lastIndexOf("\">");
                        String SGID = element.substring(IDStart + 1, IDEnd);
                        Player player = getNewPlayer(SGID);
                        players.add(player);
                    }
                }
                System.out.println("Got all players from page № " + page);
                page--;
            }
        }
        return players;
    }

//    //todo incorrect works
//    public List<Player> getUsersFromFile(File file) {
//        List<Player> players = new ArrayList<>();
//
//        if (file != null) {
//            Document SGUser = null;
//            try {
//                SGUser = Jsoup.parse(file, Charset.defaultCharset().name());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            if (SGUser != null) {
//                Elements link = SGUser.select("a[href]");
//
//                if (link.size() != 0) {
//                    for (int i = 9; i < link.size() - 7; i++) {
//                        String element = link.get(i).toString();
//                        int IDStart = element.lastIndexOf("=");
//                        int IDEnd = element.lastIndexOf("\">");
//                        String SGID = element.substring(IDStart + 1, IDEnd);
//                        Player player = getNewPlayer(SGID);
//                        players.add(player);
//                    }
//                    System.out.printf("Got all player from file '%s'.\n", file.getName());
//                }
//            }
//        }
//        return players;
//    }

    //get Document from source using JSoup lib
    private Document getDocument(String source) {
        Document doc = null;
        try {
            doc = Jsoup.connect(source)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.198 Safari/537.36 OPR/72.0.3815.400")
                    .referrer("no-referrer-when-downgrade")
                    .get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return doc;
    }
}
