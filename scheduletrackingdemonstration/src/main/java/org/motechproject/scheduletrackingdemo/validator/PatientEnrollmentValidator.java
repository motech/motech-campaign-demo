package org.motechproject.scheduletrackingdemo.validator;

import org.motechproject.scheduletrackingdemo.domain.PatientEnrollment;
import org.motechproject.scheduletrackingdemo.openmrs.OpenMrsClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class PatientEnrollmentValidator extends AbstractPatientValidator {

	@Autowired
	public PatientEnrollmentValidator(OpenMrsClient openmrsClient) {
		super(openmrsClient);
	}

	public Map<String, String> validate(PatientEnrollment formBean) {
        Map<String, String> errors = new HashMap<>();
		validatePhoneNumberFormat(formBean.getPhoneNumber(), errors);
		validateOpenMrsPatientExists(formBean.getMotechId(), errors);
		
		return errors;
	}
}
