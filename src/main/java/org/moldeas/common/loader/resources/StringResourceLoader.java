package org.moldeas.common.loader.resources;

import java.io.ByteArrayInputStream;
import java.util.logging.Level;

import org.moldeas.common.exceptions.DocumentBuilderException;
import org.moldeas.common.exceptions.ResourceNotFoundException;
import org.moldeas.common.to.KnowledgeResourcesTO;
import org.moldeas.common.utils.DocumentBuilderHelper;

import java.io.InputStream;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;

/**
 *
 * This class implements the interface ResourceLoader loading the data
 * from a String.
 *
 */
public class StringResourceLoader  implements ResourceLoader {
    
    private static final Logger logger = Logger.getLogger(StringResourceLoader.class);

    private String content;
    
    public StringResourceLoader(String content) {
       this.content = content;
    }

    public Document getKnowledgeResourceAsDocument(String filename) throws ResourceNotFoundException {
        try {
            return DocumentBuilderHelper.getDocumentFromString(content);
        } catch (DocumentBuilderException ex) {
            java.util.logging.Logger.getLogger(StringResourceLoader.class.getName()).log(Level.SEVERE, null, ex);
            throw new ResourceNotFoundException("Can not parse content.");
        }
    }
    

  
    public KnowledgeResourcesTO[] getKnowledgeResources() {
        KnowledgeResourcesTO knowledgeResourcesTO = new KnowledgeResourcesTO();
        InputStream knowledgeSourceData = new ByteArrayInputStream(content.getBytes());
        knowledgeResourcesTO.setKnowledgeSourceData(knowledgeSourceData);
        return  new KnowledgeResourcesTO[]{knowledgeResourcesTO};
    }





}
