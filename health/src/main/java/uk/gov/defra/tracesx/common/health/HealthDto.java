package uk.gov.defra.tracesx.common.health;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.actuate.health.Status;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HealthDto {
    private Status status;
}
