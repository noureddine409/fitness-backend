package com.metamafitness.fitnessbackend.service;

import com.metamafitness.fitnessbackend.dto.JwtToken;
import com.metamafitness.fitnessbackend.exception.ElementNotFoundException;
import com.metamafitness.fitnessbackend.model.User;

public interface UserService extends GenericService<User>{

    User findByEmail(String email) throws ElementNotFoundException;

    void sendVerificationEmail(User user, String siteURL);

    void generateVerificationCode(User user);

    boolean verify(String verificationCode);

    void saveAdmin();

    void saveTrainer();

    void saveDev();

    JwtToken generateResetPasswordToken(User user);

    void resetPassword(User user, String newPassword);
}
