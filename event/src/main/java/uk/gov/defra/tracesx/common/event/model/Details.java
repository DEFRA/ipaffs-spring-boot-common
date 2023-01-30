package uk.gov.defra.tracesx.common.event.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class Details {

  @NotNull
  private TransactionCode transactionCode;

  @NotNull
  @Length(max = 80)
  private String message;

  @Length(max = 80)
  @Builder.Default
  private String additionalInfo = "";

}
