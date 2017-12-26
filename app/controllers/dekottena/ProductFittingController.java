package controllers.dekottena;

import play.mvc.*;

public class ProductFittingController extends Controller {

    public Result home() {
//        return ok(views.html.dekottena.pr_coating_page.pr_coating.render());
        return ok(views.html.dekottena.pr_fitting_page.pr_fitting.render());
    }

    public Result create() {
        return TODO;
    }

    public Result edit(Integer id) {
        return TODO;
    }

    public Result delete(Integer id) {
        return TODO;
    }
}
