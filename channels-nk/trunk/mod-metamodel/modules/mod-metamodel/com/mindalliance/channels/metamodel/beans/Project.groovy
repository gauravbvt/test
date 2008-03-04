package com.mindalliance.channels.metamodel.beans

import com.mindalliance.channels.nk.bean.BeanReference
import com.mindalliance.channels.nk.bean.BeanList
import com.mindalliance.channels.metamodel.beans.environment.Environment
import com.mindalliance.channels.metamodel.beans.scenario.Scenario
import com.mindalliance.channels.metamodel.beans.process.IFMProcess
import com.mindalliance.channels.metamodel.beans.model.Model
import com.mindalliance.channels.nk.aspects.PersistentBeanAspect
import com.mindalliance.channels.nk.Action
import com.mindalliance.channels.nk.bean.SimpleData

// Top-most root bean
class Project extends AbstractModelerBean {

    public static final String ID = "PROJECT"

    def name = new SimpleData(dataClass: String.class)
    def description = new SimpleData(dataClass: String.class)
    BeanList environments = new BeanList(itemPrototype: new BeanReference(beanClass: Environment.class.name),
            itemName: 'environment')
    BeanList scenarios = new BeanList(itemPrototype: new BeanReference(beanClass: Scenario.class.name),
            itemName: 'scenario')
    BeanList models = new BeanList(itemPrototype: new BeanReference(beanClass: Model.class.name),
            itemName: 'model')
    BeanList helpTopics = new BeanList(itemPrototype: new BeanReference(beanClass: HelpTopic.class.name),
            itemName: 'topic')
    BeanReference process = new BeanReference(beanClass: IFMProcess.class.name, id: IFMProcess.ID) // constant bean reference

    public Map getBeanProperties() {
        return [name:name, description:description, environments: environments, scenarios: scenarios, models: models, process: process, helpTopics: helpTopics];
    }

    void initialize() {
        defaultMetadata = [
            name: [required: true],
            description: [required: true]
        ]
    }

    // Actions

    List getActions() {// DEFAULT
        List actions = super.getActions()
        Action addHelpTopic = new Action(name: 'addHelpTopic', label: 'Add topic', hint: 'Adds a new help topic')
        actions.add[addHelpTopic]
        return actions
    }

    void addHelpTopic(String step, Map c10nState) {
        switch (step) {
            case 'start':
                HelpTopic topic = new HelpTopic(id: context.makeGUID())
                topic.name.value = 'NO NAME'
                topic.name.description = 'NO DESCRIPTION'
                c10nState['editedBean'] = new PersistentBeanAspect(topic)
                break
            case 'commit':
                HelpTopic topic = c10nState['editedBean']
                this.helpTopics.addItem([id: topic.id, db: topic.db])
                break
            case 'abort': break // do nothing
            default: throw new IllegalArgumentException("Unsupported step $step for action addHelpTopic")
        }
    }

}