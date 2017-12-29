package controllers.dekottena;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import models.Coating;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import play.Play;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.*;
import services.DocSeqHandler;
import services.HibernateUtil;
import services.MyAwsCredentials;
import services.MyMultipartFormDataBodyParser;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;


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

    @BodyParser.Of(MyMultipartFormDataBodyParser.class)
    public Result save() throws IOException {

        Form<Coating> new_coating = formFactory.form(Coating.class).bindFromRequest();

        final Http.MultipartFormData<File> formData = request().body().asMultipartFormData();
        final Http.MultipartFormData.FilePart<File> filePart = formData.getFile("imgName");
        final String file_name = filePart.getFilename();
        final File file = filePart.getFile();
        //final long data = operateOnTempFile(file);

        if (new_coating.hasErrors()) {

            String coating_action = "New Coating";
            String coating_code = getNextCoatingSequence();

            flash("danger", "Please correct the below form.");
            return badRequest(views.html.dekottena.pr_coating_page.pr_coating
                    .render(coating_code, coating_action, new_coating));
        } else {
            Coating coating = new_coating.get();
            Session s = HibernateUtil.getSessionFactory().openSession();
            s.beginTransaction();

            if (file != null && file.exists()) {

                MyAwsCredentials s3 = new MyAwsCredentials();
                PutObjectRequest object_saved = new PutObjectRequest(s3.getBucket(),
                        s3.getUpFolder(file_name), file)
                        .withCannedAcl(CannedAccessControlList.PublicRead);
                AmazonS3Client client = s3.getClient();
                client.putObject(object_saved);

                coating.setImgPath(Play.application().configuration().getString("env.file_download_path")
                        .concat(Play.application().configuration().getString("env.file_upload_folder"))
                        .concat(file_name)
                );
            }

            s.save(coating);
            s.getTransaction().commit();
            s.close();
            flash("success", "Successfully Saved. ");
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


//    private long operateOnTempFile(File file) throws IOException {
//        final long size = Files.size(file.toPath());
//        Files.deleteIfExists(file.toPath());
//        return size;
//    }
}
