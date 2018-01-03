package controllers.dekottena;

import models.DkcmsContact;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.*;

import javax.inject.Inject;

public class cmscontactController extends Controller{
    @Inject
    FormFactory formfactoroy;

    public Result home() {

        Form<DkcmsContact> contactForm = formfactoroy.form(DkcmsContact.class);

        return ok(views.html.dekottena.cms.contact.contact.render(contactForm));
    }

    public Result save() {
        return null;
    }

}
