package org.motechproject.campaigndemo.controllers;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.campaigndemo.dao.PatientDataService;
import org.motechproject.messagecampaign.service.MessageCampaignService;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RunWith(MockitoJUnitRunner.class)
public class CampaignControllerTest {
    @InjectMocks
    private CampaignController campaignController = new CampaignController();

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;
    
    @Mock
    private MessageCampaignService service;
    
    @Mock
    private PatientDataService patientDataService;

    @Test
    public void testStartCampaign () {

    	String requestId = "12345";
    	String campaignName = "12345";
    	
    	Mockito.when(request.getParameter("externalId")).thenReturn(requestId);
    	Mockito.when(request.getParameter("campaignName")).thenReturn(campaignName);
    	
    	
    	ModelAndView modelAndView = campaignController.start(request, response);
    	
    	Assert.assertEquals("formPage", modelAndView.getViewName());
    }
    
    @Test
    public void testStopCampaign() {

    	String requestId = "12345";
    	String campaignName = "12345";
    	
    	Mockito.when(request.getParameter("externalId")).thenReturn(requestId);
    	Mockito.when(request.getParameter("campaignName")).thenReturn(campaignName);
    	
    	ModelAndView modelAndView = campaignController.stop(request);
    	
    	Assert.assertEquals("formPage", modelAndView.getViewName());
    }
    
    
    
}