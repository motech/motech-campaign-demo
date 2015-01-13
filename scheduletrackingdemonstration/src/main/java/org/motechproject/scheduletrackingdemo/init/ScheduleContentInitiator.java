package org.motechproject.scheduletrackingdemo.init;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.motechproject.cmslite.model.CMSLiteException;
import org.motechproject.cmslite.model.StreamContent;
import org.motechproject.cmslite.model.StringContent;
import org.motechproject.cmslite.service.CMSLiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Class to initialize CMSlite content in the DB upon server startup
 * Currently, the content is the name of the voice XML file to be run by Voxeo
 * @author Russell Gillen
 *
 */
@Component
public class ScheduleContentInitiator {

	/**
	 * Defined in the motech-cmslite-api module, available from applicationCmsLiteApi.xml import
	 */
	@Autowired
	private CMSLiteService cmsliteService;
	
	@Autowired
	@Qualifier(value = "scheduleMessages")
	private Properties properties;

    @PostConstruct
	public void bootstrap() throws CMSLiteException, IOException {
        for (int i = 1; i <= 4; i++) {
            try (InputStream demoMessageStream = this.getClass().getResourceAsStream("/duedemoconcept" + i + ".wav")) {
                StreamContent demoFile = new StreamContent("en", "DemoConceptQuestion" + i + "Due",
                        toByteArray(demoMessageStream), "checksum" + i, "audio/wav");
                cmsliteService.addContent(demoFile);
            }
            try (InputStream demoMessageStream2 = this.getClass().getResourceAsStream("/latedemoconcept" + i + ".wav")) {
                StreamContent demoFile2 = new StreamContent("en", "DemoConceptQuestion" + i + "Late",
                        toByteArray(demoMessageStream2), "checksum" + i, "audio/wav");
                cmsliteService.addContent(demoFile2);
            }
            cmsliteService.addContent(new StringContent("en", "DemoConceptQuestion" + i + "due", "english/due" + i + ".xml"));
            cmsliteService.addContent(new StringContent("en", "DemoConceptQuestion" + i + "due", getDemoDueMessage(i)));
            cmsliteService.addContent(new StringContent("en", "DemoConceptQuestion" + i + "late", "english/late" + i + ".xml"));
            cmsliteService.addContent(new StringContent("en", "DemoConceptQuestion" + i + "late", getDemoLateMessage(i)));
        
        }
		
        try (InputStream cronResourceStream = this.getClass().getResourceAsStream("/defaulteddemoschedule.wav")) {
            StreamContent cron = new StreamContent("en", "defaultedDemoSchedule",
                    toByteArray(cronResourceStream), "checksum1", "audio/wav");
            cmsliteService.addContent(cron);
        }

        cmsliteService.addContent(new StringContent("en", "defaulted-demo-message", "english/defaulted.xml"));
        cmsliteService.addContent(new StringContent("en", "defaulted-demo-message", "You have defaulted on your Demo Concept Schedule. Please visit your doctor for more information."));
        
	}

	private String getDemoDueMessage(int messageNumber) {
	        return this.properties.getProperty("DemoConceptQuestion" + messageNumber + "Due");
	}
	
	private String getDemoLateMessage(int messageNumber) {
		return this.properties.getProperty("DemoConceptQuestion" + messageNumber + "Late");
	}

    private Byte[] toByteArray(InputStream stream) throws IOException {
        return ArrayUtils.toObject(IOUtils.toByteArray(stream));
    }
}
