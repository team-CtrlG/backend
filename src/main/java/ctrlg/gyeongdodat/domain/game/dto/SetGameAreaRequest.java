package ctrlg.gyeongdodat.domain.game.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class SetGameAreaRequest {
    private List<Location> gameArea;
}
