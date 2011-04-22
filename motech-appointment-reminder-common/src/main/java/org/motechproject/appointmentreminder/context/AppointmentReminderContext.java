package org.motechproject.appointmentreminder.context;

import org.motechproject.appointmentreminder.dao.PatientDAO;
import org.springframework.beans.factory.annotation.Autowired;


public class AppointmentReminderContext
{
    @Autowired
    private PatientDAO patientDAO;

    public PatientDAO getPatientDAO()
    {
        return patientDAO;
    }

    public void setPatientDAO(PatientDAO patientDAO)
    {
        this.patientDAO = patientDAO;
    }

    public static AppointmentReminderContext getInstance(){
		return instance;
	}

	private static AppointmentReminderContext instance = new AppointmentReminderContext();

	private AppointmentReminderContext(){}
}