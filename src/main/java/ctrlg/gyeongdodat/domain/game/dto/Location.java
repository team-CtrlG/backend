package ctrlg.gyeongdodat.domain.game.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Location implements Serializable {

    private final BigDecimal lat;
    private final BigDecimal lng;

    @JsonCreator
    public static Location of(
            @JsonProperty("lat") BigDecimal lat,
            @JsonProperty("lng") BigDecimal lng
    ) {
        return new Location(lat, lng);
    }
}
