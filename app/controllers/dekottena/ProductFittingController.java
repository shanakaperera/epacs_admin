package controllers.dekottena;

import models.Coating;
import models.Fitting;
import org.hibernate.Session;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.*;
import services.HibernateUtil;

import javax.inject.Inject;

public class ProductFittingController extends Controller {

    @Inject
    FormFactory formfactoroy;

    public Result home() {
//        return ok(views.html.dekottena.pr_coating_page.pr_coating.render());
        String action = "New Fitting";
        String code = "FI004";
        Fitting preFitting = new Fitting(code,action);
        Form<Fitting> fittingForm = formfactoroy.form(Fitting.class);
        fittingForm.fill(preFitting);


        return ok(views.html.dekottena.pr_fitting_page.pr_fitting.render(fittingForm));
    }

    public Result save(){
        Form<Fitting> new_fitting = formfactoroy.form(Fitting.class).bindFromRequest();
        Fitting fitting = new_fitting.get();
        Session s = HibernateUtil.getSessionFactory().openSession();
        s.save(fitting);
        s.beginTransaction();
        s.getTransaction().commit();
        s.close();

        return redirect(routes.ProductFittingController.home());
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
