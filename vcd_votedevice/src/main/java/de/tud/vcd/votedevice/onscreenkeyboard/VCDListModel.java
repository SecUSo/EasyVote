package de.tud.vcd.votedevice.onscreenkeyboard;

import javax.swing.DefaultListModel;

/**
 * Listmodell für die Elemente in der Ergebnisübersicht der Suche
 * @author Roman Jöris <roman.joeris@googlemail.com>
 *
 */
public class VCDListModel extends DefaultListModel<VCDSearchable> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/* (non-Javadoc)
	 * @see javax.swing.DefaultListModel#getElementAt(int)
	 */
	public VCDSearchable getElementAt(int index) {
        Object s= super.getElementAt(index);
        return (VCDSearchable)s;
 	}
    
 
    /* (non-Javadoc)
     * @see javax.swing.DefaultListModel#addElement(java.lang.Object)
     */
    public void addElement(VCDSearchable obj) {
        this.add(this.size(), obj);
    	
    }

}
