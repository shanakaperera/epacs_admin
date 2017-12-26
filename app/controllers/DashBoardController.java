package controllers;

import play.mvc.*;
import views.html.*;

public class DashBoardController extends Controller {

    public Result dashboard() {
        return ok(views.html.dashboard_page.dashboard.render());
    }
}
