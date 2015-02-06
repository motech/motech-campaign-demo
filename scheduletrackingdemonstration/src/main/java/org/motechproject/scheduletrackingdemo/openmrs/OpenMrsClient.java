package org.motechproject.scheduletrackingdemo.openmrs;

import org.joda.time.DateTime;
import org.motechproject.openmrs19.domain.OpenMRSPatient;

import java.util.Date;

public interface OpenMrsClient {

    boolean hasConcept(String patientId, String conceptName);

    public void printValues(String externalID, String conceptName);

    public DateTime lastTimeFulfilledDateTimeObs(String patientId, String conceptName);

    public OpenMRSPatient getPatientByMotechId(String patientId);

    void savePatient(OpenMRSPatient patient);

    void addEncounterForPatient(String motechId, String conceptName, Date observedDate);
}
