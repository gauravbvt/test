package com.mindalliance.channels.core.model;

import com.mindalliance.channels.core.query.Assignments;
import com.mindalliance.channels.core.query.Commitments;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 10/29/12
 * Time: 2:13 PM
 */
public class InfoFormat extends ModelEntity {

    /**
     * Unknown info format.
     */
    public static InfoFormat UNKNOWN;

    /**
     * Name of unknown info format.
     */
    public static String UnknownName = "(unknown)";

    public InfoFormat() {
    }

    public InfoFormat( String name ) {
        super( name );
    }

    @Override
    public boolean isInvolvedIn( Assignments allAssignments, Commitments allCommitments ) {
        return CollectionUtils.exists(
                allCommitments.toList(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        Flow sharing = ( (Commitment) object ).getSharing();
                        return CollectionUtils.exists(
                                sharing.getChannels(),
                                new Predicate() {
                                    @Override
                                    public boolean evaluate( Object object ) {
                                        InfoFormat format =  ( (Channel) object ).getFormat();
                                        return format != null && format.narrowsOrEquals( InfoFormat.this );
                                    }
                                }
                        );
                    }
                }
        );

    }

    @Override
    public String getTypeName() {
        return "format";
    }

    @Override
    public String getKindLabel() {
        return "format";
    }

    public static String classLabel() {
        return "formats";
    }
}
