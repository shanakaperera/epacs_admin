package controllers.dekottena;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.google.common.base.CharMatcher;
import models.Coating;
import models.DkcmsAbout;
import models.Fitting;
import org.apache.commons.io.FileUtils;
import org.hibernate.Session;
import play.Play;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.*;
import services.HibernateUtil;
import services.ImageHandler;
import services.MyAwsCredentials;
import services.MyMultipartFormDataBodyParser;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;

import static validators.CustomValidator.IsValidImageSize;

public class cmsaboutController extends Controller {
    @Inject
    private FormFactory formfactoroy;


    public Result home() {

        Form<DkcmsAbout> aboutForm = formfactoroy.form(DkcmsAbout.class);

        return ok(views.html.dekottena.cms.about.about.render(aboutForm));
    }

    @BodyParser.Of(MyMultipartFormDataBodyParser.class)

    public Result save() throws IOException {

        Form<DkcmsAbout> aboutuspage = formfactoroy.form(DkcmsAbout.class).bindFromRequest();

        final Http.MultipartFormData<File> formData = request().body().asMultipartFormData();
        final Http.MultipartFormData.FilePart<File> filePart = formData.getFile("imgUrl");
        final String file_name = filePart.getFilename();
        final File file = filePart.getFile();

        String coating_action = "About update";
        String coating_code = "";
        String dList = null;

        if (aboutuspage.hasErrors()) {

            flash("danger", "Please correct the below form.");
            return badRequest(views.html.dekottena.cms.about.about
                    .render(aboutuspage));
        } else {
            DkcmsAbout dkcmsAbout = aboutuspage.get();
            Session s = HibernateUtil.getSessionFactory().openSession();
            s.beginTransaction();

            if (file != null && file.exists()) {

                long data = ImageHandler.operateOnTempFile(file);
                int digit_part = Integer.parseInt(CharMatcher.DIGIT.retainFrom(FileUtils.byteCountToDisplaySize(data)));

                if (!IsValidImageSize(digit_part, 50, 257)) {

                    flash("danger", "Image size should be between 50KB and 256KB.");
                    return badRequest(views.html.dekottena.cms.about.about
                            .render(aboutuspage));

                }

                MyAwsCredentials s3 = new MyAwsCredentials();
                PutObjectRequest object_saved = new PutObjectRequest(s3.getBucket(),
                        s3.getUpFolder(file_name), file)
                        .withCannedAcl(CannedAccessControlList.PublicRead);
                AmazonS3Client client = s3.getClient();
                client.putObject(object_saved);

                dkcmsAbout.setImgUrl(Play.application().configuration().getString("env.file_download_path")
                        .concat(Play.application().configuration().getString("env.file_upload_folder"))
                        .concat(file_name)
                );
            }

            s.save(dkcmsAbout);
            s.getTransaction().commit();
            s.close();
            flash("success", "Successfully Saved. ");
            return redirect(routes.cmsaboutController.home());
        }


    }

}
