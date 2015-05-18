package de.tud.vcd.eVotingTallyAssistance.gui.tallyGui;

import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;

/**
 * Fortschrittsanzeige. Schreibt auf die TallyGUI die n�chsten Schritte, die m�glich sind, um den Benutzer ein wenig zu leiten. Ebenso
 * wird anhand der Fortschrittsanzeige gezeigt, ob der Vorgang schon abgeschlossen ist oder noch nicht.
 * 
 * @author Roman J�ris <roman.joeris@googlemail.com>
 * 
 */
public class ControlPaneProgress extends ControlPane {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Interen States die m�glich sind, mit dem dazugeh�rigem Text und der Prozentangabe wieviel die Fortschrittsanzeige gef�llt ist.
	 * F�r diese interne Klasse sind zudem noch einige Getter registriert, um die Daten auslesen zu k�nnen.
	 * @author Roman J�ris <roman.joeris@googlemail.com>
	 *
	 */
	public enum States {
	    INIT(100, "Bitte stellen Sie die Gr��e entsprechend dem abgebildeten Musterwahlzettels ein. Seien Sie sorgf�ltig. Die Gr��e kann im weiteren Verlauf nicht mehr ge�ndert werden. Best�tigen Sie anschlie�end die Gr��e, um fortzufahren."), 
	    WAITING(0, "Sie k�nnen einen Wahlzettel scannen, einen bisherigen Laden, das Programm sperren oder zum Protokolldruck gehen."), 
	    SCANNED(66,"Der angeforderte Wahlzettel wird nun angezeigt. Sollte er nicht korrekt sein, so bearbeiten Sie ihn mit \"Wahlzettel editieren\". Um ihn zu speichern klicken Sie auf \"Wahlzettel speichern\"."), 
	    EDIT(33,"Der Wahlzettel kann nun editiert werden. Mit einem rechten Mausklick auf den Namen kann zudem die Kandidatennummer editiert werden."), 
	    READONLY(100, "Im Lesemodus k�nnen Wahlzettel jediglich geladen und wieder verworfen werden. Ein �ndern oder speichern ist nicht m�glich."),
	    UNDEFINED(0,"unbestimmt!!!");
	    
	    private String text;
	    private int value;
	    
	    private States(int value, String text ) {
	        this.text=text;
	        this.value=value;
	      }
	    
	    public String getText() {
	        return text;
	     }
	    public int getValue(){
	    	return value;
	    }
	}
	
	JTextArea statusText;
	JProgressBar progressBar;
	
	/**
	 * Erzeugt das Feld mit dem �bergebenen Namen als �berschrift
	 * @param name
	 */
	public ControlPaneProgress(String name) {
		super(name);
		int breiteControl=235;
		setPreferredSize(new Dimension(breiteControl,200));
		setMaximumSize(new Dimension(breiteControl,1000));
		setMinimumSize(new Dimension(breiteControl,30));
		
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		add(Box.createRigidArea(new Dimension(0,10)));
		
		progressBar= new JProgressBar(0,100);
		
		//progressBar.set
		addNewComponent("progressBar", progressBar);
		statusText=new JTextArea(""); 
		statusText.setBackground(getBackground());
		statusText.setLineWrap(true);
		statusText.setWrapStyleWord(true);
		statusText.setPreferredSize(new Dimension(200, 400));
		addNewComponent("statusText", statusText );
		
		add(Box.createVerticalGlue());
		
		//Initialisierungszustand setzen, bevor ein Update kommt
		setStatus(States.UNDEFINED);
		
	}

	/**
	 * Setzt den Status um, indem es die Fortschrittsanzeige aktualisiert und den Statustext setzt.
	 * @param status States intere Klasse f�r den Status
	 */
	public void setStatus(States status){
		statusText.setText(status.getText());
		progressBar.setValue(status.getValue());
		progressBar.setToolTipText(status.getText());
		
	}

}
