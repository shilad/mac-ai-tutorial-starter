import java.util.ArrayList;
import java.util.List;

/**
 * A friend's profile: what they like and how much they want to spend.
 * CSV format: name,age,types,vibes,budget,likedPlaces
 * Lists inside a field are separated by semicolons, like "bar;music".
 */
public class Friend {
    String name;
    int age;                   // 0 means not provided for profiles created before age was collected
    List<String> types;        // favorite types, like bar or activity
    List<String> vibes;        // favorite vibes, like chill or lively
    int budget;                // most they want to spend per night, in dollars
    List<String> likedPlaces;  // places they already like

    Friend(String name, int age, List<String> types, List<String> vibes, int budget, List<String> likedPlaces) {
        this.name = name;
        this.age = age;
        this.types = types;
        this.vibes = vibes;
        this.budget = budget;
        this.likedPlaces = likedPlaces;
    }

    static Friend fromCsv(String line) {
        String[] parts = line.split(",", -1);
        if (parts.length == 5) {
            return new Friend(parts[0].trim(), 0, splitList(parts[1]), splitList(parts[2]),
                Integer.parseInt(parts[3].trim()), splitList(parts[4]));
        }
        int age = parts[1].isBlank() ? 0 : Integer.parseInt(parts[1].trim());
        return new Friend(parts[0].trim(), age, splitList(parts[2]), splitList(parts[3]),
            Integer.parseInt(parts[4].trim()), splitList(parts[5]));
    }

    String toCsv() {
        return String.join(",", name, age == 0 ? "" : String.valueOf(age),
            String.join(";", types), String.join(";", vibes),
                String.valueOf(budget), String.join(";", likedPlaces));
    }

    /** Turns "a;b; c" into [a, b, c]. An empty field becomes an empty list. */
    static List<String> splitList(String field) {
        List<String> result = new ArrayList<>();
        for (String item : field.split(";")) {
            if (!item.trim().isEmpty()) {
                result.add(item.trim());
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return name + " | age: " + (age == 0 ? "not set" : age) + " | likes: " + types + " | vibes: " + vibes
                + " | budget: $" + budget + " | favorite spots: " + likedPlaces;
    }
}
