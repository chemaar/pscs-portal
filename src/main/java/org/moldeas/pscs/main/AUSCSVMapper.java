package org.moldeas.pscs.main;

import static org.junit.Assert.assertEquals;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.moldeas.common.loader.resources.FilesResourceLoader;
import org.moldeas.common.loader.resources.ResourceLoader;
import org.moldeas.common.utils.POIUtils;
import org.moldeas.pscs.mappers.CPV2008Mapper;
import org.moldeas.pscs.to.MappingTO;
import org.moldeas.pscs.to.PSCTO;
import org.moldeas.pscs.to.SupplierMappingTO;
import org.moldeas.pscs.to.SupplierTO;


import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import au.com.bytecode.opencsv.CSVReader;

public class AUSCSVMapper {

	public static final String SEPARATOR = "#";

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		ResourceLoader loader = new FilesResourceLoader(new String[]{CPV2008Mapper.getSource()});
		Map<String,String> unspscCodes = load();
		CPV2008Mapper cpv2008mapper = new CPV2008Mapper(loader);
		SupplierUtils utils = new SupplierUtils();
		String outputDir = "/home/chema/data/mappings/out/aus/";	
		for(int year = 2004; year<=2012;year++){ //from 2004-2006 to 2007-2012
			PrintStream ps = new PrintStream(new FileOutputStream(outputDir+year+"-mapping.csv"));
			ps.println("Contract ID#Description#Supplier#UNSPSC code#Title#CPV2008#CPV2008 code#confidence");
			System.out.println("Processing "+"/home/chema/data/mappings/aus/"+year+".csv");
			CSVReader reader = new CSVReader(new InputStreamReader(new 
					FileInputStream("/home/chema/data/mappings/aus/"+year+".csv")),SEPARATOR.charAt(0));
			String [] line = reader.readNext();//Skip first line
			while ((line = reader.readNext()) != null) {
				String id = line[POIUtils.encode("C")];
				String description = line[POIUtils.encode("I")];	
				String supplier = line[POIUtils.encode("W")];
				String code = line[POIUtils.encode("K")];
				String title = line[POIUtils.encode("L")];
				PSCTO current = new PSCTO();
				current.setUri(id);
				current.setSubject(code);	
				 if (title==null || title.equalsIgnoreCase("")){
					System.out.println("Initial: "+title+" code "+code);
					title = unspscCodes.get(code);
					System.out.println("Retrieved 1 step "+title);
					if(title!=null && title.startsWith("[0-9]+")){
						title = unspscCodes.get(title);
					}
					System.out.println("Final: "+title);
				}
				current.setPrefLabel(title);
				List<MappingTO> mappings;
				if (title != null){
					mappings = cpv2008mapper.createMappings(current);
				}else{
					mappings = new LinkedList<MappingTO>();
				}
				SupplierTO supplierTO = new SupplierTO(supplier.split(" ")[0], supplier, "http://myexample.org/"+supplier);
				//List<SupplierMappingTO> supplierMapping = utils.createMappings(supplierTO);
				//It only returns 1
				for(MappingTO mapping:mappings){
					ps.println(
							format(id)+SEPARATOR+
							format(description)+SEPARATOR+
							format(supplierTO.getLabel())+SEPARATOR+
							format(code)+SEPARATOR+
							format(title)+SEPARATOR+
							format(mapping.getTo().getPrefLabel())+SEPARATOR+
							format(mapping.getTo().getUri())+SEPARATOR+
							format(String.valueOf(mapping.getConfidence())));
				}
			}
			reader.close();
			ps.close();
			System.out.println("End Processing");
		}
		

	}

	private static Map<String, String> load() throws IOException {
		Map<String,String> unspscCodes = new HashMap<String, String>();
		CSVReader reader = new CSVReader(
				new InputStreamReader(Thread.currentThread().getContextClassLoader().
						getResourceAsStream("unspsc/sorted-unspsc")),'#');
		String [] line;
		while ((line = reader.readNext()) != null) {
			if(line.length==2) {
				unspscCodes.put(line[0],line[1]);				
			}
		}
		
		return unspscCodes;
	}

	public static String format(String id) {
		return "\""+id+"\"";
	}

}
