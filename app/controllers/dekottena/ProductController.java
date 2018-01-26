package controllers.dekottena;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.google.common.base.CharMatcher;
import com.typesafe.config.ConfigFactory;
import models.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.transform.Transformers;
import play.Play;
import play.data.DynamicForm;
import play.data.Form;
import play.data.FormFactory;
import play.data.validation.ValidationError;
import play.mvc.*;
import services.*;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static validators.CustomValidator.IsValidImageSize;

public class ProductController extends Controller {

    @Inject
    FormFactory formFactory;
    private static final String TABLE_NAME = "product";
    private List<Coating> coatings = getCoatingList();
    private List<Fitting> fittings = getFittingList();

    public Result home() {

        String action = "New Product";
        String code = getNextProductSequence();


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

    @BodyParser.Of(MyMultipartFormDataBodyParser.class)
    public Result save() throws IOException{

        Form<Product> productForm = formFactory.form(Product.class).bindFromRequest();

        final Http.MultipartFormData<File> formData = request().body().asMultipartFormData();

        Map<String, String> c_data = productForm.rawData().entrySet()
                .stream().filter(p -> p.getKey().contains("coatings"))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        Map<String, String> f_data = productForm.rawData().entrySet()
                .stream().filter(p -> p.getKey().contains("fittings"))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        Http.MultipartFormData.FilePart<File> filePart_1 = formData.getFile("mainImage");
        Http.MultipartFormData.FilePart<File> filePart_2 = formData.getFile("sideView");

        File mainImage = filePart_1.getFile();
        File sideView = filePart_2.getFile();


        List<Coating> c_selected = new ArrayList<>();
        List<Fitting> f_selected = new ArrayList<>();

        for (int c = 0; c < coatings.size(); c++) {
            if (c_data != null && c_data.containsValue(coatings.get(c).getCode())) {
                c_selected.add(coatings.get(c));
            }
        }

        for (int f = 0; f < fittings.size(); f++) {
            if (f_data != null && f_data.containsValue(fittings.get(f).getCode())) {
                f_selected.add(fittings.get(f));
            }
        }


        Product product = productForm.get();
        Set<ProductHasCoating> phcs = new HashSet<>();
        for (Coating c : c_selected) {
            phcs.add(new ProductHasCoating(c));
        }

        Set<ProductHasFitting> phfs = new HashSet<>();
        for (Fitting f : f_selected) {
            phfs.add(new ProductHasFitting(f));
        }

        product.setProductHasFittings(phfs);
        product.setProductHasCoatings(phcs);
        productForm.fill(product);

        ArrayList<ValidationError> errors = new ArrayList<>();

        if (productForm.get().getProductHasCoatings().isEmpty()) {
            errors.add(new ValidationError("productHasCoatings", "At least one Coating option should be selected."));
        }
        if (productForm.get().getProductHasFittings().isEmpty()) {
            errors.add(new ValidationError("productHasFittings", "At least one Fitting option should be selected."));
        }

        for (ValidationError error : errors) {
            productForm.reject(error);
        }

        if (productForm.hasErrors()) {
            String action = "New Product";
            String code = getNextProductSequence();

            Set<ProductHasCoating> phc = new HashSet<>();
            for (Coating c : coatings) {
                c.setSelected(c_selected.stream().anyMatch(p -> p.getCode().equalsIgnoreCase(c.getCode())));
                phc.add(new ProductHasCoating(c));

            }
            Set<ProductHasFitting> phf = new HashSet<>();
            for (Fitting f : fittings) {
                f.setSelected(f_selected.stream().anyMatch(p -> p.getCode().equalsIgnoreCase(f.getCode())));
                phf.add(new ProductHasFitting(f));
            }

            product.setProductHasCoatings(phc);
            product.setProductHasFittings(phf);
            flash("danger", "Please correct the below form.");
            return badRequest(views.html.dekottena.product_page.product.render(code, action, productForm));
        }

        Product new_product = productForm.get();
        Session s = HibernateUtil.getSessionFactory().openSession();
        s.beginTransaction();

        if (mainImage != null && mainImage.exists()) {

            String action = "New Product";
            String code = getNextProductSequence();
            final String file_name = code + "." + FilenameUtils.getExtension(filePart_1.getFilename());

            long data = ImageHandler.operateOnTempFile(mainImage);
            int digit_part = Integer.parseInt(CharMatcher.DIGIT.retainFrom(FileUtils.byteCountToDisplaySize(data)));

            if (digit_part > 0) {

                if (!IsValidImageSize(digit_part, 50, 257)) {

                    flash("danger", "Image size should be between 256KB and 700KB.");
                    return badRequest(views.html.dekottena.product_page.product
                            .render(code, action, productForm));

                }

                MyAwsCredentials s3 = new MyAwsCredentials();
                PutObjectRequest object_saved = new PutObjectRequest(s3.getBucket(),
                        s3.getUpFolder(file_name), mainImage)
                        .withCannedAcl(CannedAccessControlList.PublicRead);
                AmazonS3Client client = s3.getClient();
                client.putObject(object_saved);

                new_product.setDetailImgPath(Play.application().configuration().getString("env.file_download_path")
                        .concat(ConfigFactory.load("aws.conf").getString("env.file_upload_folder"))
                        .concat(file_name)
                );
            }

        }

        if (sideView != null && sideView.exists()) {

            String action = "New Product";
            String code = getNextProductSequence();
            final String file_name = code + "." + FilenameUtils.getExtension(filePart_2.getFilename());

            long data = ImageHandler.operateOnTempFile(sideView);
            int digit_part = Integer.parseInt(CharMatcher.DIGIT.retainFrom(FileUtils.byteCountToDisplaySize(data)));

            if (digit_part > 0) {

                if (!IsValidImageSize(digit_part, 50, 257)) {

                    flash("danger", "Image size should be between 256KB and 700KB.");
                    return badRequest(views.html.dekottena.product_page.product
                            .render(code, action, productForm));

                }

                MyAwsCredentials s3 = new MyAwsCredentials();
                PutObjectRequest object_saved = new PutObjectRequest(s3.getBucket(),
                        s3.getUpFolder(file_name), sideView)
                        .withCannedAcl(CannedAccessControlList.PublicRead);
                AmazonS3Client client = s3.getClient();
                client.putObject(object_saved);

                new_product.setHomeImgPath(Play.application().configuration().getString("env.file_download_path")
                        .concat(ConfigFactory.load("aws.conf").getString("env.file_upload_folder"))
                        .concat(file_name)
                );
            }

        }


        s.save(new_product);
        s.getTransaction().commit();
        s.close();
        flash("success", "Successfully Saved. ");
        return redirect(routes.ProductController.home());

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
