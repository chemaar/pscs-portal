package org.moldeas.pscs.mappers;

import java.util.List;

import org.moldeas.pscs.to.PSCMappingTO;
import org.moldeas.pscs.to.PSCTO;

public interface PSCMapper {

	public List<PSCMappingTO> createMappings(PSCTO pscTO);
}
