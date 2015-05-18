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
package de.tud.vcd.votedevice.controller;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.IOException;

import javax.swing.ImageIcon;

/**
 * Erstellt den Druckauftrag. Drückt dabei das übergebene Bild, welches direkt auf einer Zeichenoberfläche ertzeugt wird.
 * 
 * @author Roman Jöris <roman.joeris@googlemail.com>
 *
 */
public class VCDPrintJob implements Printable{

	
	private PrinterJob       pjob;
	private PageFormat       pageformat;

	private int site=1;
	private ImageIcon img;
	private static final double RESMUL = 3;
	
	public VCDPrintJob() {
		this.pjob = PrinterJob.getPrinterJob();
	}

	/**
	 * Legt die Formatierung fest
	 * @return
	 */
	public boolean setupPageFormat() {
		Paper paper= new Paper();
		paper.setSize(21*72/2.54,29.7*72/2.54);
		paper.setImageableArea(0, 0, 21*72/2.54,29.7*72/2.54);
		
		this.pageformat=new PageFormat();
		this.pageformat.setPaper(paper);
		pjob.setPrintable(this, this.pageformat);
		return true;
	}

	/**
	 * Legt den Namen des Auftrags und die Anzahl der Kopien fest
	 * @return
	 */
	public boolean setupJobOptions() {
		pjob.setCopies(1);
		pjob.setJobName("VCD");
		//pjob.set
		
		return true;
	}

	/**
	 * Malt das übergebene Bild auf die Seite 
	 *
	 * @param img
	 * @throws PrinterException
	 * @throws IOException
	 */
	public void printFile(ImageIcon img) throws PrinterException, IOException {
		//in = new RandomAccessFile(fname, "r");
		this.img=img;
		pjob.print();
		//in.close();
	}
	


/* (non-Javadoc)
 * @see java.awt.print.Printable#print(java.awt.Graphics, java.awt.print.PageFormat, int)
 */
public int print(Graphics graphics, PageFormat pf, int pageIndex)
			throws PrinterException {
		int ret;
		if (site==1){
			ret=PAGE_EXISTS;
			site--;
		}else{
			ret=NO_SUCH_PAGE;
		}
		
		Graphics2D g2 = (Graphics2D)graphics; 
		BufferedImage buImg = new BufferedImage(img.getIconWidth(), img.getIconHeight(), BufferedImage.TYPE_INT_ARGB); 
		buImg.getGraphics().drawImage(img.getImage(), 0,0, null);
		
		g2.scale(1.0 / RESMUL, 1.0 / RESMUL);
		g2.drawImage(buImg, 0, 0, null);
		return ret;
	}
}
