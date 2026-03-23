package com.arena.ai.service;

import com.arena.ai.dto.HintRequest;
import com.arena.common.dto.HintResponseDTO;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class HintService {

    private final ChatClient chatClient;

    @CircuitBreaker(name = "aiService", fallbackMethod = "getHintFallback")
    @Retry(name = "aiService")
    public HintResponseDTO generateHint(HintRequest request) {
        log.info("Generating hint for problem, level: {}", request.getHintLevel());

        String prompt = buildHintPrompt(request);

        String hint = chatClient.prompt()
                .user(prompt)
                .call()
                .content();

        return HintResponseDTO.builder()
                .hint(hint.trim())
                .hintLevel(request.getHintLevel())
                .build();
    }

    private String buildHintPrompt(HintRequest request) {
        return switch (request.getHintLevel()) {
            case 1 -> buildLevel1Prompt(request);
            case 2 -> buildLevel2Prompt(request);
            case 3 -> buildLevel3Prompt(request);
            default -> buildLevel1Prompt(request);
        };
    }

    /**
     * Level 1 — Pure intuition trigger.
     * Make them feel the pattern without naming it.
     */
    private String buildLevel1Prompt(HintRequest request) {
        StringBuilder p = new StringBuilder();
        p.append("You are an expert competitive programmer helping a student develop intuition.\n\n");
        p.append("Problem:\n").append(request.getProblemDescription()).append("\n\n");

        if (hasCode(request)) {
            p.append("Their current code (").append(request.getProgrammingLanguage()).append("):\n");
            p.append(request.getCurrentCode()).append("\n\n");
        }

        p.append("Give a LEVEL 1 intuition hint.\n\n");
        p.append("Your goal: make them feel what kind of problem this is without naming the algorithm.\n\n");
        p.append("Rules:\n");
        p.append("- 2-3 sentences MAX\n");
        p.append("- Ask a guiding question that triggers the right thought\n");
        p.append("- Focus on: what changes as you move through the input? what do you need to remember?\n");
        p.append("- Do NOT name any algorithm, data structure, or technique\n");
        p.append("- Do NOT give steps or code\n");
        p.append("- Tone: calm, direct, like a senior dev nudging you\n\n");
        p.append("Example for a DP problem: \"What if solving for position i depended only on a few previous positions? ");
        p.append("What is the minimum information you need to carry forward?\"\n\n");
        p.append("Example for sliding window: \"As you extend one end of a window, what do you need to remove from the other end to keep it valid?\"\n\n");
        p.append("Now give the hint for this specific problem. Output only the hint, nothing else.");

        return p.toString();
    }

    /**
     * Level 2 — Name the approach and explain the thinking framework.
     * DP → talk states. Graph → talk traversal. Greedy → talk invariant.
     */
    private String buildLevel2Prompt(HintRequest request) {
        StringBuilder p = new StringBuilder();
        p.append("You are an expert competitive programmer helping a student understand the approach.\n\n");
        p.append("Problem:\n").append(request.getProblemDescription()).append("\n\n");

        if (hasCode(request)) {
            p.append("Their current code (").append(request.getProgrammingLanguage()).append("):\n");
            p.append(request.getCurrentCode()).append("\n\n");
        }

        p.append("Give a LEVEL 2 approach hint.\n\n");
        p.append("Your goal: name the technique and explain HOW to think about it for this specific problem.\n\n");
        p.append("Rules:\n");
        p.append("- 3-4 sentences MAX\n");
        p.append("- Name the algorithm/technique (DP, two pointers, BFS, greedy, etc.)\n");
        p.append("- If it's DP: define what the STATE represents (dp[i] means...)\n");
        p.append("- If it's graph: explain what nodes/edges represent\n");
        p.append("- If it's greedy: explain the invariant you're maintaining\n");
        p.append("- If it's divide & conquer: explain what you split on\n");
        p.append("- End with ONE sentence about the transition or key operation\n");
        p.append("- No code, no full solution\n\n");
        p.append("Example for DP: \"This is a DP problem. Let dp[i] = minimum cost to reach index i. ");
        p.append("At each step, you can come from the previous 1 or 2 positions. ");
        p.append("The transition is: dp[i] = min(dp[i-1], dp[i-2]) + cost[i].\"\n\n");
        p.append("Example for two pointers: \"Use two pointers — one at each end. ");
        p.append("The key insight is that moving the pointer at the smaller value inward can only increase the area. ");
        p.append("So always move the smaller one.\"\n\n");
        p.append("Now give the hint for this specific problem. Output only the hint, nothing else.");

        return p.toString();
    }

    /**
     * Level 3 — Step by step intuition guide.
     * 3 precise bullets that walk through the full mental model.
     * Enough to implement, but no actual code.
     */
    private String buildLevel3Prompt(HintRequest request) {
        StringBuilder p = new StringBuilder();
        p.append("You are an expert competitive programmer giving a detailed approach walkthrough.\n\n");
        p.append("Problem:\n").append(request.getProblemDescription()).append("\n\n");

        if (hasCode(request)) {
            p.append("Their current code (").append(request.getProgrammingLanguage()).append("):\n");
            p.append(request.getCurrentCode()).append("\n\n");
        }

        p.append("Give a LEVEL 3 step-by-step intuition hint.\n\n");
        p.append("Your goal: give them the complete mental model to implement the solution themselves.\n\n");
        p.append("Rules:\n");
        p.append("- Exactly 3 bullet points\n");
        p.append("- Each bullet is ONE precise step — not vague, not a full paragraph\n");
        p.append("- Step 1: How to think about / model the problem (states, structure, observation)\n");
        p.append("- Step 2: The core algorithm or recurrence (with variable names if needed)\n");
        p.append("- Step 3: How to handle edge cases or extract the final answer\n");
        p.append("- No actual code, no imports, no method signatures\n");
        p.append("- Each bullet must be actionable — they should be able to code from this\n\n");
        p.append("Example for coin change DP:\n");
        p.append("• Define dp[i] = minimum coins needed to make amount i. Initialize dp[0]=0, rest as infinity.\n");
        p.append("• For each amount from 1 to target, try every coin: dp[i] = min(dp[i], dp[i - coin] + 1) if i >= coin.\n");
        p.append("• Return dp[target] if it's not infinity, else return -1.\n\n");
        p.append("Example for longest common subsequence:\n");
        p.append("• Let dp[i][j] = LCS length of first i chars of s1 and first j chars of s2.\n");
        p.append("• If s1[i]==s2[j]: dp[i][j] = dp[i-1][j-1] + 1. Else: dp[i][j] = max(dp[i-1][j], dp[i][j-1]).\n");
        p.append("• Answer is dp[m][n]. Build the table row by row.\n\n");
        p.append("Now give the hint for this specific problem. Output only the 3 bullet points, nothing else.");

        return p.toString();
    }

    private boolean hasCode(HintRequest request) {
        return request.getCurrentCode() != null && !request.getCurrentCode().isBlank();
    }

    public HintResponseDTO getHintFallback(HintRequest request, Throwable t) {
        log.warn("AI service unavailable, using fallback hint: {}", t.getMessage());

        String fallbackHint = switch (request.getHintLevel()) {
            case 1 -> "What pattern do you notice in the input? What's the minimum information you need to track as you process it?";
            case 2 -> "Think about what changes and what stays the same as you iterate. Which data structure lets you query or update that efficiently?";
            case 3 -> "• Identify what you need to track and define your data structure or state clearly\n"
                    + "• Write the core logic: how does each element affect your running answer?\n"
                    + "• Handle edge cases: empty input, single element, overflow, or no valid answer.";
            default -> "Break the problem into smaller parts and think about what each step needs from the previous one.";
        };

        return HintResponseDTO.builder()
                .hint(fallbackHint)
                .hintLevel(request.getHintLevel())
                .build();
    }
}