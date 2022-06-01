package fr.an.pricing.model;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;

public abstract class Instrument {

	@AllArgsConstructor
	public static class CurrencyInstrument extends Instrument {
		public final String name;
		
		public static CurrencyInstrument of(String name) {
			return new CurrencyInstrument(name);
		}
	}
	
	@AllArgsConstructor
	public static class ListedInstrument extends Instrument {
		public final String name;
	}

	@AllArgsConstructor
	public static class SumInstrument extends Instrument {
		public final Instrument left, right;
	}

	@AllArgsConstructor
	public static class ScaledInstrument extends Instrument {
		public final double quantity;
		public final Instrument underlying;
	}

	@AllArgsConstructor @Builder
	public static class ForwardInstrument extends Instrument {
		public final Date forwardExpiryDate;
		public final Instrument underlying;
		public final double forwardPrice;
	}

	@AllArgsConstructor @Builder
	public static class FutureInstrument extends Instrument {
		public final Date futureExpiryDate;
		public final Instrument underlying;
		public final String marketCutoff;
	}

	@AllArgsConstructor @Builder
	public static class SimpleEuropeanOptionInstrument extends Instrument {
		public final Date expiryDate;
		public final Instrument underlying;
		public final String payoffType;
		public final double strike;
		public final CurrencyInstrument currency;
		// public final String marketCutoff;
	}

}
