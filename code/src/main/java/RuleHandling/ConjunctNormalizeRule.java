package RuleHandling;

import Loading.DecomposeVisitor;
import Loading.Operator;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

import java.util.ArrayList;
import java.util.List;

public class ConjunctNormalizeRule extends NormalizeRule{

    ConjunctNormalizeRule(OWLDataFactory dataFactory){
        super(dataFactory);
    }

    @Override
    public boolean isApplicable(OWLSubClassOfAxiom axiom) {
        OWLClassExpression superClass = axiom.getSuperClass();
        superClass.accept(decomposer);
        return decomposer.getOperator() == Operator.INTERSECTION;
    }

}
