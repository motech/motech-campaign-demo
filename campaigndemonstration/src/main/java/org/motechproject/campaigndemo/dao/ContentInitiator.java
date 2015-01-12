package org.motechproject.campaigndemo.dao;

import org.motechproject.cmslite.model.CMSLiteException;
import org.motechproject.cmslite.model.StreamContent;
import org.motechproject.cmslite.model.StringContent;
import org.motechproject.cmslite.service.CMSLiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Class to initialize CMSlite content in the DB upon server startup
 * Currently, the content is the name of the voice XML file to be run by Voxeo
 * @author Russell Gillen
 *
 */

public class ContentInitiator {

	/**
	 * Defined in the cmslite-api module, available as an OSGi Service
	 */
	@Autowired
	private CMSLiteService cmsLiteService;
	
	@Autowired
	@Qualifier(value = "pregnancyMessages")
	private Properties properties;
	
	public void bootstrap() throws CMSLiteException {
        try {
            for (int i = 5; i <= 40; i++) {
                try (InputStream ghanaMessageStream = this.getClass().getResourceAsStream("/week" + i + ".wav")) {
                    StreamContent ghanaFile = new StreamContent("en", "ghanaPregnancyWeek" + i, "IVR", ghanaMessageStream, "checksum" + i, "audio/wav");
                    try {
                        cmsLiteService.addContent(ghanaFile);
                    } catch (CMSLiteException e) {

                    }
                    cmsLiteService.addContent(new StringContent("en", "pregnancy-info-week-" + i, "IVR", "english/week" + i + ".xml"));
                    cmsLiteService.addContent(new StringContent("en", "pregnancy-info-week-" + i, "SMS", getPregnancyMessage(i)));
                }
            }

            InputStream inputStreamToResource1 = this.getClass().getResourceAsStream("/cronmessage.wav");
            StreamContent cron = new StreamContent("en", "test", "IVR", inputStreamToResource1, "checksum1", "audio/wav");
            cmsLiteService.addContent(cron);
            cmsLiteService.addContent(new StringContent("en", "cron-message", "IVR", "english/cron.xml"));
            cmsLiteService.addContent(new StringContent("en", "cron-message", "SMS", "This is an SMS cron message that will repeat every two minutes until you unenroll"));
        } catch (IOException e) {

        }
	}

	private String getPregnancyMessage(int messageNumber) {
	        return this.properties.getProperty("messageWeek" + messageNumber);
	}
	
}
