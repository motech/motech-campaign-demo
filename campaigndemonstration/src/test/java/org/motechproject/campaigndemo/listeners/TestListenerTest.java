package org.motechproject.campaigndemo.listeners;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.campaigndemo.dao.PatientDataService;
import org.motechproject.campaigndemo.model.Patient;
import org.motechproject.cmslite.model.ContentNotFoundException;
import org.motechproject.cmslite.model.StringContent;
import org.motechproject.cmslite.service.CMSLiteService;
import org.motechproject.event.MotechEvent;
import org.motechproject.ivr.service.OutboundCallService;
import org.motechproject.messagecampaign.EventKeys;
import org.motechproject.messagecampaign.service.CampaignEnrollmentsQuery;
import org.motechproject.messagecampaign.service.MessageCampaignService;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.anyMap;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class TestListenerTest {

	@InjectMocks
	private TestListener listener = new TestListener();
	
	@Mock
	private PatientDataService patientDataService;
	
	@Mock
	private OutboundCallService outboundCallService;
	
	@Mock
	private MessageCampaignService campaignService;

	@Mock
	private CMSLiteService cmsliteService;
	
	@Test
	public void testWhenPatientExists() throws ContentNotFoundException {
		
		List<String> formats = new ArrayList<String>();
		formats.add("IVR");
		
		List<String> languages = new ArrayList<String>();
		languages.add("en");
		
		MotechEvent event = new MotechEvent(EventKeys.SEND_MESSAGE);
		event.getParameters().put(EventKeys.CAMPAIGN_NAME_KEY, "TestCampaign");
		event.getParameters().put(EventKeys.MESSAGE_KEY, "TestCampaignKey");
		event.getParameters().put(EventKeys.EXTERNAL_ID_KEY, "12345");
		event.getParameters().put(EventKeys.MESSAGE_FORMATS, formats);
		event.getParameters().put(EventKeys.MESSAGE_LANGUAGES, languages);

		Patient testPatient = new Patient("12345", "207");

		Mockito.when(patientDataService.findByExternalId("12345")).thenReturn(testPatient);
		Mockito.when(cmsliteService.getStringContent("en", "TestCampaignKey")).thenReturn(
				new StringContent("en", "cron-message", "demo.xml"));
		Mockito.when(cmsliteService.isStringContentAvailable("en", "TestCampaignKey")).thenReturn(true);
		listener.execute(event);
		
		verify(outboundCallService).initiateCall("voxeo", anyMap());

	}
	
	@Test
	public void testWhenPatientDoesNotExist() throws ContentNotFoundException {
		
		List<String> formats = new ArrayList<>();
		formats.add("IVR");
		
		List<String> languages = new ArrayList<>();
		languages.add("en");
		
		MotechEvent event = new MotechEvent(EventKeys.SEND_MESSAGE);
		event.getParameters().put(EventKeys.CAMPAIGN_NAME_KEY, "TestCampaign");
		event.getParameters().put(EventKeys.MESSAGE_KEY, "TestCampaignKey");
		event.getParameters().put(EventKeys.EXTERNAL_ID_KEY, "12345");
		event.getParameters().put(EventKeys.MESSAGE_FORMATS, formats);
		event.getParameters().put(EventKeys.MESSAGE_LANGUAGES, languages);
		
		Mockito.when(patientDataService.findByExternalId("12345")).thenReturn(null);
		Mockito.when(cmsliteService.getStringContent("en", "TestCampaignKey")).thenReturn(new StringContent("en", "cron-message", "demo.xml"));
		Mockito.when(cmsliteService.getStringContent("en", "TestCampaignKey")).thenReturn(new StringContent("en", "cron-message", "demo.xml"));
		
		listener.execute(event);
		
		verify(campaignService).stopAll(Matchers.any(CampaignEnrollmentsQuery.class));
	}
	
}
