package de.tud.vcd.votedevice.votecastingmanipulation;

import java.math.BigInteger;
import java.security.SecureRandom;

public class Expo{

	long before, after, time, average;
	
	
	public void exponent(){
		average = 0;
	  
	  for(int i = 0; i < 100; i++){
		SecureRandom r = new SecureRandom();
		BigInteger e = new BigInteger(256, 1, r);
		BigInteger m = new BigInteger(2048, 1, r);
		BigInteger z = new BigInteger(1024, 1, r);
		
		before = System.currentTimeMillis();
		z = z.modPow(e, m);
		after = System.currentTimeMillis();
		time = (after - before);
		System.out.println(" " + time);
		average = average + time;
	 }
	  
	  System.out.println("Durchschnitt: " + (average / 100));
   }	  
  
	
	public static void main(String[] args) {
		  Expo e = new Expo();
		  e.exponent();
	}
}