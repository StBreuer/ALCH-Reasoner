package RuleHandling;

import Loading.DecomposeVisitor;
import org.semanticweb.owlapi.model.*;

import java.util.ArrayList;
import java.util.List;

public class NormalizationRuleApplier extends RuleApplier{
    List<NormalizeRule> normalizeRules;

    public NormalizationRuleApplier(OWLDataFactory dataFactory){
        normalizeRules = new ArrayList<>();
        normalizeRules.add(new ConjunctNormalizeRule(dataFactory));
        normalizeRules.add(new DisjunctionNormalizeRule(dataFactory));
        normalizeRules.add(new LeftNegationNormalizeRule(dataFactory));
        normalizeRules.add(new RightNegationNormalizeRule(dataFactory));
    }


    //TODO NOT QUITE SURE IF ONE APPLICATION IS SUFFICIENT

    public List<Tupel<OWLAxiom,List<OWLSubClassOfAxiom>>> applyRules(OWLOntology ontology){
        //     X: old Y: new axioms
        List<Tupel<OWLAxiom, List<OWLSubClassOfAxiom>>> newAxioms = new ArrayList<>();
        ontology.getAxioms().forEach(owlClassAxiom -> {

            for (NormalizeRule rule : normalizeRules){
                if(owlClassAxiom instanceof OWLSubClassOfAxiom){
                    List<OWLSubClassOfAxiom> createdAxioms = applyRule(rule, (OWLSubClassOfAxiom) owlClassAxiom);
                    if(!createdAxioms.isEmpty()){
                        Tupel<OWLAxiom, List<OWLSubClassOfAxiom>> newTuple = new Tupel<>(owlClassAxiom, createdAxioms);
                        newAxioms.add(newTuple);
                    }
                } else if(owlClassAxiom instanceof OWLPropertyAxiom){

                } else {
                    System.out.println("something went wrong in normalisation");
                }
            }
        });

        return newAxioms;
    }

    private List<OWLSubClassOfAxiom> applyRule(NormalizeRule rule, OWLSubClassOfAxiom axiom){
        if (rule.isApplicable(axiom)){
            return rule.applyRule(axiom);
        }
        return new ArrayList<>();
    }



}
