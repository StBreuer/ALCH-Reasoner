package RuleHandling.ReasoningRules;

import RuleHandling.Proofs.ProofHandler;
import RuleHandling.Tupel;
import RuleHandling.matchVisitors.AtomConjunctionMatcher;
import RuleHandling.matchVisitors.ConjunctionNMatcher;
import Utils.FlatDataFactory;
import org.semanticweb.owlapi.model.*;

import java.util.*;

public class ConjunctionNInferenceRule implements InferenceRule{
    //                  set of subclass (set since containment check) x axiom to get superclass
    private List<Tupel<Set<OWLClass>, OWLSubClassOfAxiom>> applicableOntologyAxioms;
    private FlatDataFactory dataFactory;
    private ProofHandler proofHandler;
    private String ruleName = "ConjunctionN";
    private boolean newInference;
    public ConjunctionNInferenceRule(OWLOntology ontology, ProofHandler proofHandler){
        this.dataFactory = new FlatDataFactory(ontology.getOWLOntologyManager().getOWLDataFactory());
        this.applicableOntologyAxioms = getApplicableAxioms(ontology);
        this.proofHandler = proofHandler;
    }

    private List<Tupel<Set<OWLClass>, OWLSubClassOfAxiom>> getApplicableAxioms(OWLOntology ontology){
        AtomConjunctionMatcher matcher = new AtomConjunctionMatcher();
        return matcher.getMatches(ontology.getAxioms());
    }

    public boolean applyRule() {
        this.newInference = false;
        Set<OWLSubClassOfAxiom> activeAxioms = this.proofHandler.getActiveAxioms();
        ConjunctionNMatcher matcher = new ConjunctionNMatcher();
        Map<OWLClassExpression, Map<OWLClass, List<Tupel<Set<OWLClass>, OWLSubClassOfAxiom>>>> matches = matcher.getMatches(activeAxioms);
        for(Tupel<Set<OWLClass>, OWLSubClassOfAxiom> ontologyTuple : this.applicableOntologyAxioms){
            for (Map.Entry<OWLClassExpression, Map<OWLClass, List<Tupel<Set<OWLClass>, OWLSubClassOfAxiom>>>> matchEntry: matches.entrySet()){
                Map<OWLClass, List<Tupel<Set<OWLClass>, OWLSubClassOfAxiom>>> matchValue = matchEntry.getValue();
                Set<OWLClass> valueKeys = matchValue.keySet();
                if (valueKeys.containsAll(ontologyTuple.getX())){

                    Set<OWLClassExpression> niSet = new HashSet<>();
                    Set<OWLSubClassOfAxiom> premise = new HashSet<>();
                    List<OWLClass> a_iList = new ArrayList<>(ontologyTuple.getX());
                    //recursive function
                    createRule(niSet, premise, a_iList, matchEntry, ontologyTuple);


                   /* for (OWLClass a_i : ontologyTuple.getX()){

                        for (Tupel<Set<OWLClass>, OWLSubClassOfAxiom> matchTuple : matchEntry.getValue().get(a_i)){
                            //multiple nis
                            niSet.addAll(matchTuple.getX()); //does only contain N_i
                            premise.add(matchTuple.getY());

                        }
                    }

                    //I don't want some Ai being left in the Nis ACTUALLY I DO!!!!!!!!!!!!!
                    //niSet.removeAll(ontologyTuple.getX());

                    OWLClassExpression allNisAndM;
                    if(niSet.size() == 0){
                        allNisAndM = ontologyTuple.getY().getSuperClass();
                    }else{
                        OWLClassExpression allNis = getOWLObjectUnionOf(niSet);
                        allNisAndM = this.dataFactory.getOWLObjectUnionOf(allNis, ontologyTuple.getY().getSuperClass());
                    }
                    OWLSubClassOfAxiom newAxiom = this.dataFactory.getOWLSubClassOfAxiom(matchEntry.getKey(),allNisAndM);

                    if(proofHandler.addInference(new ArrayList<>(premise),ontologyTuple.getY(),newAxiom, this.ruleName)){
                        newInference = true;
                    }*/

                }
            }
        }
        return newInference;
    }

    private void createRule(Set<OWLClassExpression> ni, Set<OWLSubClassOfAxiom> premise, List<OWLClass> ais, Map.Entry<OWLClassExpression, Map<OWLClass, List<Tupel<Set<OWLClass>, OWLSubClassOfAxiom>>>> matchEntry, Tupel<Set<OWLClass>, OWLSubClassOfAxiom> ontologyTuple){
        if(ais.isEmpty()){
            OWLClassExpression allNisAndM;
            if(ni.size() == 0){
                allNisAndM = ontologyTuple.getY().getSuperClass();
            }else{
                OWLClassExpression allNis = getOWLObjectUnionOf(ni);
                allNisAndM = this.dataFactory.getOWLObjectUnionOf(allNis, ontologyTuple.getY().getSuperClass());
            }
            OWLSubClassOfAxiom newAxiom = this.dataFactory.getOWLSubClassOfAxiom(matchEntry.getKey(),allNisAndM);

            if(proofHandler.addInference(new ArrayList<>(premise),ontologyTuple.getY(),newAxiom, this.ruleName)){
                this.newInference = true;
            }
        } else {
            List<OWLClass> ais2pass = new ArrayList<>(ais);
            OWLClassExpression a_i = ais2pass.remove(0);

            for (Tupel<Set<OWLClass>, OWLSubClassOfAxiom> matchTuple : matchEntry.getValue().get(a_i)){
                //multiple nis
                Set ni2pass = new HashSet(ni);
                ni2pass.addAll(matchTuple.getX());

                Set<OWLSubClassOfAxiom> premise2pass = new HashSet<>(premise);
                premise2pass.add(matchTuple.getY());
                createRule(ni2pass, premise2pass, ais2pass, matchEntry, ontologyTuple);

            }
        }

    }

    private OWLClassExpression getOWLObjectUnionOf(Set<OWLClassExpression> toUnion){
        if (toUnion.size() == 1){
            return toUnion.stream().findAny().get();
        } else {
            return this.dataFactory.getOWLObjectUnionOf(toUnion);
        }
    }

    public String getRuleName() {
        return ruleName;
    }

}
