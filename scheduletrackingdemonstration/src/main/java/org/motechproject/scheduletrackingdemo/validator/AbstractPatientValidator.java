package org.motechproject.scheduletrackingdemo.validator;

import org.motechproject.openmrs19.domain.OpenMRSPatient;
import org.motechproject.scheduletrackingdemo.openmrs.OpenMrsClient;

import java.util.Map;

public abstract class AbstractPatientValidator extends AbstractMobileValidator {

    protected OpenMrsClient openmrsClient;

    public AbstractPatientValidator(OpenMrsClient openmrsClient) {
        this.openmrsClient = openmrsClient;
    }

    protected void validateOpenMrsPatientExists(String motechId, Map<String, String> errors) {
        OpenMRSPatient existingPatient = openmrsClient.getPatientByMotechId(motechId);
        if (existingPatient == null) {
            errors.put("motechId", "Could not find OpenMRS Patient with this MoTeCH Id");
        }
    }

}
