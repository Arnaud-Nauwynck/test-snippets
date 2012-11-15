package fr.an.test.serverpool;

/**
 *
 */
public abstract class DataProcessor {

	public abstract String processData(String userName, String data);

	public abstract boolean isClosed();

	public abstract boolean pingAlive();

}
