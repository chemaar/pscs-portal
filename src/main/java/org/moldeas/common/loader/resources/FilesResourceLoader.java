package org.moldeas.common.loader.resources;

import org.moldeas.common.exceptions.MoldeasModelException;
import org.moldeas.common.exceptions.ResourceNotFoundException;
import org.moldeas.common.pk.KnowledgeSourcePK;
import org.moldeas.common.to.KnowledgeResourcesTO;
import org.moldeas.common.utils.DocumentBuilderHelper;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;

/**
 *
 * This class implements the interface ResourceLoader loading the data
 * from a file referenced by a name of the current classpath.
 *
 */
public class FilesResourceLoader  implements ResourceLoader {
    
    private static final Logger logger = Logger.getLogger(FilesResourceLoader.class);

    private String []resourceNames;
    
    public FilesResourceLoader(String[] filenames) {
        this.resourceNames = filenames;
    }
    public FilesResourceLoader(List <String>filenames) {
        this.resourceNames = filenames.toArray(new String[filenames.size()]);
    }

    protected InputStream openInputStream(String filename) throws FileNotFoundException {
        logger.debug("Opening resource input stream for filename: " + filename);
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream in = classLoader.getResourceAsStream(filename);
        if (in == null) {
            logger.error("Resource file not found: " + filename);
            throw new FileNotFoundException(filename);
        } else {
            return in;
        }
    }
    
    

    public Document getKnowledgeResourceAsDocument(String filename) throws ResourceNotFoundException {
        try {
        	logger.debug("Parsing resource filename: " + filename);
            InputStream in = openInputStream(filename);
            Document document = DocumentBuilderHelper.getDocumentFromInputStream(in);
            logger.debug("Finished parsing of resource filename: " + filename);
            in.close();
            return document;
        } catch (FileNotFoundException e) {
            throw new ResourceNotFoundException(e.getMessage()); // propagate
        } catch (Exception e) {
            throw new MoldeasModelException(e, "Moldeas files resource loaded: parsing Resource file " + filename);
        }
    }
    

  
    public KnowledgeResourcesTO[] getKnowledgeResources() {
        Collection<KnowledgeResourcesTO> ontologies = new LinkedList<KnowledgeResourcesTO>();
        String file = "";
        try {
            for (int i = 0 ;i< resourceNames.length;i++) {
                file =  resourceNames[i]; 
                KnowledgeSourcePK ontologyPK = new KnowledgeSourcePK(file);
                KnowledgeResourcesTO resource = new KnowledgeResourcesTO(
                		(this.openInputStream(file)),ontologyPK);
                ontologies.add(resource);
            }
        } catch (FileNotFoundException e) {
            throw new MoldeasModelException(e, "Resource Files getting resource file:  "+file);
        }
        return  ontologies.toArray(new KnowledgeResourcesTO[ontologies.size()]);
    }





}
