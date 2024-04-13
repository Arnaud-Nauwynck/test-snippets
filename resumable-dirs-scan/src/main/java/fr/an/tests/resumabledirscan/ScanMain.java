package fr.an.tests.resumabledirscan;

public class ScanMain {
	
	public static void main(String[] args) {
		new ScanMain().run();
	}

	private void run() {
		String rootDir = "c:/";
		LevelFiFoScanner scanner = new LevelFiFoScanner();
		scanner.startScanRoot(rootDir);
	}
}
