# 🚀 **Module: Creating and Deploying Your First Serverless Function**

**Technology Stack:**

- Knative Eventing
- Red Hat Serverless
- Quarkus Funqy
- Kafka

---

## 🎯 **Scenario**

Company A is sending a payment transaction (credit card) to ACME. ACME has 4 business steps that need to happen for the transaction to be a success.

1. Customer Profile Service System  |   To check if the customer is a valid customer
2. Fraud Detection System           |   We need to verify the transaction is not fraudulent
3. Payment Processing System        |   If the transaction is legitiate, we want to sent it to payments
4. If the payment is successful 
      Ledger Service                |   We want to capture it as successful
      Notification Service          |   And notify the customer.

Our current system has a workflow engine, it is stateful. We want to move it to a process that can be distributed, stateless and fully event driven. 

---

## 🧩 **Challenge**

- Write your own Funqy event
- Annotate it in the correct part of your workflow
- Wire it up to a Knative event
- Verify the result in Kafka

---

## 🔄 Transaction Flow Breakdown

### **1. 🔹 Validate Customer (validateTransaction)**
- Triggered by an external Knative CloudEvent (`defaultChain`), e.g., from a curl call.
- Mocks a call to a Customer Profile Service (verifies customer account is active).
- Marks transaction as **VALIDATED**.
- Returns a CloudEvent of type `fraudCheck`.

###### ✨ Example: Creating a CloudEvent | Default Chain

```bash
NS=$(oc project -q)

oc exec -it curler -n "$NS" -- curl -v \
  "http://broker-ingress.knative-eventing.svc.cluster.local/${NS}/default" \
  -H "Ce-Id: txn-001" \
  -H "Ce-Specversion: 1.0" \
  -H "Ce-Type: defaultChain" \
  -H "Ce-Source: bash" \
  -H "Content-Type: application/json" \
  -d '{"transactionId":"txn-1001","customerId":"42","amount":12500,"currency":"JPY"}'
```

---

### **2. 🔸 Fraud Check (fraudCheck)**
- Triggered by the `fraudCheck` function (annotation-based mapping).
- Mocks a Fraud Detection System (rules + ML).
- Business rule:
  - If amount > 10,000 → **FLAGGED** for manual review.
  - Otherwise → **CLEARED**.
  - Emits a CloudEvent of type `annotated` to trigger the processPayment step.

###### ✨ Example: Creating a CloudEvent | Fraud Check

```bash
NS=$(oc project -q)

oc exec -it curler -n "$NS" -- curl -v \
  "http://broker-ingress.knative-eventing.svc.cluster.local/${NS}/default" \
  -H "Ce-Id: txn-002" \
  -H "Ce-Specversion: 1.0" \
  -H "Ce-Type: fraudCheck" \
  -H "Ce-Source: bash" \
  -H "Content-Type: application/json" \
  -d '{"transactionId":"txn-1002","customerId":"57","amount":5000,"currency":"USD"}'
```

---

### **3. 🔹 Payment Processing (processPayment)**
- Triggered by the `processPayment` function (annotation-based mapping).
- Mocks:
  - Core Banking API (debit customer account).
  - Payment Gateway (Visa/MasterCard).
- Marks transaction as **PROCESSED**.
- Returns a CloudEvent of type `lastChainLink`.

###### ✨ Example: Creating a CloudEvent | Payment Processing

```bash
NS=$(oc project -q)

oc exec -it curler -n "$NS" -- curl -v \
  "http://broker-ingress.knative-eventing.svc.cluster.local/${NS}/default" \
  -H "Ce-Id: txn-003" \
  -H "Ce-Specversion: 1.0" \
  -H "Ce-Type: annotated" \
  -H "Ce-Source: bash" \
  -H "Content-Type: application/json" \
  -d '{"transactionId":"txn-1003","customerId":"99","amount":2000,"currency":"EUR"}'
```

---

### **4. 🔸 Settlement & Kafka Publish (settleTransaction)**
- Triggered by `settleTransaction` function.
- Mocks:
  - Ledger Service (recording transaction).
  - Notification Service (alerting customer).
- Publishes the finalized transaction to Kafka using

```bash
NS=$(oc project -q)

oc exec -it curler -n "$NS" -- curl -v \
  "http://broker-ingress.knative-eventing.svc.cluster.local/${NS}/default" \
  -H "Ce-Id: txn-004" \
  -H "Ce-Specversion: 1.0" \
  -H "Ce-Type: lastChainLink" \
  -H "Ce-Source: bash" \
  -H "Content-Type: application/json" \
  -d '{"transactionId":"txn-1004","customerId":"88","amount":7500,"currency":"GBP","status":"PROCESSED","message":"Payment successfully processed"}'
```

---

## ✅ Key Takeaways

- You have created a Funqy Event in Quarkus
- You created a Knative Event that can respond to triggers and trigger itself
- See how quickly Quarkus can build Serverless Events
