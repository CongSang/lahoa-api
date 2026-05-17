package com.lahoa.lahoa_be.service;

import jakarta.mail.MessagingException;

public interface MailService {

    void sendSimpleEmail(
            String to,
            String subject,
            String body
    );

    void sendActivationEmail(
            String to,
            String name,
            String activationLink
    );

    void sendEmailWithAttachment(
            String to,
            String subject,
            String body,
            byte[] attachment,
            String filename
    ) throws MessagingException;
}
