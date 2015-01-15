package org.motechproject.scheduletrackingdemo.listeners;


import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.MotechListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CallResultListener {

	private static Logger logger = LoggerFactory.getLogger(CallResultListener.class);

	@MotechListener(subjects = {"ivr_call_initiated"})
	public void execute(MotechEvent event) {
		logger.debug("Handled call event");
        // Might not be necessary, we have IVR logs now


		}
	}


