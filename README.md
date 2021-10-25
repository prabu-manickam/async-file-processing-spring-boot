# async-file-processing-spring-boot

This is an example for asynchronously process the big files with out getting gateway timeouts thought HTTP requests.

There are 2 endpoints exist in this logic,

1. File upload endpoint - will upload the file to the server and initiate the background process and return an unique tracking ID with other information.
2. Status notifier endpoint - will tell the intermediate/final status of the endpoint for the given identifier


Find the demo recordings in the `/demo` folder to understand visually how this works