package fr.an.tests.helix.impl;

public enum MyState {
	/**
	 * Initial state of replica when starting up. Router should not send any request
	 * to replica in this state.
	 */
	OFFLINE,

	/**
	 * Bootstrap state is an intermediate state between OFFLINE and SLAVE. This
	 * state allows replica to do some bootstrap work like checking replication lag
	 * and catching up with peers. GET, DELETE, TTLUpdate can be routed to replica
	 * in this state.
	 */
	BOOTSTRAP,

	/**
	 * The state in which replica is fully functioning. That is, it can receive all
	 * types of requests.
	 */
	SLAVE,

	/**
	 * The state in which replica is fully functioning. For replicas from same
	 * partition in same DC, at most one is in MASTER state. Currently we don't
	 * distinguish MASTER from SLAVE and they both can receive all types of
	 * requests. In the future, we may leverage MASTER replica to perform cross-dc
	 * replication.
	 */
	MASTER,

	/**
	 * Inactive is an intermediate state between OFFLINE and SLAVE. This state
	 * allows replica to do some decommission work like making sure its peers have
	 * caught up with it. Only GET request is allowed to be routed to inactive
	 * replica.
	 */
	INACTIVE,

	/**
	 * When replica behaves unexpectedly and Helix makes replica in this state.
	 */
	ERROR,

	/**
	 * End state of a replica that is decommissioned.
	 */
	DROPPED
}
