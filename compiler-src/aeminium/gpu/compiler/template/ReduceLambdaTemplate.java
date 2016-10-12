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

	@Parameter
	String _est_;
	
	@Parameter
	String _feat_;

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

	public String getSourceComplexity() {
		return _est_;
	}
	
	public String getFeatures() {
		return _feat_;
	}

	@Local
	public ReduceLambdaTemplate(String code, String seedcode, String id,
			String[] pars, String est, String features) {
		_code_ = code;
		_seedcode_ = seedcode;
		_id_ = id;
		_pars_ = pars;
		_est_ = est;
		_feat_ = features;
	}
}
