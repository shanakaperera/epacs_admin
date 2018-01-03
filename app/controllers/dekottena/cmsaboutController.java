package controllers.dekottena;

import models.DkcmsAbout;
import models.Fitting;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.*;

import javax.inject.Inject;

public class cmsaboutController extends Controller {
    @Inject
    FormFactory formfactoroy;


    public Result home() {

        Form<DkcmsAbout> aboutForm = formfactoroy.form(DkcmsAbout.class);

        return ok(views.html.dekottena.cms.about.about.render(aboutForm));
    }

    public Result save() {
        return null;
    }

}
