import models.Session;
import models.User;
import org.junit.Test;
import play.core.j.JavaResultExtractor;
import play.mvc.Result;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.fakeRequest;
import static play.test.Helpers.route;

public class SpecialTest extends FakeApp{
    @Test
    public void testAddSessionsSameday(){
        for(int i = 1; i <= 15; i++){
            int time = i+6>12?i-6:i+6;
            String ap = i+6>11?"AM":"PM";
            Map<String, String> data = new HashMap<String, String>();
            data.put("tutorid","12");
            data.put("datepicker", "12/22/2015");
            data.put("from", Integer.toString(time)+":00");
            data.put("from-time", ap);
            data.put("location", "test location");
            data.put("coursename", "test coursename");
            data.put("courseid", "1001");
            Result result = route(fakeRequest("POST", "/addNewSession").session("connected", "meh@columbia.edu")
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


            if(i <= 4) {
                System.out.println("Im in 4");
                models.User meh = User.getUser("meh@columbia.edu");
                assertEquals(meh.num_flags, 15);
                System.out.println(meh.session_quota);
                assertEquals(meh.session_quota, i);
            }
            if(i >= 5 && i <= 8){
                System.out.println("Im in 8");
                models.User meh = User.getUser("meh@columbia.edu");
                assertEquals(meh.num_flags, 13);
                assertEquals(meh.session_quota, i);
            }
            if(i >= 9 && i <= 11){
                System.out.println("Im in 11");
                models.User meh = User.getUser("meh@columbia.edu");
                assertEquals(meh.num_flags, 11);
                assertEquals(meh.session_quota, i);
            }
            if(i >= 12 && i <= 14){
                System.out.println("Im in 14");
                models.User meh = User.getUser("meh@columbia.edu");
                assertEquals(meh.num_flags, 7);
                assertEquals(meh.session_quota, i);
            }

        }

        /*List<Session> deletes = Session.getRequestedSession(14);
        for(Session s: deletes) {
            s.deleteSession();
        }
        models.User meh = User.getUser("meh@columbia.edu");
        //System.out.println("num_flags = " + meh.num_flags);
        assertEquals(meh.num_flags, 0);
        assertEquals(meh.session_quota, 15);*/


    }

    @Test
    public void testConfirmRejectSessionGood(){
        List<Session> ls = Session.getTutoringRequest(12);
        int i = 1;
        for(Session s : ls) {
            Map<String, String> data = new HashMap<String, String>();
            data.put("sessionId", s.session_id.toString());
            data.put("status", "upcoming");
            data.put("reply", "This is from unit testing special confirm reject session");

            Result result = route(fakeRequest("POST", "/confirmRejectSession").session("connected", "something@columbia.edu")
                    .bodyForm(data));

            assertEquals(OK, result.status());

            assertEquals("application/json", result.contentType());
            byte[] body = JavaResultExtractor.getBody(result, 0L);
            String header = JavaResultExtractor.getHeaders(result).get("Content-Type");
            String charset = "utf-8";
            if (header != null && header.contains("; charset=")) {
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

            if(i <= 4) {
                models.User something = User.getUser("something@columbia.edu");
                assertEquals(something.num_flags, 15);
                assertEquals(something.session_quota, i);
            }
            if(i >= 5 && i <= 8){
                models.User something = User.getUser("something@columbia.edu");
                assertEquals(something.num_flags, 13);
                assertEquals(something.session_quota, i);
            }
            if(i >= 9 && i <= 11){
                models.User something = User.getUser("something@columbia.edu");
                assertEquals(something.num_flags, 11);
                assertEquals(something.session_quota, i);
            }
            if(i >= 12 && i <= 14){
                models.User something = User.getUser("something@columbia.edu");
                assertEquals(something.num_flags, 7);
                assertEquals(something.session_quota, i);
            }
            i++;
        }

        models.User something = User.getUser("something@columbia.edu");
        models.User meh = User.getUser("meh@columbia.edu");
        assertEquals(something.num_flags, 0);
        assertEquals(something.session_quota, 15);

        List<Session> deletes = Session.getUpcomingSession(14);
        for(Session s: deletes) {
            s.deleteSession();
        }
        meh.num_flags = 15;
        meh.session_quota = 0;
        meh.updateUser();
        something.num_flags = 15;
        something.session_quota = 0;
        something.updateUser();
    }

    @Test
    public void testAddZeroRating(){
        models.User wh = User.getUser("wh2307@columbia.edu");
        models.User ssk = User.getUser("ssk2197@columbia.edu");

        int wh_original = wh.num_flags;
        int ssk_original = ssk.num_flags;

        Map<String, String> data = new HashMap<String, String>();
        data.put("sessionId", "1005");
        data.put("rating", "0");
        data.put("comment", "This is from unit testing special rating");

        Result result = route(fakeRequest("POST", "/addRating").session("connected", "wh2307@columbia.edu")
                .bodyForm(data));

        assertEquals(OK, result.status());

        models.User wh2 = User.getUser("wh2307@columbia.edu");
        models.User ssk2 = User.getUser("ssk2197@columbia.edu");

        assertEquals(wh_original-1, wh2.num_flags);
        assertEquals(ssk_original-3, ssk2.num_flags);

        wh2.num_flags = wh_original;
        wh2.save();
        ssk2.num_flags = ssk_original;
        ssk2.save();
    }
}
