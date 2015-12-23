package controllers;

import models.Session;
import play.data.Form;
import play.mvc.*;
import views.html.*;
import models.User;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Authentication extends Controller {
    public Result authenticate() {
    	models.User user = Form.form(models.User.class).bindFromRequest("user_name","password").get();

        User db_user = User.getUser(user.user_name);

        if (db_user == null) {
            return notFound(exception.render("That e-mail address is not registered. Please go back " +
                    "and sign up to join the community! Please be sure that you validate your sign-up afterward!")); // username not found
        }

        if (user.password.compareTo(db_user.password) != 0) {
            return forbidden(exception.render("The password you entered does not match our records. " +
                    "Please try again.")); // password doesn't match
        }

        if (db_user.isActive == 0) {
            return forbidden(exception.render("You may not log in until you validate your registration. Please check your e-mail!")); // password doesn't match
        }

        if (db_user.num_flags == 0) {
            return forbidden(exception.render("This account has been locked out due to suspicious behavior. If you feel this was done in error, please contact the administrator."));
        }
        session("connected", db_user.user_name);
    	//return ok(home.render(db_user.user_name, db_user.first_name + " " + db_user.last_name));
/*
        List<Session> requested = Session.getRequestedSession(db_user.user_id);
        List<Session> upcoming = Session.getUpcomingSession(db_user.user_id);
        List<Session> completed = Session.getCompletedSession(db_user.user_id);
        List<Session> tutoringRequests = Session.getTutoringRequest(db_user.user_id);
*/
        List<Session> requested = new ArrayList<>();
        List<Session> upcoming = new ArrayList<>();
        List<Session> completed = new ArrayList<>();
        List<Session> tutoringRequests = new ArrayList<>();

        Session.fillSessionBuckets(requested, upcoming, completed, tutoringRequests, db_user);

        return ok(mysessions.render(requested, upcoming, completed, tutoringRequests, db_user.user_id));
    }

    public Result signup() {
        return ok(signup.render("Signup has not been enabled yet!"));
    }
    
    public Result signupUser() {
    	models.User user = Form.form(models.User.class).bindFromRequest("user_name","password","first_name", "last_name", "description").get();

    	User db_user = null;
    	
    	if(user.user_name == null || user.first_name == null || user.last_name == null || user.password == null
    		|| user.user_name.trim().equals("") ||	user.first_name.trim().equals("") || 
    		user.last_name.trim().equals("") || user.password.trim().equals("")) {
    		return notFound(exception.render("Username, First Name, Last Name and Password are mandatory!"));
    	} else if (!user.user_name.toLowerCase().endsWith("@columbia.edu")) {
    		return notFound(exception.render("Thanks for your interest. But Sorry, only Columbia Students with Columbia email id can signup"));
    	} else {
    		db_user = User.getUser(user.user_name);
    		if(db_user != null) {
            	return notFound(exception.render("That e-mail is already registered. Please try logging-in"));
            }

            user.creationDate = new Date();
            user.isActive = 0;
            user.num_flags = 15;
            user.hashCode = Math.abs(user.user_name.hashCode() + user.creationDate.hashCode());

    		// Save the user and redirect to home page
    		user.add();
    	}

        newEmailer.sendValidation(user);
        return ok(exception.render("Sign-up successful! An e-mail has been sent to the address you signed up with. To complete registration, click on the link. kthx!"));

    }

    public Result validateSignup() {
        Map<String, String> map = Form.form().bindFromRequest().data();

        int hashCode = Integer.parseInt(map.get("user"));

        User toValidate = User.getUserByHash(hashCode);

        if (toValidate == null) {
            return notFound(exception.render("Your registration could not be found. Please ensure you clicked the right link. Note that account registration is void if not validated within 24 hours."));
        }

        if (toValidate.isActive == 1) {
            return ok(exception.render("This account is already active, young Jedi! You may start using this immediately!"));
        }

        toValidate.isActive = 1;
        toValidate.updateUser();

        return ok(exception.render("Your account is now fully registered! You may go back and log in now."));
    }

    public Result signout(){
        session().clear();
        return ok(index.render("Signed Out"));
    }

    public Result runInvalidator() {
        for (; ; ) {
            try {
            	singleInvalidatorRun();
                TimeUnit.HOURS.sleep(1);
            }
            catch (InterruptedException e) {
                return ok(exception.render("(" + e.getMessage() + ") Invalidator stopped running at " + new Date()));
            }
        }
    }
    
    public void singleInvalidatorRun() {
        List<User> allUsers = User.getAllUsers();

        Date now = new Date();

        for (User x : allUsers) {
            if (x.isActive == 0) {
                if (now.getTime() - x.creationDate.getTime() > 3600000) {
                    x.deleteUser();
                    System.out.println("Sign-up for UID " + x.user_id + " (" + x.user_name + ") deleted");
                }
            }
        }

        System.out.println("Accounts invalidated on " + now.toString());
    }

    public Result runEvaluation() {
        boolean decrementFlags = false;

        for (; ; ) {
            try {
                singleEvaluationRun(decrementFlags);

                decrementFlags = !decrementFlags;

                TimeUnit.HOURS.sleep(24);
            }
            catch (InterruptedException e) {
                return ok(exception.render("(" + e.getMessage() + ") Evaluation stopped running at " + new Date()));
            }
        }
    }

    public void singleEvaluationRun(boolean decrementFlags) {
        List<User> allUsers = User.getAllUsers();
        Date now = new Date();

        for (User x : allUsers) {
            if (x.num_flags > 0) {
                x.session_quota = 0;

                if (decrementFlags && x.num_flags < 15) {
                    ++x.num_flags;
                }

                x.updateUser();
            }
        }

        System.out.println("Flags updated on " + now.toString());
    }
}
