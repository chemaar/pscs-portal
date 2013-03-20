package org.moldeas.pscs.mappers;

import static org.junit.Assert.*;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.moldeas.common.loader.resources.FilesResourceLoader;
import org.moldeas.common.loader.resources.ResourceLoader;
import org.moldeas.pscs.to.PSCTO;



public class CPV2008MapperTest {


	public void testLoad() {
		ResourceLoader loader = new FilesResourceLoader(new String[]{CPV2008Mapper.getSource()});
		CPV2008Mapper cpv2008mapper = new CPV2008Mapper(loader);
		assertEquals(0, cpv2008mapper.createMappings(new PSCTO()).size());
	}
	@Test
	public void testMapping(){
		PSCTO from = new PSCTO();
		from.setUri("http://moldeas.org/p1");
		from.setPrefLabel("services");
		List<PSCTO> targets = new LinkedList<PSCTO>();
		targets.add(from);
		ResourceLoader loader = new FilesResourceLoader(new String[]{CPV2008Mapper.getSource()});
		CPV2008Mapper cpv2008mapper = new CPV2008Mapper(loader);
		assertEquals(3, cpv2008mapper.createMappings(targets).size());
	}

}
