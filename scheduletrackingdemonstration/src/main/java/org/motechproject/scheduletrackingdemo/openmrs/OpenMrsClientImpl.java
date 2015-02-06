package org.motechproject.scheduletrackingdemo.openmrs;

import org.joda.time.DateTime;
import org.motechproject.openmrs19.domain.OpenMRSEncounter;
import org.motechproject.openmrs19.domain.OpenMRSFacility;
import org.motechproject.openmrs19.domain.OpenMRSObservation;
import org.motechproject.openmrs19.domain.OpenMRSPatient;
import org.motechproject.openmrs19.domain.OpenMRSProvider;
import org.motechproject.openmrs19.domain.OpenMRSUser;
import org.motechproject.openmrs19.service.OpenMRSEncounterService;
import org.motechproject.openmrs19.service.OpenMRSFacilityService;
import org.motechproject.openmrs19.service.OpenMRSObservationService;
import org.motechproject.openmrs19.service.OpenMRSPatientService;
import org.motechproject.openmrs19.service.OpenMRSUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class OpenMrsClientImpl implements OpenMrsClient {

    private static final Logger LOG = LoggerFactory.getLogger(OpenMrsClientImpl.class);

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    @Autowired
    private OpenMRSEncounterService encounterService;

    @Autowired
    private OpenMRSPatientService patientService;

    @Autowired
    private OpenMRSObservationService observationService;

    @Autowired
    private OpenMRSFacilityService facilityService;

    @Autowired
    private OpenMRSUserService userService;

    public boolean hasConcept(String patientId, String conceptName) {
        LOG.debug(conceptName);
        List<OpenMRSObservation> observationList = observationService.findObservations(patientId, conceptName);

        boolean found = false;
        if (observationList.size() > 0) {
            LOG.debug("Found encounter");
            found = true;
        } else {
            LOG.debug("No encounter found for: " + patientId);
        }
        return found;

    }

    public void printValues(String externalID, String conceptName) {
        List<OpenMRSObservation> mrsObservations = observationService.findObservations(externalID, conceptName);

        LOG.info("***** OBSERVATIONS *****");
        for (OpenMRSObservation mrsObservation : mrsObservations) {
            LOG.info(mrsObservation.toString());
        }
        LOG.info("***** ENCOUNTERS *****");
        List<OpenMRSEncounter> mrsEncounters = encounterService.getEncountersByEncounterType(externalID, conceptName);
        for (OpenMRSEncounter mrsEncounter : mrsEncounters) {
            for (OpenMRSObservation mrsObservation : mrsEncounter.getObservations()) {
                LOG.info("Belongs to: " + mrsObservation.toString());
            }
        }
    }

    public DateTime lastTimeFulfilledDateTimeObs(String patientId, String conceptName) {
        List<OpenMRSObservation> mrsObservations = observationService.findObservations(patientId, conceptName);
        Collections.sort(mrsObservations, new DateComparator());

        if (mrsObservations.size() > 0) {
            return new DateTime(mrsObservations.get(0).getValue());
        }

        return new DateTime();

    }

    public OpenMRSPatient getPatientByMotechId(String patientId) {
        return patientService.getPatientByMotechId(patientId);
    }

    private class DateComparator implements Comparator<OpenMRSObservation> {

        @Override
        public int compare(OpenMRSObservation o1, OpenMRSObservation o2) {
            DateTime time1 = new DateTime(o1.getValue());
            DateTime time2 = new DateTime(o2.getValue());

            if (time1.isBefore(time2)) {
                return -1;
            } else if (time1.isAfter(time2)) {
                return 1;
            }
            return 0;
        }

    }

    public void savePatient(OpenMRSPatient patient) {
        patientService.savePatient(patient);
    }

    public void addEncounterForPatient(String motechId, String conceptName, Date observedDate) {
        OpenMRSObservation<String> observation = new OpenMRSObservation<>(observedDate, conceptName, DATE_FORMAT.format(observedDate));
        Set<OpenMRSObservation> observations = new HashSet<>();
        observations.add(observation);
        OpenMRSPatient patient = patientService.getPatientByMotechId(motechId);

        List<? extends OpenMRSFacility> facilities = facilityService.getFacilities();
        OpenMRSFacility facility = facilities.isEmpty() ? null : facilities.get(0);

        OpenMRSUser user = userService.getUserByUserName("admin");

        OpenMRSEncounter encounter = new OpenMRSEncounter(new OpenMRSProvider(user.getPerson()), user, facility, observedDate, patient, observations, "ADULTRETURN");
        encounterService.createEncounter(encounter);
    }
}
