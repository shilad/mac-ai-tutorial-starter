import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Crew Night Out: plan nights out with your friends.
 * Run from the project folder so the CSV files are found.
 */
public class CrewNightOut {
    public static void main(String[] args) throws IOException {
        DataStore data = new DataStore();
        data.load();
        Scanner in = new Scanner(System.in);

        System.out.println("Loaded " + data.places.size() + " places, "
                + data.friends.size() + " friends, " + data.ratings.size() + " ratings.");

        while (true) {
            System.out.println();
            System.out.println("=== Crew Night Out ===");
            System.out.println("1. Set up / view friend profiles");
            System.out.println("2. Where should we go?");
            System.out.println("3. Rate a night out");
            System.out.println("4. Quit");
            System.out.print("Choose 1-4: ");

            if (!in.hasNextLine()) {
                break;
            }
            String choice = in.nextLine().trim();
            switch (choice) {
                case "1" -> manageProfiles(data, in);
                case "2" -> showRecommendations(data, in);
                case "3" -> System.out.println("Ratings: coming soon!");
                case "4" -> {
                    System.out.println("Have a great night out!");
                    return;
                }
                default -> System.out.println("Please type 1, 2, 3 or 4.");
            }
        }
    }

    private static void manageProfiles(DataStore data, Scanner in) throws IOException {
        System.out.println("\n=== Friend Profiles ===");
        if (data.friends.isEmpty()) {
            System.out.println("No profiles yet.");
        } else {
            for (Friend friend : data.friends) {
                System.out.println(friend);
            }
        }

        System.out.print("Add a profile? (y/n): ");
        if (!in.hasNextLine() || !in.nextLine().trim().equalsIgnoreCase("y")) {
            return;
        }

        String name;
        while (true) {
            System.out.print("Name: ");
            if (!in.hasNextLine()) {
                return;
            }
            name = in.nextLine().trim();
            boolean duplicate = false;
            for (Friend friend : data.friends) {
                if (friend.name.equalsIgnoreCase(name)) {
                    duplicate = true;
                    break;
                }
            }
            if (!name.isEmpty() && !name.contains(",") && !duplicate) {
                break;
            }
            System.out.println("Enter a non-empty name that is not already on the list.");
        }

        int age;
        while (true) {
            System.out.print("Age: ");
            if (!in.hasNextLine()) {
                return;
            }
            try {
                age = Integer.parseInt(in.nextLine().trim());
                if (age >= 1 && age <= 120) {
                    break;
                }
            } catch (NumberFormatException ignored) {
                // Ask again below.
            }
            System.out.println("Please enter an age from 1 to 120.");
        }

        List<String> types = readSelections(in, "What kinds of places do you like?", List.of(Place.TYPES));
        List<String> vibes = readSelections(in, "What vibes do you like?", List.of("chill", "lively", "fancy"));
        int budget = readBudget(in, "Maximum budget per person for a night out: $", -1);

        List<String> placeLabels = new ArrayList<>();
        for (Place place : data.places) {
            placeLabels.add(place.name + " (" + place.city + ")");
        }
        List<String> selectedPlaces = readSelections(in, "Choose any favorite places:", placeLabels);
        List<String> likedPlaces = new ArrayList<>();
        for (int i = 0; i < selectedPlaces.size(); i++) {
            for (Place place : data.places) {
                if (selectedPlaces.get(i).equals(placeLabels.get(data.places.indexOf(place)))) {
                    likedPlaces.add(place.name);
                    break;
                }
            }
        }

        data.friends.add(new Friend(name, age, types, vibes, budget, likedPlaces));
        data.saveFriends();
        System.out.println("Profile saved for " + name + ".");
    }

    private static List<String> readSelections(Scanner in, String question, List<String> options) {
        System.out.println(question + " Enter numbers separated by commas, or 0 to skip.");
        for (int i = 0; i < options.size(); i++) {
            System.out.println((i + 1) + ". " + options.get(i));
        }

        while (true) {
            System.out.print("Your choices: ");
            if (!in.hasNextLine()) {
                return List.of();
            }
            String answer = in.nextLine().trim();
            if (answer.isEmpty() || answer.equals("0")) {
                return List.of();
            }

            List<String> selections = new ArrayList<>();
            boolean valid = true;
            for (String choice : answer.split(",")) {
                try {
                    int index = Integer.parseInt(choice.trim()) - 1;
                    if (index < 0 || index >= options.size()) {
                        valid = false;
                        break;
                    }
                    String selection = options.get(index);
                    if (!selections.contains(selection)) {
                        selections.add(selection);
                    }
                } catch (NumberFormatException e) {
                    valid = false;
                    break;
                }
            }
            if (valid) {
                return selections;
            }
            System.out.println("Choose valid numbers separated by commas, or enter 0 to skip.");
        }
    }

    private static int readBudget(Scanner in, String prompt, int defaultBudget) {
        while (true) {
            if (defaultBudget >= 0) {
                System.out.print(prompt + " (press Enter for $" + defaultBudget + "): ");
            } else {
                System.out.print(prompt);
            }
            if (!in.hasNextLine()) {
                return Math.max(defaultBudget, 0);
            }
            String answer = in.nextLine().trim();
            if (answer.isEmpty() && defaultBudget >= 0) {
                return defaultBudget;
            }
            try {
                int budget = Integer.parseInt(answer);
                if (budget >= 0 && budget <= 10000) {
                    return budget;
                }
            } catch (NumberFormatException ignored) {
                // Ask again below.
            }
            System.out.println("Enter a whole-dollar amount from $0 to $10,000.");
        }
    }

    private static void showRecommendations(DataStore data, Scanner in) {
        if (data.places.isEmpty()) {
            System.out.println("No places are listed yet. Add places in places.csv.");
            return;
        }

        System.out.println("\nFilter by place type (0 for any):");
        System.out.println("0. Any type");
        for (int i = 0; i < Place.TYPES.length; i++) {
            System.out.println((i + 1) + ". " + Place.TYPES[i]);
        }
        String type = "any";
        while (true) {
            System.out.print("Choose a type: ");
            if (!in.hasNextLine()) {
                return;
            }
            String answer = in.nextLine().trim();
            try {
                int choice = Integer.parseInt(answer);
                if (choice == 0) {
                    break;
                }
                if (choice >= 1 && choice <= Place.TYPES.length) {
                    type = Place.TYPES[choice - 1];
                    break;
                }
            } catch (NumberFormatException ignored) {
                // Ask again below.
            }
            System.out.println("Choose a number from 0 to " + Place.TYPES.length + ".");
        }

        int defaultBudget = data.friends.stream()
                .mapToInt(friend -> friend.budget)
                .filter(budget -> budget > 0)
                .min()
                .orElse(50);
        int budget = readBudget(in, "Maximum price per person", defaultBudget);
        Recommender recommender = new Recommender(data);
        List<Place> recommendations = recommender.topPlaces(type, budget, 5);
        if (recommendations.isEmpty()) {
            System.out.println("No places match that type and budget.");
            return;
        }

        System.out.println("\nTop suggestions:");
        for (int i = 0; i < recommendations.size(); i++) {
            Place place = recommendations.get(i);
            System.out.println((i + 1) + ". " + place + " | preference match: "
                    + recommender.preferenceScore(place));
        }
    }
}
