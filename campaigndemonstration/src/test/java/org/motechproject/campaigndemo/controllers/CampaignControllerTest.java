package org.motechproject.campaigndemo.controllers;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.campaigndemo.dao.PatientDataService;
import org.motechproject.messagecampaign.contract.CampaignRequest;
import org.motechproject.messagecampaign.service.CampaignEnrollmentsQuery;
import org.motechproject.messagecampaign.service.MessageCampaignService;
import org.springframework.web.servlet.ModelAndView;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class CampaignControllerTest {

    @InjectMocks
    private CampaignController campaignController = new CampaignController();

    @Mock
    private MessageCampaignService service;

    @Mock
    private PatientDataService patientDataService;

    @Test
    public void testStartCampaign () {
    	ModelAndView modelAndView = campaignController.start("externalId", "campaignName");
    	assertEquals("formPage", modelAndView.getViewName());
        verify(service).enroll(any(CampaignRequest.class));
    }
    
    @Test
    public void testStopCampaign() {
    	ModelAndView modelAndView = campaignController.stop("externalId", "campaignName");
    	assertEquals("formPage", modelAndView.getViewName());
        verify(service).stopAll(any(CampaignEnrollmentsQuery.class));
    }
    
    
    
}