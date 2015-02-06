package org.motechproject.scheduletrackingdemo.validator;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public abstract class AbstractMobileValidator {

    private static Pattern PHONE_NUMBER_PATTERN = Pattern.compile("^[1-9][0-9]{2}-[1-9][0-9]{2}-[0-9]{4}$");

    protected void validatePhoneNumberFormat(String phoneNumber, Map<String, String> errors) {
        Matcher matcher = PHONE_NUMBER_PATTERN.matcher(phoneNumber);
        if (!matcher.matches()) {
            errors.put("phoneNumber", "Incorrect format for phone number. Format should be XXX-XXX-XXXX");
        }
    }
}
