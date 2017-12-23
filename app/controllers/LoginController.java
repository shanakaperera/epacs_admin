package controllers;

import play.mvc.*;
import views.html.*;

public class LoginController extends Controller {

    public Result login() {
        return ok(views.html.login_page.login.render());
    }
}
