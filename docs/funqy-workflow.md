# Module 4: Creating and Deploying Your First Serverless Function

## Funqy Workflow Examples

Welcome to the **Funqy Workflow Examples** for this workshop!  
These scenarios show how **Quarkus Funqy** and **Knative Events** can be used to chain functions together in real-world business processes. Each example highlights how a CloudEvent flows from one step to the next, either through **config mappings** or `@CloudEventMapping` annotations.

Use these examples as inspiration to design, extend, or adapt workflows for your own domain.

---

## Available Workflows

- [✈️ Baggage Handling (Airline)](./baggage-README.md)  
  From check-in to final delivery, model how baggage is processed, scanned, and loaded.

- [👩‍💻 Employee Onboarding](./employeeOnoboarding-README.md)  
  Automate collecting documents, provisioning accounts, and scheduling orientation.

- [📦 Package Delivery](./packageDelivery-README.md)  
  A “Deliver My Package” scenario covering warehouse prep, courier assignment, tracking, and delivery.

- [🚗 Ride Matching (Uber-style)](./rideMatching-README.md)  
  Match passengers with drivers, confirm rides, track trips, and complete payment.

- [🛒 Supermarket Layaway](./supermarketLayaway-README.md)  
  Reserve items, handle installment payments, and release goods upon completion.

- [🏥 Insurance Claim](./insuranceClaim-README.md)  
  Validate, assess, approve, and pay out insurance claims.

- [🏠 Mortgage Application](./mortageApplication-README.md)  
  Collect applicant info, run credit checks, underwrite risk, and finalize the loan.

---

## How to Use

Each README provides:
- **Diagram** → ASCII visualization of the CloudEvent flow.  
- **Step Descriptions** → What each function does.  
- **curl Examples** → Ready-to-use commands to trigger the chain.  
- **Quarkus Config** → Example `application.properties` settings.

👉 Start by picking a workflow above, follow the README, and send a CloudEvent to trigger the first step!
