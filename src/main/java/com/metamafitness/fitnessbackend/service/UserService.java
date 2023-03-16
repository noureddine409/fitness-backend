package com.metamafitness.fitnessbackend.service;

import com.metamafitness.fitnessbackend.exception.ElementNotFoundException;
import com.metamafitness.fitnessbackend.model.User;

public interface UserService extends GenericService<User>{

    public User findByEmail(String email) throws ElementNotFoundException;

    public void sendVerificationEmail(User user, String siteURL);

    public void generateVerificationCode(User user);

    public boolean verify(String code);

    public User saveAdmin();

    public User saveTrainer();

    public User saveDev();

}
