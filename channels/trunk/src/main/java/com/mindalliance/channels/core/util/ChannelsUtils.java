package com.mindalliance.channels.core.util;

import com.mindalliance.channels.core.Attachment;
import com.mindalliance.channels.core.Matcher;
import com.mindalliance.channels.core.model.AttachmentImpl;
import com.mindalliance.channels.core.model.Channel;
import com.mindalliance.channels.core.model.Connector;
import com.mindalliance.channels.core.model.Copyable;
import com.mindalliance.channels.core.model.Delay;
import com.mindalliance.channels.core.model.ElementOfInformation;
import com.mindalliance.channels.core.model.ExternalFlow;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Node;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Tag;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.NestedNullException;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.EmailValidator;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Utility functions.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 6, 2009
 * Time: 3:42:39 PM
 */
public final class ChannelsUtils {

    /**
     * The logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( ChannelsUtils.class );

    private ChannelsUtils() {

    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> mergeFlowAttributes(
            Map<String, Object> attributes,
            Map<String, Object> others ) {
        Map<String, Object> merged = new HashMap<String, Object>();
        String desc1 = (String) attributes.get( "description" );
        String desc2 = (String) others.get( "description" );
        merged.put(
                "description",
                desc2.length() > desc1.length() ? desc2 : desc1 );
        merged.put( "tags", attributes.get( "tags" ) + Tag.SEPARATOR + others.get( "tags" ) );
        merged.put( "eois", aggregateEOIs(
                (List<ElementOfInformation>) attributes.get( "eois" ),
                (List<ElementOfInformation>) others.get( "eois" ) ) );
        if ( attributes.containsKey( "all" ) && others.containsKey( "all" ) ) {
            merged.put(
                    "all",
                    (Boolean) attributes.get( "all" ) || (Boolean) others.get( "all" ) );
        }
        merged.put(
                "maxDelay",
                ( (Delay) others.get( "maxDelay" ) ).shorterThan( (Delay) attributes.get( "maxDelay" ) )
                        ? others.get( "maxDelay" ) : attributes.get( "maxDelay" )
        );
        if ( attributes.containsKey( "channels" ) && others.containsKey( "channels" ) ) {
            merged.put(
                    "channels",
                    aggregateChannels(
                            (List<Channel>) attributes.get( "channels" ),
                            (List<Channel>) others.get( "channels" ) )
            );
        }
        merged.put(
                "attachments",
                aggregateAttachments(
                        (List<Attachment>) attributes.get( "attachments" ),
                        (List<Attachment>) others.get( "attachments" ) )
        );
        Set<String> waivers = new HashSet<String>( (List<String>) attributes.get( "waivedIssueDetections" ) );
        waivers.addAll( (List<String>) others.get( "waivedIssueDetections" ) );
        merged.put(
                "waivedIssueDetections",
                new ArrayList<String>( waivers ) );
        if ( attributes.containsKey( "significanceToTarget" ) && others.containsKey( "significanceToTarget" ) ) {
            Flow.Significance significance = Flow.Significance.max(
                    (Flow.Significance) attributes.get( "significanceToTarget" ),
                    (Flow.Significance) others.get( "significanceToTarget" ) );
            merged.put(
                    "significanceToTarget",
                    significance
            );
        }
        if ( attributes.containsKey( "significanceToSource" ) && others.containsKey( "significanceToSource" ) ) {
            Flow.Significance significance = Flow.Significance.max(
                    (Flow.Significance) attributes.get( "significanceToSource" ),
                    (Flow.Significance) others.get( "significanceToSource" ) );
            merged.put(
                    "significanceToSource",
                    significance
            );
        }
        // Make intent null (unknown) if there is a conflict.
        Flow.Intent intent1 = (Flow.Intent) attributes.get( "intent" );
        Flow.Intent intent2 = (Flow.Intent) others.get( "intent" );
        if ( Flow.Intent.same( intent1, intent2 ) ) {
            merged.put( "intent", intent1 );
        } else {
            merged.put( "intent", null );
        }
        // Make restriction null (unknown) if there is a conflict.
        merged.put( "restrictions", Flow.Restriction.resolve(
                (List<Flow.Restriction>) attributes.get( "restrictions" ),
                (List<Flow.Restriction>) others.get( "restrictions" ) ) );
        // Merge is task failure flow
        merged.put(
                "ifTaskFails",
                (Boolean) attributes.get( "ifTaskFails" ) || (Boolean) others.get( "ifTaskFails" ) );
        // merge other modalities
        merged.put(
                "referencesEventPhase",
                (Boolean) attributes.get( "referencesEventPhase" ) || (Boolean) others.get( "referencesEventPhase" ) );
        merged.put(
                "canBypassIntermediate",
                (Boolean) attributes.get( "canBypassIntermediate" ) || (Boolean) others.get( "canBypassIntermediate" ) );
        merged.put(
                "receiptConfirmationRequested",
                (Boolean) attributes.get( "receiptConfirmationRequested" ) || (Boolean) others.get( "receiptConfirmationRequested" ) );
        return merged;
    }

    private static List<ElementOfInformation> aggregateEOIs(
            List<ElementOfInformation> eois,
            List<ElementOfInformation> others ) {
        List<ElementOfInformation> aggregate = new ArrayList<ElementOfInformation>();
        for ( ElementOfInformation eoi : eois ) {
            aggregate.add( new ElementOfInformation( eoi ) );
        }
        for ( final ElementOfInformation eoi : others ) {
            ElementOfInformation synonymous = (ElementOfInformation) CollectionUtils.find(
                    eois,
                    new Predicate() {
                        @Override
                        public boolean evaluate( Object object ) {
                            return Matcher.same(
                                    eoi.getContent(),
                                    ( (ElementOfInformation) object ).getContent() );
                        }
                    }
            );
            if ( synonymous == null ) {
                aggregate.add( new ElementOfInformation( eoi ) );
            } else {
                aggregate.remove( synonymous );
                aggregate.add( ElementOfInformation.merge( synonymous, eoi ) );
            }
        }
        return aggregate;
    }

    private static List<Channel> aggregateChannels( List<Channel> channels, List<Channel> others ) {
        List<Channel> aggregate = new ArrayList<Channel>();
        for ( Channel channel : channels ) {
            aggregate.add( new Channel( channel ) );
        }
        for ( final Channel channel : others ) {
            Channel synonymous = (Channel) CollectionUtils.find(
                    channels,
                    new Predicate() {
                        @Override
                        public boolean evaluate( Object object ) {
                            return channel.getMedium().equals(
                                    ( (Channel) object ).getMedium() );
                        }
                    }
            );
            if ( synonymous == null ) {
                aggregate.add( new Channel( channel ) );
            } else {
                aggregate.remove( synonymous );
                aggregate.add( Channel.merge( synonymous, channel ) );
            }
        }
        return aggregate;
    }

    private static List<Attachment> aggregateAttachments( List<Attachment> attachments, List<Attachment> others ) {
        List<Attachment> aggregate = new ArrayList<Attachment>();
        for ( Attachment attachment : attachments )
            aggregate.add( new AttachmentImpl( attachment ) );

        for ( final Attachment attachment : others ) {
            Attachment synonymous = (Attachment) CollectionUtils.find(
                    attachments,
                    new Predicate() {
                        @Override
                        public boolean evaluate( Object object ) {
                            return attachment.getUrl().equals(
                                    ( (Attachment) object ).getUrl() );
                        }
                    }
            );
            if ( synonymous == null )
                aggregate.add( new AttachmentImpl( attachment ) );
            else {
                aggregate.remove( synonymous );
                aggregate.add( AttachmentImpl.merge( synonymous, attachment ) );
            }
        }
        return aggregate;
    }


    /**
     * Captures the connection of a flow.
     *
     * @param flow a flow
     * @return a map of attribute names and values
     */
    public static Map<String, Object> getFlowConnectionState( final Flow flow ) {
        Part part;
        if ( flow.isInternal() ) {
            part = flow.getSource().isPart() ? (Part) flow.getSource() : (Part) flow.getTarget();
        } else {
            part = ( (ExternalFlow) flow ).getPart();
        }
        return getFlowConnectionState( flow, part );
    }

    /**
     * Captures the connection of a flow from the perspective of a part.
     *
     * @param flow a flow
     * @param part a part
     * @return a map of attribute names and values
     */
    public static Map<String, Object> getFlowConnectionState( final Flow flow, final Part part ) {
        final Node other;
        final boolean isSend;
        if ( flow.isInternal() ) {
            isSend = flow.getSource() == part;
            other = isSend ? flow.getTarget() : flow.getSource();
        } else {
            ExternalFlow externalFlow = (ExternalFlow) flow;
            isSend = !externalFlow.isPartTargeted();
            other = externalFlow.getConnector();
        }
        Map<String, Object> state = new HashMap<String, Object>();
        state.put( "name", flow.getName() );
        state.put( "isSend", isSend );
        state.put( "segment", part.getSegment().getId() );
        state.put( "part", part.getId() );
        state.put( "otherSegment", other.getSegment().getId() );
        if ( other.isConnector() ) {
            // Remember the connector's inner flow if external (its id is persistent, if mapped)
            if ( part.getSegment() != other.getSegment() ) {
                state.put( "other", ( (Connector) other ).getInnerFlow().getId() );
            }
        } else {
            state.put( "other", other.getId() );
        }
        state.put( "attributes", flow.mapState() );
        return state;
    }

    /**
     * Get the set of identifiables to be locked if a flow and its components are to be locked.
     *
     * @param flow a flow
     * @return a list of identifiable
     */
    public static List<Identifiable> getLockingSetFor( Flow flow ) {
        List<Identifiable> lockingSet = new ArrayList<Identifiable>();
        lockingSet.add( flow );
        if ( flow.isInternal() ) {
            Node node = flow.getSource();
            if ( node.isPart() ) lockingSet.add( node );
            node = flow.getTarget();
            if ( node.isPart() ) lockingSet.add( node );
        } else {
            ExternalFlow externalFlow = (ExternalFlow) flow;
            // lockingSet.add( externalFlow.getConnector() );
            lockingSet.add( externalFlow.getPart() );
        }
        return lockingSet;
    }

    /**
     * Get the set of identifiables to be locked if a part and its components are to be locked.
     *
     * @param part a part
     * @return a list of identifiable
     */
    public static List<Identifiable> getLockingSetFor( Part part ) {
        List<Identifiable> lockingSet = new ArrayList<Identifiable>();
        lockingSet.add( part );
        Iterator<Flow> sends = part.sends();
        while ( sends.hasNext() ) {
            Flow send = sends.next();
            lockingSet.add( send );
            lockingSet.addAll( getLockingSetFor( send ) );
        }
        Iterator<Flow> receives = part.receives();
        while ( receives.hasNext() ) {
            Flow receive = receives.next();
            lockingSet.add( receive );
            lockingSet.addAll( getLockingSetFor( receive ) );
        }
        return lockingSet;
    }

    /**
     * Initialize an object from attributes
     *
     * @param object     an object
     * @param attributes attributes
     */
    @SuppressWarnings("unchecked")
    public static void initialize( Object object, Map<String, Object> attributes ) {
        for ( String property : attributes.keySet() ) {
            Object value = attributes.get( property );
            try {
                if ( value instanceof List ) {
                    List copy = (List) value.getClass().newInstance();
                    copy.addAll( (List) value );
                    value = copy;
                }
                if ( value instanceof Copyable ) {
                    value = ( (Copyable) value ).copy();
                }
                PropertyUtils.setProperty( object, property, value );
            } catch ( IllegalAccessException e ) {
                throw new RuntimeException( e );
            } catch ( InvocationTargetException e ) {
                throw new RuntimeException( e );
            } catch ( NoSuchMethodException e ) {
                throw new RuntimeException( e );
            } catch ( InstantiationException e ) {
                throw new RuntimeException( e );
            }
        }
    }

    /**
     * Get a bean's property value.
     *
     * @param bean         an object
     * @param property     a string
     * @param defaultValue an object
     * @return an object
     */
    public static Object getProperty( Object bean, String property, Object defaultValue ) {
        if ( property == null ) return defaultValue;
        if ( property.isEmpty() ) return bean;
        Object value;
        try {
            value = PropertyUtils.getProperty( bean, property );
        } catch ( IllegalAccessException e ) {
            throw new RuntimeException( e );
        } catch ( InvocationTargetException e ) {
            throw new RuntimeException( e );
        } catch ( NoSuchMethodException e ) {
            throw new RuntimeException( e );
        } catch ( NestedNullException e ) {
            LOG.debug( "Nested null on " + property + " for " + bean );
            value = null;
        }
        return value != null ? value : defaultValue;
    }

    /**
     * Get a copy of a part, including needs and capabilities.
     *
     * @param part a part
     * @return a map
     */
    public static Map<String, Object> getPartCopy( Part part ) {
        Map<String, Object> copy = new HashMap<String, Object>();
        copy.put( "partState", part.mapState() );
        copy.put( "needs", getNeedStates( part ) );
        copy.put( "capabilities", getCapabilityStates( part ) );
        return copy;
    }

    private static List<Map<String, Object>> getNeedStates( Part part ) {
        List<Map<String, Object>> needStates = new ArrayList<Map<String, Object>>();
        Iterator<Flow> receives = part.receives();
        while ( receives.hasNext() ) {
            mergeFlowState( needStates, getReceiveState( receives.next(), part ) );
        }
        return needStates;
    }

    private static List<Map<String, Object>> getCapabilityStates( Part part ) {
        List<Map<String, Object>> capabilityStates = new ArrayList<Map<String, Object>>();
        Iterator<Flow> sends = part.sends();
        while ( sends.hasNext() ) {
            mergeFlowState( capabilityStates, getSendState( sends.next(), part ) );
        }
        return capabilityStates;
    }

    /**
     * Aggregate EOIs of synonymous flows. Drop "other" node.
     * Keep strongest attribute values when merging (terminates > triggers> critical > useful ).
     *
     * @param flowStates a list of flow states
     * @param flowState  a flow state to merge
     */
    @SuppressWarnings("unchecked")
    private static void mergeFlowState(
            List<Map<String, Object>> flowStates,
            final Map<String, Object> flowState ) {
        Map<String, Object> synonymousState = (Map<String, Object>) CollectionUtils.find(
                flowStates,
                new Predicate() {
                    @Override
                    @SuppressWarnings("unchecked")
                    public boolean evaluate( Object object ) {
                        String name = (String) ( (Map<String, Object>) object ).get( "name" );
                        String otherName = (String) flowState.get( "name" );
                        return Matcher.same( name, otherName );
                    }
                } );
        if ( synonymousState == null ) {
            flowState.put( "other", null );
            flowState.put( "otherSegment", null );
            ( (Map<String, Object>) flowState.get( "attributes" ) ).put( "askedFor", false );
            flowStates.add( flowState );
        } else {
            Map<String, Object> mergedAttributes = mergeFlowAttributes(
                    (Map<String, Object>) synonymousState.get( "attributes" ),
                    (Map<String, Object>) flowState.get( "attributes" ) );
            synonymousState.put( "attributes", mergedAttributes );
        }
    }


    /**
     * Get state of a part's capability.
     *
     * @param flow a flow
     * @param part a part
     * @return a map
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> getSendState( Flow flow, Part part ) {
        Map<String, Object> sendState = getFlowConnectionState( flow, part );
        Map<String, Object> attributes = (Map<String, Object>) sendState.get( "attributes" );
        attributes.remove( "significanceToTarget" );
        attributes.remove( "all" );
        if ( !flow.isAskedFor() ) attributes.remove( "channels" );
        return sendState;
    }

    /**
     * Get state of a part's need.
     *
     * @param flow a flow
     * @param part a part
     * @return a map
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> getReceiveState( Flow flow, Part part ) {
        Map<String, Object> receiveState = getFlowConnectionState( flow, part );
        Map<String, Object> attributes = (Map<String, Object>) receiveState.get( "attributes" );
        attributes.remove( "significanceToSource" );
        attributes.remove( "all" );
        if ( flow.isAskedFor() ) attributes.remove( "channels" );
        return receiveState;
    }

    /**
     * Whetehr a string starts with a vowel.
     *
     * @param str a string
     * @return a boolean
     */
    public static boolean startsWithVowel( String str ) {
        return !str.isEmpty() && "AEIOUaeiou".indexOf( str.charAt( 0 ) ) != -1;
    }

    /**
     * Uncapitalize if both the first and second letters are not uppercase.
     *
     * @param s a string
     * @return a string
     */
    public static String smartUncapitalize( String s ) {
        if ( s.length() > 1
                && Character.isUpperCase( s.charAt( 0 ) )
                && Character.isUpperCase( s.charAt( 1 ) ) )
            return s;
        else
            return StringUtils.uncapitalize( s );

    }

    public static String lcFirst( String phrase ) {
        if ( phrase.length() < 2 )
            return phrase;
        return ChannelsUtils.smartUncapitalize( phrase );
    }


    public static String listToString( List<?> list, String lastSep ) {
        return listToString( list, ", ", lastSep );
    }

    public static String listToString( List<?> list, String sep, String lastSep ) {
        StringWriter w = new StringWriter();
        for ( int i = 0; i < list.size(); i++ ) {
            w.append( String.valueOf( list.get( i ) ) );
            if ( i == list.size() - 2 )
                w.append( lastSep );
            else if ( i != list.size() - 1 )
                w.append( sep );
        }
        return w.toString();
    }

    public static String ensurePeriod( String sentence ) {
        return sentence == null || sentence.isEmpty() || sentence.length() > 0 && sentence.charAt(
                sentence.length() - 1 ) == '.'
                || sentence.length() > 0 && sentence.charAt( sentence.length() - 1 ) == ';' ?
                sentence :
                sentence + '.';
    }

    /**
     * Clean up a name by removing extra spaces.
     *
     * @param name a string
     * @return a string
     */
    public static String cleanUpName( String name ) {
        return name.trim()
                .replaceAll( "[\\n\\t]", " " ) // replace newlines and tabs by spaces
                .replaceAll( "\\s\\s*", " " )  // trim multiple spaces
                .replaceAll( "[^\\sA-Za-z0-9äëïöüÄËÏÖÜáéíóúÁÉÍÓÚàèìòùÀÈÌÒÙâêîôûÂÊÎÔÛçÇñÑ\\._\\-\\']", "_" ); // replace "special" characters by underscore
    }

    /**
     * Clean up a name by removing extra spaces.
     *
     * @param name a string
     * @return a string
     */
    public static String cleanUpPhrase( String name ) {
        return name.trim()
                .replaceAll( "[\\n\\t]", " " ) // replace newlines and tabs by spaces
                .replaceAll( "\\s\\s*", " " )  // trim multiple spaces
                .replaceAll( "[^\\sA-Za-z0-9äëïöüÄËÏÖÜáéíóúÁÉÍÓÚàèìòùÀÈÌÒÙâêîôûÂÊÎÔÛçÇñÑ\\._\\-\\'\\?\\!]", "_" ); // replace "special" characters by underscore
    }


    public static boolean isValidEmailAddress( String address ) {
        return address != null
                && !address.isEmpty()
                && EmailValidator.getInstance().isValid( address );
    }

    public static <T> List<T> moveUp( T item, List<T> list ) {
        int index = list.indexOf( item );
        List<T> results = new ArrayList<T>( list );
        if ( index > 0 ) {
            results.set( index, list.get( index - 1 ) );
            results.set( index - 1, item );
        }
        return results;
    }

    public static <T> List<T> moveDown( T item, List<T> list ) {
        int index = list.indexOf( item );
        List<T> results = new ArrayList<T>( list );
        if ( index >= 0 && index < ( list.size() - 1 ) ) {
            results.set( index, list.get( index + 1 ) );
            results.set( index + 1, item );
        }
        return results;
    }

    public static String decamelize( String s ) {
        StringReader reader = new StringReader( s );
        StringBuilder sb = new StringBuilder();
        int c;
        int index = 0;
        try {
            while ( ( c = reader.read() ) != -1 ) {
                char ch = (char) c;
                if ( index == 0 ) {
                    sb.append( ch );
                } else {
                    if ( Character.isUpperCase( ch ) ) {
                        sb.append( " " );
                        sb.append( Character.toLowerCase( ch ) );
                    } else {
                        sb.append( ch );
                    }
                }
                index++;
            }
        } catch ( IOException e ) {
            throw new RuntimeException( e );
        }
        return sb.toString();
    }

    public static String getShortTimeIntervalString( long millis ) {
        long diffInSeconds = Math.abs( millis / 1000 );
        /* sec */
        long seconds = ( diffInSeconds >= 60 ? diffInSeconds % 60 : diffInSeconds );
        /* min */
        long minutes = ( diffInSeconds = ( diffInSeconds / 60 ) ) >= 60 ? diffInSeconds % 60 : diffInSeconds;
        /* hours */
        long hours = ( diffInSeconds = ( diffInSeconds / 60 ) ) >= 24 ? diffInSeconds % 24 : diffInSeconds;
        /* days */
        long days = diffInSeconds / 24;

        StringBuilder sb = new StringBuilder();
        if ( days > 0 ) {
            sb.append( days );
            sb.append( " day" );
            sb.append( days > 1 ? "s" : "" );
        }
        if ( hours > 0 ) {
            if ( sb.length() == 0 ) {
                sb.append( hours );
                sb.append( " hour" );
                sb.append( hours > 1 ? "s" : "" );
            }
        }
        if ( minutes > 0 ) {
            if ( sb.length() == 0 ) {
                sb.append( minutes );
                sb.append( " minute" );
                sb.append( minutes > 1 ? "s" : "" );
            }
        }
        if ( sb.length() == 0 ) {
            sb.append( seconds );
            sb.append( " second" );
            sb.append( seconds > 1 ? "s" : "" );
        }
        return sb.toString();
    }

    public static String getLongTimeIntervalString( long millis ) {
        long diffInSeconds = Math.abs( millis / 1000 );
        /* sec */
        long seconds = ( diffInSeconds >= 60 ? diffInSeconds % 60 : diffInSeconds );
        /* min */
        long minutes = ( diffInSeconds = ( diffInSeconds / 60 ) ) >= 60 ? diffInSeconds % 60 : diffInSeconds;
        /* hours */
        long hours = ( diffInSeconds = ( diffInSeconds / 60 ) ) >= 24 ? diffInSeconds % 24 : diffInSeconds;
        /* days */
        long days = diffInSeconds / 24;

        StringBuilder sb = new StringBuilder();
        if ( days > 0 ) {
            sb.append( days );
            sb.append( " day" );
            sb.append( days > 1 ? "s" : "" );
        }
        if ( hours > 0 ) {
            if ( sb.length() > 0 ) sb.append( ", " );
            sb.append( hours );
            sb.append( " hour" );
            sb.append( hours > 1 ? "s" : "" );
        }
        if ( minutes > 0 ) {
            if ( sb.length() > 0 ) sb.append( ", " );
            sb.append( minutes );
            sb.append( " minute" );
            sb.append( minutes > 1 ? "s" : "" );
        }
        if ( sb.length() == 0 || seconds > 0 ) {
            if ( sb.length() > 0 ) sb.append( ", " );
            sb.append( seconds );
            sb.append( " second" );
            sb.append( seconds > 1 ? "s" : "" );
        }
        return sb.toString();
    }

    public static String convertTemplate( String template, Object bean ) {
        return convertTemplate( template, bean, null );
    }

    @SuppressWarnings("unchecked")
    public static String convertTemplate( String template, Object bean, Map<String, Object> extraContext ) {
        StringWriter writer = new StringWriter();
        try {
            Map context = BeanUtils.describe( bean );
            if ( extraContext != null ) {
                for ( String key : extraContext.keySet() ) {
                    context.put( key, extraContext.get( key ) );
                }
            }
            Velocity.evaluate( new VelocityContext( context ), writer, "", template );
        } catch ( Exception e ) {
            LOG.warn( "Invalid model: " + template, e );
            return template;
        }
        return writer.toString();
    }

    public static boolean areEqualOrNull( Object object, Object other ) {
        return ( object == null && other == null ) ||
                ( object != null && other != null && object.equals( other ) );
    }

    /**
     * Return a "directory-safe" equivalent name.
     *
     * @param name original name
     * @return safe version
     */
    public static String sanitize( String name ) {
        return sanitize( name, "_" );
    }

    /**
     * Return a "directory-safe" equivalent name.
     *
     * @param name        original name
     * @param replacement string replacing non-word characters
     * @return safe version
     */
    public static String sanitize( String name, String replacement ) {
        return name == null ? "" : name.replaceAll( "\\W", replacement );
    }

    /**
     * Return an "HTML attribute-safe" equivalent name.
     *
     * @param name        original name
     * @return safe version
     */
    public static String sanitizeAttribute( String name ) {
        return name.replaceAll( "\"", " " );
    }

    public static String split( String string, String separator, int maxLines, int maxLineLength ) {
        String[] tokens = StringUtils.split( string, " " );
        StringBuilder sb = new StringBuilder(  );
        String line = "";
        int lineCount = 0;
        for ( int i = 0; i < tokens.length; i++ ) {
            line += tokens[i];
            if (line.length() >= maxLineLength && lineCount < maxLines ) {
                sb.append( line );
                line = "";
                lineCount++;
                if ( i < tokens.length - 1 ) {
                    sb.append( separator );
                }
            } else {
                line += " ";
            }
        }
        sb.append( line );
        return sb.toString();
    }

    public static <T> void addIfNotNull( Collection<T> list, T... objects) {
        for ( T obj : objects) {
            if ( obj != null )
                list.add( obj );
        }
    }

}
