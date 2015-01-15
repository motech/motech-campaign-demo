/*
package org.motechproject.scheduletrackingdemo;

import org.motechproject.scheduletrackingdemo.validator.PatientEncounterValidator;
import org.motechproject.scheduletrackingdemo.validator.PatientEnrollmentValidator;
import org.motechproject.scheduletrackingdemo.validator.PatientRegistrationValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;

@Component
public class FormValidatorRegister implements ServletContextAware {

	private PatientRegistrationValidator patientRegValidator;
	private PatientEnrollmentValidator patientEnrollValidator;
	private PatientEncounterValidator patientEncounterValidator;

	@Autowired
	public FormValidatorRegister(
			PatientRegistrationValidator patientRegValidator,
			PatientEnrollmentValidator patientEnrollValidator,
			PatientEncounterValidator patientEncounterValidator) {
		this.patientRegValidator = patientRegValidator;
		this.patientEnrollValidator = patientEnrollValidator;
		this.patientEncounterValidator = patientEncounterValidator;
	}

	@Override
	public void setServletContext(ServletContext servletContext) {
		servletContext.setAttribute(PatientRegistrationValidator.class.getName(), patientRegValidator);
		servletContext.setAttribute(PatientEnrollmentValidator.class.getName(), patientEnrollValidator);
		servletContext.setAttribute(PatientEncounterValidator.class.getName(), patientEncounterValidator);
	}
}
*/
