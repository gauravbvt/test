package com.mindalliance.channels.metamodel.beans

import com.mindalliance.channels.nk.bean.BeanReference
import com.mindalliance.channels.nk.bean.BeanList
import com.mindalliance.channels.nk.bean.SimpleData

/**
* Created by IntelliJ IDEA.
* User: jf
* Date: Mar 3, 2008
* Time: 12:16:04 PM
* To change this template use File | Settings | File Templates.
*/
class HelpTopic extends AbstractModelerBean {

    def name = new SimpleData(dataClass: String.class)
    def description = new SimpleData(dataClass: String.class)
    def parent = new BeanReference(beanClass: HelpTopic.class.name, domain:parent_domain)
    def subTopics = new BeanList(calculate:'calculate_subTopics', itemName: 'subTopic')

    public Map getBeanProperties() {
        return [name: name, description: description, parent:parent, subTopics:subTopics];
    }

    void initialize() {
        defaultMetadata [
            subTopic: [hint:'The sub-topics of this topic']
            name: [required: true],
            description: [required: true]
        ]
    }

    // Find the HelpTopics that can be parents of this one
    void parent_domain(def builder)  {
      def project = beanAt(Project.ID)
      List candidates = []
      // Add all HelpTopics that have this one as ancestor
      project.topics.each {beanRef ->
        if (beanRef.id != this.id) {
            List ancestorIds = beanRef.trans('parent').collect {it.id}
            if (!ancestorIds.contains(this.id)) {
                candidates.add(beanRef)
            }
        }
      }
      builder.items {
         candidates.each {
             def topic = it
             builder.item(label: topic.name) {
                 builder.ref {
                     builder.id(topic.id)
                     builder.db(topic.db)
                 }
             }
         }
      }
    }

    // Calculate a list of BeanReferences to HelpTopics that have this as parent
    def calculate_subTopics() {
       def project = beanAt(Project.ID)
       List subs = project.helpTopics.findAll{topic -> topic.parent.id == this.id}
       return subs
    }

}