package edu.erau.holdens.moocmining.sandbox;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import edu.erau.holdens.moocmining.DiscussionEntry;
import edu.erau.holdens.moocmining.MainStuff;

public class ExcelReadSandbox {

	public static void main(String[] args) throws FileNotFoundException, IOException {

		
		HashMap<Integer, DiscussionEntry> map = MainStuff.createTranscriptsMap();

		
		for (DiscussionEntry d : map.values()){
			System.out.println(d.toString());
		}

	}

}
