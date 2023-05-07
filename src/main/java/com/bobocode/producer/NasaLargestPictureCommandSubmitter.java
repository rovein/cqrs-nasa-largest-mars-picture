package com.bobocode.producer;

import com.bobocode.dto.Command;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NasaLargestPictureCommandSubmitter {
    private static final Logger LOGGER = LoggerFactory.getLogger(NasaLargestPictureCommandSubmitter.class);
    public static final int COMMAND_ID_LENGTH = 6;

    private final RabbitTemplate rabbitTemplate;

    public String submitCommand(Command commandRequest) {
        String commandId = RandomStringUtils.randomAlphanumeric(COMMAND_ID_LENGTH);
        Command command = commandRequest.withCommandId(commandId);
        rabbitTemplate.convertAndSend("largest-picture-command-exchange", StringUtils.EMPTY, command);
        LOGGER.info("Submitted command: {}, assigned ID: {}", commandRequest, commandId);
        return commandId;
    }
}
