package META-INF.versions.9.org.bouncycastle.math.field;

import org.bouncycastle.math.field.FiniteField;

public interface ExtensionField extends FiniteField {
  FiniteField getSubfield();
  
  int getDegree();
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\META-INF\versions\9\org\bouncycastle\math\field\ExtensionField.class
 * Java compiler version: 9 (53.0)
 * JD-Core Version:       1.1.3
 */