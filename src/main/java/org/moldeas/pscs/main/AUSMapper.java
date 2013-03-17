package org.moldeas.pscs.main;

import static org.junit.Assert.assertEquals;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

public class AUSMapper {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		ResourceLoader loader = new FilesResourceLoader(new String[]{CPV2008Mapper.getSource()});
		CPV2008Mapper cpv2008mapper = new CPV2008Mapper(loader);
		String outputDir = "/home/chema/data/mappings/out/aus/";
		for(int year = 2004; year<=2004;year++){
			PrintStream ps = new PrintStream(new FileOutputStream(outputDir+year+"-mapping.csv"));
			ps.println("Contract ID;CPV2008;confidence");
			InputStream data = new FileInputStream("/home/chema/data/mappings/aus/"+year+".xls");
			HSSFWorkbook wb = new HSSFWorkbook(data);
			HSSFSheet sheet = wb.getSheet("_"+year); 	
			for(int j = 1; j<454;j++){//FIXME: only 2004
				HSSFRow row = sheet.getRow(j);
				if (row == null) {
					continue;
				}		
				String id = POIUtils.extractValue(row.getCell(POIUtils.encode("C")));
				String code = (POIUtils.extractValue(row.getCell(POIUtils.encode("K"))));
				String description = (POIUtils.extractValue(row.getCell(POIUtils.encode("L"))));
				PSCTO current = new PSCTO();
				current.setUri(id);
				current.setPrefLabel(description);
				current.setSubject(code);
				List<MappingTO> mappings = cpv2008mapper.createMappings(current);
				for(MappingTO mapping:mappings){
					ps.println(mapping.getFrom().getUri()+";"+mapping.getTo().getUri()+";"+mapping.getConfidence());
				}
			}
			data.close();
			ps.close();
		}

	}

}
