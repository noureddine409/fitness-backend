package com.metamafitness.fitnessbackend.service;

import java.util.Map;

public interface MailSenderService {

    public void sendEmail(String toEmail, String subject, Map<String, Object> model, String template);
}
