package controllers.dekottena;

import models.DkcmsSlider;
import org.hibernate.Session;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Result;
import services.HibernateUtil;

import javax.inject.Inject;

public class OrderConfirmController extends Controller {


    public Result home() {

        return ok(views.html.dekottena.order_confirm_page.order_confirm.render());

    }
}
