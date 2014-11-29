package es.kr.inf.uc3m;

import java.util.Comparator;

public class MappingComparator implements Comparator<MappingRequirementTO>{

	public int compare(MappingRequirementTO o1, MappingRequirementTO o2) {
		return Float.compare(o2.confidence, o1.confidence);
	}
	
}