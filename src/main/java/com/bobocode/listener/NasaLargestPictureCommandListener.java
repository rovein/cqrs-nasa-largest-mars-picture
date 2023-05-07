package com.bobocode.listener;

import com.bobocode.dto.Command;
import com.bobocode.service.NasaLargestPictureService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NasaLargestPictureCommandListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(NasaLargestPictureCommandListener.class);
    private final NasaLargestPictureService nasaLargestPictureService;

    @RabbitListener(queues = "largest-picture-command-queue")
    public void processLargestPictureCommand(Command command) {
        LOGGER.info("Retrieved message from the queue: {}", command);
        nasaLargestPictureService.downloadLargestPicture(command);
    }
}
