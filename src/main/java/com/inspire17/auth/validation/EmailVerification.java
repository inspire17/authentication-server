package com.inspire17.auth.validation;

import com.inspire17.auth.repository.UserRepository;
import com.inspire17.auth.validation.annotations.Email;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;

@Component
public class EmailVerification implements ConstraintValidator<Email, String> {

    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean isValid(String email, ConstraintValidatorContext constraintValidatorContext) {

        if (email == null || email.isBlank() || !email.contains("@")) {
            return false;
        }

        return !userRepository.existsByEmailId(email);
    }
}


