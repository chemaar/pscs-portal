package org.moldeas.pscs.analyzers;

import java.io.IOException;

import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;

public class SpecialCharsFilter extends TokenFilter{

	  public SpecialCharsFilter(TokenStream input) {
	        super(input);
	    }
	    
	    public Token next() throws IOException {
	    	
	    	String originalTokenType = null;
	        Token token = input.next();

	        if (token != null) {
	        	originalTokenType = token.type();
	        	
	        	/*
	        	 * To remove accents in quoted tokens delete this line
	        	 */
	        	//if  ( originalTokenType.equals(TokenTypes.QUOTED)) return token;
	        	
	    	    String clean = token.termText().replaceAll("\\W", " "); //Remove ()
	            StringBuffer tokenString = new StringBuffer(clean.toString());            
	            for (int i = 0; i < tokenString.length(); i++) {
	            	switch (tokenString.charAt(i)) {
	                case 'á':
	                    tokenString.setCharAt(i,'a');
	                    break;
	                case 'é':
	                    tokenString.setCharAt(i,'e');
	                    break;
	                case 'í':
	                    tokenString.setCharAt(i,'i');
	                    break;
	                case 'ó':
	                    tokenString.setCharAt(i,'o');
	                    break;
	                case 'ú':
	                    tokenString.setCharAt(i,'u');
	                    break;
	                case 'ü':
	                    tokenString.setCharAt(i,'u');
	                    break;
	                case 'ç':
	                	tokenString.setCharAt(i,'c');
	                    break;
	                case 'â':
	                	tokenString.setCharAt(i,'a');
	                    break;
	                case 'ã':
	                	tokenString.setCharAt(i,'a');
	                    break;
	                default:
	                    // do nothing
	                }
	            }
	            token = new Token(tokenString.toString(),token.startOffset(),token.endOffset(), originalTokenType);
	        }
	        return token;
	    }

}
