package RuleHandling.matchVisitors;

import RuleHandling.Tupel;
import org.semanticweb.owlapi.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LeftExistToRoleVisitor extends MatchVisitor{

    private Map<Tupel<OWLObjectPropertyExpression, OWLClass>, List<Tupel<OWLSubObjectPropertyOfAxiom, OWLSubClassOfAxiom>>> matchMap;
    private Map<OWLObjectPropertyExpression, List<OWLSubObjectPropertyOfAxiom>> roleMap;
    private OWLObjectPropertyExpression currentProperty;
    private OWLSubClassOfAxiom currentAxiom;


    public Map<Tupel<OWLObjectPropertyExpression, OWLClass>, List<Tupel<OWLSubObjectPropertyOfAxiom, OWLSubClassOfAxiom>>> getMatchingTuples(OWLOntology ontology,Map<OWLObjectPropertyExpression,List<OWLSubObjectPropertyOfAxiom>> roleMap){
        this.matchMap = new HashMap<>();
        this.roleMap = roleMap;
        for (OWLAxiom axiom : ontology.getAxioms()){
            axiom.accept(this);
        }
        return this.matchMap;
    }

    @Override
    public void visit(OWLClass ce) {
        //Shows that axiom was not applicable TODO maybe handle differently!?!
        if(this.currentProperty == null){
            return;
        }
        for (OWLSubObjectPropertyOfAxiom roleAxiom : this.roleMap.get(this.currentProperty)) {

            Tupel<OWLObjectPropertyExpression, OWLClass> newKey = new Tupel<>(roleAxiom.getSubProperty(), ce);

            List<Tupel<OWLSubObjectPropertyOfAxiom, OWLSubClassOfAxiom>> newValues = new ArrayList<>();
            newValues.add(new Tupel<>(roleAxiom, this.currentAxiom));


            if (matchMap.containsKey(newKey)){
                matchMap.get(newKey).addAll(newValues);
            } else {
                matchMap.put(newKey, newValues);
            }
        }

    }

    @Override
    public void visit(OWLSubClassOfAxiom axiom) {
        this.currentAxiom = axiom;
        axiom.getSubClass().accept(this);
    }

    @Override
    public void visit(OWLObjectSomeValuesFrom ce) {
        if (this.roleMap.containsKey(ce.getProperty())){
            this.currentProperty = ce.getProperty();
            ce.getFiller().accept(this);
        }
    }
}
