import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Ranks places using the crew's saved preferences and past ratings.
 */
public class Recommender {
    DataStore data;

    Recommender(DataStore data) {
        this.data = data;
    }

    /**
     * Returns the best-matching places under the budget. Type can be "any".
     */
    List<Place> topPlaces(String type, int budget, int howMany) {
        List<Place> result = new ArrayList<>();
        for (Place place : data.places) {
            if (place.cost <= budget && (type.equalsIgnoreCase("any") || place.type.equalsIgnoreCase(type))) {
                result.add(place);
            }
        }
        result.sort(Comparator.comparingInt(this::preferenceScore).reversed()
                .thenComparing(Comparator.comparingDouble(this::averageRating).reversed())
                .thenComparingInt(place -> place.cost));
        if (result.size() > howMany) {
            return new ArrayList<>(result.subList(0, howMany));
        }
        return result;
    }

    int preferenceScore(Place place) {
        int score = 0;
        for (Friend friend : data.friends) {
            if (containsIgnoreCase(friend.types, place.type)) {
                score += 3;
            }
            if (containsIgnoreCase(friend.vibes, place.vibe)) {
                score += 2;
            }
            if (containsIgnoreCase(friend.likedPlaces, place.name)) {
                score += 5;
            }
        }
        return score;
    }

    private double averageRating(Place place) {
        int total = 0;
        int count = 0;
        for (Rating rating : data.ratings) {
            if (rating.place.equalsIgnoreCase(place.name)) {
                total += rating.fun;
                count++;
            }
        }
        return count == 0 ? 0 : (double) total / count;
    }

    private static boolean containsIgnoreCase(List<String> values, String target) {
        for (String value : values) {
            if (value.equalsIgnoreCase(target)) {
                return true;
            }
        }
        return false;
    }

    /** Returns friends whose saved preferences match a place. */
    List<Friend> whoToInvite(Place place) {
        List<Friend> result = new ArrayList<>();
        for (Friend friend : data.friends) {
            if (containsIgnoreCase(friend.types, place.type)
                    || containsIgnoreCase(friend.vibes, place.vibe)
                    || containsIgnoreCase(friend.likedPlaces, place.name)) {
                result.add(friend);
            }
        }
        return result;
    }
}
