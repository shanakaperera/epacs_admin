package controllers.dekottena;

import play.mvc.Controller;
import play.mvc.Result;

public class HomeController extends Controller {

    public Result home() {
        return ok(views.html.dekottena.home_page.home.render());
    }
}
