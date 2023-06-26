package RuleHandling.ReasoningRules;

import RuleHandling.Proofs.ProofHandler;
import RuleHandling.Triple;
import RuleHandling.Tupel;
import RuleHandling.matchVisitors.*;
import Utils.FlatDataFactory;
import org.eclipse.rdf4j.model.vocabulary.OWL;
import org.semanticweb.owlapi.model.*;

import java.util.*;

public class ForAllInferenceRule implements InferenceRule{

    private FlatDataFactory dataFactory;
    private ProofHandler proofHandler;
    private Map<Tupel<OWLObjectPropertyExpression, OWLClass>, List<Tupel<OWLSubObjectPropertyOfAxiom, OWLSubClassOfAxiom>>> matchMap;
    private final String ruleName = "ForAll";



    public ForAllInferenceRule(OWLOntology ontology, ProofHandler proofHandler){
        this.dataFactory = new FlatDataFactory(ontology.getOWLOntologyManager().getOWLDataFactory());
        this.proofHandler = proofHandler;
        setApplicableAxioms(ontology);
    }

    private void setApplicableAxioms(OWLOntology ontology){
        RoleInclusionVisitor roleInclusionVisitor = new RoleInclusionVisitor();
        Map<OWLObjectPropertyExpression,List<OWLSubObjectPropertyOfAxiom>> roleInclusions = roleInclusionVisitor.getRoleInclusions(ontology);

        SuperForAllVisitor leftExistToRoleVisitor = new SuperForAllVisitor();
        this.matchMap = leftExistToRoleVisitor.getMatchingTuples(ontology, roleInclusions);
    }


    public boolean applyRule(){
        boolean newInference = false;
        List<OWLSubClassOfAxiom> activeAxioms = new ArrayList<>(this.proofHandler.getActiveAxioms());
        ForAllMatcherLeft leftMatcher = new ForAllMatcherLeft();
        ForAllMatcherRight rightMatcher = new ForAllMatcherRight();
        //                      r,                          B                   r sub s,                    A sub forall s.B
        for (Map.Entry<Tupel<OWLObjectPropertyExpression, OWLClass>, List<Tupel<OWLSubObjectPropertyOfAxiom, OWLSubClassOfAxiom>>> matchEntry : matchMap.entrySet()){

            for (Tupel<OWLSubObjectPropertyOfAxiom, OWLSubClassOfAxiom> matchValue : matchEntry.getValue()){
                //      H,                      K,                      M,                       Axiom
                Map<OWLClassExpression, List<Triple<OWLClassExpression, Set<OWLClassExpression>, OWLSubClassOfAxiom>>> leftMatchMap = leftMatcher.MatchToExpression(activeAxioms, matchEntry.getKey().getX());
                for (OWLClassExpression leftMatch:leftMatchMap.keySet()){
                    //             N,                     Axiom
                    List<Tupel<List<OWLClassExpression>,OWLSubClassOfAxiom>>rightMatch = rightMatcher.matchToClass(activeAxioms, matchValue.getY().getSubClass(), leftMatch);
                    if(assembleNewAxioms(matchEntry,leftMatchMap.get(leftMatch),rightMatch)){
                        newInference = true;
                    }

                }
            }
        }
        return newInference;
    }

    private boolean assembleNewAxioms(Map.Entry<Tupel<OWLObjectPropertyExpression, OWLClass>, List<Tupel<OWLSubObjectPropertyOfAxiom, OWLSubClassOfAxiom>>> matchEntry, List<Triple<OWLClassExpression, Set<OWLClassExpression>, OWLSubClassOfAxiom>> leftMatch, List<Tupel<List<OWLClassExpression>,OWLSubClassOfAxiom>> rightMatchList){
        boolean newInference = false;
        for (Tupel<OWLSubObjectPropertyOfAxiom, OWLSubClassOfAxiom> matchValue : matchEntry.getValue()){
                for (Triple<OWLClassExpression, Set<OWLClassExpression>, OWLSubClassOfAxiom> leftValue : leftMatch){
                    for (Tupel<List<OWLClassExpression>,OWLSubClassOfAxiom> rightMatch : rightMatchList){
                        OWLObjectUnionOf m = this.dataFactory.getOWLObjectUnionOf(leftValue.getY());
                        OWLObjectUnionOf n = this.dataFactory.getOWLObjectUnionOf(rightMatch.getX());
                        OWLObjectIntersectionOf kAndB = this.dataFactory.getOWLObjectIntersectionOf(leftValue.getX(), matchEntry.getKey().getY());
                        OWLObjectSomeValuesFrom existsRInKAndB = this.dataFactory.getOWLObjectSomeValuesFrom(matchEntry.getKey().getX(), kAndB);
                        OWLObjectUnionOf superConcept = this.dataFactory.getOWLObjectUnionOf(m,n, existsRInKAndB);
                        OWLSubClassOfAxiom newAxiom =  this.dataFactory.getOWLSubClassOfAxiom(rightMatch.getY().getSubClass(), superConcept);

                        List<OWLAxiom> ontologyPremise = new ArrayList<>();
                        ontologyPremise.add(matchValue.getX());
                        ontologyPremise.add(matchValue.getY());

                        List<OWLSubClassOfAxiom> premises = new ArrayList<>();
                        premises.add(leftValue.getZ());
                        premises.add(rightMatch.getY());

                        if(proofHandler.addInference(premises,ontologyPremise,newAxiom,this.ruleName)){
                            newInference = true;
                        }
                        this.proofHandler.addActiveClass(kAndB);
                    }
                }
        }
        return newInference;

    }
    public String getRuleName() {
        return ruleName;
    }
}
