package ecetin.digiwallet.hub.wallet.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.List;
import lombok.Getter;

@Getter
@Embeddable
public class Currency {

  @Column(name = "currency")
  private String value;

  protected Currency() {}

  public Currency(String value) {
    if (!List.of("TRY", "USD", "EUR").contains(value)) {
      throw new IllegalArgumentException("Unsupported currency");
    }
    this.value = value;
  }
}
