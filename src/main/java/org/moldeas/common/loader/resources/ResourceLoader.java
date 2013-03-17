package org.moldeas.common.loader.resources;

import org.moldeas.commons.exceptions.ResourceNotFoundException;
import org.moldeas.commons.to.KnowledgeResourcesTO;
import org.w3c.dom.Document;


/**
 * This interface indicates the set of operations to be implemented
 * for a loader of differente kind of sources (Files, Local files, String, etc.).
 */
public interface ResourceLoader {
    
    public KnowledgeResourcesTO [] getKnowledgeResources() throws ResourceNotFoundException;
    public Document getKnowledgeResourceAsDocument(String filename) throws ResourceNotFoundException;
    
}
