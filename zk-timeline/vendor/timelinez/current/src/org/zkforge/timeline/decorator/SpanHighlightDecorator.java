package org.zkforge.timeline.decorator;

import java.util.Date;

import org.json.simple.JSONObject;

public class SpanHighlightDecorator implements HighlightDecorator {

	private static int count = 0;

	private int _id = count++;

	private Date _startDate;

	private Date _endDate;

	private String _startLabel = "shot";

	private String _endLabel = "t.o.d";

	private String _color = "#FFC080";

	private int _opacity = 50;

	public SpanHighlightDecorator(Date start, Date end) {
		_startDate = start;
		_endDate = end;
	}

	public String getColor() {
		return _color;
	}

	public void setColor(String color) {
		this._color = color;
	}

	public int getOpacity() {
		return _opacity;
	}

	public void setOpacity(int opacity) {
		this._opacity = opacity;
	}

//	@Override
	public String toString() {
		// TODO Auto-generated method stub
		JSONObject json = new JSONObject();
		json.put("id", new Integer(_id));
		json.put("HighlightDecoratorName", "SpanHighlightDecorator");
		json.put("startDate", getStartDate().toString());
		json.put("endDate", getEndDate().toString());
		json.put("startLabel", getStartLabel());
		json.put("endLabel", getEndLabel());
		json.put("color", getColor());
		json.put("opacity", new Integer(getOpacity()));

		return json.toString();
	}

	public Date getEndDate() {
		return _endDate;
	}

	public void setEndDate(Date endDate) {
		this._endDate = endDate;
	}

	public String getEndLabel() {
		return _endLabel;
	}

	public void setEndLabel(String endLabel) {
		this._endLabel = endLabel;
	}

	public Date getStartDate() {
		return _startDate;
	}

	public void setStartDate(Date startDate) {
		this._startDate = startDate;
	}

	public String getStartLabel() {
		return _startLabel;
	}

	public void setStartLabel(String startLabel) {
		this._startLabel = startLabel;
	}

	public int getId() {
		// TODO Auto-generated method stub
		return _id;
	}
}
