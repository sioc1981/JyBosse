package com.google.battle.project;

import java.util.ArrayList;
import java.util.List;

public class SlideShow {

	List<Transition> transitions = new ArrayList<Transition>();
	
	public SlideShow() {
		Transition tr = new Transition();
		transitions.add(tr);
	}
	
	public void addPhoto(Photo photo) {
		for (Transition transition : transitions) {
			if(!transition.isFull()) {
				transition.addPhoto(photo);
				break;
			}
		}
	}

	public void processLine(String line) {
		Photo photo = new Photo(line,1);
		addPhoto(photo);
	}
}
