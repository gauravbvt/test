package com.mindalliance.channels.nk.bean
/**
 * Created by IntelliJ IDEA.
 * User: jf
 * Date: Feb 4, 2008
 * Time: 1:22:23 PM
 */
class BeanDomain implements IBeanDomain {

    public static final BeanDomain UNDEFINED = new BeanDomain()

    String id  // optional, determined from context if not set
    String db // ditto 
    Map args = [:]    // must contain only literals
    /* Must eval to a closure {bean, args, builder -> ...}  that returns
        <beans>
            <bean id="..." db="...">label</bean>
            ...
        </beans>

        Note that the element names can vary, but not the attribute names
    */
    String query // the name of the query that produces <beans><bean id="someID" db="someDB">label</bean>...</beans>

    boolean isDefined() {
       return query != null
    }

    String toString() {
        return URLEncoder.encode("${id ?: '_'},${db ?: '_'},${mapAsString(args)},${query ?: '_'}", 'UTF-8')
    }

    String mapAsString(Map map) {
        if (map.size() == 0) return '_'
        else {
            String s = '['
            map.each{key,val ->
                s += "$key:$val,"}
            s += ']'
            return s
        }
    }

    static BeanDomain fromString(String encoded) {
        String s = URLDecoder.decode(encoded, 'UTF-8')
        BeanDomain domain = new BeanDomain()
        List vals = s.tokenize(',')
        if (vals[0] != '_') domain.id = vals[0]
        if (vals[1] != '_') domain.db = vals[1]
        domain.args = (vals[2] != '_') ? (Map)Eval.me(vals[2]) : [:]
        if (vals[3] != '_') domain.query = vals[3]
        return domain
    }
}