/**
 * Created by wh2307 on 12/2/15.
 */

import models.User;
import play.mvc.*;
import controllers.newEmailer;

import static play.mvc.Http.Status.*;
import static play.test.Helpers.fakeRequest;
import static play.test.Helpers.route;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.Address;
import org.junit.*;
import static org.junit.Assert.*;

public class newEmailersTest extends FakeApp {
    @Test
    public void testSetupMessage() throws MessagingException {
        models.User from = new User("wh2307@columbia.edu", "foo", "yoda", "jedi", "this is a test");
        models.User to = new User("yoda@jediacademy.com", "bar", "obi-wan", "kenobi", "this is a test");
        models.User cc = new User("vader@jediacademy.com", "bar", "obi-wan", "kenobi", "this is a test");
        String subject = "THIS IS A TEST";

        Message testMessage = newEmailer.setupMessage(from.user_name, to.user_name, cc.user_name, subject);

        Address[] a_to = testMessage.getRecipients(Message.RecipientType.TO);
        Address[] a_from = testMessage.getFrom();
        Address[] a_cc = testMessage.getRecipients(Message.RecipientType.CC);

        assertEquals(a_to.length, 1);
        assertEquals(a_from.length, 1);
        assertEquals(a_cc.length, 1);

        assertEquals(from.user_name, a_from[0].toString());
        assertEquals(to.user_name, a_to[0].toString());
        assertEquals(cc.user_name, a_cc[0].toString());
        assertEquals(subject, testMessage.getSubject());
    }
}
