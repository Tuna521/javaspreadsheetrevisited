package spreadsheet;

import common.api.BasicSpreadsheet;
import common.api.CellLocation;
import common.api.Expression;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Spreadsheet implements BasicSpreadsheet {
  //
  // start replacing
  //

  /**
   * Construct an empty spreadsheet.
   *
   * <p>DO NOT CHANGE THE SIGNATURE. The test suite depends on this.
   */
  private CycleDetector cycleDetector;
  private Map<CellLocation, Cell> enviroment;

  Spreadsheet() {
    this.cycleDetector = new CycleDetector(this);
    this.enviroment = new HashMap<>();
  }

  /**
   * Parse and evaluate an expression, using the spreadsheet as a context.
   *
   * <p>DO NOT CHANGE THE SIGNATURE. The test suite depends on this.
   */
  public double evaluateExpression(String expression) throws InvalidSyntaxException {
    Expression exp = Parser.parse(expression);
    return exp.evaluate(this);
  }

  /**
   * Assign an expression to a cell.
   *
   * <p>DO NOT CHANGE THE SIGNATURE. The test suite depends on this.
   */
  public void setCellExpression(CellLocation location, String input) throws InvalidSyntaxException {
    Cell cell = getCell(location);
    String pastExpression = cell.getExpression();
    cell.setExpression(input);
    if (cycleDetector.hasCycleFrom(location)) {
      cell.setExpression(pastExpression);
    }
    cell.recalculate();
    enviroment.put(location, cell);
  }

  @Override
  public double getCellValue(CellLocation location) {
    return getCell(location).getValue();
  }

  public Cell getCell(CellLocation location) {
    return enviroment.computeIfAbsent(location, l -> new Cell(this, location));
  }
  //
  // end replacing
  //

  @Override
  public String getCellExpression(CellLocation location) {
    Cell cell = getCell(location);
    cell.recalculate();
    return cell.getExpression();
  }

  @Override
  public String getCellDisplay(CellLocation location) {
    return getCell(location).toString();
  }

  @Override
  public void addDependency(CellLocation dependent, CellLocation dependency) {
    getCell(dependency).addDependent(dependent);
  }

  @Override
  public void removeDependency(CellLocation dependent, CellLocation dependency) {
    getCell(dependency).removeDependent(dependent);
  }

  @Override
  public void recalculate(CellLocation location) {
    getCell(location).recalculate();
  }

  @Override
  public void findCellReferences(CellLocation subject, Set<CellLocation> target) {
    getCell(subject).findCellReferences(target);
  }
}
