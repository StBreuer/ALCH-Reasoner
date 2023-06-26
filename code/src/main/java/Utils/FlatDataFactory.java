package Utils;

import Loading.DecomposeVisitor;
import Loading.Operator;
import org.semanticweb.owlapi.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class FlatDataFactory {
    private final OWLDataFactory dataFactory;
    private final DecomposeVisitor decomposeVisitor = new DecomposeVisitor();

    public FlatDataFactory(OWLDataFactory dataFactory) {
        this.dataFactory = dataFactory;
    }
    public OWLClassExpression getOWLClass(String className){
        return this.dataFactory.getOWLClass(className);
    }

    public OWLObjectIntersectionOf getOWLObjectIntersectionOf(OWLClassExpression... operands){
        List<OWLClassExpression> operandsList = new ArrayList<>();
        for(OWLClassExpression operand: operands){
            operand.accept(this.decomposeVisitor);
            if(this.decomposeVisitor.getOperator() == Operator.INTERSECTION){
                operandsList.addAll(this.decomposeVisitor.getOperands());
            } else {
                operandsList.add(operand);
            }

        }
        return this.dataFactory.getOWLObjectIntersectionOf(operandsList);
    }

    public OWLObjectIntersectionOf getOWLObjectIntersectionOf(List<OWLClassExpression> operands){
        List<OWLClassExpression> operandsList = new ArrayList<>();
        for(OWLClassExpression operand: operands){
            operand.accept(this.decomposeVisitor);
            if(this.decomposeVisitor.getOperator() == Operator.INTERSECTION){
                operandsList.addAll(this.decomposeVisitor.getOperands());
            } else {
                operandsList.add(operand);
            }

        }
        return this.dataFactory.getOWLObjectIntersectionOf(operandsList);
    }

    public OWLObjectIntersectionOf getOWLObjectIntersectionOf(Set<OWLClassExpression> operands){
        List<OWLClassExpression> operandsList = new ArrayList<>();
        for(OWLClassExpression operand: operands){
            operand.accept(this.decomposeVisitor);
            if(this.decomposeVisitor.getOperator() == Operator.INTERSECTION){
                operandsList.addAll(this.decomposeVisitor.getOperands());
            } else {
                operandsList.add(operand);
            }

        }
        return this.dataFactory.getOWLObjectIntersectionOf(operandsList);
    }


    public OWLObjectUnionOf getOWLObjectUnionOf(OWLClassExpression... operands) {
        List<OWLClassExpression> operandsList = new ArrayList<>();
        for (OWLClassExpression operand : operands) {
            operand.accept(this.decomposeVisitor);
            if (this.decomposeVisitor.getOperator() == Operator.UNION) {
                operandsList.addAll(this.decomposeVisitor.getOperands());
            } else {
                operandsList.add(operand);
            }

        }
        return this.dataFactory.getOWLObjectUnionOf(operandsList);
    }
    public OWLObjectUnionOf getOWLObjectUnionOf(List<OWLClassExpression> operands) {
        List<OWLClassExpression> operandsList = new ArrayList<>();
        for (OWLClassExpression operand : operands) {
            operand.accept(this.decomposeVisitor);
            if (this.decomposeVisitor.getOperator() == Operator.UNION) {
                operandsList.addAll(this.decomposeVisitor.getOperands());
            } else {
                operandsList.add(operand);
            }

        }
        return this.dataFactory.getOWLObjectUnionOf(operandsList);
    }

    public OWLObjectUnionOf getOWLObjectUnionOf(Set<OWLClassExpression> operands) {
        List<OWLClassExpression> operandsList = new ArrayList<>();
        for (OWLClassExpression operand : operands) {
            operand.accept(this.decomposeVisitor);
            if (this.decomposeVisitor.getOperator() == Operator.UNION) {
                operandsList.addAll(this.decomposeVisitor.getOperands());
            } else {
                operandsList.add(operand);
            }

        }
        return this.dataFactory.getOWLObjectUnionOf(operandsList);
    }

    //TODO SHOULD THIS WORK THIS WAY?
    public OWLObjectComplementOf getOWLObjectComplementOf(OWLClassExpression operand){
        operand.accept(this.decomposeVisitor);
        if(this.decomposeVisitor.getOperator() == Operator.COMPLEMENT){
            return (OWLObjectComplementOf) this.decomposeVisitor.getOperand();
        } else {
            return this.dataFactory.getOWLObjectComplementOf(operand);
        }
    }

    public OWLObjectSomeValuesFrom getOWLObjectSomeValuesFrom(OWLObjectPropertyExpression property, OWLClassExpression operand){
        return this.dataFactory.getOWLObjectSomeValuesFrom(property, operand);
    }

    public OWLObjectAllValuesFrom getOWLObjectAllValuesFrom(OWLObjectPropertyExpression property, OWLClassExpression operand){
        return this.dataFactory.getOWLObjectAllValuesFrom(property, operand);
    }

    public OWLSubClassOfAxiom getOWLSubClassOfAxiom(OWLClassExpression subClass, OWLClassExpression superClass){
        return this.dataFactory.getOWLSubClassOfAxiom(subClass, superClass);
    }
    public OWLClass getOWLNothing(){
        return this.dataFactory.getOWLNothing();
    }
    public OWLClass getOWLThing(){
        return this.dataFactory.getOWLThing();
    }
}
