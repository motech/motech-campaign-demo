package org.motechproject.appointments.api.dao.impl;

import org.ektorp.CouchDbConnector;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.motechproject.appointments.api.EventKeys;
import org.motechproject.appointments.api.model.Appointment;
import org.motechproject.event.EventRelay;
import org.motechproject.model.MotechEvent;

import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class AppointmentsCouchDBDAOImplTest
{
    @Mock
    EventRelay eventRelay;

    @Mock
    CouchDbConnector couchDbConnector;

    @InjectMocks
    AppointmentsCouchDBDAOImpl appointmentsDAO;

    @Before
    public void setUp() {
        couchDbConnector = mock(CouchDbConnector.class);
        appointmentsDAO = new AppointmentsCouchDBDAOImpl(couchDbConnector);
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testAddAppointment() {
        Appointment a = new Appointment();

        ArgumentCaptor<MotechEvent> argument = ArgumentCaptor.forClass(MotechEvent.class);

        appointmentsDAO.addAppointment(a);

        verify(couchDbConnector).create(a);
        verify(eventRelay).sendEventMessage(argument.capture());

        MotechEvent event = argument.getValue();

        assertTrue(EventKeys.APPOINTMENT_CREATED_SUBJECT.equals(event.getSubject()));
    }

    @Test
    public void testUpdateAppointment() {
        Appointment a = new Appointment();

        ArgumentCaptor<MotechEvent> argument = ArgumentCaptor.forClass(MotechEvent.class);

        appointmentsDAO.updateAppointment(a);

        verify(couchDbConnector).update(a);
        verify(eventRelay).sendEventMessage(argument.capture());

        MotechEvent event = argument.getValue();

        assertTrue(EventKeys.APPOINTMENT_UPDATED_SUBJECT.equals(event.getSubject()));
    }

    @Test
    public void testRemoveAppointment() {
        Appointment a = new Appointment();

        ArgumentCaptor<MotechEvent> argument = ArgumentCaptor.forClass(MotechEvent.class);

        appointmentsDAO.removeAppointment(a);

        verify(couchDbConnector).delete(a);
        verify(eventRelay).sendEventMessage(argument.capture());

        MotechEvent event = argument.getValue();

        assertTrue(EventKeys.APPOINTMENT_DELETED_SUBJECT.equals(event.getSubject()));
    }

    @Test
    public void testRemoveAppointmentById() {
        Appointment a = new Appointment();
        a.setId("aID");

        ArgumentCaptor<MotechEvent> argument = ArgumentCaptor.forClass(MotechEvent.class);
        when(appointmentsDAO.getAppointment("aID")).thenReturn(a);

        appointmentsDAO.removeAppointment(a.getId());

        verify(couchDbConnector).delete(a);
        verify(eventRelay).sendEventMessage(argument.capture());

        MotechEvent event = argument.getValue();

        assertTrue(EventKeys.APPOINTMENT_DELETED_SUBJECT.equals(event.getSubject()));
    }

    @Test
    public void testFindByExternalId() {
        List<Appointment> list = appointmentsDAO.findByExternalId("eID");

        assertTrue(list.isEmpty());
    }

/*
                public void addAppointment(Appointment appointment);
    public void updateAppointment(Appointment appointment);
    public Appointment getAppointment(String appointmentId);
    public void removeAppointment(String appointmentId);
    public void removeAppointment(Appointment appointment);
             */
}
