# reasoner
Is the main component of the Project and has capabilities to perform 
Semantic Reasoning over Ontologies which have ALC expressiveness.

## ALCReasoner
Interface extending the OWLReasoner interface and returns a default value (Empty Node /Empty NodeSet/false) for all
methods which are only needed for expressiveness higher than ALC.

## Reasoner 
Simple implementation implementing the ALCReasoner interface. Each method contains of a method call to an 
interface implementation. To insert a different implementation of a functionality, one only needs to change 
the corresponding method to use the new implementation.

## Components 
Contains all the interfaces used in the reasoner.