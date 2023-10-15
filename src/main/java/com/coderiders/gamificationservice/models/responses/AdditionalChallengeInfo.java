package com.coderiders.gamificationservice.models.responses;

public record AdditionalChallengeInfo (
        double done,
        double toGo,
        double percentComplete
) { }
