package es.kr.inf.uc3m;

public class MetricMapping {

	double precision = 1.0;
	double recall = 1.0;
	public double getPrecision() {
		return precision;
	}
	public void setPrecision(double precision) {
		this.precision = precision;
	}
	public double getRecall() {
		return recall;
	}
	public void setRecall(double recall) {
		this.recall = recall;
	}
	@Override
	public String toString() {
		return "MetricMapping [precision=" + precision + ", recall=" + recall
				+ "]";
	}
	
}
