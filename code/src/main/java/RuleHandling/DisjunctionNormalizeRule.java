package RuleHandling;

import Loading.Operator;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

import java.util.ArrayList;
import java.util.List;

public class DisjunctionNormalizeRule extends NormalizeRule{

    public DisjunctionNormalizeRule(OWLDataFactory dataFactory){
        super(dataFactory);

    }

    @Override
    public boolean isApplicable(OWLSubClassOfAxiom axiom) {
        OWLClassExpression subClass = axiom.getSubClass();
        subClass.accept(decomposer);
        return decomposer.getOperator() == Operator.UNION;
    }

    @Override
    public  List<OWLSubClassOfAxiom> applyRule(OWLSubClassOfAxiom axiom){
        List<OWLSubClassOfAxiom> newAxioms = new ArrayList<>();
        OWLClassExpression subClass = axiom.getSubClass();
        subClass.accept(decomposer);
        decomposer.getOperands().forEach(operand -> {
            OWLSubClassOfAxiom newAxiom = dataFactory.getOWLSubClassOfAxiom(operand, axiom.getSuperClass());
            newAxioms.add(newAxiom);
        });
        return newAxioms;
    }

}
