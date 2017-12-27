package controllers;

import play.mvc.*;
import java.sql.SQLException;

public class LoginController extends Controller {

    public Result login() throws SQLException {

        String title = "Admin";

        return ok(views.html.login_page.login.render(title));
    }
}
