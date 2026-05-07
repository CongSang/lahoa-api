package com.lahoa.lahoa_be.service;

import jakarta.mail.MessagingException;

public interface MailService {

    void sendEmail(String to, String subject, String body);

    void sendEmailWithAttachment(String to, String subject, String body, byte[] attachment, String filename) throws MessagingException;
}
