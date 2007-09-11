package com.mindalliance.channels.view.common
{
	import com.mindalliance.channels.model.EditorModel;
	
	public interface IEditor
	{
		function get model() : EditorModel;
		function set model(model : EditorModel) : void;
	}
}