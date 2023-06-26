package RuleHandling.matchVisitors;

import RuleHandling.Tupel;
import org.semanticweb.owlapi.model.*;

import java.util.*;
import java.util.stream.Collectors;

public class ExistsMinusMatcherLeft extends MatchVisitor{
    private OWLObjectPropertyExpression property;
    private Map<Tupel<OWLClassExpression, Set<OWLClassExpression>>, List<OWLSubClassOfAxiom>> fillerToAxioms;
    private Set<OWLClassExpression> restOfUnion;
    private List<OWLObjectSomeValuesFrom> existentials;


    public Map<Tupel<OWLClassExpression, Set<OWLClassExpression>>, List<OWLSubClassOfAxiom>> MatchToExpression(List<OWLSubClassOfAxiom> activeClasses, OWLObjectPropertyExpression property){
        this.fillerToAxioms = new HashMap<>();
        this.property = property;
        for (OWLSubClassOfAxiom axiom : activeClasses){
            restOfUnion = new HashSet<>();
            existentials = new ArrayList<>();
            axiom.getSuperClass().accept(this);
            for (OWLObjectSomeValuesFrom existential : existentials){
                Set<OWLClassExpression> newRest = new HashSet<>(this.restOfUnion);
                newRest.remove(existential);


                Tupel<OWLClassExpression, Set<OWLClassExpression>> newTupel = new Tupel<>(existential.getFiller(), newRest);

                if (this.fillerToAxioms.containsKey(newTupel)){
                this.fillerToAxioms.get(newTupel).add(axiom);

                }else {
                    List<OWLSubClassOfAxiom> newAxiomList = new ArrayList<>();
                    newAxiomList.add(axiom);
                this.fillerToAxioms.put(newTupel, newAxiomList);
                }

            }
        }
        return this.fillerToAxioms;
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
            this.existentials.add(ce);
        }
        this.restOfUnion.add(ce); //WHY is this here? -> because there could be more than one applicable exists with different Class K
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
