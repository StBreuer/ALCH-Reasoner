package RuleHandling.matchVisitors;

import org.semanticweb.owlapi.model.*;

public abstract class BooleanMatchVisitor implements OWLObjectVisitorEx<Boolean> {
    @Override
    public Boolean visit(OWLObjectIntersectionOf ce) {
        return false;
    }
    @Override
    public Boolean visit(OWLObjectUnionOf ce) {
        return false;
    }

    @Override
    public Boolean visit(OWLObjectComplementOf ce) {
        return false;
    }

    @Override
    public Boolean visit(OWLObjectSomeValuesFrom ce) {
        return false;
    }

    @Override
    public Boolean visit(OWLObjectAllValuesFrom ce) {
        return false;
    }

    @Override
    public Boolean visit(OWLClass ce) {
        return false;
    }

    @Override
    public Boolean visit(OWLSubClassOfAxiom axiom) {
        return axiom.getSuperClass().accept(this);
    }

    @Override
    public Boolean visit(OWLEquivalentObjectPropertiesAxiom axiom) {
        System.out.println("Equivalence Axiom in Ontology (Right Disjunction visitor)");
        return false;
    }

    @Override
    public Boolean visit(OWLSubObjectPropertyOfAxiom axiom) {
        return false;
    }

    @Override
    public Boolean visit(OWLEquivalentClassesAxiom axiom) {
        return false;
    }
}
