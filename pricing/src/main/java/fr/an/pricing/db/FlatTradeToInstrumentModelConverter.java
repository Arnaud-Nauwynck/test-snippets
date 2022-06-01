package fr.an.pricing.db;


import org.springframework.stereotype.Component;

import fr.an.pricing.model.Instrument;
import fr.an.pricing.model.Instrument.CurrencyInstrument;
import fr.an.pricing.model.Instrument.ScaledInstrument;
import fr.an.pricing.model.Instrument.SimpleEuropeanOptionInstrument;
import lombok.val;

@Component
public class FlatTradeToInstrumentModelConverter {

	public Instrument toInstrument(FlatTradeEntity src) {
		String type = src.getType();
		if ("FxCallEuro".equals(type)) {
			val currency = CurrencyInstrument.of(src.getCurrency());
			return SimpleEuropeanOptionInstrument.builder()
					.currency(currency)
					.payoffType("CallEuro")
					.strike(src.getStrike())
					.expiryDate(src.getExpiryDate())
					.underlying(new ScaledInstrument(src.getQuantity(),
							CurrencyInstrument.of(src.getUnderlying()))
							)
					.build();
		} else {
			
			throw new UnsupportedOperationException();
		}
	}
}
