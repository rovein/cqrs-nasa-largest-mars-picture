package com.bobocode.service;

import com.bobocode.dto.Command;
import com.bobocode.dto.Picture;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@PropertySource("classpath:application.properties")
public class NasaLargestPictureService {
    private static final Logger LOGGER = LoggerFactory.getLogger(NasaLargestPictureService.class);
    private static final Map<String, byte[]> commandToPictureMap = new ConcurrentHashMap<>();

    private final RestTemplate restTemplate;

    @Value("${nasa.api.url}")
    private String nasaApiUrl;

    @Value("${nasa.api.key}")
    private String nasaApiKey;

    public void downloadLargestPicture(Command command) {
        LOGGER.info("Started downloading picture for command: {}", command);
        String completedApiUrl = buildNasaApiUrl(command.sol());
        JsonNode nasaResponseJson = restTemplate.getForObject(completedApiUrl, JsonNode.class);
        Optional.ofNullable(nasaResponseJson)
                .map(jsonNode -> jsonNode.get("photos"))
                .stream()
                .flatMap(jsonNode -> StreamSupport.stream(jsonNode.spliterator(), false))
                .filter(photo -> isSameCameraPhoto(command, photo))
                .parallel()
                .map(photo -> photo.get("img_src").asText())
                .map(this::retrieveActualLocation)
                .flatMap(Optional::stream)
                .map(this::retrievePictureWithContentLength)
                .max(Comparator.comparing(Picture::size))
                .ifPresent(largestPicture -> retrievePictureBytes(command, largestPicture));
        logFinishedEvent(command);
    }

    private String buildNasaApiUrl(Integer sol) {
        return UriComponentsBuilder.fromHttpUrl(nasaApiUrl)
                .queryParam("sol", sol)
                .queryParam("api_key", nasaApiKey)
                .build()
                .toUriString();
    }

    private static Boolean isSameCameraPhoto(Command command, JsonNode photo) {
        return Optional.ofNullable(command.camera())
                .map(camera -> camera.equals(photo.get("camera").get("name").asText()))
                .orElse(true);
    }

    private Optional<URI> retrieveActualLocation(String imgSrc) {
        HttpHeaders headers = restTemplate.headForHeaders(imgSrc);
        return Optional.ofNullable(headers.getLocation());
    }

    private Picture retrievePictureWithContentLength(URI actualLocation) {
        HttpHeaders headers = restTemplate.headForHeaders(actualLocation);
        return new Picture(actualLocation.toString(), headers.getContentLength());
    }

    private void retrievePictureBytes(Command command, Picture largestPicture) {
        byte[] bytes = restTemplate.getForObject(largestPicture.url(), byte[].class);
        commandToPictureMap.put(command.commandId(), bytes);
    }

    private static void logFinishedEvent(Command command) {
        if (commandToPictureMap.containsKey(command.commandId())) {
            LOGGER.info("Finished downloading picture for command: {}", command);
        } else {
            LOGGER.info("Picture was not found for command: {}", command);
        }
    }

    public byte[] retrievePicture(String commandId) {
        return commandToPictureMap.getOrDefault(commandId, retrieveErrorPictureBytes("not_found.png"));
    }

    @SuppressWarnings("SameParameterValue")
    private static byte[] retrieveErrorPictureBytes(String fileName) {
        try (FileInputStream fileInputStream = new FileInputStream(
                ResourceUtils.getFile("classpath:static/" + fileName))) {
            return fileInputStream.readAllBytes();
        } catch (FileNotFoundException e) {
            throw new RuntimeException("File " + fileName + " was not found", e);
        } catch (IOException e) {
            throw new RuntimeException("Error while reading bytes for " + fileName, e);
        }
    }
}
