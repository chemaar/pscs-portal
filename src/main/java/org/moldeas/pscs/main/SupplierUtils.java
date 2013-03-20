package org.moldeas.pscs.main;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexDeletionPolicy;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.KeepOnlyLastCommitDeletionPolicy;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.RAMDirectory;
import org.moldeas.common.exceptions.MoldeasModelException;
import org.moldeas.pscs.analyzers.PSCAnalyzer;
import org.moldeas.pscs.mappers.CPV2008Mapper;
import org.moldeas.pscs.to.MappingTO;
import org.moldeas.pscs.to.PSCTO;
import org.moldeas.pscs.to.SupplierMappingTO;
import org.moldeas.pscs.to.SupplierTO;

import au.com.bytecode.opencsv.CSVReader;

public class SupplierUtils {

	private static final String MAPPER_FIELD_PREF_LABEL = "prefLabel";

	private static final String MAPPER_FIELD_URI = "uri";

	private static final String MAPPER_FIELD_KEY = "key";

	protected static Logger logger = Logger.getLogger(CPV2008Mapper.class);

	RAMDirectory idx;
	Analyzer standardAnalyzer = new PSCAnalyzer();
	private Map<String, SupplierTO> suppliers;

	public SupplierUtils(){
		try{
			this.suppliers = loadSuppliers();
			this.idx = new RAMDirectory();
			this.standardAnalyzer = new PSCAnalyzer();
			this.indexSupplier(suppliers.values());
		}catch(Exception e){
			throw new MoldeasModelException(e);
		}


	}

	private void indexSupplier(Collection<SupplierTO> collection) throws CorruptIndexException, LockObtainFailedException, IOException {
		boolean create = true;
		IndexDeletionPolicy deletionPolicy = 
				new KeepOnlyLastCommitDeletionPolicy(); 
		IndexWriter indexWriter = 
				new IndexWriter(idx,standardAnalyzer,create,
						deletionPolicy,IndexWriter.MaxFieldLength.UNLIMITED);
		System.out.println("INDEXING SUPPLIERS"+collection.size()+" ");
		for(SupplierTO supplierTO:collection){
			Field uriField = new Field(MAPPER_FIELD_URI,supplierTO.getUri(),Field.Store.YES,Field.Index.NOT_ANALYZED);
			Field subjectField = new Field(MAPPER_FIELD_PREF_LABEL,supplierTO.getLabel(),Field.Store.YES,Field.Index.ANALYZED);
			Field keyField = new Field(MAPPER_FIELD_KEY,supplierTO.getLabel(),Field.Store.YES,Field.Index.ANALYZED);
			Document doc = new Document();
			doc.add(uriField);
			doc.add(subjectField);
			doc.add(keyField);
			indexWriter.addDocument(doc);		
		}
		indexWriter.optimize();
		indexWriter.close();
	}

	public List<SupplierMappingTO> createMappings(SupplierTO supplierTO){
		List<SupplierMappingTO> mappings = new LinkedList<SupplierMappingTO>();		
		try {
			IndexSearcher indexSearcher = new IndexSearcher(this.idx);		
			logger.debug("Searching "+supplierTO);
			String prefLabel = supplierTO.getLabel();
			ScoreDoc  scoreDoc = CPV2008Mapper.fetchSearchResults(
									CPV2008Mapper.createQueryFromString(
											CPV2008Mapper.cleanPrefLabel(prefLabel)), 
											indexSearcher, 1)[0];
			if(scoreDoc != null){
				Document doc = indexSearcher.doc(scoreDoc.doc);
				String uriTO = doc.getField(MAPPER_FIELD_URI).stringValue();
				SupplierMappingTO mapping = new SupplierMappingTO();
				mapping.setFrom(supplierTO);
				mapping.setTo(this.suppliers.get(uriTO));
				mapping.setConfidence(scoreDoc.score);
				mappings.add(mapping);
				logger.debug("Added mapping "+mapping);
			}
		} catch (Exception e) {
			logger.error("Processing "+supplierTO);
			logger.error(e);
		}finally{

		}
		return mappings;
	}

	private Map<String, SupplierTO> loadSuppliers() throws IOException{
		Map<String,SupplierTO> suppliers = new HashMap<String,SupplierTO>();
		CSVReader reader = new CSVReader(new 
				InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("oracle-suppliers")));
		String [] line = reader.readNext();//Skip first line
		Map<String,List<String>> companyName = new HashMap<String, List<String>>();		
		while ((line = reader.readNext()) != null) {
			String supplierFirstName = line[0].split(" ")[0];
			List<String> names = companyName.get(supplierFirstName);
			if(names == null){
				names = new LinkedList<String>();				
			}
			names.add(StringUtils.join(line));			
			companyName.put(supplierFirstName, names);	
		}
		//Compute most used second word
		for(String company: companyName.keySet()){
			String secondName = computeSecondWord(companyName.get(company));
			SupplierTO supplierTO = new SupplierTO(company, StringUtils.capitalize(company+" "+secondName), "http://example.org/"+company);
			suppliers.put(supplierTO.getUri(),supplierTO);
			}
		return suppliers;
	}

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		//SupplierUtils utils = new SupplierUtils();
		String [] oracles = new String []{
				"Oracle",
				"Oracle Australia Pty Limited",
				//"Oracle Australia Pty Limited DO NOT",
				"Oracle Australia Pty Ltd",
				"Oracle Corpartion",
				"Oracle Corp Aust P/L",
				"Oracle Corp. Aust. P/L",
				"Oracle Corp Aust Pty Limited",
				"Oracle (Corp) Aust Pty Ltd",
				"Oracle Corp (Aust) Pty Ltd",
				"Oracle Corp Aust Pty Ltd",
				"Oracle Corp. Australia",
				"Oracle Corp. Australia Pty.Ltd.",
				"Oracle Corpoartion (Aust) Pty Ltd",
				"Oracle Corporate Aust Pty Ltd",
				"Oracle Corporation",
				"Oracle Corporation (Aust)",
				"Oracle Corporation Aust",
				"Oracle Corporation Aust P/L",
				"Oracle Corporation Aust Pty Limited",
				"Oracle Corporation (Aust) Pty Ltd",
				"Oracle Corporation Aust Pty Ltd",
				"Oracle Corporation Aust. Pty Ltd",
				"Oracle Corporation Australia",
				"Oracle Corporation Australia Limited",
				"Oracle Corporation Australia P/L",
				"Oracle Corporation Australia Pty",
				"Oracle Corporation Australia Pty Li",
				"Oracle Corporation Australia Pty Limited",
				"Oracle Corporation Australia Pty lt",
				"Oracle Corporation Australia Pty Lt",
				"Oracle corporation Australia Pty Ltd",
				"Oracle Corporation (Australia) Pty Ltd",
				"Oracle Corporation Australia Pty Ltd",
				"Oracle Corporation Australia PTY ltd",
				"Oracle Corporation Australia PTY LTD",
				"Oracle Corporation Ltd",
				"Oracle Corporation Pty Ltd",
				"Oracle Pty Limited",
				"Oracle Risk Consultants",
				"Oracle Systems (Aust) Pty Ltd",
				"Oracle Systems (Aust) Pty Ltd - ACT",
				"Oracle Systems Australia P/L",
				"Oracle Systems (Australia) Pty Ltd",
				"Oracle Systems (Australia) Pty Ltd",
		"Oracle University"};
		SupplierUtils sup = new SupplierUtils();
		for(int i = 0; i<oracles.length;i++){
			SupplierTO pscTO = new SupplierTO(oracles[i].split(" ")[0], oracles[i], "http://myexample.org/oracle"+i);
			List<SupplierMappingTO> mappings = sup.createMappings(pscTO);
			System.out.println("<"+mappings.get(0).getFrom().getLabel()+"> the map is <"+mappings.get(0).getTo().getLabel()+">");
		}



	}


	private static String computeSecondWord(List<String> list) {
		Map<String,Integer> secondNames = new HashMap<String,Integer>();
		ValueComparator bvc =  new SupplierUtils.ValueComparator(secondNames);
		TreeMap<String,Integer> sorted_map = new TreeMap<String,Integer>(bvc);
		for(String string:list){
			String [] words = string.split(" ");
			String secondName = (words.length>1)?words[1]:"";
			Integer appears =secondNames.get(secondName); 
			if(appears != null){
				secondNames.put(secondName, appears+1);
			}else{
				secondNames.put(secondName, 1);
			}
		}
		sorted_map.putAll(secondNames);
		return sorted_map.keySet().iterator().next();
	}




	static class ValueComparator implements Comparator<String> {

		Map<String, Integer> base;
		public ValueComparator(Map<String, Integer> base) {
			this.base = base;
		}

		// Note: this comparator imposes orderings that are inconsistent with equals.    
		public int compare(String a, String b) {
			if (base.get(a) >= base.get(b)) {
				return -1;
			} else {
				return 1;
			} // returning 0 would merge keys
		}
	}


}
