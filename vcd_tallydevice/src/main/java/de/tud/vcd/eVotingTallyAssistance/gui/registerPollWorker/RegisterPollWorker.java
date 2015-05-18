package de.tud.vcd.eVotingTallyAssistance.gui.registerPollWorker;

import java.awt.Dialog;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import de.tud.vcd.eVotingTallyAssistance.common.exceptions.WahlhelferException;
import de.tud.vcd.eVotingTallyAssistance.model.Wahlhelfer;


/**
 * Erzeugt ein Eingabeformular, um die Daten der beiden Wahlhelfer zu erfassen. Die Klasse ist zudem dafür zuständig, die Daten zu prüfen und je nach 
 * Benutzereingabe entweder die Daten als Array oder bei Abbruch NULL zurückzuliefern, so dass der Controller diese bearbeiten kann. 
 * 
 * @author Roman Jöris <roman.joeris@googlemail.com>
 *
 */
public class RegisterPollWorker extends JDialog {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtName1;
	private JPasswordField passwordField_1_2;
	private JPasswordField passwordField_1_1;
	private JTextField txtName2;
	private JPasswordField passwordField_2_1;
	private JPasswordField passwordField_2_2;
	
	private Wahlhelfer wh1;
	private Wahlhelfer wh2;

	/**
	 * Erzeugt das Fenster zum Registrieren der Wahlhelfer.
	 * 
	 * @param parentframe JFrame Vaterfenster, um es darauf anzeigen zu können.
	 */
	public RegisterPollWorker(JFrame parentframe) {
		setTitle("Register PollWorker");
		setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		
		setBounds(100, 100, 527, 333);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.X_AXIS));
		
		JPanel panel = new JPanel();
		contentPane.add(panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{118, 0, 0, 89, 0};
		gbl_panel.rowHeights = new int[]{23, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_panel.columnWeights = new double[]{1.0, 1.0, 0.0, 1.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 1.0, 1.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		
		JLabel lblBitteZweiWahlhelfer = new JLabel("Bitte zwei Wahlhelfer registrieren. Kennwort kann frei gewählt werden.");
		GridBagConstraints gbc_lblBitteZweiWahlhelfer = new GridBagConstraints();
		gbc_lblBitteZweiWahlhelfer.gridwidth = 4;
		gbc_lblBitteZweiWahlhelfer.insets = new Insets(0, 0, 5, 0);
		gbc_lblBitteZweiWahlhelfer.gridx = 0;
		gbc_lblBitteZweiWahlhelfer.gridy = 1;
		panel.add(lblBitteZweiWahlhelfer, gbc_lblBitteZweiWahlhelfer);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(null, "PollWorker 1", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.gridwidth = 2;
		gbc_panel_1.anchor = GridBagConstraints.NORTH;
		gbc_panel_1.insets = new Insets(0, 0, 5, 5);
		gbc_panel_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_panel_1.gridx = 1;
		gbc_panel_1.gridy = 2;
		panel.add(panel_1, gbc_panel_1);
		GridBagLayout gbl_panel_1 = new GridBagLayout();
		gbl_panel_1.columnWidths = new int[]{50, 50, 50, 0};
		gbl_panel_1.rowHeights = new int[]{20, 0, 0, 0};
		gbl_panel_1.columnWeights = new double[]{0.0, 1.0, 0.0, Double.MIN_VALUE};
		gbl_panel_1.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		panel_1.setLayout(gbl_panel_1);
		
		JLabel lblName = new JLabel("Name:");
		GridBagConstraints gbc_lblName = new GridBagConstraints();
		gbc_lblName.anchor = GridBagConstraints.EAST;
		gbc_lblName.fill = GridBagConstraints.VERTICAL;
		gbc_lblName.insets = new Insets(0, 0, 5, 5);
		gbc_lblName.gridx = 0;
		gbc_lblName.gridy = 0;
		panel_1.add(lblName, gbc_lblName);
		
		txtName1 = new JTextField();
		GridBagConstraints gbc_txtName1 = new GridBagConstraints();
		gbc_txtName1.gridwidth = 2;
		gbc_txtName1.insets = new Insets(0, 0, 5, 0);
		gbc_txtName1.fill = GridBagConstraints.BOTH;
		gbc_txtName1.gridx = 1;
		gbc_txtName1.gridy = 0;
		panel_1.add(txtName1, gbc_txtName1);
		txtName1.setColumns(10);
		
		JLabel lblPassword = new JLabel("Password:");
		GridBagConstraints gbc_lblPassword = new GridBagConstraints();
		gbc_lblPassword.anchor = GridBagConstraints.EAST;
		gbc_lblPassword.fill = GridBagConstraints.VERTICAL;
		gbc_lblPassword.insets = new Insets(0, 0, 5, 5);
		gbc_lblPassword.gridx = 0;
		gbc_lblPassword.gridy = 1;
		panel_1.add(lblPassword, gbc_lblPassword);
		
		passwordField_1_1 = new JPasswordField();
		GridBagConstraints gbc_passwordField_1_1 = new GridBagConstraints();
		gbc_passwordField_1_1.gridwidth = 2;
		gbc_passwordField_1_1.insets = new Insets(0, 0, 5, 0);
		gbc_passwordField_1_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_passwordField_1_1.gridx = 1;
		gbc_passwordField_1_1.gridy = 1;
		panel_1.add(passwordField_1_1, gbc_passwordField_1_1);
		
		JLabel lblWiderholung = new JLabel("Wiederholung:");
		GridBagConstraints gbc_lblWiderholung = new GridBagConstraints();
		gbc_lblWiderholung.insets = new Insets(0, 0, 0, 5);
		gbc_lblWiderholung.anchor = GridBagConstraints.EAST;
		gbc_lblWiderholung.gridx = 0;
		gbc_lblWiderholung.gridy = 2;
		panel_1.add(lblWiderholung, gbc_lblWiderholung);
		
		passwordField_1_2 = new JPasswordField();
		GridBagConstraints gbc_passwordField_1_2 = new GridBagConstraints();
		gbc_passwordField_1_2.gridwidth = 2;
		gbc_passwordField_1_2.fill = GridBagConstraints.HORIZONTAL;
		gbc_passwordField_1_2.gridx = 1;
		gbc_passwordField_1_2.gridy = 2;
		panel_1.add(passwordField_1_2, gbc_passwordField_1_2);
		
		JPanel panel_2 = new JPanel();
		panel_2.setBorder(new TitledBorder(null, "PollWorker 2", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GridBagConstraints gbc_panel_2 = new GridBagConstraints();
		gbc_panel_2.anchor = GridBagConstraints.NORTH;
		gbc_panel_2.gridwidth = 2;
		gbc_panel_2.insets = new Insets(0, 0, 5, 5);
		gbc_panel_2.fill = GridBagConstraints.HORIZONTAL;
		gbc_panel_2.gridx = 1;
		gbc_panel_2.gridy = 3;
		panel.add(panel_2, gbc_panel_2);
		GridBagLayout gbl_panel_2 = new GridBagLayout();
		gbl_panel_2.columnWidths = new int[]{50, 50, 50, 0};
		gbl_panel_2.rowHeights = new int[]{20, 0, 0, 0};
		gbl_panel_2.columnWeights = new double[]{0.0, 1.0, 0.0, Double.MIN_VALUE};
		gbl_panel_2.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		panel_2.setLayout(gbl_panel_2);
		
		JLabel label = new JLabel("Name:");
		GridBagConstraints gbc_label = new GridBagConstraints();
		gbc_label.fill = GridBagConstraints.VERTICAL;
		gbc_label.anchor = GridBagConstraints.EAST;
		gbc_label.insets = new Insets(0, 0, 5, 5);
		gbc_label.gridx = 0;
		gbc_label.gridy = 0;
		panel_2.add(label, gbc_label);
		
		txtName2 = new JTextField();
		txtName2.setColumns(10);
		GridBagConstraints gbc_txtName2 = new GridBagConstraints();
		gbc_txtName2.fill = GridBagConstraints.BOTH;
		gbc_txtName2.gridwidth = 2;
		gbc_txtName2.insets = new Insets(0, 0, 5, 0);
		gbc_txtName2.gridx = 1;
		gbc_txtName2.gridy = 0;
		panel_2.add(txtName2, gbc_txtName2);
		
		JLabel label_1 = new JLabel("Password:");
		GridBagConstraints gbc_label_1 = new GridBagConstraints();
		gbc_label_1.fill = GridBagConstraints.VERTICAL;
		gbc_label_1.anchor = GridBagConstraints.EAST;
		gbc_label_1.insets = new Insets(0, 0, 5, 5);
		gbc_label_1.gridx = 0;
		gbc_label_1.gridy = 1;
		panel_2.add(label_1, gbc_label_1);
		
		passwordField_2_1 = new JPasswordField();
		GridBagConstraints gbc_passwordField_2_1 = new GridBagConstraints();
		gbc_passwordField_2_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_passwordField_2_1.gridwidth = 2;
		gbc_passwordField_2_1.insets = new Insets(0, 0, 5, 0);
		gbc_passwordField_2_1.gridx = 1;
		gbc_passwordField_2_1.gridy = 1;
		panel_2.add(passwordField_2_1, gbc_passwordField_2_1);
		
		JLabel label_2 = new JLabel("Wiederholung:");
		GridBagConstraints gbc_label_2 = new GridBagConstraints();
		gbc_label_2.anchor = GridBagConstraints.EAST;
		gbc_label_2.insets = new Insets(0, 0, 0, 5);
		gbc_label_2.gridx = 0;
		gbc_label_2.gridy = 2;
		panel_2.add(label_2, gbc_label_2);
		
		passwordField_2_2 = new JPasswordField();
		GridBagConstraints gbc_passwordField_2_2 = new GridBagConstraints();
		gbc_passwordField_2_2.fill = GridBagConstraints.HORIZONTAL;
		gbc_passwordField_2_2.gridwidth = 2;
		gbc_passwordField_2_2.gridx = 1;
		gbc_passwordField_2_2.gridy = 2;
		panel_2.add(passwordField_2_2, gbc_passwordField_2_2);
		
		JButton btnStart = new JButton("Start");
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//Controller 
				if (checkPassword(1, String.valueOf(passwordField_1_1.getPassword()), String.valueOf(passwordField_1_2.getPassword()))
				&&
				checkPassword(2, String.valueOf(passwordField_2_1.getPassword()), String.valueOf(passwordField_2_2.getPassword()))
				){
					try {
						wh1=new Wahlhelfer(txtName1.getText(), String.valueOf(passwordField_1_1.getPassword()));
						wh2=new Wahlhelfer(txtName2.getText(), String.valueOf(passwordField_2_1.getPassword()));
					} catch (WahlhelferException e) {
						JOptionPane.showConfirmDialog(null, e.getMessage(), "Warnung:", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);
					}
					
					dispose();
				}
				
			}
		});
		GridBagConstraints gbc_btnStart = new GridBagConstraints();
		gbc_btnStart.insets = new Insets(0, 0, 5, 5);
		gbc_btnStart.gridx = 1;
		gbc_btnStart.gridy = 5;
		panel.add(btnStart, gbc_btnStart);
		
		JButton btnAbort = new JButton("Abbrechen");
		btnAbort.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				wh1=null;
				wh2=null;
				dispose();
			}
		});
		GridBagConstraints gbc_btnAbort = new GridBagConstraints();
		gbc_btnAbort.insets = new Insets(0, 0, 5, 5);
		gbc_btnAbort.gridx = 2;
		gbc_btnAbort.gridy = 5;
		panel.add(btnAbort, gbc_btnAbort);
		
		setLocationRelativeTo(getParent());
	}

	/**
	 * Zeigt das Fenster an und erstellt aus den Daten ein Array von den Wahlhelfern, welche dann zurück übergeben werden.
	 * 
	 * @return Wahlhelfer[] Ein Array aus 2 Wahlhelfern.
	 */
	public Wahlhelfer[] showDialog() {
		setVisible(true);
		Wahlhelfer[] wh_arr = new Wahlhelfer[2];
		if (wh1 == null || wh2 == null) {
			wh_arr = null;
		} else {
			wh_arr[0] = wh1;
			wh_arr[1] = wh2;
		}
		return wh_arr;
	}
	
	// Diese Methode löschen!!!
//	public void setDummies(String n1, String p1, String n2, String p2){
//		txtName1.setText(n1);
//		passwordField_1_1.setText(p1);
//		passwordField_1_2.setText(p1);
//		txtName2.setText(n2);
//		passwordField_2_1.setText(p2);
//		passwordField_2_2.setText(p2);
//	}
	
	/**
	 * Prüft, ob das Kennwort ausreichend sicher ist. Das heißt mindestens 8 Zeichen besitzt.
	 * 
	 * @param id int Id des Wahlhelfers, also 1 oder 2, um anzuzeigen bei wem es nicht stimmt.
	 * @param pwd1 String Kennwort des Wahlhelfers
	 * @param pwd2 String Kennwortwiederholung des Wahlhelfers.
	 * @return
	 */
	private boolean checkPassword(int id, String pwd1, String pwd2){
		if (pwd1.equals(pwd2)){
			if (pwd1.length()>=8){
				return true;
			}else{
				JOptionPane.showMessageDialog(null, "Das Kennwort von Wahlfelfer "+id+" muss mindestens 8 Zeichen besitzen.",
						"Fehler", JOptionPane.OK_OPTION);
			}
		}else{
			JOptionPane.showMessageDialog(null, "Das Kennwort von Wahlfelfer "+id+" wurde nicht korrekt wiederholt.",
					"Fehler", JOptionPane.OK_OPTION);
		}
		
		return false;
	}
	
//	public Wahlhelfer getData(int wahlhelferindex){
//		Wahlhelfer result=null;
//		switch (wahlhelferindex) {
//		case 1:
//			result=new Wahlhelfer(txtName1.getText(), String.valueOf(passwordField_1_1.getPassword()));
//			break;
//		case 2:
//			result=new Wahlhelfer(txtName2.getText(), String.valueOf(passwordField_1_2.getPassword()));
//			break;
//		default:
//			break;
//		}
//		return result;
//	}
}
