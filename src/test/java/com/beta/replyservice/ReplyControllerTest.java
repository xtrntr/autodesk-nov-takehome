package com.beta.replyservice;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.stream.Stream;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ReplyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // v1
    @Test
    public void testReplyingNoMessage() throws Exception {
        mockMvc.perform(get("/reply")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Message is empty")));
    }

    @Test
    public void testReplyingWithMessage() throws Exception {
        mockMvc.perform(get("/reply/hello")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("hello")));
    }

    // v2
    @ParameterizedTest
    @MethodSource("validRules")
    public void testValidV2Reply(String validRule, String expectedData) throws Exception {
        mockMvc.perform(get(String.format("/v2/reply/%s", validRule))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is(expectedData)));
    }

    private static Stream<Arguments> validRules() {
        return Stream.of(
                Arguments.of("1----", "---"),
                Arguments.of("11----", "---"),
                Arguments.of("11-kbzw9ru", "kbzw9ru"),
                Arguments.of("12-kbzw9ru", "5a8973b3b1fafaeaadf10e195c6e1dd4"),
                Arguments.of("22-kbzw9ru", "e8501e64cf0a9fa45e3c25aa9e77ffd5")
        );
    }

    @ParameterizedTest
    @MethodSource("invalidFormat")
    public void testInvalidFormatV2Reply(String invalidFormat) throws Exception {
        mockMvc.perform(get(String.format("/v2/reply/%s", invalidFormat))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid format. Expected {rule}-{string}."));
    }

    private static Stream<Arguments> invalidFormat() {
        return Stream.of(
                Arguments.of("---"),
                Arguments.of("abv")
        );
    }

    @ParameterizedTest
    @MethodSource("invalidRules")
    public void testInvalidRuleV2Reply(String invalidRule) throws Exception {
        mockMvc.perform(get(String.format("/v2/reply/%s", invalidRule))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(String.format("Invalid rule: %s. Only 1 and 2 are valid inputs for a rule.", invalidRule.split("-", 2)[0])));
    }

    private static Stream<Arguments> invalidRules() {
        return Stream.of(
                Arguments.of("13-ab"),
                Arguments.of("1a2-ab")
        );
    }
}