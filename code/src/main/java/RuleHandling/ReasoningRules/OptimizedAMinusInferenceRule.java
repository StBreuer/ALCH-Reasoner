package RuleHandling.ReasoningRules;

import RuleHandling.Proofs.ProofHandler;
import RuleHandling.matchVisitors.FilterAtomsVisitor;
import RuleHandling.matchVisitors.NegativeAtomVisitor;
import Utils.FlatDataFactory;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OptimizedAMinusInferenceRule implements InferenceRule{
    private final FlatDataFactory dataFactory;
    private final ProofHandler proofHandler;
    private final String ruleName = "Aminus";
    private final Set<OWLSubClassOfAxiom> consumedAxioms = new HashSet<>();

    public OptimizedAMinusInferenceRule(OWLOntology ontology, ProofHandler proofHandler){
        this.dataFactory = new FlatDataFactory(ontology.getOWLOntologyManager().getOWLDataFactory());
        this.proofHandler = proofHandler;
    }

    public boolean applyRule(){
        boolean newInference = false;
        List<OWLSubClassOfAxiom> activeAxioms = new ArrayList<>(this.proofHandler.getActiveAxioms());
        for(OWLSubClassOfAxiom activeAxiom: activeAxioms) {
            if (consumedAxioms.contains(activeAxiom)){
                continue;
            }
            consumedAxioms.add(activeAxiom);
            NegativeAtomVisitor negativeAtomVisitor = new NegativeAtomVisitor();
            activeAxiom.getSubClass().accept(negativeAtomVisitor);
            Set<OWLClassExpression> negativeAtoms = negativeAtomVisitor.getNegativeAtoms();

            FilterAtomsVisitor filterAtomsVisitor = new FilterAtomsVisitor(negativeAtoms);
            if (activeAxiom.getSuperClass().accept(filterAtomsVisitor)){
                List<OWLClassExpression> restOfUnion = filterAtomsVisitor.getRestOfUnion();

                OWLClassExpression N;
                if (restOfUnion.isEmpty()){
                    N = this.dataFactory.getOWLNothing();
                } else {
                    N = this.dataFactory.getOWLObjectUnionOf(restOfUnion);
                }
                OWLSubClassOfAxiom newAxiom = this.dataFactory.getOWLSubClassOfAxiom(activeAxiom.getSubClass(),N);
                if(this.proofHandler.addInference(activeAxiom,newAxiom,this.ruleName)){
                    newInference = true;
                }
            }

        };
        return newInference;
    }
    public String getRuleName() {
        return ruleName;
    }
}
