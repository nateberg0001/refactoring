package theater;

/**
 * Represents a theatre play with metadata and scenes.
 *
 * <p>Short description of the class responsibilities and behavior.</p>
 *
 * @author Nathan
 * @version 1.0
 */

public class Play {

    private String name;
    private String type;

    public Play(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return this.name;
    }

    public String getType() {
        return this.type;
    }
}
