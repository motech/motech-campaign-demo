package org.motechproject.campaigndemo.model;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechBaseDataObject;

/**
 * Patients associate an external id with a phone number
 * Classes stored in CouchDB should be marked as Json serializable 
 * @author Russell Gillen
 */

@TypeDiscriminator("doc.type === 'PATIENT'")
public class Patient extends MotechBaseDataObject {
	
	@JsonProperty("type") 
	private final String type = "PATIENT";
    
	@JsonProperty
	private String externalId;
    @JsonProperty
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
		Patient patient = (Patient) o;
		if (patient.getExternalId().equals(externalId)) return true;
		return false;
	}
}
