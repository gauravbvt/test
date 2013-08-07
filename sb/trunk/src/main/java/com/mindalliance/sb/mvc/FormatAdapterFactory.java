package com.mindalliance.sb.mvc;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.context.MessageSource;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.convert.ConversionService;

import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;
import javax.persistence.metamodel.PluralAttribute;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * Wrapper on a model object to provide output information.
 */
public class FormatAdapterFactory<T> {

    private static final Pattern DOT_MATCHER = Pattern.compile( "\\." );

    private final Class<T> objectClass;
    private final EntityType<T> entityType;
    private final Set<String> propertyNames;
    private final Set<String> visiblePropertyNames;
 
    private final MessageSource messageSource;
    private final ConversionService conversionService;
    private Locale locale = Locale.getDefault();
    
    public FormatAdapterFactory( Class<T> objectClass,
                                 ConversionService conversionService, MessageSource messageSource,
                                 Metamodel metamodel ) {
        this.objectClass = objectClass;
        this.conversionService = conversionService;
        this.messageSource = messageSource;
        this.entityType = metamodel.entity( objectClass );

        JsonPropertyOrder order = AnnotationUtils.findAnnotation( objectClass, JsonPropertyOrder.class );        
        if ( order == null )
            propertyNames = visiblePropertyNames = 
                    Collections.unmodifiableSet( getDisplayableProperties( objectClass ) );
        else {
            visiblePropertyNames = Collections.unmodifiableSet(
                                        new LinkedHashSet<String>( Arrays.asList( order.value() ) ) );
            
            Set<String> orderedProperties = new LinkedHashSet<String>( visiblePropertyNames );
            orderedProperties.addAll( getDisplayableProperties( objectClass ) );
            propertyNames = Collections.unmodifiableSet( orderedProperties );
        }
    }

    private Set<String> getDisplayableProperties( Class<T> objectClass ) {
        Set<String> result = new LinkedHashSet<String>();

        Collection<String> names = new HashSet<String>();
        for ( PropertyDescriptor descriptor : PropertyUtils.getPropertyDescriptors( objectClass ) )
            if ( isConvertible( descriptor ) )
                names.add( descriptor.getName() );

        for ( Field field : objectClass.getDeclaredFields() ) {
            String name = field.getName();
            if ( names.contains( name ) )
                result.add( name );
        }
        return result;
    }

    private boolean isConvertible( PropertyDescriptor descriptor ) {
        Class<?> propertyType = descriptor.getPropertyType();
        return conversionService.canConvert( 
            Collection.class.isAssignableFrom( propertyType ) ?
                ( (PluralAttribute) entityType.getAttribute( descriptor.getName() ) ).getElementType().getJavaType() 
              : propertyType, 
            
            String.class );
    }

    /**
     * Create a new adapter on the specified object.
     * @param object the object
     * @return an adapter to use for output of formatter values.
     */
    public FormatAdapter makeAdapter( T object ) {
        return new FormatAdapterImpl<T>( object );
    }
    
    /**
     * Find all properties with a public getter, in the order of field definitions in the class (hopefully).
     *
     * @return an iterable collection of property name.
     */
    public Set<String> getPropertyNames() {
        return propertyNames;
    }

    /**
     * Find properties who should be displayed initially. These are properties specified in a @JsonPropertyOrder
     * annotation.
     *
     * @return the visible properties, or all if class is not annotated.
     */
    public Set<String> getVisiblePropertyNames() {
        return visiblePropertyNames;
    }
    
    public boolean isVisible( String propertyName ) {
        return visiblePropertyNames.contains( propertyName );
    }

    public String getDisplayName( String propertyName ) {
        String i18nedName = messageSource.getMessage( 
                MessageFormat.format( "label_{0}_{1}", 
                                      DOT_MATCHER.matcher( objectClass.getName().toLowerCase( locale ) )
                                                 .replaceAll( "_" ), 
                                      propertyName.toLowerCase( locale ) ),
               null,
               null,
               locale );

        return i18nedName == null ? deCamelCase( propertyName ) : i18nedName;   
    }

    private static String deCamelCase( CharSequence propertyName ) {
        StringBuilder builder = new StringBuilder();

        builder.append( Character.toUpperCase( propertyName.charAt( 0 ) ) );
        for ( int i = 1; i < propertyName.length(); i++ ) {
            char c = propertyName.charAt( i );
            if ( Character.isUpperCase( c ) )
                builder.append( ' ' );
            builder.append( c );
        }

        return builder.toString();
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale( Locale locale ) {
        this.locale = locale;
    }

    public Class<T> getObjectClass() {
        return objectClass;
    }

    //=================================================
    public class FormatAdapterImpl<T> implements FormatAdapter {

        private final T object;
        private final ConcurrentHashMap<String,FormattedValue> valueMap = new ConcurrentHashMap<String, FormattedValue>();

        private FormatAdapterImpl( T object ) {
            this.object = object;
        }
        
        @Override
        public FormattedValue get( String fieldName ) {

            FormattedValue value = valueMap.get( fieldName );
            if ( value == null ) {
                FormattedValue newValue = new FormattedValueImpl( fieldName );
                valueMap.putIfAbsent( fieldName, newValue );
                return newValue;
            }
            else
                return value;
        }

        @Override
        public Iterator<FormattedValue> iterator() {
            return new Iterator<FormattedValue>() {
                private final Iterator<String> iterator = propertyNames.iterator();
                
                @Override
                public boolean hasNext() {
                    return iterator.hasNext();
                }

                @Override
                public FormattedValue next() {
                    return get( iterator.next() );
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();    
                }
            };
        }

        //----------------------------------------------
        public class FormattedValueImpl implements FormattedValue {

            private final String fieldName;
            private final Object fieldValue;

            private FormattedValueImpl( String fieldName ) {
                try {
                    this.fieldName = fieldName;
                    this.fieldValue = PropertyUtils.getProperty( object, fieldName );

                } catch ( Exception e ) {
                    throw new RuntimeException( "Error accessing " + objectClass.getName() + '.' + fieldName, e );
                }
            }

            @Override
            public Object getFieldValue() {
                return fieldValue;
            }

            @Override
            public String getFieldName() {
                return fieldName;
            }
            
            @Override
            public String getName() {
                return getDisplayName( fieldName );
            }
            
            @Override
            public boolean isVisible() {
                return FormatAdapterFactory.this.isVisible( fieldName );
            }
            
            @Override
            public String getValue() {
                return fieldValue == null ? "" 
                     : conversionService.convert( fieldValue, String.class );
            }
            
            @Override
            public boolean isNull() {
                return fieldValue == null;
            }

            @Override
            public boolean isQuotable() {
                if ( fieldValue == null )
                    return true;
                
                Class<?> aClass = fieldValue.getClass();
                return Calendar.class.isAssignableFrom( aClass )
                    || String.class.isAssignableFrom( aClass )
                    || !BeanUtils.isSimpleProperty( aClass );
            }

            @Override
            public String getJavascriptValue() {
                String chars = getValue();
                if ( isQuotable() ) {
                    StringBuilder out = new StringBuilder( chars.length() + 2 );

                    // TODO make this more efficient
                    out.append( '"' );
                    for ( int i = 0; i < chars.length(); i++ ) {
                        char c = chars.charAt( i );
                        if ( c == '"' )
                            out.append( "\\\"" );
                        else
                            out.append( c );
                    }
                    out.append( '"' );

                    return out.toString();
                }
                
                return chars;
            }
            
            @Override
            public String toString() {
                return getValue();
            }
        }
    }
}
