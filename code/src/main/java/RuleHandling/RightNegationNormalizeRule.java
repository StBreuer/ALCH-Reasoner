package RuleHandling;

import Loading.Operator;
import org.semanticweb.owlapi.model.*;

import java.util.ArrayList;
import java.util.List;

public class RightNegationNormalizeRule extends NormalizeRule{

    public RightNegationNormalizeRule(OWLDataFactory dataFactory) {
        super(dataFactory);
    }

    @Override
    public boolean isApplicable(OWLSubClassOfAxiom axiom) {
        OWLClassExpression superClass = axiom.getSuperClass();
        superClass.accept(decomposer);
        return decomposer.getOperator() == Operator.COMPLEMENT;
    }

    @Override
    public List<OWLSubClassOfAxiom> applyRule(OWLSubClassOfAxiom axiom){
        List<OWLSubClassOfAxiom> newAxioms = new ArrayList<>();
        OWLClassExpression superClass = axiom.getSuperClass();
        superClass.accept(decomposer);
        OWLClassExpression boxC = decomposer.getOperand();

        OWLClassExpression boxNotC = axiom.getSubClass();
        OWLObjectIntersectionOf boxNotCAndboxC = dataFactory.getOWLObjectIntersectionOf(boxNotC,boxC);

        OWLClassExpression bottom = dataFactory.getOWLNothing();
        OWLSubClassOfAxiom newAxiom = dataFactory.getOWLSubClassOfAxiom(boxNotCAndboxC, bottom);
        newAxioms.add(newAxiom);
        return newAxioms;

        /*
        List<OWLSubClassOfAxiom> newAxioms = new ArrayList<>();
        OWLClassExpression superClass = axiom.getSuperClass();
        superClass.accept(decomposer);
        OWLClassExpression operand = decomposer.getOperand();
        OWLSubClassOfAxiom newAxiom = dataFactory.getOWLSubClassOfAxiom(axiom.getSubClass(), operand);
        newAxioms.add(newAxiom);
        return newAxioms;

         */
    }
}
