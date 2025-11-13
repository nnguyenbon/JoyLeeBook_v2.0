package model.staff;

public class GenreStats {
    private String genreName;
    private int count;
    private double percentage;

    public GenreStats() {}

    public GenreStats(String genreName, int count, double percentage) {
        this.genreName = genreName;
        this.count = count;
        this.percentage = percentage;
    }

    // Getters and Setters
    public String getGenreName() { return genreName; }
    public void setGenreName(String genreName) { this.genreName = genreName; }

    public int getCount() { return count; }
    public void setCount(int count) { this.count = count; }

    public double getPercentage() { return percentage; }
    public void setPercentage(double percentage) { this.percentage = percentage; }
}
