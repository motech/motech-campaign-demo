package org.motechproject.scheduletrackingdemo.validator;

import org.joda.time.DateTime;
import org.motechproject.scheduletrackingdemo.domain.PatientEncounter;
import org.motechproject.scheduletrackingdemo.openmrs.OpenMrsClient;
import org.motechproject.scheduletrackingdemo.openmrs.OpenMrsConceptConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class PatientEncounterValidator extends AbstractPatientValidator {
	
	@Autowired
	public PatientEncounterValidator(OpenMrsClient openmrsClient) {
		super(openmrsClient);
	}

	public Map<String, String> validate(PatientEncounter formBean) {
        Map<String, String> errors = new HashMap<>();
		validateOpenMrsPatientExists(formBean.getMotechId(), errors);
		if (errors.size() > 0) {
			// no point in other validation checks if patient doesn't exist in system
			return errors;
		}
		
		String conceptName = OpenMrsConceptConverter.convertToNameFromIndex(formBean.getObservedConcept());
		validateValidNextConcept(formBean.getMotechId(), conceptName, formBean.getObservedDate(), errors);
		
		return errors;
	}

	private void validateValidNextConcept(String motechId, String conceptName, Date fulfilledDate, Map<String, String> errors) {
		String previousConcept = OpenMrsConceptConverter.getConceptBefore(conceptName);
		if (!previousConcept.equals(conceptName)) {
			if (!openmrsClient.hasConcept(motechId, previousConcept)) {
				errors.put("observedConcept", "Patient has not fulfilled previous concept: " + previousConcept);
				return;
			}
			
			if (openmrsClient.hasConcept(motechId, conceptName)) {
				errors.put("observedConcept", "Patient already has concept: " + conceptName);
				return;
			}
			
			DateTime lastFulfilledDate = openmrsClient.lastTimeFulfilledDateTimeObs(motechId, previousConcept);
			DateTime currentFufilledDate = new DateTime(fulfilledDate);
			if (currentFufilledDate.isBefore(lastFulfilledDate)) {
				errors.put("observedDate", "Current fufill date is before last fulfill date");
			}
		}
	}
}
