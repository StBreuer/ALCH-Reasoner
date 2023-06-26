package RuleHandling.matchVisitors;

import RuleHandling.Tupel;
import org.semanticweb.owlapi.model.*;

import java.util.*;

public class ExistsBottomMatcherLeft extends BooleanMatchVisitor{
    private List<OWLClassExpression> restOfUnion;
    private OWLClassExpression exLeadsTo;


    //I think I actually do not need to check for form since the normalform is doing that for me

    public Map<OWLSubClassOfAxiom,List<OWLClassExpression>> MatchToExpression(List<OWLSubClassOfAxiom> activeClasses, OWLClassExpression exLeadsTo){
        this.exLeadsTo = exLeadsTo;
        Map<OWLSubClassOfAxiom,List<OWLClassExpression>> axiomToRest = new HashMap<>();
        for (OWLSubClassOfAxiom axiom : activeClasses){
            restOfUnion = new ArrayList<>();

            if(axiom.getSuperClass().accept(this)){
                axiomToRest.put(axiom, restOfUnion);
            }
        }
        return axiomToRest;
    }

    @Override
    public Boolean visit(OWLObjectUnionOf ce) {
        Boolean rtn = false;
        for(OWLClassExpression operand:ce.getOperandsAsList()){
            if (operand.accept(this)){
                rtn = true;
            }
        }
        return rtn;
    }

    @Override
    public Boolean visit(OWLObjectSomeValuesFrom ce) {
        if (ce.getFiller().equals(exLeadsTo)){
            return true;
        }
        this.restOfUnion.add(ce);
        return false;

    }
    @Override
    public Boolean visit(OWLClass ce) {
        this.restOfUnion.add(ce);
        return false;
    }

}
