/**
 * (c) Copyright 2011 WESO, Computer Science Department,
 * Facultad de Ciencias, University of Oviedo, Oviedo, Asturias, Spain, 33007
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.moldeas.common.to;

import java.io.InputStream;
import java.io.Serializable;

import org.moldeas.common.pk.KnowledgeSourcePK;


/**
 * This class is a transfer object for the data of a knowledge resource.
 */
public class KnowledgeResourcesTO implements Serializable {

    private KnowledgeSourcePK knowledgeSourcePk;
    private InputStream knowledgeSourceData;
    public KnowledgeResourcesTO() {
        super();
    }
   public KnowledgeResourcesTO(InputStream data, KnowledgeSourcePK pk) {
        super();
        knowledgeSourceData = data;
        knowledgeSourcePk = pk;
    }
    public InputStream getKnowledgeSourceData() {
        return knowledgeSourceData;
    }
    public void setKnowledgeSourceData(InputStream knowledgeSourceData) {
        this.knowledgeSourceData = knowledgeSourceData;
    }
    public KnowledgeSourcePK getKnowledgeSourcePk() {
        return knowledgeSourcePk;
    }
    public void setKnowledgeSourcePk(KnowledgeSourcePK knowledgeSourcePk) {
        this.knowledgeSourcePk = knowledgeSourcePk;
    }

    public String toString() {     
        return this.getClass().getSimpleName()+"("+knowledgeSourcePk.toString()+", "+knowledgeSourceData.toString()+")";
    }
    
    
    

    
}
