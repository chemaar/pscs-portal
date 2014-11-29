package es.kr.inf.uc3m;

public class MappingRequirementTO {

	String from;
	String to;
	float confidence;
	float normalizedConfidence;
	@Override
	public String toString() {
		return "MappingRequirementTO [from=" + from + ", to=" + to
				+ ", confidence=" + confidence + "]";
	}
	
}
