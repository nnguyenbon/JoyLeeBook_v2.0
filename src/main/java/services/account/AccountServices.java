//package services.account;
//
//import dao.AccountDAO;
//import dto.PaginationRequest;
//import model.BanReason;
//import dto.AccountDTO;
//
//import java.util.List;
//
//public class AccountServices {
//    private AccountDAO accountDAO = new AccountDAO();
//
////    public List<AccountDTO> getAccountList(String search,String filterByRole, String roleInSession, PaginationRequest paginationRequest) throws Exception {
////        return accountDAO.getAccounts(search,filterByRole, roleInSession, paginationRequest);
////    }
//
//    public int getTotalAccounts(String search, String filterByRole, String roleInSession) throws Exception {
//        return accountDAO.getTotalAccounts(search, filterByRole, roleInSession);
//    }
//
//
//    public List<AccountDTO> searchAccounts(String query, String currentRole) throws Exception {
//        return accountDAO.search(query, currentRole);
//    }
//
//    public void createAccount(AccountDTO account, String password) throws Exception {
//        // Hash password here if needed
//        accountDAO.insertAccount(account, password);
//    }
//
//    public void updateAccount(AccountDTO account) throws Exception {
//        accountDAO.updateAccount(account);
//    }
//
//    public void deleteAccount(int accountId) throws Exception {
//        accountDAO.deleteAccount(accountId);
//    }
//
//    public void banAccount(int accountId, BanReason reason) throws Exception {
//        accountDAO.banAccount(accountId, reason);
//    }
//}