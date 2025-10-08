package model;

public class SeriesCategories {
    private int seriesId;
    private int categoryId;

    public SeriesCategories(int seriesId, int categoryId) {
        this.seriesId = seriesId;
        this.categoryId = categoryId;
    }

    public SeriesCategories() {}
    public int getSeriesId() {
        return seriesId;
    }

    public void setSeriesId(int seriesId) {
        this.seriesId = seriesId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }
}
