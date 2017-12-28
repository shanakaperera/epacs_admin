package validators;

import javax.validation.*;
import java.lang.annotation.*;
import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

//@Target({FIELD})
//@Retention(RUNTIME)
//@Constraint(validatedBy = NumbersOnlyValidator.class)
//@play.data.Form.Display
public interface OnlyNumbers extends Annotation {
}
