package Loading;

import RuleHandling.NormalizationRuleApplier;
import RuleHandling.Tupel;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.OWLObjectTransformer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Normalizer {
    private final NegativeUniversalsRemover negativeUniversalsRemover = new NegativeUniversalsRemover();
    public Normalizer() {
    }

    public OWLOntology normalize(OWLOntology ontology) throws OWLOntologyCreationException {
        OWLDataFactory dataFactory = ontology.getOWLOntologyManager().getOWLDataFactory();
        Map<OWLClassExpression, OWLClassExpression> replacements = negativeUniversalsRemover.getReplacements(ontology);
        updateOntology(replacements, dataFactory, ontology);
        StructualTransformer stTranformer = new StructualTransformer();
        ontology = stTranformer.transformOntology(ontology);

        NormalizationRuleApplier ruleApplier = new NormalizationRuleApplier(dataFactory);
        List<Tupel<OWLAxiom, List<OWLSubClassOfAxiom>>> byRulesNormalizedAxioms = ruleApplier.applyRules(ontology);

        for (Tupel<OWLAxiom, List<OWLSubClassOfAxiom>> tuple : byRulesNormalizedAxioms){
            ontology.remove(tuple.getX());
            ontology.addAxioms(tuple.getY());
        }

        // add self inclusion of roles
        Set<OWLObjectProperty> properties = ontology.getObjectPropertiesInSignature();
        ontology.addAxioms(createPropertyInclusions(properties, dataFactory));

        return ontology;

    }

    private List<OWLAxiom> createPropertyInclusions(Set<OWLObjectProperty> properties, OWLDataFactory dataFactory){
        List<OWLAxiom> newAxioms = new ArrayList<>();
        for(OWLObjectProperty property: properties){
            OWLAxiom newAxiom = dataFactory.getOWLSubObjectPropertyOfAxiom(property, property);
            newAxioms.add(newAxiom);
        }
        return newAxioms;
    }

    private void updateOntology(Map<OWLClassExpression, OWLClassExpression> replacements, OWLDataFactory dataFactory, OWLOntology ontology){
        OWLObjectTransformer<OWLClassExpression> replacer = new OWLObjectTransformer<>((x) -> true, (input)->{
           OWLClassExpression expression = replacements.get(input);
           if(expression == null){
               return input;
           }
           return expression;
        }, dataFactory, OWLClassExpression.class);
        List<OWLOntologyChange> results = replacer.change(ontology);
        ontology.applyChanges(results);
    }
}
