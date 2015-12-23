import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.*;

import controllers.*;
import models.Session;
import org.junit.*;
import static org.junit.Assert.*;

import play.core.j.JavaResultExtractor;
import play.libs.Json;
import play.mvc.*;

import static play.mvc.Http.Status.BAD_REQUEST;
import static play.mvc.Http.Status.NOT_FOUND;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.contentAsString;
import static play.test.Helpers.fakeRequest;
import static play.test.Helpers.route;

public class SessionControllerTest extends FakeApp{

    @Test
    public void testMySessionPage(){
        Result result = route(fakeRequest("GET", "/mysessions").session("connected", "gl2483@columbia.edu"));
        assertEquals(OK, result.status());
        assertEquals("text/html", result.contentType());
    }

    @Test
    public void testMySessionPageNotSignedIn(){
        Result result = route(fakeRequest("GET", "/mysessions"));
        assertEquals(OK, result.status());
        assertEquals("text/html", result.contentType());
        assertTrue(contentAsString(result).contains("Please sign in first!"));
    }

    @Test
    public void testConfirmRejectSessionGood(){
        Map<String, String> data = new HashMap<String, String>();
        data.put("sessionId","111");
        data.put("status", "upcoming");
        data.put("reply", "This is from unit testing confirm reject session");

        Result result = route(fakeRequest("POST", "/confirmRejectSession").session("connected", "gl2483@columbia.edu")
                .bodyForm(data));

        assertEquals(OK, result.status());

        assertEquals("application/json", result.contentType());
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
        assertTrue(bodyStr.contains("success"));
    }

    @Test
    public void testConfirmRejectSessionBadSession(){
        Map<String, String> data = new HashMap<String, String>();
        data.put("sessionId","0");
        data.put("status", "upcoming");
        data.put("reply", "This is from unit testing confirm reject session");

        Result result = route(fakeRequest("POST", "/confirmRejectSession").session("connected", "gl2483@columbia.edu")
                .bodyForm(data));

        assertEquals(OK, result.status());

        assertEquals("text/html", result.contentType());
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
        assertTrue(bodyStr.contains("Session object should not be null"));
    }

    @Test
    public void testAddRating(){
        Map<String, String> data = new HashMap<String, String>();
        data.put("sessionId","111");
        data.put("rating", "2");
        data.put("comment", "This is from unit testing add rating");

        Result result = route(fakeRequest("POST", "/addRating").session("connected", "sg3163@columbia.edu")
                .bodyForm(data));

        assertEquals(NOT_FOUND, result.status());

        assertEquals("application/json", result.contentType());
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
        assertTrue(bodyStr.contains("success"));
        models.Session test = Session.find.byId(111);
        assertEquals((int)test.rating, 2);
    }
}
