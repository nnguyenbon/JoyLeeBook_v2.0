package dto;

// Assuming UserInfoDTO is a wrapper for user details; implement if needed
class UserInfoDTO {
    private AccountDTO account;
    // Additional fields if needed

    public UserInfoDTO(AccountDTO account) {
        this.account = account;
    }

    // Getters/Setters
    public AccountDTO getAccount() {
        return account;
    }

    public void setAccount(AccountDTO account) {
        this.account = account;
    }
}
