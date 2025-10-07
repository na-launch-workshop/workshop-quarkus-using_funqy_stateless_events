package org.workshop;

import io.quarkus.funqy.Context;
import io.quarkus.funqy.Funq;
import io.quarkus.funqy.knative.events.CloudEvent;
import io.quarkus.funqy.knative.events.CloudEventMapping;
import org.jboss.logging.Logger;

import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

public class WorkshopTransactionChain {
    private static final Logger log = Logger.getLogger(WorkshopTransactionChain.class);

    @Inject
    @Channel("<username>-transactions-completed")
    Emitter<Transaction> transactionEmitter;

    /**
     * Step 1: validateTransaction
     * Expects knative event of type "defaultChain".
     * Emits event of type "fraudCheck".
     */
    @Funq
    @CloudEventMapping(trigger = "defaultChain", responseSource = "validation", responseType = "fraudCheck")
    public Transaction validateTransaction(Transaction tx) {
        log.infof("*** Validation Step *** Transaction %s from customer %s for %.2f %s",
                tx.transactionId, tx.customerId, tx.amount, tx.currency);

        // Mock external dependency
        log.info("Connecting to Customer Profile Service... ✅ Customer account is active");

        tx.status = "VALIDATED";
        tx.message = "Transaction validated successfully";
        return tx;
    }

    /**
     * Step 2: fraudCheck
     * Triggered by defaultChain output (fraudCheck).
     * Emits event of type "annotated".
     */
    @Funq
    @CloudEventMapping(trigger = "fraudCheck", responseSource = "fraud", responseType = "annotated")
    public Transaction fraudCheck(Transaction tx) {
        log.infof("*** Fraud Check Step *** Transaction %s for customer %s",
                tx.transactionId, tx.customerId);

        // Mock external dependency
        log.info("Connecting to Fraud Detection System... ⚡ Running rules and ML models...");

        // Mock: simple fraud check
        if (tx.amount > 10000) {
            tx.status = "FLAGGED";
            tx.message = "Transaction flagged for manual review";
        } else {
            tx.status = "CLEARED";
            tx.message = "Transaction cleared by fraud system";
        }
        return tx;
    }

    /**
     * Step 3: processPayment
     * Triggered by fraudCheck output (annotated).
     * Emits event of type "lastChainLink".
     */
    @Funq
    @CloudEventMapping(trigger = "annotated", responseSource = "payments", responseType = "lastChainLink")
    public Transaction processPayment(Transaction tx) {
        log.infof("*** Payment Step *** Processing payment for %s", tx.transactionId);

        // Mock external dependency
        log.info("Connecting to Core Banking API... 💳 Debit from account...");
        log.info("Connecting to Payment Gateway (VISA/MasterCard) ... ✅ Payment confirmed");

        tx.status = "PROCESSED";
        tx.message = "Payment successfully processed";
        return tx;
    }

    /**
     * Step 4: settleTransaction
     * Triggered by processPayment output (lastChainLink).
     */
    @Funq
    @CloudEventMapping(trigger = "lastChainLink")
    public void settleTransaction(Transaction tx, @Context CloudEvent event) {
        log.infof("*** Settlement Step *** Finalizing transaction %s", tx.transactionId);

        // Mock external dependency
        log.info("Connecting to Ledger Service... 🧾 Recording transaction...");
        log.info("Connecting to Notification Service... 📲 Sending confirmation to customer...");

        // publish to Kafka
        transactionEmitter.send(tx);

        log.infof("Transaction %s for customer %s completed with status: %s (%s)",
                tx.transactionId, tx.customerId, tx.status, tx.message);
    }

    // Simple DTO to carry transaction data
    public static class Transaction {
        public String transactionId;
        public String customerId;
        public double amount;
        public String currency;
        public String status;
        public String message;
    }
}
