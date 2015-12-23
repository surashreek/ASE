package controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import play.*;
import play.api.mvc.Session;
import play.libs.Json;
import play.mvc.*;
import play.twirl.api.Html;
import views.html.*;
import models.*;
import play.data.Form;
import java.util.*;

import java.util.List;

public class User extends Controller {
    public Result home() {
        String Username = session("connected");
        models.User u = models.User.getUser(Username);
        if(Username == null || u == null || Username.isEmpty())
            return ok(exception.render("Please sign in first!"));

        return ok(home.render(session("connected"), Html.apply("")));
    }

    public Result getMySessions(){
        String username = Session.class.getName();
        if(username == null)
            return badRequest("<p> you are terrible</p>");
        models.User u = models.User.getUser(username);

        List<models.Session> requested = models.Session.getRequestedSession(u.user_id);
        List<models.Session> upcoming = models.Session.getUpcomingSession(u.user_id);
        List<models.Session> completed = models.Session.getCompletedSession(u.user_id);
        List<models.Session> tutoringRequests = models.Session.getTutoringRequest(u.user_id);

        return ok(mysessions.render(requested, upcoming, completed, tutoringRequests, u.user_id));
    }

    public Result profile() {
    	String username = session("connected");
        models.User user = models.User.getUser(username);
        if(username == null || user == null || username.isEmpty())
            return ok(exception.render("Please sign in first!"));
    	
        return ok(profile.render(user));
    }

    public Result beatutor() {
        String username = session("connected");
        models.User user = models.User.getUser(username);
        if(username == null || user == null || username.isEmpty())
            return ok(exception.render("Please sign in first!"));

        List<Course> lc = Course.getAllCourses();
        return ok(tutor.render(lc));
    }

    public Result addTutor() {
        Form f = Form.form().bindFromRequest();
        Map<String, String> map = f.data();

        ObjectNode response = Json.newObject();

        String description = map.get("description");
        int id_count = Integer.parseInt(map.get("id_count"));

        Set<Integer> ids = new HashSet<Integer>();

        for(int i = 0; i < id_count; i++){
            String parse = map.get("course" + Integer.toString(i));
            if (parse != null) {
                Integer in = Integer.parseInt(parse);
                ids.add(in);
            }
        }

        if(ids.size() == 0){
            response.put("status", "fail");
            response.put("message", "null-course");
            //response.put("message", "You need to select at least one course to tutor."/* jquery/ajax response */);
            return ok(response);
        }

        String Username = session("connected");
        models.User tutor = models.User.getUser(Username);
        if(Username == null || tutor == null || Username.isEmpty())
            return badRequest(exception.render("Please Sign-In first!"));

        for(Integer in : ids) {
            Tutor t = new Tutor();
            t.key.course_id = in;
            t.key.user_id = tutor.user_id;
            t.description = description;
            Tutor exists = Tutor.getTutor(new CompositeKey(tutor.user_id, in));
            if(exists == null)
                t.add();
            else{
                response.put("status", "fail");
                response.put("message", "duplicate-course");
                //response.put("message", "You are already a tutor for  " + exists.Course_name + ". No changes were made."/* jquery/ajax response */);
                return ok(response);
            }
        }

        response.put("status", "success");
        response.put("message", "signup-success");  //only for consistency

        Logger.info("hello");
        //response.put("message", "Your sign-ups were successful. Students can now see you as an available tutor for the subjects below."  /* jquery/ajax response */);
        return ok(response);

    }
}
