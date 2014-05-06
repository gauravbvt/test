package com.mindalliance.channels.core.model.time;

/**
* Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: 5/1/14
* Time: 2:41 PM
*/
public enum TimeUnit {
    Year,
    Month,
    Week,
    Day,
    Hour,
    Minute,
    Second;

    public static TimeUnit getSubUnitOf( TimeUnit timeUnit ) {
        switch ( timeUnit ) {
            case Year:
                return TimeUnit.Month;
            case Month:
                return TimeUnit.Week;
            case Week:
                return TimeUnit.Day;
            case Day:
                return TimeUnit.Hour;
            case Hour:
                return TimeUnit.Minute;
            case Minute:
                return TimeUnit.Second;
            default:
                return null;
        }
    }

    public static int getMaxTrancheIndex( TimeUnit timeUnit ) {
        switch ( timeUnit ) {
            case Year:
                return 12;
            case Month:
                return 4;
            case Week:
                return 7;
            case Day:
                return 24;
            case Hour:
                return 60;
            case Minute:
                return 60;
            default:
                return 0;
        }
    }

    public static TimeUnit parse( String unitString ) {
        String u = unitString.toLowerCase();
        if ( u.startsWith( "second" ))
            return Second;
        else if ( u.startsWith( "minute" ) )
            return Minute;
        else if ( u.startsWith( "hour" ) )
            return Hour;
        else if ( u.startsWith( "day" ) )
            return Day;
        else if ( u.startsWith( "week" ) )
            return Week;
        else if ( u.startsWith( "month" ) )
            return Month;
        else if ( u.startsWith( "year" ) )
            return Year;
        else
            throw new IllegalArgumentException( "Can't parse time unit " + unitString );
    }
}
