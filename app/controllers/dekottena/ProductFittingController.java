package controllers.dekottena;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.google.common.base.CharMatcher;
import com.typesafe.config.ConfigFactory;
import models.Fitting;
import org.apache.commons.io.FileUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.transform.Transformers;
import play.Play;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.*;
import services.*;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.List;

import static validators.CustomValidator.IsValidImageSize;

public class ProductFittingController extends Controller {

    @Inject
    FormFactory formfactoroy;
    private static final String TABLE_NAME = "fitting";

    public Result home() {

        String fitting_action = "New Fitting";
        String fitting_code = getNextFittingSequence();
        String dList = getDataList();

        Form<Fitting> fittingForm = formfactoroy.form(Fitting.class);

        return ok(views.html.dekottena.pr_fitting_page.pr_fitting
                .render(fitting_code, fitting_action, dList, fittingForm));
    }

    @BodyParser.Of(MyMultipartFormDataBodyParser.class)
    public Result save() throws IOException {

        Form<Fitting> new_fitting = formfactoroy.form(Fitting.class).bindFromRequest();

        final Http.MultipartFormData<File> formData = request().body().asMultipartFormData();
        final Http.MultipartFormData.FilePart<File> filePart = formData.getFile("imgName");
        final String file_name = filePart.getFilename();
        final File file = filePart.getFile();


        String fitting_action = "New Fitting";
        String fitting_code = getNextFittingSequence();
        String dList = getDataList();

        if (new_fitting.hasErrors()) {

            flash("danger", "Please correct the below form.");
            return badRequest(views.html.dekottena.pr_fitting_page.pr_fitting
                    .render(fitting_code, fitting_action, dList, new_fitting));
        } else {

            Fitting fitting = new_fitting.get();
            Session s = HibernateUtil.getSessionFactory().openSession();
            s.beginTransaction();

            if (file != null && file.exists()) {

                long data = ImageHandler.operateOnTempFile(file);
                int digit_part = Integer.parseInt(CharMatcher.DIGIT.retainFrom(FileUtils.byteCountToDisplaySize(data)));

                if (!IsValidImageSize(digit_part, 50, 257)) {

                    flash("danger", "Image size should be between 50KB and 256KB.");
                    return badRequest(views.html.dekottena.pr_fitting_page.pr_fitting
                            .render(fitting_code, fitting_action, dList, new_fitting));

                }

                MyAwsCredentials s3 = new MyAwsCredentials();
                PutObjectRequest object_saved = new PutObjectRequest(s3.getBucket(),
                        s3.getUpFolder(file_name), file)
                        .withCannedAcl(CannedAccessControlList.PublicRead);
                AmazonS3Client client = s3.getClient();
                client.putObject(object_saved);

                fitting.setImgPath(ConfigFactory.load("aws.conf").getString("env.file_download_path")
                        .concat(ConfigFactory.load("aws.conf").getString("env.file_upload_folder"))
                        .concat(file_name)
                );
            }

            s.save(fitting);
            s.getTransaction().commit();
            s.close();
            flash("success", "Successfully Saved.");
            return redirect(routes.ProductFittingController.home());
        }
    }

    public Result edit(String code) {
        return TODO;
    }

    public Result update() {
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

    private String getDataList() {

        Session s = HibernateUtil.getSessionFactory().openSession();
        Criteria c = s.createCriteria(Fitting.class);
        c.setProjection(Projections.projectionList()
                .add(Projections.property("code"), "code")
                .add(Projections.property("title"), "title")
        );
        c.setResultTransformer(Transformers.aliasToBean(Fitting.class));
        List<Fitting> list = c.list();

        String fList = "";
        for (Fitting f : list) {
            fList += "<option value='" + f.getCode() + "'>" + f.getTitle() + "</option>";
        }

        s.close();
        return fList;
    }
}
