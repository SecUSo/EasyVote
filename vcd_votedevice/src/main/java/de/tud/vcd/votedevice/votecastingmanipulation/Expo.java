/*******************************************************************************
 * #  Copyright 2015 SecUSo.org / Jurlind Budurushi / Roman Jöris
 * #
 * #  Licensed under the Apache License, Version 2.0 (the "License");
 * #  you may not use this file except in compliance with the License.
 * #  You may obtain a copy of the License at
 * #
 * #      http://www.apache.org/licenses/LICENSE-2.0
 * #
 * #  Unless required by applicable law or agreed to in writing, software
 * #  distributed under the License is distributed on an "AS IS" BASIS,
 * #  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * #  See the License for the specific language governing permissions and
 * #  limitations under the License.
 *******************************************************************************/
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