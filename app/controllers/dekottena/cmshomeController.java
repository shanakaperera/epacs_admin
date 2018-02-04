package controllers.dekottena;


import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.google.common.base.CharMatcher;
import com.typesafe.config.ConfigFactory;
import models.DkcmsAbout;
import models.DkcmsContact;
import models.DkcmsSlider;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
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

public class cmshomeController extends Controller {

    @Inject
    FormFactory formfactoroy;


    public Result home() {

        Session s = HibernateUtil.getSessionFactory().openSession();
        DkcmsSlider slider = s.load(DkcmsSlider.class, 1);

        Form<DkcmsSlider> sliderForm = formfactoroy.form(DkcmsSlider.class).fill(slider);


        return ok(views.html.dekottena.cms.home.home.render(sliderForm));
    }


    @BodyParser.Of(MyMultipartFormDataBodyParser.class)
    public Result save() throws IOException{

        Form<DkcmsSlider> homepage = formfactoroy.form(DkcmsSlider.class).bindFromRequest();

        final Http.MultipartFormData<File> formData = request().body().asMultipartFormData();
        final Http.MultipartFormData.FilePart<File> filePart = formData.getFile("imgUrl");
        final File file = filePart.getFile();




        if (homepage.hasErrors()) {

            flash("danger", "Please correct the below form.");
            return badRequest(views.html.dekottena.cms.home.home
                    .render(homepage));
        } else {

            DkcmsSlider dkcmsslider = homepage.get();
            final String file_name = "abt-display" + "." + FilenameUtils.getExtension(filePart.getFilename());
            Session s = HibernateUtil.getSessionFactory().openSession();
            s.beginTransaction();

            if (file != null && file.exists()) {

                long data = ImageHandler.operateOnTempFile(file);
                int digit_part = Integer.parseInt(CharMatcher.DIGIT.retainFrom(FileUtils.byteCountToDisplaySize(data)));

                if (digit_part > 0) {

                    if (!IsValidImageSize(digit_part, 50, 257)) {

                        flash("danger", "Image size should be between 50KB and 256KB.");
                        return badRequest(views.html.dekottena.cms.home.home
                                .render(homepage));

                    }

                    MyAwsCredentials s3 = new MyAwsCredentials();
                    PutObjectRequest object_saved = new PutObjectRequest(s3.getBucket(),
                            s3.getUpFolder(file_name), file)
                            .withCannedAcl(CannedAccessControlList.PublicRead);
                    AmazonS3Client client = s3.getClient();
                    client.putObject(object_saved);

                    dkcmsslider.setImgUrl(Play.application().configuration().getString("env.file_download_path")
                            .concat(ConfigFactory.load("aws.conf").getString("env.file_upload_folder"))
                            .concat(file_name)
                    );
                }

            }

            s.update(dkcmsslider);
            s.getTransaction().commit();
            s.close();
            flash("success", "Successfully Saved. ");
            return redirect(routes.cmshomeController.home());


        }
    }
}
