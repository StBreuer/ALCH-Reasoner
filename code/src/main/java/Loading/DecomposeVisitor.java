package Loading;

import org.semanticweb.owlapi.model.*;

import java.util.ArrayList;
import java.util.List;

/**
 * This class decomposes Axioms and class expressions into their parts and makes them accessible
 */
public class DecomposeVisitor implements OWLObjectVisitor {

    private boolean axiomUseful;
    private Operator operator;
    private List<OWLClassExpression> operands;
    private OWLClassExpression operand;
    private OWLObjectPropertyExpression property;

    public DecomposeVisitor() {}




    @Override
    public void visit(OWLObjectIntersectionOf ce) {
        setOperator(Operator.INTERSECTION);
        setOperands(ce.getOperandsAsList());
    }



    @Override
    public void visit(OWLObjectUnionOf ce) {
        setOperator(Operator.UNION);
        setOperands(ce.getOperandsAsList());
    }

    @Override
    public void visit(OWLObjectComplementOf ce) {
        setOperator(Operator.COMPLEMENT);
        setOperand(ce.getOperand());
    }

    @Override
    public void visit(OWLObjectSomeValuesFrom ce) {
        setOperator(Operator.SOME_VALUE_FROM);
        setProperty(ce.getProperty());
        setOperand(ce.getFiller());
    }

    @Override
    public void visit(OWLObjectAllValuesFrom ce) {
        setOperator(Operator.ALL_VALUES_FROM);
        setProperty(ce.getProperty());
        setOperand(ce.getFiller());
    }

    @Override
    public void visit(OWLClass ce) {
        setOperator(Operator.CLASS);
        setOperand(ce);
    }

    @Override
    public void visit(OWLSubClassOfAxiom axiom) {
        setOperator(Operator.SUBCLASSAXIOM);
        List<OWLClassExpression> operands = new ArrayList<>();
        operands.add(axiom.getSubClass());
        operands.add(axiom.getSuperClass());
        setOperands(operands);
        setAxiomUseful(true);

    }

    @Override
    public void visit(OWLEquivalentClassesAxiom axiom) {
        setOperator(Operator.EQUIVALENTAXIOM);
        setOperands(axiom.getOperandsAsList());
        setAxiomUseful(true);
    }

    @Override
    public void visit(OWLEquivalentObjectPropertiesAxiom axiom) {
        setAxiomUseful(false);
    }

    @Override
    public void visit(OWLSubObjectPropertyOfAxiom axiom) {
        setAxiomUseful(false);
    }

    public Operator getOperator() {
        return operator;
    }

    private void setOperator(Operator operator) {
        this.operator = operator;
    }

    public List<OWLClassExpression> getOperands() {
        return operands;
    }

    private void setOperands(List<OWLClassExpression> operands) {
        this.operands = operands;
    }

    public OWLClassExpression getOperand() {
        return operand;
    }

    private void setOperand(OWLClassExpression operand) {
        this.operand = operand;
    }

    public OWLObjectPropertyExpression getProperty() {
        return property;
    }

    private void setProperty(OWLObjectPropertyExpression property) {
        this.property = property;
    }

    public boolean isAxiomUseful() {
        return axiomUseful;
    }

    private void setAxiomUseful(boolean axiomUseful) {
        this.axiomUseful = axiomUseful;
    }
}
