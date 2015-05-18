import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;

import javax.swing.ImageIcon;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;

import de.tud.vcd.common.BallotCardDesign;
import de.tud.vcd.votedevice.model.IBallotCardImageCreator;
import de.tud.vcd.votedevice.municipalElection.model.BallotCard;
import de.tud.vcd.votedevice.municipalElection.model.BallotCardImageCreatorNewDesign;
import de.tud.vcd.votedevice.municipalElection.model.Candidate;
import de.tud.vcd.votedevice.municipalElection.model.Party;


public class ExcelImport {

	private static String FILE = "Stimmzettel.pdf";
	
	private HashMap<Integer, Integer> stimmensumme;
	
	public ExcelImport() throws Exception {
		stimmensumme= new HashMap<Integer, Integer>();
		
		try {
			URL resource = getClass().getClassLoader().getResource("StimmauszählungFertig.xls");
			File file = new File(resource.toURI());
			FileInputStream fileInputStream = new FileInputStream(file);
			 String path = System.getProperty("user.dir");
		    //    System.out.println(path);
			//String file= getClass().getClassLoader().
			HSSFWorkbook workbook = new HSSFWorkbook(fileInputStream);
			HSSFSheet worksheet = workbook.getSheet("Stimmzettel Erfassung");
			
			Document document = new Document(PageSize.A4,0,0,0,0);
			PdfWriter writer=PdfWriter.getInstance(document, new FileOutputStream(path+"/"+FILE));
		      document.open();
		      //addMetaData(document);
		      //addTitlePage(document);
		      //addContent(document);
		      HSSFRow row1 = worksheet.getRow(2);
		      
				
				int position=5;
				HSSFCell cell=null;
		      do{
		    	  
		    	  cell = row1.getCell(position);
		    	  
		    	  if(cell!=null){
		    		  addWahlzettel(document,writer, worksheet, position);
		    	  }
		    	  position+=2;
		      }
		      while(cell!=null);
		      
		     
		      document.close();
			 System.out.println(stimmensumme.toString());
			
			
//			HSSFRow row1 = worksheet.getRow(0);
//			HSSFCell cellA1 = row1.getCell(5);//  getCell((short) 0);
//			String a1Val = cellA1.getStringCellValue();
//			HSSFCell cellB1 = row1.getCell(7);
//			String b1Val = cellB1.getStringCellValue();
//			HSSFCell cellC1 = row1.getCell(9);
//			String c1Val = cellC1.getStringCellValue();
//			HSSFCell cellD1 = row1.getCell(11);
//			String d1Val = cellD1.getStringCellValue();
//
//			System.out.println("A1: " + a1Val);
//			System.out.println("B1: " + b1Val);
//			System.out.println("C1: " + c1Val);
//			System.out.println("D1: " + d1Val);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}
	
	public void addWahlzettel(Document document,PdfWriter writer, HSSFSheet worksheet, int spaltenindex) throws Exception{
		BallotCard bc= readDataSet(worksheet, spaltenindex);
		IBallotCardImageCreator bcic= new BallotCardImageCreatorNewDesign(595*3 , 842*3 , bc);
		
		ImageIcon ic= bcic.createImage(Color.WHITE);
		
		//ic.setImage(ic.getImage().getScaledInstance(595,842,java.awt.Image.SCALE_DEFAULT ));
		
		
		PdfContentByte cb = writer.getDirectContent();
		
//		PdfTemplate tp = cb.createTemplate(400,400);
//        Graphics2D g2 = tp.createGraphics(400,400);
		
        // Create the graphics as shapes
        cb.saveState();
        float breite = document.getPageSize().getWidth();
        float hoehe = document.getPageSize().getHeight();
        Graphics2D g2 = cb.createGraphics(breite, hoehe);//  createGraphicsShapes(breite,hoehe);
        //Shape oldClip = g2.getClip();
        //g2.clipRect(0, 0, (int)breite, (int)hoehe);
        g2.scale(1.0 / 3, 1.0 / 3);
        g2.drawImage(ic.getImage(), 0,0, ic.getIconWidth(),ic.getIconHeight(),null);
        
       //Druckt eine Id auf jeden Wahlzettel um ihn zuordnen zu können!!!!
        g2.setFont(new Font("Sans Sarif", Font.PLAIN,12));
        g2.drawString(((spaltenindex-3)/2)+"", breite*3-150,(120));
		
        
        g2.dispose();
		cb.restoreState();

		document.newPage();
		
	
	}
	
	
	
	public BallotCard readDataSet(HSSFSheet worksheet, int spaltenindex) throws Exception{
		InputStream filename=getClass().getClassLoader().getResource("wahlzettel.xml").openStream();
		BallotCardDesign bcd = BallotCardDesign.getInstance(filename);
		BallotCard bc= new BallotCard(bcd);
		
		String[] parteien= {"CDU","SPD","FDP","GRÜNE","DIE LINKE","UFFBASSE","BIG","PIRATEN","UWIGA","FWDA"};
		System.out.println("---------------------");
		System.out.println("STIMMZETTEL EINLESEN ("+((spaltenindex-3)/2)+"):");
		System.out.println("---------------------");
		//Hier nun die Werte aus der Excelspalte einlesen:

		
		//ungültig einlesen
		HSSFRow r = worksheet.getRow(5);
		HSSFCell c = r.getCell(spaltenindex+1);
		String v = c.getStringCellValue();
		if (v.equals("x")){
			System.out.println("Ungültig");
			bc.setManualVoteInvalid(true);
		}else{
			System.out.println("gültig");
		}
		//Parteien einlesen
		
		for (int i=6;i<16;i++){
			HSSFRow row1 = worksheet.getRow(i);
			HSSFCell cell = row1.getCell(spaltenindex+1);
			String val="";
			if (cell!=null){
				//System.out.println("reihe:"+i);
				if (cell.getCellType()==HSSFCell.CELL_TYPE_STRING){
					val = cell.getStringCellValue();
				}
			}else{
				val="";
			}
			
			if (val.equals("x")){
				System.out.println("Partei gefunden: "+parteien[i-6]);
				bc.setPartyVoted(parteien[i-6], true);
			}
		}
		
		//Kandidaten einlesen:
		int id;
		int rowId=18;
		do{
			HSSFRow row1 = worksheet.getRow(rowId);
			HSSFCell cell = row1.getCell(spaltenindex);
			
			if (cell!=null){
				id = (int)cell.getNumericCellValue();
			}else{
				id=0;
			}
			
			HSSFCell cell2 = row1.getCell(spaltenindex+1);
			int votes;
			if (cell2!=null){
				votes = (int)cell2.getNumericCellValue();
			}else{
				votes=0;
			};
			 
			if (id!=0){
				System.out.println("Id gefunden: "+id+" : "+ votes);
				bc.setCandidateVote(parteien[(id/100)-1], id, votes);
				
			}
			rowId++;
		}while(id!=0);
		
		//Gestrichene einlesen
		rowId=90;
		do{
			System.out.println("Reihe: "+rowId+" "+spaltenindex);
			if(rowId==96){
				System.out.println("hier problem");
			}
			HSSFRow row1 = worksheet.getRow(rowId);
			HSSFCell cell = row1.getCell(spaltenindex);
			if (cell!=null){
				id = (int)cell.getNumericCellValue();
			}else{
				id=0;
			}
			//HSSFCell cell2 = row1.getCell(spaltenindex+1);
			//int votes = (int)cell2.getNumericCellValue();
			if (id!=0){
				System.out.println("Streichen: "+id);
				bc.setCandidateCrossed(parteien[(id/100)-1], id);
				
			}
			rowId++;
		}while(id!=0);
		
		for (Party p: bc.getPartyList()){
			for (Candidate cand: bc.getParty(p.getName()).getCandidates()){
				Integer cid=cand.getId();
				Integer cstimme=cand.getCountedVotes();
				
				Integer alt= stimmensumme.get(cid);
				if (alt!=null){
					cstimme+=alt;
				}
				stimmensumme.put(cid, cstimme);
			}
		}
		
		
		
		return bc;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			ExcelImport ei = new ExcelImport();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
