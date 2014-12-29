Η εντολή για την εκτέλεση του αρχείου .jar (βρισκόμαστε στο ίδιο path με το αρχείο των samples sample.txt):
java -jar recommender.jar [default εκτέλεση με 75% training set και 25% testing set].
Διαφορετικά δέχεται δύο παραμέτρους, η πρώτη για το percentage του training set και η δεύτερη για αυτό του testing.
πχ java -jar recommender.jar 25 75 [εκτέλεση με 25% training set και 75% testing set]. 