package org.moldeas.pscs.mappers;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexDeletionPolicy;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.KeepOnlyLastCommitDeletionPolicy;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.RAMDirectory;
import org.moldeas.common.exceptions.MoldeasModelException;
import org.moldeas.common.loader.JenaRDFModelWrapper;
import org.moldeas.common.loader.resources.ExternalizeFilesResourceLoader;
import org.moldeas.common.loader.resources.FilesResourceLoader;
import org.moldeas.common.loader.resources.ResourceLoader;
import org.moldeas.common.utils.PSCConstants;
import org.moldeas.pscs.analyzers.PSCAnalyzer;
import org.moldeas.pscs.to.MappingTO;
import org.moldeas.pscs.to.PSCTO;


import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

public class CPV2008Mapper {
	private static final String MAPPER_FIELD_PREF_LABEL = "prefLabel";

	private static final String MAPPER_FIELD_URI = "uri";

	protected static Logger logger = Logger.getLogger(CPV2008Mapper.class);

	RAMDirectory idx;
	Analyzer standardAnalyzer;
	private Map<String,PSCTO> cpv2008;
	protected final static String SOURCE = "cpv/cpv-2008.ttl";

	public CPV2008Mapper(ResourceLoader loader){
		try{
			this.cpv2008 = createPSCTOs(loader);
			this.idx = new RAMDirectory();
			this.standardAnalyzer = new PSCAnalyzer();
			indexPSC(this.cpv2008.values());
		}catch(Exception e){
			throw new MoldeasModelException(e);
		}
	}
	private void indexPSC(Collection<PSCTO> pscTOs) throws CorruptIndexException, LockObtainFailedException, IOException {
		boolean create = true;
		IndexDeletionPolicy deletionPolicy = 
				new KeepOnlyLastCommitDeletionPolicy(); 
		IndexWriter indexWriter = 
				new IndexWriter(idx,standardAnalyzer,create,
						deletionPolicy,IndexWriter.MaxFieldLength.UNLIMITED);
		System.out.println("INDEXING "+pscTOs.size()+" ");
		for(PSCTO pscTO:pscTOs){
			Field uriField = new Field(MAPPER_FIELD_URI,pscTO.getUri(),Field.Store.YES,Field.Index.NOT_ANALYZED);
			Field subjectField = new Field(MAPPER_FIELD_PREF_LABEL,pscTO.getPrefLabel(),Field.Store.YES,Field.Index.ANALYZED);
			Document doc = new Document();
			doc.add(uriField);
			doc.add(subjectField);
			indexWriter.addDocument(doc);
		}
		indexWriter.optimize();
		indexWriter.close();
	}
	private Map<String,PSCTO> createPSCTOs(ResourceLoader loader) {
		Map<String,PSCTO> cpvPSCTOs = new HashMap<String,PSCTO>();
		JenaRDFModelWrapper rdfModel = new JenaRDFModelWrapper(loader,"TURTLE");
		Model model = (Model) rdfModel.getModel();		
		ResIterator it = model.listResourcesWithProperty(model.getProperty(PSCConstants.SKOS_prefLabel));
		while (it.hasNext()){
			PSCTO current = new PSCTO();
			Resource r = it.next();
			StmtIterator iter = model.listStatements(
					new SimpleSelector(r, model.getProperty(PSCConstants.SKOS_prefLabel), (RDFNode) null) {
						public boolean selects(Statement s)
						{return s.getLiteral().getLanguage().equalsIgnoreCase("en");}
					});	
			while (iter.hasNext()){
				current.setUri(r.getURI());
				current.setPrefLabel(iter.next().getString());
			}
			logger.debug("Loaded "+current);
			cpvPSCTOs.put(current.getUri(),current);
		}		
		return cpvPSCTOs;
	}
	public List<MappingTO> createMappings(PSCTO pscTO){
		List<MappingTO> mappings = new LinkedList<MappingTO>();		
		try {
			IndexSearcher indexSearcher = new IndexSearcher(this.idx);		
			logger.debug("Searching "+pscTO);
			List<ScoreDoc> matchPSCTOs = new LinkedList<ScoreDoc>();
			String prefLabel = pscTO.getPrefLabel();
			ScoreDoc[] scoreDocs = fetchSearchResults(
					createQueryFromString(CPV2008Mapper.cleanPrefLabel(prefLabel)), indexSearcher, 3);
			for(int i = 0; i<scoreDocs.length;i++){
				Document doc = indexSearcher.doc(scoreDocs[i].doc);
				String uriTO = doc.getField(MAPPER_FIELD_URI).stringValue();
				MappingTO mapping = new MappingTO();
				mapping.setFrom(pscTO);
				mapping.setTo(this.cpv2008.get(uriTO));
				mapping.setConfidence(scoreDocs[i].score);
				mappings.add(mapping);
				logger.debug("Added mapping "+mapping);
			}
		} catch (Exception e) {
			logger.error("Processing "+pscTO);
			logger.error(e);
		}finally{

		}
		return mappings;
	}


	public List<MappingTO> createMappings(List<PSCTO> pscTOs){
		List<MappingTO> mappings = new LinkedList<MappingTO>();		
		//FIXME: to optimize indexsearcher no delegate call
		for(PSCTO pscTO:pscTOs){
			try {
				IndexSearcher indexSearcher = new IndexSearcher(this.idx);		

				logger.debug("Searching "+pscTO);
				List<ScoreDoc> matchPSCTOs = new LinkedList<ScoreDoc>();
				String prefLabel = pscTO.getPrefLabel();
				ScoreDoc[] scoreDocs = fetchSearchResults(
						createQueryFromString(CPV2008Mapper.cleanPrefLabel(prefLabel)), indexSearcher, 3);
				for(int i = 0; i<scoreDocs.length;i++){
					Document doc = indexSearcher.doc(scoreDocs[i].doc);
					String uriTO = doc.getField(MAPPER_FIELD_URI).stringValue();
					MappingTO mapping = new MappingTO();
					mapping.setFrom(pscTO);
					mapping.setTo(this.cpv2008.get(uriTO));
					mapping.setConfidence(scoreDocs[i].score);
					mappings.add(mapping);
					logger.debug("Added mapping "+mapping);
					}
			} catch (Exception e) {
				logger.error(pscTO.getUri());
				logger.error(e);
			}finally{

			}
		}
		return mappings;
	}

	public static String getSource() {
		return SOURCE;
	}


	public static Query createQueryFromString(String q) throws ParseException {		
		QueryParser parser = new QueryParser(MAPPER_FIELD_PREF_LABEL,
				new PSCAnalyzer());
		parser.setDefaultOperator(QueryParser.Operator.OR);
		return parser.parse(q);
	}

	private static ScoreDoc[] fetchSearchResults(Query query, Searcher indexSearcher, int n ){
		try{
			TopScoreDocCollector collector = TopScoreDocCollector.create(n, true);
			indexSearcher.search(query, collector);
			ScoreDoc[] scoreDocs = collector.topDocs().scoreDocs;
			return scoreDocs;
		}catch(IOException e){
			e.printStackTrace();
		}
		return new ScoreDoc[0];
	}

	public static String cleanPrefLabel(String q){
		String value = q.replaceAll("-", "");
		value = value.replaceAll("á", "a");
		value = value.replaceAll("é", "e");
		value = value.replaceAll("í", "i");
		value = value.replaceAll("ó", "o");
		value = value.replaceAll("ú", "u");
		value = value.replaceAll("\\W", " ").replaceAll("\\d", "");
		return value;
	}

}
