package org.motechproject.scheduletrackingdemo.validator;

import org.motechproject.openmrs19.domain.OpenMRSPatient;
import org.motechproject.scheduletrackingdemo.domain.PatientRegistration;
import org.motechproject.scheduletrackingdemo.openmrs.OpenMrsClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class PatientRegistrationValidator extends AbstractMobileValidator {

    private OpenMrsClient openmrsClient;

    @Autowired
    public PatientRegistrationValidator(OpenMrsClient openmrsClient) {
        this.openmrsClient = openmrsClient;
    }

    public Map<String, String> validate(PatientRegistration formBean) {
        Map<String, String> errors = new HashMap<>();
        validatePhoneNumberFormat(formBean.getPhoneNumber(), errors);
        validateUniqueMotechId(formBean.getMotechId(), errors);

        return errors;
    }

    protected void validateUniqueMotechId(String motechId, Map<String, String> errors) {
        OpenMRSPatient existingPatient = openmrsClient.getPatientByMotechId(motechId);
        if (existingPatient != null) {
            errors.put("motechId", "Already a patient with this MoTeCH Id");
        }
    }
}
