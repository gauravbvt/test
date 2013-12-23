package com.mindalliance.channels.core.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * A definition of work time.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/20/13
 * Time: 11:17 AM
 */
public class WorkTime implements Serializable {

    public enum WorkPeriod {

        FullTime,
        BusinessHours,
        PartTime,
        None;

        public String getLabel() {
            switch ( this ) {
                case FullTime: return "Full time";
                case BusinessHours: return "Business hours";
                case PartTime: return "Part time";
                default: return "None";
            }
        }

    }
    private WorkPeriod workPeriod = WorkPeriod.FullTime;

    public static WorkPeriod[] ALL_WORK_PERIODS = { WorkPeriod.FullTime, WorkPeriod.BusinessHours, WorkPeriod.PartTime};

    public static WorkTime FullTime() {
        return new WorkTime( WorkPeriod.FullTime );
    }

    public static WorkTime BusinessHours() {
        return new WorkTime( WorkPeriod.BusinessHours );
    }

    public static WorkTime PartTime() {
        return new WorkTime( WorkPeriod.PartTime );
    }


    public static List<WorkPeriod> allWorkPeriods() {
        return Arrays.asList(ALL_WORK_PERIODS );
    }


    public WorkTime() {}

    public WorkTime( WorkPeriod workPeriod ) {
        this.workPeriod = workPeriod;
    }

    public WorkPeriod getWorkPeriod() {
        return workPeriod;
    }

    public void setWorkPeriod( WorkPeriod workPeriod ) {
        this.workPeriod = workPeriod;
    }

    public boolean includes( WorkTime other ) {
        return getWorkPeriod() == WorkPeriod.FullTime ||
                getWorkPeriod() == other.getWorkPeriod();
    }

    public boolean isAlways() {
        return getWorkPeriod() == WorkPeriod.FullTime;
    }

    public boolean isEmpty() {
        return getWorkPeriod() == WorkPeriod.None;
    }

    @Override
    public String toString() {
        return getWorkPeriod().getLabel();
    }

    @Override
    public boolean equals( Object object ) {
        return object instanceof WorkTime
                && ((WorkTime)object).getWorkPeriod() == getWorkPeriod();
    }

    @Override
    public int hashCode() {
        return getWorkPeriod().hashCode();
    }
}
