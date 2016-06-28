package temp;

public interface FieldTransformer extends InsertTransformer {
	
	@Override public String transform(Object value);
	
}
