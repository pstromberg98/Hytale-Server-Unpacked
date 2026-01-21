package META-INF.versions.9.org.bouncycastle.math.field;

import org.bouncycastle.math.field.ExtensionField;
import org.bouncycastle.math.field.Polynomial;

public interface PolynomialExtensionField extends ExtensionField {
  Polynomial getMinimalPolynomial();
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\META-INF\versions\9\org\bouncycastle\math\field\PolynomialExtensionField.class
 * Java compiler version: 9 (53.0)
 * JD-Core Version:       1.1.3
 */