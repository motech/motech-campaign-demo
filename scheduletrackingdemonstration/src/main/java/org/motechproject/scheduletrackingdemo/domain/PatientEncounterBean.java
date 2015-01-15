package org.motechproject.scheduletrackingdemo.domain;

import java.util.Date;

public class PatientEncounterBean {

	private String motechId;
	private Date observedDate;
	private int observedConcept;
	
	public String getMotechId() {
		return motechId;
	}
	
	public void setMotechId(String motechId) {
		this.motechId = motechId;
	}
	
	public Date getObservedDate() {
		return observedDate;
	}
	
	public void setObservedDate(Date observedDate) {
		this.observedDate = observedDate;
	}
	
	public int getObservedConcept() {
		return observedConcept;
	}
	
	public void setObservedConcept(int observedConcept) {
		this.observedConcept = observedConcept;
	}
}
