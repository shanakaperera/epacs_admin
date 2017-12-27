package controllers.dekottena;

import models.Coating;
import models.Product;
import org.hibernate.Session;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.*;
import services.HibernateUtil;

import javax.inject.Inject;

public class ProductController extends Controller {

    @Inject
    FormFactory formFactory;

    public Result home() {

        String action = "New Product";
        String code = "PR0552";
        Form<Product> productForm = formFactory.form(Product.class);
        Product product = new Product(code, action);
        productForm.fill(product);

        return ok(views.html.dekottena.product_page.product.render(productForm));
    }

    public Result save() {

        Form<Product> new_product = formFactory.form(Product.class).bindFromRequest();
        Product product = new_product.get();
        Session s = HibernateUtil.getSessionFactory().openSession();
        s.save(product);
        s.beginTransaction();
        s.getTransaction().commit();
        s.close();

        return redirect(routes.ProductController.home());
    }

    public Result update(Integer id) {
        return TODO;
    }

    public Result delete(Integer id) {
        return TODO;
    }
}
