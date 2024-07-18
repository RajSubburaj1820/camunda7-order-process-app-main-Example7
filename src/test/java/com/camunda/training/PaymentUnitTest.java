package com.camunda.training;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.*;

import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.camunda.community.process_test_coverage.junit5.platform7.ProcessEngineCoverageExtension;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(ProcessEngineCoverageExtension.class)
@Deployment(resources = {"Exercise7.bpmn"})
public class PaymentUnitTest {

  @Test
  public void happy_path_test() {
    ProcessInstance processInstance = runtimeService().startProcessInstanceByKey("PaymentProcessEx7",
        withVariables("orderTotal", 45.99, "customerCredit", 30.00));
    assertThat(processInstance).isWaitingAt(findId("Deduct customer credit")).externalTask()
        .hasTopicName("creditDeduction");

    complete(externalTask());

    assertThat(processInstance).isWaitingAt(findId("Charge credit card")).externalTask()
        .hasTopicName("creditCardCharging");

    complete(externalTask());


    assertThat(processInstance).isWaitingAt(findId("Payment completed")).externalTask()
    .hasTopicName("paymentCompletion");

    complete(externalTask());
  }


}
