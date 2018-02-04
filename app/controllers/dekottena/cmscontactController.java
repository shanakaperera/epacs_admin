package controllers.dekottena;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.google.common.base.CharMatcher;
import com.typesafe.config.ConfigFactory;
import models.DkcmsAbout;
import models.DkcmsContact;
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

public class cmscontactController extends Controller{
    @Inject
    FormFactory formfactoroy;

    public Result home() {

        Session s = HibernateUtil.getSessionFactory().openSession();
        DkcmsContact contact = s.load(DkcmsContact.class,1);
        Form<DkcmsContact> contactForm = formfactoroy.form(DkcmsContact.class).fill(contact);

        return ok(views.html.dekottena.cms.contact.contact.render(contactForm));

    }


    @BodyParser.Of(MyMultipartFormDataBodyParser.class)
    public Result save() throws IOException {

        Form<DkcmsContact> contactuspage = formfactoroy.form(DkcmsContact.class).bindFromRequest();

//        final Http.MultipartFormData<File> formData = request().body().asMultipartFormData();

        if (contactuspage.hasErrors()) {

            flash("danger", "Please correct the below form.");
            return badRequest(views.html.dekottena.cms.contact.contact
                    .render(contactuspage));
        } else {

            DkcmsContact dkcmscontact = contactuspage.get();
            Session s = HibernateUtil.getSessionFactory().openSession();
            s.beginTransaction();



            s.update(dkcmscontact);
            s.getTransaction().commit();
            s.close();
            flash("success", "Successfully Saved. ");
            return redirect(routes.cmscontactController.home());
        }



    }

}
