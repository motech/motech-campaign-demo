package org.motechproject.campaigndemo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * Spring controller for displaying the initial demo form page
 *
 * @author Russell Gillen
 */
@Controller
@RequestMapping("/form")
public class FormController {

    @RequestMapping(value = "/cron", method = RequestMethod.GET)
    public ModelAndView cronCampaign() {
        return new ModelAndView("cronFormPage");
    }

    @RequestMapping(value = "/offset", method = RequestMethod.GET)
    public ModelAndView offsetCampaign() {
        return new ModelAndView("formPage");
    }

    @RequestMapping(value = "/patient", method = RequestMethod.GET)
    public ModelAndView openMRSPatients() {
        return new ModelAndView("patientPage");
    }
}
