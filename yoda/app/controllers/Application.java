package controllers;

import models.Course;
import models.Session;
import models.User;
import java.util.*;

import play.mvc.*;

import play.twirl.api.Html;
import views.html.*;


public class Application extends Controller {

    public Result index() {
        /*
        User k = User.getUser("test");
        System.out.println("my name is = " + k.user_name);
        k.first_name = "testing";
        k.updateUser();

        List<User> a = User.getAllUsers();
        for(User u : a)
            System.out.println("username = " + u.user_name + " user_id = " + u.user_id);

        Course c = Course.getCourse("COMSW4156");
            System.out.println("coursename = " + c.name);
        */

        String username = session("connected");
        models.User user = models.User.getUser(username);
        if(username != null && user != null && !username.isEmpty()) {
            return ok(home.render(session("connected"), Html.apply("")));
        }

        return ok(index.render("Welcome to Yoda!"));
    }

    public Result about() {
        return ok(about.render());
    }

    public Result exception() {
        return forbidden(exception.render("You are not allowed to view this page."));
    }

    public Result notfound(String path) { return notFound(exception.render(path + " Not Found")); }

}

