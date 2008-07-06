package com.mindalliance.channels.playbook.ifm.definition

import com.mindalliance.channels.playbook.ref.Bean
import com.mindalliance.channels.playbook.ifm.playbook.InformationAct
import com.mindalliance.channels.playbook.ref.impl.BeanImpl
import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ref.Referenceable
import com.mindalliance.channels.playbook.ifm.Described
import com.mindalliance.channels.playbook.support.RefUtils

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 19, 2008
 * Time: 9:38:48 AM

 A matching domain defined by extension (enumeration of Referenceables) and/or intension (alternate definitions)

When negated:
    Succeeds if
        specification dose not match all, and
        enumeration does not contains (reference to) bean and no description matches bean

   When affirmed:
    Succeeds if
        specification matches all, or
        enumeration contains (a reference to) to matched bean or at least one description matches bean

*/
abstract class Specification extends BeanImpl implements MatchingDomain, Described {

    String description  = '' // a brief description of the specification
    boolean negated = false  // if negated, the matching domain is the complement of the specification
    List<Ref> enumeration = []   // specification by enumeration
    List<Definition> definitions = [] // OR by definitions (ORed)


    abstract Class<? extends Bean> getMatchingDomainClass()

    String toString() {
        return "${this.class.name}: $description"
    }

    // BEANIMPL

    @Override
    List<String> transientProperties() {
        return (List<String>)(super.transientProperties() +
                                ['enumerable', 'enumerated', 'defined', 'matchingDomainClass', 'summary'])
    }

    // end BEANIMPL

    // DEFINITION

    boolean matchesAll() {  // undefined matches any instance of instance class
        return !isEnumerated() && !isDefined() && !negated
    }

    MatchResult match(Bean bean, InformationAct informationAct) {
        if (!bean) throw new IllegalArgumentException("Can't match null")
        if (!this.matchingDomainClass.isAssignableFrom(bean.class))
            throw new IllegalArgumentException("Can't match bean: expecting a ${this.matchingDomainClass.name}")
        if (negated)
            return matchNegated(bean, informationAct)
        else
            return matchAffirmed(bean, informationAct)
    }

    MatchResult fullMatch(Bean bean, InformationAct informationAct) { // keep matching after failure (gather all failures)
        if (!bean) throw new IllegalArgumentException("Can't match null")
        if (!this.matchingDomainClass.isAssignableFrom(bean.class))
            throw new IllegalArgumentException("Can't match bean: expecting a ${this.matchingDomainClass.name}")
        if (negated)
            return matchFullNegated(bean, informationAct)
        else
            return matchFullAffirmed(bean, informationAct)
    }

    boolean matches(Bean bean, InformationAct informationAct) {
        MatchResult result = match(bean, informationAct)
        return result.matched;
    }

    // A narrows B if and only if any instance matching A also matches B (A defines an equal or smaller matching domain)
    // iow: A's matching domain is a subset of B's matching domain
    boolean narrows(MatchingDomain matchingDomain) {
        if (matchingDomain.class != this.class) throw new IllegalArgumentException("Expecting a ${this.class.name}")
        Specification specification = (Specification)matchingDomain
        if (matchesAll() && specification.matchesAll()) return true // both define a universal matching domain
        if (negated != specification.negated) return false
        if (negated)
            return narrowsNegated(specification)
        else
            return narrowsAffirmed(specification)
    }

    // END DEFINITION

    boolean isEnumerated() {
        return !enumeration.isEmpty()
    }

    boolean isDefined() {
        return !definitions.isEmpty()
    }

    // PRIVATE

    private MatchResult matchAffirmed(Bean bean, InformationAct informationAct) {
        if (matchesAll()) return new MatchResult(matched: true, successes:["Affirmed universal domain matches anything"])
        if (isEnumerated())  {
          if  (enumeration.contains(((Referenceable)bean).reference))
                    return new MatchResult(matched:true, successes: ["Matched enumeration in affirmed specification"])
        }
        if (isDefined()) {
            Definition definition = (Definition)definitions.find {d -> d.matches(bean, informationAct)}
            if (definition) {
                MatchResult result = new MatchResult(matched:true, successes:["Matched definition (${definition.description}) in affirmed specification"])
                return result
            }
        }
        return new MatchResult(matched: false, failures:["Unmatched affirmed specification"])
    }

    private MatchResult matchNegated(Bean bean, InformationAct informationAct) {
        if (matchesAll()) return new MatchResult(matched: false, failures:["Negated universal domain matches nothing"])
        if (isEnumerated())  { // if bean referenced -> failure
            if  (enumeration.contains(((Referenceable)bean).reference))
                return new MatchResult(matched:false, failures:["Matched enumeration in negated specification"])
        }
        if (isDefined()) { // if bean matches a definition -> failure
            Definition definition = (Definition)definitions.find {d -> d.matches(bean, informationAct)}
            if (definition) {
                MatchResult result = new MatchResult(matched:false, failures:["Matched definition (${definition.description}) in negated specification"])
                return result
            }
        }
        return new MatchResult(matched: true, successes:["Unmatched negated specification"])
    }

    private MatchResult matchFullAffirmed(Bean bean, InformationAct informationAct) {
        MatchResult result = new MatchResult(matched:false)
        if (matchesAll()) {
            result.matched = true
            result.successes.add("Affirmed universal domain matches anything")
            return result
        }
        if (isEnumerated())  {
          if  (enumeration.contains(((Referenceable)bean).reference))  {
              result.matched = true
              result.successes.add("Same as ${((Referenceable)bean).reference} in enumeration of affirmed specification")
          }
        }
        if (isDefined()) {
            definitions.each {d ->
                MatchResult defResult = d.match(bean, informationAct)
                if (defResult.matched) {
                    result.matched = true
                    result.successes.add("Matched description (${d.description}) in affirmed specification:\n${defResult}")
                }
                else {
                    result.failures.add("Did not match description (${d.description}) in affirmed specification:\n${defResult}")
                }
            }
        }
        return result
    }

    private MatchResult matchFullNegated(Bean bean, InformationAct informationAct) {
        MatchResult result = new MatchResult(matched:true)
        if (matchesAll()) {
            result.matched = false
            result.failures.add("Negated universal domain matches nothing")
            return result
        }
        if (isEnumerated())  { // if bean referenced -> failure
            if  (enumeration.contains(((Referenceable)bean).reference)) {
                result.matched = false
                result.failures.add("${((Referenceable)bean).reference} in enumeration of negated specification")
            }
        }
        if (isDefined()) { // if bean described -> failure
            definitions.each {d ->
                MatchResult defResult = d.matches(bean, informationAct)
                if (defResult.matched) {
                    result.matched = false
                    result.failures.add("Matched description (${d.description}) in negated specification:\n${defResult}")
                }
                else {
                    result.successes.add("Did not match description (${d.description}) in negated specification:\n${defResult}")
                }
            }
        }
        return result
    }

    /*
        (not A) subset of (not B) <=> B subset A
    */
    private boolean narrowsNegated(Specification specification) {
        return specification.narrowsAffirmed(this)
    }

    /*
        Narrows specification if
            enumeration is a subset
            AND all definitions narrow a specification's description  (the described matching domain is a subset of specification's described matching domain)
     */
    private boolean narrowsAffirmed(Specification specification) {
        if (specification.matchesAll()) return true // specification "defines" a universal matching domain
        if (!enumeration.minus(specification.enumeration).isEmpty()) return false // if narrowing specification's enumeration is not a subset, then it is not narrowing
        if (!definitions.every {d -> specification.definitions.any {other -> d.narrows(other)}}) return false
        return true
    }

    String getSummary() {
      return RefUtils.summarize(description ?: 'No description', MAX_SUMMARY_SIZE);
    }
}