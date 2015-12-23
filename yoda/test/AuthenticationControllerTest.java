import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.*;

import controllers.*;
import org.junit.*;
import static org.junit.Assert.*;

import play.core.j.JavaResultExtractor;
import play.libs.Json;
import play.mvc.*;

import static play.mvc.Controller.session;
import static play.mvc.Http.Status.*;
import static play.mvc.Results.forbidden;
import static play.test.Helpers.contentAsString;
import static play.test.Helpers.fakeRequest;
import static play.test.Helpers.route;

public class AuthenticationControllerTest extends FakeApp{

    @Test
    public void testAuthenticateGood(){
        Map<String, String> data = new HashMap<String, String>();
        data.put("user_name","gl2483@columbia.edu");
        data.put("password", "anotherpassword");
        Result result = route(fakeRequest("POST", "/authenticate")
                .bodyForm(data));

        assertEquals(OK, result.status());
        //assertEquals(session().get("connected"), "gl2483@columbia.edu");
    }

    @Test
    public void testAuthenticateWrongPassword(){
        Map<String, String> data = new HashMap<String, String>();
        data.put("user_name","gl2483@columbia.edu");
        data.put("password", "wrongpassword");
        Result result = route(fakeRequest("POST", "/authenticate")
                .bodyForm(data));

        assertEquals(FORBIDDEN, result.status());

        byte[] body = JavaResultExtractor.getBody(result, 0L);
        String header = JavaResultExtractor.getHeaders(result).get("Content-Type");
        String charset = "utf-8";
        if(header != null && header.contains("; charset=")){
            charset = header.substring(header.indexOf("; charset=") + 10, header.length()).trim();
        }
        String bodyStr = null;
        try {
            bodyStr = new String(body, charset);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //System.out.println(bodyStr);
        assertTrue(bodyStr.contains("The password you entered does not match our records."));
    }

    @Test
    public void testAuthenticateWrongUser(){
        Map<String, String> data = new HashMap<String, String>();
        data.put("user_name","wrong@columbia.edu");
        data.put("password", "wrongpassword");
        Result result = route(fakeRequest("POST", "/authenticate")
                .bodyForm(data));

        assertEquals(NOT_FOUND, result.status());

    }

    @Test
    public void testAuthenticateNotActivated(){
        Map<String, String> data = new HashMap<String, String>();
        data.put("user_name","lol@columbia.edu");
        data.put("password", "something");
        Result result = route(fakeRequest("POST", "/authenticate")
                .bodyForm(data));

        assertEquals(FORBIDDEN, result.status());

        byte[] body = JavaResultExtractor.getBody(result, 0L);
        String header = JavaResultExtractor.getHeaders(result).get("Content-Type");
        String charset = "utf-8";
        if(header != null && header.contains("; charset=")){
            charset = header.substring(header.indexOf("; charset=") + 10, header.length()).trim();
        }
        String bodyStr = null;
        try {
            bodyStr = new String(body, charset);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //System.out.println(bodyStr);
        assertTrue(bodyStr.contains("You may not log in until you validate your registration. Please check your e-mail!"));
    }

    @Test
    public void testSignupUserEmpty(){
        Map<String, String> data = new HashMap<String, String>();
        data.put("user_name","unittest@columbia.edu");
        data.put("first_name","unit");
        data.put("last_name", "test");

        Result result = route(fakeRequest("POST", "/signupUser")
                .bodyForm(data));

        assertEquals(NOT_FOUND, result.status());

        byte[] body = JavaResultExtractor.getBody(result, 0L);
        String header = JavaResultExtractor.getHeaders(result).get("Content-Type");
        String charset = "utf-8";
        if(header != null && header.contains("; charset=")){
            charset = header.substring(header.indexOf("; charset=") + 10, header.length()).trim();
        }
        String bodyStr = null;
        try {
            bodyStr = new String(body, charset);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //System.out.println(bodyStr);
        assertTrue(bodyStr.contains("Username, First Name, Last Name and Password are mandatory!"));
    }

    @Test
    public void testSignupUserNotColumbia(){
        Map<String, String> data = new HashMap<String, String>();
        data.put("user_name","unittest@notcolumbia.edu");
        data.put("password", "password");
        data.put("first_name","unit");
        data.put("last_name", "test");
        data.put("description", "unittest");

        Result result = route(fakeRequest("POST", "/signupUser")
                .bodyForm(data));

        assertEquals(NOT_FOUND, result.status());

        byte[] body = JavaResultExtractor.getBody(result, 0L);
        String header = JavaResultExtractor.getHeaders(result).get("Content-Type");
        String charset = "utf-8";
        if(header != null && header.contains("; charset=")){
            charset = header.substring(header.indexOf("; charset=") + 10, header.length()).trim();
        }
        String bodyStr = null;
        try {
            bodyStr = new String(body, charset);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //System.out.println(bodyStr);
        assertTrue(bodyStr.contains("Thanks for your interest. But Sorry, only Columbia Students with Columbia email id can signup"));
    }

    @Test
    public void testSignupUserAlreadyUser(){
        Map<String, String> data = new HashMap<String, String>();
        data.put("user_name","gl2483@columbia.edu");
        data.put("password", "password");
        data.put("first_name","unit");
        data.put("last_name", "test");
        data.put("description", "unittest");

        Result result = route(fakeRequest("POST", "/signupUser")
                .bodyForm(data));

        assertEquals(NOT_FOUND, result.status());

        byte[] body = JavaResultExtractor.getBody(result, 0L);
        String header = JavaResultExtractor.getHeaders(result).get("Content-Type");
        String charset = "utf-8";
        if(header != null && header.contains("; charset=")){
            charset = header.substring(header.indexOf("; charset=") + 10, header.length()).trim();
        }
        String bodyStr = null;
        try {
            bodyStr = new String(body, charset);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //System.out.println(bodyStr);
        assertTrue(bodyStr.contains("That e-mail is already registered. Please try logging-in"));
    }

    @Test
    public void testValidateSignupNotFound(){

        Result result = route(fakeRequest("GET", "/validate?user=123456789"));

        assertEquals(NOT_FOUND, result.status());

        byte[] body = JavaResultExtractor.getBody(result, 0L);
        String header = JavaResultExtractor.getHeaders(result).get("Content-Type");
        String charset = "utf-8";
        if(header != null && header.contains("; charset=")){
            charset = header.substring(header.indexOf("; charset=") + 10, header.length()).trim();
        }
        String bodyStr = null;
        try {
            bodyStr = new String(body, charset);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //System.out.println(bodyStr);
        assertTrue(bodyStr.contains("Your registration could not be found"));
    }

    @Test
    public void testValidateSignupAlreadyActive(){

        Result result = route(fakeRequest("GET", "/validate?user=131828937"));

        assertEquals(OK, result.status());

        byte[] body = JavaResultExtractor.getBody(result, 0L);
        String header = JavaResultExtractor.getHeaders(result).get("Content-Type");
        String charset = "utf-8";
        if(header != null && header.contains("; charset=")){
            charset = header.substring(header.indexOf("; charset=") + 10, header.length()).trim();
        }
        String bodyStr = null;
        try {
            bodyStr = new String(body, charset);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //System.out.println(bodyStr);
        assertTrue(bodyStr.contains("This account is already active, young Jedi!"));
    }
}
