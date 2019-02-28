package com.google.battle.project;

public class Transition {

	
	public boolean full = false;
	Slide slide1;
	Slide slide2;
	
//	Photo photo1;
//	Photo photo2;

	public Transition() {
	}
	
	public Transition(Photo photo) {
		slide1 = new Slide(photo);
	}

	public void addPhoto(Photo photo) {
		if(!slide1.horizontal && !photo.horizontal)
			slide1.addSecondPhoto(photo);
		else if(!slide2.horizontal && !photo.horizontal)
			slide2.addSecondPhoto(photo);
			
	}
	
	public boolean isFull() {
		return slide1 != null && slide2 != null && slide1.isFull() && slide2.isFull();
	}
}
