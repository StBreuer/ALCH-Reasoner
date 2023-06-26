package RuleHandling.ReasoningRules;

import RuleHandling.Proofs.ProofHandler;
import RuleHandling.ReplacerVisitors.DisjunctionDecomposer;
import RuleHandling.ReplacerVisitors.ExistsPlusReplacer;
import RuleHandling.matchVisitors.RightDisjunctionVisitor;
import RuleHandling.matchVisitors.RightExistentialVisitor;
import Utils.FlatDataFactory;
import org.semanticweb.owlapi.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExistPlusInferenceRule implements InferenceRule{
    private Map<OWLClassExpression,OWLClassExpression> applicableOntologyAxioms;
    private FlatDataFactory dataFactory;
    private ProofHandler proofHandler;
    private String ruleName = "ExistPlus";

    public ExistPlusInferenceRule(OWLOntology ontology, ProofHandler proofHandler){
        applicableOntologyAxioms = getApplicableAxioms(ontology);
        this.dataFactory = new FlatDataFactory(ontology.getOWLOntologyManager().getOWLDataFactory());
        this.proofHandler = proofHandler;
    }
    public boolean applyRule(){
        boolean newInference = false;
        List<OWLSubClassOfAxiom> activeAxioms = new ArrayList<>(this.proofHandler.getActiveAxioms());
        RightDisjunctionVisitor disjunctionVisitor = new RightDisjunctionVisitor();
        DisjunctionDecomposer decomposeVisitor = new DisjunctionDecomposer();
        ExistsPlusReplacer replacer = new ExistsPlusReplacer(this.dataFactory);
        for(OWLSubClassOfAxiom axiom: activeAxioms){
            if (axiom.accept(disjunctionVisitor)){
                axiom.getSuperClass().accept(decomposeVisitor);
                List<OWLClassExpression> toMatch = decomposeVisitor.getClassExpressions();
                for(OWLClassExpression toReplace: toMatch){
                    if(this.applicableOntologyAxioms.containsKey(toReplace)){

                        OWLClassExpression newClass = replacer.replace(axiom.getSuperClass(), toReplace, this.applicableOntologyAxioms.get(toReplace));
                        OWLSubClassOfAxiom newAxiom = dataFactory.getOWLSubClassOfAxiom(axiom.getSubClass(), newClass);
                        OWLSubClassOfAxiom ontologyAxiom = dataFactory.getOWLSubClassOfAxiom(toReplace, this.applicableOntologyAxioms.get(toReplace)); // TODO optimize this

                        if(proofHandler.addInference(axiom, ontologyAxiom, newAxiom, this.ruleName)){
                            newInference = true;
                        }
                    }
                };
            }
        };
        return newInference;
    }


    private Map<OWLClassExpression, OWLClassExpression> getApplicableAxioms(OWLOntology ontology){
        Map<OWLClassExpression,OWLClassExpression> decomposedAxiomMap = new HashMap<>();
        RightExistentialVisitor visitor = new RightExistentialVisitor();

        //generalClassAxioms() Does not work somehow
        ontology.getAxioms().forEach(axiom -> {
            if(axiom.accept(visitor)){
                OWLSubClassOfAxiom subClassOfAxiom = ((OWLSubClassOfAxiom)axiom);
                decomposedAxiomMap.put(subClassOfAxiom.getSubClass(),subClassOfAxiom.getSuperClass());
            }
        });
        return decomposedAxiomMap;
    }
    public String getRuleName() {
        return ruleName;
    }

}
