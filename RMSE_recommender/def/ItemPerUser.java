package def;

/**
 * Item representation for each user, containing the rating its given and the
 * prediction that its going to be calculated for the specific user.
 *
 * @author 2037,2056
 */
public class ItemPerUser {

    private short rating, prediction;

    public ItemPerUser(short rating) {
        this.rating = rating;
        prediction = 0;
    }

    // SET methods
    public void setPrediction(short prediction) {
        this.prediction = prediction;
    }

    // GET methods
    public short getRating() {
        return rating;
    }

    public short getPrediction() {
        return prediction;
    }

}
