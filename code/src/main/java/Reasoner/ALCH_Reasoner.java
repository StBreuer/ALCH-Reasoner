package Reasoner;

import Loading.Normalizer;
import RuleHandling.Proofs.ProofHandler;
import RuleHandling.ReasoningRules.*;
import de.tu_dresden.inf.lat.evee.proofGenerators.ELKProofGenerator;
import de.tu_dresden.inf.lat.evee.proofs.data.Proof;
import de.tu_dresden.inf.lat.evee.proofs.data.exceptions.ProofGenerationException;
import de.tu_dresden.inf.lat.evee.proofs.interfaces.IInference;
import de.tu_dresden.inf.lat.evee.proofs.interfaces.IProof;
import de.tu_dresden.inf.lat.evee.proofs.interfaces.IProofGenerator;
import de.tu_dresden.inf.lat.evee.proofs.json.JsonProofWriter;
import de.tu_dresden.inf.lat.evee.proofs.proofGenerators.TreeProofGenerator;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

import java.io.IOException;
import java.util.*;

public class ALCH_Reasoner implements IProofGenerator<OWLAxiom,OWLOntology> {
    private Set<String> noNewInferences = new HashSet<>();
    private ProofHandler proofHandler;
    private OWLOntology ontology;
    public ALCH_Reasoner(){
    }

    public boolean reason(OWLOntology ontology, OWLSubClassOfAxiom goal) throws OWLOntologyCreationException {
        Normalizer normalizer = new Normalizer();
        OWLOntology normalizedOntology = normalizer.normalize(ontology);
        Set<OWLAxiom> normalizedAxioms = normalizedOntology.getAxioms();
        System.out.println("normalization finished");

        this.proofHandler = new ProofHandler();
        this.proofHandler.setGoal(goal);
        APlusInferenceRule aPlus = new APlusInferenceRule(normalizedOntology, this.proofHandler);
        AMinusInferenceRule aMinus = new AMinusInferenceRule(normalizedOntology, this.proofHandler);
        ConjunctionNInferenceRule conjunctionN = new ConjunctionNInferenceRule(normalizedOntology, this.proofHandler);
        ExistPlusInferenceRule existPlus = new ExistPlusInferenceRule(normalizedOntology, this.proofHandler);
        ExistsBottomInferenceRule existsBottom = new ExistsBottomInferenceRule(normalizedOntology, this.proofHandler);
        ExistsMinusInferenceRule existsMinus = new ExistsMinusInferenceRule(normalizedOntology, this.proofHandler);
        ForAllInferenceRule forAll = new ForAllInferenceRule(normalizedOntology, this.proofHandler);

        while (notFinished(this.proofHandler)){
            applyRule(aPlus);
            applyRule(aMinus);
            applyRule(conjunctionN);
            applyRule(existPlus);
            applyRule(existsBottom);
            applyRule(existsMinus);
            applyRule(forAll);
        }
        System.out.println("reasoning finished");

        if (this.proofHandler.isGoalReached()) {
            System.out.println("goal reached");
            return true;
        } else {
            System.out.println("no proof found");
            return false;
        }
    }

    public IProof<OWLAxiom> getTreeProve(OWLAxiom axiom) throws ProofGenerationException {
        IProof<OWLAxiom> proof = this.getProof(axiom);
        return TreeProofGenerator.getMinimalDepthProof(proof);

        /*
        List<IInference<OWLAxiom>> inferences = this.generateProofBFS(proof, axiom);
        IProof<OWLAxiom> treeProof = new Proof<>(axiom, inferences);
        return treeProof;


         */

    }

    private void applyRule(InferenceRule rule){
        if(rule.applyRule()){
            noNewInferences.remove(rule.getRuleName());
        }else {
            noNewInferences.add(rule.getRuleName());
        }
    }

    private boolean notFinished(ProofHandler proofHandler){
        if (proofHandler.isGoalReached()){
            return false;
        }
        if (this.noNewInferences.size() >= 7){
            return false;
        }
        return true;
    }


    @Override
    public void setOntology(OWLOntology ontology) {
        this.ontology = ontology;
    }

    @Override
    public boolean supportsProof(OWLAxiom axiom) {
        return this.proofHandler.isConclusion(axiom);
    }

    @Override
    public IProof<OWLAxiom> getProof(OWLAxiom axiom) throws ProofGenerationException {
        if (axiom instanceof OWLSubClassOfAxiom){
            try {
                reason(this.ontology,(OWLSubClassOfAxiom) axiom);
            } catch (OWLOntologyCreationException e) {
                e.printStackTrace();
            }
        }else {
            System.out.println("axiom is no Subclassof axiom!!!");
            return null;

        }
        return this.proofHandler.getProof();
    }

    @Override
    public boolean successful() {
        return this.proofHandler.isGoalReached();
    }

    private List<IInference<OWLAxiom>> generateProofRecursive(IProof<OWLAxiom> proof, OWLAxiom goal){
        List<IInference<OWLAxiom>> inferences = proof.getInferences();
        List<IInference<OWLAxiom>> treeProof = new ArrayList<>();
        for (IInference<OWLAxiom> inference : inferences){
            if (inference.getConclusion().equals(goal)){
                inference.getPremises();
                for (OWLAxiom premise : inference.getPremises()){
                    treeProof.addAll(generateProof(proof, premise));
                    treeProof.add(inference);
                }
                return treeProof;
            }
        }
        return treeProof;
    }

    private List<IInference<OWLAxiom>> generateProof(IProof<OWLAxiom> proof, OWLAxiom goal) {
        List<IInference<OWLAxiom>> treeProof = new ArrayList<>();
        Set<IInference<OWLAxiom>> visited = new HashSet<>();
        Deque<IInference<OWLAxiom>> stack = new LinkedList<>();

        // Find the inference(s) that prove the goal and add them to the stack
        proof.getInferences(goal).forEach(stack::push);

        while (!stack.isEmpty()) {
            IInference<OWLAxiom> curr = stack.peek();

            if (visited.contains(curr)) {
                stack.pop();
                continue;
            }

            boolean allVisited = true;

            for (OWLAxiom premise : curr.getPremises()) {
                boolean premiseVisited = false;

                for (IInference<OWLAxiom> premiseInference : proof.getInferences(premise)) {
                    if (visited.contains(premiseInference)) {
                        premiseVisited = true;
                        break;
                    }
                    if (!stack.contains(premiseInference)) {
                        if (!premiseInference.getConclusion().equals(premise)) {
                            stack.push(premiseInference);
                        }
                    }
                }

                if (!premiseVisited) {
                    allVisited = false;
                    break;
                }
            }

            if (allVisited) {
                if (!curr.getConclusion().equals(curr.getPremises().iterator().next())) {
                    treeProof.add(curr);
                }
                visited.add(curr);
                stack.pop();
            }
        }

        return treeProof;
    }

    private List<IInference<OWLAxiom>> generateProofDFS(IProof<OWLAxiom> proof, OWLAxiom goal){
        List<IInference<OWLAxiom>> inferences = proof.getInferences();
        List<IInference<OWLAxiom>> treeProof = new ArrayList<>();
        Set<IInference<OWLAxiom>> visited = new HashSet<>();
        Deque<IInference<OWLAxiom>> stack = new LinkedList<>();

        for (IInference<OWLAxiom> inference : inferences) {
            if (inference.getConclusion().equals(goal)){
                stack.push(inference);
                break;
            }
        }
        while (!stack.isEmpty()){
            IInference<OWLAxiom> currentGoal = stack.pop();
            treeProof.add(currentGoal);
            for (OWLAxiom premise : currentGoal.getPremises()){
                for (IInference<OWLAxiom> inference : inferences){
                    if (inference.getConclusion().equals(premise)){
                        if (!visited.contains(inference)){
                            stack.push(inference);
                            visited.add(inference);
                        }
                    }
                }
            }

        }

        return treeProof;
    }

    private List<IInference<OWLAxiom>> generateProofBFS(IProof<OWLAxiom> proof, OWLAxiom goal){
        List<IInference<OWLAxiom>> inferences = proof.getInferences();
        List<IInference<OWLAxiom>> treeProof = new ArrayList<>();
        Set<IInference<OWLAxiom>> visited = new HashSet<>();
        Deque<IInference<OWLAxiom>> stack = new LinkedList<>();
        Queue<IInference<OWLAxiom>> queue = new LinkedList<>();

        for (IInference<OWLAxiom> inference : inferences) {
            if (inference.getConclusion().equals(goal)){
                queue.add(inference);
                break;
            }
        }
        while (!queue.isEmpty()){
            IInference<OWLAxiom> currentGoal = queue.remove();
            treeProof.add(currentGoal);
            for (OWLAxiom premise : currentGoal.getPremises()){
                for (IInference<OWLAxiom> inference : inferences){
                    if (inference.getConclusion().equals(premise)){
                        if (!visited.contains(inference)){
                            //stack.push(inference);
                            queue.add(inference);
                            visited.add(inference);
                        }
                    }
                }
            }

        }

        return treeProof;
    }



}
