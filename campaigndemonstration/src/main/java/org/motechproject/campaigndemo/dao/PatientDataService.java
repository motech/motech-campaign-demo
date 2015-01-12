package org.motechproject.campaigndemo.dao;

import org.motechproject.campaigndemo.model.Patient;
import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.annotations.LookupField;
import org.motechproject.mds.service.MotechDataService;

/**
 * The MDS data service for the {@link org.motechproject.campaigndemo.model.Patient} domain
 * class.
 */
public interface PatientDataService extends MotechDataService<Patient> {

    @Lookup
    Patient findByExternalId(@LookupField(name = "externalId") String externalId);
}
