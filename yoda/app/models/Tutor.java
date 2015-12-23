package models;

import java.lang.Integer;
import java.util.*;
import java.util.HashMap;
import play.Logger;

import com.avaje.ebean.Model;
import javax.persistence.*;

@Entity
@Table(name = "TUTORS")
public class Tutor extends Model implements Comparable<Tutor>{

    public String description;

    @EmbeddedId
    public CompositeKey key;

    @Transient
    public String Tutor_name;
    @Transient
    public String Course_name;
    @Transient
    public String rating = "NR";

    public Tutor(){
        key = new CompositeKey();
    }

    public static Finder<CompositeKey, Tutor> find = new Finder<CompositeKey, Tutor>(Tutor.class);

    public static List<Tutor> getAllTutors(){
        List<Tutor> lt = find.all();
        for(Tutor t : lt){
            Course c = Course.find.byId(t.key.course_id);
            User u = User.find.byId(t.key.user_id);
            t.Course_name = c.name;
            t.Tutor_name = u.first_name + " " + u.last_name;
        }
        return lt;
    }

    public static Tutor getTutor(CompositeKey key){
        Tutor t = find.byId(key);

        if (t == null)
            return null;

        User u = User.find.byId(t.key.user_id);
        Course c = Course.find.byId(t.key.course_id);
        t.Course_name = c.name;
        t.Tutor_name = u.first_name + " " + u.last_name;
        return t;
    }

    public static List<Tutor> getTutorByUserId(Integer userid){
        List<Tutor> ret = new ArrayList<Tutor>();
        List<Tutor> lt = getAllTutors();
        for(Tutor t : lt){
            if(t.key.user_id.equals(userid))
                ret.add(t);
        }
        return ret;
    }

    public static List<Tutor> getSortedTutor(){
        List<Tutor> ret = Tutor.getAllTutors();
        List<Session> ls = Session.getAllSessions();
        for(Tutor t : ret) {
            List<Session> tutorSessions = new ArrayList<Session>();
            for(Session s : ls){
                if(s.course_id.equals(t.key.course_id) && s.tutor_id.equals(t.key.user_id))
                    tutorSessions.add(s);
            }
            Integer ratingSum = 0, counter = 0, averageRating = 0;
            for (Session s : tutorSessions) {
                if(s.rating > -1) {
                    ratingSum += s.rating;
                    counter++;
                }
            }

            if (counter != 0)
                averageRating = (ratingSum / counter);

            if(averageRating > 0) {
                t.rating = Integer.toString(averageRating) ;
            } else {
                t.rating = "NR";
            }

        }

        //Logger.info("-----");
        Collections.sort(ret, Comparators.RATING);
        return ret;

    }

    public static List<Tutor> getTutorByCourseId(Integer courseid, List<Tutor> lt){
        List<Tutor> ret = new ArrayList<Tutor>();
        Map<Tutor, Integer> tutorByRating = new HashMap<Tutor, Integer>();
        for (Tutor t : lt){
            if(t.key.course_id.equals(courseid))
                ret.add(t);
        }

        /* Compute hashmap with tutor and average rating */
        for (Tutor t : ret) {
            List<Session> tutorSessions = Session.getSessionsByTutor(t.key.user_id);

            Integer ratingSum = 0, counter = 0, averageRating = 0;
            for (Session s : tutorSessions) {
                //Logger.info("session Course = " + s.course_id);

                if (s.course_id.equals(courseid)) {
                    ratingSum += s.rating;
                    counter++;
                }
            }

            /* Compute average rating; if tutor has no sessions, set rating to 0 */
            if (counter != 0)
                averageRating = (ratingSum / counter);

            if(averageRating > 0) {
                t.rating = Integer.toString(averageRating) ;
            } else {
                t.rating = "NR";
            }

            tutorByRating.put(t, averageRating);
        }

        /* Sort hashmap by value */
        Map<Tutor, Integer> sortedTutor = sortByValue(tutorByRating);

        /* Get sorted tutors to return */
        List<Tutor> sortedTutorList = new ArrayList<Tutor>(sortedTutor.keySet());
        return sortedTutorList;
    }

    public void add(){
        this.save();
    }

    public void updateTutor(){
        this.update();
    }

    public void deleteTutor(){
        this.delete();
    }

    public static Map sortByValue(Map unsortedMap) {
        Map<Tutor, Integer> sortedMap = new TreeMap(new ValueComparator(unsortedMap));
        sortedMap.putAll(unsortedMap);
        return sortedMap;
    }

    @Override
    public int compareTo(Tutor o) {
        return Comparators.RATING.compare(this, o);
    }

    public static class Comparators {

        public static Comparator<Tutor> RATING = new Comparator<Tutor>() {
            @Override
            public int compare(Tutor o1, Tutor o2) {
                if(o1.rating.equals(o2.rating) )
                    return 0;
                else if( o1.rating.equals("NR") && !o2.rating.equals("NR"))
                    return 1;
                else if( !o1.rating.equals("NR") && o2.rating.equals("NR"))
                    return -1;
                else if( !o1.rating.equals("NR") && !o2.rating.equals("NR")){
                    if(Integer.parseInt(o1.rating) > Integer.parseInt(o2.rating))
                        return -1;
                    else
                        return 1;
                }
                else
                    return 0;
            }
        };
    }
}

class ValueComparator implements Comparator {

    Map<Tutor, Integer> map;

    public ValueComparator(Map<Tutor, Integer> map) {
        this.map = map;
    }

    public int compare(Object keyA, Object keyB) {
        Comparable valueA = (Comparable) map.get(keyA);
        Comparable valueB = (Comparable) map.get(keyB);
        return valueB.compareTo(valueA);
    }
}