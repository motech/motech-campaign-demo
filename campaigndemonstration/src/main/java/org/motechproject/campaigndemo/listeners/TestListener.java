package org.motechproject.campaigndemo.listeners;


import org.motechproject.campaigndemo.dao.PatientDataService;
import org.motechproject.campaigndemo.model.Patient;
import org.motechproject.cmslite.model.ContentNotFoundException;
import org.motechproject.cmslite.model.StringContent;
import org.motechproject.cmslite.service.CMSLiteService;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.ivr.service.CallInitiationException;
import org.motechproject.ivr.service.OutboundCallService;
import org.motechproject.messagecampaign.EventKeys;
import org.motechproject.messagecampaign.service.CampaignEnrollmentsQuery;
import org.motechproject.messagecampaign.service.MessageCampaignService;
import org.motechproject.sms.service.OutgoingSms;
import org.motechproject.sms.service.SmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A listener class used to listen on fired campaign message events.
 * This class demonstrates how to listen in on events and taking action based
 * upon their payload. Payloads are stored as a String-Object mapping pair, where the String
 * is found in an appropriate EventKey class and the Object is the relevant data or information
 * associated with the key. The payload information should be known ahead of time.
 * <p/>
 * AllMessageCampaigns accesses the simple-message-campaign.json file found
 * in the resource package in the demo. The json file defines the characteristics
 * of a campaign.
 *
 * @author Russell Gillen
 */
@Component
public class TestListener {

    private static final String SMS_FORMAT = "SMS";
    private static final String IVR_FORMAT = "IVR";
    private static final String VOXEO_CONFIG = "voxeo";

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * Defined in the cms-lite module, available as an OSGi service
     */
    @Autowired
    private CMSLiteService cmsliteService;

    /**
     * Defined in campaignDemoResource.xml
     */
    @Autowired
    private PatientDataService patientDataService;

    /**
     * Defined in the IVR module, available as an OSGi service
     */
    @Autowired
    private OutboundCallService outboundCallService;

    /**
     * Defined in the message-campaign module, available as an OSGi service
     */
    @Autowired
    private MessageCampaignService service;

    @Autowired
    private SmsService smsService;

    /**
     * Methods are registered as listeners on specific MOTECH events. All MOTECH events
     * have an associated subject, which is found in an appropriate EventKeys class.
     * When an event with that particular subject is relayed, this method will be invoked.
     * The payload parameters, in this case, campaign name, message key and external id, must be known
     * ahead of time.
     *
     * @param event The Motech event relayed by the EventRelay
     * @throws ContentNotFoundException
     */
    @MotechListener(subjects = {EventKeys.SEND_MESSAGE})
    public void execute(MotechEvent event) throws ContentNotFoundException {

        String campaignName = (String) event.getParameters().get(EventKeys.CAMPAIGN_NAME_KEY);
        String messageKey = (String) event.getParameters().get(EventKeys.MESSAGE_KEY);
        String externalId = (String) event.getParameters().get(EventKeys.EXTERNAL_ID_KEY);
        List<String> languages = ((List<String>) event.getParameters().get(EventKeys.MESSAGE_LANGUAGES));
        String language = languages.get(0);
        List<String> formats = ((List<String>) event.getParameters().get(EventKeys.MESSAGE_FORMATS));

        Patient patient = patientDataService.findByExternalId(externalId);

        if (patient == null) { //In the event no patient was found, the campaign is unscheduled
            CampaignEnrollmentsQuery toRemove = new CampaignEnrollmentsQuery()
                    .withCampaignName(campaignName)
                    .withExternalId(externalId);

            service.stopAll(toRemove); //See CampaignController for documentation on MessageCampaignService calls
        } else {

            String phoneNum = patient.getPhoneNum();

            if (formats.contains(IVR_FORMAT)) { //place IVR call
                if (cmsliteService.isStringContentAvailable(language, messageKey)) {
                    StringContent content = cmsliteService.getStringContent(language, messageKey);

                    /**
                     * Call requests are used to place IVR calls. They contain a phone number,
                     * timeout duration, and vxml URL for content.
                     */
                    Map<String, String> request = new HashMap<>();

                    request.put("call_timeout", "119");
                    request.put("vxml", content.getValue());
                    request.put("phonenum", phoneNum);

                    request.put("USER_ID", patient.getExternalId()); //put Id in the payload

                    /**
                     * The Voxeo module sends a request to the Voxeo website, at which point
                     * control is passed to the ccxml file. The ccxml file will play
                     * the vxmlUrl defined in the CallRequest. The vxmlUrl contains the content
                     * of the voice message. The Voxeo website informs
                     * the motech voxeo module of transition state changes in the phone call.
                     */
                    try {
                        outboundCallService.initiateCall(VOXEO_CONFIG, request);
                    } catch (CallInitiationException e) {
                        log.error("Unable to place the call", e);
                    }
                } else {
                    log.error("No content available");
                }
            }
            if (formats.contains(SMS_FORMAT)) { //send SMS message
                if (cmsliteService.isStringContentAvailable(language, messageKey)) {
                    StringContent content = cmsliteService.getStringContent(language, messageKey);

                    OutgoingSms sms = new OutgoingSms(Arrays.asList(phoneNum), content.getValue());

                    smsService.send(sms);

                } else { //no content, don't send SMS
                    log.error("No content available");
                }
            }
        }
    }
}

