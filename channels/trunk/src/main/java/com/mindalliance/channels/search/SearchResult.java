package com.mindalliance.channels.search;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.Hits;

import java.util.*;

/**
 * A SearchResult contains the information from a search hit Document that is 
 * needed to display search results to a user and navigate to those results on the desktop. 
 * 
 * @author ebax
 *
 */

public class SearchResult {
	public String guid;
	public String name;
	public String kind;
	public String project;
	public String projectGuid;
	
	public SearchResult(String guid, String name, String kind, String project, String projectGuid) {
		this.guid = guid;
		this.name = name;
		this.kind = kind;
		this.project = project;
		this.projectGuid = projectGuid;
	}
	
	public SearchResult(Document d) {
		this(d.get("guid"), d.get("name"), d.get("kind"), d.get("project"), d.get("projectGuid"));
	}
	
	public String getGuid() {return guid;}
	public String getName() {return name;}
	public String getKind() {return kind;}
	public String getProject() {return project;}
	public String getProjectGuid() {return projectGuid;}
	
	public static List<SearchResult> convert(Hits hits, int maxResults) {
		ArrayList<SearchResult> out = new ArrayList<SearchResult>();
		
		for (int i=0; i<maxResults && i<hits.length(); i++) {
			try {out.add(new SearchResult(hits.doc(i)));}
			catch (Exception trouble) {trouble.printStackTrace();}
		}
		
		return out;
	}
	
	public static List<SearchResult> test(int n) {
		ArrayList<SearchResult> out = new ArrayList<SearchResult>();
		
		for (int i=0; i<n; i++) {
			out.add(new SearchResult("guid" + i, "name" + i, "kind" + i, "project" + i, "projectGuid" + i));
		}
		
		return out;
	}
}
