package temp;

public class UpperTransformer implements FieldTransformer, ValueTransformer {

	@Override
	public String transform(Object value) {
		if (value != null) {
			return value.toString().toUpperCase();
		}
		
		return null;
	}

}
