package def;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * 
 * @author 2037,2056
 */
public class Main {

    public static void main(String[] args) {
        ////////////////////////////////////////////////////////////////////////////
        //  1. saving command arguments for training and testing set percentage  //                    
        //////////////////////////////////////////////////////////////////////////
        int trPercent = 75, tePercent = 25;
        if (args.length == 2) {
            try {
                trPercent = Integer.parseInt(args[0]);
                tePercent = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.err.println("Arguments" + " must be integers!");
                System.exit(1);
            }
        } else if ((args.length != 0) && (args.length != 2)) {
            System.err.println("Two arguments needed");
            System.exit(1);
        }
        /////////////////////////////////////////////////////////////////////////////
        //  2. reading from file sample.txt - creating users and items associating//
        //  with each user - and the represenation(ItemPerUser) for each one     //                    
        //////////////////////////////////////////////////////////////////////////
        BufferedReader br;
        String s;
        String delims = "\t";
        String[] tokens;
        Utils.firstRun();
        try {
            br = new BufferedReader(new FileReader("sample.txt"));
            while ((s = br.readLine()) != null) { // row = user rating
                tokens = s.split(delims);
                //if new user
                Integer user_id = Integer.parseInt(tokens[0]);
                if (Utils.isNewUser(user_id)) {
                    Utils.addUser(user_id, new User(user_id));
                    Utils.nUsers++;
                }
                // if new item
                Integer item_id = Integer.parseInt(tokens[1]);
                if (Utils.isNewItem(item_id)) {
                    Utils.addItem(item_id, new Item(item_id, (short) Integer.parseInt(tokens[2]),
                            (short) Integer.parseInt(tokens[3]),
                            (short) Integer.parseInt(tokens[4])));
                }
                // add the item that user rated
                // and the rating for this item and for the specific  user
                Utils.users.get(user_id).ratedItem(item_id, (short) Integer.parseInt(tokens[5]));
            }
            br.close();
        } catch (IOException e) {
            System.err.println("File sample.txt not found!");
            System.exit(1);
        }

        ///////////////////////////////////////////////////////////////////////////
        //  3. creating training and testing set for each user,                 //
        //  extracting user's profile from his training set                    //                   
        ////////////////////////////////////////////////////////////////////////
        for (User user : Utils.users.values()) {
            user.setSets((double) trPercent / 100, (double) tePercent / 100);
            user.setProfile();
        }
        System.out.println("Training done!");

        ////////////////////////////////////////////////////////////////////////////
        //  4. storing denominator of like  for total degree of interest         //           
        //////////////////////////////////////////////////////////////////////////
        Utils.initStore();
        for (User user : Utils.users.values()) {
            user.storeValRatings();
        }
        Utils.calculateDenLikeTotal();

        System.out.println("Storing done!");

        ////////////////////////////////////////////////////////////////////////////
        //  5. calculating predictions for each user                             //                    
        //////////////////////////////////////////////////////////////////////////
        for (User user : Utils.users.values()) {
            user.setPredictions();
        }

        System.out.println("Testing done!");

        ////////////////////////////////////////////////////////////////////////////
        //  6. writing statisting to file                                        //                    
        //////////////////////////////////////////////////////////////////////////
        try {
            Utils.writeStatisticsToFile("stats_" + trPercent + "_" + tePercent + ".txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("ptrain=" + Utils.ptrain);
        System.out.println("ptest=" + Utils.ptest);
        System.out.println("nusers=" + Utils.nUsers);
    }

}
