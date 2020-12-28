package controller;

import model.*;
import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import view.ConsoleHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Random;

public class Controller {
    private Model model = new JDBCModel();
    private ConsoleHelper view = new ConsoleHelper();

    private static final Logger logger = LoggerFactory.getLogger(Controller.class);

    public static void main(String[] args) {

        // Set up a simple configuration that logs on the console.
        BasicConfigurator.configure();

        Controller controller = new Controller();
        controller.getCommand();
    }

    private void getCommand() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            String c;
            do {
                c = reader.readLine();
                String[] m = c.split(" ");
                String command = m[0].toLowerCase();
                String cmdArgN1 = "";
                String cmdArgN2 = "";

                if (m.length > 1) {
                    cmdArgN1 = m[1];
                }
                if (m.length > 2) {
                    cmdArgN2 = m[2];
                }

                switch (command.toLowerCase()) {
                    //player commands
                    case "!add-player" : model.addNewPlayer(cmdArgN1); break;//++
                    case "!delete-player" : model.deletePlayer(cmdArgN1); break;//++
                    case "!update-player" : model.updatePlayer(cmdArgN1); break;//++
                    case "!add-admin" : model.addAdmin(cmdArgN1); break;//++
                    case "!set-admin" : model.setAdmin(cmdArgN1); break;//++
                    case "!delete-admin" : model.deleteAdmin(cmdArgN1); break;//++
                    case "!get-bmid" : model.getBMID(cmdArgN1); break;//++
                    case "!print-players" : model.printAllPlayers(); break;//++
                    case "!search-player" : model.searchPlayer(cmdArgN1); break; //++

                    //family commands
                    case "!delete-family" : model.deleteFamily(cmdArgN1); break;//++
                    case "!print-families" : model.printAllFamilies(); break;//+
                    case "!search-family" : model.printFamily(cmdArgN1); break;//++
                    case "!update-family" : model.updateFamily(cmdArgN1); break; //+
                    case "!top" : model.topFamilies(); break;//+
                    case "!admins" : model.searchAllAdmins(); break;//++
                    case "!totals" : model.totals(); break;//+

                    //db commands
                    case "!save" : model.writeToDB(); break;//++
                    case "!get" : model.readFromDB(); break;//++
                    case "!backup" : model.backup(); break;//++
                    case "!clear-all" : model.clearAll(); break;//++
                    case "!exit" : exit(); break;//+

                    //others commands
                    case "!fill-players" : model.fillPlayers(cmdArgN1); break;//+
                    case "!help" : help(); break;//+
                    default: view.printMessage("Wrong command format. Please try again or write '!exit' for close app.");
                }
            } while (!c.toLowerCase().equals("exit"));
        } catch (IOException e) {
            logger.error(Arrays.toString(e.getStackTrace()));
        }
    }

    private void help() {//todo more detail description
        System.out.println("List of commands with description\n" +
                "!add-player         - looking for a player using the link on the site passed to the method" +
                "                      and adds it to the database. For example: !add-player player_profile_link.\n" +
                "!delete-player      - removes a player from the database using the link to his profile or battlemetric ID.\n" +
                "!update-player      - update player info from spartan gaming. \n" +
                "!add-admin          - add new player like admin. After this command need set Battle Metrics ID. \n" +
                "!set-admin          - sets the admin flag to the player. Usually admins do not show on the site. " +
                "                      Such a player must be created manually.\n" +
                "!delete-admin       - removes a admin status from player.\n" +
                "!get-bmid           - get player Battle Metrics ID if he online. \n" +
                "!print-players      - to bring to the screen all the players in the database.\n" +
                "!search-player      - looks for a player in the database and displays it on the screen. " +
                "                      Search is carried out by the link to the profile " +
                "                      or by the first letters in the nickname or by spartangaming ID.\n" +
                "\n" +
                "!delete-family      - remove family from the base. Searches by the first letters in the name. " +
                "                      The first found one will be deleted.\n" +
                "!print-families     - to bring to the screen all the families in the database..\n" +
                "!search-family      - looking for the first letters of the family. Displays the first one found.\n" +
                "!update-family      - update family members info, like !update-player. \n" +

                "!top                - print top 10 families by their members count. \n" +
                "!admins             - print all admins. \n" +
                "!totals             - print families, players and admins count in databse. \n" +
                "\n" +
                "!save               - saves the current database and makes a backup.\n" +
                "!get                - reads data from the current database.\n" +
                "!backup             - do database backup. \n" +
                "!clear-all          - clear database. \n" +
                "!exit               - exit from app. \n" +
                "\n" +
                "!fill-players       - reads player data from X pages. The process is long and may fail.\n" +
                "!help               - displays a list of commands with a description.\n");
    }

    private void exit() {
        view.printMessage("Saving data base...");
        model.writeToDB();
        System.exit(0);
    }
}
