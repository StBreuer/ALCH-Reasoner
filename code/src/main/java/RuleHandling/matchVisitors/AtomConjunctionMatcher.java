package RuleHandling.matchVisitors;

import RuleHandling.Tupel;
import org.semanticweb.owlapi.model.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AtomConjunctionMatcher extends BooleanMatchVisitor{
    Set<OWLClass> activeClasses;
    //      A_i                     Axiom
    public List<Tupel<Set<OWLClass>, OWLSubClassOfAxiom>> getMatches(Set<OWLAxiom> axioms){
        List<Tupel<Set<OWLClass>, OWLSubClassOfAxiom>> matches = new ArrayList<>();
        for (OWLAxiom axiom : axioms){
            this.activeClasses = new HashSet<>();
            if(axiom.accept(this)){
                matches.add(new Tupel<>(new HashSet<>(this.activeClasses), (OWLSubClassOfAxiom) axiom));
            }
        }
        return matches;
    }


    @Override
    public Boolean visit(OWLObjectIntersectionOf ce) {
        for(OWLClassExpression operand : ce.getOperandsAsList()){
            if (!operand.accept(this)){
                return false;
            }
        }
        return true;
    }

    @Override
    public Boolean visit(OWLClass ce) {
        this.activeClasses.add(ce);
        return true;
    }

    @Override
    public Boolean visit(OWLSubClassOfAxiom axiom) {
        return axiom.getSubClass().accept(this);
    }
}
