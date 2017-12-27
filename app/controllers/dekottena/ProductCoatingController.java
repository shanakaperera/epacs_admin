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
    private FormFactory formFactory;

    public Result home() {

        String coating_action = "New Coating";
        String coating_code = "CO552";
        Form<Coating> coatingForm = formFactory.form(Coating.class);
        // .fill(new Coating(coating_code, coating_action));
        return ok(views.html.dekottena.pr_coating_page.pr_coating.render(coatingForm.fill(new Coating(coating_code, coating_action))));

    }

    public Result save() {

        Form<Coating> new_coating = formFactory.form(Coating.class).bindFromRequest();

        if (new_coating.hasErrors()) {
            flash("danger", "Please correct the below form.");
            return badRequest(views.html.dekottena.pr_coating_page.pr_coating.render(new_coating));
        } else {
            Coating coating = new_coating.get();
            Session s = HibernateUtil.getSessionFactory().openSession();
            s.save(coating);
            s.beginTransaction();
            s.getTransaction().commit();
            s.close();
            flash("success", "Successfully Saved.");
            return redirect(routes.ProductCoatingController.home());
        }

    }

    public Result update(Integer id) {
        return TODO;
    }

    public Result delete(Integer id) {
        return TODO;
    }

}
