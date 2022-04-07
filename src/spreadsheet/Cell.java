package spreadsheet;

import common.api.BasicSpreadsheet;
import common.api.CellLocation;
import common.api.Expression;
import java.util.HashSet;
import java.util.Set;

/**
 * A single cell in a spreadsheet, tracking the expression, value, and other parts of cell state.
 */
public class Cell {

  /**
   * Constructs a new cell.
   *
   * <p>DO NOT CHANGE THE SIGNATURE. The test suite depends on this.
   *
   * @param spreadsheet The parent spreadsheet,
   * @param location The location of this cell in the spreadsheet.
   */

  private Expression expression;
  private double value;
  private CellLocation location;
  private BasicSpreadsheet spreadsheet;
  private Set<CellLocation> dependents;

  Cell(BasicSpreadsheet spreadsheet, CellLocation location) {
    this.location = location;
    this.expression = null;
    this.value = 0;
    this.spreadsheet = spreadsheet;
    this.dependents = new HashSet<>();
  }

  /**
   * Gets the cell's last calculated value.
   *
   * <p>DO NOT CHANGE THE SIGNATURE. The test suite depends on this.
   *
   * @return the cell's value.
   */
  public double getValue() {
    return value;
  }

  /**
   * Gets the cell's last stored expression, in string form.
   *
   * <p>DO NOT CHANGE THE SIGNATURE. The test suite depends on this.
   *
   * @return a string that parses to an equivalent expression to that last stored in the cell;
   * if no expression is stored, we return the empty string.
   */
  public String getExpression() {
    if (expression == null) {
      return "";
    } else {
      return expression.toString();
    }
  }

  /**
   * Sets the cell's expression from a string.
   *
   * <p>DO NOT CHANGE THE SIGNATURE. The test suite depends on this.
   *
   * @param input The string representing the new cell expression.
   * @throws InvalidSyntaxException if the string cannot be parsed.
   */
  public void setExpression(String input) throws InvalidSyntaxException {
    if (input.isEmpty()) {
      Set<CellLocation> beforeDependencies = new HashSet<>();
      if (expression != null) {
        expression.findCellReferences(beforeDependencies);
      }
      beforeDependencies.stream().forEach(d -> spreadsheet.removeDependency(this.location, d));

      expression = null;
    } else {
      Set<CellLocation> beforeDependencies = new HashSet<>();
      if (expression != null) {
        expression.findCellReferences(beforeDependencies);
      }

      expression = Parser.parse(input);

      Set<CellLocation> afterDependencies = new HashSet<>();
      if (expression != null) {
        expression.findCellReferences(afterDependencies);
      }

      Set<CellLocation> toBeRemoved = new HashSet<>(beforeDependencies);
      toBeRemoved.removeAll(afterDependencies);
      toBeRemoved.stream().forEach(d -> spreadsheet.removeDependency(this.location, d));

      Set<CellLocation> toBeAdded = new HashSet<>(afterDependencies);
      toBeAdded.removeAll(beforeDependencies);
      toBeAdded.stream().forEach(d -> spreadsheet.addDependency(this.location, d));
    }
  }

  /**
   * @return a string representing the value, if any, of this cell.
   */
  @Override
  public String toString() {
    if (expression == null) {
      return "";
    } else {
      return String.valueOf(value);
    }
  }

  /**
   * Adds the given location to this cell's dependents.
   *
   * <p>DO NOT CHANGE THE SIGNATURE. The test suite depends on this.
   *
   * @param location the location to add.
   */
  public void addDependent(CellLocation location) {
    dependents.add(location);
  }

  /**
   * Adds the given location to this cell's dependents.
   *
   * <p>DO NOT CHANGE THE SIGNATURE. The test suite depends on this.
   *
   * @param location the location to add.
   */
  public void removeDependent(CellLocation location) {
    dependents.remove(location);
  }

  /**
   * Adds this cell's expression's references to a set.
   *
   * <p>DO NOT CHANGE THE SIGNATURE. The test suite depends on this.
   *
   * @param target The set that will receive the dependencies for this
   */
  public void findCellReferences(Set<CellLocation> target) {
    if (expression != null) {
      this.expression.findCellReferences(target);
    }
  }

  /**
   * Recalculates this cell's value based on its expression.
   *
   * <p>DO NOT CHANGE THE SIGNATURE. The test suite depends on this.
   */
  public void recalculate() {
    if (expression == null) {
      value = 0.0;
    } else {
      value = this.expression.evaluate(spreadsheet);
    }
    dependents.stream().forEach(d -> spreadsheet.recalculate(d));
  }
}
