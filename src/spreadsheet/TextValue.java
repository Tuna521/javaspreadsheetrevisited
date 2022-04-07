package spreadsheet;

import common.api.CellLocation;
import common.api.EvaluationContext;
import common.api.Expression;
import java.util.Set;

public class TextValue implements Expression {

  private String text;

  public TextValue(String text) {
    this.text = text;
  }
  @Override
  public double evaluate(EvaluationContext context) {
    return 0;
  }

  @Override
  public void findCellReferences(Set<CellLocation> dependencies) {
  }
}
