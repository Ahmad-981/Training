package com.practice.project06.account;

import com.practice.project06.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Random;


@Getter
@Setter
@Entity(name = "accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "accountID")
    private Long accountID;

    @ManyToOne
    @JoinColumn(name = "userID", nullable = false)
    private User user;

    @Column(name = "account_number", nullable = false, unique = true)
    private String accountNumber;

    @Column(name = "account_type", nullable = false)
    private String accountType;

    @Column(name = "password", nullable = false)
    private String password;

    @PrePersist
    private void generateAccountNumber() {
        if (this.accountNumber == null) {
            this.accountNumber = generateRandomString(6);
        }
    }

    private String generateRandomString(int length) {
        int leftLimit = 60; // numeral '0'
        int rightLimit = 110; // letter 'z'
        Random random = new Random();
        StringBuilder buffer = new StringBuilder(length);

        while (buffer.length() < length) {
            int randomLimitedInt = leftLimit + (int)
                    (random.nextFloat() * (rightLimit - leftLimit + 1));
            if (Character.isLetterOrDigit(randomLimitedInt)) {
                buffer.append((char) randomLimitedInt);
            }
        }
        return buffer.toString();
    }
}