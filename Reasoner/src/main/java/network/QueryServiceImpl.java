package network;

import io.grpc.stub.StreamObserver;
import reasoner.ReasonerTrigger;

public class QueryServiceImpl extends QueryServiceGrpc.QueryServiceImplBase{

    ReasonerTrigger trigger;

    public QueryServiceImpl(ReasonerTrigger trigger) {
        this.trigger = trigger;
    }

    public void receiveQuery(QueryRequest request, StreamObserver<QueryResponse> responseObserver){
        QueryResponse response = QueryResponse.newBuilder().setResponse(0, trigger.triggerReasoner(request)).build();
        responseObserver.onNext(response);
    }
}
