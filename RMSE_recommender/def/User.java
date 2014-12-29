package def;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

/**
 * User containing user_id, training and testing set, profile. Each user is
 * responsible for splitting the items associating with him into training and
 * testing set, calculating his profile and predictions for the items of his
 * testing set.
 *
 * @author 2037,2056
 */
public class User {

    private final Integer id;
    private HashMap<Integer, ItemPerUser> itemsRated; // original items corresponding to the user created at step 2 of main
    private final HashMap<Integer, ItemPerUser> trainingSet; // created at step 3 of main 
    private final HashMap<Integer, ItemPerUser> testingSet;// created at step 3 of main 
    private final Vector<Double>[] profile; // degree of interest for values of attribute represented by 3 vectors, one for each attr
    private final ArrayList<Short>[] attrVals; // values for each attr - the values for which profile stores the corresponding degrees of interest
    private char team;

    User(Integer id) {
        this.id = id;
        itemsRated = new HashMap<Integer, ItemPerUser>();
        trainingSet = new HashMap<Integer, ItemPerUser>();
        testingSet = new HashMap<Integer, ItemPerUser>();
        // 3 different vectors, one for each attr
        profile = (Vector<Double>[]) new Vector[3];
        // in each vector the corresponding degree of interest for each value is stored
        //vectorSize = new int[3];
        attrVals = (ArrayList<Short>[]) new ArrayList[3];
    }

    public Integer getId() {
        return id;
    }

    public Set<Integer> getTrainingIds() {
        return trainingSet.keySet();
    }

    /**
     * Add item representation for the user in itemsRated
     *
     * @param itemId
     * @param rating field of item's representation
     */
    public void ratedItem(Integer itemId, short rating) {
        itemsRated.put(itemId, new ItemPerUser(rating));
    }

    /**
     * Split itemsRated into two sets, based on the percentages given [question
     * A]
     *
     * @param trPercent percentage for training set
     * @param tePercent percentage for testing set
     */
    public void setSets(double trPercent, double tePercent) {
        int i = 1;
        int trNum = (int) Math.round(trPercent * itemsRated.size());
        short rating;
        for (Integer id : itemsRated.keySet()) {
            // consider the first trNum items of itemsRated as the training set of the user
            // and save them in the appropriate collection
            if (i <= trNum) {
                trainingSet.put(id, itemsRated.get(id));
                i++;
                Utils.ptrain++;
                rating = itemsRated.get(id).getRating();
                switch (rating) {
                    case 1:
                        Utils.nRatings[0]++;
                        break;
                    case 2:
                        Utils.nRatings[1]++;
                        break;
                    case 3:
                        Utils.nRatings[2]++;
                        break;
                    case 4:
                        Utils.nRatings[3]++;
                        break;
                    case 5:
                        Utils.nRatings[4]++;
                        break;
                    default:
                        // will never execute
                        break;
                }
                // the rest items are considered the testing set
            } else {
                Utils.ptest++;
                testingSet.put(id, itemsRated.get(id));
            }
        }
        itemsRated.clear();
        itemsRated = null;
        if ((trNum >= 20) && (trNum <= 50)) {
            team = 'A';
            Utils.nTeam[0]++;
        } else if ((trNum >= 51) && (trNum <= 100)) {
            team = 'B';
            Utils.nTeam[1]++;
        } else if ((trNum >= 101) && (trNum <= 300)) {
            team = 'C';
            Utils.nTeam[2]++;
        } else if (trNum > 300) {
            team = 'D';
            Utils.nTeam[3]++;
        } else {
            team = 'v';
        }
    }

    /**
     * calculate and store user's profile [question B]
     */
    @SuppressWarnings("unchecked")
    public void setProfile() {
        ArrayList<Short> values = new ArrayList<Short>();
        ArrayList<Integer> itemsContainingValue = new ArrayList<Integer>();
        double pop, like, denLike, meanRatingVal;
        long before = System.currentTimeMillis();
        for (int i = 0; i < 3; i++) { // for each attr
            getAttrValues(i, values);
            Utils.sumUniqueVals[i] += values.size();
            profile[i] = new Vector<Double>();
            attrVals[i] = new ArrayList<Short>();
            denLike = calculateDenLike(values, i); // calculate one time the denominator of like
            for (Short value : values) { // for each value of attr
                meanRatingVal = getItemsValAttr(value, i, itemsContainingValue);
                // calculate pop
                pop = (double) itemsContainingValue.size() / trainingSet.size();
                // calculate like
                like = meanRatingVal / denLike;
                // calculate doi and add it in vector
                profile[i].add(2 * pop * like / (like + pop));
                attrVals[i].add(value);
                itemsContainingValue.clear();
            }
            values.clear();
        }
        // define in which team the user belongs
        // based on the number of items in his training set
        long after = System.currentTimeMillis();
        switch (team) {
            case 'A':
                Utils.timeTeam[0] += (after - before);
                break;
            case 'B':
                Utils.timeTeam[1] += (after - before);
                break;
            case 'C':
                Utils.timeTeam[2] += (after - before);
                break;
            case 'D':
                Utils.timeTeam[3] += (after - before);
                break;
            case 'v':
                // do nothing
                break;
            default:
                // will never execute
                break;
        }
        // cleaning the house
        values = null;
        itemsContainingValue = null;
    }

    /**
     * calculate and store prediction for each item in the testing set of the
     * user [question C] for each item that its prediction has been calculated,
     * save the difference between the prediction estimated and the rating given
     * in order to calculate the total rmse of the algorithm [question D]
     */
    public void setPredictions() {
        short val, max, S;
        double itemAccuracy;
        double sumUserRMSE = 0, userRMSE;
        for (Integer test : testingSet.keySet()) { // calculate prediction for each item
            max = 1;
            for (int i = 0; i < 3; i++) { // for each attr calculate S
                val = Utils.items.get(test).getAttrVal(i);
                S = (short) Math.round(4 * getDOI(i, val) + 1);
                if (S > max) {
                    max = S;
                }
            }
            testingSet.get(test).setPrediction(max);
            itemAccuracy = Math.pow(max - testingSet.get(test).getRating(), 2);
            sumUserRMSE += itemAccuracy;
            Utils.sumRMSE += itemAccuracy;
        }
        userRMSE = Math.sqrt((double) sumUserRMSE / testingSet.size()); // store rmse for each user [question E]
        // depending on his team, add user's rmse to the correspoing rmse 
        // for the mean rmse to be calculated [question E]
        switch (team) {
            case 'A':
                Utils.sumTeamRMSE[0] += userRMSE;
                break;
            case 'B':
                Utils.sumTeamRMSE[1] += userRMSE;
                break;
            case 'C':
                Utils.sumTeamRMSE[2] += userRMSE;
                break;
            case 'D':
                Utils.sumTeamRMSE[3] += userRMSE;
                break;
            case 'v':
                // do nothing
                break;
            default:
                // will never execute
                break;
        }
    }

    /**
     * get degree of interest for the specific value and attr which have been
     * already calculated by the creating profile process (step 3 of main) if
     * not calculate the degree of interest based on the training sets of all
     * users using Utils' methods and fields which store the needed information.
     *
     * @param attr
     * @param value
     * @return
     */
    public double getDOI(int attr, short value) {
        // get the already calculated doi 
        if (attrVals[attr].contains(value)) {
            return profile[attr].elementAt(attrVals[attr].indexOf(value));
        } // calculate total interest for the value of the attribute attr
        else if (Utils.valRatings[attr].keySet().contains(value)) {
            double pop, like;
            pop = (double) (Utils.valRatings[attr].get(value).size() / Utils.ptrain);
            like = (double) (Utils.calculateNumLikeTotal(attr, value) / Utils.denLikeTotal[attr]);
            return (2 * pop * like / (like + pop));
        } // when the requested value of attr is found neither among the spesific user
        // nor among the training sets of all the users define the doi of the value as zero(the minimum it can get)
        else {
            Utils.increaseNotFound();
            return 0;
        }
    }

    /**
     * store in Utils the values for each attribute found in the training set
     * and the ratings of the items in which these values are found.(step 4 of
     * main)
     */
    public void storeValRatings() {
        Short value;
        for (Integer item_id : trainingSet.keySet()) {
            for (int i = 0; i < 3; i++) {
                value = Utils.items.get(item_id).getAttrVal(i);
                if (Utils.valRatings[i].get(value) == null) {
                    Utils.valRatings[i].put(value, new ArrayList<Short>());
                }
                Utils.valRatings[i].get(value).add(trainingSet.get(item_id).getRating());
            }
        }
    }

    // _______________ helping functions to create profile _______________ //
    /**
     * calculate denominator of like
     *
     * @param values values found in the attribute a
     * @param a attribute for which denominator of like is being calculated
     * @return sum of mean ratings of the values of attribute a
     */
    private double calculateDenLike(ArrayList<Short> values, int a) {
        double s = 0;
        ArrayList<Integer> itemsContainingValue = new ArrayList<Integer>();
        for (Short value : values) {
            s += (double) getItemsValAttr(value, a, itemsContainingValue) / itemsContainingValue.size();
            itemsContainingValue.clear();
        }
        return s;
    }

    /**
     * Store in collection all the values found in attribute a
     *
     * @param a attribute for which the values should be found
     * @param collection arraylist in which the values are stored
     */
    private void getAttrValues(int a, ArrayList<Short> collection) {
        java.util.Set<Short> values = new TreeSet<Short>();
        for (Integer itemId : trainingSet.keySet()) {
            values.add(Utils.items.get(itemId).getAttrVal(a));
        }
        collection.addAll(values);
        values.clear();
        values = null;
    }

    /**
     * Store in arraylist the items which have the value v for attribute a
     *
     * @param v value
     * @param a attribute
     * @param itemsContainingValue arraylist where the items are stored
     * @return mean rating for the value v of attribute a based on the ratings
     * of the items found
     */
    private double getItemsValAttr(short v, int a, ArrayList<Integer> itemsContainingValue) {
        int s = 0;
        for (Integer itemId : trainingSet.keySet()) {
            if (Utils.items.get(itemId).getAttrVal(a) == v) {
                itemsContainingValue.add(itemId);
                s += trainingSet.get(itemId).getRating();
            }
        }
        return (double) (s / itemsContainingValue.size());
    }
}
