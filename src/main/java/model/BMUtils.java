package model;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

public class BMUtils {

    private static final Logger logger = LoggerFactory.getLogger(BMUtils.class);

    private static final String CHERNO_SERVER_LINK = "https://www.battlemetrics.com/servers/arma3/1447962";
    private static final int UPDATE_TIME = 900_000; //15 min

    private Document doc = null;
    private Calendar documentTime = null;

    private Document getServerInfo() {
        if (!checkDocumentTime(documentTime)) {//document live more than 15 minutes
            doc = null;
            try {
                doc = Jsoup.connect(CHERNO_SERVER_LINK)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.132 Safari/537.36 OPR/67.0.3575.79")
                        .referrer("no-referrer-when-downgrade")
                        .get();
            } catch (IOException e) {
                logger.error(Arrays.toString(e.getStackTrace()));
            }
            if (doc != null) {
                documentTime = Calendar.getInstance(Locale.getDefault());
            }
        }
        return doc;
    }

    public boolean isOnline(String playerName) {
        Document doc = getServerInfo();

        if (doc != null) {
            Elements activePlayers = doc.getElementsByAttributeValue("class", "col-md-8");
            Elements elements = activePlayers.get(0).getElementsByAttribute("href");

            if (elements.size() != 0) {
                for (Element e : elements) {
                    if (e.text().equals(playerName)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public String getBMID(Player player) {
        Document BM = getServerInfo();

        Elements activePlayers = BM.getElementsByAttributeValue("class", "col-md-8");
        Elements elements = activePlayers.get(0).getElementsByAttribute("href");
        if (elements.size() != 0) {
            for (Element e : elements) {
                if (e.text().equals(player.getTempNickName())) {
                    String id = e.attr("href");
                    id =  id.substring(id.lastIndexOf('/') + 1);
                    return id;
                }
            }
        }
        return "0";
    }

    //return true if current document live less than 15 minutes
    //false - old doc  true - new doc
    private boolean checkDocumentTime(Calendar documentTime) {
        if (documentTime == null) return false;

        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        long _15_minutes = documentTime.getTimeInMillis() + UPDATE_TIME; //15 minutes

        return _15_minutes > calendar.getTimeInMillis();
    }
}
