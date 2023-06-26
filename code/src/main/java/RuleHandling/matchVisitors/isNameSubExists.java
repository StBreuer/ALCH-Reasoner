package RuleHandling.matchVisitors;

import org.semanticweb.owlapi.model.*;

import java.util.HashMap;
import java.util.Map;

public class isNameSubExists implements OWLObjectVisitorEx<Boolean> {
    private boolean sub;
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
        return OWLObjectVisitorEx.super.visit(ce);
    }

    @Override
    public Boolean visit(OWLObjectAllValuesFrom ce) {
        if(sub){
            return false;
        }
        return true;
    }

    @Override
    public Boolean visit(OWLClass ce) {
        if (sub){
            return true;
        }
        return false;
    }

    @Override
    public Boolean visit(OWLSubClassOfAxiom axiom) {
        this.sub = true;
        if(!axiom.getSubClass().accept(this)){
            return false;
        }
        this.sub =false;
        if(!axiom.getSuperClass().accept(this)){
            return false;
        }
        return true;

    }


    //Problem
    @Override
    public Boolean visit(OWLEquivalentClassesAxiom axiom) {
        axiom.asOWLSubClassOfAxioms().forEach(owlSubClassOfAxiom -> {
            owlSubClassOfAxiom.accept(this);
        });
        return null;
    }
}
