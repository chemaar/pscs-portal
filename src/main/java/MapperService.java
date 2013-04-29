

import java.io.IOException;

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




public class MapperService {

	private static final String SUPPLIER_UNIFIED_NAMES_CSV = "suppliers/supplier-unified-names.csv";

	private PSCMapper cpv2008mapper;
	private SupplierMapper supplierMapper;

	public MapperService() throws IOException{
		this.cpv2008mapper  = (PSCMapper) 
				ApplicationContextLocator.getApplicationContext().getBean(PSCMapper.class.getSimpleName());
		this.supplierMapper = (SupplierMapper) 
				ApplicationContextLocator.getApplicationContext().getBean(SupplierMapper.class.getSimpleName());
	}

	public ListMappingTO cpv2008(String label){	 
		try{
			PSCTO pscTO = new PSCTO(MappingConstants.FAKE_URI_FROM+label);
			pscTO.setPrefLabel(label);
			return new ListMappingTO(cpv2008mapper.createMappings(pscTO));
		}catch(Exception e){
			 throw new WebApplicationException(Response.Status.BAD_REQUEST);
		}
		
	}
	
	public SupplierMappingTO aus(@QueryParam("label") String label){	 
		try{
			//FIXME: get(0)
			return this.supplierMapper.createMappings(
					new SupplierTO("", label, MappingConstants.FAKE_URI_FROM+label)).get(0);
		}catch(Exception e){
			 throw new WebApplicationException(Response.Status.BAD_REQUEST);
		}
		
	}
	
	
}