package reasoner.components;

import org.semanticweb.owlapi.model.OWLAxiom;

import java.util.Optional;
import java.util.Set;

public interface EntailmentChecker {
    boolean isEntailed(OWLAxiom owlAxiom);

    Optional<Set<OWLAxiom>> isEntailed(Set<? extends OWLAxiom> set);
}
