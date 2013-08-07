package com.mindalliance.sb.mvc;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * Kludge for adding a space after the comma when converting collections to a string.
 */
public class CollectionToStringConverter implements ConditionalGenericConverter, InitializingBean {

    private String delimiter = ", ";

    private final ConversionService conversionService;

    public CollectionToStringConverter( ConversionService conversionService ) {
        this.conversionService = conversionService;

    }

    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
        return Collections.singleton( new ConvertiblePair( Collection.class, String.class ) );
    }

    @Override
    public Object convert( Object source, TypeDescriptor sourceType, TypeDescriptor targetType ) {
        if ( source == null )
            return null;

        StringBuilder sb = new StringBuilder();
        int i = 0;
        for ( Object sourceElement : (Collection<?>) source ) {
            if ( i > 0 )
                sb.append( delimiter );

            sb.append( conversionService.convert( sourceElement,
                                                  sourceType.elementTypeDescriptor( sourceElement ),
                                                  targetType ) );
            i++;
        }

        return sb.toString();
    }

    @Override
    public boolean matches( TypeDescriptor sourceType, TypeDescriptor targetType ) {
        TypeDescriptor sourceElementType = sourceType.getElementTypeDescriptor();
        return targetType == null || sourceElementType == null || conversionService.canConvert( sourceElementType,
                                                                                                targetType )
            || sourceElementType.getType().isAssignableFrom( targetType.getType() );
    }

    public String getDelimiter() {
        return delimiter;
    }

    public void setDelimiter( String delimiter ) {
        this.delimiter = delimiter;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
     

    }
}
