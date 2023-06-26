package RuleHandling.matchVisitors;

import RuleHandling.Tupel;
import org.semanticweb.owlapi.model.*;

import java.util.*;

public class ForAllMatcherRight extends BooleanMatchVisitor{

    private OWLClassExpression className;
    private OWLSubClassOfAxiom currentAxiom;
    private List<OWLClassExpression> restOfUnion;

    public List<Tupel<List<OWLClassExpression>,OWLSubClassOfAxiom>> matchToClass(List<OWLSubClassOfAxiom> activeAxioms, OWLClassExpression className, OWLClassExpression leftMatch){
        List<Tupel<List<OWLClassExpression>, OWLSubClassOfAxiom>> matches = new ArrayList<>();
        this.className = className;
        activeAxioms.forEach(axiom -> {
            if(axiom.getSubClass().equals(leftMatch)){
                restOfUnion = new ArrayList<>();
                if (axiom.getSuperClass().accept(this)){
                    Tupel<List<OWLClassExpression>, OWLSubClassOfAxiom> newTuple = new Tupel<>(new ArrayList<>(this.restOfUnion), axiom);
                    matches.add(newTuple);
                }
            }
        });
        return matches;
    }
    @Override
    public Boolean visit(OWLObjectUnionOf ce) {
        Boolean matches = false;
        for (OWLClassExpression operand: ce.getOperandsAsList()){
            if(operand.accept(this)){
                matches = true;
            }
        }
        return matches;
    }

    @Override
    public Boolean visit(OWLObjectSomeValuesFrom ce) {
        this.restOfUnion.add(ce);
        return false;
    }

    @Override
    public Boolean visit(OWLObjectComplementOf ce) {
        this.restOfUnion.add(ce);
        return false;
    }


    @Override
    public Boolean visit(OWLObjectAllValuesFrom ce) {
        this.restOfUnion.add(ce);
        return false;
    }

    @Override
    public Boolean visit(OWLClass ce) {
        if (ce.equals(className)){
            return true;
        }
        this.restOfUnion.add(ce);
        return false;
    }
}
