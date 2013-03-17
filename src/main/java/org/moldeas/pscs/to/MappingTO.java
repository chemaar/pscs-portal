package org.moldeas.pscs.to;

public class MappingTO {

	private PSCTO from;
	private PSCTO to;
	private double confidence;
	public PSCTO getFrom() {
		return from;
	}
	public void setFrom(PSCTO from) {
		this.from = from;
	}
	public PSCTO getTo() {
		return to;
	}
	public void setTo(PSCTO to) {
		this.to = to;
	}
	public double getConfidence() {
		return confidence;
	}
	public void setConfidence(double confidence) {
		this.confidence = confidence;
	}
	public MappingTO(PSCTO from, PSCTO to, double confidence) {
		super();
		this.from = from;
		this.to = to;
		this.confidence = confidence;
	}
	public MappingTO() {
		super();
		// TODO Auto-generated constructor stub
	}
	@Override
	public String toString() {
		return "MappingTO [from=" + from + ", to=" + to + ", confidence="
				+ confidence + "]";
	}
	
}
