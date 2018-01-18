package controllers.dekottena;

import models.Tree;
import models.TreeContent;
import org.hibernate.Query;
import org.hibernate.Session;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Result;
import services.HibernateUtil;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class ProductCategoryController extends Controller {

    @Inject
    private FormFactory formFactory;

    public Result home() {

        Session s = HibernateUtil.getSessionFactory().openSession();
        List<Tree> tree = getTree(s);
        s.close();
        return ok(views.html.dekottena.pr_category_page.pr_category.render(tree));
    }

    public Result save() {

        DynamicForm requestData = formFactory.form().bindFromRequest();
        String pid = requestData.get("parent_id");
        String cname = requestData.get("cat_name");
        Session s = HibernateUtil.getSessionFactory().openSession();

        Query query = s.createSQLQuery(
                "CALL r_tree_traversal(:typed, :val, :pId);")
                .setParameter("typed", "insert")
                .setParameter("val", null)
                .setParameter("pId", pid);
        int nxt_nodeid = ((BigInteger) query.uniqueResult()).intValue();

        TreeContent tc = new TreeContent();
        tc.setNodeId(nxt_nodeid);
        tc.setName(cname);
        tc.setLang("en");

        s.beginTransaction();
        s.save(tc);
        s.getTransaction().commit();
        s.close();
        flash("success", "Successfully Saved. ");
        return redirect(routes.ProductCategoryController.home());
    }

    public Result delete() {

        DynamicForm requestData = formFactory.form().bindFromRequest();
        String del_id = requestData.get("delete_id");

        Session s = HibernateUtil.getSessionFactory().openSession();
        Query query = s.createSQLQuery(
                "CALL r_tree_traversal(:typed, :val, :pId);")
                .setParameter("typed", "delete")
                .setParameter("val", del_id)
                .setParameter("pId", null);
        query.executeUpdate();
        s.close();
        flash("success", "Successfully Deleted. ");
        return redirect(routes.ProductCategoryController.home());
    }

    private List<Tree> getTree(Session session) {
        Query query = session.createSQLQuery(
                "CALL r_return_tree(:pedited, :lang);")
                .setParameter("pedited", null)
                .setParameter("lang", "en");
        List<Object[]> rows = query.list();
        List<Tree> tree = new ArrayList<>();

        for (Object[] row : rows) {
            tree.add(new Tree(Integer.parseInt(row[0].toString()), row[1].toString()));
        }
        return tree;
    }
}
