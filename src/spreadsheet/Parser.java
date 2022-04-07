package spreadsheet;

import common.api.Expression;
import common.lexer.InvalidTokenException;
import common.lexer.Lexer;
import common.lexer.Token;
import java.util.EmptyStackException;
import java.util.Stack;
import spreadsheet.BinaryOperator.Operator;

public class Parser {

  /**
   * Parse a string into an Expression.
   *
   * <p>DO NOT CHANGE THE SIGNATURE. The test suite depends on this.
   */
  private static void binaryOperator(
      Operator curOperator, Stack<Operator> operatorStack, Stack<Expression> operandStack)
      throws EmptyStackException {
    if (operatorStack.empty() || curOperator == Operator.LPARENTHESIS) {
      operatorStack.push(curOperator);
    } else if ((operatorStack.peek().getPrecedence() > curOperator.getPrecedence()
        || (operatorStack.peek().getPrecedence() == curOperator.getPrecedence()
        && operatorStack.peek().getAssociativity() == "Left"))) {
      Operator topOperator = operatorStack.pop();
      if (topOperator != Operator.LPARENTHESIS && topOperator != Operator.RPARENTHESIS) {
        Expression rhside = operandStack.pop();
        Expression lhside = operandStack.pop();
        operandStack.push(new BinaryOperator(topOperator, lhside, rhside));
        binaryOperator(curOperator, operatorStack, operandStack);
      } else if (topOperator == Operator.RPARENTHESIS) {
        binaryOperator(curOperator, operatorStack, operandStack);
      }
    } else {
      operatorStack.push(curOperator);
    }
  }

  static Expression parse(String input) throws InvalidSyntaxException {

    Stack<Operator> operatorStack = new Stack<Operator>();
    Stack<Expression> operandStack = new Stack<Expression>();

    Lexer lexer = new Lexer(input);
    while (true) {
      Token token = null;
      try {
        token = lexer.nextToken();
      } catch (InvalidTokenException e) {
        throw new InvalidSyntaxException(e.getMessage());
      }
      if (token == null) {
        break;
      }

      switch (token.kind) {
        case NUMBER:
          operandStack.push(new Number(token.numberValue));
          break;
        case CELL_LOCATION:
          operandStack.push(new CellReference(token.cellLocationValue));
          break;
        case PLUS:
          binaryOperator(Operator.ADDITION, operatorStack, operandStack);
          break;
        case MINUS:
          binaryOperator(Operator.SUBTRACTION, operatorStack, operandStack);
          break;
        case STAR:
          binaryOperator(Operator.MULTIPLICATION, operatorStack, operandStack);
          break;
        case SLASH:
          binaryOperator(Operator.DIVISION, operatorStack, operandStack);
          break;
        case CARET:
          binaryOperator(Operator.EXPONENTIATION, operatorStack, operandStack);
          break;
        case LPARENTHESIS:
          binaryOperator(Operator.LPARENTHESIS, operatorStack, operandStack);
          break;
        case RPARENTHESIS:
          binaryOperator(Operator.RPARENTHESIS, operatorStack, operandStack);
          break;
        case LANGLE:
          binaryOperator(Operator.SMALLER, operatorStack, operandStack);
          break;
        case RANGLE:
          binaryOperator(Operator.BIGGER, operatorStack, operandStack);
          break;
        case EQUALS:
          throw new UnsupportedOperationException("Equals not yet implemented");
        default:
          throw new InvalidSyntaxException("No such syntax exist");
      }
    }

    while (!operatorStack.empty()) {
      Expression rhside = operandStack.pop();
      Expression lhside = operandStack.pop();
      Operator topOperator = operatorStack.pop();
      operandStack.push(new BinaryOperator(topOperator, lhside, rhside));
    }

    return operandStack.pop();
  }
}
