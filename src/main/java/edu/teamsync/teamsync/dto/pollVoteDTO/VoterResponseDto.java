package edu.teamsync.teamsync.dto.pollVoteDTO;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class VoterResponseDto {

    private String selectedOptions ;
    private List<Long> userId ;
    private int count ;
}
