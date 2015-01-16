package org.motechproject.scheduletrackingdemo.listeners;

import com.google.common.collect.Multimap;
import org.joda.time.DateTime;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.openmrs19.domain.OpenMRSFacility;
import org.motechproject.openmrs19.domain.OpenMRSPatient;
import org.motechproject.openmrs19.domain.OpenMRSPerson;
import org.motechproject.scheduletrackingdemo.PatientScheduler;
import org.motechproject.scheduletrackingdemo.domain.PatientEncounter;
import org.motechproject.scheduletrackingdemo.domain.PatientEnrollment;
import org.motechproject.scheduletrackingdemo.domain.PatientRegistration;
import org.motechproject.scheduletrackingdemo.event.CommcareEventsParser;
import org.motechproject.scheduletrackingdemo.openmrs.OpenMrsClient;
import org.motechproject.scheduletrackingdemo.openmrs.OpenMrsConceptConverter;
import org.motechproject.scheduletrackingdemo.validator.PatientEncounterValidator;
import org.motechproject.scheduletrackingdemo.validator.PatientEnrollmentValidator;
import org.motechproject.scheduletrackingdemo.validator.PatientRegistrationValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MobileFormListener {
	private static final String DEMO_SCHEDULE_NAME = "Demo Concept Schedule";
	private static final Logger LOG = LoggerFactory.getLogger(MobileFormListener.class);
	
	@Autowired
	private OpenMrsClient openmrsClient;
	
	@Autowired
	private PatientScheduler patientScheduler;

    @Autowired
    private PatientRegistrationValidator registrationValidator;

    @Autowired
    private PatientEnrollmentValidator enrollmentValidator;

    @Autowired
    private PatientEncounterValidator encounterValidator;

	
	@MotechListener(subjects = { "org.motechproject.commcare.api.forms" })
	public void handleFormEvent(MotechEvent event) {
        String formType = ((Map) event.getParameters().get("attributes")).get("name").toString();

        if ("Registration".equals(formType)) {
            handleRegistrationForm(event.getParameters());
        } else if ("Encounter".equals(formType)) {
            handleEncounterForm(event.getParameters());
        } else if ("Enrollment".equals(formType)) {
            handleEnrollmentForm(event.getParameters());
        }
	}

    private void handleRegistrationForm(Map<String, Object> parameters) {
        PatientRegistration bean = CommcareEventsParser.parseEventToPatientRegistration((Multimap) parameters.get("subElements"));

        Map<String, String> errors = registrationValidator.validate(bean);
        if (!errors.isEmpty()) {
            LOG.error("The validation of a registration form has failed for the following fields: ");
            for (Map.Entry<String, String> entry : errors.entrySet()) {
                LOG.error("Field: {} - Cause: {}", entry.getKey(), entry.getValue());
            }
            return;
        }

        OpenMRSPerson person = new OpenMRSPerson(bean.getMotechId());
        person.setFirstName(bean.getFirstName());
        person.setLastName(bean.getLastName());
        person.setDateOfBirth(new DateTime(bean.getDateOfBirth()));
        person.setBirthDateEstimated(false);
        person.setGender(bean.getGender());

        OpenMRSFacility facility = new OpenMRSFacility("1");
        OpenMRSPatient patient = new OpenMRSPatient(bean.getMotechId(), person, facility);

        openmrsClient.savePatient(patient);
        patientScheduler.saveMotechPatient(bean.getMotechId(), stripDashFromPhoneNumber(bean.getPhoneNumber()));

        if (bean.isEnrollPatient()) {
            patientScheduler.enrollIntoSchedule(bean.getMotechId(), DEMO_SCHEDULE_NAME);
        }
    }

    private void handleEnrollmentForm(Map<String, Object> parameters) {
        PatientEnrollment enrollment = CommcareEventsParser.parseEventToPatientEnrollment((Multimap) parameters.get("subElements"));

        Map<String, String> errors = enrollmentValidator.validate(enrollment);
        if (!errors.isEmpty()) {
            LOG.error("The validation of an enrollment form has failed for the following fields: ");
            for (Map.Entry<String, String> entry : errors.entrySet()) {
                LOG.error("Field: {} - Cause: {}", entry.getKey(), entry.getValue());
            }
            return;
        }

        patientScheduler.saveMotechPatient(enrollment.getMotechId(), stripDashFromPhoneNumber(enrollment.getPhoneNumber()));
        patientScheduler.enrollIntoSchedule(enrollment.getMotechId(), DEMO_SCHEDULE_NAME);
    }

    private void handleEncounterForm(Map<String, Object> parameters) {
        PatientEncounter encounter = CommcareEventsParser.parseEventToPatientEncounter((Multimap) parameters.get("subElements"));

        Map<String, String> errors = encounterValidator.validate(encounter);
        if (!errors.isEmpty()) {
            LOG.error("The validation of an encounter form has failed for the following fields: ");
            for (Map.Entry<String, String> entry : errors.entrySet()) {
                LOG.error("Field: {} - Cause: {}", entry.getKey(), entry.getValue());
            }
            return;
        }

        String conceptName = OpenMrsConceptConverter.convertToNameFromIndex(encounter.getObservedConcept());
        openmrsClient.addEncounterForPatient(encounter.getMotechId(), conceptName, encounter.getObservedDate());
    }

    private String stripDashFromPhoneNumber(String phoneNum) {
		return phoneNum.replaceAll("-", "");
	}

}
