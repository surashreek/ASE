package models;

import java.util.*;
import com.avaje.ebean.Model;
import javax.persistence.*;

@Entity
@Table(name = "COURSES")
public class Course extends Model{

    @Id
    public Integer course_id;
    public String name;
    public String description;

    public static Finder<Integer, Course> find = new Finder<Integer, Course>(Course.class);

    public static List<Course> getAllCourses(){
        return find.all();
    }

    public static Course getCourse(String coursename){
        List<Course> lc = getAllCourses();
        for(Course c : lc){
            if(c.name.equals(coursename))
                return c;
        }
        return null;
    }

    public static Course getCourseById(int id) {
        List<Course> lc = getAllCourses();
        for (Course c : lc) {
            if (c.course_id == id) {
                return c;
            }
        }

        return null;
    }

    public void add(){
        this.save();
    }

    public void updateCourse(){
        this.update();
    }

    public void deleteCourse(){
        this.delete();
    }



}
