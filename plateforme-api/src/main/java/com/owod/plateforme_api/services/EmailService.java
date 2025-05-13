package com.owod.plateforme_api.services;

import com.owod.plateforme_api.models.payload.ContactRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Service responsible for sending various application emails, such as password reset notifications and contact form messages.
 */
@Service
public class EmailService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${MAIL_USERNAME}")
    private String fromEmail;

    @Value("${mail.contact}")
    private String toEmail;

    /**
     * Sends a password reset email to the specified recipient, including a link to reset the password.
     *
     * @param toEmail   the recipient's email address
     * @param resetLink the password reset URL to include in the email body
     */
    public void sendPasswordResetEmail(String toEmail, String resetLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Réinitialisation de mot de passe - Plateforme OWOD");
        message.setText(
                "Bonjour,\n\n" +
                        "Vous avez demandé à réinitialiser votre mot de passe pour accéder à la plateforme OWOD.\n" +
                        "Pour réinitialiser votre mot de passe, veuillez cliquer sur le lien suivant :\n\n" +
                        resetLink + "\n\n" +
                        "Ce lien est valide pendant 1 heure. Si vous n'êtes pas à l'origine de cette demande, " +
                        "vous pouvez ignorer cet email en toute sécurité.\n\n" +
                        "Ceci est un email automatique, merci de ne pas y répondre. Si vous avez des questions, " +
                        "veuillez contacter notre support via le site officiel.\n\n" +
                        "Cordialement,\n" +
                        "L'équipe OWOD"
        );
        mailSender.send(message);
    }

    /**
     * Sends a contact form email using the details provided in the ContactRequest.
     *
     * @param request the contact request payload containing sender email, subject, reason, and description
     */
    public void sendContactEmail(ContactRequest request) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(request.getEmail());
        message.setTo(toEmail);
        message.setSubject("Plateforme OWOD - demande de contact : " + request.getSubject());
        message.setText(
                "Email: " + request.getEmail() + "\n" +
                        "Raison: " + request.getReason() + "\n" +
                        "Description:\n" + request.getDescription()
        );
        mailSender.send(message);
    }
}
