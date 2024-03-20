const {QueryRequest} = require("./QueryService_pb.js")
const {QueryService} = require("./QueryService_grpc_web_pb.js")

var queryService = new QueryService("https.//localhost:8080")

function sendRequest(query){
    var request = new QueryRequest();
    request.setQuery(query);
    queryService.sendRequest(request);
}