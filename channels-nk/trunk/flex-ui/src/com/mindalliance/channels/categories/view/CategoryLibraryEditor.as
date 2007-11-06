package com.mindalliance.channels.categories.view
{
	import com.mindalliance.channels.categories.events.GetCategoryListByDisciplineEvent;
	import com.mindalliance.channels.categories.events.GetCategoryListEvent;
	import com.mindalliance.channels.common.events.GetElementEvent;
	import com.mindalliance.channels.model.ElementListNames;
	import com.mindalliance.channels.util.CairngormHelper;
	import com.mindalliance.channels.common.view.Chooser;
	import com.mindalliance.channels.vo.common.ElementVO;

	public class CategoryLibraryEditor extends Chooser
	{
		
		private var disciplineSelector : DisciplineSelector;
		
		public function CategoryLibraryEditor()
		{
			super();
            elementName="Categories";
            editor=new CategoryEditor();
		}
		override protected function init() : void {
			  disciplineSelector = new DisciplineSelector();
			  disciplineSelector.taxonomyEditable = true;
			  disciplineSelector.changeFunction = selectDiscipline;
			  this.chooserRoot.addChildAt(disciplineSelector, 0);
			  super.init();
        }
        override protected function populateList() : void {
            selectDiscipline(disciplineSelector.taxonomy, disciplineSelector.discipline);
        }
        override protected function populateElement(id : String) : void {
            CairngormHelper.fireEvent( new GetElementEvent(id, model.editorModel));
        }
        private function selectDiscipline(taxonomy : String, discipline : ElementVO) : void {
            if (taxonomy != null) {    
                if (discipline != null && discipline.id != '<All>') {
                    
                    var disciplineId : String  = discipline.id;
                    elementListKey = ElementListNames.CATEGORY_LIST_KEY + taxonomy + disciplineId;
                    CairngormHelper.fireEvent(new GetCategoryListByDisciplineEvent(taxonomy, disciplineId));
                    
                    
                } else {
                    elementListKey =  ElementListNames.CATEGORY_LIST_KEY + taxonomy;  
                    CairngormHelper.fireEvent(new GetCategoryListEvent(taxonomy));
                }
            }
        }
	}
}