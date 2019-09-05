package org.innov8.tcb.workflow2;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class Workflow {

    /**
     * the entrance step
     */
    private Step entranceStep;

    /**
     * the step can be used by Lex
     */
    private Step lexStep;

    /**
     * step name - Step
     */
    private Map<String, Step> steps;

    public static Workflow from(List<Step> steps) {
        Step entranceStep = steps.stream().filter(Step::isEntrance).findFirst().get();
        Step lexStep = steps.stream().filter(Step::isForLex).findFirst().get();
        Map<String, Step> stepMap = steps.stream().collect(Collectors.toMap(Step::getName, s -> s));
        return new Workflow(entranceStep, lexStep, stepMap);
    }
}
