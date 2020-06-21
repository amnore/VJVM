package com.mcwcapsule.VJVM.vm;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class VMOptions {
    @Builder.Default
    private final String bootstrapClassPath = "";
    @Builder.Default
    private final String userClassPath = ".";
    @Builder.Default
    private final int heapSize = 1024;
    private final String entryClass;
    private final String[] args;
}
