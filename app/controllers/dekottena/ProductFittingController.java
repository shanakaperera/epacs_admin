package controllers.dekottena;

import models.Fitting;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.*;
import services.DocSeqHandler;
import services.HibernateUtil;

import javax.inject.Inject;

public class ProductFittingController extends Controller {

    @Inject
    FormFactory formfactoroy;
    private static final String TABLE_NAME = "fitting";

    public Result home() {

        String fitting_action = "New Fitting";
        String fitting_code = getNextFittingSequence();

        Form<Fitting> fittingForm = formfactoroy.form(Fitting.class);

        return ok(views.html.dekottena.pr_fitting_page.pr_fitting
                .render(fitting_code, fitting_action, fittingForm));
    }

    public Result save() {

        Form<Fitting> new_fitting = formfactoroy.form(Fitting.class).bindFromRequest();

        if (new_fitting.hasErrors()) {

            String fitting_action = "New Fitting";
            String fitting_code = getNextFittingSequence();

            flash("danger", "Please correct the below form.");
            return badRequest(views.html.dekottena.pr_fitting_page.pr_fitting
                    .render(fitting_code, fitting_action, new_fitting));
        } else {

            Fitting fitting = new_fitting.get();
            Session s = HibernateUtil.getSessionFactory().openSession();
            s.save(fitting);
            s.beginTransaction();
            s.getTransaction().commit();
            s.close();
            flash("success", "Successfully Saved.");
            return redirect(routes.ProductFittingController.home());
        }
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

    private String getNextFittingSequence() {
        //===============HAVE TO SET PROJECTION IN ORDER TO ACCURATE DATA FETCHING MECHANISM
        DocSeqHandler seqHandler = new DocSeqHandler();
        Session session = HibernateUtil.getSessionFactory().openSession();
        Criteria c = session.createCriteria(Fitting.class);
        c.addOrder(Order.desc("id"));
        c.setMaxResults(1);

        Fitting ft = (Fitting) c.uniqueResult();
        session.close();
        if (ft != null) {
            seqHandler.reqTable(TABLE_NAME, Integer.parseInt(ft.getCode().replaceAll("\\D+", "")) + 1);
            return seqHandler.getSeq_code();
        } else {
            seqHandler.reqTable(TABLE_NAME, 0);
            return seqHandler.getSeq_code();
        }
    }
}
