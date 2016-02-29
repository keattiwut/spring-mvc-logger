# spring-mvc-logger
@Author Krzysztof Ziomek

## Purpose of this application is to exemplify:

1. Logging of request and response body in Spring MVC app
2. MDC (Mapped Diagnostic Context) for tracing requests by requestId
3. SLF4J over logback-spring (spring extention for logback)

Implementation is based on Spring Boot 1.3.2

## Run

    gradle bootRun

If you want redirect logs into the file, use profile 'prodenv' to run with dailyRollingFileAppender.
Logs will be redirected into file logs/spring-mvc-logger.log as configured in logback-spring.xml.

    gradle bootRun spring.active.profile=prodenv

## Logging of request and response with Spring MVC

I had a need to log full request and response for REST Service invocations.
I rummaged internet and Spring package to find appropriate filter or interceptor.
I found CommonRequestLoggingFilter Spring class which provides only basic logging of request. This class does not log body at all.
I haven't found logging class for response. 
It was surprising as I was working with Jersey before and hadn't have trouble to find full functional logger for request and response.
 
I decided to write new filter to fill the gap. MvcLoggingFilter is referential implementation of what I needed.
Filter is defined in SpringMvcLoggerApplication.
Filter implementation would be straightforward if not requirement to log the bodies of request and response.  Body is a stream so can be read only once.
The tricky solution is I had to create wrappers of HttpServletRequest and HttpServletResponse objects.
Wrappers override getInputStream() and getOutputStream() to copy stream into variable. As data are stored in variables they can be used more than once.

Request is buffered into byte[] and logged to file before MVC is invoked.
Response is buffered into ByteArrayOutputStream with TeeOutputStream when MVC writes response to output strem. 
Classes based in kziomek.filter.logging package are part of MvcLoggingFilter implementation. 

## MDC to trace requests by requestId
MDC is mechanism for easy tracing request by its id.

MDCFilter creates x-rid key-value pair and store it in context of current thread. MDC put value into Thread Local. 
x-rid contains unique value generated with UUID class.

x-rid - custom request id

## SLF4J over logback-spring - spring extention for logback
I configured SLF4J over logback as logging mechanism.
In everyday local development I prefer to log into console but in production it is necessary to log into file and rotate file everyday.

logback-spring extentions integrates with spring profile mechanism.
Profile default - logs into console
Profile prodenv - logs into spring-mvc-logger.log file in logs folder.


API
===

Invoke endpoints below and look at your logs to behold that
 1. x-rid is generated if not provided as request header
 2. You can filter logs by x-rid value to trace logs for only one specific invocation.

### GET http://localhost:8080/api/v1/hello
Response is String message.

### GET http://localhost:8080/api/v1/hello?name={name}
Response is String message.

### POST http://localhost:8080/api/v1/hello

Request body:

    {
        "name" : "Krzysztof Ziomek"
    }
    
Response is String message.



