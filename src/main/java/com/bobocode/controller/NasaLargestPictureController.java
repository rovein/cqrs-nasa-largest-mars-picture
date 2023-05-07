package com.bobocode.controller;

import com.bobocode.dto.Command;
import com.bobocode.producer.NasaLargestPictureCommandSubmitter;
import com.bobocode.service.NasaLargestPictureService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/mars/pictures/largest")
@RequiredArgsConstructor
public class NasaLargestPictureController {
    private final NasaLargestPictureCommandSubmitter nasaLargestPictureCommandSubmitter;
    private final NasaLargestPictureService nasaLargestPictureService;

    @PostMapping
    public ResponseEntity<?> handleCommand(@RequestBody Command command) {
        String commandId = nasaLargestPictureCommandSubmitter.submitCommand(command);
        URI pictureLocation = ServletUriComponentsBuilder.fromCurrentRequestUri()
                .pathSegment("{commandId}")
                .build(commandId);
        return ResponseEntity.created(pictureLocation).build();
    }

    @GetMapping("/{commandId}")
    public ResponseEntity<byte[]> get(@PathVariable String commandId) {
        byte[] picture = nasaLargestPictureService.retrievePicture(commandId);
        return ResponseEntity.ok()
                .contentLength(picture.length)
                .contentType(MediaType.IMAGE_PNG)
                .body(picture);
    }
}
