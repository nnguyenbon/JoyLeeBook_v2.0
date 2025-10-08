package model;
import java.time.LocalDateTime;

public class User {
    private int userId;
    private String username;
    private String fullName;
    private String passwordHash;
    private String role;
    private String email;
    private String status;
    private String emailOtp;
    private boolean isVerified;
    private boolean isGoogleAccount;
    private String googleAccountId;
    private LocalDateTime createdAt;
    private int points;

    public User(int userId, String username, String fullName, String passwordHash, String role, String email, String status, String emailOtp, boolean isVerified, boolean isGoogleAccount, String googleAccountId, LocalDateTime createdAt, int points) {
        this.userId = userId;
        this.username = username;
        this.fullName = fullName;
        this.passwordHash = passwordHash;
        this.role = role;
        this.email = email;
        this.status = status;
        this.emailOtp = emailOtp;
        this.isVerified = isVerified;
        this.isGoogleAccount = isGoogleAccount;
        this.googleAccountId = googleAccountId;
        this.createdAt = createdAt;
        this.points = points;
    }

    public User() {
    }
    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEmailOtp() {
        return emailOtp;
    }

    public void setEmailOtp(String emailOtp) {
        this.emailOtp = emailOtp;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }

    public boolean isGoogleAccount() {
        return isGoogleAccount;
    }

    public void setGoogleAccount(boolean googleAccount) {
        isGoogleAccount = googleAccount;
    }

    public String getGoogleAccountId() {
        return googleAccountId;
    }

    public void setGoogleAccountId(String googleAccountId) {
        this.googleAccountId = googleAccountId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}
