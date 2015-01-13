package org.motechproject.scheduletrackingdemo.model;

import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;

/**
 * Patients associate an external id with a phone number
 * This is an MDS entity.
 * @author Russell Gillen
 */
@Entity
public class Patient {
	
	@Field
	private String externalId;

	@Field
	private String phoneNum;

    public Patient() {
    }
    
    public Patient(String externalId) {
    	this.externalId = externalId;
    }
    
	public Patient(String externalId, String phoneNum) {
		this.externalId = externalId;
		this.phoneNum = phoneNum;
	}
	
	public String getPhoneNum() {
		return phoneNum;
	}
	
	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}
	
	public String getExternalId() {
		return externalId;
	}
	
	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}
	
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (!(o instanceof Patient)) {
			return false;
		}

		Patient other = (Patient) o;

		return other.getExternalId().equals(externalId);
	}
}
