# insurance claim Workflow

This workflow demonstrates a real-world event-driven process modeled with **Quarkus Funqy Knative Events**.

## Diagram
```
┌──────────────────────────────┐
│ CloudEvent arrives           │
│ ce-type = submitClaim        │
└──────────┬───────────────────┘
           │ invokes
           ▼
┌──────────────────────────────┐
│ validateClaim(Claim c)       │
│ - Check policy validity      │
│ - Verify documents           │
└──────────┬───────────────────┘
           │ emits
           ▼
┌────────────────────────────────────────────┐
│ CloudEvent emitted                         │
│ ce-type = claimValidated                   │
│ ce-source = validateClaim                  │
└──────────┬─────────────────────────────────┘
           ▼
┌──────────────────────────────┐
│ assessDamage(Claim c)        │
│ - Inspect damage evidence    │
│ - Estimate cost              │
└──────────┬───────────────────┘
           │ emits
           ▼
┌────────────────────────────────────────────┐
│ CloudEvent emitted                         │
│ ce-type = damageAssessed                   │
│ ce-source = damageAssessment               │
└──────────┬─────────────────────────────────┘
           │ matches @CloudEventMapping(trigger="damageAssessed")
           ▼
┌──────────────────────────────┐
│ approveOrReject(Claim c)     │
│ - Apply rules                │
│ - Approve or deny claim      │
└──────────┬───────────────────┘
           │ emits
           ▼
┌────────────────────────────────────────────┐
│ CloudEvent emitted                         │
│ ce-type = claimDecision                    │
│ ce-source = approveOrReject                │
└──────────┬─────────────────────────────────┘
           │ triggers final handler
           ▼
┌──────────────────────────────┐
│ payout(Claim c)              │
│ - Send funds to customer     │
│ - Notify claimant            │
└──────────────────────────────┘
```

## Event Flow
- Starts with a CloudEvent of type specific to this workflow.
- Events flow step by step, emitting new CloudEvents at each stage.
- Others use **@CloudEventMapping annotations** to route based on ce-type.

## Expected Output
Each step enriches the object with new status information until the final step completes the workflow.
