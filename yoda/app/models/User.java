//package models;
//
//import java.util.*;
//
//import com.avaje.ebean.Model;
//import javax.persistence.*;
//
//@Entity
//@Table(name = "YODA_USER")//table mapping
//public class User extends Model{
//
//    @Column(name = "user_id") //column mapping
//    public Integer user_id;
//    public String user_name;
//    public String password;
//    public String first_name;
//    public String last_name;
//    public String description;
//
//    public User(String pUserName, String pPassword, String pFirstName, String pLastName, String pDescription){
//        user_name = pUserName;
//        first_name = pFirstName;
//        last_name = pLastName;
//        description = pDescription;
//        password = pPassword;
//    }
//
//
//
//    public static Finder<String, User> find = new Finder<String, User>(User.class);
//
//    public static List<User> getAllUsers(){
//        return find.all();
//    }
//
//    public static User getUser(String username){
//
//        return find.byId(username);
//    }
//
//
//
//}
package models;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.*;

import com.avaje.ebean.Model;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.persistence.*;

@Entity
@Table(name = "YODA_USER")//table mapping
public class User extends Model{

    @Id
    @Column(name = "USER_ID") //column mapping
    public Integer user_id;
    @Column(name = "USER_NAME")
    public String user_name;
    @Column(name = "PASSWORD")
    public String password;
    @Column(name = "FIRST_NAME")
    public String first_name;
    @Column(name = "LAST_NAME")
    public String last_name;
    @Column(name = "DESCRIPTION")
    public String description;
    @Column(name = "CREATION_DATE")
    public Date creationDate;
    @Column(name = "IS_ACTIVE")
    public int isActive;
    @Column(name = "HASH")
    public int hashCode;
    @Column(name = "NUM_FLAGS")
    public int num_flags;
    @Column(name = "SESSION_QUOTA")
    public int session_quota;

    public User() {
    	
    }

    public User(String pUserName, String pPassword, String pFirstName, String pLastName, String pDescription){
        user_name = pUserName;
        first_name = pFirstName;
        last_name = pLastName;
        description = pDescription;
        password = pPassword;
    }



    public static Finder<Integer, User> find = new Finder<Integer, User>(User.class);

    public static List<User> getAllUsers(){
        return find.all();
    }

    public static User getUser(String username){
        List<User> lu = getAllUsers();
        for(User u : lu){
            if(u.user_name.equals(username)){
                return u;
            }
        }
        return null;
    }

    public static User getUserById(int userid){
        List<User> lu = getAllUsers();
        for(User u : lu){
            if(u.user_id == userid){
                return u;
            }
        }
        return null;
    }

    public static User getUserByHash(int hashCode){
        List<User> lu = getAllUsers();
        for(User u : lu){
            if(u.hashCode == hashCode){
                return u;
            }
        }
        return null;
    }

    public void add(){
    //    try {
       //     this.password = pwHash(this.password);
            this.save();
    //    }catch(NoSuchAlgorithmException|InvalidKeySpecException ex) {
    //        ex.printStackTrace();
    //    }
    }

    public static User retrieveUserFromList(int user_id, List<User> list) {
        Iterator<User> iter = list.iterator();

        while (iter.hasNext()) {
            User x = iter.next();
            if (x.user_id.equals(user_id)) {
                return x;
            }
        }

        return null;
    }

    public void updateUser(){
        this.update();
    }

    public void deleteUser(){
        this.delete();
    }

    /*public boolean authenticate(){
        User u = getUser(this.user_name);
        if(u == null)
            return false;

        String hashedPW = "";
        try {
            hashedPW = pwHash(this.password);
        }catch(NoSuchAlgorithmException|InvalidKeySpecException ex) {
            ex.printStackTrace();
        }

        return hashedPW.equals(u.password);
    }*/

    private String pwHash(String pw) throws NoSuchAlgorithmException, InvalidKeySpecException{
        KeySpec spec = new PBEKeySpec(password.toCharArray());
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] hash = factory.generateSecret(spec).getEncoded();
        Base64.Encoder enc = Base64.getEncoder();
        return enc.encodeToString(hash);
    }

    public int incrementSessionQuota() {
    	this.session_quota = this.session_quota +1;
    	this.save();
    	return this.session_quota;
    }
    
    // Returns true if number of flags reduce to 0
    public boolean decrementNumFlags(int value) {
    	this.num_flags = this.num_flags - value;
    	if(num_flags <= 0) {
    		num_flags = 0;
    		this.save();
    		return true;
    	} else {
    		this.save();
    		return false;
    	}
    }

}
