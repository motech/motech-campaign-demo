package org.motechproject.campaigndemo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;

/**
 * Spring controller for displaying the initial demo form page
 * @author Russell Gillen
 */
@Controller
public class FormController {

	public ModelAndView cronCampaign() {
		return new ModelAndView("cronFormPage");
	}
	
	public ModelAndView offsetCampaign() {
		return new ModelAndView("formPage");
	}
	
	public ModelAndView openMRSPatients() {
		return new ModelAndView("patientPage");
	}
}
