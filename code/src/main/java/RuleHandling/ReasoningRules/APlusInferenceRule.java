package RuleHandling.ReasoningRules;

import RuleHandling.Proofs.ProofHandler;
import RuleHandling.matchVisitors.ConjunctionNameVisitor;
import Utils.FlatDataFactory;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class APlusInferenceRule implements InferenceRule{
    private final FlatDataFactory dataFactory;
    private ProofHandler proofHandler;
    private final String ruleName = "Aplus";

    public APlusInferenceRule(OWLOntology ontology, ProofHandler proofHandler){
        this.dataFactory = new FlatDataFactory(ontology.getOWLOntologyManager().getOWLDataFactory());
        this.proofHandler = proofHandler;
    }
    public boolean applyRule(){
        Set<OWLClassExpression> activeClasses = new HashSet<>(this.proofHandler.getActiveClasses());

        ConjunctionNameVisitor visitor = new ConjunctionNameVisitor(this.dataFactory);
        List<OWLSubClassOfAxiom> subClassOfAxioms = new ArrayList<>();
        for(OWLClassExpression classExpression: activeClasses){
             subClassOfAxioms.addAll(visitor.getNewAxioms(classExpression));
             this.proofHandler.getActiveClasses().remove(classExpression);
        }

        return proofHandler.addInferences(new ArrayList<>(),new ArrayList<>(), subClassOfAxioms, ruleName);
    }

    public String getRuleName() {
        return ruleName;
    }
}
