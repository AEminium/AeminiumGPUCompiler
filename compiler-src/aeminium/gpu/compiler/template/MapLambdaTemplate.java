package aeminium.gpu.compiler.template;

import spoon.template.Local;
import spoon.template.Parameter;
import spoon.template.Template;

public class MapLambdaTemplate implements Template {

	@Parameter
	String _code_;

	@Parameter
	String _id_;

	@Parameter
	String[] _pars_;

	@Parameter
	String _ot_;
	
	@Parameter
	String _est_;
	
	@Parameter
	String _feat_;

	public String getSource() {
		return _code_;
	}

	public String getId() {
		return _id_;
	}

	public String[] getParameters() {
		return _pars_;
	}
	
	public String getOutputType() {
		return _ot_;
	}
	
	public String getSourceComplexity() {
		return _est_;
	}

	public String getFeatures() {
		return _feat_;
	}

	@Local
	public MapLambdaTemplate(String code, String id, String[] pars, String ot, String est, String features) {
		_code_ = code;
		_id_ = id;
		_pars_ = pars;
		_ot_ = ot;
		_est_ = est;
		_feat_ = features;
	}
}
