package org.redhat;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
public class FunqyTest {

    @Test
    public void testDefaultChain() {
        given().contentType("application/json")
            .header("ce-specversion", "1.0")
            .header("ce-id", UUID.randomUUID().toString())
            .header("ce-type", "defaultChain")
            .header("ce-source", "test")
            .body("{\"transactionId\":\"txn-001\",\"customerId\":\"12345\",\"amount\":2500,\"currency\":\"USD\"}")
            .post("/")
            .then().statusCode(200)
            .header("ce-id", notNullValue())
            .header("ce-type", "fraudCheck")
            .header("ce-source", "validation")
            .body("status", equalTo("VALIDATED"))
            .body("message", equalTo("Transaction validated successfully"));
    }

    @Test
    public void testFraudCheckCleared() {
        given().contentType("application/json")
            .header("ce-specversion", "1.0")
            .header("ce-id", UUID.randomUUID().toString())
            .header("ce-type", "fraudCheck")
            .header("ce-source", "test")
            .body("{\"transactionId\":\"txn-002\",\"customerId\":\"54321\",\"amount\":5000,\"currency\":\"USD\",\"status\":\"VALIDATED\",\"message\":\"Transaction validated successfully\"}")
            .post("/")
            .then().statusCode(200)
            .header("ce-id", notNullValue())
            .header("ce-type", "annotated")
            .header("ce-source", "fraud")
            .body("status", equalTo("CLEARED"))
            .body("message", equalTo("Transaction cleared by fraud system"));
    }

    @Test
    public void testFraudCheckFlagged() {
        given().contentType("application/json")
            .header("ce-specversion", "1.0")
            .header("ce-id", UUID.randomUUID().toString())
            .header("ce-type", "fraudCheck")
            .header("ce-source", "test")
            .body("{\"transactionId\":\"txn-003\",\"customerId\":\"11111\",\"amount\":20000,\"currency\":\"USD\",\"status\":\"VALIDATED\",\"message\":\"Transaction validated successfully\"}")
            .post("/")
            .then().statusCode(200)
            .header("ce-id", notNullValue())
            .header("ce-type", "annotated")
            .header("ce-source", "fraud")
            .body("status", equalTo("FLAGGED"))
            .body("message", equalTo("Transaction flagged for manual review"));
    }

    @Test
    public void testAnnotatedChain() {
        given().contentType("application/json")
            .header("ce-specversion", "1.0")
            .header("ce-id", UUID.randomUUID().toString())
            .header("ce-type", "annotated")
            .header("ce-source", "test")
            .body("{\"transactionId\":\"txn-004\",\"customerId\":\"22222\",\"amount\":3000,\"currency\":\"USD\",\"status\":\"CLEARED\",\"message\":\"Transaction cleared by fraud system\"}")
            .post("/")
            .then().statusCode(200)
            .header("ce-id", notNullValue())
            .header("ce-type", "lastChainLink")
            .header("ce-source", "payments")
            .body("status", equalTo("PROCESSED"))
            .body("message", equalTo("Payment successfully processed"));
    }
}