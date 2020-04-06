package fr.an.test.ambarijpa.state;

/**
 * Represents the state of an alert.
 */
public enum AlertState {
  /**
   * Alert does not need to be distributed.  Normal Operation.
   */
  OK(0),

  /**
   * Alert indicates there may be an issue.  The component may be operating
   * normally but may be in danger of becoming <code>CRITICAL</code>.
   */
  WARNING(2),

  /**
   * Indicates there is a critical situation that needs to be addressed.
   */
  CRITICAL(3),

  /**
   * The state of the alert is not known.
   */
  UNKNOWN(1),

  /**
   * Indicates that the state of the alert should be discarded, but the alert
   * timestamps should be updated so that it is not considered stale.
   */
  SKIPPED(4);

  private final int intValue;

  public int getIntValue() {
    return intValue;
  }

  AlertState(int i) {
    this.intValue = i;
  }
}
