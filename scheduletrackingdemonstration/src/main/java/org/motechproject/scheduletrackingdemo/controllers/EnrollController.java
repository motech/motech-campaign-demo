package org.motechproject.scheduletrackingdemo.controllers;

import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.scheduletracking.domain.exception.InvalidEnrollmentException;
import org.motechproject.scheduletracking.service.EnrollmentService;
import org.motechproject.scheduletracking.service.ScheduleTrackingService;
import org.motechproject.scheduletrackingdemo.openmrs.OpenMrsClient;
import org.motechproject.scheduletrackingdemo.PatientScheduler;
import org.motechproject.scheduletrackingdemo.dao.PatientDataService;
import org.motechproject.scheduletrackingdemo.model.Patient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Controller
public class EnrollController {

	private static final Logger LOG = LoggerFactory.getLogger(EnrollController.class);

	@Autowired
	private PatientDataService patientDataService;

	@Autowired
	private ScheduleTrackingService scheduleTrackingService;

	@Autowired
	private EnrollmentService enrollmentService;

	@Autowired
	private OpenMrsClient openMrsClient;

	@Autowired
	private PatientScheduler patientSchedule;

	public ModelAndView start(HttpServletRequest request) {

		String externalID = request.getParameter("externalID");
		String scheduleName = request.getParameter("scheduleName");

		patientSchedule.enrollIntoSchedule(externalID, scheduleName);
		
		List<Patient> patientList = patientDataService.retrieveAll();
		
		Map<String, Object> modelMap = new TreeMap<>();
		modelMap.put("patients", patientList); //List of patients is for display purposes only

		return new ModelAndView("scheduleTrackingPage", modelMap);
	}

	public ModelAndView stop(HttpServletRequest request) {

		String externalID = request.getParameter("externalID");
		String scheduleName = request.getParameter("scheduleName");

		try {
			scheduleTrackingService.unenroll(externalID, Arrays.asList(scheduleName));
		} catch (InvalidEnrollmentException e) {
			LOG.warn("Could not unenroll externalId=" + externalID + ", scheduleName=" + scheduleName);
		}

		List<Patient> patientList = patientDataService.retrieveAll();
		
		Map<String, Object> modelMap = new TreeMap<>();
		modelMap.put("patients", patientList); //List of patients is for display purposes only

		return new ModelAndView("scheduleTrackingPage", modelMap);
	}

	public ModelAndView fulfill(HttpServletRequest request) {

		String externalID = request.getParameter("externalID");
		String scheduleName = request.getParameter("scheduleName");

		scheduleTrackingService.fulfillCurrentMilestone(externalID, scheduleName, DateUtil.today());

		List<Patient> patientList = patientDataService.retrieveAll();
		
		Map<String, Object> modelMap = new TreeMap<>();
		modelMap.put("patients", patientList); //List of patients is for display purposes only

		return new ModelAndView("scheduleTrackingPage", modelMap);

	}

	public ModelAndView obs(HttpServletRequest request) {
		String externalID = request.getParameter("externalID");
		String conceptName = request.getParameter("conceptName");

		openMrsClient.printValues(externalID, conceptName);

		openMrsClient.lastTimeFulfilledDateTimeObs(externalID, conceptName);

		List<Patient> patientList = patientDataService.retrieveAll();
		
		Map<String, Object> modelMap = new TreeMap<>();
		modelMap.put("patients", patientList); //List of patients is for display purposes only

		return new ModelAndView("scheduleTrackingPage", modelMap);
	}

	public ModelAndView scheduleTracking() {
		
		List<Patient> patientList = patientDataService.retrieveAll();
		
		Map<String, Object> modelMap = new TreeMap<>();
		modelMap.put("patients", patientList); //List of patients is for display purposes only

		return new ModelAndView("scheduleTrackingPage", modelMap);
	}
}




