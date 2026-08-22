package com.homeservice.homecraft_backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            System.out.println("Email sent to: " + to);
        } catch (Exception e) {
            System.err.println("Failed to send email to " + to + ": " + e.getMessage());
        }
    }

    public void sendNewBidEmail(String clientEmail, String clientName, String professionalName, String projectTitle, Double bidAmount) {
        String subject = "New Bid on Your Project: " + projectTitle;
        String body = String.format(
                "Hello %s,\n\n" +
                        "You have received a new bid on your project '%s'.\n\n" +
                        "Professional: %s\n" +
                        "Bid Amount: $%.2f\n\n" +
                        "Please login to view and respond to this bid.\n\n" +
                        "Best regards,\n" +
                        "HomeCraft Connect Team",
                clientName, projectTitle, professionalName, bidAmount
        );
        sendEmail(clientEmail, subject, body);
    }

    public void sendBidAcceptedEmail(String professionalEmail, String professionalName, String projectTitle) {
        String subject = "Your Bid Has Been Accepted! 🎉";
        String body = String.format(
                "Hello %s,\n\n" +
                        "Congratulations! Your bid on project '%s' has been accepted.\n\n" +
                        "Please login to view the project details and start working.\n\n" +
                        "Best regards,\n" +
                        "HomeCraft Connect Team",
                professionalName, projectTitle
        );
        sendEmail(professionalEmail, subject, body);
    }

    public void sendBidRejectedEmail(String professionalEmail, String professionalName, String projectTitle) {
        String subject = "Bid Update: " + projectTitle;
        String body = String.format(
                "Hello %s,\n\n" +
                        "Your bid on project '%s' has been rejected.\n\n" +
                        "Don't worry! Keep browsing other projects that match your skills.\n\n" +
                        "Best regards,\n" +
                        "HomeCraft Connect Team",
                professionalName, projectTitle
        );
        sendEmail(professionalEmail, subject, body);
    }

    public void sendProjectCompletedEmail(String professionalEmail, String professionalName, String projectTitle) {
        String subject = "Project Completed: " + projectTitle;
        String body = String.format(
                "Hello %s,\n\n" +
                        "The project '%s' has been marked as completed!\n\n" +
                        "You can expect a review from the client soon.\n\n" +
                        "Best regards,\n" +
                        "HomeCraft Connect Team",
                professionalName, projectTitle
        );
        sendEmail(professionalEmail, subject, body);
    }

    public void sendNewReviewEmail(String professionalEmail, String professionalName, String projectTitle, int rating) {
        String subject = "New Review Received! ⭐";
        String body = String.format(
                "Hello %s,\n\n" +
                        "You have received a new review on project '%s'.\n" +
                        "Rating: %d/5\n\n" +
                        "Login to see the full review.\n\n" +
                        "Best regards,\n" +
                        "HomeCraft Connect Team",
                professionalName, projectTitle, rating
        );
        sendEmail(professionalEmail, subject, body);
    }
}