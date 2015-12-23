package models;

import javax.persistence.*;

@Embeddable
public class CompositeKey{
    public Integer user_id;
    public Integer course_id;

    public CompositeKey(){}

    public CompositeKey(Integer u_id, Integer c_id){
        user_id = u_id;
        course_id = c_id;
    }

    @Override
    public int hashCode() {
        return user_id + course_id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass()) {
            return false;
        }
        CompositeKey pk = (CompositeKey)obj;
        if (pk.user_id.equals(user_id) && pk.course_id.equals(course_id)) {
            return true;
        }
        return false;
    }
}
