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
	String _est_;

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

	@Local
	public MapLambdaTemplate(String code, String id, String[] pars, String est) {
		_code_ = code;
		_id_ = id;
		_pars_ = pars;
		_est_ = est;
	}
}
