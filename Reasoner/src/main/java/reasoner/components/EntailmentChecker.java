package reasoner.components;

import org.semanticweb.owlapi.model.OWLAxiom;

import java.util.Set;

public interface EntailmentChecker {
    boolean isEntailed(OWLAxiom owlAxiom);

    boolean isEntailed(Set<? extends OWLAxiom> set);
}
