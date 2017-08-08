## Components

### Request Payload Builder

**FileHTTPBodyBuilder** - Takes a payloadFile as the input file path and uses the contents of that file as the payload.

Usage:
```
    requestBuilder: org.ebayopensource.regression.internal.components.requestBuilder.FileHTTPBodyBuilder
    dataInput:
      payload   :  '/tmp/payloadFile.txt'

```

**StringHTTPBuilder** - Takes a payload as the input and uses the contents of that string as the payload.

Usage:
```
    requestBuilder: org.ebayopensource.regression.internal.components.requestBuilder.StringHTTPBuilder
    dataInput:
      payload   :   '{"json":"value"}'

```

### Recorder

**SimpleHTTPJSONRecorder** - Records the request url, headers, response status code, request body and response body. Used for JSON based services.

Usage:
```
  - requestName: shop for a pair of shoes
    path: /shopping
    method: POST
    requestBuilder: org.ebayopensource.regression.example.ExampleRequestBuilder
    dataInput:
      conversationId     : 12345
      keyword            : Show me a pair of shoes
      mission_start      : yes
    recorder: org.ebayopensource.regression.internal.components.recorder.SimpleHTTPJSONRecorder
    comparator: org.ebayopensource.regression.internal.components.comparator.SimpleHTTPJsonComparator
```

### Comparator

**SimpleHTTPJsonComparator** - Compares simple string types and the structures of JSON (ignores differences in values)

Usage:

```
  - requestName: shop for a pair of shoes
    path: /shopping
    method: POST
    requestBuilder: org.ebayopensource.regression.example.ExampleRequestBuilder
    dataInput:
      conversationId     : 12345
      keyword            : Show me a pair of shoes
      mission_start      : yes
    recorder: org.ebayopensource.regression.internal.components.recorder.SimpleHTTPJSONRecorder
    comparator: org.ebayopensource.regression.internal.components.comparator.SimpleHTTPJsonComparator
```

**StrictHTTPJsonComparator** - Compares simple string types and compares JSON structurally and values. 

### Progress Printer

**URLProgressPrinter** - Prints the URL of every request that is made in a continuation request.

Usage:

```
  - requestName: traverse through HATEOAS service.
    path: /shopping/search/shoes
    method: GET
    continuation: org.ebayopensource.regression.example.HATEOASContinuation
    progressPrinter: org.ebayopensource.regression.internal.components.printer.URLProgressPrinter
    recorder: org.ebayopensource.regression.internal.components.recorder.SimpleHTTPJSONRecorder
    comparator: org.ebayopensource.regression.internal.components.comparator.StrictHTTPJsonComparator
```