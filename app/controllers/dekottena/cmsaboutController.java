package controllers.dekottena;

import models.Fitting;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.*;

import javax.inject.Inject;

public class cmsaboutController extends Controller {
    @Inject
    FormFactory formfactoroy;
    private static final String TABLE_NAME = "sss";

    public Result home() {

        String fitting_action = "New Fitting";
        String fitting_code = "ww";


        Form<Fitting> fittingForm = formfactoroy.form(Fitting.class);

        return ok(views.html.dekottena.cms.about.about.render(null));
    }

}
