package vjvm.runtime.classdata.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class NameAndTypeConstant extends Constant {
    @Getter
    private final String name;
    @Getter
    private final String descriptor;
}
