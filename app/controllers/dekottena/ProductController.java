package controllers.dekottena;

import models.Product;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.*;
import services.DocSeqHandler;
import services.HibernateUtil;

import javax.inject.Inject;

public class ProductController extends Controller {

    @Inject
    FormFactory formFactory;
    private static final String TABLE_NAME = "product";

    public Result home() {

        String action = "New Product";
        String code = getNextProductSequence();
        Form<Product> productForm = formFactory.form(Product.class);

        return ok(views.html.dekottena.product_page.product.render(code, action, productForm));
    }

    public Result save() {

        Form<Product> new_product = formFactory.form(Product.class).bindFromRequest();

        if (new_product.hasErrors()) {

            String action = "New Product";
            String code = getNextProductSequence();

            flash("danger", "Please correct the below form.");
            return badRequest(views.html.dekottena.product_page.product.render(code, action, new_product));
        } else {

            Product product = new_product.get();
            Session s = HibernateUtil.getSessionFactory().openSession();
            s.save(product);
            s.beginTransaction();
            s.getTransaction().commit();
            s.close();
            flash("success", "Successfully Saved.");
            return redirect(routes.ProductController.home());
        }

    }

    public Result update(Integer id) {
        return TODO;
    }

    public Result delete(Integer id) {
        return TODO;
    }

    private String getNextProductSequence() {
        //===============HAVE TO SET PROJECTION IN ORDER TO ACCURATE DATA FETCHING MECHANISM
        DocSeqHandler seqHandler = new DocSeqHandler();
        Session session = HibernateUtil.getSessionFactory().openSession();
        Criteria c = session.createCriteria(Product.class);
        c.addOrder(Order.desc("id"));
        c.setMaxResults(1);

        Product pr = (Product) c.uniqueResult();
        session.close();
        if (pr != null) {
            seqHandler.reqTable(TABLE_NAME, Integer.parseInt(pr.getCode().replaceAll("\\D+", "")) + 1);
            return seqHandler.getSeq_code();
        } else {
            seqHandler.reqTable(TABLE_NAME, 0);
            return seqHandler.getSeq_code();
        }
    }
}
