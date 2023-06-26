package RuleHandling.matchVisitors;

import RuleHandling.Triple;
import RuleHandling.Tupel;
import org.semanticweb.owlapi.model.*;

import java.util.*;

public class ForAllMatcherLeft extends MatchVisitor{
    private OWLObjectPropertyExpression property;
    private Map<Tupel<OWLClassExpression, Set<OWLClassExpression>>, List<OWLSubClassOfAxiom>> fillerToAxioms;
    private Set<OWLClassExpression> restOfUnion;
    private List<OWLObjectSomeValuesFrom> existentials;


    public Map<OWLClassExpression, List<Triple<OWLClassExpression, Set<OWLClassExpression>, OWLSubClassOfAxiom>>> MatchToExpression(List<OWLSubClassOfAxiom> activeClasses, OWLObjectPropertyExpression property){
        Map<OWLClassExpression, List<Triple<OWLClassExpression, Set<OWLClassExpression>, OWLSubClassOfAxiom>>> subclassToBuildEssentials = new HashMap<>();
        this.property = property;
        for (OWLSubClassOfAxiom axiom : activeClasses){
            restOfUnion = new HashSet<>();
            existentials = new ArrayList<>();
            axiom.getSuperClass().accept(this);
            for (OWLObjectSomeValuesFrom existential : existentials){
                Set<OWLClassExpression> newRest = new HashSet<>(this.restOfUnion);
                newRest.remove(existential);


                Triple<OWLClassExpression, Set<OWLClassExpression>, OWLSubClassOfAxiom> newTriple = new Triple<>(existential.getFiller(), newRest, axiom);

                if (subclassToBuildEssentials.containsKey(axiom.getSubClass())){
                    subclassToBuildEssentials.get(axiom.getSubClass()).add(newTriple);

                }else {
                    List<Triple<OWLClassExpression, Set<OWLClassExpression>, OWLSubClassOfAxiom>> newAxiomList = new ArrayList<>();
                    newAxiomList.add(newTriple);
                    subclassToBuildEssentials.put(axiom.getSubClass(), newAxiomList);
                }

            }
        }
        return subclassToBuildEssentials;
    }

    @Override
    public void visit(OWLObjectUnionOf ce) {
        ce.getOperandsAsList().forEach(operand ->{
            operand.accept(this);
        });
    }

    @Override
    public void visit(OWLObjectSomeValuesFrom ce) {
        if (ce.getProperty().equals(this.property)){
            existentials.add(ce);
        }
        this.restOfUnion.add(ce);
    }
    @Override
    public void visit(OWLObjectComplementOf ce) {
        this.restOfUnion.add(ce);
    }


    @Override
    public void visit(OWLObjectAllValuesFrom ce) {
        this.restOfUnion.add(ce);
    }

    @Override
    public void visit(OWLClass ce) {
        this.restOfUnion.add(ce);
    }

}
