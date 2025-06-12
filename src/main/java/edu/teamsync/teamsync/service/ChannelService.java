package edu.teamsync.teamsync.service;

import edu.teamsync.teamsync.dto.channelDTO.ChannelRequestDTO;
import edu.teamsync.teamsync.dto.channelDTO.ChannelResponseDTO;
import edu.teamsync.teamsync.dto.channelDTO.ChannelUpdateDTO;
import edu.teamsync.teamsync.entity.Channels;
import edu.teamsync.teamsync.entity.Projects;
import edu.teamsync.teamsync.entity.Users;
import edu.teamsync.teamsync.exception.http.NotFoundException;
import edu.teamsync.teamsync.mapper.ChannelMapper;
import edu.teamsync.teamsync.repository.ChannelRepository;
import edu.teamsync.teamsync.repository.ProjectRepository;
import edu.teamsync.teamsync.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChannelService {

    @Autowired
    private ChannelRepository channelRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private ChannelMapper channelMapper;

    @Transactional
    public void createChannel(ChannelRequestDTO requestDto) {
        // Validate project existence
        Projects project = projectRepository.findById(requestDto.projectId())
                .orElseThrow(() -> new NotFoundException("Project with ID " + requestDto.projectId() + " not found"));

        // Validate member existence
        List<Long> memberIds = requestDto.memberIds();
        for (Long memberId : memberIds) {
            if (!userRepository.existsById(memberId)) {
                throw new NotFoundException("User with ID " + memberId + " not found");
            }
        }
        Channels channel = channelMapper.toEntity(requestDto);
        channel.setProject(project);
        channelRepository.save(channel);
    }

    public ChannelResponseDTO getChannelById(Long id) {
        Channels channel = channelRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Channel with ID " + id + " not found"));

        return channelMapper.toDto(channel);
    }

    public List<ChannelResponseDTO> getAllChannels() {
        List<Channels> channels = channelRepository.findAll();
        return channels.stream()
                .map(channelMapper::toDto)
                .collect(Collectors.toList());
    }
    @Transactional
    public void updateChannel(Long id, ChannelUpdateDTO dto) {
        Channels existing = channelRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Channel not found: " + id));

        Projects project = projectRepository.findById(dto.projectId())
                .orElseThrow(() -> new NotFoundException("Project not found: " + dto.projectId()));

        List<Long> memberIds = dto.members();
        for (Long memberId : memberIds) {
            Users user = userRepository.findById(memberId)
                    .orElseThrow(() -> new NotFoundException("User with ID " + memberId + " not found"));
        }
        channelMapper.updateEntityFromDto(dto, existing);
        existing.setProject(project);
        channelRepository.save(existing);
    }
    @Transactional
    public void deleteChannel(Long id) {
        if (!channelRepository.existsById(id)) {
            throw new NotFoundException("Channel with ID " + id + " not found");
        }
        channelRepository.deleteById(id);
    }
}