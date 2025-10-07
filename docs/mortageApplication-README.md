# mortgage application Workflow

This workflow demonstrates a real-world event-driven process modeled with **Quarkus Funqy Knative Events**.

## Diagram
```
┌──────────────────────────────┐
│ CloudEvent arrives           │
│ ce-type = submitApplication  │
└──────────┬───────────────────┘
           │ invokes
           ▼
┌──────────────────────────────┐
│ collectDetails(App a)        │
│ - Capture applicant info     │
│ - Upload docs                │
└──────────┬───────────────────┘
           │ emits
           ▼
┌────────────────────────────────────────────┐
│ CloudEvent emitted                         │
│ ce-type = detailsCollected                 │
│ ce-source = collectDetails                 │
└──────────┬─────────────────────────────────┘
           ▼
┌──────────────────────────────┐
│ creditCheck(App a)           │
│ - Run credit bureau check    │
│ - Compute score              │
└──────────┬───────────────────┘
           │ emits
           ▼
┌────────────────────────────────────────────┐
│ CloudEvent emitted                         │
│ ce-type = creditChecked                    │
│ ce-source = creditCheckMapping             │
└──────────┬─────────────────────────────────┘
           │ matches @CloudEventMapping(trigger="creditChecked")
           ▼
┌──────────────────────────────┐
│ underwriting(App a)          │
│ - Risk models                │
│ - Approve/reject conditions  │
└──────────┬───────────────────┘
           │ emits
           ▼
┌────────────────────────────────────────────┐
│ CloudEvent emitted                         │
│ ce-type = underwritten                     │
│ ce-source = underwriting                   │
└──────────┬─────────────────────────────────┘
           │ triggers final handler
           ▼
┌──────────────────────────────┐
│ finalizeLoan(App a)          │
│ - Approve loan + rate        │
│ - Notify applicant           │
└──────────────────────────────┘
```

## Event Flow
- Starts with a CloudEvent of type specific to this workflow.
- Events flow step by step, emitting new CloudEvents at each stage.
- Others use **@CloudEventMapping annotations** to route based on ce-type.

## Expected Output
Each step enriches the object with new status information until the final step completes the workflow.
