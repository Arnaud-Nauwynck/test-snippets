package fr.an.test.ambarijpa.state;

public enum HostRoleStatus {
  /**
   * Not queued for a host.
   */
  PENDING,

  /**
   * Queued for a host, or has already been sent to host, but host did not answer yet.
   */
  QUEUED,

  /**
   * Host reported it is working, received an IN_PROGRESS command status from host.
   */
  IN_PROGRESS,

  /**
   * Task is holding, waiting for command to proceed to completion.
   */
  HOLDING,

  /**
   * Host reported success
   */
  COMPLETED,

  /**
   * Failed
   */
  FAILED,

  /**
   * Task is holding after a failure, waiting for command to skip or retry.
   */
  HOLDING_FAILED,

  /**
   * Host did not respond in time
   */
  TIMEDOUT,

  /**
   * Task is holding after a time-out, waiting for command to skip or retry.
   */
  HOLDING_TIMEDOUT,

  /**
   * Operation was abandoned
   */
  ABORTED,

  /**
   * The operation failed and was automatically skipped.
   */
  SKIPPED_FAILED;
}
