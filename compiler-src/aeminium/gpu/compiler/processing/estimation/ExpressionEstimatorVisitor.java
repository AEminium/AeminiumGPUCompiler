package aeminium.gpu.compiler.processing.estimation;

import java.lang.annotation.Annotation;
import java.util.List;

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

	Estimation estimation = new EstimationStore();
	int multiplier = 1;
	
	public static int staticLoopSize = 10;
	
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
			CtAnnotation<A> annotation) {}

	@Override
	public <T> void visitCtCodeSnippetExpression(
			CtCodeSnippetExpression<T> expression) {}

	@Override
	public void visitCtCodeSnippetStatement(CtCodeSnippetStatement statement) {}

	@Override
	public <A extends Annotation> void visitCtAnnotationType(
			CtAnnotationType<A> annotationType) {}

	@Override
	public void visitCtAnonymousExecutable(CtAnonymousExecutable anonymousExec) {}

	@Override
	public <T, E extends CtExpression<?>> void visitCtArrayAccess(
			CtArrayAccess<T, E> arrayAccess) {
		estimation.addEstimation("arrayaccess", multiplier);
		scan(arrayAccess.getIndexExpression());
	}

	@Override
	public <T> void visitCtArrayTypeReference(CtArrayTypeReference<T> reference) {
		scan(reference.getComponentType());
	}

	@Override
	public <T> void visitCtAssert(CtAssert<T> asserted) {}

	@Override
	public <T, A extends T> void visitCtAssignment(
			CtAssignment<T, A> assignement) {
		scan(assignement.getAssigned());
		scan(assignement.getAssignment());
	}

	@Override
	public <T> void visitCtBinaryOperator(CtBinaryOperator<T> operator) {
		scan(operator.getLeftHandOperand());
		estimation.addEstimation(operator.getKind().name().toLowerCase(), multiplier);
		scan(operator.getRightHandOperand());
		
	}

	@Override
	public <R> void visitCtBlock(CtBlock<R> block) {
		for (CtStatement e : block.getStatements()) {
			scan(e);
		}
		
	}

	@Override
	public void visitCtBreak(CtBreak breakStatement) {}

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
	public void visitCtCatch(CtCatch catchBlock) {}

	@Override
	public <T> void visitCtClass(CtClass<T> ctClass) {}

	@Override
	public <T> void visitCtConditional(CtConditional<T> conditional) {
		scan(conditional.getCondition());
		scan(conditional.getThenExpression());
		scan(conditional.getElseExpression());
	}

	@Override
	public <T> void visitCtConstructor(CtConstructor<T> c) {}

	@Override
	public void visitCtContinue(CtContinue continueStatement) {}

	@Override
	public void visitCtDo(CtDo doLoop) {
		int oldMultiplier = multiplier;
		multiplier *= staticLoopSize;
		scan(doLoop.getBody());
		scan(doLoop.getLoopingExpression());
		multiplier = oldMultiplier;
	}

	@Override
	public <T extends Enum<?>> void visitCtEnum(CtEnum<T> ctEnum) {}

	@Override
	public <T> void visitCtExecutableReference(
			CtExecutableReference<T> reference) {}

	@Override
	public <T> void visitCtField(CtField<T> f) {}

	@SuppressWarnings("rawtypes")
	@Override
	public <T> void visitCtFieldAccess(CtFieldAccess<T> fieldAccess) {
		CtFieldReference var = fieldAccess.getVariable();
		if (MathConverter.hasConstant(var.getQualifiedName())) {
			estimation.addEstimation("fieldaccess",multiplier);
		} else if (var.isFinal()) {
			scan(var.getDeclaration().getDefaultExpression());
		}
	}

	@Override
	public <T> void visitCtFieldReference(CtFieldReference<T> reference) {}

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
	}

	@Override
	public void visitCtForEach(CtForEach foreach) {}

	@Override
	public void visitCtIf(CtIf ifElement) {
		scan(ifElement.getCondition());
		scan(ifElement.getThenStatement());
		if (ifElement.getElseStatement() != null) {
			scan(ifElement.getElseStatement());
		}
	}

	@Override
	public <T> void visitCtInterface(CtInterface<T> intrface) {}

	@SuppressWarnings("rawtypes")
	@Override
	public <T> void visitCtInvocation(CtInvocation<T> invocation) {
		CtExecutableReference<T> ex = invocation.getExecutable();
		
		String qualifiedName = ex.getDeclaringType().getQualifiedName();
		String methodname = ex.getSimpleName();
		
		if (MathConverter.hasMethod(qualifiedName, methodname)) {
			// MathFunction f = MathConverter.getMathFunction(qualifiedName, methodname);
			
			estimation.addEstimation(methodname, multiplier);
			for (CtExpression o: invocation.getArguments()) {
				scan(o);
			}
		}
	}

	@Override
	public <T> void visitCtLiteral(CtLiteral<T> literal) {}

	@Override
	public <T> void visitCtLocalVariable(CtLocalVariable<T> localVariable) {}

	@Override
	public <T> void visitCtLocalVariableReference(
			CtLocalVariableReference<T> reference) {}

	@Override
	public <T> void visitCtMethod(CtMethod<T> m) {}

	@Override
	public <T> void visitCtNewArray(CtNewArray<T> newArray) {
		// TODO: alocc?
	}

	@Override
	public <T> void visitCtNewClass(CtNewClass<T> newClass) {}

	@Override
	public <T, A extends T> void visitCtOperatorAssignement(
			CtOperatorAssignment<T, A> assignment) {
		scan(assignment.getAssigned());
		scan(assignment.getAssignment());
	}

	@Override
	public void visitCtPackage(CtPackage ctPackage) {}

	@Override
	public void visitCtPackageReference(CtPackageReference reference) {}

	@Override
	public <T> void visitCtParameter(CtParameter<T> parameter) {}

	@Override
	public <T> void visitCtParameterReference(CtParameterReference<T> reference) {}

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
	public void visitCtSynchronized(CtSynchronized synchro) {}

	@Override
	public void visitCtThrow(CtThrow throwStatement) {}

	@Override
	public void visitCtTry(CtTry tryBlock) {}

	@Override
	public void visitCtTypeParameter(CtTypeParameter typeParameter) {}

	@Override
	public void visitCtTypeParameterReference(CtTypeParameterReference ref) {}

	@Override
	public <T> void visitCtTypeReference(CtTypeReference<T> reference) {}

	@Override
	public <T> void visitCtUnaryOperator(CtUnaryOperator<T> operator) {
		scan(operator.getOperand());
		estimation.addEstimation(operator.getKind().name().toLowerCase(), multiplier);
		
	}

	@Override
	public <T> void visitCtVariableAccess(CtVariableAccess<T> variableAccess) {
		scan(variableAccess.getVariable());
	}

	@Override
	public void visitCtWhile(CtWhile whileLoop) {
		int oldMultiplier = multiplier;
		multiplier *= staticLoopSize;
		scan(whileLoop.getLoopingExpression());
		scan(whileLoop.getBody());
		multiplier = oldMultiplier;
	}

	public String getExpressionString() {
		return estimation.toString();
	}

}
