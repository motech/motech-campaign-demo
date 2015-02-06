package org.motechproject.scheduletrackingdemo.controllers;

import org.motechproject.scheduletrackingdemo.PatientScheduler;
import org.motechproject.scheduletrackingdemo.dao.PatientDataService;
import org.motechproject.scheduletrackingdemo.model.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Spring controller for adding and removing users from a patient database using MDS
 * Patients minimally need a phone number and external id in order to make calls from campaign messages
 *
 * @author Russell Gillen
 */
@Controller
@RequestMapping("/patient")
public class PatientController {

    @Autowired
    private PatientDataService patientDataService;

    @Autowired
    private PatientScheduler patientScheduler;

    @RequestMapping(value = "/addScheduleUser", method = RequestMethod.POST)
    public ModelAndView addScheduleUser(HttpServletRequest request, HttpServletResponse response) {
        return add("scheduleTrackingPage", request);
    }

    @RequestMapping(value = "/removeScheduleUser", method = RequestMethod.POST)
    public ModelAndView removeScheduleUser(HttpServletRequest request, HttpServletResponse response) {
        return remove("scheduleTrackingPage", request);
    }

    private ModelAndView add(String returnPage, HttpServletRequest request) {
        String phoneNum = request.getParameter("phoneNum");
        String externalID = request.getParameter("externalId");

        patientScheduler.saveMotechPatient(externalID, phoneNum);

        List<Patient> patientList = patientDataService.retrieveAll();

        Map<String, Object> modelMap = new TreeMap<String, Object>();
        modelMap.put("patients", patientList); //List of patients is for display purposes only

        return new ModelAndView(returnPage, modelMap);
    }

    private ModelAndView remove(String returnPage, HttpServletRequest request) {
        String externalID = request.getParameter("externalId");

        Patient patient = patientDataService.findByExternalId(externalID);
        if (patient != null) {
            patientDataService.delete(patient);
        }

        List<Patient> patientList = patientDataService.retrieveAll();

        Map<String, Object> modelMap = new TreeMap<>();
        modelMap.put("patients", patientList); //List of patients is for display purposes only

        return new ModelAndView(returnPage, modelMap);
    }
}
