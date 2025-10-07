## ⚙️ Behind the Scenes

### 1. 🛠 Function Binding (Funqy + CloudEvents)
- Each method annotated with `@Funq` becomes a function endpoint that can be invoked.
- Quarkus Funqy Knative extension automatically exposes these functions as Knative event handlers.
- Events are passed as CloudEvents (a CNCF spec for structured event metadata).
- The mapping between input event types and functions is controlled by:
  - `@CloudEventMapping` annotations

👉 When `validateTransaction` returns a `Transaction`, Funqy wraps it into a CloudEvent with a new type (`fraudCheck`) so it can trigger the next function.

### 2. ☁️ Knative Eventing
- Knative Eventing acts as the event router inside Kubernetes/OpenShift.
- Behind the scenes:
  - Your function is running as a container (via Knative Service).
  - When it receives a CloudEvent (`defaultChain`), it processes it and responds with another CloudEvent (`fraudCheck`).
  - Knative Broker + Triggers deliver the output event to the correct next function (`fraudCheck`).
- Think of it as a pub/sub pipeline: each function emits a new event type, and Knative routes it.

### 3. 🔗 Chaining Events
- Each function’s output becomes the input event for the next function.
- Example flow:
  1. `validateTransaction` → emits `fraudCheck`
  2. Knative Trigger sees `fraudCheck` → routes to `fraudCheck` function
  3. `fraudCheck` → emits `annotated`
  4. Knative Trigger routes to `processPayment`
  5. `processPayment` → emits `lastChainLink`
  6. Knative Trigger routes to `settleTransaction`

👉 This chaining is event-driven — there is no direct Java method call between them. It’s all mediated by Knative Eventing.

### 4. 🎷 Kafka Integration

```java
@Channel("<username>-transactions-completed")
Emitter<Transaction> transactionEmitter;
```

- This comes from MicroProfile Reactive Messaging with the SmallRye Kafka connector.
- When you call `transactionEmitter.send(tx)`:
  - The `Transaction` is serialized (usually JSON).
  - A Kafka `ProducerRecord` is built and sent to the broker.
  - If `acks=all`, the producer waits until the broker confirms replication.
- If the topic doesn’t exist:
  - If `auto.create.topics.enable=true`, Kafka auto-creates it.
  - Otherwise, you get `UNKNOWN_TOPIC_OR_PARTITION`.

### 5. 🚀 Deployment on OpenShift/Knative
- Knative builds your Quarkus app into a container and deploys it as a Knative Service (auto-scalable).
- Each `@Funq` function is exposed via Knative Eventing → so it can receive events from the Broker.
- Your Kafka cluster (Strimzi/AMQ Streams) runs separately, managed by its operator.
- At the end, you’ve got:
  - Quarkus Funqy Functions = business logic steps.
  - Knative Eventing = event router between steps.
  - Kafka = durable log / message bus for final results.

## 🌟 Key Features
- ✅ Event chaining: Each step emits a CloudEvent that triggers the next function.
- ✅ Mocked external integrations: Profile service, fraud detection, core banking, payments, ledger, notifications.
- ✅ Kafka integration: The last step publishes the completed transaction to a Kafka topic (`<username>-transactions-completed`).
- ✅ DTO (`Transaction`): A simple data holder for `transactionId`, `customerId`, `amount`, `currency`, `status`, and `message`.