package RuleHandling.ReasoningRules;

import RuleHandling.Proofs.ProofHandler;
import RuleHandling.matchVisitors.ExistsBottomMatcherLeft;
import Utils.FlatDataFactory;
import org.semanticweb.owlapi.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExistsBottomInferenceRule implements InferenceRule{
    private final FlatDataFactory dataFactory;
    private ProofHandler proofHandler;
    private final String ruleName = "ExistsBottom";

    public ExistsBottomInferenceRule(OWLOntology ontology, ProofHandler proofHandler){
        this.dataFactory = new FlatDataFactory(ontology.getOWLOntologyManager().getOWLDataFactory());
        this.proofHandler = proofHandler;
    }

    public boolean applyRule(){
        boolean newInference = false;
        List<OWLSubClassOfAxiom> activeAxioms = new ArrayList<>(this.proofHandler.getActiveAxioms());
        ExistsBottomMatcherLeft matcher = new ExistsBottomMatcherLeft();
        for (OWLSubClassOfAxiom axiom : activeAxioms){
            if (axiom.getSuperClass().isOWLNothing()){
                //  Axiom                   M
                Map<OWLSubClassOfAxiom,List<OWLClassExpression>> leftMatchMap = matcher.MatchToExpression(activeAxioms,axiom.getSubClass());
                if(assembleNewAxioms(axiom, leftMatchMap)){
                    newInference = true;
                }
            }
        }
        return newInference;
    }

    private boolean assembleNewAxioms(OWLSubClassOfAxiom axiom, Map<OWLSubClassOfAxiom,List<OWLClassExpression>> leftMatchMap){
        boolean newInference = false;
        for (Map.Entry<OWLSubClassOfAxiom,List<OWLClassExpression>> entry : leftMatchMap.entrySet()){
            OWLObjectUnionOf union = this.dataFactory.getOWLObjectUnionOf(entry.getValue());
            OWLSubClassOfAxiom newAxiom = this.dataFactory.getOWLSubClassOfAxiom(entry.getKey().getSubClass(), union);

            List<OWLSubClassOfAxiom> premises = new ArrayList<>();
            premises.add(entry.getKey());
            premises.add(axiom);
            if(this.proofHandler.addInference(premises,newAxiom, this.ruleName)){
                newInference = true;
            }
        }
        return newInference;
    }
    public String getRuleName() {
        return ruleName;
    }
}
