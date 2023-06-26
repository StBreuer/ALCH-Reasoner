package RuleHandling.matchVisitors;

import org.semanticweb.owlapi.model.*;

import java.util.*;

public class RoleInclusionVisitor extends MatchVisitor {

    Map<OWLObjectPropertyExpression, List<OWLSubObjectPropertyOfAxiom>> superPropertyToAxioms;
    public Map<OWLObjectPropertyExpression, List<OWLSubObjectPropertyOfAxiom>> getRoleInclusions(OWLOntology ontology){
        this.superPropertyToAxioms = new HashMap<>();

        Set<OWLAxiom> axioms = ontology.getAxioms();
        for (OWLAxiom axiom : axioms){
            axiom.accept(this);
        }
        return this.superPropertyToAxioms;
    }

    @Override
    public void visit(OWLSubObjectPropertyOfAxiom axiom) {
        if (this.superPropertyToAxioms.containsKey(axiom.getSuperProperty())){
            this.superPropertyToAxioms.get(axiom.getSuperProperty()).add(axiom);
        } else {
            List<OWLSubObjectPropertyOfAxiom> newList = new ArrayList<>();
            newList.add(axiom);
            this.superPropertyToAxioms.put(axiom.getSuperProperty(), newList);
        }
    }


}
