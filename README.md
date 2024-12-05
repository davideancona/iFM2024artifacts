## Artifacts
### Available folders

- `rml-instrumentation`: most of the code used for the instrumentation
- `specifications`: the source code `.rml` of the specification together with the compiled version `.pl` used to generate the monitor
- `experiments`: tables with average response times (`response_times.csv`) and the total numbers of generated and monitored events (`count.csv`). These tables refer to the tests performed within the time frame of 15 minutes, as reported in Section 6 of the paper. The other files refer to tests on longer time frames (30m, 60m, and 90m) as mentioned in the paper. The event counts for such tests are limited to the haviest case of instrumentation level 2 with 11 publishers and 11 subscribers.
