package RuleHandling.matchVisitors;

import org.semanticweb.owlapi.model.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NegativeAtomVisitor extends MatchVisitor {

    private final Set<OWLClassExpression> negativeAtoms = new HashSet<>();

    public Set<OWLClassExpression> getNegativeAtoms() {
        return negativeAtoms;
    }

    @Override
    public void visit(OWLObjectIntersectionOf ce) {
        ce.getOperandsAsList().forEach(operand ->{
            operand.accept(this);
        });
    }

    //Assumes this is in normal form and therefore there are only atoms negated
    @Override
    public void visit(OWLObjectComplementOf ce) {
        this.negativeAtoms.add(ce.getOperand());
    }


}
