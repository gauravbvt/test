package com.mindalliance.sb.mvc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManagerFactory;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Cache service for format adapter factories.
 */
@SuppressWarnings( "unchecked" )
@Service
public class FormatAdapterFactoryService {

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private FormattingConversionService conversionService;
    
    @Autowired
    private EntityManagerFactory entityManagerFactory;
    
    private final Map<Class<?>,FormatAdapterFactory<?>> cache = new ConcurrentHashMap<Class<?>, FormatAdapterFactory<?>>();

    private static final Logger LOG = LoggerFactory.getLogger( FormatAdapterFactoryService.class );

    public <T> FormatAdapterFactory<T> getFactory( Class<T> modelClass ) {

        try {
            FormatAdapterFactory<?> factory = cache.get( modelClass );
            if ( factory == null ) {
                FormatAdapterFactory<T> result = 
                    new FormatAdapterFactory<T>( modelClass, conversionService, messageSource,
                                                 entityManagerFactory.getMetamodel() );
                cache.put( modelClass, result );
                return result;
                
            } else
                return (FormatAdapterFactory<T>) factory;
            
        } catch ( RuntimeException e ) {
            // Make an entry in the log
            LOG.error( "Error while creating a factory", e );
            throw e;
        }
    }

    /**
     * Infer a format adapter factory from an item in a list.
     * @param list a list of objects
     * @param <T> the type to be inferred
     * @return  an appropriate factory
     */
    public <T> FormatAdapterFactory<T> getFactoryFromList( Collection<T> list ) {        
        if ( list == null || list.isEmpty() )
            throw new IllegalArgumentException( "List can't be null or empty" );

        return (FormatAdapterFactory<T>) getFactory( list.iterator().next().getClass() );
    }
}
