/*
 * Copyright (c) 2006 - 2012 LinogistiX GmbH
 *
 * www.linogistix.com
 *
 * Project: myWMS-LOS
*/
package de.linogistix.los.common.businessservice;

import javax.ejb.Local;

import org.mywms.facade.FacadeException;
import org.mywms.globals.DocumentTypes;

/**
 * @author krane
 *
 */
@Local
public interface LOSPrintService {

	public static String DEFAULT_PRINTER = "default";
	public static String NO_PRINTER = "none";

	/**
	 * Print a document on a given printer.
	 *
	 * This method is maintained for backward compatibility, please use
	 * {@link LOSPrintService#print(String, String, byte[], String) print} instead.
	 *
	 * @param printer
	 * @param bytes
	 * @param type (@see {@link DocumentTypes})
	 * @throws FacadeException
	 * @Deprecated The preferred method is to user the print method supplying a jobName.
	 */
	@Deprecated
	public void print(String printer, byte[] bytes, String type) throws FacadeException;


	/**
	 * Prints a document to a given printer and assigns the print job with
	 * the given jobName.
	 *
	 * The printer name may be {@link LOSPrintService#DEFAULT_PRINTER DEFAULT_PRINTER}
	 * in which case default system printer is looked up.
	 *
	 * If <code>printer</code> starts with "prn:" the prefix is ignored and the rest is
	 * interpreted as the printer name. e.g. "prn:MyPrinter1" is the same as "MyPrinter1"
	 *
	 * If <code>printer</code> starts with "cmd:" a temp file is created with the
	 * <code>bytes</code> and the command is called substituting occurrences of ":file:"
	 * with the temp file name,
	 * For example, <code>"cmd: lpr :file:"</code> will result in <code>lpr JobName-212334.prn</code>
	 * being executed on the command line.
	 *
	 * @param printer the name of the printer
	 * @param jobName the jobName assigned to the print job
	 * @param bytes the data to print
	 * @param type the type of the print job.
	 * @throws FacadeException if an error occurs while printing.
	 * @author Salim Vanak
	 */
	public void print(String printer, String jobName, byte[] bytes, String type) throws FacadeException;

}
