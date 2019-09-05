package org.innov8.tcb.workflow2;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class NextStep {
    @JsonProperty
    String name;
    @JsonProperty
    String condition;
}
