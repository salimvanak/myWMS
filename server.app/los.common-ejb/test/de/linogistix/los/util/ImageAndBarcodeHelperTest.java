/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.util;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import junit.framework.TestCase;
import org.apache.log4j.Logger;


/**
 *
 * @author trautm
 */
public class ImageAndBarcodeHelperTest extends TestCase {
    
    private static final Logger log = Logger.getLogger(ImageAndBarcodeHelperTest.class);
    
    public ImageAndBarcodeHelperTest(String testName) {
        super(testName);
    }            

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

//    /**
//     * Test of scaleBarcodeMaxWidth method, of class ImageAndBarcodeHelper.
//     */
//    public void testScaleBarcodeMaxWidth() {
//        try {
//            System.out.println("scaleBarcodeMaxWidth");
//            Image i = it.businesslogic.ireport.barcode.BcImage.getBarcodeImage(9, "A10-10-10", false, true, null, 0, 0);
//            int width = 195;
//            int cropHeigth = 30;
//           
//            BufferedImage result = ImageAndBarcodeHelper.scaleBarcodeMaxWidth(i, width, cropHeigth);
//            File tempFile = new File("testout/testScaleBarcodeMaxWidth.jpg");
//            ImageIO.write(result, "png", tempFile);
//        } catch (IOException ex) {
//            log.error(ex, ex);
//            fail();
//        }
//        
//    }

}
