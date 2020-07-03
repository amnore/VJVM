package com.mcwcapsule.VJVM.runtime.classdata.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class UTF8Constant extends Constant {
    private final String value;
}
