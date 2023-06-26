package RuleHandling.matchVisitors;

import Utils.FlatDataFactory;
import org.semanticweb.owlapi.model.*;

import java.util.ArrayList;
import java.util.List;

public class ConjunctionNameVisitor implements OWLClassExpressionVisitor {
    private List<OWLClass> owlClasses;
    private final FlatDataFactory dataFactory;
    private boolean fitsPattern;

    public ConjunctionNameVisitor(FlatDataFactory dataFactory){
        this.dataFactory = dataFactory;
    }

    public List<OWLSubClassOfAxiom> getNewAxioms(OWLClassExpression owlClassExpression){
        this.fitsPattern = true;
        this.owlClasses = new ArrayList<>();

        List<OWLSubClassOfAxiom> newAxioms = new ArrayList<>();
        OWLClassExpression currentClassExpression = owlClassExpression;

        owlClassExpression.accept(this);

        if (fitsPattern){
            this.owlClasses.forEach(owlClass ->{
                OWLSubClassOfAxiom newAxiom = dataFactory.getOWLSubClassOfAxiom(currentClassExpression,owlClass);
                newAxioms.add(newAxiom);
            });
        }

        return newAxioms;
    }

    @Override
    public void visit(OWLClass ce) {
        this.owlClasses.add(ce);
    }

    @Override
    public void visit(OWLObjectIntersectionOf ce) {
        ce.getOperandsAsList().forEach(operand -> {
            operand.accept(this);
        });
    }

    @Override
    public void visit(OWLObjectComplementOf ce) {
        //don't need to check if ce is a complex class since we assume normal form.
    }

    @Override
    public void visit(OWLObjectUnionOf ce) {
        this.fitsPattern = false;
        return;
    }

    @Override
    public void visit(OWLObjectSomeValuesFrom ce) {
        this.fitsPattern = false;
    }

    @Override
    public void visit(OWLObjectAllValuesFrom ce) {
        this.fitsPattern = false;
    }
}
