package def;

/**
 * Item containing item_id and values of the attributes
 * album(0),artist(1),genre(2).
 *
 * @author 2037,2056
 */
public class Item {

    private final Integer id;
    private final short[] attr;

    public Item(Integer id, short album, short artist, short genre) {
        this.id = id;
        attr = new short[3];
        attr[Utils.ALBUM_ATTR] = album;
        attr[Utils.ARTIST_ATTR] = artist;
        attr[Utils.GENRE_ATTR] = genre;
    }

    // GET methods
    public Integer getId() {
        return id;
    }

    public short getAttrVal(int a) {
        return attr[a];
    }
}
