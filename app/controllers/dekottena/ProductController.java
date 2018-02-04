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
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
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
    private Map<String, String> tree = getTree();

    public Result home() {

        String action = "New Product";
        String dList = getDataList();
        String code = getNextProductSequence();

        Set<ProductHasCoating> phc = new HashSet<>();
        for (Coating c : coatings) {
            c.setSelected(false);
            phc.add(new ProductHasCoating(c));
        }
        Set<ProductHasFitting> phf = new HashSet<>();
        for (Fitting f : fittings) {
            f.setSelected(false);
            phf.add(new ProductHasFitting(f));
        }

        Product pi = new Product(phf, phc, code);

        Form<Product> productForm = formFactory.form(Product.class);

        return ok(views.html.dekottena.product_page.product.render(code, action, dList, productForm.fill(pi), tree));
    }

    @BodyParser.Of(MyMultipartFormDataBodyParser.class)
    public Result save() throws IOException {

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
            phcs.add(new ProductHasCoating(c, product));
        }

        Set<ProductHasFitting> phfs = new HashSet<>();
        for (Fitting f : f_selected) {
            phfs.add(new ProductHasFitting(f, product));
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
            String dList = getDataList();
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
            return badRequest(views.html.dekottena.product_page.product.render(code, action, dList, productForm, tree));
        }

        Product new_product = productForm.get();
        Session s = HibernateUtil.getSessionFactory().openSession();
        s.beginTransaction();

        if (mainImage != null && mainImage.exists()) {

            String action = "New Product";
            String dList = getDataList();
            String code = getNextProductSequence();
            final String file_name = code + "_main" + "." + FilenameUtils.getExtension(filePart_1.getFilename());

            long data = ImageHandler.operateOnTempFile(mainImage);
            int digit_part = Integer.parseInt(CharMatcher.DIGIT.retainFrom(FileUtils.byteCountToDisplaySize(data)));

            if (digit_part > 0) {

                if (!IsValidImageSize(digit_part, 50, 257)) {

                    flash("danger", "Image size should be between 256KB and 700KB.");
                    return badRequest(views.html.dekottena.product_page.product
                            .render(code, action, dList, productForm, tree));

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
            String dList = getDataList();
            String code = getNextProductSequence();
            final String file_name = code + "_sideview" + FilenameUtils.getExtension(filePart_2.getFilename());

            long data = ImageHandler.operateOnTempFile(sideView);
            int digit_part = Integer.parseInt(CharMatcher.DIGIT.retainFrom(FileUtils.byteCountToDisplaySize(data)));

            if (digit_part > 0) {

                if (!IsValidImageSize(digit_part, 50, 257)) {

                    flash("danger", "Image size should be between 256KB and 700KB.");
                    return badRequest(views.html.dekottena.product_page.product
                            .render(code, action, dList, productForm, tree));

                }

                MyAwsCredentials s3 = new MyAwsCredentials();
                PutObjectRequest object_saved = new PutObjectRequest(s3.getBucket(),
                        s3.getUpFolder(file_name), sideView)
                        .withCannedAcl(CannedAccessControlList.PublicRead);
                AmazonS3Client client = s3.getClient();
                client.putObject(object_saved);

                new_product.setSideViewsImg(Play.application().configuration().getString("env.file_download_path")
                        .concat(ConfigFactory.load("aws.conf").getString("env.file_upload_folder"))
                        .concat(file_name)
                );
            }

        }

        new_product.setTreeContent(getTC(s, productForm.get().getTreeContent().getNodeId()));
        s.save(new_product);
        s.getTransaction().commit();
        s.close();
        flash("success", "Successfully Saved. ");
        return redirect(routes.ProductController.home());

    }

    public Result edit(String code) {

        String pro_action = "Edit Product";
        String dList = getDataList();

        Session s = HibernateUtil.getSessionFactory().openSession();
        Criteria c = s.createCriteria(Product.class);
        c.add(Restrictions.eq("code", code));
        Product fp = (Product) c.uniqueResult();
        s.close();
        if (fp == null) {
            flash("danger", "No product found with given name.");
            return redirect(routes.ProductController.home());
        }

        Set<ProductHasCoating> phc = new HashSet<>();
        for (Coating co : coatings) {
            boolean b = fp.getProductHasCoatings().stream().anyMatch(p -> p.getCoating().getId() == co.getId());
            if (b) {
                co.setSelected(true);
            }
            phc.add(new ProductHasCoating(co, fp));
        }
        Set<ProductHasFitting> phf = new HashSet<>();
        for (Fitting fi : fittings) {
            boolean b = fp.getProductHasFittings().stream().anyMatch(p -> p.getFitting().getId() == fi.getId());
            if (b) {
                fi.setSelected(true);
            }
            phf.add(new ProductHasFitting(fi, fp));
        }

        fp.setProductHasCoatings(phc);
        fp.setProductHasFittings(phf);

        Form<Product> filterForm = formFactory.form(Product.class).fill(fp);

        return ok(views.html.dekottena.product_page.product
                .render(code, pro_action, dList, filterForm, tree));
    }

    @BodyParser.Of(MyMultipartFormDataBodyParser.class)
    public Result update() throws IOException {

        Form<Product> update_product = formFactory.form(Product.class).bindFromRequest();
        final Http.MultipartFormData<File> formData = request().body().asMultipartFormData();

        Map<String, String> c_data = update_product.rawData().entrySet()
                .stream().filter(p -> p.getKey().contains("coatings"))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        Map<String, String> f_data = update_product.rawData().entrySet()
                .stream().filter(p -> p.getKey().contains("fittings"))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        Http.MultipartFormData.FilePart<File> filePart_1 = formData.getFile("detailImgPath");
        Http.MultipartFormData.FilePart<File> filePart_2 = formData.getFile("sideViewsImg");

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

        Product product = update_product.get();
        Set<ProductHasCoating> phcs = new HashSet<>();
        for (Coating c : c_selected) {
            phcs.add(new ProductHasCoating(c, product));
        }

        Set<ProductHasFitting> phfs = new HashSet<>();
        for (Fitting f : f_selected) {
            phfs.add(new ProductHasFitting(f, product));
        }

        product.setProductHasFittings(phfs);
        product.setProductHasCoatings(phcs);
        update_product.fill(product);

        ArrayList<ValidationError> errors = new ArrayList<>();

        if (update_product.get().getProductHasCoatings().isEmpty()) {
            errors.add(new ValidationError("productHasCoatings", "At least one Coating option should be selected."));
        }
        if (update_product.get().getProductHasFittings().isEmpty()) {
            errors.add(new ValidationError("productHasFittings", "At least one Fitting option should be selected."));
        }

        for (ValidationError error : errors) {
            update_product.reject(error);
        }

        if (update_product.hasErrors()) {
            String action = "Edit Product";
            String dList = getDataList();
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
            return badRequest(views.html.dekottena.product_page.product.render(code, action, dList, update_product, tree));
        }

        Product pr_update = update_product.get();
        Session s = HibernateUtil.getSessionFactory().openSession();
        s.beginTransaction();

        if (mainImage != null && mainImage.exists()) {

            String action = "Edit Product";
            String dList = getDataList();
            String code = getNextProductSequence();
            final String file_name = code + "_main" + "." + FilenameUtils.getExtension(filePart_1.getFilename());

            long data = ImageHandler.operateOnTempFile(mainImage);
            int digit_part = Integer.parseInt(CharMatcher.DIGIT.retainFrom(FileUtils.byteCountToDisplaySize(data)));

            if (digit_part > 0) {

                if (!IsValidImageSize(digit_part, 50, 257)) {

                    flash("danger", "Image size should be between 256KB and 700KB.");
                    return badRequest(views.html.dekottena.product_page.product
                            .render(code, action, dList, update_product, tree));

                }

                MyAwsCredentials s3 = new MyAwsCredentials();
                PutObjectRequest object_saved = new PutObjectRequest(s3.getBucket(),
                        s3.getUpFolder(file_name), mainImage)
                        .withCannedAcl(CannedAccessControlList.PublicRead);
                AmazonS3Client client = s3.getClient();
                client.putObject(object_saved);

                pr_update.setDetailImgPath(Play.application().configuration().getString("env.file_download_path")
                        .concat(ConfigFactory.load("aws.conf").getString("env.file_upload_folder"))
                        .concat(file_name)
                );
            }

        }

        if (sideView != null && sideView.exists()) {

            String action = "New Product";
            String dList = getDataList();
            String code = getNextProductSequence();
            final String file_name = code + "_sideview" + FilenameUtils.getExtension(filePart_2.getFilename());

            long data = ImageHandler.operateOnTempFile(sideView);
            int digit_part = Integer.parseInt(CharMatcher.DIGIT.retainFrom(FileUtils.byteCountToDisplaySize(data)));

            if (digit_part > 0) {

                if (!IsValidImageSize(digit_part, 50, 257)) {

                    flash("danger", "Image size should be between 256KB and 700KB.");
                    return badRequest(views.html.dekottena.product_page.product
                            .render(code, action, dList, update_product, tree));

                }

                MyAwsCredentials s3 = new MyAwsCredentials();
                PutObjectRequest object_saved = new PutObjectRequest(s3.getBucket(),
                        s3.getUpFolder(file_name), sideView)
                        .withCannedAcl(CannedAccessControlList.PublicRead);
                AmazonS3Client client = s3.getClient();
                client.putObject(object_saved);

                pr_update.setSideViewsImg(Play.application().configuration().getString("env.file_download_path")
                        .concat(ConfigFactory.load("aws.conf").getString("env.file_upload_folder"))
                        .concat(file_name)
                );
            }

        }

        pr_update.setTreeContent(getTC(s, update_product.get().getTreeContent().getNodeId()));
        s.update(pr_update);
        s.getTransaction().commit();
        s.close();
        flash("success", "Successfully Updated. ");
        return redirect(routes.ProductController.home());
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

    private Map<String, String> getTree() {

        Session s = HibernateUtil.getSessionFactory().openSession();
        Query query = s.createSQLQuery(
                "CALL r_return_tree(:pedited, :lang);")
                .setParameter("pedited", 1)
                .setParameter("lang", "en");
        List<Object[]> rows = query.list();
        Map<String, String> tree = new HashMap<>();

        for (Object[] row : rows) {
            tree.put(row[0].toString(), row[1].toString());
        }
        s.close();
        return tree;
    }

    private TreeContent getTC(Session s, int nodeId) {
        Criteria c = s.createCriteria(TreeContent.class);
        c.add(Restrictions.eq("nodeId", nodeId));
        TreeContent tc = (TreeContent) c.uniqueResult();
        return tc;
    }

    private String getDataList() {

        Session s = HibernateUtil.getSessionFactory().openSession();
        Criteria c = s.createCriteria(Product.class);
        c.setProjection(Projections.projectionList()
                .add(Projections.property("code"), "code")
                .add(Projections.property("title"), "title")
        );
        c.setResultTransformer(Transformers.aliasToBean(Product.class));
        List<Product> pList = c.list();

        String dList = "";
        for (Product po : pList) {
            dList += "<option value='" + po.getCode() + "'>" + po.getTitle() + "</option>";
        }

        s.close();
        return dList;
    }
}
