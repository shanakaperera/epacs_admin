package controllers.dekottena;

import models.Size;
import org.hibernate.Session;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.*;
import services.HibernateUtil;

import javax.inject.Inject;

public class ProductSizeController extends Controller {

    @Inject
    FormFactory formFactory;

    public Result home() {

        String action = "New Product Size";
        Form<Size> sizeForm = formFactory.form(Size.class);

        return ok(views.html.dekottena.pr_size_page.pr_size.render(action, sizeForm));
    }

    public Result save() {

        Form<Size> new_size = formFactory.form(Size.class).bindFromRequest();

        if (new_size.hasErrors()) {

            String action = "New Product Size";
            flash("danger", "Please correct the below form.");
            return badRequest(views.html.dekottena.pr_size_page.pr_size.render(action, new_size));

        } else {

            Size size = new_size.get();
            Session s = HibernateUtil.getSessionFactory().openSession();
            s.save(size);
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
