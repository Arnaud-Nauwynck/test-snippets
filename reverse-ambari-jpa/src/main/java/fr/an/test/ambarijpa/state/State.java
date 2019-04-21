package fr.an.test.ambarijpa.state;


public enum State {
  /**
   * Initial/Clean state.
   */
  INIT,
  /**
   * In the process of installing.
   */
  INSTALLING,
  /**
   * Install failed.
   */
  INSTALL_FAILED,
  /**
   * State when install completed successfully.
   */
  INSTALLED,
  /**
   * In the process of starting.
   */
  STARTING,
  /**
   * State when start completed successfully.
   */
  STARTED,
  /**
   * In the process of stopping.
   */
  STOPPING,
  /**
   * In the process of uninstalling.
   */
  UNINSTALLING,
  /**
   * State when uninstall completed successfully.
   */
  UNINSTALLED,
  /**
   * In the process of wiping out the install.
   */
  WIPING_OUT,
  /**
   * In the process of upgrading the host component deployed bits.
   * Valid only for host component state
   */
  UPGRADING,
  /**
   * Disabled master's backup state
   */
  DISABLED,
  /**
   * State could not be determined.
   */
  UNKNOWN;


}
