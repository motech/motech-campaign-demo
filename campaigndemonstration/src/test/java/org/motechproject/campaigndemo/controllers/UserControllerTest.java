package org.motechproject.campaigndemo.controllers;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.campaigndemo.dao.PatientDataService;
import org.motechproject.campaigndemo.model.Patient;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class UserControllerTest {

	@InjectMocks
	private UserController userController = new UserController();

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;
    
    @Mock
    private PatientDataService patientDataService;

    @Test
    public void testAddUser () {

    	String requestId = "12345";
    	String phoneNum = "207";
    	
    	Mockito.when(request.getParameter("externalId")).thenReturn(requestId);
    	Mockito.when(request.getParameter("phoneNum")).thenReturn(phoneNum);
    	
    	ModelAndView modelAndView = userController.addCronUser(request);
    	
    	Assert.assertEquals("cronFormPage", modelAndView.getViewName());
    	verify(patientDataService).findByExternalId(Matchers.anyString());
    	verify(patientDataService, Mockito.atMost(1)).create(Matchers.any(Patient.class));
    	verify(patientDataService, Mockito.atMost(1)).update(Matchers.any(Patient.class));

    }
    
    @Test
    public void testRemoveUser() {

    	String requestId = "12345";
    	
    	Mockito.when(request.getParameter("externalId")).thenReturn(requestId);

    	ModelAndView modelAndView = userController.removeCronUser(request);
    	
    	Assert.assertEquals("cronFormPage", modelAndView.getViewName());
    	verify(patientDataService).delete(any(Patient.class));

    }
    
    
    
}