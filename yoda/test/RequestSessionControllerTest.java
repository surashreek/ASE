import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.*;

import controllers.*;
import models.*;
import models.User;
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

public class RequestSessionControllerTest extends FakeApp{

    @Test
    public void testRequestSessionPage(){
        Result result = route(fakeRequest("GET", "/requestsession").session("connected", "gl2483@columbia.edu"));
        assertEquals(OK, result.status());
        assertEquals("text/html", result.contentType());
    }

    @Test
    public void testaddNewSessionNotSignedIn(){
        Map<String, String> data = new HashMap<String, String>();
        data.put("tutorid","2");
        data.put("datepicker", "12/03/2015");
        data.put("from", "15:00");
        data.put("from-time", "PM");
        data.put("location", "test location");
        data.put("coursename", "test coursename");
        data.put("courseid", "1001");
        Result result = route(fakeRequest("POST", "/addNewSession")
                .bodyForm(data));

        assertEquals(BAD_REQUEST, result.status());
    }

    @Test
    public void testaddNewSessionNoTutorSelected(){
        Map<String, String> data = new HashMap<String, String>();
        data.put("tutorid","0");
        data.put("datepicker", "12/03/2015");
        data.put("from", "15:00");
        data.put("from-time", "PM");
        data.put("location", "test location");
        data.put("coursename", "test coursename");
        data.put("courseid", "1001");
        Result result = route(fakeRequest("POST", "/addNewSession").session("connected", "gl2483@columbia.edu")
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
        assertTrue(bodyStr.contains("fail") && bodyStr.contains("Please select a Tutor"));
    }

    @Test
    public void testaddNewSessionNoCourseSelected(){
        Map<String, String> data = new HashMap<String, String>();
        data.put("tutorid","2");
        data.put("datepicker", "12/03/2015");
        data.put("from", "15:00");
        data.put("from-time", "PM");
        data.put("location", "test location");
        data.put("coursename", "test coursename");
        data.put("courseid", "0");
        Result result = route(fakeRequest("POST", "/addNewSession").session("connected", "gl2483@columbia.edu")
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
        assertTrue(bodyStr.contains("fail") && bodyStr.contains("Please select a course"));
    }

    @Test
    public void testaddNewSessionTutorNotExist(){
        Map<String, String> data = new HashMap<String, String>();
        data.put("tutorid","99999");
        data.put("datepicker", "12/03/2015");
        data.put("from", "15:00");
        data.put("from-time", "PM");
        data.put("location", "test location");
        data.put("coursename", "test coursename");
        data.put("courseid", "1001");
        Result result = route(fakeRequest("POST", "/addNewSession").session("connected", "gl2483@columbia.edu")
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
        assertTrue(bodyStr.contains("fail") && bodyStr.contains("Tutor does not exists"));
    }

    @Test
    public void testaddNewSessionBadDate(){
        Map<String, String> data = new HashMap<String, String>();
        data.put("tutorid","2");
        data.put("datepicker", "bad date");
        data.put("from", "15:00");
        data.put("from-time", "PM");
        data.put("location", "test location");
        data.put("coursename", "test coursename");
        data.put("courseid", "1001");
        Result result = route(fakeRequest("POST", "/addNewSession").session("connected", "gl2483@columbia.edu")
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
        assertTrue(bodyStr.contains("fail") && bodyStr.contains("Unable to parse datetime"));
    }

    @Test
    public void testaddNewSessionBadTime(){
        Map<String, String> data = new HashMap<String, String>();
        data.put("tutorid","2");
        data.put("datepicker", "12/03/2015");
        data.put("from", "15:00");
        data.put("from-time", "PM");
        data.put("location", "test location");
        data.put("coursename", "test coursename");
        data.put("courseid", "1001");
        Result result = route(fakeRequest("POST", "/addNewSession").session("connected", "gl2483@columbia.edu")
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
        assertTrue(bodyStr.contains("fail") && bodyStr.contains("Enter 0-12 for HH"));
    }

    @Test
    public void testaddNewSessionCheckTime(){
        Map<String, String> data = new HashMap<String, String>();
        data.put("tutorid","2");
        data.put("datepicker", "12/02/2015");
        data.put("from", "6:00");
        data.put("from-time", "PM");
        data.put("location", "test location");
        data.put("coursename", "test coursename");
        data.put("courseid", "1001");
        Result result = route(fakeRequest("POST", "/addNewSession").session("connected", "gl2483@columbia.edu")
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
        assertTrue(bodyStr.contains("fail") && bodyStr.contains("You cannot request a session that's before or less than an hour from now"));
    }


}
