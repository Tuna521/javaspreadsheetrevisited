package spreadsheet;

import common.api.BasicSpreadsheet;
import common.api.CellLocation;
import java.util.HashSet;
import java.util.Set;

/**
 * Detects dependency cycles.
 */
public class CycleDetector {

  private BasicSpreadsheet spreadsheet;

  /**
   * Constructs a new cycle detector.
   *
   * <p>DO NOT CHANGE THE SIGNATURE. The test suite depends on this.
   *
   * @param spreadsheet The parent spreadsheet, used for resolving cell locations.
   */
  CycleDetector(BasicSpreadsheet spreadsheet) {
    this.spreadsheet = spreadsheet;
  }

  /**
   * Checks for a cycle in the spreadsheet, starting at a particular cell.
   *
   * <p>DO NOT CHANGE THE SIGNATURE. The test suite depends on this.
   *
   * @param start The cell location where cycle detection should start.
   * @return Whether a cycle was detected in the dependency graph starting at the given cell.
   */
  public boolean hasCycleFrom(CellLocation start) {
    return hasCycleFromHelper(start, new HashSet<>());
  }

  private boolean hasCycleFromHelper(CellLocation current, Set<CellLocation> seen) {
    if (seen.contains(current)) {
      return true;
    }

    Set<CellLocation> subDependencies = new HashSet<>();
    spreadsheet.findCellReferences(current, subDependencies);

    for (CellLocation d : subDependencies) {
      Set<CellLocation> helper = new HashSet<>(seen);
      helper.add(current);
      if (this.hasCycleFromHelper(d, helper)) {
        return true;
      }
    }
    return false;
  }
}
