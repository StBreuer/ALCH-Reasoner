package RuleHandling.matchVisitors;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class FilterAtomsVisitor extends BooleanMatchVisitor{
    private final List<OWLClassExpression> restOfUnion = new ArrayList<>();
    private final Set<OWLClassExpression> searchedForAtoms;

    public FilterAtomsVisitor(Set<OWLClassExpression> searchedForAtoms){
        this.searchedForAtoms = searchedForAtoms;
    }

    @Override
    public Boolean visit(OWLObjectUnionOf ce) {
        Boolean containedNegative = false;
        for(OWLClassExpression operand : ce.getOperandsAsList()){
            if (operand.accept(this)){
                containedNegative = true;
            }
        }
        return containedNegative;
    }

    @Override
    public Boolean visit(OWLClass ce) {
        if (searchedForAtoms.contains(ce)){
            return true;
        }
        this.restOfUnion.add(ce);
        return false;
    }


    public List<OWLClassExpression> getRestOfUnion() {
        return restOfUnion;
    }
}
