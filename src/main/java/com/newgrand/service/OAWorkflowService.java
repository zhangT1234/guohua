package com.newgrand.service;

import java.util.List;
import java.util.Map;

public interface OAWorkflowService {

    String createWorkflow(String requestName, String workflowId, Map<String, Object> mainData, List<Map<String, Object>> detailData, String userId);

}
