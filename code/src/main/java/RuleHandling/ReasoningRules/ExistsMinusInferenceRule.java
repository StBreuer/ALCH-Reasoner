package RuleHandling.ReasoningRules;

import RuleHandling.Proofs.ProofHandler;
import RuleHandling.Tupel;
import RuleHandling.matchVisitors.ExistsMinusMatcherLeft;
import RuleHandling.matchVisitors.ExistsMinusMatcherRight;
import RuleHandling.matchVisitors.LeftExistToRoleVisitor;
import RuleHandling.matchVisitors.RoleInclusionVisitor;
import Utils.FlatDataFactory;
import org.semanticweb.owlapi.model.*;

import java.util.*;


public class ExistsMinusInferenceRule implements InferenceRule{
    private FlatDataFactory dataFactory;
    private ProofHandler proofHandler;
    private String ruleName = "ExistsMinus";
    //  Has r and A as keys since these are searched for in the active axioms
    private Map<Tupel<OWLObjectPropertyExpression, OWLClass>, List<Tupel<OWLSubObjectPropertyOfAxiom, OWLSubClassOfAxiom>>> matchMap;
    // ontology axioms are actually needed here
    private List<OWLSubClassOfAxiom> ontologyAxioms;

    public ExistsMinusInferenceRule(OWLOntology ontology, ProofHandler proofHandler){
        this.dataFactory = new FlatDataFactory(ontology.getOWLOntologyManager().getOWLDataFactory());
        this.proofHandler = proofHandler;
        setApplicableAxioms(ontology);
    }

    public List<OWLSubClassOfAxiom> setOntologyAxioms(Set<OWLAxiom> ontologyAxioms){
        this.ontologyAxioms = new ArrayList<>();

        for (OWLAxiom axiom:ontologyAxioms){
            if(axiom instanceof OWLSubClassOfAxiom){
                OWLSubClassOfAxiom subClassOfAxiom = (OWLSubClassOfAxiom)axiom;
                this.ontologyAxioms.add(subClassOfAxiom);
            }
            else {
                System.out.println("axiom is not a OWLSubClassOfAxiom");
            }
        }
        return this.ontologyAxioms;
    }



    private void setApplicableAxioms(OWLOntology ontology){
        RoleInclusionVisitor roleInclusionVisitor = new RoleInclusionVisitor();
        Map<OWLObjectPropertyExpression,List<OWLSubObjectPropertyOfAxiom>> roleInclusions = roleInclusionVisitor.getRoleInclusions(ontology);

        LeftExistToRoleVisitor leftExistToRoleVisitor = new LeftExistToRoleVisitor();
        this.matchMap = leftExistToRoleVisitor.getMatchingTuples(ontology, roleInclusions);
    }

    public boolean applyRule(){
        boolean newInference = false;
        List<OWLSubClassOfAxiom> activeAxioms = new ArrayList<>(this.proofHandler.getActiveAxioms());
        activeAxioms.addAll(this.ontologyAxioms);
        ExistsMinusMatcherLeft leftMatcher = new ExistsMinusMatcherLeft();
        ExistsMinusMatcherRight rightMatcher = new ExistsMinusMatcherRight();
        for (Tupel<OWLObjectPropertyExpression, OWLClass> key : matchMap.keySet()){
            //              K,              Set ( M ) >                 Corresponding Axioms
            Map<Tupel<OWLClassExpression, Set<OWLClassExpression>>, List<OWLSubClassOfAxiom>> leftMap = leftMatcher.MatchToExpression(activeAxioms,key.getX());
            for (Tupel<OWLClassExpression, Set<OWLClassExpression>> leftKey : leftMap.keySet()){
                //  A               Axiom
                Map<OWLClass, List<OWLSubClassOfAxiom>> rightMap = rightMatcher.matchToClass(activeAxioms, key.getY(), leftKey.getX());
                if(assembleNewAxioms(leftMap, rightMap, key)){
                    newInference = true;
                }
            }

        }
        return newInference;
    }

    private boolean assembleNewAxioms(Map<Tupel<OWLClassExpression, Set<OWLClassExpression>>, List<OWLSubClassOfAxiom>> leftMap, Map<OWLClass, List<OWLSubClassOfAxiom>> rightMap,Tupel<OWLObjectPropertyExpression, OWLClass> matchKey ){
        boolean newInference = false;
        for (Tupel<OWLClassExpression, Set<OWLClassExpression>> leftKey :leftMap.keySet()){
            for(OWLSubClassOfAxiom leftBody : leftMap.get(leftKey)){
                for (Tupel<OWLSubObjectPropertyOfAxiom, OWLSubClassOfAxiom> matchBody: this.matchMap.get(matchKey)){
                    for (OWLClass rightClass: rightMap.keySet()){
                        for (OWLSubClassOfAxiom rightAxiom : rightMap.get(rightClass)){
                            // not A
                            OWLObjectComplementOf negatedClass = this.dataFactory.getOWLObjectComplementOf(rightClass);
                            // K and not A
                            OWLClassExpression conjunction = this.dataFactory.getOWLObjectIntersectionOf(rightAxiom.getSubClass(), negatedClass);
                            // exist r.( K and not A)
                            OWLClassExpression existential = this.dataFactory.getOWLObjectSomeValuesFrom(matchKey.getX(),conjunction);
                            // M
                            OWLClassExpression classExpression = this.dataFactory.getOWLObjectUnionOf(leftKey.getY());
                            // M or B
                            classExpression = this.dataFactory.getOWLObjectUnionOf(classExpression, matchBody.getY().getSuperClass());
                            // M or B or (exist r.( K and not A))
                            classExpression = this.dataFactory.getOWLObjectUnionOf(classExpression, existential);
                            //H sub M or B or (exist r.( K and not A))
                            OWLSubClassOfAxiom newAxiom = this.dataFactory.getOWLSubClassOfAxiom(leftBody.getSubClass(),classExpression);




                            List<OWLSubClassOfAxiom> premises = new ArrayList<>();
                            premises.add(leftBody);
                            premises.add(rightAxiom);

                            //Collect Ontology Premise
                            List<OWLAxiom> ontologyPremises = new ArrayList<>();

                            ontologyPremises.add(matchBody.getX());
                            ontologyPremises.add(matchBody.getY());

                            if(proofHandler.addInference(premises,ontologyPremises,newAxiom, this.ruleName)){
                                newInference = true;
                            }
                            this.proofHandler.addActiveClass(conjunction);
                        }
                    }
                }
            }
        }
        return newInference;
    }
    public String getRuleName() {
        return ruleName;
    }
}
