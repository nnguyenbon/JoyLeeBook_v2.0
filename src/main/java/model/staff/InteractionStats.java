package model.staff;

public class InteractionStats {
    private int likes;
    private int comments;
    private int ratings;
    private int saves;
    private int totalInteractions;

    public InteractionStats() {}

    public InteractionStats(int likes, int comments, int ratings, int saves) {
        this.likes = likes;
        this.comments = comments;
        this.ratings = ratings;
        this.saves = saves;
        this.totalInteractions = likes + comments + ratings + saves;
    }

    // Getters and Setters
    public int getLikes() { return likes; }
    public void setLikes(int likes) { this.likes = likes; }

    public int getComments() { return comments; }
    public void setComments(int comments) { this.comments = comments; }

    public int getVotes() { return ratings; }
    public void setVotes(int votes) { this.ratings = votes; }


    public int getSaves() { return saves; }
    public void setSaves(int saves) { this.saves = saves; }

    public int getTotalInteractions() { return totalInteractions; }
    public void setTotalInteractions(int totalInteractions) { this.totalInteractions = totalInteractions; }
}
