## Configuration

A sample configuration file is included in the project under `application.yaml`.

> NOTE: there is currently a bug where the TOML parser used in Zeebe parses all numbers as doubles, which if passed
directly as `ProducerConfig` may cause errors. It's recommended for now to use the extra config arguments for
non-numerial values until that's fixed.

```yaml
- id: kafka
      className: io.zeebe.exporters.kafka.KafkaExporter
      args:
        maxInFlightRecords: 1000
        inFlightRecordCheckIntervalMs: 1000
        producer:
          servers: 'http://localhost:9092'
          requestTimeoutMs: 5000
          closeTimeoutMs: 5000
          clientId: zeebe
          maxConcurrentRequests: 3
          config: {}
        records:
          defaults:
            type: event
            topic: zeebe
          deployment:
            topic: zeebe-deployment
          incident:
            topic: zeebe-incident
          jobBatch:
            topic: zeebe-job-batch
          job:
            topic: zeebe-job
          message:
            topic: zeebe-message
          messageSubscription:
            topic: zeebe-message-subscription
          messageStartEventSubscription:
            topic: zeebe-message-subscription-start-event
          raft:
            topic: zeebe-raft
          timer:
            topic: zeebe-timer
          variable:
            topic: zeebe-variable
          workflowInstance:
            type: event
            topic: zeebe-workflow
          workflowInstanceSubscription:
            topic: zeebe-workflow-subscription
```
