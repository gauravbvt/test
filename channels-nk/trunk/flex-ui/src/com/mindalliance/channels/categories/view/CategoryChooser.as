package com.mindalliance.channels.categories.view
{
	import com.mindalliance.channels.categories.events.GetCategoryListByDisciplineEvent;
	import com.mindalliance.channels.categories.events.GetCategoryListEvent;
	import com.mindalliance.channels.common.events.GetElementEvent;
	import com.mindalliance.channels.model.ElementListNames;
	import com.mindalliance.channels.util.CairngormHelper;
	import com.mindalliance.channels.common.view.Chooser;
	import com.mindalliance.channels.vo.common.ElementVO;

	public class CategoryChooser extends Chooser
	{
		private var disciplineSelector : DisciplineSelector;
		[Bindable] public var taxonomy : String;
		
		public function CategoryChooser()
		{
			super();
			elementName="Categories";
			editor=new CategoryChooserViewer();
		}
		
		override protected function init() : void {
		  disciplineSelector = new DisciplineSelector();
		  disciplineSelector.taxonomy = taxonomy;
		  disciplineSelector.taxonomyEditable = false;
		  disciplineSelector.changeFunction = selectDiscipline;
		  this.chooserRoot.addChildAt(disciplineSelector, 0);
		  this.title='Choose categories (Taxonomy: ' + taxonomy + ')';
          super.init();
		}
		
		override protected function populateList() : void {
			selectDiscipline(disciplineSelector.taxonomy, disciplineSelector.discipline);
		}
		
		override protected function populateElement(id : String) : void {
            CairngormHelper.fireEvent( new GetElementEvent(id, model.editorModel));
        }
		private function selectDiscipline(taxonomy : String, discipline : ElementVO) : void {
                
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