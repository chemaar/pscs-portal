package org.moldeas.pscs.main;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.moldeas.common.loader.JenaRDFModelWrapper;
import org.moldeas.common.loader.resources.FilesResourceLoader;
import org.moldeas.common.loader.resources.ResourceLoader;
import org.moldeas.common.utils.POIUtils;
import org.moldeas.common.utils.PSCConstants;
import org.moldeas.pscs.mappers.CPV2008Mapper;
import org.moldeas.pscs.to.MappingTO;
import org.moldeas.pscs.to.PSCTO;

import au.com.bytecode.opencsv.CSVReader;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

public class USAMapper {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		//
		//Load NAICS
//		ResourceLoader loader = new FilesResourceLoader(new String[]{"naics/naics-2007.ttl"});
//		List<PSCTO> pSCTOs = new LinkedList<PSCTO>();
//		JenaRDFModelWrapper rdfModel = new JenaRDFModelWrapper(loader,"TURTLE");
//		Model model = (Model) rdfModel.getModel();		
//		ResIterator it = model.listResourcesWithProperty(model.getProperty(PSCConstants.SKOS_prefLabel));
//		while (it.hasNext()){
//			PSCTO current = new PSCTO();
//			Resource r = it.next();
//			StmtIterator iter = model.listStatements(
//					new SimpleSelector(r, model.getProperty(PSCConstants.SKOS_prefLabel), (RDFNode) null) {
//						public boolean selects(Statement s)
//						{return s.getLiteral().getLanguage().equalsIgnoreCase("en");}
//					});	
//			while (iter.hasNext()){
//				current.setUri(r.getURI());
//				current.setPrefLabel(iter.next().getString());
//			}
//			StmtIterator matches = model.listStatements(r, model.getProperty(PSCConstants.SKOS_CLOSE_MATCH), (RDFNode) null);
//			while (matches.hasNext()){
//				System.out.println(matches.next().getResource().getURI());
//			}
//			pSCTOs.add(current);
//		}		
		
		//Load File
		ResourceLoader loader = new FilesResourceLoader(new String[]{CPV2008Mapper.getSource()});
		CPV2008Mapper cpv2008mapper = new CPV2008Mapper(loader);
		String USA_DIR = "/home/chema/data/mappings/usa/";
		String USA_OUTPUT_DIR = "/home/chema/data/mappings/out/usa/";
		for(int year = 2011; year<=2014;year++){
			PrintStream ps = new PrintStream(new FileOutputStream(USA_OUTPUT_DIR+year+"-mapping.csv"));
			ps.println("Contract ID;CPV2008;confidence");
			System.out.println("Processing "+USA_DIR+year+".csv");
			CSVReader reader = new CSVReader(new InputStreamReader(new FileInputStream(USA_DIR+year+".csv")),',');
			String [] line;
			while ((line = reader.readNext()) != null) {
				String id = line[POIUtils.encode("A")];
				String code = line[POIUtils.encode("AM")];
				String description = line[POIUtils.encode("AN")];	
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
