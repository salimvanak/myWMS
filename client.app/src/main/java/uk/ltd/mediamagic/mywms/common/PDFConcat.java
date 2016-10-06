package uk.ltd.mediamagic.mywms.common;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.mywms.model.Document;

import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfWriter;

public class PDFConcat implements Closeable{

	private final FileOutputStream file;
	private final PdfWriter writer;
	private com.lowagie.text.Document doc = new com.lowagie.text.Document();
	
	public PDFConcat(File file) throws Exception {
		this.file = new FileOutputStream(file);
		writer = PdfWriter.getInstance(doc, this.file);
		doc.open();
	}
	
	public void add(Document docIn) throws IOException {
		PdfContentByte cb = writer.getDirectContent();
    
    ByteArrayInputStream in = new ByteArrayInputStream(docIn.getDocument());
    PdfReader reader = new PdfReader(in);
    for (int i = 1; i <= reader.getNumberOfPages(); i++) {
    	doc.newPage();
    	//import the page from source pdf
    	PdfImportedPage page = writer.getImportedPage(reader, i);
    	//add the page to the destination pdf
    	cb.addTemplate(page, 0, 0);
    }    
	}
	
	@Override
	public void close() throws IOException {
    doc.close();
		writer.close();
		file.close();
		
	}
}
