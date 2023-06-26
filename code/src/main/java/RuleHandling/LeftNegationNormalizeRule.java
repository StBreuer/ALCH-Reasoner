package RuleHandling;

import Loading.Operator;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

import java.util.ArrayList;
import java.util.List;

public class LeftNegationNormalizeRule extends NormalizeRule{

    public LeftNegationNormalizeRule(OWLDataFactory dataFactory) {
        super(dataFactory);
    }

    @Override
    public boolean isApplicable(OWLSubClassOfAxiom axiom) {
        OWLClassExpression subClass = axiom.getSubClass();
        subClass.accept(decomposer);
        return decomposer.getOperator() == Operator.COMPLEMENT;
    }

    @Override
    public List<OWLSubClassOfAxiom> applyRule(OWLSubClassOfAxiom axiom){
        List<OWLSubClassOfAxiom> newAxioms = new ArrayList<>();
        OWLClassExpression subClass = axiom.getSubClass();
        subClass.accept(decomposer);
        OWLClassExpression boxC = decomposer.getOperand();

        OWLClassExpression boxNotC = axiom.getSuperClass();
        OWLObjectUnionOf boxNotCAndboxC = dataFactory.getOWLObjectUnionOf(boxNotC,boxC);

        OWLClassExpression top = dataFactory.getOWLThing();
        OWLSubClassOfAxiom newAxiom = dataFactory.getOWLSubClassOfAxiom(top, boxNotCAndboxC);
        newAxioms.add(newAxiom);
        return newAxioms;
    }
}
