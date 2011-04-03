package aeminium.gpu.compiler.template;

import spoon.template.Local;
import spoon.template.Parameter;
import spoon.template.Template;

public class MapLambdaTemplate implements Template {
	
	@Parameter
	String _code_;
	
	@Parameter
	String _id_;
	
	public String getSource() {
		return _code_;
	}
	
	public String getId() {
		return _id_;
	}

	@Local
	public MapLambdaTemplate(String code, String id) {
		_code_ = code;
		_id_ = id;
	}
}
