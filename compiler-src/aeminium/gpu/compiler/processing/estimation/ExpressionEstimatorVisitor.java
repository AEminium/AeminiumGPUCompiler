package aeminium.gpu.compiler.processing.estimation;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import spoon.reflect.code.CtThrow;
import spoon.reflect.code.CtTry;
import spoon.reflect.code.CtUnaryOperator;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.code.CtWhile;
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
import aeminium.gpu.compiler.processing.opencl.MathConverter;

public class ExpressionEstimatorVisitor implements CtVisitor {

	public static int staticLoopSize = 10;
	Estimation estimation = new EstimationStore();
	int multiplier = 1;
	
	int depth=0;
	int[] features = new int[] {
			0, // 0 - outter access (1st, 2nd and 3rd level)
			0,
			0,
			0, // 3 - inner access (1st, 2nd and 3rd level)
			0,
			0,
			0, // 6 - constant access (1st, 2nd and 3rd level)
			0,
			0,
			0, // 9 - outter write (1st, 2nd and 3rd level)
			0,
			0,
			0, // 12 - inner write (1st, 2nd and 3rd level)
			0,
			0,
			0, // 15 - basic (1st, 2nd and 3rd level)
			0,
			0,
			0, // 18 - sin, cos, arc, tan, etc... (1st, 2nd and 3rd level)
			0,
			0,
			0, // 21 - pow, log, ... (1st, 2nd and 3rd level)
			0,
			0,
			0, // 24 - min, ax, ... (1st, 2nd and 3rd level)
			0,
			0
	};
	static Map<String, Integer> funs = new HashMap<String, Integer>();
	static {
		int basic = 15;
		int sin = 18;
		int pow = 21;
		int min = 24;
		funs.put("mul", basic);
		funs.put("plus", basic);
		funs.put("div", basic);
		funs.put("eq", basic);
		funs.put("le", basic);
		funs.put("mod", basic);
		funs.put("minus", basic);
		funs.put("postinc", basic);
		funs.put("sin", sin);
		funs.put("asin", sin);
		funs.put("cos", sin);
		funs.put("acos", sin);
		funs.put("tan", sin);
		funs.put("atan", sin);
		funs.put("pow", pow);
		funs.put("log", pow);
		funs.put("ln", pow);
		funs.put("sqrt", pow);
		funs.put("min", min);
		funs.put("max", min);
	}
	
	private void incFeature(int n) {
		features[n + Math.min(depth, 2)]++;
	}
	
	private void incVariable(String var) {
		incVariable(var, 0);
	}
		
	private void incVariable(String var, int rw) {
		System.out.println("feat: var: " + var  + " rw: " + rw);
	}
	
	private void incFun(String code) {
		if (!funs.containsKey(code)) {
			System.out.println("feat: op: " + code );
		} else {
			incFeature(funs.get(code));
		}
	}

	/* General Scan Functions */
	public ExpressionEstimatorVisitor scan(CtElement e) {
		if (e != null) {
			e.accept(this);
		}
		return this;
	}

	public ExpressionEstimatorVisitor scan(CtReference ref) {
		if (ref != null) {
			ref.accept(this);
		}
		return this;
	}

	@Override
	public <A extends Annotation> void visitCtAnnotation(
			CtAnnotation<A> annotation) {
	}

	@Override
	public <T> void visitCtCodeSnippetExpression(
			CtCodeSnippetExpression<T> expression) {
	}

	@Override
	public void visitCtCodeSnippetStatement(CtCodeSnippetStatement statement) {
	}

	@Override
	public <A extends Annotation> void visitCtAnnotationType(
			CtAnnotationType<A> annotationType) {
	}

	@Override
	public void visitCtAnonymousExecutable(CtAnonymousExecutable anonymousExec) {
	}

	@Override
	public <T, E extends CtExpression<?>> void visitCtArrayAccess(
			CtArrayAccess<T, E> arrayAccess) {
		incVariable(arrayAccess.getIndexExpression().toString());
		incFeature(0);
		estimation.addEstimation("arrayaccess", multiplier);
		scan(arrayAccess.getIndexExpression());
	}

	@Override
	public <T> void visitCtArrayTypeReference(CtArrayTypeReference<T> reference) {
		scan(reference.getComponentType());
	}

	@Override
	public <T> void visitCtAssert(CtAssert<T> asserted) {
	}

	@Override
	public <T, A extends T> void visitCtAssignment(
			CtAssignment<T, A> assignement) {
		incVariable(assignement.getAssigned().toString(), 1);
		scan(assignement.getAssigned());
		scan(assignement.getAssignment());
	}

	@Override
	public <T> void visitCtBinaryOperator(CtBinaryOperator<T> operator) {
		scan(operator.getLeftHandOperand());
		incFun(operator.getKind().name().toLowerCase());
		estimation.addEstimation(operator.getKind().name().toLowerCase(),
				multiplier);
		scan(operator.getRightHandOperand());

	}



	@Override
	public <R> void visitCtBlock(CtBlock<R> block) {
		for (CtStatement e : block.getStatements()) {
			scan(e);
		}

	}

	@Override
	public void visitCtBreak(CtBreak breakStatement) {
	}

	@Override
	public <S> void visitCtCase(CtCase<S> caseStatement) {
		if (caseStatement.getCaseExpression() != null) {
			scan(caseStatement.getCaseExpression());
		}
		for (CtStatement s : caseStatement.getStatements()) {
			scan(s);
		}
	}

	@Override
	public void visitCtCatch(CtCatch catchBlock) {
	}

	@Override
	public <T> void visitCtClass(CtClass<T> ctClass) {
	}

	@Override
	public <T> void visitCtConditional(CtConditional<T> conditional) {
		scan(conditional.getCondition());
		scan(conditional.getThenExpression());
		scan(conditional.getElseExpression());
	}

	@Override
	public <T> void visitCtConstructor(CtConstructor<T> c) {
	}

	@Override
	public void visitCtContinue(CtContinue continueStatement) {
	}

	@Override
	public void visitCtDo(CtDo doLoop) {
		depth++;
		int oldMultiplier = multiplier;
		multiplier *= staticLoopSize;
		scan(doLoop.getBody());
		scan(doLoop.getLoopingExpression());
		multiplier = oldMultiplier;
		depth--;
	}

	@Override
	public <T extends Enum<?>> void visitCtEnum(CtEnum<T> ctEnum) {
	}

	@Override
	public <T> void visitCtExecutableReference(
			CtExecutableReference<T> reference) {
	}

	@Override
	public <T> void visitCtField(CtField<T> f) {
	}

	@SuppressWarnings("rawtypes")
	@Override
	public <T> void visitCtFieldAccess(CtFieldAccess<T> fieldAccess) {
		CtFieldReference var = fieldAccess.getVariable();
		
		if (MathConverter.hasConstant(var.getQualifiedName())) {
			estimation.addEstimation("fieldaccess", multiplier);
			incFeature(6);
		} else if (var.isFinal()) {
			scan(var.getDeclaration().getDefaultExpression());
		}
	}

	@Override
	public <T> void visitCtFieldReference(CtFieldReference<T> reference) {
	}

	@Override
	public void visitCtFor(CtFor forLoop) {
		List<CtStatement> st = forLoop.getForInit();
		if (st.size() > 0) {
			scan(st.get(0));
		}
		if (st.size() > 1) {
			for (int i = 1; i < st.size(); i++) {
				scan(st.get(i));
			}
		}

		int oldMultiplier = multiplier;
		multiplier *= staticLoopSize;
		depth++;

		// TODO: Introspect for loop expression
		scan(forLoop.getExpression());
		for (CtStatement s : forLoop.getForUpdate()) {
			scan(s);
		}
		if (forLoop.getBody() instanceof CtBlock) {
			scan(forLoop.getBody());
		} else {
			scan(forLoop.getBody());
		}
		multiplier = oldMultiplier;
		depth--;
	}

	@Override
	public void visitCtForEach(CtForEach foreach) {
	}

	@Override
	public void visitCtIf(CtIf ifElement) {
		scan(ifElement.getCondition());
		scan(ifElement.getThenStatement());
		if (ifElement.getElseStatement() != null) {
			scan(ifElement.getElseStatement());
		}
	}

	@Override
	public <T> void visitCtInterface(CtInterface<T> intrface) {
	}

	@SuppressWarnings("rawtypes")
	@Override
	public <T> void visitCtInvocation(CtInvocation<T> invocation) {
		CtExecutableReference<T> ex = invocation.getExecutable();

		String qualifiedName = ex.getDeclaringType().getQualifiedName();
		String methodname = ex.getSimpleName();

		if (MathConverter.hasMethod(qualifiedName, methodname)) {
			// MathFunction f = MathConverter.getMathFunction(qualifiedName,
			// methodname);

			incFun(methodname);
			estimation.addEstimation(methodname, multiplier);
			for (CtExpression o : invocation.getArguments()) {
				scan(o);
			}
		}
	}

	@Override
	public <T> void visitCtLiteral(CtLiteral<T> literal) {
	}

	@Override
	public <T> void visitCtLocalVariable(CtLocalVariable<T> localVariable) {
	}

	@Override
	public <T> void visitCtLocalVariableReference(
			CtLocalVariableReference<T> reference) {
	}

	@Override
	public <T> void visitCtMethod(CtMethod<T> m) {
	}

	@Override
	public <T> void visitCtNewArray(CtNewArray<T> newArray) {
		// TODO: alocc?
	}

	@Override
	public <T> void visitCtNewClass(CtNewClass<T> newClass) {
	}

	@Override
	public <T, A extends T> void visitCtOperatorAssignement(
			CtOperatorAssignment<T, A> assignment) {
		incFun(assignment.getKind().toString().toLowerCase());
		incVariable(assignment.getAssigned().toString(), 1);
		estimation.addEstimation(assignment.getKind().toString().toLowerCase(), multiplier);
		scan(assignment.getAssigned());
		scan(assignment.getAssignment());
	}

	@Override
	public void visitCtPackage(CtPackage ctPackage) {
	}

	@Override
	public void visitCtPackageReference(CtPackageReference reference) {
	}

	@Override
	public <T> void visitCtParameter(CtParameter<T> parameter) {
	}

	@Override
	public <T> void visitCtParameterReference(CtParameterReference<T> reference) {
	}

	@Override
	public <R> void visitCtReturn(CtReturn<R> returnStatement) {
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
		scan(switchStatement.getSelector());
		for (CtCase<?> c : switchStatement.getCases()) {
			scan(c);
		}
	}

	@Override
	public void visitCtSynchronized(CtSynchronized synchro) {
	}

	@Override
	public void visitCtThrow(CtThrow throwStatement) {
	}

	@Override
	public void visitCtTry(CtTry tryBlock) {
	}

	@Override
	public void visitCtTypeParameter(CtTypeParameter typeParameter) {
	}

	@Override
	public void visitCtTypeParameterReference(CtTypeParameterReference ref) {
	}

	@Override
	public <T> void visitCtTypeReference(CtTypeReference<T> reference) {
	}

	@Override
	public <T> void visitCtUnaryOperator(CtUnaryOperator<T> operator) {
		scan(operator.getOperand());
		incFun(operator.getKind().name().toLowerCase());
		estimation.addEstimation(operator.getKind().name().toLowerCase(),
				multiplier);

	}

	@Override
	public <T> void visitCtVariableAccess(CtVariableAccess<T> variableAccess) {
		incVariable(variableAccess.getVariable().toString());
		scan(variableAccess.getVariable());
	}

	@Override
	public void visitCtWhile(CtWhile whileLoop) {
		depth++;
		int oldMultiplier = multiplier;
		multiplier *= staticLoopSize;
		scan(whileLoop.getLoopingExpression());
		scan(whileLoop.getBody());
		multiplier = oldMultiplier;
		depth--;
	}

	public String getExpressionString() {
		return estimation.toString();
	}

}
