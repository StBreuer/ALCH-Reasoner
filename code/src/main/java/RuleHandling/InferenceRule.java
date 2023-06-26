package RuleHandling;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

import java.util.List;

public abstract class InferenceRule {
    public abstract boolean isApplicable(List<OWLSubClassOfAxiom> axioms);
    public abstract OWLAxiom applyRule(List<OWLSubClassOfAxiom> axioms);
}
