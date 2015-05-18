package de.tud.vcd.votedevice.timeofvoting;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;




public class VotingTime {

	
	public String WriteFile(String Nachricht) throws IOException{
		 
		Calendar cal = Calendar.getInstance();
		int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
		int Month = cal.get(Calendar.MONTH) + 1;
		
		
        String filename = "Zeiten_"+dayOfMonth+ "-" + Month + ".txt";
		System.out.println(filename);
		//Zeile in die txt datei schreiben
        PrintWriter out = new PrintWriter(new FileWriter(filename, true));
        out.println(Nachricht);
        out.close();

        return (Nachricht);

	}
	
}
