package com.coderiders.gamificationservice.models.commonutils.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;

@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class UtilsUser implements Serializable {

    private String username;
    private String firstName;
    private String lastName;
    private String clerkId;
    private String imageUrl;

}
