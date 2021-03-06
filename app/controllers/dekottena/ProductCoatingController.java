package controllers.dekottena;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.google.common.base.CharMatcher;
import com.typesafe.config.ConfigFactory;
import models.Coating;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import play.Play;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.*;
import services.*;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static validators.CustomValidator.IsValidImageSize;


public class ProductCoatingController extends Controller {

    @Inject
    private FormFactory formFactory;
    private static final String TABLE_NAME = "coating";

    public Result home() {

        String coating_action = "New Coating";
        String coating_code = getNextCoatingSequence();

        String dList = getDataList();

        Form<Coating> coatingForm = formFactory.form(Coating.class);
        return ok(views.html.dekottena.pr_coating_page.pr_coating
                .render(coating_code, coating_action, dList, coatingForm));
    }

    @BodyParser.Of(MyMultipartFormDataBodyParser.class)
    public Result save() throws IOException {

        Form<Coating> new_coating = formFactory.form(Coating.class).bindFromRequest();

        final Http.MultipartFormData<File> formData = request().body().asMultipartFormData();
        final Http.MultipartFormData.FilePart<File> filePart = formData.getFile("imgName");
        final File file = filePart.getFile();

        String coating_action = "New Coating";
        String coating_code = getNextCoatingSequence();
        String dList = getDataList();
        final String file_name = coating_code + "." + FilenameUtils.getExtension(filePart.getFilename());

        if (new_coating.hasErrors()) {

            flash("danger", "Please correct the below form.");
            return badRequest(views.html.dekottena.pr_coating_page.pr_coating
                    .render(coating_code, coating_action, dList, new_coating));
        } else {
            Coating coating = new_coating.get();
            Session s = HibernateUtil.getSessionFactory().openSession();
            s.beginTransaction();

            if (file != null && file.exists()) {

                long data = ImageHandler.operateOnTempFile(file);
                int digit_part = Integer.parseInt(CharMatcher.DIGIT.retainFrom(FileUtils.byteCountToDisplaySize(data)));

                if (digit_part > 0) {

                    if (!IsValidImageSize(digit_part, 50, 257)) {

                        flash("danger", "Image size should be between 50KB and 256KB.");
                        return badRequest(views.html.dekottena.pr_coating_page.pr_coating
                                .render(coating_code, coating_action, dList, new_coating));

                    }

                    MyAwsCredentials s3 = new MyAwsCredentials();
                    PutObjectRequest object_saved = new PutObjectRequest(s3.getBucket(),
                            s3.getUpFolder(file_name), file)
                            .withCannedAcl(CannedAccessControlList.PublicRead);
                    AmazonS3Client client = s3.getClient();
                    client.putObject(object_saved);

                    coating.setImgPath(Play.application().configuration().getString("env.file_download_path")
                            .concat(ConfigFactory.load("aws.conf").getString("env.file_upload_folder"))
                            .concat(file_name)
                    );
                }

            }

            s.save(coating);
            s.getTransaction().commit();
            s.close();
            flash("success", "Successfully Saved. ");
            return redirect(routes.ProductCoatingController.home());
        }

    }

    public Result edit(String code) {

        String coating_action = "Edit Coating";
        String dList = getDataList();

        Session s = HibernateUtil.getSessionFactory().openSession();
        Criteria c = s.createCriteria(Coating.class);
        c.add(Restrictions.eq("code", code));
        Coating fc = (Coating) c.uniqueResult();
        if (fc == null) {
            flash("danger", "No coating found with given name.");
            return redirect(routes.ProductCoatingController.home());
        }
        Form<Coating> filterForm = formFactory.form(Coating.class).fill(fc);

        return ok(views.html.dekottena.pr_coating_page.pr_coating
                .render(code, coating_action, dList, filterForm));
    }

    @BodyParser.Of(MyMultipartFormDataBodyParser.class)
    public Result update() throws IOException {

        Form<Coating> update_coating = formFactory.form(Coating.class).bindFromRequest();

        final Http.MultipartFormData<File> formData = request().body().asMultipartFormData();
        final Http.MultipartFormData.FilePart<File> filePart = formData.getFile("imgName");
        final File file = filePart.getFile();

        String coating_action = "Edit Coating";
        Coating coating = update_coating.get();
        final String file_name = coating.getCode() + "." + FilenameUtils.getExtension(filePart.getFilename());
        String dList = getDataList();

        if (update_coating.hasErrors()) {

            flash("danger", "Please correct the below form.");
            return badRequest(views.html.dekottena.pr_coating_page.pr_coating
                    .render(coating.getCode(), coating_action, dList, update_coating));
        } else {

            Session s = HibernateUtil.getSessionFactory().openSession();
            s.beginTransaction();

            if (file != null && file.exists()) {

                long data = ImageHandler.operateOnTempFile(file);
                int digit_part = Integer.parseInt(CharMatcher.DIGIT.retainFrom(FileUtils.byteCountToDisplaySize(data)));

                if (digit_part > 0) {

                    if (!IsValidImageSize(digit_part, 50, 257)) {

                        flash("danger", "Image size should be between 50KB and 256KB.");
                        return badRequest(views.html.dekottena.pr_coating_page.pr_coating
                                .render(coating.getCode(), coating_action, dList, update_coating));

                    }

                    MyAwsCredentials s3 = new MyAwsCredentials();
                    PutObjectRequest object_saved = new PutObjectRequest(s3.getBucket(),
                            s3.getUpFolder(file_name), file)
                            .withCannedAcl(CannedAccessControlList.PublicRead);
                    AmazonS3Client client = s3.getClient();
                    client.putObject(object_saved);

                    coating.setImgPath(Play.application().configuration().getString("env.file_download_path")
                            .concat(ConfigFactory.load("aws.conf").getString("env.file_upload_folder"))
                            .concat(file_name)
                    );
                }
            }

            s.update(coating);
            s.getTransaction().commit();
            s.close();
            flash("success", "Successfully Updated. ");
            return redirect(routes.ProductCoatingController.edit(coating.getCode()));
        }


    }

    public Result delete(String code) {
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

    private String getDataList() {

        Session s = HibernateUtil.getSessionFactory().openSession();
        Criteria c = s.createCriteria(Coating.class);
        c.setProjection(Projections.projectionList()
                .add(Projections.property("code"), "code")
                .add(Projections.property("title"), "title")
        );
        c.setResultTransformer(Transformers.aliasToBean(Coating.class));
        List<Coating> cList = c.list();

        String dList = "";
        for (Coating co : cList) {
            dList += "<option value='" + co.getCode() + "'>" + co.getTitle() + "</option>";
        }

        s.close();
        return dList;
    }


}
