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

import static play.mvc.Http.Status.BAD_REQUEST;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.contentAsString;
import static play.test.Helpers.fakeRequest;
import static play.test.Helpers.route;


public class ControllerTest extends FakeApp{


    @Test
    public void testTutorPage(){
        Result result = route(fakeRequest("GET", "/beatutor").session("connected", "gl2483@columbia.edu"));
        assertEquals(OK, result.status());
        assertEquals("text/html", result.contentType());
    }

    @Test
    public void testProfilePage(){
        Result result = route(fakeRequest("GET", "/profile").session("connected", "gl2483@columbia.edu"));
        assertEquals(OK, result.status());
        assertEquals("text/html", result.contentType());
    }

    @Test
    public void testaddTutorNotSignedIn(){
        Map<String, String> data = new HashMap<String, String>();
        data.put("course0","1001");
        data.put("description", "This is from unit testing");
        Result result = route(fakeRequest("POST", "/addTutor")
                .bodyForm(data));

        assertEquals(BAD_REQUEST, result.status());
    }

    @Test
    public void testaddTutorAlreadyTutor(){
        Map<String, String> data = new HashMap<String, String>();
        data.put("course0","1001");
        data.put("description", "This is from unit testing");
        Result result = route(fakeRequest("POST", "/addTutor").session("connected", "gl2483@columbia.edu")
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
        assertTrue(bodyStr.contains("fail") && bodyStr.contains("You are already a tutor"));
    }

    @Test
    public void testaddTutorNoCourseSelected(){
        Map<String, String> data = new HashMap<String, String>();
        data.put("description", "This is from unit testing");
        Result result = route(fakeRequest("POST", "/addTutor").session("connected", "gl2483@columbia.edu")
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
        assertTrue(bodyStr.contains("fail") && bodyStr.contains("You need to select at least one course to tutor"));
    }

}
