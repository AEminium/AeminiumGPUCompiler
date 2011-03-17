package processing;

import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.template.Substitution;
import spoon.template.Template;
import template.MapLambdaTemplate;

public class MapLambdaProcessor<T>  extends AbstractProcessor<CtMethod<T>>{

	@SuppressWarnings("rawtypes")
	@Override
	public void process(CtMethod<T> target) {
		if (target.getSimpleName().equals("call")) {
			CtClass parent = target.getParent(CtClass.class);
			int c = parent.getMethods().size();
			/*
			Set<CtMethod> methods = parent.getMethods();
			parent.setMethods(methods);
			*/
			
			createGetSourceMethod(target);
			int c2 = parent.getMethods().size();
			System.out.println("Alcides says:" + target.getSimpleName() + " c:" + c + "->" + c2);
			
		}
	}
	
	@SuppressWarnings("unchecked")
	private StringBuilder visitExpr(CtCodeElement expr, StringBuilder b) {
		StringBuilder n = new StringBuilder();
		if (expr instanceof CtBlock) {
			// Iterate over statements
			for (CtStatement st : ((CtBlock<T>) expr).getStatements()) {
				n.append(visitExpr(st, b));
				n.append("\n");
			}
			
		} else if (expr instanceof CtReturn) {
			CtReturn<T> ret = (CtReturn<T>) expr;
			n.append("return ");
			n.append(visitExpr(ret.getReturnedExpression(), b));
			n.append(";");
			
		} else if (expr instanceof CtVariableAccess) {
			CtVariableAccess<?> va = (CtVariableAccess<?>) expr;
			if (va.getVariable().getSimpleName().equals("input")) {
				n.append("input");
			} else {
				// TODO: Check if valid
				System.out.println("Type of var " + va.getVariable().getSimpleName() + " is " + va.getVariable().getType());
				
				b.append(va.getVariable().getSimpleName());
			}
			
		} else {
			System.out.println("Unknown type:" + expr.getClass());
		}
		
		return n;
	}

	private CtMethod<T> createGetSourceMethod(CtMethod<T> target) {
		// Template
		
		StringBuilder clCode = new StringBuilder();
		CtBlock<T> body = target.getBody();
		clCode = visitExpr(body, clCode);
		
		System.out.println(":::: Final Code ::::");
		System.out.println(clCode);
		System.out.println(":: End Final Code ::");
		Template t = new MapLambdaTemplate(clCode.toString());
		Substitution.insertAllMethods(target.getParent(CtClass.class), t);
        return null;
		/*
		
		CtMethod<T> n = new CtMethodImpl<T>();
		
		// Signature
		n.setSimpleName("getSource2");
		n.setParameters(new ArrayList());
		
		// Return Type
		CtTypeReference void_ = new CtTypeReferenceImpl<T>();
		void_.setSimpleName("java.lang.String");
		n.setType(void_);
		
		// Body
		
		
		
		
		return n;*/
	}

}
