package fr.an.tests.spark.sql;

import java.util.ArrayList;
import java.util.List;

public class Exo3 {

	public static void main(String[] args) {
		Model model = new Model();
		
		View1 view1 = new View1();
		view1.setModel(model);
		
		View2 view2 = new View2();
		view2.setModel(model);
		
		model.publishChange();
		System.out.println();
	}
	public static interface Listener {
		public void onModelChange(Model m);
	}
	
	public static class Model {
		List<Listener> listeners = new ArrayList<>();
		public void addListener(Listener l) {
			this.listeners.add(l);
		}
		public void publishChange() {
			for(Listener l : listeners) {
				l.onModelChange(this);
			}
		}
	}

	
	public static abstract class View {
		protected Model model;

	}

	public static class View1 extends View implements Listener {

		@Override
		public void onModelChange(Model m) {
			System.out.println("View1 refresh");
		}

		public void setModel(Model model) {
			this.model = model;
			this.model.addListener(this);
		}
		
	}

	public static class View2 extends View {
		private Listener innerListener = (m) -> {
			System.out.println("View2 refresh");
		};

		public void setModel(Model model) {
			this.model = model;
			this.model.addListener(innerListener);
		}

	}

}
