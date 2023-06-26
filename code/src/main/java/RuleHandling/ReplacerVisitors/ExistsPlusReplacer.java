package RuleHandling.ReplacerVisitors;

import Utils.FlatDataFactory;
import org.semanticweb.owlapi.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExistsPlusReplacer implements OWLClassExpressionVisitorEx<OWLClassExpression> {


    private OWLClassExpression toReplace;
    private OWLClassExpression replacement;
    private FlatDataFactory dataFactory;

    public ExistsPlusReplacer(FlatDataFactory dataFactory){
        this.dataFactory = dataFactory;
    }
    public OWLClassExpression replace(OWLClassExpression concept, OWLClassExpression toReplace, OWLClassExpression replacement){
        this.toReplace = toReplace;
        this.replacement = replacement;
        return concept.accept(this);
    }
    @Override
    public OWLClassExpression visit(OWLClass ce) {
        if (ce.equals(toReplace)){
            return replacement;
        }
        return ce;
    }

    @Override
    public OWLClassExpression visit(OWLObjectIntersectionOf ce) {
        System.out.println("intersection should not appear (ExistsPlusReplacer)");
        return ce;
    }

    @Override
    public OWLClassExpression visit(OWLObjectUnionOf ce) {
        List<OWLClassExpression> newOperands = new ArrayList<>();
        for (OWLClassExpression operand: ce.getOperandsAsList()){
            newOperands.add(operand.accept(this));
        }
        return dataFactory.getOWLObjectUnionOf(newOperands);
    }

    @Override
    public OWLClassExpression visit(OWLObjectComplementOf ce) {
        System.out.println("complement should not appear (DisjunctionDecomposer)");
        return ce;
    }

    @Override
    public OWLClassExpression visit(OWLObjectSomeValuesFrom ce) {
        if (ce.equals(toReplace)){
            return replacement;
        }
        return ce;
    }

    @Override
    public OWLClassExpression visit(OWLObjectAllValuesFrom ce) {
        if (ce.equals(toReplace)){
            return replacement;
        }
        return ce;
    }
}
