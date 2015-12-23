package controllers;

import java.util.*;

import com.fasterxml.jackson.databind.node.*;
import models.*;
import models.User;
import play.data.Form;
import play.mvc.*;
import views.html.*;
import play.libs.Json;
import java.text.*;


public class RequestSession extends Controller {

    public Result index(){
        String username = session("connected");
        models.User user = models.User.getUser(username);
        if(username == null || user == null || username.isEmpty())
            return ok(exception.render("Please sign in first!"));

        List<Course> lc = Course.getAllCourses();
        List<User> lu = User.getAllUsers();
        User x = User.getUser(session("connected"));
        /*List<Tutor> allTutors = Tutor.getAllTutors();
        List<Tutor> lt = new ArrayList<>();

        for(Course c: lc) {
        	lt.addAll(Tutor.getTutorByCourseId(c.course_id, allTutors));
        }*/

        List<Tutor> lt = Tutor.getSortedTutor();

        Iterator<Tutor> iter = lt.iterator();
        while(iter.hasNext()){
            Tutor t = iter.next();
            User q = User.retrieveUserFromList(t.key.user_id, lu);

            if(t.key.user_id.equals(x.user_id) || q.num_flags == 0)
                iter.remove();
        }

        return ok(requestSession.render(lc, lt));
    }

    public Result AddNewSession(){

        ObjectNode response = Json.newObject();

        Form f = Form.form().bindFromRequest();

        Map<String, String> map = f.data();
        Integer tutorid = Integer.parseInt(map.get("tutorid"));

        if(tutorid == 0){
            response.put("status", "fail");
            response.put("message", "Please select a Tutor!"/* jquery/ajax response */);
            return ok(response);
        }

        String date = map.get("datepicker");
        String time = map.get("from");
        String longtime = time + " " + map.get("from-time");
        String datetime = date + " " + longtime;
        String location = map.get("location");
        String coursename = map.get("coursename");
        Integer courseid = Integer.parseInt(map.get("courseid"));

        if(courseid == 0){
            response.put("status", "fail");
            response.put("message", "Please select a course!"/* jquery/ajax response */);
            return ok(response);
        }

        String Username = session("connected");
        models.User tutee = models.User.getUser(Username);
        if(Username == null || tutee == null || Username.isEmpty())
            return badRequest(exception.render("Please Sign-In first!"));

        models.User tutor = User.find.byId(tutorid);
        if(tutor == null) {
            response.put("status", "fail");
            response.put("message", "Tutor does not exists!"/* jquery/ajax response */);
            return ok(response);
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a", Locale.US);
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm");

        Date d, t, check;
        try {
            t = timeFormat.parse(time);
            d = dateFormat.parse(datetime);
            check = timeFormat.parse("13:00");
            if(t.after(check)){
                response.put("status", "fail");
                response.put("message", "Enter 0-12 for HH"/* jquery/ajax response */);
                return ok(response);
            }

        }catch (ParseException ex){
            //ex.printStackTrace();
            response.put("status", "fail");
            response.put("message", "Unable to parse datetime"/* jquery/ajax response */);
            return ok(response);
        }

        Date now = new Date();
        if( (d.getTime() - now.getTime()) <= 3600000 ){ //less than an hour
            response.put("status", "fail");
            response.put("message", "You cannot request a session that's before or less than an hour from now");
            return ok(response);
        }

        List<Session> ls = Session.getUpcomingSession(tutee.user_id);
        for(Session s : ls){
            if( Math.abs(s.scheduled_time.getTime() - d.getTime()) <= 3600000 ){
                response.put("status", "fail");
                response.put("message", "You have an upcoming session less than an hour from this request");
                return ok(response);
            }
        }

        Session newSession = new Session();
        newSession.tutee_id = tutee.user_id;
        newSession.tutor_id = tutor.user_id;
        newSession.location = location;
        newSession.scheduled_time = d;
        newSession.course_id = courseid;
        newSession.course_name = coursename;
        newSession.status = "requested";
        newSession.rating = -1;

        newSession.add();
        
        if(checkAccountBan(newSession.tutee_id)) {
        	// Account is banned, redirect to Exception page
        	session().clear();
            response.put("status", "ban");
            return ok(response);
      //  	return forbidden(exception.render("This account has been locked out due to suspicious behavior. If you feel this was done in error, please contact the administrator."));
        }

        response.put("status", "success");
        response.put("message", "Your tutoring session has been requested. An e-mail has been sent to your tutor to accept or decline your invitation."/* jquery/ajax response */);


        String personalMessage = map.get("message");
        newEmailer.sendRequest(tutor.first_name, tutor.last_name, tutor.user_name, coursename, d, personalMessage, location);

        return ok(response);
    }
    
    private boolean checkAccountBan(Integer tutee_id) {

    	User user = User.getUserById(tutee_id);
    	int count = user.incrementSessionQuota();
    	boolean userBanned = false;
    	
    	if(count ==5) {
    		userBanned = user.decrementNumFlags(2);
    	}else if (count == 9) {
    		userBanned = user.decrementNumFlags(2);
    	}else if (count == 12) {
            userBanned = user.decrementNumFlags(4);
        }else if (count == 15) {
    		userBanned = user.decrementNumFlags(15);
    	}
    	
    	// Check if user has equal to or more than 15 sessions in a day, if so, ban the user
    	List<Session> sessions = Session.getAllSessions();
    	Date todayDate = new Date();
    	int daySessioncount = 0;
    	for(Session session : sessions) {
    		if(session.tutee_id.equals(tutee_id)) {
    			Date scheduleDate = session.scheduled_time;
    			if(scheduleDate.getYear() == todayDate.getYear() && scheduleDate.getMonth() == todayDate.getMonth() &&
    					scheduleDate.getDate() == todayDate.getDate()) {
    				daySessioncount++;
    			}
    		}
    	}
    	if(daySessioncount >=15) {
    		return true;
    	}
    	return userBanned;
    }
}
