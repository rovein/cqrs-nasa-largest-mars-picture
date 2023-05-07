package com.bobocode.dto;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public record Command(String commandId, Integer sol, String camera) {

    public Command withCommandId(String commandId) {
        return new Command(commandId, this.sol, this.camera);
    }

    @Override
    public String toString() {
        ReflectionToStringBuilder reflectionToStringBuilder = new ReflectionToStringBuilder(this);
        reflectionToStringBuilder.setExcludeNullValues(true);
        return reflectionToStringBuilder.toString();
    }
}
