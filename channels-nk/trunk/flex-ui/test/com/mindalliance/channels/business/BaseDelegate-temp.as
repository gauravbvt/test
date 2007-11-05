package com.mindalliance.channels.business
{

	import com.mindalliance.channels.vo.common.ElementVO;
	
	import mx.collections.ArrayCollection;
	import mx.rpc.IResponder;
    
    public class BaseDelegate
    {
    	
    	private static var testData : TestDataXML = new TestDataXML();
    	
    	public static var idCounter : int = 0;
    	
        public var responder:IResponder;
    
        public var typeName : String;
    
        public function BaseDelegate(responder:IResponder)
        {
            this.responder = responder;
        }
        
        public function performQuery(name : String, parameters : Array) : void
        {
        	
            var value : Object = new Object();           
            if (parameters != null) {
                for (var key : String in parameters) { 
                   value[key] = parameters[key];    
                }
                
            }
            var list : XML = <list></list>;
            
            value["data"] = fromXMLElementList(list);
        }
        
        private function performQuery(name : String, parameters : Array) : XMLList {
        	var list : XML = <list></list>;
        	var elements : XMLList;
        	switch(name) {
                case "allOrganizations": 
                                   elements = testData.organization; break;
                case  "allPersons" : elements = testData.person; break;
                case  "allProjects" : elements = testData.project; break;
                case  "allRepositories" : elements = testData.repository; break;
                case  "allRoles" : elements = testData.role; break;
                case  "artifactsInScenario" : elements = testData.artifact.(scenarioId=parameters["scenarioId"]);break;
                case  "categoriesInTaxonomy" : elements= testData.category.(@taxonomy=parameters["taxonomy"]);break;
                case  "categoriesInTaxonomyAndDiscipline" : elements=testData.category.(@taxonomy=parameters["taxonomy"] && disciplines.categoryId=parameters["disciplineId"]);break;
                case  "categoriesOfElement": elements=testData.
            }
            
        }
        
        public function getElement(desiredID : String) : void {
        	var XMLList list = testData[typeName].(id==desiredID);
        	var value : Object = new Object();
            value["id"] = desiredID;
        	if (list.length > 0) {
        	   	value["data"] = delegate.fromXML(list[0]);
        	}
            responder.result(value);
        }
        
        public function deleteElement(desiredID : String, parameters : Array = null) : void {
        	var XMLList list = testData[typeName].(id==desiredID);
        	if (list.length > 0) {
        	   responder.fault("Delete failed");
        	   return;	
        	}
            var value : Object = new Object();
            if (parameters == null) {           
              parameters = new Array();
            }
            parameters["id"] = desiredID;
            for (var key : String in parameters) {
                value[key] = parameters[key];
            }
            value["data"] = true;
            delete  testData[typeName].(id==desiredID);
            responder.result(value);
        }
        
        public function createElement(doc : XML, parameters : Array = null) : void{
        	var value : Object = new Object();
            if (parameters == null) {           
              parameters = new Array();
            }
            for (var key : String in parameters) {
                value[key] = parameters[key];
            }
            var idNode : XML = <id></id>;
            idNode.appendChild(idCounter++);
        	doc.appendChild(idNode);
        	testData.appendChild(doc);
        	value["data"] = true;
        	responder.result(value);
        	
        }
        
        public function updateElement(obj : ElementVO) : void {           
        	var value : Object = new Object();
            value["id"] = obj.id;
            
            delete  testData[typeName].(id==obj.id);
            testData.appendChild(toXML(obj));
            responder.result(value);  
        }

        
        /**
         * Extending delegates should override this method to parse XML
         * into the appropriate Value Object.
         */
        public function fromXML(results : XML) : ElementVO {
            return null;
        }
        /**
         * Extending delegates should override this method to generate XML
         * from the passed in Value Object.
         */
        public function toXML(obj : ElementVO) : XML {
            return null;
        }
        public function fromXMLElementList(list : XML) : ArrayCollection {
            var results : ArrayCollection = new ArrayCollection();
            for each (var el : XML in list.elements(typeName)) {
                results.addItem(new ElementVO(el.id, el.name)); 
            }
            return results; 
        }
        

    }
}