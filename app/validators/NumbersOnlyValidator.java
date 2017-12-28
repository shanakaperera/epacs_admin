package validators;

import play.libs.F;

import javax.validation.ConstraintValidator;

public class NumbersOnlyValidator extends play.data.validation.Constraints.Validator<Object>
        implements ConstraintValidator<OnlyNumbers, Object> {
    @Override
    public void initialize(OnlyNumbers onlyNumbers) {

    }

    @Override
    public boolean isValid(Object o) {
        return false;
    }

    @Override
    public F.Tuple<String, Object[]> getErrorMessageKey() {
        return null;
    }

}
