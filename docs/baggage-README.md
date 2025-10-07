# baggage handler Workflow

This workflow demonstrates a real-world event-driven process modeled with **Quarkus Funqy Knative Events**.

## Diagram
```
┌──────────────────────────────┐
│ CloudEvent arrives           │
│ ce-type = checkInBaggage     │
└──────────┬───────────────────┘
           │ invokes
           ▼
┌──────────────────────────────┐
│ checkInBaggage(Bag bag)      │
│ - Tag + Scan bag             │
│ - Assign destination         │
└──────────┬───────────────────┘
           │ emits
           ▼
┌───────────────────────────────────────────┐
│ CloudEvent emitted                        │
│ ce-type = checkInBaggage.output           │
│ ce-source = checkInBaggage                │
└──────────┬────────────────────────────────┘
           ▼
┌──────────────────────────────┐
│ securityScan(Bag bag)        │
│ - X-ray scan                 │
│ - Flag suspicious items      │
└──────────┬───────────────────┘
           │ emits
           ▼
┌───────────────────────────────────────────┐
│ CloudEvent emitted                        │
│ ce-type = scanned                         │
│ ce-source = securityMapping               │
└──────────┬────────────────────────────────┘
           │ matches @CloudEventMapping(trigger="scanned")
           ▼
┌──────────────────────────────┐
│ loadToAircraft(Bag bag)      │
│ - Sort bags to correct plane │
│ - Mark "Loaded"              │
└──────────┬───────────────────┘
           │ emits
           ▼
┌───────────────────────────────────────────┐
│ CloudEvent emitted                        │
│ ce-type = loaded                          │
│ ce-source = loadToAircraft                │
└──────────┬────────────────────────────────┘
           │ triggers final handler
           ▼
┌──────────────────────────────┐
│ finalDelivery(Bag bag)       │
│ - Update system "Bag onboard"│
│ - Notify passenger app       │
│ - End of chain               │
└──────────────────────────────┘
```

## Event Flow
- Starts with a CloudEvent of type specific to this workflow
- Events flow step by step, emitting new CloudEvents at each stage.
- Others use **@CloudEventMapping annotations** to route based on ce-type.

## Expected Output
Each step enriches the object with new status information until the final step completes the workflow.