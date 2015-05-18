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
package de.tud.vcd.votedevice.tuTestElection;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.Icon;

import de.tud.vcd.common.BallotCardDesign;
import de.tud.vcd.votedevice.multielection.VCDModel;
import de.tud.vcd.votedevice.onscreenkeyboard.Searcher;
import de.tud.vcd.votedevice.onscreenkeyboard.VCDSearchable;

public class TUTestElectionModel extends Observable implements VCDModel, VCDSearchable {

	public enum State {
		  INIT, VOTING 
	}
	
	public enum Validity {
		 VALID, INVALID_EMPTY, INVALID_TOOMUCH, INVALID_MANUALINVALID
	}
	
	/*
	 * Sind Sie grundsätzlich zufrieden mit Ihrem BA/MA/Promotions-Betreuer bzw. Vorgesetzten?

A: immer zufrieden
B: meistens zufrieden
C: mal zufrieden mal nicht
D: selten zufrieden
E: nie zufrieden
	 */
	
	public enum VoteState { A, B, C, D, E}
	
	private ArrayList<VoteState> voteState;
	private State state;
	private boolean manualInvalid;
	
	
	public TUTestElectionModel(BallotCardDesign bcd) throws Exception{
		voteState=new ArrayList<TUTestElectionModel.VoteState>();
		state=State.INIT;
		manualInvalid=false;
		this.updateObserver();
	}
	
	public Validity getValidity(){
		//VALID, INVALID_MANUAL,INVALID_TOOMUCHCANDIDATES,INVALID_ONLY_PARTIES, INVALID_EMPTY, VALID_REDUCE_PARTIES, VALID_REDUCE_CANDIDATES, INVALID_TOO_MUCH_VOTES_EACH_CANDIDATE 
		if (manualInvalid==true){
			return Validity.INVALID_MANUALINVALID;
		}else if (voteState.isEmpty()){
			return Validity.INVALID_EMPTY;
		}else if (voteState.size()>1){
			return Validity.INVALID_TOOMUCH;
		}else{
			return Validity.VALID;
		}
		
		
		
	}
	
	public void setState(State state){
		this.state=state;
	}
	
	public void resetBallotCard(){
		
		this.voteState.clear();
		manualInvalid=false;
		state=State.INIT;
		updateObserver();
	}
	
	public boolean isValid(){
		if (manualInvalid==false && voteState.size()==1){
			return true;
		}else {
			return false;
		}
	}
	
//	public ArrayList<RejectReasons> getFails(){
//		return rules.getFails(this);
//	}
	
	
	
	public void addObserver(Observer o) {
		// Wird aus dem Parentobjekt übernommen, aber so ist es übersichtlicher.
		super.addObserver(o);
	}
	
	
	/**
	 * weist die Observer an sich zu aktualisieren.
	 */
	public void updateObserver() {
		setChanged();
		notifyObservers(this);
		//setChanged();
		//notifyObservers(this.ergebnis);
	}

	
	/**
	 * @return the state
	 */
	public State getState() {
		return state;
	}


	

	

	public void search(Searcher s, String str) {
		//Hier Suche implementieren!!! Suche aber sinnlos
//		for(Party p: this.getPartyList()){
//			p.search(s, str);
//			s.searchFor(p, str);
//		}
		
	}

	/**
	 * @return the voteState
	 */
	public ArrayList<VoteState> getVoteStates() {
		return voteState;
	}
	
	/**
	 * @return the voteState
	 */
	public String getVoteStateText(VoteState voteState) {
		switch (voteState) {
		case A:
			return "A: immer zufrieden";
			
		case B:
			return "B: meistens zufrieden";
			
		case C:
			return "C: mal zufrieden mal nicht";
			
		case D:
			return "D: selten zufrieden";
			
		case E:
			return "E: nie zufrieden";
			
		default:
			return "unbekannt";
			
		}
	}

	public ArrayList<VoteState> getAllVoteStates(){
		ArrayList<VoteState> vss=new ArrayList<TUTestElectionModel.VoteState>();
		vss.add(VoteState.A);
		vss.add(VoteState.B);
		vss.add(VoteState.C);
		vss.add(VoteState.D);
		vss.add(VoteState.E);
		return vss;
	}
	
	/**
	 * @param voteState the voteState to set
	 */
	public void setVoteState(VoteState voteState) {
		state=State.VOTING;
		if (!this.voteState.contains(voteState)){
			this.voteState.add(voteState);
		}
		updateObserver();
	}
	
	public void toogleVoteState(VoteState voteState){
		state=State.VOTING;
		if (!this.voteState.contains(voteState)){
			this.voteState.add(voteState);
		}else{
			this.voteState.remove(voteState);
		}
		System.out.println(this.voteState.toString());
		updateObserver();
	}
	
	public void removeVoteState(VoteState vs){
		voteState.remove(vs);
		updateObserver();
		
	}
	
	public boolean containsVoteState(VoteState vs){
		return voteState.contains(vs);
	}

	public Icon createBallotCardReview(int width, int height) {
		TUTestElectionPrintForm imageForm;
		try {
			imageForm = new TUTestElectionPrintForm(width, height, this);
			return imageForm.createImage(Color.WHITE) ;
		} catch (Exception e) {
			return null;
		}
		
	}
	
}
