package Loading;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class transforms an Ontology structurally in the way its described in
 * František Simančík, Yevgeny Kazakov, and Ian Horrocks.
 * Consequence-based reasoning beyond Horn ontologies. Technical report, University of Oxford, 2011
 */
public class StructualTransformer{
    StructualTransformer(){}

    public OWLOntology transformOntology(OWLOntology ontology) throws OWLOntologyCreationException {
        OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();
        OWLOntology newOntology = ontologyManager.createOntology();

        List<OWLAxiom> newAxioms = createNewAxioms(ontology);
        newOntology.addAxioms(newAxioms);

        return newOntology;
    }



    private List<OWLAxiom> createNewAxioms(OWLOntology ontology){
        List<OWLAxiom> newAxioms = new ArrayList<>();
        OWLDataFactory dataFactory = ontology.getOWLOntologyManager().getOWLDataFactory();
        NegativePositiveConceptsSeparatorVisitor filterVisitor = new NegativePositiveConceptsSeparatorVisitor();
        StructuralTransformVisitor stVisitor = new StructuralTransformVisitor(dataFactory);


        ontology.getAxioms().forEach(axiom -> {
            if (axiom instanceof OWLSubClassOfAxiom){
                OWLClassExpression flattenedSubExp = stVisitor.flatten(((OWLSubClassOfAxiom) axiom).getSubClass());
                OWLClassExpression flattenedSuperExp = stVisitor.flatten(((OWLSubClassOfAxiom) axiom).getSuperClass());
                //add [C] < [D]
                newAxioms.add(dataFactory.getOWLSubClassOfAxiom(flattenedSubExp, flattenedSuperExp));
                axiom.accept(filterVisitor);
            } else if (axiom instanceof OWLEquivalentClassesAxiom){
                ((OWLEquivalentClassesAxiom) axiom).asOWLSubClassOfAxioms().forEach(owlSubClassOfAxiom -> {
                    OWLClassExpression flattenedSubExp = stVisitor.flatten(owlSubClassOfAxiom.getSubClass());
                    OWLClassExpression flattenedSuperExp = stVisitor.flatten( owlSubClassOfAxiom.getSuperClass());
                    newAxioms.add(dataFactory.getOWLSubClassOfAxiom(flattenedSubExp,flattenedSuperExp));
                    axiom.accept(filterVisitor);
                });

            } else if (axiom instanceof OWLSubObjectPropertyOfAxiom){
                newAxioms.add(axiom);

            } else if (axiom instanceof OWLEquivalentObjectPropertiesAxiom){
                newAxioms.addAll(((OWLEquivalentObjectPropertiesAxiom) axiom).asSubObjectPropertyOfAxioms());
            }
        });

        newAxioms.addAll(createNegativeFlattenedAxioms(filterVisitor.getNegativeExpressions(), stVisitor, dataFactory));
        newAxioms.addAll(createPositiveFlattenedAxioms(filterVisitor.getPositiveExpressions(), stVisitor, dataFactory));

        return newAxioms;
    }

    private Set<OWLAxiom> createNegativeFlattenedAxioms(List<OWLClassExpression> negativeExpressions, StructuralTransformVisitor stVisitor, OWLDataFactory dataFactory){
        Set<OWLAxiom> newAxioms = new HashSet<>();
        negativeExpressions.forEach(classExpression -> {
            newAxioms.add(dataFactory.getOWLSubClassOfAxiom(classExpression.accept(stVisitor), stVisitor.flatten(classExpression)));

        });
        return newAxioms;
    }

    private Set<OWLAxiom> createPositiveFlattenedAxioms(List<OWLClassExpression> positiveExpressions, StructuralTransformVisitor stVisitor, OWLDataFactory dataFactory){
        Set<OWLAxiom> newAxioms = new HashSet<>();
        positiveExpressions.forEach(classExpression -> {
            newAxioms.add(dataFactory.getOWLSubClassOfAxiom(stVisitor.flatten(classExpression), classExpression.accept(stVisitor)));

        });
        return newAxioms;
    }

}
