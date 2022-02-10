package vjvm.runtime.object;

import vjvm.vm.VMContext;

import java.nio.charset.StandardCharsets;

public class StringObject extends JObject {
  public StringObject(String value, VMContext context) {
    super(context.bootstrapLoader().loadClass("Ljava/lang/String;"));

    var valueField = type().findField("value", "[B");
    var coderField = type().findField("coder", "B");
    var latin1Field = type().findField("LATIN1", "B");
    var array = new ArrayObject(
      context.bootstrapLoader().loadClass("[B"),
      value.getBytes(StandardCharsets.UTF_16BE)
    );

    data().int_(valueField.offset(), array.address());
    data().int_(coderField.offset(),
      type().staticFields().int_(latin1Field.offset()));
  }

  public String value() {
    var stringClass = type();
    var arrOffset = stringClass.findField("value", "[B").offset();
    var coderOffset = stringClass.findField("coder", "B").offset();
    var coderLatin1 = stringClass.findField("LATIN1", "B").offset();

    var arr = (ArrayObject) context().heap().get(data().address(arrOffset));
    var coder = data().address(coderOffset);
    var staticFields = type().staticFields();

    return new String(arr.value(), (coder == staticFields.int_(coderLatin1)
      ? StandardCharsets.US_ASCII : StandardCharsets.UTF_16BE));
  }
}
