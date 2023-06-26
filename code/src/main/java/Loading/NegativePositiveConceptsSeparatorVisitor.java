package Loading;

import org.semanticweb.owlapi.model.*;
import uk.ac.manchester.cs.owl.owlapi.OWLSubClassOfAxiomImpl;

import java.util.ArrayList;
import java.util.List;

public class NegativePositiveConceptsSeparatorVisitor implements OWLObjectVisitor {

    private List<OWLClassExpression> negativeExpressions = new ArrayList<>();
    private List<OWLClassExpression> positiveExpressions = new ArrayList<>();
    private List<OWLAxiom> propertyAxioms = new ArrayList<>();
    private boolean negative;

    public NegativePositiveConceptsSeparatorVisitor(){
        negative = false;
    }

    private void reverseNegative(){
        if(this.negative){
            this.negative = false;
        } else {
            this.negative = true;
        }
    }

    @Override
    public void visit(OWLClass ce) {
        if (this.negative){
            this.negativeExpressions.add(ce);
        }else {
            this.positiveExpressions.add(ce);
        }
    }

    @Override
    public void visit(OWLObjectIntersectionOf ce) {
        if(this.negative){
            this.negativeExpressions.add(ce);
        }else {
            this.positiveExpressions.add(ce);
        }

        boolean safeBool = this.negative;
        ce.getOperandsAsList().forEach(
                operands->{
                    operands.accept(this);
                    this.negative = safeBool;
                });

    }

    @Override
    public void visit(OWLObjectUnionOf ce) {
        if(this.negative){
            this.negativeExpressions.add(ce);
        }else {
            this.positiveExpressions.add(ce);
        }
        boolean safeBool = this.negative;
        ce.getOperandsAsList().forEach(
                operands->{
                    operands.accept(this);
                    this.negative = safeBool;
                });
    }

    @Override
    public void visit(OWLObjectComplementOf ce) {
        if(this.negative){
            this.negativeExpressions.add(ce);
        }else {
            this.positiveExpressions.add(ce);
        }
        reverseNegative();
        ce.getOperand().accept(this);
    }

    @Override
    public void visit(OWLObjectSomeValuesFrom ce) {
        if(this.negative){
            this.negativeExpressions.add(ce);
        }else {
            this.positiveExpressions.add(ce);
        }
        ce.getFiller().accept(this);
    }

    @Override
    public void visit(OWLObjectAllValuesFrom ce) {
        if(this.negative){
            this.negativeExpressions.add(ce);
        }else {
            this.positiveExpressions.add(ce);
        }
        ce.getFiller().accept(this);
    }

    @Override
    public void visit(OWLSubClassOfAxiom axiom) {
        this.negative = false;
        axiom.getSuperClass().accept(this);
        this.negative = true;
        axiom.getSubClass().accept(this);
    }

    @Override
    public void visit(OWLEquivalentClassesAxiom axiom) {
        //This needs to Split into SubclassOf Axioms

        List<OWLClassExpression> operands = axiom.getOperandsAsList();
        this.negative = false;
        operands.get(0).accept(this);
        this.negative = true;
        operands.get(1).accept(this);
        this.negative = false;
        operands.get(1).accept(this);
        this.negative = true;
        operands.get(0).accept(this);
    }

    @Override
    public void visit(OWLEquivalentObjectPropertiesAxiom axiom) {
        this.propertyAxioms.addAll(axiom.asSubObjectPropertyOfAxioms());
    }

    @Override
    public void visit(OWLSubObjectPropertyOfAxiom axiom) {
        this.propertyAxioms.add(axiom);

    }

    public List<OWLClassExpression> getNegativeExpressions() {
        return negativeExpressions;
    }

    public List<OWLClassExpression> getPositiveExpressions() {
        return positiveExpressions;
    }
}
