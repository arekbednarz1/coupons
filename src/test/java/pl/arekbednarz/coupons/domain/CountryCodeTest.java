package pl.arekbednarz.coupons.domain;

import org.junit.jupiter.api.Test;
import pl.arekbednarz.coupons.domain.model.value.CountryCode;

import static org.assertj.core.api.Assertions.*;


class CountryCodeTest {

	@Test
	void shouldCreateValidCountryCode() {
		CountryCode cc = CountryCode.of("pl");

		assertThat(cc.value()).isEqualTo("PL");
		assertThat(cc.toString()).isEqualTo("PL");
	}

	@Test
	void shouldTrimAndUppercaseInput() {
		CountryCode cc = CountryCode.of(" de ");

		assertThat(cc.value()).isEqualTo("DE");
	}

	@Test
	void shouldRejectNull() {
		assertThatThrownBy(() -> CountryCode.of(null))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("Country code must not be blank");
	}

	@Test
	void shouldRejectBlank() {
		assertThatThrownBy(() -> CountryCode.of("   "))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("Country code must not be blank");
	}

	@Test
	void shouldRejectInvalidLength() {
		assertThatThrownBy(() -> CountryCode.of("POL"))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("Country code must be 2 letters");

		assertThatThrownBy(() -> CountryCode.of("P"))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("Country code must be 2 letters");
	}

	@Test
	void shouldImplementEqualsAndHashCodeCorrectly() {
		CountryCode a = CountryCode.of("PL");
		CountryCode b = CountryCode.of("pl");

		assertThat(a).isEqualTo(b);
		assertThat(a.hashCode()).isEqualTo(b.hashCode());
	}

	@Test
	void shouldNotBeEqualToDifferentCountry() {
		CountryCode a = CountryCode.of("PL");
		CountryCode b = CountryCode.of("DE");

		assertThat(a).isNotEqualTo(b);
	}
}
