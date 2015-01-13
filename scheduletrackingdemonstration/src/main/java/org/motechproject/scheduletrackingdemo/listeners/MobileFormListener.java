package org.motechproject.scheduletrackingdemo.listeners;

import org.motechproject.scheduletrackingdemo.openmrs.OpenMrsClient;
import org.motechproject.scheduletrackingdemo.openmrs.OpenMrsConceptConverter;
import org.motechproject.scheduletrackingdemo.PatientScheduler;
import org.motechproject.scheduletrackingdemo.beans.PatientEncounterBean;
import org.motechproject.scheduletrackingdemo.beans.PatientEnrollmentBean;
import org.motechproject.scheduletrackingdemo.beans.PatientRegistrationBean;
import org.motechproject.mobileforms.api.callbacks.FormPublisher;
import org.motechproject.model.MotechEvent;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;
import org.motechproject.server.event.annotations.MotechListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MobileFormListener {
	private static final String DEMO_SCHEDULE_NAME = "Demo Concept Schedule";

	Logger logger = LoggerFactory.getLogger(MobileFormListener.class);
	
	@Autowired
	OpenMrsClient openmrsClient;
	
	@Autowired
	PatientScheduler patientScheduler;
	
	@MotechListener(subjects = { FormPublisher.FORM_VALIDATION_SUCCESSFUL + ".DemoGroup.DemoPatientRegistration" })
	public void handlePatientRegistrationForm(MotechEvent event) {
		PatientRegistrationBean bean = (PatientRegistrationBean)event.getParameters().get(FormPublisher.FORM_BEAN);
		MRSPerson person = new MRSPerson().firstName(bean.getFirstName())
									 	  .lastName(bean.getLastName())
									 	  .dateOfBirth(bean.getDateOfBirth())
									 	  .birthDateEstimated(false)
									 	  .gender(bean.getGender());
		MRSFacility facility = new MRSFacility("1");
		MRSPatient patient = new MRSPatient(bean.getMotechId(), person, facility);
		
		openmrsClient.savePatient(patient);
		patientScheduler.saveMotechPatient(bean.getMotechId(), stripDashFromPhoneNumber(bean.getPhoneNumber()));
		
		if (bean.isEnrollPatient()) {
			patientScheduler.enrollIntoSchedule(bean.getMotechId(), DEMO_SCHEDULE_NAME);
		}
	}

	private String stripDashFromPhoneNumber(String phoneNum) {
		return phoneNum.replaceAll("-", "");
	}
	
	@MotechListener(subjects = { FormPublisher.FORM_VALIDATION_SUCCESSFUL + ".DemoGroup.DemoPatientEnrollment" })
	public void handlePatientEnrollment(MotechEvent event) {
		PatientEnrollmentBean bean = (PatientEnrollmentBean)event.getParameters().get(FormPublisher.FORM_BEAN);
		patientScheduler.saveMotechPatient(bean.getMotechId(), stripDashFromPhoneNumber(bean.getPhoneNumber()));
		patientScheduler.enrollIntoSchedule(bean.getMotechId(), DEMO_SCHEDULE_NAME);
	}
	
	@MotechListener(subjects = { FormPublisher.FORM_VALIDATION_SUCCESSFUL + ".DemoGroup.DemoPatientEncounter" })
	public void handlePatientEncounter(MotechEvent event) {
		PatientEncounterBean bean = (PatientEncounterBean)event.getParameters().get(FormPublisher.FORM_BEAN);
		String conceptName = OpenMrsConceptConverter.convertToNameFromIndex(bean.getObservedConcept());
		openmrsClient.addEncounterForPatient(bean.getMotechId(), conceptName, bean.getObservedDate());
	}
}
