package es.kr.inf.uc3m;

public class RequirementTO {

	String id;
	String text;
	String type;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public RequirementTO(String id, String text, String type) {
		super();
		this.id = id;
		this.text = text;
		this.type = type;
	}
	
	
}
