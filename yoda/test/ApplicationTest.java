import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.*;
import play.mvc.Result;

import org.junit.*;
import static org.junit.Assert.*;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.fakeRequest;
import static play.test.Helpers.route;


/**
*
* Simple (JUnit) tests that can call all parts of a play app.
* If you are interested in mocking a whole application, see the wiki for more details.
*
*/
public class ApplicationTest extends FakeApp {

    /***************************User**********************************/
    @Test
    public void createUserTest(){

                String username = "test";
                String password = "pass";
                String firstName = "first";
                String lastName = "last";
                String description = "description";
                models.User u = new User(username, password, firstName, lastName, description);

                assertEquals(u.user_name, username);
                assertEquals(u.password, password);
                assertEquals(u.first_name, firstName);
                assertEquals(u.last_name, lastName);
                assertEquals(u.description, description);

    }

    @Test
    public void getUserTest(){
                List<User> users = User.getAllUsers();
                assertTrue(users.size() > 0);

                for(User u : users) {
                    assertNotNull(u.user_id);
                    assertNotNull(u.user_name);
                    assertNotNull(u.password);
                    assertNotNull(u.first_name);
                    assertNotNull(u.last_name);
                    assertNotNull(u.description);

                    User test = User.getUser(u.user_name);
                    assertNotNull(test);
                    assertEquals(u.user_id, test.user_id);
                    assertEquals(u.user_name, test.user_name);
                    assertEquals(u.password, test.password);
                    assertEquals(u.first_name, test.first_name);
                    assertEquals(u.last_name, test.last_name);
                    assertEquals(u.description, test.description);

                    User test2 = User.getUserById(u.user_id);
                    assertNotNull(test);
                    assertEquals(u.user_id, test2.user_id);
                    assertEquals(u.user_name, test2.user_name);
                    assertEquals(u.password, test2.password);
                    assertEquals(u.first_name, test2.first_name);
                    assertEquals(u.last_name, test2.last_name);
                    assertEquals(u.description, test2.description);
                }

    }

    /*@Test
    public void AddUserTest(){
        String username = "test";
        String password = "pass";
        String firstName = "first";
        String lastName = "last";
        String description = "description";
        models.User u = new User(username, password, firstName, lastName, description);

        u.add();

        assertNotNull(u.user_id);
    }

    @Test
    public void UpdateUserTest(){
        String username = "gl2483@columbia.edu";
        String firstName = "Guanqi(updated)";
        String lastName = "Luo(updated)";
        String description = "new description";

        List<User> list = User.getAllUsers();
        for(User u : list){
            System.out.println(u.user_name);
        }

        User exist = User.getUser(username);
        assertNotNull(exist);

        exist.first_name = firstName;
        exist.last_name = lastName;
        exist.description = description;

        exist.updateUser();
        //re-get user
        exist = User.getUser(username);

        assertEquals(exist.first_name, firstName);
        assertEquals(exist.last_name, lastName);
        assertEquals(exist.description, description);
    }

    @Test
    public void RemoveUserTest(){
        String username = "test";

        User u = User.getUser(username);
        assertNotNull(u);

        u.deleteUser();

        //re-get user
        u = User.getUser(username);

        assertNull(u);
    }*/

    /***************************User**********************************/

    /***************************Tutor**********************************/
    @Test
    public void createTutorTest(){
        CompositeKey key = new CompositeKey(1,1);
        Tutor t = new Tutor();
        t.key = key;
        t.description = "wawawa";
        t.Tutor_name = "tname";
        t.Course_name = "Cname";

        assertEquals(t.key, key);
        assertEquals(t.description, "wawawa");
    }

    @Test
    public void getTutorTest(){
        List<Tutor> lt = Tutor.getAllTutors();
        assertTrue(lt.size() > 0);

        for(Tutor t : lt) {
            assertNotNull(t.Tutor_name);
            assertNotNull(t.Course_name);

            Tutor test = Tutor.getTutor(t.key);
            assertNotNull(test);

            List<Tutor> utest = Tutor.getTutorByUserId(t.key.user_id);
            assertTrue(utest.size() > 0);

            List<Tutor> ctest = Tutor.getTutorByCourseId(t.key.course_id, Tutor.getAllTutors());
            assertTrue(ctest.size() > 0);
        }
    }

    /***************************Tutor**********************************/

    /***************************Course**********************************/
    @Test
    public void createCourseTest(){
        Course c = new Course();
        c.name = "coursename";
        c.description = "course desc";

        assertEquals(c.name, "coursename");
        assertEquals(c.description, "course desc");

    }

    @Test
    public void getCoursesTest(){
        List<Course> lc = Course.getAllCourses();
        assertTrue(lc.size() > 0);

        for(Course c : lc){
            assertNotNull(c.course_id);

            Course test = Course.getCourse(c.name);
            assertNotNull(test);
        }
    }
    /***************************Course**********************************/


    /***************************Session**********************************/

    @Test
    public void getSessionTest(){
        List<Session> ls = Session.getAllSessions();
        assertTrue(ls.size() > 0);

        for(Session s : ls){
            assertNotNull(s);
            assertNotNull(s.course_name);
            assertNotNull(s.tutor_name);
            assertNotNull(s.tutee_name);

            List<Session> tutee = Session.getSessionsByTutee(s.tutee_id);
            assertTrue(tutee.size() >= 0);
            for(Session tee: tutee){
                assertEquals(tee.tutee_id, s.tutee_id);
            }

            List<Session> tutor = Session.getSessionsByTutor(s.tutor_id);
            assertTrue(tutor.size() >= 0);
            for(Session tor: tutor){
                assertEquals(tor.tutor_id, s.tutor_id);
            }
        }

    }

    @Test
    public void getRequestedTest(){
        List<Session> requested = Session.getRequestedSession(1);
        assertTrue(requested.size() >= 0);

        for(Session s : requested){
            assertEquals(s.status, "requested");
            assertTrue(s.tutee_id == 1);
            assertTrue(s.scheduled_time.after(new Date()));
        }
    }

    @Test
    public void getUpcomingTest(){
        List<Session> upcoming = Session.getUpcomingSession(1);
        assertTrue(upcoming.size() >= 0);

        for(Session s : upcoming){
            assertEquals(s.status, "upcoming");
            assertTrue(s.tutee_id == 1);
            assertTrue(s.scheduled_time.after(new Date()));
        }
    }

    @Test
    public void getCompletedTest(){
        List<Session> completed = Session.getCompletedSession(1);
        assertTrue(completed.size() >= 0);

        for(Session s : completed){
            assertTrue(s.tutee_id == 1);
            assertTrue(s.scheduled_time.before(new Date()));
        }
    }

    @Test
    public void getTutoringRequestTest(){
        List<Session> tutoring = Session.getTutoringRequest(1);
        assertTrue(tutoring.size() >= 0);

        for(Session s : tutoring){
            assertEquals(s.status, "requested");
            assertTrue(s.tutor_id == 1);
            assertTrue(s.scheduled_time.after(new Date()));
        }
    }

    /***************************Session**********************************/

    /***************************Rating**********************************/

    @Test
    public void getRatingTest(){
        List<Rating> lr = Rating.getAllRatings();
        assertTrue(lr.size() >= 0);

        for(Rating r : lr){
            Rating test = Rating.getRating(r.SessionId);
            assertNotNull(test);
            assertEquals(test.SessionId, r.SessionId);
        }
    }

    /***************************Rating**********************************/
    
    @Test
    public void testInvalidator(){
    	
    	User user = new User();
    	user.user_id = 5000;
    	user.user_name = "testuser@columbia.edu";
    	user.password = "somepassword";
    	user.creationDate = new Date();
    	
    	Calendar cal = Calendar.getInstance();
    	cal.setTime(user.creationDate);
    	cal.add(Calendar.DATE, -2); // subtract 2 days
    	 
    	user.creationDate = cal.getTime();
    	System.out.println("Creation Date is - " +user.creationDate);
        user.isActive = 0;
        user.hashCode = Math.abs(user.user_name.hashCode() + user.creationDate.hashCode());
        user.add();
        
        Result result = route(fakeRequest("GET", "/singleInvalidatorRun"));
    	
        User userOld = User.getUserById(5000);
        
        assertNull(userOld);
        
    }
}
