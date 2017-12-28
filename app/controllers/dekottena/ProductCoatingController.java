package controllers.dekottena;

import models.Coating;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.*;
import services.DocSeqHandler;
import services.HibernateUtil;

import javax.inject.Inject;

public class ProductCoatingController extends Controller {

    @Inject
    private FormFactory formFactory;
    private static final String TABLE_NAME = "coating";

    public Result home() {

        String coating_action = "New Coating";
        String coating_code = getNextCoatingSequence();

        Form<Coating> coatingForm = formFactory.form(Coating.class);
        return ok(views.html.dekottena.pr_coating_page.pr_coating
                .render(coating_code, coating_action, coatingForm));

    }

    public Result save() {

        Form<Coating> new_coating = formFactory.form(Coating.class).bindFromRequest();

        if (new_coating.hasErrors()) {

            String coating_action = "New Coating";
            String coating_code = getNextCoatingSequence();

            flash("danger", "Please correct the below form.");
            return badRequest(views.html.dekottena.pr_coating_page.pr_coating
                    .render(coating_code, coating_action, new_coating));
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

    private String getNextCoatingSequence() {
        //===============HAVE TO SET PROJECTION IN ORDER TO ACCURATE DATA FETCHING MECHANISM
        DocSeqHandler seqHandler = new DocSeqHandler();
        Session session = HibernateUtil.getSessionFactory().openSession();
        Criteria c = session.createCriteria(Coating.class);
        c.addOrder(Order.desc("id"));
        c.setMaxResults(1);

        Coating co = (Coating) c.uniqueResult();
        session.close();
        if (co != null) {
            seqHandler.reqTable(TABLE_NAME, Integer.parseInt(co.getCode().replaceAll("\\D+", "")) + 1);
            return seqHandler.getSeq_code();
        } else {
            seqHandler.reqTable(TABLE_NAME, 0);
            return seqHandler.getSeq_code();
        }
    }

}
