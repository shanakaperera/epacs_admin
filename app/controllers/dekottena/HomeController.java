package controllers.dekottena;

import models.DkcmsContact;
import models.DkcmsSlider;
import org.hibernate.Session;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Result;
import services.HibernateUtil;

import javax.inject.Inject;

public class HomeController extends Controller {

    @Inject
    FormFactory formfactoroy;

    public Result home() {

        Session s = HibernateUtil.getSessionFactory().openSession();
        DkcmsSlider slider = s.load(DkcmsSlider.class,1);

        Form<DkcmsSlider> contactForm = formfactoroy.form(DkcmsSlider.class).fill(slider);

        return ok(views.html.dekottena.home_page.home.render());
    }
}
