package ctrlg.gyeongdodat.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

	private static final String WEBSOCKET_DOCS = """

			---

			## WebSocket API

			WebSocket 연결 URL: `ws://{host}/ws`

			### 클라이언트 → 서버

			| 엔드포인트 | 설명 | Payload |
			|-----------|------|---------|
			| `/app/game/player/{gamePlayerId}/move` | 플레이어 위치 전송 | `{ "lat": BigDecimal, "lng": BigDecimal }` |

			### 서버 → 클라이언트 (5초 주기 브로드캐스트)

			| 구독 채널 | 설명 | Payload |
			|----------|------|---------|
			| `/send/game/{gameId}/positions` | 전체 플레이어 위치 | `List<PlayerPositionResponse>` |
			| `/send/game/{gameId}/jail` | 수감된 플레이어 목록 | `List<GamePlayerRedis>` |

			### PlayerPositionResponse 구조
			```json
			{
			  "playerId": "string",
			  "team": "POLICE | THIEF",
			  "status": "ACTIVE | JAILED | UNKNOWN",
			  "lat": "BigDecimal",
			  "lng": "BigDecimal",
			  "thiefNumber": "Integer (도둑만)"
			}
			```
			""";

	@Bean
	public OpenAPI openAPI() {
		return new OpenAPI()
				.info(new Info()
						.title("경도닷 API")
						.description("경도닷 게임 서버 API 문서" + WEBSOCKET_DOCS)
						.version("v1.0.0"));
	}
}
