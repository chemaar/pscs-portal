package org.moldeas.pscs.services;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.ProduceMime;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;



@Path("/hello")
public class HelloRest {
	@GET
	@Path("sayHello")
	@ProduceMime({"text/plain", "application/xml", "application/json"})
	public String sayHello(@PathParam("name") String name){	 
		try{
			return "Hello "+name;
		}catch(Exception e){
			 throw new WebApplicationException(Response.Status.BAD_REQUEST);
		}
		
	}
	
	
}