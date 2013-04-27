package org.moldeas.pscs.mappers;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.moldeas.common.utils.MappingConstants;
import org.moldeas.pscs.to.SupplierMappingTO;
import org.moldeas.pscs.to.SupplierTO;

import au.com.bytecode.opencsv.CSVReader;

public class AUSSupplierMapper implements SupplierMapper {

	private  Map<String, String> supplierMappings;
	public AUSSupplierMapper(String file, String sep) throws IOException{
		this.supplierMappings = loadSuppliers(file, sep);
	}
	@Override
	public List<SupplierMappingTO> createMappings(SupplierTO supplierTO) {
		String supplierMapping = supplierMappings.get(supplierTO.getLabel());
		if(supplierMapping==null || supplierMapping.equals("")){
			supplierMapping = MappingConstants.OTHERS_SUPPLIERS_NAME;
		}
		SupplierTO to = new SupplierTO("", supplierMapping, MappingConstants.FAKE_URI_TO+supplierMapping);
		LinkedList<SupplierMappingTO> result = new LinkedList<SupplierMappingTO>();
		result.add(new SupplierMappingTO(supplierTO, to, 1.0));
		return result;
	}

	private static Map<String, String> loadSuppliers(String file, String sep) throws IOException {
		Map<String,String> supplierNames = new HashMap<String, String>();
		CSVReader reader = new CSVReader(
				new InputStreamReader(Thread.currentThread().getContextClassLoader().
						getResourceAsStream(file)),sep.charAt(0));
		String [] line;
		while ((line = reader.readNext()) != null) {
			if(line.length==2) {
				supplierNames.put(line[0],line[1]);				
			}
		}
		
		return supplierNames;
	}
	
	
}
