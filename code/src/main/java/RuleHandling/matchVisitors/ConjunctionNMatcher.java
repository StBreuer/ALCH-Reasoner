package RuleHandling.matchVisitors;

import RuleHandling.Tupel;
import org.semanticweb.owlapi.model.*;

import java.util.*;

public class ConjunctionNMatcher extends BooleanMatchVisitor{

    Set<OWLClass> classes;
    //      H                           A_i         List of N_i and A_i    hole axiom (list since maybe not unique)
    public Map<OWLClassExpression, Map<OWLClass, List<Tupel<Set<OWLClass>, OWLSubClassOfAxiom>>>> getMatches(Set<OWLSubClassOfAxiom> activeAxioms){
        //Map<OWLClassExpression,Tupel<Set<OWLClass>, Map<OWLClass,Tupel<List<OWLClassExpression>,OWLSubClassOfAxiom>>>> matches = new HashMap<>();
        Map<OWLClassExpression, Map<OWLClass, List<Tupel<Set<OWLClass>, OWLSubClassOfAxiom>>>> matches = new HashMap<>();

        for(OWLSubClassOfAxiom axiom : activeAxioms){
            classes = new HashSet<>();
            if (axiom.getSuperClass().accept(this)){
                OWLClassExpression h = axiom.getSubClass();
                if (matches.containsKey(h)){
                    fillSubMap(matches.get(h), axiom);
                }else{
                    Map<OWLClass, List<Tupel<Set<OWLClass>, OWLSubClassOfAxiom>>> map = new HashMap<>();
                    fillSubMap(map, axiom);
                    matches.put(h,map);
                }
            }
        }
        return matches;
    }

    private void fillSubMap(Map<OWLClass, List<Tupel<Set<OWLClass>, OWLSubClassOfAxiom>>> map, OWLSubClassOfAxiom axiom){
        for(OWLClass concept: this.classes){
            Set<OWLClass> n_i = new HashSet<>();
            n_i.addAll(this.classes);
            n_i.remove(concept);
            if (map.containsKey(concept)){
                map.get(concept).add(new Tupel<>(n_i,axiom));
            }else {
                List<Tupel<Set<OWLClass>, OWLSubClassOfAxiom>> list = new ArrayList<>();
                list.add(new Tupel<>(n_i,axiom));
                map.put(concept, list);
            }
        }
    }

    @Override
    public Boolean visit(OWLObjectUnionOf ce) {
        Boolean rtn = true;
        for(OWLClassExpression operand : ce.getOperandsAsList()){
            rtn = operand.accept(this);
        }
        return rtn;
    }

    @Override
    public Boolean visit(OWLClass ce) {
        this.classes.add(ce);
        return true;
    }

}
