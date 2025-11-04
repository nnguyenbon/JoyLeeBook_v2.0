package model;

import java.time.LocalDateTime;

public class Staff extends Account{
    private int staffId;

    public int getStaffId() {
        return staffId;
    }

    public void setStaffId(int staffId) {
        this.staffId = staffId;
    }
}
