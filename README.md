Java Web Server
===============

A simple web servlet container, capable of routing HTTP GET requests to any number of simple servlets.

Developed and tested on Java 1.8, using Maven 3.2.5.

To run tests, simply run `mvn test`. Coverage data will be output to target/coverage-report. To build, run `mvn package`. The created jar can be run to start the server on local port 8000. Files will be served directly from the executing directory's /files subdirectory for requests starting with /files. Normally this root would be handled via config or command line, and also set up to prevent traversing outside the designated directory, but I left it relative for this example to keep it simple for anyone to run (normally the execution port would be similarly configurable). Requests to paths starting with /echo will respond with some simple data about the request, including the value of a query string parameter named "echo" if provided.

All requests are treated as GET no matter what HTTP method is actually sent, since that is all that is supported; supporting other methods would, of course, necessitate actually parsing and validating the sent HTTP method.

Simple statistics are tracked separately for each servlet, including total requests, current active requests, and average response time. Each request to a given servlet will output that servlet's to the server's STDOUT. This could easily have been logged to disk instead, or written to a database, but was again kept simple for ease of running the app.

Routing to servlets is also very simple; the route mapping is done via the start of the path, e.g. creating a route for "/foo" will match requests to "/foo", "/foo/", "/foo/bar", "/foo?baz=qux", etc. If multiple routes match, the _last_ match in the routing table is used (.e.g if there is first a route for "/foo" to servlet Alpha, and later in the list "/foo/bar" to servlet Beta, a request is made to "/foo/bar/baz", it will be routed to Beta).
