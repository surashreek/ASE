package models;

import java.util.*;

import javax.persistence.*;
import com.avaje.ebean.Model;
import org.joda.time.DateTime;

@Entity
@Table(name = "YODA_SESSION")
public class Session extends Model{

    @Id
    public Integer session_id;
    public Integer tutor_id;
    public Integer tutee_id;
    public Date scheduled_time;
    public String location;
    public String status;
    public boolean email_sent;
    public Integer course_id;
    public String course_name;
    public Integer rating;
    public String user_comment;

    @Transient
    public String tutor_name;
    @Transient
    public String tutee_name;


    public static Finder<Integer, Session> find = new Finder<Integer, Session>(Session.class);

    public static List<Session> getAllSessions(){
        return find.all();
    }
    
    public static Session getSessionById(int sessionId){
    	
    	 List<Session> su = getAllSessions();
         for(Session s : su){
             if(s.session_id == sessionId){
                 return s;
             }
         }
        return null;
    }

    public static void fillSessionBuckets(List<Session> requested, List<Session> upcoming, List<Session> completed, List<Session> tutoringRequests, User me){
        List<Session> allSessions = Session.getAllSessions();
        Iterator<Session> iter = allSessions.iterator();
        List<User> allUsers = User.getAllUsers();

        while (iter.hasNext()) {
            Session s = iter.next();

            if (s.tutee_id.equals(me.user_id)) {
                User other = User.retrieveUserFromList(s.tutor_id, allUsers);
                s.tutor_name = other.first_name + " " + other.last_name;
                s.tutee_name = me.first_name + " " + me.last_name;

                if (s.status.toLowerCase().equals("upcoming") && s.scheduled_time.after(new Date())) {
                    upcoming.add(s);
                }
                else if (s.status.toLowerCase().equals("requested") && s.scheduled_time.after(new Date())) {
                    requested.add(s);
                }
                else if (s.scheduled_time.before(new Date()) && s.status.equalsIgnoreCase("upcoming")) {
                    completed.add(s);
                }
            }
            else if (s.tutor_id.equals(me.user_id)) {
                User other = User.retrieveUserFromList(s.tutee_id, allUsers);
                s.tutor_name = me.first_name + " " + me.last_name;
                s.tutee_name = other.first_name + " " + other.last_name;

                if (s.status.toLowerCase().equals("upcoming") && s.scheduled_time.after(new Date())) {
                    upcoming.add(s);
                }
                if (s.status.toLowerCase().equals("requested") && s.scheduled_time.after(new Date())) {
                    tutoringRequests.add(s);
                }
                else if (s.scheduled_time.before(new Date()) && s.status.equalsIgnoreCase("upcoming")) {
                    completed.add(s);
                }
            }
        }

        Collections.sort(requested, Datetime);
        Collections.sort(upcoming, Datetime);
        Collections.sort(completed, Datetime);
        Collections.sort(tutoringRequests, Datetime);
    }

    public static List<Session> getSessionsByTutor(Integer tutorid){
        List<Session> ret = new ArrayList<Session>();
        List<Session> ls = getAllSessions();
        for(Session s : ls){
            if(s.tutor_id.equals(tutorid)) {
                User tutor = User.find.byId(s.tutor_id);
                User tutee = User.find.byId(s.tutee_id);
                s.tutor_name = tutor.first_name + " " + tutor.last_name;
                s.tutee_name = tutee.first_name + " " + tutee.last_name;
                ret.add(s);
            }
        }

        return ret;
    }

    public static List<Session> getSessionsByTutee(Integer tuteeid){
        List<Session> ret = new ArrayList<Session>();
        List<Session> ls = getAllSessions();
        for(Session s : ls){
            if(s.tutee_id.equals(tuteeid)) {
                User tutor = User.find.byId(s.tutor_id);
                User tutee = User.find.byId(s.tutee_id);
                s.tutor_name = tutor.first_name + " " + tutor.last_name;
                s.tutee_name = tutee.first_name + " " + tutee.last_name;
                ret.add(s);
            }
        }

        return ret;
    }

    public static List<Session> getSessionsByTutorKey(CompositeKey key){
        //List<Session> ret = find.where().ieq("COURSE_ID", courseId.toString()).findList();
        List<Session> ret = new ArrayList<Session>();
        List<Session> ls = getAllSessions();
        for(Session s : ls){
            if(s.course_id.equals(key.course_id) && s.tutor_id.equals(key.user_id)) {
                User tutor = User.find.byId(s.tutor_id);
                User tutee = User.find.byId(s.tutee_id);
                s.tutor_name = tutor.first_name + " " + tutor.last_name;
                s.tutee_name = tutee.first_name + " " + tutee.last_name;
                ret.add(s);
            }
        }

        return ret;
    }
    
    public static void updateStatus(Integer sessionId, String status) {
    	
    	Session session = Session.find.byId(sessionId);
    	session.status = status;
    	session.save();
    }

    public  static void updateRating(Integer sessionId, Integer rating, String comment) {
        Session session = Session.find.byId(sessionId);
        session.rating = rating;
        session.user_comment = comment;
        session.save();
    }

    public static List<Session> getRequestedSession(Integer tuteeid){
        List<Session> tuteeSessions = getSessionsByTutee(tuteeid);
        List<Session> result = new ArrayList<Session>();
        Date now = new Date();
        for(Session s : tuteeSessions){
            s.rating = -1;
            s.user_comment = "";
            if((s.status.toLowerCase().equals("requested")) && (s.scheduled_time.after(now)))
                result.add(s);
        }
        Collections.sort(result, Datetime);
        return result;
    }

    public static List<Session> getUpcomingSession(Integer tuteeid){
        List<Session> tuteeSessions = getSessionsByTutee(tuteeid);
        tuteeSessions.addAll(getSessionsByTutor(tuteeid));
        List<Session> result = new ArrayList<Session>();
        for(Session s : tuteeSessions) {
            s.rating = -1;
            s.user_comment = "";
            if(s.status.toLowerCase().equals("upcoming") && s.scheduled_time.after(new Date()))
                result.add(s);

        }
        Collections.sort(result, Datetime);
        return result;
    }

    public static List<Session> getCompletedSession(Integer tuteeid){
        List<Session> tuteeSessions = getSessionsByTutee(tuteeid);
        List<Session> result = new ArrayList<Session>();

        /* These sessions should have a rating, if they don't, an appropriate action should be taken-
         * Tutor: Should see a message saying: "Waiting for your tutee to rate this session."
         * Tutee: Should see the rating button and comment box.
         *
         * If a rating exists,
         * Tutor: Should see his rating and comment
         * Tutee: Should see his rating and comment, with no option to edit it (for v.0.2)
         */
        for(Session s : tuteeSessions){
            Integer sessionId = s.session_id;
            if(s.scheduled_time.before(new Date()) && s.status.equalsIgnoreCase("upcoming")) //only checks datetime
                result.add(s);
        }
        Collections.sort(result, Datetime);
        return result;
    }

    public static List<Session> getTutoringRequest(Integer tutorid){
        List<Session> tuteeSessions = getSessionsByTutor(tutorid);
        List<Session> result = new ArrayList<Session>();
        Date now = new Date();
        for(Session s : tuteeSessions){
            s.rating = -1;
            s.user_comment = "";
            if(s.status.toLowerCase().equals("requested") && s.scheduled_time.after(now))
                result.add(s);
        }
        Collections.sort(result, Datetime);
        return result;
    }

    public void add(){
        this.save();
    }

    public void updateSession(){
        this.update();
    }

    public void deleteSession(){
        this.delete();
    }


    public static Comparator<Session> Datetime = new Comparator<Session>() {
        @Override
        public int compare(Session o1, Session o2) {
            if(o1.scheduled_time.before(o2.scheduled_time))
                return -1;
            else
                return 1;
        }
    };
}
