package es.kr.inf.uc3m;

import static org.junit.Assert.*;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

public class RequirementMatcherTest {

	@Test
	public void testCalculateMetrics() {
		ListMappingRequirementTO expectedMappings = new ListMappingRequirementTO();
		expectedMappings.to.add("SR1");
		List<MappingRequirementTO> resultsMappings = new LinkedList<MappingRequirementTO>();
		MappingRequirementTO mapping1 = new MappingRequirementTO();
		mapping1.to = "SR1";
		resultsMappings.add(mapping1);
		MetricMapping metrics = RequirementMatcher.calculateMetrics(expectedMappings,resultsMappings,10);
		System.out.println(metrics);
		assertTrue(Double.compare(metrics.precision, 1.0)==0);
		assertTrue(Double.compare(metrics.recall, 1.0)==0);
	}

	@Test
	public void testCalculateMetrics2() {
		ListMappingRequirementTO expectedMappings = new ListMappingRequirementTO();
		expectedMappings.to.add("SR1");
		List<MappingRequirementTO> resultsMappings = new LinkedList<MappingRequirementTO>();
		MappingRequirementTO mapping1 = new MappingRequirementTO();
		mapping1.to = "SR1";
		MappingRequirementTO mapping2 = new MappingRequirementTO();
		mapping2.to = "SR2";
		resultsMappings.add(mapping1);
		resultsMappings.add(mapping2);
		MetricMapping metrics = RequirementMatcher.calculateMetrics(expectedMappings,resultsMappings,10);
		System.out.println(metrics);
		assertTrue(Double.compare(metrics.precision, 0.500)==0);
		assertTrue(Double.compare(metrics.recall, 1.0)==0);
	}
	
	@Test
	public void testCalculateMetrics3() {
		ListMappingRequirementTO expectedMappings = new ListMappingRequirementTO();
		expectedMappings.to.add("SR1");
		expectedMappings.to.add("SR2");
		List<MappingRequirementTO> resultsMappings = new LinkedList<MappingRequirementTO>();
		MappingRequirementTO mapping1 = new MappingRequirementTO();
		mapping1.to = "SR1";
		MappingRequirementTO mapping2 = new MappingRequirementTO();
		mapping2.to = "SR4";
		resultsMappings.add(mapping1);
		resultsMappings.add(mapping2);
		MetricMapping metrics = RequirementMatcher.calculateMetrics(expectedMappings,resultsMappings,10);
		System.out.println(metrics);
		assertTrue(Double.compare(metrics.precision, 0.500)==0);
		assertTrue(Double.compare(metrics.recall, 0.500)==0);
	}
	@Test
	public void testCalculateMetrics4() {
		ListMappingRequirementTO expectedMappings = new ListMappingRequirementTO();
		expectedMappings.to.add("SR1");
		expectedMappings.to.add("SR2");
		expectedMappings.to.add("SR3");
		List<MappingRequirementTO> resultsMappings = new LinkedList<MappingRequirementTO>();
		MappingRequirementTO mapping1 = new MappingRequirementTO();
		mapping1.to = "SR1";
		MappingRequirementTO mapping2 = new MappingRequirementTO();
		mapping2.to = "SR4";
		MappingRequirementTO mapping3 = new MappingRequirementTO();
		mapping3.to = "SR2";
		resultsMappings.add(mapping1);
		resultsMappings.add(mapping2);
		resultsMappings.add(mapping3);
		MetricMapping metrics = RequirementMatcher.calculateMetrics(expectedMappings,resultsMappings,10);
		System.out.println(metrics);
		assertTrue(Double.compare(metrics.precision, 0.6666666666666666)==0);
		assertTrue(Double.compare(metrics.recall, 0.6666666666666666)==0);
	}
	
}
