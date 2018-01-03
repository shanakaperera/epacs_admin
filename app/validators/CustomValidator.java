package validators;

import org.apache.commons.lang3.Range;

public class CustomValidator {

    public static boolean IsValidImageSize(int givenSize, int min, int max) {

        Range<Integer> myRange = Range.between(min, max);
        return myRange.contains(givenSize);

    }
}
