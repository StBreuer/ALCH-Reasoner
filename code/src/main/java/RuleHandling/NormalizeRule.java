package RuleHandling;

import Loading.DecomposeVisitor;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

import java.util.ArrayList;
import java.util.List;

public abstract class NormalizeRule {

    protected OWLDataFactory dataFactory;
    protected DecomposeVisitor decomposer;

    public NormalizeRule(OWLDataFactory dataFactory){
        this.dataFactory = dataFactory;
        decomposer = new DecomposeVisitor();
    }
    public abstract boolean isApplicable(OWLSubClassOfAxiom axiom);

    public  List<OWLSubClassOfAxiom> applyRule(OWLSubClassOfAxiom axiom){
        List<OWLSubClassOfAxiom> newAxioms = new ArrayList<>();
        OWLClassExpression superClass = axiom.getSuperClass();
        superClass.accept(decomposer);
        decomposer.getOperands().forEach(operand -> {
            OWLSubClassOfAxiom newAxiom = dataFactory.getOWLSubClassOfAxiom(axiom.getSubClass(), operand);
            newAxioms.add(newAxiom);
        });
        return newAxioms;
    }

}
