# ride matching Workflow

This workflow demonstrates a real-world event-driven process modeled with **Quarkus Funqy Knative Events**.

## Diagram
```
┌──────────────────────────────┐
│ CloudEvent arrives           │
│ ce-type = requestRide        │
└──────────┬───────────────────┘
           │ invokes
           ▼
┌──────────────────────────────┐
│ matchDriver(Ride r)          │
│ - Find nearby driver         │
│ - Check availability         │
└──────────┬───────────────────┘
           │ emits
           ▼
┌────────────────────────────────────────────┐
│ CloudEvent emitted                         │
│ ce-type = driverMatched                    │
│ ce-source = matchDriver                    │
└──────────┬─────────────────────────────────┘
           │ matches @CloudEventMapping(trigger="driverMatched")
           ▼
┌──────────────────────────────┐
│ confirmRide(Ride r)          │
│ - Driver accepts ride        │
│ - Notify passenger           │
└──────────┬───────────────────┘
           │ emits
           ▼
┌────────────────────────────────────────────┐
│ CloudEvent emitted                         │
│ ce-type = confirmed                        │
│ ce-source = confirmRide                    │
└──────────┬─────────────────────────────────┘
           │ matches @CloudEventMapping(trigger="confirmed")
           ▼
┌──────────────────────────────┐
│ trackRide(Ride r)            │
│ - GPS tracking               │
│ - Update ETA continuously    │
└──────────┬───────────────────┘
           │ emits
           ▼
┌────────────────────────────────────────────┐
│ CloudEvent emitted                         │
│ ce-type = rideInProgress                   │
│ ce-source = trackRide                      │
└──────────┬─────────────────────────────────┘
           │ triggers final handler
           ▼
┌──────────────────────────────┐
│ completeRide(Ride r)         │
│ - Mark finished              │
│ - Process payment            │
└──────────────────────────────┘
```

## Event Flow
- Starts with a CloudEvent of type specific to this workflow.
- Events flow step by step, emitting new CloudEvents at each stage.
- Others use **@CloudEventMapping annotations** to route based on ce-type.

## Expected Output
Each step enriches the object with new status information until the final step completes the workflow.
