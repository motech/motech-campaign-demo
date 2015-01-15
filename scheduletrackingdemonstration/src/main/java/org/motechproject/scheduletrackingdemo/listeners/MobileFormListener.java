package org.motechproject.scheduletrackingdemo.listeners;

import com.google.common.collect.Multimap;
import org.joda.time.DateTime;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.openmrs19.domain.OpenMRSFacility;
import org.motechproject.openmrs19.domain.OpenMRSPatient;
import org.motechproject.openmrs19.domain.OpenMRSPerson;
import org.motechproject.scheduletrackingdemo.PatientScheduler;
import org.motechproject.scheduletrackingdemo.domain.PatientRegistration;
import org.motechproject.scheduletrackingdemo.event.CommcareEventsParser;
import org.motechproject.scheduletrackingdemo.openmrs.OpenMrsClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MobileFormListener {
	private static final String DEMO_SCHEDULE_NAME = "Demo Concept Schedule";

	Logger logger = LoggerFactory.getLogger(MobileFormListener.class);
	
	@Autowired
	OpenMrsClient openmrsClient;
	
	@Autowired
	PatientScheduler patientScheduler;
	
	@MotechListener(subjects = { "org.motechproject.commcare.api.forms" })
	public void handleFormEvent(MotechEvent event) {
        String formType = ((Map) event.getParameters().get("attributes")).get("name").toString();

        if ("Registration".equals(formType)) {
            handleRegistrationForm(event.getParameters());
        }
	}

    private void handleRegistrationForm(Map<String, Object> parameters) {
        PatientRegistration bean = CommcareEventsParser.parseEventToPatientRegistration((Multimap) parameters.get("subElements"));

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

    private String stripDashFromPhoneNumber(String phoneNum) {
		return phoneNum.replaceAll("-", "");
	}
	
	//@MotechListener(subjects = { FormPublisher.FORM_VALIDATION_SUCCESSFUL + ".DemoGroup.DemoPatientEnrollment" })
	public void handlePatientEnrollment(MotechEvent event) {
		//PatientEnrollmentBean bean = (PatientEnrollmentBean)event.getParameters().get(FormPublisher.FORM_BEAN);
		//patientScheduler.saveMotechPatient(bean.getMotechId(), stripDashFromPhoneNumber(bean.getPhoneNumber()));
		//patientScheduler.enrollIntoSchedule(bean.getMotechId(), DEMO_SCHEDULE_NAME);
	}
	
	//@MotechListener(subjects = { FormPublisher.FORM_VALIDATION_SUCCESSFUL + ".DemoGroup.DemoPatientEncounter" })
	public void handlePatientEncounter(MotechEvent event) {
		//PatientEncounterBean bean = (PatientEncounterBean)event.getParameters().get(FormPublisher.FORM_BEAN);
		//String conceptName = OpenMrsConceptConverter.convertToNameFromIndex(bean.getObservedConcept());
		//openmrsClient.addEncounterForPatient(bean.getMotechId(), conceptName, bean.getObservedDate());
	}
}
