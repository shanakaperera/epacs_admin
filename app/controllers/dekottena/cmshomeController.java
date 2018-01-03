package controllers.dekottena;


import models.DkcmsSlider;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.*;

import javax.inject.Inject;

public class cmshomeController extends Controller {

    @Inject
    FormFactory formfactoroy;


    public Result home() {

        Form<DkcmsSlider> sliderForm = formfactoroy.form(DkcmsSlider.class);

        return ok(views.html.dekottena.cms.home.home.render(sliderForm));
    }


    public Result save() {
        return null;
    }
}
