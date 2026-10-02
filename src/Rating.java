/**
 * One friend's fun rating of one night out.
 * CSV format: date,place,friend,fun
 */
public class Rating {
    String date;   // YYYY-MM-DD
    String place;
    String friend;
    int fun;       // 1 to 10

    Rating(String date, String place, String friend, int fun) {
        this.date = date;
        this.place = place;
        this.friend = friend;
        this.fun = fun;
    }

    static Rating fromCsv(String line) {
        String[] parts = line.split(",", -1);
        return new Rating(parts[0].trim(), parts[1].trim(), parts[2].trim(),
                Integer.parseInt(parts[3].trim()));
    }

    String toCsv() {
        return String.join(",", date, place, friend, String.valueOf(fun));
    }
}
