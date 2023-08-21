package RuleHandling.Proofs;

import RuleHandling.Tupel;
import de.tu_dresden.inf.lat.evee.proofs.data.Inference;
import de.tu_dresden.inf.lat.evee.proofs.data.Proof;
import de.tu_dresden.inf.lat.evee.proofs.interfaces.IProof;
import de.tu_dresden.inf.lat.evee.proofs.proofGenerators.MinimalTreeProofGenerator;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProofHandler {


    private Set<OWLClassExpression> activeClasses;
    private Set<Inference<OWLAxiom>> inferences = new HashSet<>();
    private OWLSubClassOfAxiom goal;
    private OWLAxiom finalConclusion;
    Set<OWLSubClassOfAxiom> activeAxioms = new HashSet<>();
    boolean goalReached = false;

    int AminusCounter = 0;
    int AplusCounter = 0;
    int ConjunctionNCounter = 0;
    int ExistPlusCounter = 0;
    int ExistsBottomCounter = 0;
    int ExistsMinusCounter = 0;
    int ForAllCounter = 0;

    private void updateCounter(Inference<OWLAxiom> inference){
        if(inference.getRuleName().equals("Aminus")){
            AminusCounter++;
        }
        if(inference.getRuleName().equals("Aplus")){
            AplusCounter++;
        }
        if(inference.getRuleName().equals("ConjunctionN")){
            ConjunctionNCounter++;
        }
        if(inference.getRuleName().equals("ExistPlus")){
            ExistPlusCounter++;
        }
        if(inference.getRuleName().equals("ExistsBottom")){
            ExistsBottomCounter++;
        }
        if(inference.getRuleName().equals("ExistsMinus")){
            ExistsMinusCounter++;
        }
        if(inference.getRuleName().equals("ForAll")){
            ForAllCounter++;
        }

    }
    public ProofHandler(OWLSubClassOfAxiom goal, Set<OWLAxiom> axioms){
        setGoal(goal);
        this.finalConclusion = finalConclusion;
        addActiveAxioms(axioms);
    }
    public ProofHandler(OWLSubClassOfAxiom goal){
        setGoal(goal);
    }
    public ProofHandler(){
    }

    public void addActiveAxioms(Set<OWLAxiom> axioms){
        for (OWLAxiom axiom:axioms){
            if(axiom instanceof OWLSubClassOfAxiom){
                OWLSubClassOfAxiom subClassOfAxiom = (OWLSubClassOfAxiom)axiom;
                this.activeAxioms.add(subClassOfAxiom);
            }
            else {
                System.out.println("axiom is not a OWLSubClassOfAxiom");
            }
        }
    }

    public boolean isConclusion(OWLAxiom conclusion){
        for (Inference<OWLAxiom> inference:inferences){
            if(inference.getConclusion().equals(conclusion)){
                return true;
            }
        }
        return false;
    }
    public Proof<OWLAxiom> getProof(){
        Proof<OWLAxiom> proof = new Proof<>(this.finalConclusion);
        proof.addInferences(Collections.unmodifiableSet(this.inferences)); //don't understand why an unmodifiableSet is needed but Intellij knows better I guess
        return proof;
    }

    public void setGoal(OWLSubClassOfAxiom goal){
        this.activeClasses = new HashSet<>();
        this.activeClasses.add(goal.getSubClass());
        this.goal = goal;
    }
    public boolean isGoalReached(){
        return goalReached;
    }
    private void setGoalReached(Inference<OWLAxiom> inference){
        if (inference.getConclusion() instanceof OWLSubClassOfAxiom){
            OWLSubClassOfAxiom axiom = (OWLSubClassOfAxiom)inference.getConclusion();
            if(axiom.equals(this.goal) ){
                this.goalReached = true;
                this.finalConclusion  = inference.getConclusion();
            }
        }else{
            System.out.println("conclusion is not a OWLSubClassOfAxiom");
        }


    }



    private boolean addInferenceToList(Inference<OWLAxiom> inference){
        if(this.inferences.contains(inference)){
            return false;
        }
        updateCounter(inference);
        setGoalReached(inference);
        this.inferences.add(inference);
        return true;
    }
    private void addOntologyInferenceToList(Inference<OWLAxiom> inference){
        if(this.inferences.contains(inference)){
            return;
        }
        this.inferences.add(inference);
    }

    private void addOntologyInferencesToList(List<OWLAxiom> ontologyPremises){
        for (OWLAxiom ontologyPremise:ontologyPremises){
            addOntologyInferenceToList(ontologyPremise);
        }
    }
    private void addOntologyInferenceToList(OWLAxiom ontologyPremise){
        List<OWLAxiom> premises = new ArrayList<>();
        String ruleName = "Ontology";
        Inference<OWLAxiom> inference = new Inference<>(ontologyPremise,ruleName,premises);
        addOntologyInferenceToList(inference);
    }


    public boolean addInference(OWLSubClassOfAxiom premise, OWLSubClassOfAxiom conclusion, String ruleName){
        this.activeAxioms.add(conclusion);
        List<OWLSubClassOfAxiom> premises = new ArrayList<>();
        premises.add(premise);
        Inference<OWLAxiom> inference = new Inference<>(conclusion,ruleName,premises);
        return addInferenceToList(inference);
    }
    public boolean addInference(List<OWLSubClassOfAxiom> premises, OWLSubClassOfAxiom conclusion, String ruleName){
        this.activeAxioms.add(conclusion);
        Inference<OWLAxiom> inference = new Inference<>(conclusion,ruleName,premises);

        return addInferenceToList(inference);
    }

    public boolean addInference(OWLSubClassOfAxiom premise, OWLSubClassOfAxiom ontologyPremise, OWLSubClassOfAxiom conclusion, String ruleName){
        this.activeAxioms.add(conclusion);
        List<OWLSubClassOfAxiom> premises = new ArrayList<>();
        premises.add(premise);
        premises.add(ontologyPremise);
        addOntologyInferenceToList(ontologyPremise);
        Inference<OWLAxiom> inference = new Inference<>(conclusion,ruleName,premises);
        return addInferenceToList(inference);
    }

    //Assumes premises are not changed anymore
    public boolean addInference(List<OWLSubClassOfAxiom> premises, OWLSubClassOfAxiom ontologyPremise, OWLSubClassOfAxiom conclusion, String ruleName ) {
        this.activeAxioms.add(conclusion);
        premises.add(ontologyPremise);
        addOntologyInferenceToList(ontologyPremise);
        Inference<OWLAxiom> inference = new Inference<>(conclusion, ruleName, premises);
        return addInferenceToList(inference);
    }

    public boolean addInference(List<OWLSubClassOfAxiom> premises, List<OWLAxiom> ontologyPremises, OWLSubClassOfAxiom conclusion, String ruleName){
        this.activeAxioms.add(conclusion);
        List<OWLAxiom> completePremises = Stream.concat(premises.stream(), ontologyPremises.stream()).collect(Collectors.toList());
        addOntologyInferencesToList(ontologyPremises);
        Inference<OWLAxiom> inference = new Inference<>(conclusion, ruleName, completePremises);
        return addInferenceToList(inference);

    }

    /*
    public void addInference(List<OWLSubClassOfAxiom> premises, List<OWLSubClassOfAxiom> ontologyPremise, OWLSubClassOfAxiom conclusion, String ruleName ){

        if(premises.isEmpty()){
            //In Case only ontology axioms are used
            Set<Inference<OWLSubClassOfAxiom>> inferences = new HashSet<>();
            inferences.add(new Inference<>(conclusion,ruleName,premises));

            Set<OWLSubClassOfAxiom> containedAxioms = new HashSet<>();
            containedAxioms.add(conclusion);
            //this.emptyPremiseAxioms.add(conclusion);

            this.proofs.add(new Tupel<>(inferences, containedAxioms));
        }
        for (Tupel<Set<Inference<OWLSubClassOfAxiom>>,Set<OWLSubClassOfAxiom>> proof : proofs){
            if (proof.getY().containsAll(premises)){
                if (!proof.getY().contains(conclusion)){


                    List<OWLSubClassOfAxiom> completePremises = Stream.concat(premises.stream(), ontologyPremise.stream()).collect(Collectors.toList());
                    proof.getX().add(new Inference<>(conclusion,ruleName,completePremises));
                    proof.getY().add(conclusion);
                }
            }
        }

        Inference<OWLSubClassOfAxiom> inference = new Inference<>(conclusion, ruleName, premises);
        activeAxioms.add(conclusion);
    }

     */

    public boolean addInferences(List<OWLSubClassOfAxiom> premises, List<OWLAxiom> ontologyPremise, List<OWLSubClassOfAxiom> conclusions, String ruleName){
        boolean newInference = false;
        for (OWLSubClassOfAxiom conclusion: conclusions){
           if(addInference(premises, ontologyPremise, conclusion, ruleName)){
               newInference = true;
           }
        }
       return newInference;
    }

    //TODO WHY NOT USED!!!? I THINK USED IN THE ADD AXIOM METHOD ?
    public void addActiveClass(OWLClassExpression activeClass){
        this.activeClasses.add(activeClass);
    }
    public Set<OWLClassExpression> getActiveClasses() {
        return activeClasses;
    }

    public Set<OWLSubClassOfAxiom> getActiveAxioms() {
        return activeAxioms;
    }


}
