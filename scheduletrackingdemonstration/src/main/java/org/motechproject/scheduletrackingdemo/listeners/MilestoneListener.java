package org.motechproject.scheduletrackingdemo.listeners;

import org.motechproject.cmslite.model.ContentNotFoundException;
import org.motechproject.cmslite.model.StringContent;
import org.motechproject.cmslite.service.CMSLiteService;
import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.ivr.service.OutboundCallService;
import org.motechproject.scheduletracking.events.MilestoneEvent;
import org.motechproject.scheduletracking.events.constants.EventSubjects;
import org.motechproject.scheduletracking.service.ScheduleTrackingService;
import org.motechproject.scheduletrackingdemo.dao.PatientDataService;
import org.motechproject.scheduletrackingdemo.model.Patient;
import org.motechproject.scheduletrackingdemo.openmrs.OpenMrsClient;
import org.motechproject.sms.service.OutgoingSms;
import org.motechproject.sms.service.SmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class MilestoneListener {

	private static final Logger LOG = LoggerFactory.getLogger(MilestoneListener.class);
	private static final String VOXEO_CONFIG = "voxeo";

	@Autowired
	private OutboundCallService outboundCallService;

	@Autowired
	private OpenMrsClient openmrsClient;

	@Autowired
	private ScheduleTrackingService scheduleTrackingService;

	@Autowired
	private PatientDataService patientDataService;

	@Autowired
	private CMSLiteService cmsliteService;
	
	@Autowired
	private SmsService smsService;

	@MotechListener(subjects = { EventSubjects.MILESTONE_ALERT })
	public void execute(MotechEvent event) {
		LOG.debug("Handled milestone event");

		MilestoneEvent mEvent = new MilestoneEvent(event);

		LOG.info("For: " + mEvent.getExternalId() + " --- " + mEvent.getWindowName() + " --- "
				+ mEvent.getScheduleName() + " --- " + mEvent.getWindowName());

		String milestoneConceptName = mEvent.getMilestoneData().get("conceptName");

		if (milestoneConceptName == null) {
			return; //This method does not handle events without conceptName
		}
		
		boolean hasFulfilledMilestone = openmrsClient.hasConcept(mEvent.getExternalId(), milestoneConceptName);

		if (hasFulfilledMilestone && mEvent.getReferenceDateTime().minusDays(1).isBefore(openmrsClient.lastTimeFulfilledDateTimeObs(mEvent.getExternalId(), milestoneConceptName))) {
			LOG.debug("Fulfilling milestone for: " + mEvent.getExternalId()
					+ " with schedule: " + mEvent.getScheduleName());
			
			scheduleTrackingService.fulfillCurrentMilestone(mEvent.getExternalId(), mEvent.getScheduleName(), DateUtil.today());
		} else if (!mEvent.getWindowName().equals("max")){ //Place calls and/or text messages, but not for the max alerts
			
			Patient patient = patientDataService.findByExternalId(mEvent.getExternalId());
			
			if (patient != null) {
				
				String IVRFormat = mEvent.getMilestoneData().get("IVRFormat");
				String SMSFormat = mEvent.getMilestoneData().get("SMSFormat");
				String language = mEvent.getMilestoneData().get("language");
				String messageName = mEvent.getMilestoneData().get("messageName");


                if ("true".equals(SMSFormat) && language != null && messageName != null) {
                    sendSMS(patient, language, messageName, mEvent.getWindowName());
                }
				if ("true".equals(IVRFormat) && language != null && messageName != null) {
					placeCall(patient, language, messageName, mEvent.getWindowName());
				}
			}
		}
	}

	@MotechListener(subjects = { EventSubjects.MILESTONE_DEFAULTED })
	public void defaulted(MotechEvent event) {
		MilestoneEvent mEvent = new MilestoneEvent(event);
		Patient patient = patientDataService.findByExternalId(mEvent.getExternalId());
		
		if (patient != null) {
			placeCall(patient, "en", "defaulted-demo-message", "");
			sendSMS(patient, "en", "defaulted-demo-message", "");
			
		}
		LOG.debug("Handled milestone event"); //Currently do nothing with defaultment event
	}

	private void placeCall(Patient patient, String language, String messageName, String windowName) {
		if (cmsliteService.isStringContentAvailable(language, messageName + windowName)) {
			StringContent content = null;
			try {
				content = cmsliteService.getStringContent(language, messageName + windowName);
			} catch (ContentNotFoundException e) {
				LOG.error("Unable to retrieve string content", e);
			}
			if (content != null) {
				LOG.info("Calling");

				Map<String, String> request = new HashMap<>();
				request.put("USER_ID", patient.getExternalId()); //put Id in the payload

				outboundCallService.initiateCall(VOXEO_CONFIG, request);
			}
		} else {
			LOG.error("No IVR content available");
		}
	}

	private void sendSMS(Patient patient, String language, String messageName, String windowName) {
		if (cmsliteService.isStringContentAvailable(language, messageName + windowName)) {
			try {
				StringContent content = cmsliteService.getStringContent(language, messageName + windowName);
				smsService.send(new OutgoingSms(patient.getPhoneNum(), content.getValue()));
			} catch (ContentNotFoundException e) {
				LOG.error("Unable to find content", e);
			}
		} else { //no content, don't send SMS
			LOG.error("No SMS content available");
		}

	}

}
