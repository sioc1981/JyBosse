package com.google.battle.project;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Engine {

	public static void main(String agrs[]) throws FileNotFoundException, IOException {
		// 1 photo
		SlideShow ss = new SlideShow();
		
		boolean firstRead =  false;
		
		try (BufferedReader br = new BufferedReader(new FileReader(new File("a_example.txt")))) {
		    String line;
		    while ((line = br.readLine()) != null) {
		    	if(firstRead)
		    	ss.processLine(line);
		    	else
		    		firstRead = true;
		    }
		}
	}
}
