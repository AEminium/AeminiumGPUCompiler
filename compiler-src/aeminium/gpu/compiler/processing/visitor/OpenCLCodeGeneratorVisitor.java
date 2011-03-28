package aeminium.gpu.compiler.processing.visitor;

import java.lang.annotation.Annotation;
import java.util.List;

import spoon.processing.Environment;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtArrayAccess;
import spoon.reflect.code.CtAssert;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtBreak;
import spoon.reflect.code.CtCase;
import spoon.reflect.code.CtCatch;
import spoon.reflect.code.CtCodeSnippetExpression;
import spoon.reflect.code.CtCodeSnippetStatement;
import spoon.reflect.code.CtConditional;
import spoon.reflect.code.CtContinue;
import spoon.reflect.code.CtDo;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.code.CtFor;
import spoon.reflect.code.CtForEach;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtNewArray;
import spoon.reflect.code.CtNewClass;
import spoon.reflect.code.CtOperatorAssignment;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtStatementList;
import spoon.reflect.code.CtSwitch;
import spoon.reflect.code.CtSynchronized;
import spoon.reflect.code.CtTargetedExpression;
import spoon.reflect.code.CtThrow;
import spoon.reflect.code.CtTry;
import spoon.reflect.code.CtUnaryOperator;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.code.CtWhile;
import spoon.reflect.code.UnaryOperatorKind;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtAnnotationType;
import spoon.reflect.declaration.CtAnonymousExecutable;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtEnum;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtLocalVariableReference;
import spoon.reflect.reference.CtPackageReference;
import spoon.reflect.reference.CtParameterReference;
import spoon.reflect.reference.CtReference;
import spoon.reflect.reference.CtTypeParameterReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtVisitor;
import aeminium.gpu.compiler.processing.opencl.BoxedTypes;
import aeminium.gpu.compiler.processing.opencl.CLType;
import aeminium.gpu.compiler.processing.opencl.MathConverter;
import aeminium.gpu.compiler.processing.opencl.MathFunction;
import aeminium.gpu.compiler.processing.utils.CodeGenerationContext;

public class OpenCLCodeGeneratorVisitor implements CtVisitor {
	
	private boolean isPossible = true;
	
	protected String input_var;
	protected Environment env;
	protected CodeGenerationContext context = new CodeGenerationContext();
	private StringBuffer sbf = new StringBuffer();
	
	public OpenCLCodeGeneratorVisitor(Environment e, String var) {
		env = e;
		input_var = var;
	}
	
	/* Wanted Methods */
	
	public boolean canBeGenerated() {
		return isPossible;
	}
	
	public String toString() {
		return sbf.toString();
	}
	
	/* General Scan Functions */
	public OpenCLCodeGeneratorVisitor scan(CtElement e) {
		if (e != null) {
			e.accept(this);
		}
		return this;
	}
	
	public OpenCLCodeGeneratorVisitor scan(CtReference ref) {
		if (ref != null) {
			ref.accept(this);
		}
		return this;
	}
	
	public OpenCLCodeGeneratorVisitor write(String s) {
		if (s != null) {
			sbf.append(s);
		}
		return this;
	}
	
	private OpenCLCodeGeneratorVisitor writeln() {
		write("\n");
		return this;
	}
	
	private boolean isWhite(char c) {
		return (c == ' ') || (c == '\t') || (c == '\n');
	}
	
	private OpenCLCodeGeneratorVisitor removeLastChar() {
		while (isWhite(sbf.charAt(sbf.length() - 1))) {
			sbf.deleteCharAt(sbf.length() - 1);
		}
		sbf.deleteCharAt(sbf.length() - 1);
		while (isWhite(sbf.charAt(sbf.length() - 1))) {
			sbf.deleteCharAt(sbf.length() - 1);
		}
		return this;
	}
	
	public static String quote(String s) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < s.length(); i++) {
			char ch = s.charAt(i);
			switch (ch) {
			case '\n':
				buf.append("\\n");
				break;
			case '\t':
				buf.append("\\t");
				break;
			case '\b':
				buf.append("\\b");
				break;
			case '\f':
				buf.append("\\f");
				break;
			case '\r':
				buf.append("\\r");
				break;
			case '\"':
				buf.append("\\\"");
				break;
			case '\'':
				buf.append("\\\'");
				break;
			case '\\':
				buf.append("\\\\");
				break;
			default:
				// if (ch < 32 || 128 <= ch && ch < 255) {
				// buf.append("\\");
				// buf.append((char) ('0' + (ch >> 6) % 8));
				// buf.append((char) ('0' + (ch >> 3) % 8));
				// buf.append((char) ('0' + (ch) % 8));
				// } else {
				buf.append(ch);
				// }
			}
		}
		return buf.toString();
	}
	
	private void cancelConversion(Object o) {
		isPossible = false;
		if (env.isVerbose()) {
			System.out.println("CL conversion impossible because of: " + o + "," + o.getClass());
		}
	}
	
	
	/* Entering and Leaving */
	protected void enterCtExpression(CtExpression<?> e) {
		if (shouldSetBracket(e)) {
			context.parenthesedExpression.push(e);
			write("(");
		}
		if (!e.getTypeCasts().isEmpty()) {
			for (CtTypeReference<?> r : e.getTypeCasts()) {
				write("(");
				scan(r);
				write(")");
				write("(");
				context.parenthesedExpression.push(e);
			}
		}
	}

	protected void enterCtStatement(CtStatement s) {
		if (s.getLabel() != null) {
			write(s.getLabel()).write(" : ");
		}
	}

	protected void exitCtExpression(CtExpression<?> e) {
		while ((context.parenthesedExpression.size() > 0)
				&& e.equals(context.parenthesedExpression.peek())) {
			context.parenthesedExpression.pop();
			write(")");
		}
	}
	
	private boolean shouldSetBracket(CtExpression<?> e) {
		if (e.getTypeCasts().size() != 0) {
			return true;
		}
		if ((e.getParent() instanceof CtBinaryOperator)
				|| (e.getParent() instanceof CtUnaryOperator)) {
			return (e instanceof CtTargetedExpression)
					|| (e instanceof CtAssignment)
					|| (e instanceof CtConditional)
					|| (e instanceof CtUnaryOperator);
		}
		if (e.getParent() instanceof CtTargetedExpression) {
			return (e instanceof CtBinaryOperator)
					|| (e instanceof CtAssignment)
					|| (e instanceof CtConditional);
		}
	
		return false;
	}
	
	/* Tabs */
	
	public OpenCLCodeGeneratorVisitor incTab() {
		context.nbTabs++;
		return this;
	}
	
	public OpenCLCodeGeneratorVisitor decTab() {
		context.nbTabs--;
		return this;
	}
	
	public OpenCLCodeGeneratorVisitor setTabCount(int tabCount) {
		context.nbTabs = tabCount;
		return this;
	}
	
	/* Code snippets */
	
	private void writeStatement(CtStatement e) {
		if ( (e instanceof CtTry) 
			|| (e instanceof CtSynchronized)
			|| (e instanceof CtForEach)
		) {
			cancelConversion(e);
		} else {
			scan(e);
			if (!((e instanceof CtBlock) || (e instanceof CtIf)
					|| (e instanceof CtFor) 
					|| (e instanceof CtWhile) || (e instanceof CtSwitch) )) {
				write(";");
			}
		}
	}
	
	private <T> OpenCLCodeGeneratorVisitor writeLocalVariable(CtLocalVariable<T> localVariable) {
		if (!context.noTypeDecl) {
			scan(localVariable.getType());
			write(" ");
		}
		write(localVariable.getSimpleName());
		if (localVariable.getDefaultExpression() != null) {
			write(" = ");
			scan(localVariable.getDefaultExpression());
		}
		return this;
	}
	
	private OpenCLCodeGeneratorVisitor writeOperator(BinaryOperatorKind o) {
			switch (o) {
			case OR:
				write("||");
				break;
			case AND:
				write("&&");
				break;
			case BITOR:
				write("|");
				break;
			case BITXOR:
				write("^");
				break;
			case BITAND:
				write("&");
				break;
			case EQ:
				write("==");
				break;
			case NE:
				write("!=");
				break;
			case LT:
				write("<");
				break;
			case GT:
				write(">");
				break;
			case LE:
				write("<=");
				break;
			case GE:
				write(">=");
				break;
			case SL:
				write("<<");
				break;
			case SR:
				write(">>");
				break;
			case USR:
				write(">>>");
				break;
			case PLUS:
				write("+");
				break;
			case MINUS:
				write("-");
				break;
			case MUL:
				write("*");
				break;
			case DIV:
				write("/");
				break;
			case MOD:
				write("%");
				break;
			case INSTANCEOF:
				cancelConversion("instanceof");
				break;
			}
			return this;
	}
	
	
	void preWriteUnaryOperator(UnaryOperatorKind o) {
		switch (o) {
		case POS:
			write("+");
			break;
		case NEG:
			write("-");
			break;
		case NOT:
			write("!");
			break;
		case COMPL:
			write("~");
			break;
		case PREINC:
			write("++");
			break;
		case PREDEC:
			write("--");
			break;
		}
	}
	
	protected void postWriteUnaryOperator(UnaryOperatorKind o) {
		switch (o) {
		case POSTINC:
			write("++");
			break;
		case POSTDEC:
			write("--");
			break;
		}
	}
	
	
	/* AST Parsing */
	
	@Override
	public <A extends Annotation> void visitCtAnnotation(
			CtAnnotation<A> annotation) {
		// Ignore Annotations
		// TODO: Idea: Add OpenCL version of code via annotations.
	}


	@Override
	public <T> void visitCtCodeSnippetExpression(
			CtCodeSnippetExpression<T> expression) {
		// Ignore for compiler
	}


	@Override
	public void visitCtCodeSnippetStatement(CtCodeSnippetStatement statement) {
		// Ignore for compiler
		
	}


	@Override
	public <A extends Annotation> void visitCtAnnotationType(
			CtAnnotationType<A> annotationType) {
		// Nothing to do
	}


	@Override
	public void visitCtAnonymousExecutable(CtAnonymousExecutable anonymousExec) {
		// Ignore for Compiler
		
	}


	@Override
	public <T, E extends CtExpression<?>> void visitCtArrayAccess(
			CtArrayAccess<T, E> arrayAccess) {
		scan(arrayAccess.getTarget());
		write("[");
		scan(arrayAccess.getIndexExpression());
		write("]");
	}


	@Override
	public <T> void visitCtArrayTypeReference(CtArrayTypeReference<T> reference) {
		scan(reference.getComponentType());
	}

	

	@Override
	public <T> void visitCtAssert(CtAssert<T> asserted) {
		cancelConversion(asserted);
	}

	@Override
	public <T, A extends T> void visitCtAssignment(
			CtAssignment<T, A> assignement) {
		enterCtStatement(assignement);
		enterCtExpression(assignement);
		scan(assignement.getAssigned());
		write(" = ");
		scan(assignement.getAssignment());
		exitCtExpression(assignement);
	}


	@Override
	public <T> void visitCtBinaryOperator(CtBinaryOperator<T> operator) {
		enterCtExpression(operator);
		boolean paren = (operator.getParent() instanceof CtBinaryOperator)
				|| (operator.getParent() instanceof CtUnaryOperator);
		if (paren) {
			write("(");
		}
		scan(operator.getLeftHandOperand());
		write(" ").writeOperator(operator.getKind()).write(" ");
		scan(operator.getRightHandOperand());
		if (paren) {
			write(")");
		}
		exitCtExpression(operator);
	}




	@Override
	public <R> void visitCtBlock(CtBlock<R> block) {
		enterCtStatement(block);
		write("{").incTab();
		for (CtStatement e : block.getStatements()) {
			if (!e.isImplicit()) {
				writeln();
				writeStatement(e);
			}
		}
		decTab().writeln().write("}");
		
	}

	@Override
	public void visitCtBreak(CtBreak breakStatement) {
		enterCtStatement(breakStatement);
		write("break");
		if (breakStatement.getTargetLabel() != null) {
			write(" " + breakStatement.getTargetLabel());
		}
	}


	@SuppressWarnings("rawtypes")
	@Override
	public <S> void visitCtCase(CtCase<S> caseStatement) {
		enterCtStatement(caseStatement);
		if (caseStatement.getCaseExpression() != null) {
			write("case ");
			// writing enum case expression
			if ((caseStatement.getCaseExpression() instanceof CtFieldAccess)
					&& ((CtFieldAccess) caseStatement.getCaseExpression())
							.getVariable().getType().getQualifiedName().equals(
									((CtFieldAccess) caseStatement
											.getCaseExpression()).getVariable()
											.getDeclaringType()
											.getQualifiedName())) {
				write(((CtFieldAccess) caseStatement.getCaseExpression())
						.getVariable().getSimpleName());
			} else {
				scan(caseStatement.getCaseExpression());
			}
		} else {
			write("default");
		}
		write(" :").incTab();

		for (CtStatement s : caseStatement.getStatements()) {
			writeln().writeStatement(s);
		}
		decTab();
		
	}


	@Override
	public void visitCtCatch(CtCatch catchBlock) {
		// Should not be required, just in case.
		cancelConversion(catchBlock);
	}


	@Override
	public <T> void visitCtClass(CtClass<T> ctClass) {
		cancelConversion(ctClass);
	}


	@Override
	public <T> void visitCtConditional(CtConditional<T> conditional) {
		enterCtExpression(conditional);
		scan(conditional.getCondition());
		write(" ? ");
		scan(conditional.getThenExpression());
		write(" : ");
		scan(conditional.getElseExpression());
		exitCtExpression(conditional);
	}


	@Override
	public <T> void visitCtConstructor(CtConstructor<T> c) {
		// TODO: Need to support Integers and Other Wrappers
		cancelConversion(c);
	}


	@Override
	public void visitCtContinue(CtContinue continueStatement) {
		enterCtStatement(continueStatement);
		write("continue");
	}


	@Override
	public void visitCtDo(CtDo doLoop) {
		enterCtStatement(doLoop);
		write("do ");
		writeStatement(doLoop.getBody());
		write(" while (");
		scan(doLoop.getLoopingExpression());
		write(" )");		
	}


	@Override
	public <T extends Enum<?>> void visitCtEnum(CtEnum<T> ctEnum) {
		cancelConversion(ctEnum);
	}


	@Override
	public <T> void visitCtExecutableReference(
			CtExecutableReference<T> reference) {
		// Ignore for compiler
	}


	@Override
	public <T> void visitCtField(CtField<T> f) {
		// Should not get here.
		cancelConversion(f);
	}


	@SuppressWarnings("rawtypes")
	@Override
	public <T> void visitCtFieldAccess(CtFieldAccess<T> fieldAccess) {
		CtFieldReference var = fieldAccess.getVariable();
		
		if (MathConverter.hasConstant(var.getQualifiedName())) {
			write(MathConverter.getConstant(var.getQualifiedName()));
		} else if (var.isFinal()) {
			scan(var.getDeclaration().getDefaultExpression());
		} else {
			cancelConversion(fieldAccess);
		}
	}


	@Override
	public <T> void visitCtFieldReference(CtFieldReference<T> reference) {
		// TODO: Implement Math stuff.
		cancelConversion(reference);
	}


	@Override
	public void visitCtFor(CtFor forLoop) {
		enterCtStatement(forLoop);
		write("for (");
		List<CtStatement> st = forLoop.getForInit();
		if (st.size() > 0) {
			scan(st.get(0));
		}
		if (st.size() > 1) {
			context.noTypeDecl = true;
			for (int i = 1; i < st.size(); i++) {
				write(", ");
				scan(st.get(i));
			}
			context.noTypeDecl = false;
		}
		write(" ; ");
		scan(forLoop.getExpression());
		write(" ; ");
		for (CtStatement s : forLoop.getForUpdate()) {
			scan(s);
			write(" , ");
		}
		if (forLoop.getForUpdate().size() > 0) {
			removeLastChar();
		}
		write(")");
		if (forLoop.getBody() instanceof CtBlock) {
			write(" ");
			scan(forLoop.getBody());
		} else {
			incTab().writeln();
			writeStatement(forLoop.getBody());
			decTab();
		}
	}


	@Override
	public void visitCtForEach(CtForEach foreach) {
		cancelConversion(foreach);
		// TODO: Maybe go through the array_list or sth?
	}


	@Override
	public void visitCtIf(CtIf ifElement) {
		enterCtStatement(ifElement);
		write("if (");
		scan(ifElement.getCondition());
		write(")");
		if (ifElement.getThenStatement() instanceof CtBlock) {
			write(" ");
			scan(ifElement.getThenStatement());
			write(" ");
		} else {
			incTab().writeln();
			writeStatement(ifElement.getThenStatement());
			decTab().writeln();
		}
		if (ifElement.getElseStatement() != null) {
			write("else");
			if (ifElement.getElseStatement() instanceof CtIf) {
				write(" ");
				scan(ifElement.getElseStatement());
			} else if (ifElement.getElseStatement() instanceof CtBlock) {
				write(" ");
				scan(ifElement.getElseStatement());
			} else {
				incTab().writeln();
				writeStatement(ifElement.getElseStatement());
				decTab().writeln();
			}
		}
	}


	@Override
	public <T> void visitCtInterface(CtInterface<T> intrface) {
		cancelConversion(intrface);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public <T> void visitCtInvocation(CtInvocation<T> invocation) {
		CtExecutableReference<T> ex = invocation.getExecutable();
		
		String qualifiedName = ex.getDeclaringType().getQualifiedName();
		String methodname = ex.getSimpleName();
		
		if (MathConverter.hasMethod(qualifiedName, methodname)) {
			MathFunction f = MathConverter.getMathFunction(qualifiedName, methodname);
			
			enterCtExpression(invocation);
			
			// Ensure OpenCL and Java compability
			write(CastHelper.getReturnTypeCast(f.getReturnType(), ex.getType()));
			
			write(f.getOpenCLName());
			
			write("(");
			boolean remove = false; 
			int argIndex = 0;
			CLType[] innerCasts = f.getArgumentTypes();
			for (CtExpression o: invocation.getArguments()) {
				
				if (innerCasts.length > argIndex) {
					// Check arguments for required casts
					write(CastHelper.getReturnTypeCast(innerCasts[argIndex], o.getType()));
				}
				
				o.accept(this);
				remove = true;
				write(", ");
				argIndex++;
			}
			if (remove) {
				removeLastChar();
			}
			write(")");
			exitCtExpression(invocation);
		} else {
			cancelConversion(invocation);
		}
	}


	@Override
	public <T> void visitCtLiteral(CtLiteral<T> literal) {
		enterCtExpression(literal);
		if (literal.getValue() == null) {
			write("null");
		} else if (literal.getValue() instanceof Long) {
			write(literal.getValue()+"");
		} else if (literal.getValue() instanceof Float) {
			write(literal.getValue()+"");
		} else if (literal.getValue() instanceof Character) {
			write("'");
			write(quote(String.valueOf(literal.getValue())));
			write("'");
		} else if (literal.getValue() instanceof String) {
			// TODO: String 
			//write("\"" + quote((String) literal.getValue()) + "\"");
			cancelConversion(literal.getValue());
		} else if (literal.getValue() instanceof Class) {
			// TODO: Math stuff
			cancelConversion(literal.getValue());
			//write(((Class<?>) literal.getValue()).getName());
		} else if (literal.getValue() instanceof CtReference) {
			scan((CtReference) literal.getValue());
		} else {
			write(literal.getValue().toString());
		}
		exitCtExpression(literal);
		
	}


	@Override
	public <T> void visitCtLocalVariable(CtLocalVariable<T> localVariable) {
		enterCtStatement(localVariable);
		writeLocalVariable(localVariable);
	}


	@Override
	public <T> void visitCtLocalVariableReference(
			CtLocalVariableReference<T> reference) {
		write(reference.getSimpleName());
	}


	@Override
	public <T> void visitCtMethod(CtMethod<T> m) {
		// TODO: Allow for method definition
		cancelConversion(m);
	}


	@SuppressWarnings("rawtypes")
	@Override
	public <T> void visitCtNewArray(CtNewArray<T> newArray) {
		enterCtExpression(newArray);

		if (!(context.currentTopLevel instanceof CtAnnotationType)) {
			CtTypeReference<?> ref = newArray.getType();

			if (ref != null) {
				write("new ");
			}

			context.skipArray = true;
			scan(ref);
			context.skipArray = false;
			for (int i = 0; ref instanceof CtArrayTypeReference; i++) {
				write("[");
				if (newArray.getDimensionExpressions().size() > i) {
					scan(newArray.getDimensionExpressions().get(i));
				}
				write("]");
				ref = ((CtArrayTypeReference) ref).getComponentType();
			}
		}
		if (newArray.getDimensionExpressions().size() == 0) {
			write("{ ");
			for (CtExpression e : newArray.getElements()) {
				scan(e);
				write(" , ");
			}
			if (newArray.getElements().size() > 0) {
				removeLastChar();
			}
			write(" }");
		}
		exitCtExpression(newArray);
	}


	@Override
	public <T> void visitCtNewClass(CtNewClass<T> newClass) {
		String kl = newClass.getExecutable().getDeclaringType().getQualifiedName();
		
		if (BoxedTypes.hasClass(kl)) {
			scan(newClass.getArguments().get(0));
		} else {
			cancelConversion(newClass);
		}
	}


	@Override
	public <T, A extends T> void visitCtOperatorAssignement(
			CtOperatorAssignment<T, A> assignment) {
		enterCtStatement(assignment);
		enterCtExpression(assignment);
		scan(assignment.getAssigned());
		write(" ");
		writeOperator(assignment.getKind());
		write("= ");
		scan(assignment.getAssignment());
		exitCtExpression(assignment);
	}


	@Override
	public void visitCtPackage(CtPackage ctPackage) {
		// Should not get here.
		cancelConversion(ctPackage);
	}


	@Override
	public void visitCtPackageReference(CtPackageReference reference) {
		// Not supported.
		// TODO: What about Math?
		cancelConversion(reference);
	}


	@Override
	public <T> void visitCtParameter(CtParameter<T> parameter) {
		// Only to define functions
	}


	@Override
	public <T> void visitCtParameterReference(CtParameterReference<T> reference) {
		if (reference.getSimpleName().equals(input_var)) {
			write("input");
		} else {
			write(reference.getSimpleName());	
		}
	}


	@Override
	public <R> void visitCtReturn(CtReturn<R> returnStatement) {
		enterCtStatement(returnStatement);
		write("return ");
		scan(returnStatement.getReturnedExpression());
	}


	@Override
	public <R> void visitCtStatementList(CtStatementList<R> statements) {
		for (CtStatement s : statements.getStatements()) {
			scan(s);
		}
	}


	@Override
	public <S> void visitCtSwitch(CtSwitch<S> switchStatement) {
		enterCtStatement(switchStatement);
		write("switch (");
		scan(switchStatement.getSelector());
		write(") {").incTab();
		for (CtCase<?> c : switchStatement.getCases()) {
			writeln().scan(c);
		}
		decTab().writeln().write("}");
	}


	@Override
	public void visitCtSynchronized(CtSynchronized synchro) {
		// TODO: Barrier?
		cancelConversion(synchro);
	}


	@Override
	public void visitCtThrow(CtThrow throwStatement) {
		cancelConversion(throwStatement);
	}


	@Override
	public void visitCtTry(CtTry tryBlock) {
		cancelConversion(tryBlock);
	}


	@Override
	public void visitCtTypeParameter(CtTypeParameter typeParameter) {
		// TODO: Some Generalization?
		cancelConversion(typeParameter);
	}


	@Override
	public void visitCtTypeParameterReference(CtTypeParameterReference ref) {
		// TODO: Some Generalization?
		cancelConversion(ref);
	}


	@Override
	public <T> void visitCtTypeReference(CtTypeReference<T> reference) {
		// TODO: Verify this. addImport?
		if (!(reference instanceof CtArrayTypeReference)) {
			if (reference.getDeclaringType() == null) {
				write(reference.getQualifiedName());
			} else {
				write(reference.getDeclaringType().getQualifiedName());
			}
		}
	}


	@Override
	public <T> void visitCtUnaryOperator(CtUnaryOperator<T> operator) {
		enterCtStatement(operator);
		enterCtExpression(operator);
		preWriteUnaryOperator(operator.getKind());
		context.enterTarget();
		scan(operator.getOperand());
		context.exitTarget();
		postWriteUnaryOperator(operator.getKind());
		exitCtExpression(operator);
	}


	@Override
	public <T> void visitCtVariableAccess(CtVariableAccess<T> variableAccess) {
		enterCtExpression(variableAccess);
		scan(variableAccess.getVariable());
		exitCtExpression(variableAccess);
		
	}


	@Override
	public void visitCtWhile(CtWhile whileLoop) {
		enterCtStatement(whileLoop);
		write("while (");
		scan(whileLoop.getLoopingExpression());
		write(")");

		if (whileLoop.getBody() instanceof CtBlock) {
			write(" ");
			scan(whileLoop.getBody());
		} else {
			incTab().writeln();
			writeStatement(whileLoop.getBody());
			decTab();
		}
	}

	
}
