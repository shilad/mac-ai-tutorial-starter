import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads and saves places, friends and ratings as CSV files.
 * Each file has a header line, which is skipped when loading.
 */
public class DataStore {
    static final Path PLACES_FILE = Path.of("places.csv");
    static final Path FRIENDS_FILE = Path.of("friends.csv");
    static final Path RATINGS_FILE = Path.of("ratings.csv");

    List<Place> places = new ArrayList<>();
    List<Friend> friends = new ArrayList<>();
    List<Rating> ratings = new ArrayList<>();

    void load() throws IOException {
        for (String line : readDataLines(PLACES_FILE)) {
            places.add(Place.fromCsv(line));
        }
        for (String line : readDataLines(FRIENDS_FILE)) {
            friends.add(Friend.fromCsv(line));
        }
        for (String line : readDataLines(RATINGS_FILE)) {
            ratings.add(Rating.fromCsv(line));
        }
    }

    void saveFriends() throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add("name,age,types,vibes,budget,likedPlaces");
        for (Friend f : friends) {
            lines.add(f.toCsv());
        }
        Files.write(FRIENDS_FILE, lines);
    }

    void saveRatings() throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add("date,place,friend,fun");
        for (Rating r : ratings) {
            lines.add(r.toCsv());
        }
        Files.write(RATINGS_FILE, lines);
    }

    /** Reads a file, skipping the header and blank lines. A missing file means no data. */
    private static List<String> readDataLines(Path file) throws IOException {
        List<String> result = new ArrayList<>();
        if (!Files.exists(file)) {
            return result;
        }
        List<String> lines = Files.readAllLines(file);
        for (int i = 1; i < lines.size(); i++) {
            if (!lines.get(i).isBlank()) {
                result.add(lines.get(i));
            }
        }
        return result;
    }
}
