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
import java.util.LinkedList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.moldeas.common.loader.resources.FilesResourceLoader;
import org.moldeas.common.loader.resources.ResourceLoader;
import org.moldeas.common.utils.POIUtils;
import org.moldeas.pscs.mappers.CPV2008Mapper;
import org.moldeas.pscs.to.MappingTO;
import org.moldeas.pscs.to.PSCTO;


import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import au.com.bytecode.opencsv.CSVReader;

public class AUSCSVMapper {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		ResourceLoader loader = new FilesResourceLoader(new String[]{CPV2008Mapper.getSource()});
		CPV2008Mapper cpv2008mapper = new CPV2008Mapper(loader);
		String outputDir = "/home/chema/data/mappings/out/aus/";	
		for(int year = 2007; year<=2012;year++){
			PrintStream ps = new PrintStream(new FileOutputStream(outputDir+year+"-mapping.csv"));
			ps.println("Contract ID;CPV2008;confidence");
			System.out.println("Processing "+"/home/chema/data/mappings/aus/"+year+".csv");
			CSVReader reader = new CSVReader(new InputStreamReader(new FileInputStream("/home/chema/data/mappings/aus/"+year+".csv")),';');
			String [] line;
			while ((line = reader.readNext()) != null) {
				String id = line[POIUtils.encode("C")];
				String code = line[POIUtils.encode("K")];
				String description = line[POIUtils.encode("L")];	
				System.out.println("ID "+id+" Code "+code+" Description "+description);
				PSCTO current = new PSCTO();
				current.setUri(id);
				current.setPrefLabel(description);
				current.setSubject(code);
				List<MappingTO> mappings = cpv2008mapper.createMappings(current);
				for(MappingTO mapping:mappings){
					ps.println(mapping.getFrom().getUri()+";"+mapping.getTo().getUri()+";"+mapping.getConfidence());
				}
			}
			reader.close();
			ps.close();
			System.out.println("End Processing");
		}
		

	}

}
