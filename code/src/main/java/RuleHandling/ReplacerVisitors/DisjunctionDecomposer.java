package RuleHandling.ReplacerVisitors;


import org.semanticweb.owlapi.model.*;

import java.util.ArrayList;
import java.util.List;

public class DisjunctionDecomposer implements OWLClassExpressionVisitor {

    List<OWLClassExpression> classExpressions = new ArrayList<>();

    @Override
    public void visit(OWLClass ce) {
        this.classExpressions.add(ce);
    }
    @Override
    public void visit(OWLObjectIntersectionOf ce) {
        System.out.println("intersection should not appear (DisjunctionDecomposer)");
    }

    @Override
    public void visit(OWLObjectUnionOf ce) {
        ce.getOperandsAsList().forEach(operand ->{
            operand.accept(this);
        });
    }

    @Override
    public void visit(OWLObjectComplementOf ce) {
        System.out.println("complement should not appear (DisjunctionDecomposer)");

    }

    @Override
    public void visit(OWLObjectSomeValuesFrom ce) {
        this.classExpressions.add(ce);
    }

    @Override
    public void visit(OWLObjectAllValuesFrom ce) {
       this.classExpressions.add(ce);
    }

    public List<OWLClassExpression> getClassExpressions() {
        return classExpressions;
    }


}
