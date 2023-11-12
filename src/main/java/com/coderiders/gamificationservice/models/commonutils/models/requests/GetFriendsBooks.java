package com.coderiders.gamificationservice.models.commonutils.models.requests;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetFriendsBooks {
    private String username;
    private String firstName;
    private String lastName;
    private String clerkId;
    private String imageUrl;
    private String lastBookId;
    private String lastBookTitle;
    private String lastBookUpdated;
}
