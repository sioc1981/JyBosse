package com.google.battle.plop;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Hello world!
 *
 */
public class App 
{
	private static final String[] fileList = new String [] {
			"a_example.txt",
			"b_lovely_landscapes.txt",
			"c_memorable_moments.txt",
			"d_pet_pictures.txt",
			"e_shiny_selfies.txt"
	};
    public static void main( String[] args )
    {
    	for (String fileIn : fileList) {
       	ClassLoader classLoader = App.class.getClassLoader();
    	File file = new File(classLoader.getResource(fileIn).getFile());
        Charset charset = Charset.forName("ISO-8859-1");
        try {
          StringBuilder sb = new StringBuilder();
          List<String> lines = Files.readAllLines(file.toPath(), charset);
          int count = 0;
          int lineNr = 0;
          boolean skipfirst = true;
          int vBuffer = -1;
          for (String line : lines) {
        	  if (skipfirst) {
        		  skipfirst = false;
        		  continue;
        	  }
        	  if (line.startsWith("H")) {
        		  sb.append(lineNr);
        		  sb.append("\n");
        		  ++count;
        	  } else { //V
        		  if(vBuffer == -1)
        			  vBuffer = lineNr;
        		  else {
        			  sb.append(vBuffer + " " + lineNr);
            		  sb.append("\n");
            		  ++count;
        			  vBuffer = -1;
        		  }
        	  }
        	  ++lineNr;
          }
          Files.writeString(Paths.get(fileIn + ".out"), count + "\n" + sb.toString());
          
        } catch (IOException e) {
          System.out.println(e);
        }
    	}

    }
}
