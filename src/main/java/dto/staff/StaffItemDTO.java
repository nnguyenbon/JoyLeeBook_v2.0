package dto.staff;

import model.Series;

import java.util.List;

public class StaffItemDTO {
    private int staffId;
    private String userName;
    private int totalHandleReport;
    private int totalApprovedReview;
    private int totalRejectedReview;
    private String createDate;
    private String fullName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }


}
