![](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
## SolarQube Analysis
![Static Badge](https://img.shields.io/badge/Maintainability-A-Green)![Static Badge](https://img.shields.io/badge/Duplications-2.6%25-Green)
![Static Badge](https://img.shields.io/badge/Reliability-A-Green)![Static Badge](https://img.shields.io/badge/Coverage-56%25-blue)

# Simple Semantic Web Reasoner
This project implements a simple semantic web reasoner which supports ALC Expressiveness.
The architecture allows to exchange the functionalities necessitated by the interface provided by the OWL API
through an implementation which creates a new interface for each functionality.
The different interfaces can be found in 
`src/main/java/reasoner/components` with their corresponding implementations in 
`src/main/java/reasoner/components/implementations`. If desired, an implementation can be switched out by adapting the 
method in `src/main/jave/reasoner/Reasoner.java`.

## Usage 
To understand how to use the provided code, check out the tests provided in 
`src/test/java/` where test for the type- and instance retrieval as well as test for 
entailments can be found.

## Adding new Ontologies
The ontologies this project uses are placed in `src/main/resources/`. Thus, to add a new ontology it simply needs to be
added to this folder in a format which is supported by the OWL API `OWLManager`.