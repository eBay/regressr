### Continuations

Let's start v1 of the mock service.

```docker-compose -f example/continuation/v1.yml up```

Run regressr record (Open a new terminal):
```
./regressr.sh -r record example ../example/continuation/strategy.yml
```

Press CTRL+C to stop the docker-compose process.

Let's start v2 of the mock service.

```docker-compose -f example/continuation/v2.yml up```

Replay to generate a difference report:
```
./regressr.sh compareWith example
[main] INFO org.ebayopensource.regression.internal.workflow.ReplayWorkflow - Starting request traverse_through_HATEOAS_service.
[main] INFO org.ebayopensource.regression.application.aio.graph.AIOGraphContinuationPrinter - Calling URL http://localhost:9882/endpoint/shopping/search/shoes
[main] INFO org.ebayopensource.regression.application.aio.graph.AIOGraphContinuationPrinter - Calling URL http://localhost:9882/endpoint/shopping/items/item1
[main] INFO org.ebayopensource.regression.application.aio.graph.AIOGraphContinuationPrinter - Calling URL http://localhost:9882/endpoint/shopping/items/item2
[main] INFO org.ebayopensource.regression.application.aio.graph.AIOGraphContinuationPrinter - Calling URL http://localhost:9882/endpoint/shopping/items/item3
[main] INFO org.ebayopensource.regression.application.aio.graph.AIOGraphContinuationPrinter - Calling URL http://localhost:9882/endpoint/sellers/acmeseller
[main] INFO org.ebayopensource.regression.application.aio.graph.AIOGraphContinuationPrinter - Calling URL http://localhost:9882/endpoint/sellers/acmeseller
[main] INFO org.ebayopensource.regression.application.aio.graph.AIOGraphContinuationPrinter - Calling URL http://localhost:9882/endpoint/sellers/acmeseller
[main] INFO com.ebay.n.CommandLineMain$ - Report was generated at /Users/asfernando/code/regression-suite/testrun_1496960082820.html
```

Click on **View Regressions** in the report to show the regressions captured.
