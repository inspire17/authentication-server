package com.inspire17.auth.entity;

import com.inspire17.auth.model.AccountStatus;
import com.inspire17.auth.model.UserRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
// Name of the table where user is stored
@Table(name = "family_user")
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true)
    private String username;

    @Column(name = "email_id", nullable = false)
    private String emailId;

    @Column(name="mobile_number")
    private String mobileNumber;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    @Enumerated(EnumType.STRING)
    @Column(name="account_status")
    private AccountStatus accountStatus;

    @Column(name = "is_email_verified",nullable = false)
    private Boolean isEmailVerified;
}