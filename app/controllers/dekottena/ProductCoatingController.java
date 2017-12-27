package controllers.dekottena;

import models.Coating;
import org.hibernate.Session;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.*;
import services.HibernateUtil;

import javax.inject.Inject;

public class ProductCoatingController extends Controller {

    @Inject
    FormFactory formFactory;

    public Result home() {

        String coating_action = "New Coating";
        String coating_code = "CO552";
        Form<Coating> coatingForm = formFactory.form(Coating.class);
        Coating preCoating = new Coating(coating_code, coating_action);
        coatingForm.fill(preCoating);

        return ok(views.html.dekottena.pr_coating_page.pr_coating.render(coatingForm));
    }

    public Result save() {

        Form<Coating> new_coating = formFactory.form(Coating.class).bindFromRequest();
        Coating coating = new_coating.get();
        Session s = HibernateUtil.getSessionFactory().openSession();
        s.save(coating);
        s.beginTransaction();
        s.getTransaction().commit();
        s.close();

        return redirect(routes.ProductCoatingController.home());
    }

    public Result update(Integer id) {
        return TODO;
    }

    public Result delete(Integer id) {
        return TODO;
    }

}
