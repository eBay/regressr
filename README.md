<p align="center"><img src="https://github.com/eBay/regressr/blob/master/Regressr.png"/></p>

[![Build Status](https://travis-ci.org/eBay/regressr.svg?branch=master)](https://travis-ci.org/eBay/regressr) ![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg) [![Coverage Status](https://codecov.io/gh/eBay/regressr/branch/master/graph/badge.svg)](https://codecov.io/github/eBay/regressr)

Regressr is a command line tool to test http services. With Regressr, a test engineer can write several strategies that test several request-response conversations. Once the strategies are created, they can be run against the service to record the gold standard. When a new build is deployed, the strategies can be run again to capture any regressions and a comparison report is generated which can be used to roll back the new build if required. 

Regressr aims to keep regression testing SIMPLE and overhead to a minimum.

Features:

 * Customizable to all kinds of HTTP based services. Write what to call, what to record and what to compare. Regressr provides default implementations of these [components](https://github.com/ebay/regressr/tree/master/src/main/scala/org/ebayopensource/regression/internal/components) for your convenience. If these do not work for your requirements, write your own components in Scala. 
 * Super simple to grok with a low conceptual surface area. Write a basic regression suite quickly within minutes. Or write a complex and exhaustive service regression suite within a few days of using Regressr instead of months.
 * Pluggable datastores and report generators.
 * Command line execution and report generation.

## Prerequisites
* OSX
* SBT [install from here](http://www.scala-sbt.org/download.html)

## Quick Start
```git clone git@github.ebay.com:eBay/regressr.git```

For your convenience, regressr comes with some mock services out of the box. 

Let's start v1 of the mock service.

```docker-compose -f example/v1.yml up```

Run regressr record (Open a new terminal):
```
./regressr.sh -r record example example/example.yml
```

Press CTRL+C to stop the docker-compose process.

Let's start v2 of the mock service.

```docker-compose -f example/v2.yml up```

Replay to generate a difference report:
```
./regressr.sh compareWith example
[main] INFO org.ebayopensource.regression.internal.workflow.ReplayWorkflow$ - Starting request say_hello
[main] INFO org.ebayopensource.regression.internal.workflow.ReplayWorkflow$ - Starting request shop_for_a_pair_of_shoes
[main] INFO com.ebay.n.CommandLineMain$ - Report was generated at /Users/asfernando/code/regression-suite/testrun_1495742693840.html
open /Users/asfernando/code/regression-suite/testrun_1495742693840.html
```

Click on **View Regressions** in the report to show the regressions captured.

## How does it work

Regressr supports two modes of execution:

* Record - Use this mode to record the gold standard.
* Compare - Use this mode to compare the current version of the service with a previous recording.

The diagram explains what is done in these two modes:

<p align="center"><img src="https://github.com/ebay/regressr/blob/master/RegressrFlow.png"/></p>

One question that users may have is about recorded and replayed entries. Entries are strings that the tester chooses to record and compare. An example of an entry could be the HTTP response status code. Recording an entry like this will catch regressions in change of response status codes between the two runs.

For comparing two valid JSONs, use the JSON comparator that comes with Regressr: https://github.com/ebay/regressr/blob/master/src/main/scala/org/ebayopensource/regression/internal/components/comparator/SimpleHTTPJsonComparator.scala

This comparator ignores differences in values.

## Write a test strategy file

Regressr is highly customizable for testing HTTP services. To achieve customizability as well as ease of use, Regressr comes with a set of components ready to use, while giving test engineers full flexibility to extend and write their own test cases.

Take a look at example/example.yml. There are at a minimum of 2 components that need to be created and configured per request. These are Recorder and Comparator.
 
#### Recorder
This component chooses what to record from the response. This also contains the request as well. This component receives the HTTP response + request and expects a RequestRecordingEntry. The test engineer is free to write an implementation of the Recorder class. Use the recorder to capture interesting pieces of data that needs to be recorded. 

Regressr comes with an out of the box recorder component **SimpleHTTPJSONRecorder** which records the request url, headers, method, response url, headers, status code and compares the structure of the JSON body (ignoring values).

#### Comparator
This component is used to explain how the comparison is done. Comparing two strings might be really simple vs. comparing two well formed json formats - i.e. the latter can be subjective - comparing JSONs strict (with values), structure only. For most JSON based services, Regressr comes with an out of the box Comparator **SimpleHTTPJsonComparator**. Regressr calls the comparator during the **compareWith** phase with the recorded and replayed request+response and expects 0 or more CompareMessage instances. CompareMessage instances are used to capture regressions when present.

#### Request Payload Builder [Optional]
For request types that accept an HTTP payload such as POST, PUT and the like, the request builder offers a flexible way to convert a map of [String, String] entries from the strategy file's dataInput element to a HTTP payload. This is super powerful because if the external service's contract changes and you have 1000 requests, changing the request builder will adapt those requests automatically to the new service.

#### Continuation [Optional]
Continuations are an advanced feature in regressr. Think about an auto-traversable HATEOAS service like [this](https://developer.paypal.com/docs/api/hateoas-links/)  What if you would like to auto-traverse the resources automatically without specifying each request explicitly in the strategy file? This is where continuations come in. 

Use this component when you want to generate a new (or several) new requests programmatically from a previous response. The interface for this class is below:

```def getContinuations(resp: HTTPResponse) : Try[Seq[HTTPRequest]]```

For each HTTPResponse that is received by Regressr, the continuation is expected to create 0 or more HTTPRequest instances that are used to make requests. Also remember to create a base case when the continuation will stop by returning an empty Seq() 

The ideal use case for continuations is when you want to traverse a resource tree of HATEOAS RESTful services. An example of continuations is [here](https://github.com/ebay/regressr/tree/master/example/continuation/README.md).

#### ProgressPrinter [Optional]
For simple requests, Regressr auto prints the requests as they are made on the console. For continuation requests, you might also want to specify a progress printer to let Regressr know how you want your continuations to be printed on the console. This helps in debugging complex continuations.

## License

Apache 2.0. License
