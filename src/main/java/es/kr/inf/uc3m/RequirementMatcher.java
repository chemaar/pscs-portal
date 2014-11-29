package es.kr.inf.uc3m;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RequirementMatcher {

	public static void main(String []args) throws Exception{
		MappingComparator mc = new MappingComparator();
		Map<String,ListMappingRequirementTO> expectedMappings = new HashMap<String,ListMappingRequirementTO>();
		//Load stakeholder requirements
		int test = 1;
		String testId = "t"+test;
		String mappingId = "m"+test;
		List<RequirementTO> stakeholderRequirements = loadRequirements("tests//"+testId+"-stakeholder.txt","Stakeholder");
		//Load systems requirements
		List<RequirementTO> systemRequirements = loadRequirements("tests//"+testId+"-system.txt","System");
		System.out.println("Loaded stakeholder requirements: "+stakeholderRequirements.size()+" system: "+systemRequirements.size());
		//Load mappings
		BufferedReader fileReader = new BufferedReader(new FileReader(new File("tests//"+mappingId+"-mappings.txt")));
		String line = fileReader.readLine();
		while(line!=null){
			String fields[] = line.split("#");
			String from = fields[0];
			String to = fields[1];
			ListMappingRequirementTO mapping = expectedMappings.get(from);
			if(mapping!=null){
				mapping.to.add(to);
			}else{
				mapping = new ListMappingRequirementTO();
				mapping.from = from;
				mapping.to.add(to);
				expectedMappings.put(from, mapping);
			}
			line = fileReader.readLine();
		}
		fileReader.close();
		//Load domain ontologies
		//Index systems requirements
		RequirementIndexer indexer = new RequirementIndexer(systemRequirements);
		//For every stakeholder requirement
		for(RequirementTO stakeholderRequirement:stakeholderRequirements){
			List<MappingRequirementTO> extractedMappings = indexer.createMappings(stakeholderRequirement);
			if(extractedMappings.size()>0){
				Collections.sort(extractedMappings, mc);
				float max = extractedMappings.get(0).confidence;
				float min = extractedMappings.get(extractedMappings.size()-1).confidence;	
				System.out.println("Mappings created: "+extractedMappings.size()+" max confidence: "+max+" min: "+min);
				for(MappingRequirementTO mapping:extractedMappings){
					mapping.normalizedConfidence = (float)mapping.confidence-min/(max-min);
				}
			}
			
		}
		//Generate query
		//Search
		//Calculate p, r
		//Generate expanded query
		//Search
		//Calculate p,r
	}

	public static MetricMapping calculateMetrics(
			ListMappingRequirementTO expectedMappings,
			List<MappingRequirementTO> resultsMappings, int take){
		int countTp = 0;
		int countFp = 0;
		int countFn = 0;
		Set<String> resultTo = new HashSet<String>();
		for(MappingRequirementTO mapping:resultsMappings){
			resultTo.add(mapping.to);
			if(expectedMappings.to.contains(mapping.to)){
				countTp++;
			}
		}
		for(String to:expectedMappings.to){
			if(!resultTo.contains(to)){
				countFn++;
			}
		}
		countFp = resultTo.size()-countTp;
		//System.out.println("Expected: "+expectedMappings.to.size()+"Result:"+resultTo.size()+"TP:"+countTp+" FP:"+countFp+" FN: "+countFn);
		double precision = (double) countTp/(countTp+countFp);
		double recall = (double) countTp/(countTp+countFn);
		MetricMapping metrics = new MetricMapping();
		metrics.precision = precision;
		metrics.recall = recall;
		return metrics;
		
	}

	private static List<RequirementTO> loadRequirements(String fileStakeholder,
			String type) throws IOException {
		List<RequirementTO> requirements = new LinkedList<RequirementTO>();
		BufferedReader fileReader = new BufferedReader(new FileReader(new File(fileStakeholder)));
		String line = fileReader.readLine();
		while(line!=null){
			String fields[] = line.split("#");
			String id = fields[0];
			String text = fields[1];
			requirements.add(new RequirementTO(id,text, type));
			line = fileReader.readLine();
		}
		fileReader.close();
		return requirements;
	}
	

	

}
