package it.polimi.tiw.beans;

import java.io.Serializable;

public class Comment implements Serializable{
	private static final long serialVersionUID = 1L;
	
	int id;
	int userID;
	int imageID;
	String text;
	
	
	public int getID() {
		return id;
	}
	public void setID(int id) {
		this.id = id;
	}
	public int getUserID() {
		return userID;
	}
	public void setUserID(int userID) {
		this.userID = userID;
	}
	public int getImageID() {
		return imageID;
	}
	public void setImageID(int imageID) {
		this.imageID = imageID;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	
	public String toString() {
		return "\ncomment: " + id + "text: " + text;
	}
}
