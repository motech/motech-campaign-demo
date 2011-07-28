package org.motechproject.scheduletracking.api.domain.factory;

import org.motechproject.scheduletracking.api.domain.*;
import org.motechproject.scheduletracking.api.userspecified.AlertRecord;
import org.motechproject.scheduletracking.api.userspecified.MilestoneRecord;
import org.motechproject.scheduletracking.api.userspecified.ScheduleRecord;
import org.motechproject.scheduletracking.api.userspecified.ScheduleWindowsRecord;
import org.motechproject.valueobjects.WallTime;
import org.motechproject.valueobjects.factory.WallTimeFactory;

import java.util.List;

public class ScheduleFactory {
    public static Schedule create(ScheduleRecord scheduleRecord) {
        Schedule schedule = new Schedule(scheduleRecord.name());
        List<MilestoneRecord> milestoneRecords = scheduleRecord.milestoneRecords();
        for (MilestoneRecord milestoneRecord : milestoneRecords) {
            ScheduleWindowsRecord scheduleWindowsRecord = milestoneRecord.scheduleWindowsRecord();

            Milestone milestone = new Milestone(milestoneRecord.name(), milestoneRecord.referenceDate(), wallTime(scheduleWindowsRecord.earliest()),
                                        wallTime(scheduleWindowsRecord.due()), wallTime(scheduleWindowsRecord.late()), wallTime(scheduleWindowsRecord.max()));
            schedule.addMilestone(milestone);

            for (AlertRecord alertRecord : milestoneRecord.alerts()) {
                MilestoneWindow milestoneWindow = milestone.window(WindowName.valueOf(alertRecord.window()));
                milestoneWindow.addAlert(new AlertConfiguration(WallTimeFactory.create(alertRecord.startOffset()), WallTimeFactory.create(alertRecord.interval()), Integer.parseInt(alertRecord.count())));
            }
        }
        return schedule;
    }

    private static WallTime wallTime(String userDefinedTime) {
        return WallTimeFactory.create(userDefinedTime);
    }
}