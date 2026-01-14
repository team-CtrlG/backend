package ctrlg.gyeongdodat.api.game.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class GameSocketController {

	@MessageMapping("/hello")
	@SendTo("/send/hello")
	public String hello(String message) {
		return "Server got: " + message;
	}
}
