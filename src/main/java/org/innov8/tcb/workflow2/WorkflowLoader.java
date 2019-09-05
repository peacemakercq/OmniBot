package org.innov8.tcb.workflow2;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class WorkflowLoader {

    /**
     * Workflow name - Workflow
     */
    private Map<String, Workflow> workflows = new HashMap<>();

    public Map<String, Workflow> getWorkflows() {
        return ImmutableMap.copyOf(workflows);
    }

    @PostConstruct
    public void loadWorkflows() throws IOException {
        String workflowPath = getClass()
                .getClassLoader()
                .getResource("workflows").getFile();
        File workflowDirectory = new File(workflowPath);

        for (File file : workflowDirectory.listFiles()) {
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            Map<String, List<Step>> fileMap = mapper.readValue(
                    file,
                    new TypeReference<Map<String, List<Step>>>() {});
            List<Step> steps = fileMap.get("steps");
            Workflow workflow = Workflow.from(steps);
            workflows.put(FilenameUtils.getBaseName(file.getName()), workflow);
        }
    }
}
