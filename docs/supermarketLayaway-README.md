# supermarket layaway Workflow

This workflow demonstrates a real-world event-driven process modeled with **Quarkus Funqy Knative Events**.

## Diagram
```
┌──────────────────────────────┐
│ CloudEvent arrives           │
│ ce-type = startLayaway       │
└──────────┬───────────────────┘
           │ invokes
           ▼
┌──────────────────────────────┐
│ reserveItem(Order o)         │
│ - Mark inventory as held     │
│ - Assign reservation ID      │
└──────────┬───────────────────┘
           │ emits
           ▼
┌────────────────────────────────────────────┐
│ CloudEvent emitted                         │
│ ce-type = itemReserved                     │
│ ce-source = reserveItem                    │
└──────────┬─────────────────────────────────┘
           │
           ▼
┌──────────────────────────────┐
│ acceptDeposit(Order o)       │
│ - Record partial payment     │
│ - Issue receipt              │
└──────────┬───────────────────┘
           │ emits
           ▼
┌────────────────────────────────────────────┐
│ CloudEvent emitted                         │
│ ce-type = depositReceived                  │
│ ce-source = paymentMapping                 │
└──────────┬─────────────────────────────────┘
           │ matches @CloudEventMapping(trigger="depositReceived")
           ▼
┌──────────────────────────────┐
│ trackPayments(Order o)       │
│ - Monitor installments       │
│ - Send reminders             │
└──────────┬───────────────────┘
           │ emits
           ▼
┌────────────────────────────────────────────┐
│ CloudEvent emitted                         │
│ ce-type = paymentsUpdated                  │
│ ce-source = trackPayments                  │
└──────────┬─────────────────────────────────┘
           │ triggers final handler
           ▼
┌──────────────────────────────┐
│ releaseItem(Order o)         │
│ - Final payment complete     │
│ - Customer collects item     │
└──────────────────────────────┘
```

## Event Flow
- Starts with a CloudEvent of type specific to this workflow.
- Events flow step by step, emitting new CloudEvents at each stage.
- Others use **@CloudEventMapping annotations** to route based on ce-type.

## Expected Output
Each step enriches the object with new status information until the final step completes the workflow.
