package Loading;

import org.semanticweb.owlapi.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StructuralTransformVisitor implements OWLObjectVisitorEx<OWLClassExpression> {

    private OWLDataFactory dataFactory;

    public StructuralTransformVisitor(OWLDataFactory dataFactory){

        this.dataFactory = dataFactory;
    }

    private List<OWLClassExpression> flatten(List<OWLClassExpression> classExpressions){
        List<OWLClassExpression> flattened = new ArrayList<>();
        classExpressions.forEach(classExpression ->{
            String string = stringify(classExpression);
            flattened.add(toClassExpression(string));
        });
        return flattened;

    }
    public OWLClassExpression flatten(OWLClassExpression classExpression){
        String string = stringify(classExpression);
        return toClassExpression(string);
    }

    //TODO how to handle ontology IRIs
    private OWLClassExpression toClassExpression(String string){
        return this.dataFactory.getOWLClass(string);
    }
    private String stringify(OWLClassExpression owlClassExpression){
        String str = owlClassExpression.toString();
        str = str.replaceAll("[<|>]", "");
        return "[" + str + "]";
    }

    @Override
    public OWLClassExpression visit(OWLClass ce) {
        return ce;
    }


    //Should not use this anymore
    @Override
    public OWLClassExpression visit(OWLOntology ontology) {
        ontology.generalClassAxioms().forEach(owlClassAxiom -> {
            owlClassAxiom.accept(this);
        });
        return null;
    }

    @Override
    public OWLClassExpression visit(OWLObjectIntersectionOf ce) {
        List<OWLClassExpression> operands = flatten(ce.getOperandsAsList());
        OWLObjectIntersectionOf flattenedIntersection= this.dataFactory.getOWLObjectIntersectionOf(operands);
        return flattenedIntersection;
    }

    @Override
    public OWLClassExpression visit(OWLObjectUnionOf ce) {
        List<OWLClassExpression> operands = flatten(ce.getOperandsAsList());
        return this.dataFactory.getOWLObjectUnionOf(operands);
    }

    @Override
    public OWLClassExpression visit(OWLObjectComplementOf ce) {
        OWLClassExpression newOperand = flatten(ce.getOperand());
        return this.dataFactory.getOWLObjectComplementOf(newOperand);
    }

    @Override
    public OWLClassExpression visit(OWLObjectSomeValuesFrom ce) {
        OWLObjectPropertyExpression property = ce.getProperty();
        OWLClassExpression flattenedFiller = flatten(ce.getFiller());
        return this.dataFactory.getOWLObjectSomeValuesFrom(property,flattenedFiller);
    }

    @Override
    public OWLClassExpression visit(OWLObjectAllValuesFrom ce) {
        OWLObjectPropertyExpression property = ce.getProperty();
        OWLClassExpression flattenedFiller = flatten(ce.getFiller());
        return this.dataFactory.getOWLObjectAllValuesFrom(property,flattenedFiller);

    }
}
