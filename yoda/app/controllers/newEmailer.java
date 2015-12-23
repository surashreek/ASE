package controllers;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.*;

public class newEmailer {
    final static String username = "cka.jedi.master@gmail.com";
    final static String password = "cka4156cka";
    static Properties props = new Properties();
    static javax.mail.Session session;

    private static void setProps() {
        session = javax.mail.Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
    }

    public static Message setupMessage(String from, String to, String cc, String subjectLine) throws MessagingException {
        setProps();
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        if (cc != null) {
            message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(cc));
        }
        message.setSubject(subjectLine);

        return message;
    }

    public static void sendRequest(String f_recipient, String l_recipient, String email, String coursename, Date d, String personalMessage, String location) {
        try {
            Message message = setupMessage(username, email, null, "You have a tutoring request for class " + coursename);

            message.setText("Dear " + f_recipient + " " + l_recipient
                    + ",\n\n Congratulations! Someone requested a tutoring session from you!"
                    + "\n\n Course name: " + coursename
                    + "\n Time and date: " + d
                    + "\n Location: " + location
                    + "\n Personal message: " + personalMessage
                    + "\n\n To accept or decline this request, please log into your account.");

            Transport.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    public static void sendAccept(models.User tutor, models.User tutee, String course, Date d, String personalMessage, String location) {
        try {
            Message message = setupMessage(username, tutee.user_name, tutor.user_name, "ACCEPTED: your tutoring request for " + course);

            message.setText("Dear " + tutee.first_name + " " + tutee.last_name
                    + ",\n\n Good news! " + tutor.first_name + " " + tutor.last_name + " has accepted your request for tutoring!"
                    + "\n\n Course name: " + course
                    + "\n Time and date: " + d
                    + "\n Location: " + location
                    + "\n Personal message: " + personalMessage
                    + "\n\n Your tutor's e-mail address is cc'd on this. "
                    + "Use this as a primary channel for communication for now. "
                    + "Please note at this time that you cannot cancel your request...");

            Transport.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    public static void sendDecline(models.User tutor, models.User tutee, String coursename, Date d, String personalMessage, String location) {
        try {
            Message message = setupMessage(username, tutee.user_name, null, "DECLINED: your tutoring request for " + coursename);

            message.setText("Dear " + tutee.first_name + " " + tutee.last_name
                    + ",\n\n Unfortunately, " + tutor.first_name + " " + tutor.last_name + " has declined your request for tutoring. /sadface"
                    + "\n\n Course name: " + coursename
                    + "\n Time and date: " + d
                    + "\n Location: " + location
                    + "\n Personal message: " + personalMessage
                    + "\n\n This person may have declined because he/she is not available at that time. Or they might just hate you. "
                    + "Read the personal message to find out.");

            Transport.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    public static void sendValidation(models.User new_user) {
        try {
            Message message = setupMessage(username, new_user.user_name, null, "Feel the force! (and validate your sign-up)");

            message.setText("Dear " + new_user.first_name + " " + new_user.last_name
                    + ",\n\n Thank you for signing up! Please click on the below link to complete your registration!"
                    + "\n\n Note that the link will only be valid for the next 24 hours, after which your sign-up will be invalidated."
                    + "\n\n http://localhost:9000/validate?user=" + new_user.hashCode
                    + "\n\n If you are receiving this message in error, please notify us immediately and do not click on the link!");

            Transport.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    public static void sendReminder(models.User user) {
        try {
            Message message = setupMessage(username, user.user_name, null, "REMINDER: You have tutoring sessions coming up!");

            message.setText("Dear " + user.first_name + " " + user.last_name
                    + ",\n\n This is a friendly reminder that you have one or more tutoring sessions coming up in the next 24 hours. "
                    + "To avoid spamming you, you will only receive one message. Please log in to your account to review all upcoming sessions."
                    + "\n\n Note that there's currently no interface to cancel a session, so please be courteous and inform your tutor/tutee "
                    + "by e-mail if you cannot make it!"
                    + "\n\n http://localhost:9000"
                    + "\n\n If you are receiving this message in error, please notify us immediately.");

            Transport.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}