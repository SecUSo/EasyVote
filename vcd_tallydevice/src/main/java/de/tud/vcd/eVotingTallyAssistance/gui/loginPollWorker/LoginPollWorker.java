package de.tud.vcd.eVotingTallyAssistance.gui.loginPollWorker;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.border.EmptyBorder;

import de.tud.vcd.eVotingTallyAssistance.controller.Tallying_C;


/**
 * Stellt die LoginFunktionalität zur Verfügung, mit der sich die Wahlhelfer am
 * System anmelden können.
 * 
 * @author Roman Jöris <roman.joeris@googlemail.com>
 * 
 */
public class LoginPollWorker extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JPasswordField passwordField1;
	private JPasswordField passwordField2;

	/**
	 * Erzeugt den Dialog und bindet es an das übergeordnete Fenster als modales
	 * Fenster. Das heißt, dass Fenster kann nicht umgangen werden. Wird es doch
	 * umgangen ist es nicht schlimm, da das Model gesperrt ist und somit eine
	 * Anweisung mit einem Fehler quittiert werden würden.
	 * 
	 * @param c
	 *            Tallying_C Kontroller, der für die Funktionaliät zuständig
	 *            ist.
	 * @param parent
	 *            JFrame übergeordnetes Fenster für die modale Darstellung
	 * @param name1
	 *            String: Name des ersten Wahlhelfers
	 * @param name2
	 *            String: Name des zweiten Wahlhelfers
	 */
	public LoginPollWorker(Tallying_C c, JFrame parent, String name1,
			String name2) {
		setBounds(100, 100, 450, 300);
		setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[] { 0, 131, 0, 131, 0, 0 };
		gbl_contentPanel.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gbl_contentPanel.columnWeights = new double[] { 1.0, 0.0, 0.0, 0.0,
				1.0, Double.MIN_VALUE };
		gbl_contentPanel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0,
				0.0, 1.0, 0.0, Double.MIN_VALUE };
		contentPanel.setLayout(gbl_contentPanel);
		{
			JLabel lblSieKnnenSich = new JLabel(
					"Bitte melden Sie sich wieder am System an.");
			GridBagConstraints gbc_lblSieKnnenSich = new GridBagConstraints();
			gbc_lblSieKnnenSich.gridwidth = 3;
			gbc_lblSieKnnenSich.insets = new Insets(0, 0, 5, 5);
			gbc_lblSieKnnenSich.gridx = 1;
			gbc_lblSieKnnenSich.gridy = 1;
			contentPanel.add(lblSieKnnenSich, gbc_lblSieKnnenSich);
		}
		{
			JLabel lblNewLabel_2 = new JLabel("Wahlhelfer 1:");
			lblNewLabel_2.setFont(new Font("Tahoma", Font.BOLD, 11));
			GridBagConstraints gbc_lblNewLabel_2 = new GridBagConstraints();
			gbc_lblNewLabel_2.anchor = GridBagConstraints.WEST;
			gbc_lblNewLabel_2.insets = new Insets(0, 0, 5, 5);
			gbc_lblNewLabel_2.gridx = 1;
			gbc_lblNewLabel_2.gridy = 3;
			contentPanel.add(lblNewLabel_2, gbc_lblNewLabel_2);
		}
		{
			JLabel lblNewLabel_3 = new JLabel("Wahlhelfer 2:");
			lblNewLabel_3.setFont(new Font("Tahoma", Font.BOLD, 11));
			GridBagConstraints gbc_lblNewLabel_3 = new GridBagConstraints();
			gbc_lblNewLabel_3.anchor = GridBagConstraints.WEST;
			gbc_lblNewLabel_3.insets = new Insets(0, 0, 5, 5);
			gbc_lblNewLabel_3.gridx = 3;
			gbc_lblNewLabel_3.gridy = 3;
			contentPanel.add(lblNewLabel_3, gbc_lblNewLabel_3);
		}
		{
			JLabel lblName1 = new JLabel(name1);
			GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
			gbc_lblNewLabel.anchor = GridBagConstraints.WEST;
			gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
			gbc_lblNewLabel.gridx = 1;
			gbc_lblNewLabel.gridy = 4;
			contentPanel.add(lblName1, gbc_lblNewLabel);
		}
		{
			JLabel lblName2 = new JLabel(name2);
			GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
			gbc_lblNewLabel_1.anchor = GridBagConstraints.WEST;
			gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 5);
			gbc_lblNewLabel_1.gridx = 3;
			gbc_lblNewLabel_1.gridy = 4;
			contentPanel.add(lblName2, gbc_lblNewLabel_1);
		}
		{
			passwordField1 = new JPasswordField();
			//  Nächste Zeile löschen nur Dummyeingabe!!!
			//passwordField1.setText("asdfasdf");
			GridBagConstraints gbc_passwordField = new GridBagConstraints();
			gbc_passwordField.insets = new Insets(0, 0, 5, 5);
			gbc_passwordField.fill = GridBagConstraints.HORIZONTAL;
			gbc_passwordField.gridx = 1;
			gbc_passwordField.gridy = 5;
			contentPanel.add(passwordField1, gbc_passwordField);
		}
		{
			passwordField2 = new JPasswordField();
			//  Nächste Zeile löschen nur Dummyeingabe!!!
			//passwordField2.setText("asdfasdf");
			GridBagConstraints gbc_passwordField_1 = new GridBagConstraints();
			gbc_passwordField_1.insets = new Insets(0, 0, 5, 5);
			gbc_passwordField_1.fill = GridBagConstraints.HORIZONTAL;
			gbc_passwordField_1.gridx = 3;
			gbc_passwordField_1.gridy = 5;
			contentPanel.add(passwordField2, gbc_passwordField_1);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.setActionCommand("LOGIN");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {

					}
				});
				okButton.addActionListener(c.getControllerListener());
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Abbrechen");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						JOptionPane.showConfirmDialog(null, "Ohne Login können Sie mit der Bearbeitung nicht fortfahren.", "Hinweis:", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
						
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
		setLocationRelativeTo(getParent());
	}

	/**
	 * Liest die eingegebenen Kennwörter aus und gibt sie zurück.
	 * 
	 * @return String[] zwei Kennwörter als StringArray
	 */
	public String[] getPasswords() {
		String[] result = new String[2];
		result[0] = String.valueOf(passwordField1.getPassword());
		passwordField1.setText("");
		result[1] = String.valueOf(passwordField2.getPassword());
		passwordField2.setText("");
		dispose();
		return result;
	}
}
