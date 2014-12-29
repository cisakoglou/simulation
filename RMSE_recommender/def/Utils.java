package def;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Class in which the rest of the classes have access. Stores users and items.
 * Calculates total interest for the value missing of the training set of a
 * user. Writes statistics to file.
 *
 * @author 2037,2056
 */
public class Utils {

    public static final int ALBUM_ATTR = 0;
    public static final int ARTIST_ATTR = 1;
    public static final int GENRE_ATTR = 2;

    public static HashMap<Integer, User> users;// user_id : user
    public static HashMap<Integer, Item> items;// item_id : item

    // storing
    public static HashMap<Short, ArrayList<Short>>[] valRatings;
    public static double denLikeTotal[];

    // testing
    public static int notFound;
    public static double sumRMSE;

    // statistics
    public static int ptest; // #items of full testing set(the testing sets of all users)
    public static int ptrain; // #items of full training set(the training sets of all users)
    public static int nUsers; // #users
    public static long nRatings[]; // # ratings for each rating [1-5]
    public static long sumUniqueVals[]; // unique values found in each attribute
    public static long nTeam[]; // # users in each team
    public static double sumTeamRMSE[]; // sum RMSE of each team's users
    public static long timeTeam[]; // sum time needed for creating profile for each team

    public static void firstRun() {
        items = new HashMap<Integer, Item>();
        users = new HashMap<Integer, User>();
        // -- testing variables -- //
        notFound = 0;
        sumRMSE = 0;
        // -- statistics variables -- //
        ptest = 0;
        ptrain = 0;
        nUsers = 0;
        nRatings = new long[5];
        for (int i = 0; i < 5; i++) {
            nRatings[i] = 0;
        }
        sumUniqueVals = new long[3];
        for (int i = 0; i < 3; i++) {
            sumUniqueVals[i] = 0;
        }
        nTeam = new long[4];
        for (int i = 0; i < 4; i++) {
            nTeam[i] = 0;
        }
        sumTeamRMSE = new double[4];
        for (int i = 0; i < 4; i++) {
            sumTeamRMSE[i] = 0;
        }
        timeTeam = new long[4];
        for (int i = 0; i < 4; i++) {
            timeTeam[i] = 0;
        }
    }

    public static void addUser(Integer user_id, User newUser) {
        users.put(user_id, newUser);
    }

    public static boolean isNewUser(Integer user_id) {
        if (users.containsKey(user_id)) {
            return false;
        } else {
            return true;
        }
    }

    public static void addItem(Integer user_id, Item newItem) {
        items.put(user_id, newItem);
    }

    public static boolean isNewItem(Integer item_id) {
        if (items.containsKey(item_id)) {
            return false;
        } else {
            return true;
        }
    }

    public static void initStore() {
        valRatings = (HashMap<Short, ArrayList<Short>>[]) new HashMap[3];
        valRatings[0] = new HashMap<>();
        valRatings[1] = new HashMap<>();
        valRatings[2] = new HashMap<>();
    }

    /**
     * calculate denominator of like for total interest. for each value of each
     * attribute calculate the mean rating of the value based on the ratings of
     * the items this value has been found. sum these mean ratings for the
     * values of each attribute three total dois for each attribute are created
     * and stored.
     */
    public static void calculateDenLikeTotal() {
        double sum;
        denLikeTotal = new double[3];
        for (int i = 0; i < 3; i++) {
            denLikeTotal[i] = 0;
            for (Short value : valRatings[i].keySet()) {
                sum = 0;
                for (Short rating : valRatings[i].get(value)) {
                    sum += rating;
                }
                denLikeTotal[i] += (double) (sum / valRatings[i].get(value).size());
            }
        }
    }

    /**
     * calculate numerator of like mean rating of the specific value and
     * attribute
     *
     * @param attr attribute in which the value is found
     * @param value value for which the mean rating is being calculated
     * @return the mean rating of the value
     */
    public static double calculateNumLikeTotal(Integer attr, Short value) {
        int sum = 0;
        for (Short rating : valRatings[attr].get(value)) {
            sum += rating;
        }
        return (double) (sum / valRatings[attr].get(value).size());
    }

    public static void increaseNotFound() {
        notFound++;
    }

    public static void writeStatisticsToFile(String filename) throws IOException {
        DecimalFormat df = new DecimalFormat("##.##");
        DecimalFormat df_ac = new DecimalFormat("##.####");
        BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
        bw.write("RMSE: " + df_ac.format(Math.sqrt((double) sumRMSE / ptest)));
        bw.newLine();
        bw.write("Not found values(in the whole training set): " + notFound);
        bw.newLine();
        bw.newLine();
        // percentage of different ratings
        for (int i = 0; i < 5; i++) {
            bw.write("Percentage of items rated with " + (i + 1) + ": " + (df.format((double) nRatings[i] * 100 / ptrain)) + "%");
            bw.newLine();
        }
        bw.newLine();
        // # unique values in each attribute
        for (int i = 0; i < 3; i++) {
            bw.write("# unique values for attr " + (i + 1) + ": " + valRatings[i].size());
            bw.newLine();
        }
        bw.newLine();
        // mean user [# items - # values in each attr]
        bw.write("Mean user: " + (ptrain / nUsers) + " items rated("
                + (sumUniqueVals[0] / nUsers) + " unique values in attr 1 - "
                + (sumUniqueVals[1] / nUsers) + " unique values in attr 2 - "
                + (sumUniqueVals[2] / nUsers) + " unique values in attr 3)");
        bw.newLine();
        bw.newLine();
        // percentage of each item out of the total users,
        // mean rmse for each team and
        // mean execution time for creating profiles of a user belonging to that team
        bw.write("Team A(20-50): " + (df.format((double) nTeam[0] * 100 / nUsers))
                + "%\t Mean accuracy: " + (df_ac.format(sumTeamRMSE[0] / nTeam[0]))
                + "\t Mean time: " + (df_ac.format((double) timeTeam[0] / nTeam[0]) + " ms"));
        bw.newLine();
        bw.write("Team B(51-100): " + (df.format((double) nTeam[1] * 100 / nUsers))
                + "%\t Mean accuracy: " + (df_ac.format(sumTeamRMSE[1] / nTeam[1]))
                + "\t Mean time: " + (df_ac.format((double) timeTeam[1] / nTeam[1]) + " ms"));
        bw.newLine();
        bw.write("Team C(101-300): " + (df.format((double) nTeam[2] * 100 / nUsers))
                + "%\t Mean accuracy: " + (df_ac.format(sumTeamRMSE[2] / nTeam[2]))
                + "\t Mean time: " + (df_ac.format((double) timeTeam[2] / nTeam[2]) + " ms"));
        bw.newLine();
        bw.write("Team D(>300): " + (df.format((double) nTeam[3] * 100 / nUsers))
                + "%\t Mean accuracy: " + (df_ac.format(sumTeamRMSE[3] / nTeam[3]))
                + "\t Mean time: " + (df_ac.format((double) timeTeam[3] / nTeam[3]) + " ms"));
        bw.newLine();

        bw.close();

    }
}
