package org.zkforge.timeline.decorator;

import java.util.Date;

import org.json.simple.JSONObject;

public class PointHighlightDecorator implements HighlightDecorator {
	private static int count = 0;

	private int _id = count++;

	private Date _date;

	private String _color = "#FFC080";

	private int _opacity = 50;

	public PointHighlightDecorator(Date date) {
		_date = date;

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
		json.put("HighlightDecoratorName", "PointHighlightDecorator");
		json.put("date", getDate().toString());
		json.put("color", getColor());
		json.put("opacity", new Integer(getOpacity()));

		return json.toString();
	}

	public Date getDate() {
		return _date;
	}

	public void setDate(Date date) {
		this._date = date;
	}

	public int getId() {
		// TODO Auto-generated method stub
		return _id;
	}
}
