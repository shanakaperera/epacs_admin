package controllers.dekottena;

import models.Product;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;

public class CustomerController extends Controller{
    @Inject
    FormFactory formfactoroy;

    public Result home() {

        Form<Product> productForm = formfactoroy.form(Product.class);

        return ok(views.html.dekottena.customer_page.customer.render("cccc",productForm));


    }
}
