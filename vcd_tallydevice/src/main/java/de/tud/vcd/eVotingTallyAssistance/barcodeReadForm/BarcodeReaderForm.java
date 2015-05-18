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
/**
 * 
 */
package de.tud.vcd.eVotingTallyAssistance.barcodeReadForm;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * @author Roman Jöris <roman.joeris@googlemail.com>
 *
 */
public class BarcodeReaderForm extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JTextField txtName1;
	
	private String barcode="";
	
	/**
	 * 
	 */
	public BarcodeReaderForm(JFrame parentframe) {
		setTitle("Barcode einlesen...");
		setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		
		setBounds(100, 100, 527, 333);
		
		getContentPane().setLayout (new BorderLayout());
		  
		  Box box = Box.createVerticalBox();
		  JLabel label = new JLabel("Bitte Scannen Sie nun den Barcode.");
		  box.add(label);
		  label.setAlignmentY(Component.LEFT_ALIGNMENT);
		  label.setAlignmentX(Component.LEFT_ALIGNMENT);
		  
		  JLabel label2 = new JLabel("Geben Sie währenddessen keine weiteren Tastaturdaten ein.");
		  box.add(label2);
		  label2.setAlignmentY(Component.LEFT_ALIGNMENT);
		  label2.setAlignmentX(Component.LEFT_ALIGNMENT);
		  
		  box.add(Box.createVerticalStrut(5));
		  
		  txtName1 = new JTextField();
			//box.add(txtName1);
			txtName1.setColumns(3);
		  
		  
			JPanel panel = new JPanel();
			
			
			URL imageURLl = getClass().getClassLoader().getResource("barcodescanner.jpg");
			ImageIcon ico = null;
			if (imageURLl != null) {
				try {
					ico = new ImageIcon(ImageIO.read(imageURLl));
					
					ico.setImage(ico.getImage().getScaledInstance(200,200,Image.SCALE_DEFAULT)); 
					
			        
			        panel.add(new JLabel(ico));	
					box.add(panel);
				} catch (IOException e) {
					//e.printStackTrace();
				}
			}
			
			
			
		  box.setBorder(BorderFactory.createEmptyBorder(5,15,5,15));
		  getContentPane().add(box, BorderLayout.CENTER);
		  
		  
		  
		  box = Box.createHorizontalBox();
		  box.add(Box.createHorizontalGlue());
		  //JButton okButton = new JButton("Ok");
		 // getRootPane().setDefaultButton(okButton);
		  //box.add(okButton);
		  box.add(Box.createHorizontalStrut(5));
		  
		  JButton abortButton = new JButton("Abbrechen");
			 // getRootPane().setDefaultButton(okButton);
		  box.add(abortButton);
		  abortButton.setFocusable(false);
		  
		  //ActionListener zum Schließen einbauen:
		  abortButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					barcode="";
					dispose();
				}});
		  
		 //box.add(new JButton("Abbrechen"));
		  box.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		  getContentPane().add(box, BorderLayout.SOUTH);
		  
		  
		//  getRootPane().setDefaultButton(okButton);
		panel.setFocusable(true);
		 panel.addKeyListener( new KeyListener()
		  {
		    public void keyTyped( KeyEvent e ) {
		      barcode=barcode+e.getKeyChar();
		    }
		    public void keyPressed( KeyEvent e ) {
		      if (e.getKeyCode()==10){
		    	  //System.out.println("Enter gedrückt");
		    	  setVisible(false);
		      }
		    }
		    public void keyReleased( KeyEvent e ) {
		    }
		  });
		 //setVisible(true);
		//Zwischenspeicher löschen:
			barcode="";

	}

	
//	public static void main(String[] args) {
//		BarcodeReaderForm c= new BarcodeReaderForm(null);
//		//c.setVisible(true);
//		String t=c.readBarcode();
//		System.out.println("barcode erkannt: "+t);
//		
//		t=c.readBarcode();
//		System.out.println("barcode erkannt: "+t);
//		//System.out.println(c.getBarcode());
//	}
	
	public String readBarcode(){
		barcode="";
		setVisible(true);
		//barcode="108080130802308033080430805308063080730808308093081030811308123081330814308153081630817308183081930820308213082230823308242";
		//barcode="101010120102201031010410105101061010710108101091011010111101121011310114101151011610117101181011910120101211012210123101241012510126101271012810129101301013110132101331013410135101361013710138101391014010141101421014310144101451014610147101481014910150101511015210153101541015510156101571015810159101601016110162101631016410165101661016710168101691";
		//barcode="10201061011310201102031020410205102063020710208302091021010211102121021310214102151021610217102181021910220102211022210223102241022510226102271022810229102301023110232102331023410235102361023710238102391024010241102421024310244102451024610247102481030510309204091041310509206141070820715308111081410916110142";
		//barcode="00201061011310206302082030510309204091041310509206141070820715308111081410916110142";
		//barcode="10907011070520708207102071210715109012090220903209042090520906209072090820909209102091120912209132091420915209162091720918209192092020921209222092320924209252092620927209282092910930109311093210933110081";
		//barcode="00000106060130602306033060430605306062060720608206092061020611206122061320614206152061620617206182061920620206212062220623206242062520626206272062820629206302063120632206332";
		//barcode="543211030301203022030320304203052030620307203082030920310203112031220313103141031510316103171031810319103201032110322103231032410325103261032710328103291033010331103321033310334103351033610337103381033910340103411034210343103441034510346103471034810349103501035110352103531035410355103561035710358103591";
		//barcode="543211080801309012_";
		//barcode = "543211040101104073?453";
		return barcode;
		//
	}


}
