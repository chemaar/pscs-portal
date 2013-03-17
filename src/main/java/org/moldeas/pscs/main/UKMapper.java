package org.moldeas.pscs.main;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.List;

import org.moldeas.common.loader.resources.FilesResourceLoader;
import org.moldeas.common.loader.resources.ResourceLoader;
import org.moldeas.common.utils.POIUtils;
import org.moldeas.pscs.mappers.CPV2008Mapper;
import org.moldeas.pscs.to.MappingTO;
import org.moldeas.pscs.to.PSCTO;

import au.com.bytecode.opencsv.CSVReader;

public class UKMapper {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		ResourceLoader loader = new FilesResourceLoader(new String[]{CPV2008Mapper.getSource()});
		CPV2008Mapper cpv2008mapper = new CPV2008Mapper(loader);
		String UK_DIR = "/home/chema/data/mappings/uk/";
		String Uk_OUTPUT_DIR = "/home/chema/data/mappings/out/uk/";
		for(int year = 2013; year<=2013;year++){
			PrintStream ps = new PrintStream(new FileOutputStream(Uk_OUTPUT_DIR+year+"-mapping.csv"));
			ps.println("Transaction Number;CPV2008;confidence");
			System.out.println("Processing "+UK_DIR+year+".csv");
			CSVReader reader = new CSVReader(new InputStreamReader(new FileInputStream(UK_DIR+year+".csv")),';');
			String [] line;
			while ((line = reader.readNext()) != null) {
				String id = line[POIUtils.encode("G")];
				String code = line[POIUtils.encode("G")];
				String description = line[POIUtils.encode("D")];	
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
