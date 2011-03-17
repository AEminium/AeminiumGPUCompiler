package template;

import spoon.template.Local;
import spoon.template.Parameter;
import spoon.template.Template;

public class MapLambdaTemplate implements Template {
	
	
	@Parameter
	String _code_;
	
	public String getSource() {
		return _code_;
	}

	@Local
	public MapLambdaTemplate(String code) {
		_code_ = code;
	}
}
