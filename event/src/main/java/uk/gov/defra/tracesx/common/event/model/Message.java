package uk.gov.defra.tracesx.common.event.model;

import static uk.gov.defra.tracesx.common.event.util.Constants.STATIC_IP;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Message {

  @NotNull
  @Length(max = 80)
  private String user;

  @NotNull
  @Length(max = 80)
  private String sessionId;

  @NotNull
  @Length(max = 80)
  private String dateTime;

  @NotNull
  private Application application;

  @NotNull
  private Component component;

  @NotNull
  @Length(max = 80)
  private String ip;

  @NotNull
  private PmcCode pmcCode;

  @NotNull
  private Priority priority;

  @Valid
  @NotNull
  private Details details;

  @NotNull
  private String environment;

  public static Message.MessageBuilder getDefaultMessageBuilder() {
    return Message.builder()
        .dateTime(LocalDateTime.now().toString())
        .application(Application.IPAFFS)
        .priority(Priority.INFO)
        .ip(STATIC_IP);
  }
}
