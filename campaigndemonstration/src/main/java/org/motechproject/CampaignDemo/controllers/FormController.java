package org.motechproject.CampaignDemo.controllers;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

/**
 * Spring controller for displaying the initial demo form page
 * @author Russell Gillen
 */

public class FormController extends MultiActionController {

	
	public ModelAndView test(HttpServletRequest request, HttpServletResponse response) {
		return new ModelAndView("formPage");
	}
	
}