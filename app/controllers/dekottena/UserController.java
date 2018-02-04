package controllers.dekottena;

import models.Product;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;

public class UserController extends Controller {

    @Inject
    FormFactory formfactoroy;

    public Result home() {

        Form<Product> productForm = formfactoroy.form(Product.class);

        return ok(views.html.dekottena.user_page.user.render("cccc",productForm,productForm));

    }
}
