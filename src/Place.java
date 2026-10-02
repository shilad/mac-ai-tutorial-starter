/**
 * A place the crew can go: a restaurant, bar, activity, music venue or night out spot.
 * CSV format: name,type,style,cost,vibe,city
 */
public class Place {
    static final String[] TYPES = {"food", "bar", "activity", "music", "night out"};

    String name;
    String type;   // one of TYPES
    String style;  // cuisine or style, like "tacos", "karaoke" or "jazz"
    int cost;      // usual cost per person, in dollars
    String vibe;   // chill, lively or fancy
    String city;

    Place(String name, String type, String style, int cost, String vibe, String city) {
        this.name = name;
        this.type = type;
        this.style = style;
        this.cost = cost;
        this.vibe = vibe;
        this.city = city;
    }

    static Place fromCsv(String line) {
        String[] parts = line.split(",", -1);
        String city = parts.length > 5 ? parts[5].trim() : "Twin Cities";
        return new Place(parts[0].trim(), parts[1].trim(), parts[2].trim(),
            Integer.parseInt(parts[3].trim()), parts[4].trim(), city);
    }

    String toCsv() {
        return String.join(",", name, type, style, String.valueOf(cost), vibe, city);
    }

    @Override
    public String toString() {
        return name + " (" + city + "; " + type + ", " + style + ", ~$" + cost + " each, " + vibe + ")";
    }
}
