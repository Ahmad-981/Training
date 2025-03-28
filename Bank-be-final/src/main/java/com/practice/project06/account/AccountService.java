package com.practice.project06.account;

import com.practice.project06.balance.BalanceRepository;
import com.practice.project06.transaction.TransactionRepository;
import com.practice.project06.user.User;
import com.practice.project06.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private BalanceRepository balanceRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    private String generateRandomString(int length) {
        SecureRandom secureRandom = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int digit = secureRandom.nextInt(10);
            sb.append(digit);
        }

        return sb.toString();
    }

    public Map<String, Object> createAccount(AccountDTO accountDTO) {
        User user = accountDTO.getUser();

        if (user == null || user.getUserID() == null) {
            throw new IllegalArgumentException("User ID must not be null");
        }

        User existingUser = userRepository.findById(user.getUserID())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Account account = new Account();
        account.setAccountNumber(accountDTO.getAccountNumber());
        account.setAccountType(accountDTO.getAccountType());
        account.setUser(existingUser);
        account.setAccountNumber(generateRandomString(8));

        Account savedAccount = accountRepository.save(account);
        Map<String, Object> response = new HashMap<>();
        response.put("accountId", savedAccount.getAccountID());
        response.put("accountNum", savedAccount.getAccountNumber());

        return response;
    }

    public Optional<Account> updateAccount(Long id, Account updatedAccount) {
        Optional<Account> existingAccountOpt = accountRepository.findById(id);

        if (existingAccountOpt.isPresent()) {
            Account existingAccount = existingAccountOpt.get();

            existingAccount.setAccountNumber(updatedAccount.getAccountNumber());
            existingAccount.setAccountType(updatedAccount.getAccountType());

            User updatedUser = updatedAccount.getUser();
            if (updatedUser != null) {
                User existingUser = existingAccount.getUser();
                if (existingUser != null) {
                    existingUser.setUsername(updatedUser.getUsername());
                    existingUser.setRole(updatedUser.getRole());
                    existingUser.setEmail(updatedUser.getEmail());
                    existingUser.setAddress(updatedUser.getAddress());
                    userRepository.save(existingUser);
                }
            }
            return Optional.of(accountRepository.save(existingAccount));
        } else {
            return Optional.empty();
        }
    }

    @Transactional
    public boolean deleteAccount(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Account not found"));

        // Soft delete the account
        account.setIsDeleted(true);
        accountRepository.save(account);

        // Optionally, handle associated transactions (if needed)
        // Transactions related to 'fromAccount' and 'toAccount' should be fetched
        // and processed if you need to maintain historical data.

        return true;
    }

//    @Transactional
//    public boolean deleteAccount(Long id) {
//        Account account = accountRepository.findById(id)
//                .orElseThrow(() -> new EntityNotFoundException("Account not found"));
//        balanceRepository.deleteByAccountId(id);
//        transactionRepository.deleteByFromAccountId(id);
//        transactionRepository.deleteByToAccountId(id);
//        accountRepository.delete(account);
//        return true;
//
//    }

}