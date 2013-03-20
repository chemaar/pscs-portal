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
		for(int year = 2013; year<=2013;year++){//2013 is not really 2013 is a part
			PrintStream ps = new PrintStream(new FileOutputStream(Uk_OUTPUT_DIR+year+"-mapping.csv"));
			ps.println("Contract ID#Description#Supplier#UNSPSC code#Title#CPV2008#CPV2008 code#confidence");
			System.out.println("Processing "+UK_DIR+year+".csv");
			CSVReader reader = new CSVReader(new InputStreamReader(new FileInputStream(UK_DIR+year+".csv")),'\t');//2012 \t and 2013 ;
			String [] line = reader.readNext();//skip first line
			while ((line = reader.readNext()) != null) {
				//For 2013
				String id = line[POIUtils.encode("G")];
				String description = line[POIUtils.encode("D")]+" "+line[POIUtils.encode("E")];
				String supplier = line[POIUtils.encode("F")];
				String title = description;				
				String code = line[POIUtils.encode("G")];
				
				System.out.println("ID "+id+" Code "+code+" description "+description+" supplier "+ supplier);
				PSCTO current = new PSCTO();
				current.setUri(id);
				current.setPrefLabel(description);
				current.setSubject(code);
				List<MappingTO> mappings = cpv2008mapper.createMappings(current);
				for(MappingTO mapping:mappings){
					ps.println(
							AUSCSVMapper.format(id)+AUSCSVMapper.SEPARATOR+
						    AUSCSVMapper.format(description)+AUSCSVMapper.SEPARATOR+
						    AUSCSVMapper.format(supplier)+AUSCSVMapper.SEPARATOR+
						    AUSCSVMapper.format(code)+AUSCSVMapper.SEPARATOR+
						    AUSCSVMapper.format(title)+AUSCSVMapper.SEPARATOR+
						    AUSCSVMapper.format(mapping.getTo().getPrefLabel())+AUSCSVMapper.SEPARATOR+
							AUSCSVMapper.format(mapping.getTo().getUri())+AUSCSVMapper.SEPARATOR+
							AUSCSVMapper.format(String.valueOf(mapping.getConfidence())));
				}
			}
			reader.close();
			ps.close();
			System.out.println("End Processing");
		}
		

	}

}
