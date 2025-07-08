package edu.teamsync.teamsync.dtos;


import edu.teamsync.teamsync.dto.channelDTO.ChannelRequestDTO;
import edu.teamsync.teamsync.dto.channelDTO.ChannelResponseDTO;
import edu.teamsync.teamsync.dto.channelDTO.ChannelUpdateDTO;
import edu.teamsync.teamsync.entity.Channels.ChannelType;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ChannelDTOTests {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Nested
    @DisplayName("ChannelRequestDTO Tests")
    class ChannelRequestDTOTests {

        @Test
        @DisplayName("Should create valid ChannelRequestDTO with all required fields")
        void createChannelRequestDTO_ValidData_ShouldPass() {
            List<Long> memberIds = Arrays.asList(1L, 2L, 3L);
            ChannelRequestDTO dto = new ChannelRequestDTO(
                    "General Discussion",
                    ChannelType.group,
                    1L,
                    memberIds
            );

            Set<ConstraintViolation<ChannelRequestDTO>> violations = validator.validate(dto);

            assertTrue(violations.isEmpty());
            assertEquals("General Discussion", dto.name());
            assertEquals(ChannelType.group, dto.type());
            assertEquals(1L, dto.projectId());
            assertEquals(memberIds, dto.memberIds());
        }

        @Test
        @DisplayName("Should fail validation when name is blank")
        void createChannelRequestDTO_BlankName_ShouldFail() {
            List<Long> memberIds = Arrays.asList(1L, 2L);
            ChannelRequestDTO dto = new ChannelRequestDTO(
                    "",
                    ChannelType.group,
                    1L,
                    memberIds
            );

            Set<ConstraintViolation<ChannelRequestDTO>> violations = validator.validate(dto);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getMessage().equals("Name cannot be blank")));
        }

        @Test
        @DisplayName("Should fail validation when name is null")
        void createChannelRequestDTO_NullName_ShouldFail() {
            List<Long> memberIds = Arrays.asList(1L, 2L);
            ChannelRequestDTO dto = new ChannelRequestDTO(
                    null,
                    ChannelType.group,
                    1L,
                    memberIds
            );

            Set<ConstraintViolation<ChannelRequestDTO>> violations = validator.validate(dto);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getMessage().equals("Name cannot be blank")));
        }

        @Test
        @DisplayName("Should fail validation when name is whitespace only")
        void createChannelRequestDTO_WhitespaceOnlyName_ShouldFail() {
            List<Long> memberIds = Arrays.asList(1L, 2L);
            ChannelRequestDTO dto = new ChannelRequestDTO(
                    "   ",
                    ChannelType.group,
                    1L,
                    memberIds
            );

            Set<ConstraintViolation<ChannelRequestDTO>> violations = validator.validate(dto);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getMessage().equals("Name cannot be blank")));
        }

        @Test
        @DisplayName("Should fail validation when type is null")
        void createChannelRequestDTO_NullType_ShouldFail() {
            List<Long> memberIds = Arrays.asList(1L, 2L);
            ChannelRequestDTO dto = new ChannelRequestDTO(
                    "General Discussion",
                    null,
                    1L,
                    memberIds
            );

            Set<ConstraintViolation<ChannelRequestDTO>> violations = validator.validate(dto);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getMessage().equals("Type cannot be null")));
        }

        @Test
        @DisplayName("Should fail validation when project ID is null")
        void createChannelRequestDTO_NullProjectId_ShouldFail() {
            List<Long> memberIds = Arrays.asList(1L, 2L);
            ChannelRequestDTO dto = new ChannelRequestDTO(
                    "General Discussion",
                    ChannelType.group,
                    null,
                    memberIds
            );

            Set<ConstraintViolation<ChannelRequestDTO>> violations = validator.validate(dto);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getMessage().equals("Project ID cannot be null")));
        }

        @Test
        @DisplayName("Should fail validation when member IDs is null")
        void createChannelRequestDTO_NullMemberIds_ShouldFail() {
            ChannelRequestDTO dto = new ChannelRequestDTO(
                    "General Discussion",
                    ChannelType.group,
                    1L,
                    null
            );

            Set<ConstraintViolation<ChannelRequestDTO>> violations = validator.validate(dto);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getMessage().equals("Member IDs cannot be null")));
        }

        @Test
        @DisplayName("Should fail validation when member IDs is empty")
        void createChannelRequestDTO_EmptyMemberIds_ShouldFail() {
            ChannelRequestDTO dto = new ChannelRequestDTO(
                    "General Discussion",
                    ChannelType.group,
                    1L,
                    Collections.emptyList()
            );

            Set<ConstraintViolation<ChannelRequestDTO>> violations = validator.validate(dto);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getMessage().equals("At least one member ID is required")));
        }

        @Test
        @DisplayName("Should work with different channel types")
        void createChannelRequestDTO_DifferentChannelTypes_ShouldPass() {
            List<Long> memberIds = Arrays.asList(1L, 2L);

            ChannelRequestDTO textChannel = new ChannelRequestDTO(
                    "Text Channel",
                    ChannelType.group,
                    1L,
                    memberIds
            );

            ChannelRequestDTO voiceChannel = new ChannelRequestDTO(
                    "Voice Channel",
                    ChannelType.group,
                    1L,
                    memberIds
            );

            Set<ConstraintViolation<ChannelRequestDTO>> textViolations = validator.validate(textChannel);
            Set<ConstraintViolation<ChannelRequestDTO>> voiceViolations = validator.validate(voiceChannel);

            assertTrue(textViolations.isEmpty());
            assertTrue(voiceViolations.isEmpty());
            assertEquals(ChannelType.group, textChannel.type());
            assertEquals(ChannelType.group, voiceChannel.type());
        }

        @Test
        @DisplayName("Should fail validation with multiple errors")
        void createChannelRequestDTO_MultipleErrors_ShouldFail() {
            ChannelRequestDTO dto = new ChannelRequestDTO(
                    "",
                    null,
                    null,
                    null
            );

            Set<ConstraintViolation<ChannelRequestDTO>> violations = validator.validate(dto);

            assertEquals(4, violations.size());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getMessage().equals("Name cannot be blank")));
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getMessage().equals("Type cannot be null")));
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getMessage().equals("Project ID cannot be null")));
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getMessage().equals("Member IDs cannot be null")));
        }

        @Test
        @DisplayName("Should work with single member ID")
        void createChannelRequestDTO_SingleMemberID_ShouldPass() {
            List<Long> memberIds = Arrays.asList(1L);
            ChannelRequestDTO dto = new ChannelRequestDTO(
                    "Private Channel",
                    ChannelType.group,
                    1L,
                    memberIds
            );

            Set<ConstraintViolation<ChannelRequestDTO>> violations = validator.validate(dto);

            assertTrue(violations.isEmpty());
            assertEquals(1, dto.memberIds().size());
            assertEquals(1L, dto.memberIds().get(0));
        }

        @Test
        @DisplayName("Should work with multiple member IDs")
        void createChannelRequestDTO_MultipleMemberIDs_ShouldPass() {
            List<Long> memberIds = Arrays.asList(1L, 2L, 3L, 4L, 5L);
            ChannelRequestDTO dto = new ChannelRequestDTO(
                    "Team Channel",
                    ChannelType.group,
                    1L,
                    memberIds
            );

            Set<ConstraintViolation<ChannelRequestDTO>> violations = validator.validate(dto);

            assertTrue(violations.isEmpty());
            assertEquals(5, dto.memberIds().size());
            assertEquals(memberIds, dto.memberIds());
        }

        @Test
        @DisplayName("Should test equals and hashCode methods")
        void createChannelRequestDTO_EqualsAndHashCode_ShouldWork() {
            List<Long> memberIds = Arrays.asList(1L, 2L, 3L);

            ChannelRequestDTO dto1 = new ChannelRequestDTO(
                    "General Discussion",
                    ChannelType.group,
                    1L,
                    memberIds
            );

            ChannelRequestDTO dto2 = new ChannelRequestDTO(
                    "General Discussion",
                    ChannelType.group,
                    1L,
                    memberIds
            );

            ChannelRequestDTO dto3 = new ChannelRequestDTO(
                    "Different Channel",
                    ChannelType.group,
                    2L,
                    Arrays.asList(4L, 5L)
            );

            assertEquals(dto1, dto2);
            assertNotEquals(dto1, dto3);
            assertEquals(dto1.hashCode(), dto2.hashCode());
            assertNotEquals(dto1.hashCode(), dto3.hashCode());
        }

        @Test
        @DisplayName("Should test toString method")
        void createChannelRequestDTO_ToString_ShouldWork() {
            List<Long> memberIds = Arrays.asList(1L, 2L, 3L);
            ChannelRequestDTO dto = new ChannelRequestDTO(
                    "General Discussion",
                    ChannelType.group,
                    1L,
                    memberIds
            );

            String toString = dto.toString();
            assertNotNull(toString);
            assertTrue(toString.contains("General Discussion"));
            assertTrue(toString.contains("group"));
            assertTrue(toString.contains("1"));
        }
    }

    @Nested
    @DisplayName("ChannelResponseDTO Tests")
    class ChannelResponseDTOTests {

        @Test
        @DisplayName("Should create valid ChannelResponseDTO with all fields")
        void createChannelResponseDTO_ValidData_ShouldPass() {
            List<Long> members = Arrays.asList(1L, 2L, 3L);
            ChannelResponseDTO dto = new ChannelResponseDTO(
                    1L,
                    "General Discussion",
                    ChannelType.group,
                    1L,
                    members
            );

            assertEquals(1L, dto.id());
            assertEquals("General Discussion", dto.name());
            assertEquals(ChannelType.group, dto.type());
            assertEquals(1L, dto.projectId());
            assertEquals(members, dto.members());
        }

        @Test
        @DisplayName("Should create ChannelResponseDTO with null optional fields")
        void createChannelResponseDTO_NullOptionalFields_ShouldPass() {
            ChannelResponseDTO dto = new ChannelResponseDTO(
                    1L,
                    null,
                    null,
                    null,
                    null
            );

            assertEquals(1L, dto.id());
            assertNull(dto.name());
            assertNull(dto.type());
            assertNull(dto.projectId());
            assertNull(dto.members());
        }

        @Test
        @DisplayName("Should work with different channel types")
        void createChannelResponseDTO_DifferentChannelTypes_ShouldPass() {
            List<Long> members = Arrays.asList(1L, 2L);

            ChannelResponseDTO textChannel = new ChannelResponseDTO(
                    1L,
                    "Text Channel",
                    ChannelType.group,
                    1L,
                    members
            );

            ChannelResponseDTO voiceChannel = new ChannelResponseDTO(
                    2L,
                    "Voice Channel",
                    ChannelType.group,
                    1L,
                    members
            );

            assertEquals(ChannelType.group, textChannel.type());
            assertEquals(ChannelType.group, voiceChannel.type());
        }

        @Test
        @DisplayName("Should work with empty members list")
        void createChannelResponseDTO_EmptyMembers_ShouldPass() {
            ChannelResponseDTO dto = new ChannelResponseDTO(
                    1L,
                    "Empty Channel",
                    ChannelType.group,
                    1L,
                    Collections.emptyList()
            );

            assertEquals(1L, dto.id());
            assertEquals("Empty Channel", dto.name());
            assertEquals(ChannelType.group, dto.type());
            assertEquals(1L, dto.projectId());
            assertTrue(dto.members().isEmpty());
        }

        @Test
        @DisplayName("Should work with single member")
        void createChannelResponseDTO_SingleMember_ShouldPass() {
            List<Long> members = Arrays.asList(1L);
            ChannelResponseDTO dto = new ChannelResponseDTO(
                    1L,
                    "Private Channel",
                    ChannelType.group,
                    1L,
                    members
            );

            assertEquals(1, dto.members().size());
            assertEquals(1L, dto.members().get(0));
        }

        @Test
        @DisplayName("Should work with multiple members")
        void createChannelResponseDTO_MultipleMembers_ShouldPass() {
            List<Long> members = Arrays.asList(1L, 2L, 3L, 4L, 5L);
            ChannelResponseDTO dto = new ChannelResponseDTO(
                    1L,
                    "Team Channel",
                    ChannelType.group,
                    1L,
                    members
            );

            assertEquals(5, dto.members().size());
            assertEquals(members, dto.members());
        }

        @Test
        @DisplayName("Should test equals and hashCode methods")
        void createChannelResponseDTO_EqualsAndHashCode_ShouldWork() {
            List<Long> members = Arrays.asList(1L, 2L, 3L);

            ChannelResponseDTO dto1 = new ChannelResponseDTO(
                    1L,
                    "General Discussion",
                    ChannelType.group,
                    1L,
                    members
            );

            ChannelResponseDTO dto2 = new ChannelResponseDTO(
                    1L,
                    "General Discussion",
                    ChannelType.group,
                    1L,
                    members
            );

            ChannelResponseDTO dto3 = new ChannelResponseDTO(
                    2L,
                    "Different Channel",
                    ChannelType.group,
                    2L,
                    Arrays.asList(4L, 5L)
            );

            assertEquals(dto1, dto2);
            assertNotEquals(dto1, dto3);
            assertEquals(dto1.hashCode(), dto2.hashCode());
            assertNotEquals(dto1.hashCode(), dto3.hashCode());
        }

        @Test
        @DisplayName("Should test toString method")
        void createChannelResponseDTO_ToString_ShouldWork() {
            List<Long> members = Arrays.asList(1L, 2L, 3L);
            ChannelResponseDTO dto = new ChannelResponseDTO(
                    1L,
                    "General Discussion",
                    ChannelType.group,
                    1L,
                    members
            );

            String toString = dto.toString();
            assertNotNull(toString);
            assertTrue(toString.contains("General Discussion"));
            assertTrue(toString.contains("group"));
            assertTrue(toString.contains("1"));
        }

        @Test
        @DisplayName("Should handle large member lists")
        void createChannelResponseDTO_LargeMemberList_ShouldWork() {
            List<Long> members = Arrays.asList(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L);
            ChannelResponseDTO dto = new ChannelResponseDTO(
                    1L,
                    "Large Team Channel",
                    ChannelType.group,
                    1L,
                    members
            );

            assertEquals(10, dto.members().size());
            assertEquals(members, dto.members());
        }
    }

    @Nested
    @DisplayName("ChannelUpdateDTO Tests")
    class ChannelUpdateDTOTests {

        @Test
        @DisplayName("Should create valid ChannelUpdateDTO with all required fields")
        void createChannelUpdateDTO_ValidData_ShouldPass() {
            List<Long> members = Arrays.asList(1L, 2L, 3L);
            ChannelUpdateDTO dto = new ChannelUpdateDTO(
                    "Updated Channel",
                    ChannelType.group,
                    1L,
                    members
            );

            Set<ConstraintViolation<ChannelUpdateDTO>> violations = validator.validate(dto);

            assertTrue(violations.isEmpty());
            assertEquals("Updated Channel", dto.name());
            assertEquals(ChannelType.group, dto.type());
            assertEquals(1L, dto.projectId());
            assertEquals(members, dto.members());
        }

        @Test
        @DisplayName("Should fail validation when name is blank")
        void createChannelUpdateDTO_BlankName_ShouldFail() {
            List<Long> members = Arrays.asList(1L, 2L);
            ChannelUpdateDTO dto = new ChannelUpdateDTO(
                    "",
                    ChannelType.group,
                    1L,
                    members
            );

            Set<ConstraintViolation<ChannelUpdateDTO>> violations = validator.validate(dto);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getMessage().equals("Name cannot be blank")));
        }

        @Test
        @DisplayName("Should fail validation when name is null")
        void createChannelUpdateDTO_NullName_ShouldFail() {
            List<Long> members = Arrays.asList(1L, 2L);
            ChannelUpdateDTO dto = new ChannelUpdateDTO(
                    null,
                    ChannelType.group,
                    1L,
                    members
            );

            Set<ConstraintViolation<ChannelUpdateDTO>> violations = validator.validate(dto);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getMessage().equals("Name cannot be blank")));
        }

        @Test
        @DisplayName("Should fail validation when name is whitespace only")
        void createChannelUpdateDTO_WhitespaceOnlyName_ShouldFail() {
            List<Long> members = Arrays.asList(1L, 2L);
            ChannelUpdateDTO dto = new ChannelUpdateDTO(
                    "   ",
                    ChannelType.group,
                    1L,
                    members
            );

            Set<ConstraintViolation<ChannelUpdateDTO>> violations = validator.validate(dto);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getMessage().equals("Name cannot be blank")));
        }

        @Test
        @DisplayName("Should fail validation when type is null")
        void createChannelUpdateDTO_NullType_ShouldFail() {
            List<Long> members = Arrays.asList(1L, 2L);
            ChannelUpdateDTO dto = new ChannelUpdateDTO(
                    "Updated Channel",
                    null,
                    1L,
                    members
            );

            Set<ConstraintViolation<ChannelUpdateDTO>> violations = validator.validate(dto);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getMessage().equals("Type cannot be null")));
        }

        @Test
        @DisplayName("Should fail validation when project ID is null")
        void createChannelUpdateDTO_NullProjectId_ShouldFail() {
            List<Long> members = Arrays.asList(1L, 2L);
            ChannelUpdateDTO dto = new ChannelUpdateDTO(
                    "Updated Channel",
                    ChannelType.group,
                    null,
                    members
            );

            Set<ConstraintViolation<ChannelUpdateDTO>> violations = validator.validate(dto);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getMessage().equals("Project ID cannot be null")));
        }

        @Test
        @DisplayName("Should fail validation when members is null")
        void createChannelUpdateDTO_NullMembers_ShouldFail() {
            ChannelUpdateDTO dto = new ChannelUpdateDTO(
                    "Updated Channel",
                    ChannelType.group,
                    1L,
                    null
            );

            Set<ConstraintViolation<ChannelUpdateDTO>> violations = validator.validate(dto);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getMessage().equals("Members cannot be null")));
        }

        @Test
        @DisplayName("Should fail validation when members is empty")
        void createChannelUpdateDTO_EmptyMembers_ShouldFail() {
            ChannelUpdateDTO dto = new ChannelUpdateDTO(
                    "Updated Channel",
                    ChannelType.group,
                    1L,
                    Collections.emptyList()
            );

            Set<ConstraintViolation<ChannelUpdateDTO>> violations = validator.validate(dto);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getMessage().equals("At least one member is required")));
        }

        @Test
        @DisplayName("Should work with different channel types")
        void createChannelUpdateDTO_DifferentChannelTypes_ShouldPass() {
            List<Long> members = Arrays.asList(1L, 2L);

            ChannelUpdateDTO textChannel = new ChannelUpdateDTO(
                    "Updated Text Channel",
                    ChannelType.group,
                    1L,
                    members
            );

            ChannelUpdateDTO voiceChannel = new ChannelUpdateDTO(
                    "Updated Voice Channel",
                    ChannelType.group,
                    1L,
                    members
            );

            Set<ConstraintViolation<ChannelUpdateDTO>> textViolations = validator.validate(textChannel);
            Set<ConstraintViolation<ChannelUpdateDTO>> voiceViolations = validator.validate(voiceChannel);

            assertTrue(textViolations.isEmpty());
            assertTrue(voiceViolations.isEmpty());
            assertEquals(ChannelType.group, textChannel.type());
            assertEquals(ChannelType.group, voiceChannel.type());
        }

        @Test
        @DisplayName("Should fail validation with multiple errors")
        void createChannelUpdateDTO_MultipleErrors_ShouldFail() {
            ChannelUpdateDTO dto = new ChannelUpdateDTO(
                    "",
                    null,
                    null,
                    null
            );

            Set<ConstraintViolation<ChannelUpdateDTO>> violations = validator.validate(dto);

            assertEquals(4, violations.size());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getMessage().equals("Name cannot be blank")));
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getMessage().equals("Type cannot be null")));
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getMessage().equals("Project ID cannot be null")));
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getMessage().equals("Members cannot be null")));
        }

        @Test
        @DisplayName("Should work with single member")
        void createChannelUpdateDTO_SingleMember_ShouldPass() {
            List<Long> members = Arrays.asList(1L);
            ChannelUpdateDTO dto = new ChannelUpdateDTO(
                    "Updated Private Channel",
                    ChannelType.group,
                    1L,
                    members
            );

            Set<ConstraintViolation<ChannelUpdateDTO>> violations = validator.validate(dto);

            assertTrue(violations.isEmpty());
            assertEquals(1, dto.members().size());
            assertEquals(1L, dto.members().get(0));
        }

        @Test
        @DisplayName("Should work with multiple members")
        void createChannelUpdateDTO_MultipleMembers_ShouldPass() {
            List<Long> members = Arrays.asList(1L, 2L, 3L, 4L, 5L);
            ChannelUpdateDTO dto = new ChannelUpdateDTO(
                    "Updated Team Channel",
                    ChannelType.group,
                    1L,
                    members
            );

            Set<ConstraintViolation<ChannelUpdateDTO>> violations = validator.validate(dto);

            assertTrue(violations.isEmpty());
            assertEquals(5, dto.members().size());
            assertEquals(members, dto.members());
        }

        @Test
        @DisplayName("Should test equals and hashCode methods")
        void createChannelUpdateDTO_EqualsAndHashCode_ShouldWork() {
            List<Long> members = Arrays.asList(1L, 2L, 3L);

            ChannelUpdateDTO dto1 = new ChannelUpdateDTO(
                    "Updated Channel",
                    ChannelType.group,
                    1L,
                    members
            );

            ChannelUpdateDTO dto2 = new ChannelUpdateDTO(
                    "Updated Channel",
                    ChannelType.group,
                    1L,
                    members
            );

            ChannelUpdateDTO dto3 = new ChannelUpdateDTO(
                    "Different Channel",
                    ChannelType.group,
                    2L,
                    Arrays.asList(4L, 5L)
            );

            assertEquals(dto1, dto2);
            assertNotEquals(dto1, dto3);
            assertEquals(dto1.hashCode(), dto2.hashCode());
            assertNotEquals(dto1.hashCode(), dto3.hashCode());
        }

        @Test
        @DisplayName("Should test toString method")
        void createChannelUpdateDTO_ToString_ShouldWork() {
            List<Long> members = Arrays.asList(1L, 2L, 3L);
            ChannelUpdateDTO dto = new ChannelUpdateDTO(
                    "Updated Channel",
                    ChannelType.group,
                    1L,
                    members
            );

            String toString = dto.toString();
            assertNotNull(toString);
            assertTrue(toString.contains("Updated Channel"));
            assertTrue(toString.contains("group"));
            assertTrue(toString.contains("1"));
        }

        @Test
        @DisplayName("Should handle channel type updates")
        void createChannelUpdateDTO_ChannelTypeUpdates_ShouldWork() {
            List<Long> members = Arrays.asList(1L, 2L);

            ChannelUpdateDTO dto = new ChannelUpdateDTO(
                    "Flexible Channel",
                    ChannelType.group,
                    1L,
                    members
            );

            Set<ConstraintViolation<ChannelUpdateDTO>> violations = validator.validate(dto);

            assertTrue(violations.isEmpty());
            assertEquals(ChannelType.group, dto.type());

            // Test with different type
            ChannelUpdateDTO updatedDto = new ChannelUpdateDTO(
                    "Flexible Channel",
                    ChannelType.group,
                    1L,
                    members
            );

            Set<ConstraintViolation<ChannelUpdateDTO>> updatedViolations = validator.validate(updatedDto);

            assertTrue(updatedViolations.isEmpty());
            assertEquals(ChannelType.group, updatedDto.type());
        }

        @Test
        @DisplayName("Should handle member list updates")
        void createChannelUpdateDTO_MemberListUpdates_ShouldWork() {
            List<Long> originalMembers = Arrays.asList(1L, 2L, 3L);
            List<Long> updatedMembers = Arrays.asList(1L, 2L, 3L, 4L, 5L);

            ChannelUpdateDTO dto = new ChannelUpdateDTO(
                    "Growing Channel",
                    ChannelType.group,
                    1L,
                    originalMembers
            );

            Set<ConstraintViolation<ChannelUpdateDTO>> violations = validator.validate(dto);

            assertTrue(violations.isEmpty());
            assertEquals(3, dto.members().size());

            // Test with updated members
            ChannelUpdateDTO updatedDto = new ChannelUpdateDTO(
                    "Growing Channel",
                    ChannelType.group,
                    1L,
                    updatedMembers
            );

            Set<ConstraintViolation<ChannelUpdateDTO>> updatedViolations = validator.validate(updatedDto);

            assertTrue(updatedViolations.isEmpty());
            assertEquals(5, updatedDto.members().size());
            assertEquals(updatedMembers, updatedDto.members());
        }
    }
}
