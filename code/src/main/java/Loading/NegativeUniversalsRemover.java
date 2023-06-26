package Loading;

import org.semanticweb.owlapi.model.*;

import java.util.*;

public class NegativeUniversalsRemover {
    private boolean changed;
    public NegativeUniversalsRemover(){

    }

    public Map<OWLClassExpression, OWLClassExpression> getReplacements(OWLOntology ontology){
        Set<OWLClassExpression> classExpressions = getClassExpressionsOfOntology(ontology);
        return fillReplacements(classExpressions, ontology);

    }


    private Map<OWLClassExpression, OWLClassExpression> fillReplacements(Set<OWLClassExpression> classExpressions, OWLOntology ontology){
        final Map<OWLClassExpression, OWLClassExpression> replacements = new HashMap<>();
        final DecomposeVisitor visitor = new DecomposeVisitor();
        final OWLDataFactory dataFactory = ontology.getOWLOntologyManager().getOWLDataFactory();

        for (OWLClassExpression expression : classExpressions){
            setChanged(false);
            OWLClassExpression replacement = rewriteExpression(expression, visitor, dataFactory,false);
            if (isChanged()){
                replacements.put(expression, replacement);
            }
        }
        return replacements;
    }
    private Set<OWLClassExpression> getClassExpressionsOfOntology(OWLOntology ontology){
        DecomposeVisitor visitor = new DecomposeVisitor();
        Set<OWLClassExpression> classExpressions = new HashSet<>();
        ontology.getAxioms().forEach(axiom ->{
            axiom.accept(visitor);
            if (visitor.isAxiomUseful()){
                classExpressions.addAll(visitor.getOperands());
            }
        });
        return classExpressions;
    }

    private OWLClassExpression rewriteExpression(OWLClassExpression expression, DecomposeVisitor visitor,OWLDataFactory dataFactory , boolean negative){
        expression.accept(visitor);
        Operator operator = visitor.getOperator();
        switch (operator){
            case CLASS -> {
                return expression;
            }
            case UNION -> {
                List<OWLClassExpression> oldOperands = visitor.getOperands();
                List<OWLClassExpression> newOperands = new LinkedList<>();
                for (OWLClassExpression operand : oldOperands){
                    newOperands.add(rewriteExpression(operand, visitor, dataFactory, negative));
                }
                return dataFactory.getOWLObjectUnionOf(newOperands);
            }
            case INTERSECTION -> {
                List<OWLClassExpression> oldOperands = visitor.getOperands();
                List<OWLClassExpression> newOperands = new LinkedList<>();
                for (OWLClassExpression operand : oldOperands){
                    newOperands.add(rewriteExpression(operand, visitor, dataFactory, negative));
                }
                return dataFactory.getOWLObjectIntersectionOf(newOperands);
            }
            case ALL_VALUES_FROM -> {
                if (negative){
                    setChanged(true);
                    OWLObjectPropertyExpression property = visitor.getProperty();
                    OWLClassExpression filler = visitor.getOperand();
                    filler =rewriteExpression(filler, visitor, dataFactory, true);
                    OWLObjectComplementOf complement = dataFactory.getOWLObjectComplementOf(filler);
                    OWLObjectSomeValuesFrom exists = dataFactory.getOWLObjectSomeValuesFrom(property, complement);
                    return dataFactory.getOWLObjectComplementOf(exists);
                }else {
                    OWLObjectPropertyExpression property = visitor.getProperty();
                    OWLClassExpression filler = visitor.getOperand();
                    filler = rewriteExpression(filler, visitor, dataFactory, false);
                    return dataFactory.getOWLObjectAllValuesFrom(property, filler);
                }
            }
            case SOME_VALUE_FROM -> {
                OWLObjectPropertyExpression property = visitor.getProperty();
                OWLClassExpression filler = visitor.getOperand();
                filler = rewriteExpression(filler, visitor, dataFactory, negative);
                return dataFactory.getOWLObjectSomeValuesFrom(property, filler);
            }
            case COMPLEMENT -> {
                OWLClassExpression operand = visitor.getOperand();
                operand = rewriteExpression(operand, visitor, dataFactory, reverseBoolean(negative));
                return dataFactory.getOWLObjectComplementOf(operand);
            }
        }
        return null;
    }

    private boolean reverseBoolean(boolean bool){
        if (bool){
            return false;
        }
        return true;
    }


    private boolean isChanged() {
        return changed;
    }

    private void setChanged(boolean changesOccur) {
        this.changed = changesOccur;
    }
}
