package controllers;

import play.data.Form;
import play.libs.Json;
import play.mvc.*;
import views.html.*;
import models.*;
import models.User;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class Sessions extends Controller {
    public Result mySessions() {
        String Username = session("connected");
        models.User u = models.User.getUser(Username);
        if(Username == null || u == null || Username.isEmpty())
            return ok(exception.render("Please sign in first!"));
/*
        List<Session> requested = Session.getRequestedSession(u.user_id);
        List<Session> upcoming = Session.getUpcomingSession(u.user_id);
        List<Session> completed = Session.getCompletedSession(u.user_id);
        List<Session> tutoringRequests = Session.getTutoringRequest(u.user_id);
*/

        List<Session> requested = new ArrayList<Session>();
        List<Session> upcoming = new ArrayList<Session>();
        List<Session> completed = new ArrayList<Session>();
        List<Session> tutoringRequests = new ArrayList<Session>();

        Session.fillSessionBuckets(requested, upcoming, completed, tutoringRequests, u);

        return ok(mysessions.render(requested, upcoming, completed, tutoringRequests, u.user_id));
    }
    
    public Result confirmRejectSession() {
    	
    	Form f = Form.form().bindFromRequest();
        Map<String, String> map = f.data();
        
        ObjectNode response = Json.newObject();
        
        String sessionId = map.get("sessionId");
        String status = map.get("status");

        // Send e-mail based on accept/decline. not tested yet; I'm afraid that the sessionObj will be null here.
        Session sessionObj = null;

        List<Session> sessionList = Session.getAllSessions();

        for (Session x : sessionList) {
            if (x.session_id == Integer.parseInt(sessionId)) {
                sessionObj = x;
                break;
            }
        }

        if(sessionObj == null){
            return notFound(exception.render("Session object should not be null and should never happened unless during unit test."));
        }

        models.User tutor = models.User.getUserById(sessionObj.tutor_id);
        models.User tutee = models.User.getUserById(sessionObj.tutee_id);

        String s_course = sessionObj.course_name;
        Date s_d = sessionObj.scheduled_time;
        String s_loc = sessionObj.location;

        Session.updateStatus(Integer.parseInt(sessionId), status);
        String reply = map.get("reply");

        try {
            if (status.equals("upcoming")) {
            	
            	if(checkAccountBan(tutor)) {
            		// Redirect user to banned page
            		session().clear();
            	//	return forbidden(exception.render("This account has been locked out due to suspicious behavior. If you feel this was done in error, please contact the administrator."));
            	}
            	
                newEmailer.sendAccept(tutor, tutee, s_course, s_d, URLDecoder.decode(reply, "UTF-8"), s_loc);
            } else if (status.equals("rejected")) {
                newEmailer.sendDecline(tutor, tutee, s_course, s_d, URLDecoder.decode(reply, "UTF-8"), s_loc);
            }
        }
        catch (UnsupportedEncodingException e) {
            return notFound(exception.render("Oops...something went wrong."));
        }

        response.put("status", "success");
        
        return ok(response);
    }

    public Result addRating() {
        Form f = Form.form().bindFromRequest();
        Map<String, String> map = f.data();

        ObjectNode response = Json.newObject();
        Integer sessionId = Integer.parseInt(map.get("sessionId"));
        Integer rating = Integer.parseInt(map.get("rating"));
        String comment = map.get("comment");
        
        Session.updateRating(sessionId, rating, comment);
        
        if(rating == 0) {
        	Session session = Session.getSessionById(sessionId);
        	models.User tutee = models.User.getUserById(session.tutee_id);
        	models.User tutor = models.User.getUserById(session.tutor_id);
        	
        	boolean isBanned = tutee.decrementNumFlags(1);
        	tutor.decrementNumFlags(3);
        	// If banned then call the logic to log out
        	if(isBanned) {
        		session().clear();
                response.put("status", "ban");
                return ok(response);
        	//	return forbidden(exception.render("This account has been locked out due to suspicious behavior. If you feel this was done in error, please contact the administrator."));
        	}
        }
        
        response.put("status", "success");
        return ok(response);
    }

    // loop through all sessions; if a reminder has not been sent, the status is upcoming, and neither the tutor or tutee has been sent a reminder, send a reminder.
    // we then add both tutor and tutee to a list of user IDs so that we don't risk spamming people with too many reminders.
    // Do this every 6 hours.

    public Result sendReminders() {

        try {
            for (; ; ) {
                Date now = new Date();
                List<Integer> reminded = new ArrayList<>();

                List<Session> allSessions = Session.getAllSessions();

                for (Session x : allSessions) {
                    if (x.scheduled_time.before(now) && x.status.equalsIgnoreCase("UPCOMING")) {
                        x.status = "COMPLETE";
                        x.updateSession();
                        continue; // don't send a reminder if it's in the past
                    }

                    if (!reminded.contains(x.tutee_id) && !reminded.contains(x.tutor_id)) { // only sent a reminder once, to avoid spam
                        if (!x.email_sent && x.status.equalsIgnoreCase("UPCOMING") && (x.scheduled_time.getTime() - now.getTime() < 3600000)) {
                            models.User tutor = models.User.getUserById(x.tutor_id);
                            models.User tutee = models.User.getUserById(x.tutee_id);

                            newEmailer.sendReminder(tutor);
                            newEmailer.sendReminder(tutee);
                            reminded.add(tutor.user_id);
                            reminded.add(tutee.user_id);
                            x.email_sent = true;
                            x.updateSession();
                        }
                    }
                }

                System.out.println("Reminders sent on " + now.toString());
                TimeUnit.HOURS.sleep(6);
            }
        }
        catch (InterruptedException e) {
            return ok(exception.render("(" + e.getMessage() + ") Invalidator stopped running at " + new Date()));
        }
    }
    
private boolean checkAccountBan(models.User tutor) {
    	
    	int count = tutor.incrementSessionQuota();
    	boolean userBanned = false;
    	
    	if(count ==5) {
    		userBanned = tutor.decrementNumFlags(2);
    	}else if (count == 9) {
    		userBanned = tutor.decrementNumFlags(2);
        }else if (count == 12) {
            userBanned = tutor.decrementNumFlags(4);
    	}else if (count == 15) {
    		userBanned = tutor.decrementNumFlags(15);
    	}
    	
    	// Check if user has equal to or more than 15 sessions in a day, if so, ban the user
    	List<Session> sessions = Session.getAllSessions();
    	Date todayDate = new Date();
    	int daySessioncount = 0;
    	for(Session session : sessions) {
    		if(session.tutor_id.equals(tutor.user_id)) {
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
