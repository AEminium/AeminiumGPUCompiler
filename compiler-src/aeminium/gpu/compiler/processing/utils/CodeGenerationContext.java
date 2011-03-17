package aeminium.gpu.compiler.processing.utils;

import java.util.Stack;

import spoon.reflect.code.CtExpression;
import spoon.reflect.declaration.CtSimpleType;
import spoon.reflect.reference.CtTypeReference;

public class CodeGenerationContext {
	
	public Stack<CtTypeReference<?>> currentThis = new Stack<CtTypeReference<?>>();
	
	public Stack<CtExpression<?>> parenthesedExpression = new Stack<CtExpression<?>>();

	public CtSimpleType<?> currentTopLevel;
	
	public int nbTabs = 0;
	
	public boolean skipArray = false;
	public boolean noTypeDecl = false;
	
	int target = 0;
	int jumped = 0;

	public void enterTarget() {
		target++;
	}

	public void exitTarget() {
		if (jumped > 0) {
			jumped--;
		} else {
			target--;
		}
	}

	public void jumpTarget() {
		jumped++;
		target--;
	}

}
