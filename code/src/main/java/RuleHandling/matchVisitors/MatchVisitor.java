package RuleHandling.matchVisitors;

import org.semanticweb.owlapi.model.*;

public class MatchVisitor implements OWLObjectVisitor {
    @Override
    public void visit(OWLObjectIntersectionOf ce) {
        return;
    }

    @Override
    public void visit(OWLObjectUnionOf ce) {
        return;
    }

    @Override
    public void visit(OWLObjectComplementOf ce) {
        return;
    }

    @Override
    public void visit(OWLObjectSomeValuesFrom ce) {
        return;
    }

    @Override
    public void visit(OWLObjectAllValuesFrom ce) {
        return;
    }

    @Override
    public void visit(OWLClass ce) {
        return;
    }

    @Override
    public void visit(OWLSubClassOfAxiom axiom) {
        return;
    }

    @Override
    public void visit(OWLEquivalentClassesAxiom axiom) {
        System.out.println("EquivalentClassesAxiom in ontology (MatchVisitor)");
    }

    @Override
    public void visit(OWLSubObjectPropertyOfAxiom axiom) {
        return;
    }

    @Override
    public void visit(OWLEquivalentObjectPropertiesAxiom axiom) {
        System.out.println("EquivalentObjectPropertiesAxiom in ontology (MatchVisitor)");
    }
}
