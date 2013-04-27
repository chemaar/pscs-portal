package org.moldeas.pscs.services;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.ProduceMime;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.moldeas.common.utils.ApplicationContextLocator;
import org.moldeas.common.utils.MappingConstants;
import org.moldeas.pscs.mappers.PSCMapper;
import org.moldeas.pscs.mappers.SupplierMapper;
import org.moldeas.pscs.to.ListMappingTO;
import org.moldeas.pscs.to.PSCTO;
import org.moldeas.pscs.to.SupplierMappingTO;
import org.moldeas.pscs.to.SupplierTO;



@Path("/mapping")
public class MapperServiceRest {

	private static final String SUPPLIER_UNIFIED_NAMES_CSV = "suppliers/supplier-unified-names.csv";

	private PSCMapper cpv2008mapper;
	private SupplierMapper supplierMapper;

	public MapperServiceRest() throws IOException{
		this.cpv2008mapper  = (PSCMapper) 
				ApplicationContextLocator.getApplicationContext().getBean(PSCMapper.class.getSimpleName());
		this.supplierMapper = (SupplierMapper) 
				ApplicationContextLocator.getApplicationContext().getBean(SupplierMapper.class.getSimpleName());
	}

	@GET
	@Path("cpv2008")
	@ProduceMime({"text/plain", "application/xml", "application/json"})
	public ListMappingTO cpv2008(@QueryParam("code") String code){	 
		try{
			PSCTO pscTO = new PSCTO(MappingConstants.FAKE_URI_FROM+code);
			pscTO.setPrefLabel(code);
			return new ListMappingTO(cpv2008mapper.createMappings(pscTO));
		}catch(Exception e){
			 throw new WebApplicationException(Response.Status.BAD_REQUEST);
		}
		
	}
	
	@GET
	@Path("supplier")
	@ProduceMime({"text/plain", "application/xml", "application/json"})
	public SupplierMappingTO aus(@QueryParam("name") String name){	 
		try{
			//FIXME: get(0)
			return this.supplierMapper.createMappings(
					new SupplierTO("", name, MappingConstants.FAKE_URI_FROM+name)).get(0);
		}catch(Exception e){
			 throw new WebApplicationException(Response.Status.BAD_REQUEST);
		}
		
	}
	
	
}