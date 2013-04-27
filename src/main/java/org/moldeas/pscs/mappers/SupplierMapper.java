package org.moldeas.pscs.mappers;

import java.util.List;

import org.moldeas.pscs.to.SupplierMappingTO;
import org.moldeas.pscs.to.SupplierTO;

public interface SupplierMapper {

	public List<SupplierMappingTO> createMappings(SupplierTO supplierTO);
}
