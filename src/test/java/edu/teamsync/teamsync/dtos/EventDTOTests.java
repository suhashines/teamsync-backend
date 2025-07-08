package edu.teamsync.teamsync.dtos;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import edu.teamsync.teamsync.dto.eventsDTO.EventCreationDTO;
import edu.teamsync.teamsync.dto.eventsDTO.EventResponseDTO;
import edu.teamsync.teamsync.dto.eventsDTO.EventUpdateDTO;
import edu.teamsync.teamsync.entity.Events.EventType;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class EventDTOTests {

    private Validator validator;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Nested
    @DisplayName("EventCreationDTO Tests")
    class EventCreationDTOTest {

        @Test
        @DisplayName("Should create valid EventCreationDTO")
        void shouldCreateValidEventCreationDTO() {
            EventCreationDTO dto = new EventCreationDTO(
                    "Team Meeting",
                    "Weekly team sync",
                    EventType.Outing,
                    LocalDate.of(2024, 12, 25),
                    Arrays.asList(1L, 2L, 3L)
            );

            Set<ConstraintViolation<EventCreationDTO>> violations = validator.validate(dto);
            assertTrue(violations.isEmpty());

            assertEquals("Team Meeting", dto.title());
            assertEquals("Weekly team sync", dto.description());
            assertEquals(EventType.Outing, dto.type());
            assertEquals(LocalDate.of(2024, 12, 25), dto.date());
            assertEquals(Arrays.asList(1L, 2L, 3L), dto.participantIds());
        }

        @Test
        @DisplayName("Should fail validation when title is blank")
        void shouldFailValidationWhenTitleIsBlank() {
            EventCreationDTO dto = new EventCreationDTO(
                    "",
                    "Description",
                    EventType.Outing,
                    LocalDate.of(2024, 12, 25),
                    Arrays.asList(1L, 2L)
            );

            Set<ConstraintViolation<EventCreationDTO>> violations = validator.validate(dto);
            assertEquals(1, violations.size());
            assertEquals("Title cannot be blank", violations.iterator().next().getMessage());
        }

        @Test
        @DisplayName("Should fail validation when title is null")
        void shouldFailValidationWhenTitleIsNull() {
            EventCreationDTO dto = new EventCreationDTO(
                    null,
                    "Description",
                    EventType.Outing,
                    LocalDate.of(2024, 12, 25),
                    Arrays.asList(1L, 2L)
            );

            Set<ConstraintViolation<EventCreationDTO>> violations = validator.validate(dto);
            assertEquals(1, violations.size());
            assertEquals("Title cannot be blank", violations.iterator().next().getMessage());
        }

        @Test
        @DisplayName("Should fail validation when type is null")
        void shouldFailValidationWhenTypeIsNull() {
            EventCreationDTO dto = new EventCreationDTO(
                    "Title",
                    "Description",
                    null,
                    LocalDate.of(2024, 12, 25),
                    Arrays.asList(1L, 2L)
            );

            Set<ConstraintViolation<EventCreationDTO>> violations = validator.validate(dto);
            assertEquals(1, violations.size());
            assertEquals("Type cannot be null", violations.iterator().next().getMessage());
        }

        @Test
        @DisplayName("Should fail validation when date is null")
        void shouldFailValidationWhenDateIsNull() {
            EventCreationDTO dto = new EventCreationDTO(
                    "Title",
                    "Description",
                    EventType.Outing,
                    null,
                    Arrays.asList(1L, 2L)
            );

            Set<ConstraintViolation<EventCreationDTO>> violations = validator.validate(dto);
            assertEquals(1, violations.size());
            assertEquals("Date cannot be null", violations.iterator().next().getMessage());
        }

        @Test
        @DisplayName("Should fail validation when participant IDs is null")
        void shouldFailValidationWhenParticipantIdsIsNull() {
            EventCreationDTO dto = new EventCreationDTO(
                    "Title",
                    "Description",
                    EventType.Outing,
                    LocalDate.of(2024, 12, 25),
                    null
            );

            Set<ConstraintViolation<EventCreationDTO>> violations = validator.validate(dto);
            assertEquals(1, violations.size());
            assertEquals("Participant IDs cannot be null", violations.iterator().next().getMessage());
        }

        @Test
        @DisplayName("Should fail validation when participant IDs is empty")
        void shouldFailValidationWhenParticipantIdsIsEmpty() {
            EventCreationDTO dto = new EventCreationDTO(
                    "Title",
                    "Description",
                    EventType.Outing,
                    LocalDate.of(2024, 12, 25),
                    Collections.emptyList()
            );

            Set<ConstraintViolation<EventCreationDTO>> violations = validator.validate(dto);
            assertEquals(1, violations.size());
            assertEquals("At least one participant ID is required", violations.iterator().next().getMessage());
        }

        @Test
        @DisplayName("Should allow null description")
        void shouldAllowNullDescription() {
            EventCreationDTO dto = new EventCreationDTO(
                    "Title",
                    null,
                    EventType.Outing,
                    LocalDate.of(2024, 12, 25),
                    Arrays.asList(1L)
            );

            Set<ConstraintViolation<EventCreationDTO>> violations = validator.validate(dto);
            assertTrue(violations.isEmpty());
            assertNull(dto.description());
        }


        @Test
        @DisplayName("Should serialize to JSON with snake_case")
        void shouldSerializeToJsonWithSnakeCase() throws Exception {
            EventCreationDTO dto = new EventCreationDTO(
                    "Team Meeting",
                    "Weekly sync",
                    EventType.Outing,
                    LocalDate.of(2024, 12, 25),
                    Arrays.asList(1L, 2L)
            );

            String json = objectMapper.writeValueAsString(dto);
            System.out.println("Actual JSON: " + json);  // Add this line

            assertTrue(json.contains("\"participant_ids\""));
            assertTrue(json.contains("\"Team Meeting\""));
            assertTrue(json.contains("\"Weekly sync\""));
            assertTrue(json.contains("\"2024-12-25\""));
        }

        @Test
        @DisplayName("Should deserialize from JSON with snake_case")
        void shouldDeserializeFromJsonWithSnakeCase() throws Exception {
            String json = """
                {
                    "title": "Team Meeting",
                    "description": "Weekly sync",
                    "type": "Outing",
                    "date": "2024-12-25",
                    "participant_ids": [1, 2, 3]
                }
                """;

            EventCreationDTO dto = objectMapper.readValue(json, EventCreationDTO.class);

            assertEquals("Team Meeting", dto.title());
            assertEquals("Weekly sync", dto.description());
            assertEquals(EventType.Outing, dto.type());
            assertEquals(LocalDate.of(2024, 12, 25), dto.date());
            assertEquals(3, dto.participantIds().size());
            assertEquals(1, dto.participantIds().get(0));
            assertEquals(2, dto.participantIds().get(1));
            assertEquals(3, dto.participantIds().get(2));

        }

    }

    @Nested
    @DisplayName("EventResponseDTO Tests")
    class EventResponseDTOTest {

        @Test
        @DisplayName("Should create valid EventResponseDTO")
        void shouldCreateValidEventResponseDTO() {
            EventResponseDTO dto = new EventResponseDTO(
                    1L,
                    "Team Meeting",
                    "Weekly team sync",
                    EventType.Outing,
                    LocalDate.of(2024, 12, 25),
                    Arrays.asList(1L, 2L, 3L),
                    LocalDate.of(2024, 12, 20)
            );

            assertEquals(1L, dto.id());
            assertEquals("Team Meeting", dto.title());
            assertEquals("Weekly team sync", dto.description());
            assertEquals(EventType.Outing, dto.type());
            assertEquals(LocalDate.of(2024, 12, 25), dto.date());
            assertEquals(Arrays.asList(1L, 2L, 3L), dto.participantIds());
            assertEquals(LocalDate.of(2024, 12, 20), dto.tentativeStartingDate());
        }

        @Test
        @DisplayName("Should allow null values")
        void shouldAllowNullValues() {
            EventResponseDTO dto = new EventResponseDTO(
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
            );

            assertNull(dto.id());
            assertNull(dto.title());
            assertNull(dto.description());
            assertNull(dto.type());
            assertNull(dto.date());
            assertNull(dto.participantIds());
            assertNull(dto.tentativeStartingDate());
        }

        @Test
        @DisplayName("Should serialize to JSON with snake_case")
        void shouldSerializeToJsonWithSnakeCase() throws Exception {
            EventResponseDTO dto = new EventResponseDTO(
                    1L,
                    "Team Meeting",
                    "Weekly sync",
                    EventType.Outing,
                    LocalDate.of(2024, 12, 25),
                    Arrays.asList(1L, 2L),
                    LocalDate.of(2024, 12, 20)
            );

            String json = objectMapper.writeValueAsString(dto);

            assertTrue(json.contains("\"participant_ids\""));
            assertTrue(json.contains("\"tentative_starting_date\""));
            assertTrue(json.contains("\"2024-12-25\""));
            assertTrue(json.contains("\"2024-12-20\""));
        }

        @Test
        @DisplayName("Should deserialize from JSON with snake_case")
        void shouldDeserializeFromJsonWithSnakeCase() throws Exception {
            String json = """
                {
                    "id": 1,
                    "title": "Team Meeting",
                    "description": "Weekly sync",
                    "type": "Outing",
                    "date": "2024-12-25",
                    "participant_ids": [1, 2, 3],
                    "tentative_starting_date": "2024-12-20"
                }
                """;

            EventResponseDTO dto = objectMapper.readValue(json, EventResponseDTO.class);

            assertEquals(1L, dto.id());
            assertEquals("Team Meeting", dto.title());
            assertEquals("Weekly sync", dto.description());
            assertEquals(EventType.Outing, dto.type());
            assertEquals(LocalDate.of(2024, 12, 25), dto.date());
            assertEquals(3, dto.participantIds().size());
            assertEquals(1, dto.participantIds().get(0));
            assertEquals(2, dto.participantIds().get(1));
            assertEquals(3, dto.participantIds().get(2));
            assertEquals(LocalDate.of(2024, 12, 20), dto.tentativeStartingDate());
        }
    }

    @Nested
    @DisplayName("EventUpdateDTO Tests")
    class EventUpdateDTOTest {

        @Test
        @DisplayName("Should create valid EventUpdateDTO")
        void shouldCreateValidEventUpdateDTO() {
            EventUpdateDTO dto = new EventUpdateDTO(
                    "Updated Meeting",
                    "Updated description",
                    EventType.Birthday,
                    LocalDate.of(2024, 12, 26),
                    Arrays.asList(1L, 2L, 3L)
            );

            Set<ConstraintViolation<EventUpdateDTO>> violations = validator.validate(dto);
            assertTrue(violations.isEmpty());

            assertEquals("Updated Meeting", dto.title());
            assertEquals("Updated description", dto.description());
            assertEquals(EventType.Birthday, dto.type());
            assertEquals(LocalDate.of(2024, 12, 26), dto.date());
            assertEquals(Arrays.asList(1L, 2L, 3L), dto.participants());
        }

        @Test
        @DisplayName("Should fail validation when title is blank")
        void shouldFailValidationWhenTitleIsBlank() {
            EventUpdateDTO dto = new EventUpdateDTO(
                    "",
                    "Description",
                    EventType.Outing,
                    LocalDate.of(2024, 12, 25),
                    Arrays.asList(1L, 2L)
            );

            Set<ConstraintViolation<EventUpdateDTO>> violations = validator.validate(dto);
            assertEquals(1, violations.size());
            assertEquals("Title cannot be blank", violations.iterator().next().getMessage());
        }

        @Test
        @DisplayName("Should fail validation when participants is null")
        void shouldFailValidationWhenParticipantsIsNull() {
            EventUpdateDTO dto = new EventUpdateDTO(
                    "Title",
                    "Description",
                    EventType.Outing,
                    LocalDate.of(2024, 12, 25),
                    null
            );

            Set<ConstraintViolation<EventUpdateDTO>> violations = validator.validate(dto);
            assertEquals(1, violations.size());
            assertEquals("Participants cannot be null", violations.iterator().next().getMessage());
        }

        @Test
        @DisplayName("Should fail validation when participants is empty")
        void shouldFailValidationWhenParticipantsIsEmpty() {
            EventUpdateDTO dto = new EventUpdateDTO(
                    "Title",
                    "Description",
                    EventType.Outing,
                    LocalDate.of(2024, 12, 25),
                    Collections.emptyList()
            );

            Set<ConstraintViolation<EventUpdateDTO>> violations = validator.validate(dto);
            assertEquals(1, violations.size());
            assertEquals("At least one participant is required", violations.iterator().next().getMessage());
        }


        @Test
        @DisplayName("Should serialize to JSON with snake_case")
        void shouldSerializeToJsonWithSnakeCase() throws Exception {
            EventUpdateDTO dto = new EventUpdateDTO(
                    "Updated Meeting",
                    "Updated description",
                    EventType.Birthday,
                    LocalDate.of(2024, 12, 26),
                    Arrays.asList(1L, 2L)
            );

            String json = objectMapper.writeValueAsString(dto);

            // Check that property names are in snake_case
            assertTrue(json.contains("\"participants\""));  // field name converted to snake_case

            // Check that property values are preserved as-is
            assertTrue(json.contains("\"Updated Meeting\""));      // title value
            assertTrue(json.contains("\"Updated description\""));  // description value
            assertTrue(json.contains("\"2024-12-26\""));          // date value
        }


        @Test
        @DisplayName("Should deserialize from JSON with snake_case")
        void shouldDeserializeFromJsonWithSnakeCase() throws Exception {
            String json = """
        {
            "title": "Updated Meeting",
            "description": "Updated description",
            "type": "Birthday",
            "date": "2024-12-26",
            "participants": [1, 2, 3]
        }
        """;

            EventUpdateDTO dto = objectMapper.readValue(json, EventUpdateDTO.class);

            assertEquals("Updated Meeting", dto.title());
            assertEquals("Updated description", dto.description());
            assertEquals(EventType.Birthday, dto.type());
            assertEquals(LocalDate.of(2024, 12, 26), dto.date());

            // Compare list contents instead of list objects
            assertEquals(3, dto.participants().size());
            assertEquals(1, dto.participants().get(0));
            assertEquals(2, dto.participants().get(1));
            assertEquals(3, dto.participants().get(2));

            // Alternative: Convert to the same list type for comparison
            // assertEquals(new ArrayList<>(Arrays.asList(1, 2, 3)), dto.participants());
        }
    }

    @Nested
    @DisplayName("Record Equality Tests")
    class RecordEqualityTest {

        @Test
        @DisplayName("EventCreationDTO records should be equal when all fields match")
        void eventCreationDTORecordsShouldBeEqualWhenAllFieldsMatch() {
            EventCreationDTO dto1 = new EventCreationDTO(
                    "Meeting",
                    "Description",
                    EventType.Outing,
                    LocalDate.of(2024, 12, 25),
                    Arrays.asList(1L, 2L)
            );
            EventCreationDTO dto2 = new EventCreationDTO(
                    "Meeting",
                    "Description",
                    EventType.Outing,
                    LocalDate.of(2024, 12, 25),
                    Arrays.asList(1L, 2L)
            );

            assertEquals(dto1, dto2);
            assertEquals(dto1.hashCode(), dto2.hashCode());
        }

        @Test
        @DisplayName("EventResponseDTO records should be equal when all fields match")
        void eventResponseDTORecordsShouldBeEqualWhenAllFieldsMatch() {
            EventResponseDTO dto1 = new EventResponseDTO(
                    1L,
                    "Meeting",
                    "Description",
                    EventType.Outing,
                    LocalDate.of(2024, 12, 25),
                    Arrays.asList(1L, 2L),
                    LocalDate.of(2024, 12, 20)
            );
            EventResponseDTO dto2 = new EventResponseDTO(
                    1L,
                    "Meeting",
                    "Description",
                    EventType.Outing,
                    LocalDate.of(2024, 12, 25),
                    Arrays.asList(1L, 2L),
                    LocalDate.of(2024, 12, 20)
            );

            assertEquals(dto1, dto2);
            assertEquals(dto1.hashCode(), dto2.hashCode());
        }

        @Test
        @DisplayName("EventUpdateDTO records should be equal when all fields match")
        void eventUpdateDTORecordsShouldBeEqualWhenAllFieldsMatch() {
            EventUpdateDTO dto1 = new EventUpdateDTO(
                    "Meeting",
                    "Description",
                    EventType.Outing,
                    LocalDate.of(2024, 12, 25),
                    Arrays.asList(1L, 2L)
            );
            EventUpdateDTO dto2 = new EventUpdateDTO(
                    "Meeting",
                    "Description",
                    EventType.Outing,
                    LocalDate.of(2024, 12, 25),
                    Arrays.asList(1L, 2L)
            );

            assertEquals(dto1, dto2);
            assertEquals(dto1.hashCode(), dto2.hashCode());
        }

        @Test
        @DisplayName("Records should not be equal when fields differ")
        void recordsShouldNotBeEqualWhenFieldsDiffer() {
            EventCreationDTO dto1 = new EventCreationDTO(
                    "Meeting 1",
                    "Description",
                    EventType.Outing,
                    LocalDate.of(2024, 12, 25),
                    Arrays.asList(1L, 2L)
            );
            EventCreationDTO dto2 = new EventCreationDTO(
                    "Meeting 2",
                    "Description",
                    EventType.Outing,
                    LocalDate.of(2024, 12, 25),
                    Arrays.asList(1L, 2L)
            );

            assertNotEquals(dto1, dto2);
        }
    }

    @Nested
    @DisplayName("toString Tests")
    class ToStringTest {

        @Test
        @DisplayName("Should generate meaningful toString for EventCreationDTO")
        void shouldGenerateMeaningfulToStringForEventCreationDTO() {
            EventCreationDTO dto = new EventCreationDTO(
                    "Meeting",
                    "Description",
                    EventType.Outing,
                    LocalDate.of(2024, 12, 25),
                    Arrays.asList(1L, 2L)
            );

            String toString = dto.toString();

            assertTrue(toString.contains("Meeting"));
            assertTrue(toString.contains("Description"));
            assertTrue(toString.contains("Outing"));
            assertTrue(toString.contains("2024-12-25"));
            assertTrue(toString.contains("[1, 2]"));
        }

        @Test
        @DisplayName("Should generate meaningful toString for EventResponseDTO")
        void shouldGenerateMeaningfulToStringForEventResponseDTO() {
            EventResponseDTO dto = new EventResponseDTO(
                    1L,
                    "Meeting",
                    "Description",
                    EventType.Outing,
                    LocalDate.of(2024, 12, 25),
                    Arrays.asList(1L, 2L),
                    LocalDate.of(2024, 12, 20)
            );

            String toString = dto.toString();

            assertTrue(toString.contains("1"));
            assertTrue(toString.contains("Meeting"));
            assertTrue(toString.contains("Description"));
            assertTrue(toString.contains("Outing"));
            assertTrue(toString.contains("2024-12-25"));
            assertTrue(toString.contains("2024-12-20"));
        }

        @Test
        @DisplayName("Should generate meaningful toString for EventUpdateDTO")
        void shouldGenerateMeaningfulToStringForEventUpdateDTO() {
            EventUpdateDTO dto = new EventUpdateDTO(
                    "Meeting",
                    "Description",
                    EventType.Outing,
                    LocalDate.of(2024, 12, 25),
                    Arrays.asList(1L, 2L)
            );

            String toString = dto.toString();

            assertTrue(toString.contains("Meeting"));
            assertTrue(toString.contains("Description"));
            assertTrue(toString.contains("Outing"));
            assertTrue(toString.contains("2024-12-25"));
            assertTrue(toString.contains("[1, 2]"));
        }
    }
}
