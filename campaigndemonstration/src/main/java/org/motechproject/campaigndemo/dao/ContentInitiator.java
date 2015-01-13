package org.motechproject.campaigndemo.dao;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.motechproject.cmslite.model.CMSLiteException;
import org.motechproject.cmslite.model.StreamContent;
import org.motechproject.cmslite.model.StringContent;
import org.motechproject.cmslite.service.CMSLiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Class to initialize CMSlite content in the DB upon server startup
 * Currently, the content is the name of the voice XML file to be run by Voxeo
 * @author Russell Gillen
 *
 */
@Component
public class ContentInitiator {

    public static final String COMM_TYPE = "commType";

	/**
	 * Defined in the cms-lite module, available as an OSGi Service
	 */
	@Autowired
	private CMSLiteService cmsLiteService;
	
	@Autowired
	@Qualifier(value = "pregnancyMessages")
	private Properties properties;
	
	public void bootstrap() throws IOException, CMSLiteException {
        for (int i = 5; i <= 40; i++) {
            try (InputStream ghanaMessageStream = this.getClass().getResourceAsStream("/week" + i + ".wav")) {
                StreamContent ghanaFile = new StreamContent("en", "ghanaPregnancyWeek" + i, toByteArray(ghanaMessageStream),
                        "checksum" + i, "audio/wav");
                cmsLiteService.addContent(ghanaFile);
                cmsLiteService.addContent(new StringContent("en", "pregnancy-info-week-" + i, "english/week" + i + ".xml", ivrMetadata()));
                cmsLiteService.addContent(new StringContent("en", "pregnancy-info-week-" + i, getPregnancyMessage(i), smsMetadata()));
            }
        }

        try (InputStream inputStreamToResource1 = this.getClass().getResourceAsStream("/cronmessage.wav")) {
            StreamContent cron = new StreamContent("en", "test", toByteArray(inputStreamToResource1), "checksum1", "audio/wav");
            cmsLiteService.addContent(cron);
            cmsLiteService.addContent(new StringContent("en", "cron-message", "english/cron.xml", ivrMetadata()));
            cmsLiteService.addContent(new StringContent("en", "cron-message",
                    "This is an SMS cron message that will repeat every two minutes until you unenroll", smsMetadata()));
        }
	}

	private String getPregnancyMessage(int messageNumber) {
	        return this.properties.getProperty("messageWeek" + messageNumber);
	}

    private Map<String, String> ivrMetadata() {
        return commTypeMetadata("IVR");
    }

    private Map<String, String> smsMetadata() {
        return commTypeMetadata("SMS");
    }

    private Map<String, String> commTypeMetadata(String value) {
        Map<String, String> metadata = new HashMap<>();
        metadata.put(COMM_TYPE, value);
        return metadata;
    }

    private Byte[] toByteArray(InputStream stream) throws IOException {
        return ArrayUtils.toObject(IOUtils.toByteArray(stream));
    }
}
