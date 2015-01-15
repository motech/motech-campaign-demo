package org.motechproject.scheduletrackingdemo;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.motechproject.commons.date.model.Time;
import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.scheduletracking.domain.Milestone;
import org.motechproject.scheduletracking.domain.Schedule;
import org.motechproject.scheduletracking.service.EnrollmentRecord;
import org.motechproject.scheduletracking.service.EnrollmentRequest;
import org.motechproject.scheduletracking.service.ScheduleTrackingService;
import org.motechproject.scheduletrackingdemo.dao.PatientDataService;
import org.motechproject.scheduletrackingdemo.model.Patient;
import org.motechproject.scheduletrackingdemo.openmrs.OpenMrsClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * This class provides an API for tasks relating to motech patients and schedule tracking
 */
@Component
public class PatientScheduler {
	
	@Autowired
	private PatientDataService patientDataService;
	
	@Autowired
	private OpenMrsClient openMrsClient;

	@Autowired
	private ScheduleTrackingService scheduleTrackingService;
	
	public void enrollIntoSchedule(String externalID, String scheduleName) {
		if (patientDataService.findByExternalId(externalID) != null &&
				openMrsClient.getPatientByMotechId(externalID) != null) { //do not let users that aren't in both databases register
			Schedule schedule = scheduleTrackingService.getScheduleByName(scheduleName);

			if (schedule == null)
				throw new RuntimeException("No schedule found with name: " + scheduleName);

			String lastConceptFulfilled = "";
			String checkConcept;

			for (Milestone milestone : schedule.getMilestones()) {
				checkConcept = milestone.getData().get("conceptName");
				if (checkConcept != null) {
					if (openMrsClient.hasConcept(externalID, checkConcept)) {
						System.out.println(lastConceptFulfilled);
						lastConceptFulfilled = checkConcept;
						System.out.println(lastConceptFulfilled);
					}
				}
			}

			final DateTime now = DateUtil.now();

			EnrollmentRequest enrollmentRequest = new EnrollmentRequest().setExternalId(externalID)
					.setScheduleName(scheduleName).setEnrollmentDate(now.toLocalDate())
					.setEnrollmentTime(new Time(now.getHourOfDay(), now.getMinuteOfHour()));;

			if (StringUtils.isNotBlank(lastConceptFulfilled)) { //start at the next milestone
				EnrollmentRecord enrollment = scheduleTrackingService.getEnrollment(externalID, scheduleName);
				if (enrollment != null) { //Enrollment already exists, but now re-enrolling to whatever their latest last milestone fulfillment was, based on OpenMRS
					scheduleTrackingService.unenroll(externalID, Arrays.asList(scheduleName));
				}
				enrollmentRequest.setStartingMilestoneName(schedule.getNextMilestoneName(lastConceptFulfilled));
			}

			scheduleTrackingService.enroll(enrollmentRequest);			
		}		
	}
	
	public void saveMotechPatient(String externalID, String phoneNum) {
		if (StringUtils.isNotBlank(externalID)) {
			Patient patient = patientDataService.findByExternalId(externalID); //Only one patient should be returned if ID is unique, but it is returned as list
			if (patient != null) { //Patient already exists, so it is updated
				patient.setPhoneNum(phoneNum);
				patientDataService.update(patient);
			} else {
				patientDataService.create(new Patient(externalID, phoneNum));
			}
		}
	}
}
