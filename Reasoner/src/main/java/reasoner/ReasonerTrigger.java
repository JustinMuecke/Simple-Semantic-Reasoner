package reasoner;


import network.QueryRequest;
import network.QueryResponse;

public class ReasonerTrigger {

    Reasoner reasoner;
    public ReasonerTrigger(Reasoner reasoner){
        this.reasoner = reasoner;
    }

    public String triggerReasoner(QueryRequest query){
        System.out.println(query.getQuery());
        //response = reasoner.trigger(query)
        return query.getQuery();
    }
}
