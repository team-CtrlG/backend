package ctrlg.gyeongdodat.domain.game.service;

import com.github.f4b6a3.ulid.UlidCreator;
import ctrlg.gyeongdodat.domain.game.entity.GameRedis;
import ctrlg.gyeongdodat.domain.game.repository.GameRedisRepository;
import ctrlg.gyeongdodat.domain.game.service.command.GameCreateCommand;
import ctrlg.gyeongdodat.domain.game.service.command.GameUpdateCommand;
import ctrlg.gyeongdodat.global.exception.ErrorCode;
import ctrlg.gyeongdodat.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class GameRedisService {

    private final GameRedisRepository gameRedisRepository;
    private final Random random = new Random();

    public GameRedis create(GameCreateCommand command) {
        String id = UlidCreator.getUlid().toString();
        GameRedis game = command.toEntity(id);

        // 출석코드가 없으면 자동 생성
        if (game.getAttendanceCode() == null || game.getAttendanceCode().isEmpty()) {
            String attendanceCode = generateAttendanceCode();
            GameUpdateCommand updateCommand = GameUpdateCommand.builder()
                    .attendanceCode(attendanceCode)
                    .build();
            game.update(updateCommand);
        }

        return gameRedisRepository.save(game);
    }

    public String generateAttendanceCode() {
        int code = random.nextInt(10000);
        return String.format("%04d", code);
    }

    public GameRedis findById(String id) {
        return gameRedisRepository.findById(id)
                .orElseThrow(() -> new GlobalException(ErrorCode.GAME_NOT_FOUND));
    }

    public GameRedis findByIdOrNull(String id) {
        return gameRedisRepository.findById(id).orElse(null);
    }

    public List<GameRedis> findAll() {
        return StreamSupport.stream(gameRedisRepository.findAll().spliterator(), false)
                .toList();
    }

    public GameRedis update(String id, GameUpdateCommand command) {
        GameRedis game = findById(id);
        game.update(command);
        return gameRedisRepository.save(game);
    }

    public void delete(String id) {
        if (!gameRedisRepository.existsById(id)) {
            throw new GlobalException(ErrorCode.GAME_NOT_FOUND);
        }
        gameRedisRepository.deleteById(id);
    }
}
