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

        String action = "New Coating";
        String code = "Si005";
        Form<Size> sizeForm = formFactory.form(Size.class);
        Size preSize = new Size(code, action);
        sizeForm.fill(preSize);

        return ok(views.html.dekottena.pr_size_page.pr_size.render(sizeForm));
    }

    public Result save() {

        Form<Size> new_zise = formFactory.form(Size.class).bindFromRequest();
        Size size = new_zise.get();
        Session s = HibernateUtil.getSessionFactory().openSession();
        s.save(size);
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
