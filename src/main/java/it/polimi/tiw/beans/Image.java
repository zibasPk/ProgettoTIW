package it.polimi.tiw.beans;

import java.io.Serializable;
import java.util.Date;

public class Image implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private int id;
	private String title;
	private Date date;
	private String description;
	private String path;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	
	public String toString() {
		return " id: " + id + " title: " + title + " date: " + date.toString();
	}
	
}
