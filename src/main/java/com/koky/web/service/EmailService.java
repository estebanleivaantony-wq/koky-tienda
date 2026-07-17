package com.koky.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("kokystiendas@gmail.com"); // Tu correo configurado
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);
            System.out.println(">>> Correo enviado exitosamente a: " + to);
        } catch (Exception e) {
            System.err.println(">>> Error enviando correo: " + e.getMessage());
            e.printStackTrace();
        }
    }
}