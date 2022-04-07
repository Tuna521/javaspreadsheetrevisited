package spreadsheet;

import common.api.CellLocation;
import common.api.EvaluationContext;
import common.api.Expression;
import java.util.Set;

public class BinaryOperator implements Expression {

  private Expression lhside;
  private Expression rhside;
  private Operator operator;

  public BinaryOperator(Operator operator, Expression lhside, Expression rhside) {
    this.operator = operator;
    this.lhside = lhside;
    this.rhside = rhside;
  }

  @Override
  public double evaluate(EvaluationContext context) {
    double evalLeft = lhside.evaluate(context);
    double evalRight = rhside.evaluate(context);
    switch (operator) {
      case ADDITION:
        return (evalLeft + evalRight);
      case SUBTRACTION:
        return (evalLeft - evalRight);
      case MULTIPLICATION:
        return (evalLeft * evalRight);
      case DIVISION:
        return (evalLeft / evalRight);
      case EXPONENTIATION:
        return (Math.pow(evalLeft, evalRight));
      case BIGGER:
        return ((evalLeft > evalRight) ? 1.0 : 0.0);
      case SMALLER:
        return ((evalLeft < evalRight) ? 1.0 : 0.0);
      default:
        throw new IllegalArgumentException();
    }
  }

  @Override
  public void findCellReferences(Set<CellLocation> dependencies) {
    lhside.findCellReferences(dependencies);
    rhside.findCellReferences(dependencies);
  }

  @Override
  public String toString() {
    String stringLeft = lhside.toString();
    String stringRight = rhside.toString();

    switch (operator) {
      case ADDITION:
        return "(" + stringLeft + " + " + stringRight + ")";
      case SUBTRACTION:
        return "(" + stringLeft + " - " + stringRight + ")";
      case MULTIPLICATION:
        return "(" + stringLeft + " * " + stringRight + ")";
      case DIVISION:
        return "(" + stringLeft + " / " + stringRight + ")";
      case EXPONENTIATION:
        return "(" + stringLeft + "^" + stringRight + ")";
      case BIGGER:
        return "(" + stringLeft + " > " + stringLeft + ")";
      case SMALLER:
        return "(" + stringLeft + " < " + stringLeft + ")";
      default:
        throw new IllegalArgumentException();
    }
  }

  public enum Operator {
    ADDITION(1, "Left"),
    SUBTRACTION(1, "Left"),
    MULTIPLICATION(2, "Left"),
    DIVISION(2, "Left"),
    EXPONENTIATION(3, "Right"),
    LPARENTHESIS(0, "Left"),
    RPARENTHESIS(0, "Right"),
    BIGGER(4, "Left"),
    SMALLER(4, "Left");

    private final Integer precedence;
    private final String associativity;

    private Operator(Integer precedence, String associativity) {
      this.precedence = precedence;
      this.associativity = associativity;
    }

    public Integer getPrecedence() {
      return precedence;
    }

    public String getAssociativity() {
      return associativity;
    }
  }
}
