package org.motechproject.campaigndemo.controllers;

import org.apache.commons.lang.StringUtils;
import org.motechproject.campaigndemo.dao.PatientDataService;
import org.motechproject.campaigndemo.model.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Spring controller for adding and removing users from a patient database using Couch
 * Patients minimally need a phone number and external id in order to make calls from campaign messages
 * @author Russell Gillen
 *
 */
@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private PatientDataService patientDataService;

	@RequestMapping(value = "/addCronUser")
	public ModelAndView addCronUser(HttpServletRequest request) {
		return add("cronFormPage", request);
	}

	@RequestMapping(value = "/removeCronUser")
	public ModelAndView removeCronUser(HttpServletRequest request) {
		return remove("cronFormPage", request);
	}

	@RequestMapping(value = "/addOffsetUser")
	public ModelAndView addOffsetUser(HttpServletRequest request) {
		return add("formPage", request);
	}

	@RequestMapping(value = "/removeOffsetUser")
	public ModelAndView removeOffsetUser(HttpServletRequest request) {
		return remove("formPage", request);
	}

	private ModelAndView add(String returnPage, HttpServletRequest request) {
		String phoneNum = request.getParameter("phoneNum");
		String externalID = request.getParameter("externalId");

		//Don't register empty string IDs
		if (StringUtils.isNotBlank(externalID)) {
			Patient patient = patientDataService.findByExternalId(externalID); //Only one patient should be returned if ID is unique, but it is returned as list
			if (patient != null) { //Patient already exists, so it is updated
				patient.setPhoneNum(phoneNum);
				patientDataService.update(patient);
			} else {
				patientDataService.create(new Patient(externalID, phoneNum));
			}
		}

		List<Patient> patientList = patientDataService.retrieveAll();

		Map<String, Object> modelMap = new TreeMap<>();
		modelMap.put("patients", patientList); //List of patients is for display purposes only

		return new ModelAndView(returnPage, modelMap);
	}

	private ModelAndView remove(String returnPage, HttpServletRequest request) {
		String externalID = request.getParameter("externalId");
		Patient patient = patientDataService.findByExternalId(externalID);

		patientDataService.delete(patient);

		List<Patient> patientList = patientDataService.retrieveAll();

		Map<String, Object> modelMap = new TreeMap<>();
		modelMap.put("patients", patientList); //List of patients is for display purposes only

		return new ModelAndView(returnPage, modelMap);
	}

}
