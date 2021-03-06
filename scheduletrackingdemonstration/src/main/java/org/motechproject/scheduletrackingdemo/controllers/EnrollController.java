package org.motechproject.scheduletrackingdemo.controllers;

import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.scheduletracking.service.ScheduleTrackingService;
import org.motechproject.scheduletrackingdemo.PatientScheduler;
import org.motechproject.scheduletrackingdemo.dao.PatientDataService;
import org.motechproject.scheduletrackingdemo.model.Patient;
import org.motechproject.scheduletrackingdemo.openmrs.OpenMrsClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Controller
@RequestMapping("/enroll")
public class EnrollController {

    private static final Logger LOG = LoggerFactory.getLogger(EnrollController.class);

    @Autowired
    private PatientDataService patientDataService;

    @Autowired
    private ScheduleTrackingService scheduleTrackingService;

    @Autowired
    private OpenMrsClient openMrsClient;

    @Autowired
    private PatientScheduler patientSchedule;

    @RequestMapping(value = "/start", method = RequestMethod.POST)
    public ModelAndView start(HttpServletRequest request) {

        String externalID = request.getParameter("externalID");
        String scheduleName = request.getParameter("scheduleName");

        patientSchedule.enrollIntoSchedule(externalID, scheduleName);

        List<Patient> patientList = patientDataService.retrieveAll();

        Map<String, Object> modelMap = new TreeMap<>();
        modelMap.put("patients", patientList); //List of patients is for display purposes only

        return new ModelAndView("scheduleTrackingPage", modelMap);
    }

    @RequestMapping(value = "/stop", method = RequestMethod.POST)
    public ModelAndView stop(HttpServletRequest request) {

        String externalID = request.getParameter("externalID");
        String scheduleName = request.getParameter("scheduleName");

        scheduleTrackingService.unenroll(externalID, Arrays.asList(scheduleName));

        List<Patient> patientList = patientDataService.retrieveAll();

        Map<String, Object> modelMap = new TreeMap<>();
        modelMap.put("patients", patientList); //List of patients is for display purposes only

        return new ModelAndView("scheduleTrackingPage", modelMap);
    }

    @RequestMapping(value = "/fulfill", method = RequestMethod.GET)
    public ModelAndView fulfill(HttpServletRequest request) {

        String externalID = request.getParameter("externalID");
        String scheduleName = request.getParameter("scheduleName");

        scheduleTrackingService.fulfillCurrentMilestone(externalID, scheduleName, DateUtil.today());

        List<Patient> patientList = patientDataService.retrieveAll();

        Map<String, Object> modelMap = new TreeMap<>();
        modelMap.put("patients", patientList); //List of patients is for display purposes only

        return new ModelAndView("scheduleTrackingPage", modelMap);

    }

    @RequestMapping(value = "/obs", method = RequestMethod.GET)
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

    @RequestMapping(value = "/scheduleTracking", method = RequestMethod.GET)
    public ModelAndView scheduleTracking() {

        List<Patient> patientList = patientDataService.retrieveAll();

        Map<String, Object> modelMap = new TreeMap<>();
        modelMap.put("patients", patientList); // List of patients is for display purposes only

        return new ModelAndView("scheduleTrackingPage", modelMap);
    }
}




