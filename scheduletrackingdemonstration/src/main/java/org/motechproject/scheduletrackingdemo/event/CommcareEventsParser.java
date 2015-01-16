package org.motechproject.scheduletrackingdemo.event;

import com.google.common.collect.Multimap;
import org.motechproject.scheduletrackingdemo.domain.PatientEncounter;
import org.motechproject.scheduletrackingdemo.domain.PatientEnrollment;
import org.motechproject.scheduletrackingdemo.domain.PatientRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;

public final class CommcareEventsParser {

    private static final Logger LOG = LoggerFactory.getLogger(CommcareEventsParser.class);

    private static final String VAL = "value";
    private static final String DATE_FORMAT = "yyyy-MM-d";
    private static final DateFormat DF = new SimpleDateFormat(DATE_FORMAT);

    public static PatientRegistration parseEventToPatientRegistration(Multimap elements) {
        PatientRegistration patient = new PatientRegistration();
        patient.setFirstName(valueFromMultimap(elements, "firstName"));
        patient.setLastName(valueFromMultimap(elements, "lastName"));
        patient.setMotechId(valueFromMultimap(elements, "motechID"));
        String dateString = valueFromMultimap(elements, "dob");

        try {
            patient.setDateOfBirth(DF.parse(dateString));
        } catch (ParseException e) {
            LOG.error("The parsing of date {} using format {} has failed", dateString, DATE_FORMAT);
        }

        patient.setGender(valueFromMultimap(elements, "gender"));
        patient.setPhoneNumber(valueFromMultimap(elements, "phoneNumber"));
        patient.setEnrollPatient(Boolean.getBoolean(valueFromMultimap(elements, "enrollPatient")));

        return patient;
    }

    public static PatientEncounter parseEventToPatientEncounter(Multimap elements) {
        PatientEncounter encounter = new PatientEncounter();
        encounter.setMotechId(valueFromMultimap(elements, "motechId"));

        String observedDateString = valueFromMultimap(elements, "observedDate");
        try {
            encounter.setObservedDate(DF.parse(observedDateString));
        } catch (ParseException e) {
            LOG.error("The parsing of date {} using format {} has failed", observedDateString, DATE_FORMAT);
        }

        encounter.setObservedConcept(Integer.valueOf(valueFromMultimap(elements, "observedConcept")));

        return encounter;
    }

    public static PatientEnrollment parseEventToPatientEnrollment(Multimap elements) {
        PatientEnrollment enrollment = new PatientEnrollment();

        enrollment.setMotechId(valueFromMultimap(elements, "motechId"));
        enrollment.setPhoneNumber(valueFromMultimap(elements, "phoneNumber"));

        return enrollment;
    }

    private static String valueFromMultimap(Multimap map, String elementName) {
        return ((Map) map.get(elementName).toArray()[0]).get(VAL).toString();
    }
}
