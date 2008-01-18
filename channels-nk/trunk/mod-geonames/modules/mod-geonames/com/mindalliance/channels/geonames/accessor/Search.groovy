package com.mindalliance.channels.geonames.accessor

import org.ten60.netkernel.layer1.nkf.INKFConvenienceHelper as Context

import com.mindalliance.channels.nk.NetKernelCategory
import com.mindalliance.channels.nk.accessors.AbstractAccessor
import org.geonames.FeatureClass
import org.geonames.Style
import org.geonames.ToponymSearchCriteria
import org.geonames.WebService

/**
*
*/
class Search extends AbstractAccessor {
    static def ACCESSOR_PARAMS = [
            "name": String,
            "nameEquals": String,
            "nameStartsWith": String,
            "adminCode1": String,
            "adminCode2": String,
            "adminCode3": String,
            "adminCode4": String,
            "countryCode": String,
            "language": String,
            "q": String,
            "tag": String,
            "featureClass": FeatureClass,
            "style": Style,
            "maxRows": Integer,
            "startRow": Integer
    ]

    protected void source(Context context) {
        use(NetKernelCategory) {
            def searchArgs = new ToponymSearchCriteria()

            ACCESSOR_PARAMS.each {k, v ->
                if (context.params."${k}?") {
                    searchArgs."${k}" = v.valueOf(context.params."${k}")
                }
            }                                                   

            def result = xmlAspect {xml ->
                xml.list {
                    def results = WebService.search(searchArgs).toponyms
                    results.each { t ->
                        xml.location {
                            xml.id(t.geonameId)
                            xml.name(t.name)
                        }
                    }
                }
            }

            context.respond(result, "text/xml", false)
        }

    }

}