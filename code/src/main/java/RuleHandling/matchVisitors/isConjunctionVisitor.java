package RuleHandling.matchVisitors;

import org.semanticweb.owlapi.model.*;

import java.util.List;

public class isConjunctionVisitor implements OWLClassExpressionVisitorEx<Boolean> {
    @Override
    public Boolean visit(OWLObjectIntersectionOf ce) {
        Boolean allTrue = true;
        List<OWLClassExpression> operands = ce.getOperandsAsList();
        for(OWLClassExpression operand : operands){
            if(!operand.accept(this)){
                allTrue = false;
            }
        }
        return allTrue;
    }

    @Override
    public Boolean visit(OWLObjectUnionOf ce) {
        return false;
    }

    @Override
    public Boolean visit(OWLObjectComplementOf ce) {
        return false;
    }

    @Override
    public Boolean visit(OWLObjectSomeValuesFrom ce) {
        return false;
    }

    @Override
    public Boolean visit(OWLObjectAllValuesFrom ce) {
        return false;
    }

    @Override
    public Boolean visit(OWLClass ce) {
        return true;
    }
}
