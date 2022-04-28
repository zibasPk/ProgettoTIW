package it.polimi.tiw.beans;

import java.io.Serializable;
import java.util.Date;

public class Album implements Serializable {
	private static final long serialVersionUID = 1L;
	int id;
	String title;
	int ownerID;
	Date creationDate;
	
	public int getID() {
		return id;
	}
	public void setID(int id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getOwnerID() {
		return ownerID;
	}
	public void setOwnerID(int ownerID) {
		this.ownerID = ownerID;
	}
	public Date getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	
	public String toString() {
		return "\nid: " + id + "title: " + title + "creation date: " + creationDate;
	}
}
