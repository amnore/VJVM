package com.mcwcapsule.VJVM.runtime.metadata.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class UTF8Constant extends Constant {
    private final String value;
}
