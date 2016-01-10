package com.myMoneyTracker.util;

import java.util.Date;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Class that can be used to send automatic mails.
 * 
 * @author Florin
 */
public class EmailSender {
    
    private String senderEmail;
    private String senderPassword;
    
    /**
     * Method used to send an email to the specified receiver.
     * 
     * @param receiverEmail: email address of the receiver
     * @param subject: subject of the message
     * @param message: the message content of the mail
     * 
     * @throws MessagingException: exception thrown in case of issues
     */
    public void sendEmail(String receiverEmail, String subject, String message) throws MessagingException {
    
        Properties props = buildProperties();
        Session session = Session.getDefaultInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
            
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });
        
        // -- Create a new message --
        Message msg = new MimeMessage(session);
        
        // -- Set the FROM and TO fields --
        msg.setFrom(new InternetAddress(senderEmail));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(receiverEmail, false));
        msg.setSubject(subject);
        msg.setText(message);
        msg.setSentDate(new Date());
        Transport.send(msg);
    }
    
    public void setSenderEmail(String senderEmail) {
    
        this.senderEmail = senderEmail;
    }
    
    public void setSenderPassword(String senderPassword) {
    
        this.senderPassword = senderPassword;
    }
    
    private Properties buildProperties() {
    
        Properties props = System.getProperties();
        props.setProperty("mail.smtp.host", "smtp.gmail.com");
        props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.setProperty("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.port", "465");
        props.setProperty("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.auth", "true");
        props.put("mail.debug", "true");
        props.put("mail.store.protocol", "pop3");
        props.put("mail.transport.protocol", "smtp");
        return props;
    }
    
}
