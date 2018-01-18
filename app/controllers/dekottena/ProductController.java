package controllers.dekottena;

import models.*;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.transform.Transformers;
import play.data.DynamicForm;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.*;
import services.DocSeqHandler;
import services.HibernateUtil;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProductController extends Controller {

    @Inject
    FormFactory formFactory;
    private static final String TABLE_NAME = "product";

    public Result home() {

        String action = "New Product";
        String code = getNextProductSequence();
        List<Coating> coatings = getCoatingList();
        List<Fitting> fittings = getFittingList();

        Set<ProductHasCoating> phc = new HashSet<>();
        for (Coating c : coatings) {
            phc.add(new ProductHasCoating(c));
        }
        Set<ProductHasFitting> phf = new HashSet<>();
        for (Fitting f : fittings) {
            phf.add(new ProductHasFitting(f));
        }

        Product pi = new Product(phf, phc);

        Form<Product> productForm = formFactory.form(Product.class);

        return ok(views.html.dekottena.product_page.product.render(code, action, productForm.fill(pi)));
    }

    public Result save() {

        String coatings = request().getQueryString("coatings");

        return ok(coatings);

//        Form<Product> new_product = formFactory.form(Product.class).bindFromRequest();

//        String action = "New Product";
//        String code = getNextProductSequence();
//
//        List<Coating> coatings = getCoatingList();
//        List<Fitting> fittings = getFittingList();
//
//        if (new_product.hasErrors()) {
//            flash("danger", "Please correct the below form.");
//            return badRequest(views.html.dekottena.product_page.product.render(code, action, new_product));
//        } else {
//            Product product = new_product.get();
//            Session s = HibernateUtil.getSessionFactory().openSession();
//            s.beginTransaction();
//            s.save(product);
//            s.getTransaction().commit();
//            s.close();
//            flash("success", "Successfully Saved.");
//            return redirect(routes.ProductController.home());
//        }

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

    private List<Coating> getCoatingList() {

        Session s = HibernateUtil.getSessionFactory().openSession();
        Criteria c = s.createCriteria(Coating.class);
        c.setProjection(Projections.projectionList()
                .add(Projections.property("id"), "id")
                .add(Projections.property("code"), "code")
                .add(Projections.property("title"), "title")
                .add(Projections.property("imgPath"), "imgPath")
        );
        c.setResultTransformer(Transformers.aliasToBean(Coating.class));
        List<Coating> cList = c.list();
        return cList;
    }

    private List<Fitting> getFittingList() {

        Session s = HibernateUtil.getSessionFactory().openSession();
        Criteria c = s.createCriteria(Fitting.class);
        c.setProjection(Projections.projectionList()
                .add(Projections.property("id"), "id")
                .add(Projections.property("code"), "code")
                .add(Projections.property("title"), "title")
                .add(Projections.property("imgPath"), "imgPath")
        );
        c.setResultTransformer(Transformers.aliasToBean(Fitting.class));
        List<Fitting> fList = c.list();
        return fList;
    }
}
