package aeminium.gpu.compiler.template;

import spoon.template.Local;
import spoon.template.Parameter;
import spoon.template.Template;

public class ReduceLambdaTemplate implements Template {
	
	@Parameter
	String _code_;
	
	@Parameter
	String _seedcode_;
	
	@Parameter
	String _id_;
	
	@Parameter
	String[] _pars_;
	
	public String getSeedSource() {
		return _seedcode_;
	}
	
	public String getSource() {
		return _code_;
	}
	
	public String getId() {
		return _id_;
	}
	
	public String[] getParameters() {
		return _pars_;
	}

	@Local
	public ReduceLambdaTemplate(String code, String seedcode, String id, String[] pars) {
		_code_ = code;
		_seedcode_ = seedcode;
		_id_ = id;
		_pars_ = pars;
	}
}
