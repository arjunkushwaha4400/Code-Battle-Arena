package com.arena.battle.config;

import com.arena.battle.entity.Problem;
import com.arena.battle.entity.TestCase;
import com.arena.battle.repository.ProblemRepository;
import com.arena.common.enums.Difficulty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements ApplicationListener<ContextRefreshedEvent> {

    private final ProblemRepository problemRepository;

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (problemRepository.count() == 0) {
            log.info("Seeding problems into database...");
            seedProblems();
            log.info("Done seeding {} problems.", problemRepository.count());
        } else {
            log.info("Problems already exist, skipping seed.");
        }
    }

    private void seedProblems() {

        // =====================================================================
        // MEDIUM PROBLEMS (10)
        // =====================================================================

        // M1 - Longest Substring Without Repeating Characters
        Problem m1 = Problem.builder()
                .title("Longest Substring Without Repeating Characters")
                .difficulty(Difficulty.MEDIUM)
                .description("""
                        Given a string s, find the length of the longest substring without repeating characters.

                        **Example 1:**
                        Input: abcabcbb
                        Output: 3
                        Explanation: The answer is "abc", with length 3.

                        **Example 2:**
                        Input: bbbbb
                        Output: 1

                        **Example 3:**
                        Input: pwwkew
                        Output: 3
                        Explanation: The answer is "wke".""")
                .timeLimitSeconds(5)
                .memoryLimitMb(256)
                .inputFormat("A single line containing string s.")
                .outputFormat("A single integer — the length of the longest substring without repeating characters.")
                .constraints("0 <= s.length <= 5 * 10^4\ns consists of English letters, digits, symbols and spaces.")
                .starterCodeJava("""
                        import java.util.*;
                        public class Main {
                            public static int lengthOfLongestSubstring(String s) {
                                // Your code here
                                return 0;
                            }
                            public static void main(String[] args) {
                                Scanner sc = new Scanner(System.in);
                                String s = sc.nextLine();
                                System.out.println(lengthOfLongestSubstring(s));
                            }
                        }""")
                .starterCodePython("""
                        def length_of_longest_substring(s):
                            # Your code here
                            return 0
                        if __name__ == "__main__":
                            s = input()
                            print(length_of_longest_substring(s))""")
                .starterCodeJavascript("""
                        function lengthOfLongestSubstring(s) {
                            // Your code here
                            return 0;
                        }
                        const readline = require("readline");
                        const rl = readline.createInterface({ input: process.stdin });
                        rl.on("line", (s) => { console.log(lengthOfLongestSubstring(s)); rl.close(); });""")
                .build();
        m1.addTestCase(TestCase.builder().input("abcabcbb").expectedOutput("3").isHidden(false).orderIndex(0).explanation("abc is the longest").build());
        m1.addTestCase(TestCase.builder().input("bbbbb").expectedOutput("1").isHidden(false).orderIndex(1).explanation("single b").build());
        m1.addTestCase(TestCase.builder().input("pwwkew").expectedOutput("3").isHidden(false).orderIndex(2).explanation("wke").build());
        m1.addTestCase(TestCase.builder().input("a").expectedOutput("1").isHidden(true).orderIndex(3).explanation("single char").build());
        m1.addTestCase(TestCase.builder().input("aab").expectedOutput("2").isHidden(true).orderIndex(4).explanation("ab is longest").build());
        problemRepository.save(m1);

        // M2 - 3Sum
        Problem m2 = Problem.builder()
                .title("3Sum")
                .difficulty(Difficulty.MEDIUM)
                .description("""
                        Given an integer array nums, return all triplets [nums[i], nums[j], nums[k]] such that
                        i != j, i != k, j != k, and nums[i] + nums[j] + nums[k] == 0.

                        The solution set must not contain duplicate triplets.
                        Print each triplet in non-decreasing order, triplets sorted lexicographically.

                        **Example 1:**
                        Input: -1 0 1 2 -1 -4
                        Output:
                        -1 -1 2
                        -1 0 1

                        **Example 2:**
                        Input: 0 1 1
                        Output: (empty)

                        **Example 3:**
                        Input: 0 0 0
                        Output:
                        0 0 0""")
                .timeLimitSeconds(5).memoryLimitMb(256)
                .inputFormat("A single line of space-separated integers.")
                .outputFormat("Each triplet on its own line with values space-separated in non-decreasing order.")
                .constraints("3 <= nums.length <= 3000\n-10^5 <= nums[i] <= 10^5")
                .starterCodeJava("""
                        import java.util.*;
                        public class Main {
                            public static List<List<Integer>> threeSum(int[] nums) {
                                // Your code here
                                return new ArrayList<>();
                            }
                            public static void main(String[] args) {
                                Scanner sc = new Scanner(System.in);
                                String[] parts = sc.nextLine().split(" ");
                                int[] nums = new int[parts.length];
                                for (int i = 0; i < parts.length; i++) nums[i] = Integer.parseInt(parts[i]);
                                List<List<Integer>> result = threeSum(nums);
                                for (List<Integer> t : result)
                                    System.out.println(t.get(0) + " " + t.get(1) + " " + t.get(2));
                            }
                        }""")
                .starterCodePython("""
                        def three_sum(nums):
                            # Your code here
                            return []
                        if __name__ == "__main__":
                            nums = list(map(int, input().split()))
                            result = three_sum(nums)
                            for t in result:
                                print(*t)""")
                .starterCodeJavascript("""
                        function threeSum(nums) {
                            // Your code here
                            return [];
                        }
                        const readline = require("readline");
                        const rl = readline.createInterface({ input: process.stdin });
                        rl.on("line", (line) => {
                            const nums = line.split(" ").map(Number);
                            threeSum(nums).forEach(t => console.log(t.join(" ")));
                            rl.close();
                        });""")
                .build();
        m2.addTestCase(TestCase.builder().input("-1 0 1 2 -1 -4").expectedOutput("-1 -1 2\n-1 0 1").isHidden(false).orderIndex(0).explanation("Two valid triplets").build());
        m2.addTestCase(TestCase.builder().input("0 1 1").expectedOutput("").isHidden(false).orderIndex(1).explanation("No triplet sums to 0").build());
        m2.addTestCase(TestCase.builder().input("0 0 0").expectedOutput("0 0 0").isHidden(false).orderIndex(2).explanation("All zeros").build());
        m2.addTestCase(TestCase.builder().input("-2 0 0 2 2").expectedOutput("-2 0 2").isHidden(true).orderIndex(3).explanation("Deduplicated").build());
        m2.addTestCase(TestCase.builder().input("-4 -1 -1 0 1 2").expectedOutput("-1 -1 2\n-1 0 1").isHidden(true).orderIndex(4).explanation("Sorted input").build());
        problemRepository.save(m2);

        // M3 - Product of Array Except Self
        Problem m3 = Problem.builder()
                .title("Product of Array Except Self")
                .difficulty(Difficulty.MEDIUM)
                .description("""
                        Given an integer array nums, return an array answer such that answer[i] is equal to
                        the product of all elements of nums except nums[i].

                        You must solve it in O(n) time without using the division operation.

                        **Example 1:**
                        Input: 1 2 3 4
                        Output: 24 12 8 6

                        **Example 2:**
                        Input: -1 1 0 -3 3
                        Output: 0 0 9 0 0""")
                .timeLimitSeconds(5).memoryLimitMb(256)
                .inputFormat("A single line of space-separated integers.")
                .outputFormat("Space-separated integers representing the answer array.")
                .constraints("2 <= nums.length <= 10^5\n-30 <= nums[i] <= 30")
                .starterCodeJava("""
                        import java.util.*;
                        public class Main {
                            public static int[] productExceptSelf(int[] nums) {
                                // Your code here
                                return new int[]{};
                            }
                            public static void main(String[] args) {
                                Scanner sc = new Scanner(System.in);
                                int[] nums = Arrays.stream(sc.nextLine().split(" ")).mapToInt(Integer::parseInt).toArray();
                                int[] res = productExceptSelf(nums);
                                StringBuilder sb = new StringBuilder();
                                for (int i = 0; i < res.length; i++) { if (i > 0) sb.append(" "); sb.append(res[i]); }
                                System.out.println(sb);
                            }
                        }""")
                .starterCodePython("""
                        def product_except_self(nums):
                            # Your code here
                            return []
                        if __name__ == "__main__":
                            nums = list(map(int, input().split()))
                            print(*product_except_self(nums))""")
                .starterCodeJavascript("""
                        function productExceptSelf(nums) {
                            // Your code here
                            return [];
                        }
                        const readline = require("readline");
                        const rl = readline.createInterface({ input: process.stdin });
                        rl.on("line", (line) => {
                            console.log(productExceptSelf(line.split(" ").map(Number)).join(" "));
                            rl.close();
                        });""")
                .build();
        m3.addTestCase(TestCase.builder().input("1 2 3 4").expectedOutput("24 12 8 6").isHidden(false).orderIndex(0).explanation("Basic case").build());
        m3.addTestCase(TestCase.builder().input("-1 1 0 -3 3").expectedOutput("0 0 9 0 0").isHidden(false).orderIndex(1).explanation("Contains zero").build());
        m3.addTestCase(TestCase.builder().input("2 3").expectedOutput("3 2").isHidden(false).orderIndex(2).explanation("Two elements").build());
        m3.addTestCase(TestCase.builder().input("1 0 0 2").expectedOutput("0 0 0 0").isHidden(true).orderIndex(3).explanation("Two zeros").build());
        m3.addTestCase(TestCase.builder().input("-1 -1 -1 -1").expectedOutput("-1 -1 -1 -1").isHidden(true).orderIndex(4).explanation("All negatives").build());
        problemRepository.save(m3);

        // M4 - Container With Most Water
        Problem m4 = Problem.builder()
                .title("Container With Most Water")
                .difficulty(Difficulty.MEDIUM)
                .description("""
                        You are given an integer array height of length n. Find two lines that together with
                        the x-axis form a container holding the most water.

                        Return the maximum amount of water the container can store.

                        **Example 1:**
                        Input: 1 8 6 2 5 4 8 3 7
                        Output: 49

                        **Example 2:**
                        Input: 1 1
                        Output: 1""")
                .timeLimitSeconds(5).memoryLimitMb(256)
                .inputFormat("A single line of space-separated integers representing heights.")
                .outputFormat("A single integer — the maximum water the container can store.")
                .constraints("2 <= n <= 10^5\n0 <= height[i] <= 10^4")
                .starterCodeJava("""
                        import java.util.*;
                        public class Main {
                            public static int maxArea(int[] height) {
                                // Your code here
                                return 0;
                            }
                            public static void main(String[] args) {
                                Scanner sc = new Scanner(System.in);
                                int[] height = Arrays.stream(sc.nextLine().split(" ")).mapToInt(Integer::parseInt).toArray();
                                System.out.println(maxArea(height));
                            }
                        }""")
                .starterCodePython("""
                        def max_area(height):
                            # Your code here
                            return 0
                        if __name__ == "__main__":
                            height = list(map(int, input().split()))
                            print(max_area(height))""")
                .starterCodeJavascript("""
                        function maxArea(height) {
                            // Your code here
                            return 0;
                        }
                        const readline = require("readline");
                        const rl = readline.createInterface({ input: process.stdin });
                        rl.on("line", (line) => { console.log(maxArea(line.split(" ").map(Number))); rl.close(); });""")
                .build();
        m4.addTestCase(TestCase.builder().input("1 8 6 2 5 4 8 3 7").expectedOutput("49").isHidden(false).orderIndex(0).explanation("Classic case").build());
        m4.addTestCase(TestCase.builder().input("1 1").expectedOutput("1").isHidden(false).orderIndex(1).explanation("Minimal").build());
        m4.addTestCase(TestCase.builder().input("4 3 2 1 4").expectedOutput("16").isHidden(false).orderIndex(2).explanation("Symmetric").build());
        m4.addTestCase(TestCase.builder().input("1 2 1").expectedOutput("2").isHidden(true).orderIndex(3).explanation("Three elements").build());
        m4.addTestCase(TestCase.builder().input("2 3 4 5 18 17 6").expectedOutput("17").isHidden(true).orderIndex(4).explanation("Max not at ends").build());
        problemRepository.save(m4);

        // M5 - Jump Game
        Problem m5 = Problem.builder()
                .title("Jump Game")
                .difficulty(Difficulty.MEDIUM)
                .description("""
                        You are given an integer array nums. You are initially positioned at the first index.
                        Each element represents your maximum jump length at that position.

                        Return true if you can reach the last index, or false otherwise.

                        **Example 1:**
                        Input: 2 3 1 1 4
                        Output: true

                        **Example 2:**
                        Input: 3 2 1 0 4
                        Output: false
                        Explanation: You always arrive at index 3 which has jump length 0.""")
                .timeLimitSeconds(5).memoryLimitMb(256)
                .inputFormat("A single line of space-separated integers.")
                .outputFormat("true or false")
                .constraints("1 <= nums.length <= 10^4\n0 <= nums[i] <= 10^5")
                .starterCodeJava("""
                        import java.util.*;
                        public class Main {
                            public static boolean canJump(int[] nums) {
                                // Your code here
                                return false;
                            }
                            public static void main(String[] args) {
                                Scanner sc = new Scanner(System.in);
                                int[] nums = Arrays.stream(sc.nextLine().split(" ")).mapToInt(Integer::parseInt).toArray();
                                System.out.println(canJump(nums));
                            }
                        }""")
                .starterCodePython("""
                        def can_jump(nums):
                            # Your code here
                            return False
                        if __name__ == "__main__":
                            nums = list(map(int, input().split()))
                            print("true" if can_jump(nums) else "false")""")
                .starterCodeJavascript("""
                        function canJump(nums) {
                            // Your code here
                            return false;
                        }
                        const readline = require("readline");
                        const rl = readline.createInterface({ input: process.stdin });
                        rl.on("line", (line) => { console.log(canJump(line.split(" ").map(Number))); rl.close(); });""")
                .build();
        m5.addTestCase(TestCase.builder().input("2 3 1 1 4").expectedOutput("true").isHidden(false).orderIndex(0).explanation("Can reach end").build());
        m5.addTestCase(TestCase.builder().input("3 2 1 0 4").expectedOutput("false").isHidden(false).orderIndex(1).explanation("Stuck at zero").build());
        m5.addTestCase(TestCase.builder().input("0").expectedOutput("true").isHidden(false).orderIndex(2).explanation("Already at end").build());
        m5.addTestCase(TestCase.builder().input("1 0 0 0").expectedOutput("false").isHidden(true).orderIndex(3).explanation("Cannot pass zero").build());
        m5.addTestCase(TestCase.builder().input("5 0 0 0 0 0").expectedOutput("true").isHidden(true).orderIndex(4).explanation("Big first jump").build());
        problemRepository.save(m5);

        // M6 - Longest Palindromic Substring
        Problem m6 = Problem.builder()
                .title("Longest Palindromic Substring")
                .difficulty(Difficulty.MEDIUM)
                .description("""
                        Given a string s, return the longest palindromic substring in s.
                        If multiple answers have the same length, return the one that starts earliest.

                        **Example 1:**
                        Input: babad
                        Output: bab

                        **Example 2:**
                        Input: cbbd
                        Output: bb

                        **Example 3:**
                        Input: racecar
                        Output: racecar""")
                .timeLimitSeconds(5).memoryLimitMb(256)
                .inputFormat("A single line containing string s.")
                .outputFormat("The longest palindromic substring.")
                .constraints("1 <= s.length <= 1000\ns consists of only digits and English letters.")
                .starterCodeJava("""
                        import java.util.*;
                        public class Main {
                            public static String longestPalindrome(String s) {
                                // Your code here
                                return "";
                            }
                            public static void main(String[] args) {
                                Scanner sc = new Scanner(System.in);
                                System.out.println(longestPalindrome(sc.nextLine()));
                            }
                        }""")
                .starterCodePython("""
                        def longest_palindrome(s):
                            # Your code here
                            return ""
                        if __name__ == "__main__":
                            print(longest_palindrome(input()))""")
                .starterCodeJavascript("""
                        function longestPalindrome(s) {
                            // Your code here
                            return "";
                        }
                        const readline = require("readline");
                        const rl = readline.createInterface({ input: process.stdin });
                        rl.on("line", (s) => { console.log(longestPalindrome(s)); rl.close(); });""")
                .build();
        m6.addTestCase(TestCase.builder().input("babad").expectedOutput("bab").isHidden(false).orderIndex(0).explanation("First longest").build());
        m6.addTestCase(TestCase.builder().input("cbbd").expectedOutput("bb").isHidden(false).orderIndex(1).explanation("Even palindrome").build());
        m6.addTestCase(TestCase.builder().input("racecar").expectedOutput("racecar").isHidden(false).orderIndex(2).explanation("Whole string").build());
        m6.addTestCase(TestCase.builder().input("a").expectedOutput("a").isHidden(true).orderIndex(3).explanation("Single char").build());
        m6.addTestCase(TestCase.builder().input("aacabdkacaa").expectedOutput("aca").isHidden(true).orderIndex(4).explanation("Multiple candidates").build());
        problemRepository.save(m6);

        // M7 - Search in Rotated Sorted Array
        Problem m7 = Problem.builder()
                .title("Search in Rotated Sorted Array")
                .difficulty(Difficulty.MEDIUM)
                .description("""
                        An integer array nums sorted in ascending order is possibly rotated at an unknown pivot.
                        Given the array and an integer target, return the index of target if it is in nums, or -1 if not.

                        You must write an algorithm with O(log n) runtime.

                        **Example 1:**
                        Input:
                        4 5 6 7 0 1 2
                        0
                        Output: 4

                        **Example 2:**
                        Input:
                        4 5 6 7 0 1 2
                        3
                        Output: -1""")
                .timeLimitSeconds(5).memoryLimitMb(256)
                .inputFormat("First line: space-separated integers (the rotated array).\nSecond line: the target integer.")
                .outputFormat("A single integer — the index of target, or -1 if not found.")
                .constraints("1 <= nums.length <= 5000\n-10^4 <= nums[i] <= 10^4\nAll values in nums are unique.")
                .starterCodeJava("""
                        import java.util.*;
                        public class Main {
                            public static int search(int[] nums, int target) {
                                // Your code here
                                return -1;
                            }
                            public static void main(String[] args) {
                                Scanner sc = new Scanner(System.in);
                                int[] nums = Arrays.stream(sc.nextLine().split(" ")).mapToInt(Integer::parseInt).toArray();
                                int target = Integer.parseInt(sc.nextLine().trim());
                                System.out.println(search(nums, target));
                            }
                        }""")
                .starterCodePython("""
                        def search(nums, target):
                            # Your code here
                            return -1
                        if __name__ == "__main__":
                            nums = list(map(int, input().split()))
                            target = int(input())
                            print(search(nums, target))""")
                .starterCodeJavascript("""
                        function search(nums, target) {
                            // Your code here
                            return -1;
                        }
                        const readline = require("readline");
                        const rl = readline.createInterface({ input: process.stdin });
                        const lines = [];
                        rl.on("line", l => lines.push(l.trim()));
                        rl.on("close", () => { console.log(search(lines[0].split(" ").map(Number), parseInt(lines[1]))); });""")
                .build();
        m7.addTestCase(TestCase.builder().input("4 5 6 7 0 1 2\n0").expectedOutput("4").isHidden(false).orderIndex(0).explanation("Found after pivot").build());
        m7.addTestCase(TestCase.builder().input("4 5 6 7 0 1 2\n3").expectedOutput("-1").isHidden(false).orderIndex(1).explanation("Not found").build());
        m7.addTestCase(TestCase.builder().input("1\n0").expectedOutput("-1").isHidden(false).orderIndex(2).explanation("Single element miss").build());
        m7.addTestCase(TestCase.builder().input("3 1\n1").expectedOutput("1").isHidden(true).orderIndex(3).explanation("Two element rotated").build());
        m7.addTestCase(TestCase.builder().input("6 7 8 1 2 3 4 5\n8").expectedOutput("2").isHidden(true).orderIndex(4).explanation("Found before pivot").build());
        problemRepository.save(m7);

        // M8 - Decode Ways
        Problem m8 = Problem.builder()
                .title("Decode Ways")
                .difficulty(Difficulty.MEDIUM)
                .description("""
                        A message containing letters A-Z can be encoded: A=1, B=2, ..., Z=26.

                        Given a string s of digits, return the number of ways to decode it.

                        **Example 1:**
                        Input: 12
                        Output: 2
                        Explanation: "12" can be "AB" (1 2) or "L" (12).

                        **Example 2:**
                        Input: 226
                        Output: 3

                        **Example 3:**
                        Input: 06
                        Output: 0""")
                .timeLimitSeconds(5).memoryLimitMb(256)
                .inputFormat("A single line containing the string s (digits only).")
                .outputFormat("A single integer — the number of ways to decode the string.")
                .constraints("1 <= s.length <= 100\ns contains only digits.")
                .starterCodeJava("""
                        import java.util.*;
                        public class Main {
                            public static int numDecodings(String s) {
                                // Your code here
                                return 0;
                            }
                            public static void main(String[] args) {
                                Scanner sc = new Scanner(System.in);
                                System.out.println(numDecodings(sc.nextLine().trim()));
                            }
                        }""")
                .starterCodePython("""
                        def num_decodings(s):
                            # Your code here
                            return 0
                        if __name__ == "__main__":
                            print(num_decodings(input().strip()))""")
                .starterCodeJavascript("""
                        function numDecodings(s) {
                            // Your code here
                            return 0;
                        }
                        const readline = require("readline");
                        const rl = readline.createInterface({ input: process.stdin });
                        rl.on("line", (s) => { console.log(numDecodings(s.trim())); rl.close(); });""")
                .build();
        m8.addTestCase(TestCase.builder().input("12").expectedOutput("2").isHidden(false).orderIndex(0).explanation("AB or L").build());
        m8.addTestCase(TestCase.builder().input("226").expectedOutput("3").isHidden(false).orderIndex(1).explanation("Three ways").build());
        m8.addTestCase(TestCase.builder().input("06").expectedOutput("0").isHidden(false).orderIndex(2).explanation("Leading zero invalid").build());
        m8.addTestCase(TestCase.builder().input("11106").expectedOutput("2").isHidden(true).orderIndex(3).explanation("Mixed zeros").build());
        m8.addTestCase(TestCase.builder().input("1").expectedOutput("1").isHidden(true).orderIndex(4).explanation("Single digit").build());
        problemRepository.save(m8);

        // M9 - Coin Change
        Problem m9 = Problem.builder()
                .title("Coin Change")
                .difficulty(Difficulty.MEDIUM)
                .description("""
                        You are given an integer array coins representing different denominations and an integer amount.

                        Return the fewest number of coins needed to make up that amount. If impossible, return -1.

                        **Example 1:**
                        Input:
                        1 5 11
                        11
                        Output: 1

                        **Example 2:**
                        Input:
                        1 2 5
                        11
                        Output: 3

                        **Example 3:**
                        Input:
                        2
                        3
                        Output: -1""")
                .timeLimitSeconds(5).memoryLimitMb(256)
                .inputFormat("First line: space-separated coin denominations.\nSecond line: the target amount.")
                .outputFormat("A single integer — minimum coins needed, or -1 if impossible.")
                .constraints("1 <= coins.length <= 12\n1 <= coins[i] <= 2^31 - 1\n0 <= amount <= 10^4")
                .starterCodeJava("""
                        import java.util.*;
                        public class Main {
                            public static int coinChange(int[] coins, int amount) {
                                // Your code here
                                return -1;
                            }
                            public static void main(String[] args) {
                                Scanner sc = new Scanner(System.in);
                                int[] coins = Arrays.stream(sc.nextLine().split(" ")).mapToInt(Integer::parseInt).toArray();
                                int amount = Integer.parseInt(sc.nextLine().trim());
                                System.out.println(coinChange(coins, amount));
                            }
                        }""")
                .starterCodePython("""
                        def coin_change(coins, amount):
                            # Your code here
                            return -1
                        if __name__ == "__main__":
                            coins = list(map(int, input().split()))
                            amount = int(input())
                            print(coin_change(coins, amount))""")
                .starterCodeJavascript("""
                        function coinChange(coins, amount) {
                            // Your code here
                            return -1;
                        }
                        const readline = require("readline");
                        const rl = readline.createInterface({ input: process.stdin });
                        const lines = [];
                        rl.on("line", l => lines.push(l.trim()));
                        rl.on("close", () => { console.log(coinChange(lines[0].split(" ").map(Number), parseInt(lines[1]))); });""")
                .build();
        m9.addTestCase(TestCase.builder().input("1 5 11\n11").expectedOutput("1").isHidden(false).orderIndex(0).explanation("Single coin covers it").build());
        m9.addTestCase(TestCase.builder().input("1 2 5\n11").expectedOutput("3").isHidden(false).orderIndex(1).explanation("5+5+1").build());
        m9.addTestCase(TestCase.builder().input("2\n3").expectedOutput("-1").isHidden(false).orderIndex(2).explanation("Impossible").build());
        m9.addTestCase(TestCase.builder().input("1\n0").expectedOutput("0").isHidden(true).orderIndex(3).explanation("Amount is zero").build());
        m9.addTestCase(TestCase.builder().input("3 5 7\n14").expectedOutput("2").isHidden(true).orderIndex(4).explanation("7+7").build());
        problemRepository.save(m9);

        // M10 - Binary Tree Level Order Traversal
        Problem m10 = Problem.builder()
                .title("Binary Tree Level Order Traversal")
                .difficulty(Difficulty.MEDIUM)
                .description("""
                        Given the root of a binary tree (provided in BFS order, -1 means null),
                        return the level order traversal of its node values.

                        **Example 1:**
                        Input: 3 9 20 -1 -1 15 7
                        Output:
                        3
                        9 20
                        15 7

                        **Example 2:**
                        Input: 1
                        Output:
                        1""")
                .timeLimitSeconds(5).memoryLimitMb(256)
                .inputFormat("A single line of space-separated integers in BFS order. -1 means null.")
                .outputFormat("Each level on its own line, values space-separated left to right.")
                .constraints("1 <= number of nodes <= 2000\n-1000 <= Node.val <= 1000")
                .starterCodeJava("""
                        import java.util.*;
                        public class Main {
                            static int[] tree;
                            public static List<List<Integer>> levelOrder(int rootIdx, int n) {
                                List<List<Integer>> result = new ArrayList<>();
                                if (rootIdx >= n || tree[rootIdx] == -1) return result;
                                Queue<Integer> queue = new LinkedList<>();
                                queue.offer(rootIdx);
                                while (!queue.isEmpty()) {
                                    int size = queue.size();
                                    List<Integer> level = new ArrayList<>();
                                    for (int i = 0; i < size; i++) {
                                        int idx = queue.poll();
                                        if (idx >= n || tree[idx] == -1) continue;
                                        level.add(tree[idx]);
                                        int left = 2*idx+1, right = 2*idx+2;
                                        if (left < n && tree[left] != -1) queue.offer(left);
                                        if (right < n && tree[right] != -1) queue.offer(right);
                                    }
                                    if (!level.isEmpty()) result.add(level);
                                }
                                return result;
                            }
                            public static void main(String[] args) {
                                Scanner sc = new Scanner(System.in);
                                String[] parts = sc.nextLine().split(" ");
                                tree = new int[parts.length];
                                for (int i = 0; i < parts.length; i++) tree[i] = Integer.parseInt(parts[i]);
                                List<List<Integer>> result = levelOrder(0, parts.length);
                                for (List<Integer> level : result) {
                                    StringBuilder sb = new StringBuilder();
                                    for (int i = 0; i < level.size(); i++) { if (i > 0) sb.append(" "); sb.append(level.get(i)); }
                                    System.out.println(sb);
                                }
                            }
                        }""")
                .starterCodePython("""
                        from collections import deque
                        def level_order(tree):
                            if not tree or tree[0] == -1: return []
                            result, queue, n = [], deque([0]), len(tree)
                            while queue:
                                level_vals = []
                                for _ in range(len(queue)):
                                    idx = queue.popleft()
                                    if idx >= n or tree[idx] == -1: continue
                                    level_vals.append(tree[idx])
                                    l, r = 2*idx+1, 2*idx+2
                                    if l < n and tree[l] != -1: queue.append(l)
                                    if r < n and tree[r] != -1: queue.append(r)
                                if level_vals: result.append(level_vals)
                            return result
                        if __name__ == "__main__":
                            tree = list(map(int, input().split()))
                            for level in level_order(tree): print(*level)""")
                .starterCodeJavascript("""
                        function levelOrder(tree) {
                            if (!tree.length || tree[0] === -1) return [];
                            const result = [], queue = [0], n = tree.length;
                            while (queue.length) {
                                const size = queue.length, level = [];
                                for (let i = 0; i < size; i++) {
                                    const idx = queue.shift();
                                    if (idx >= n || tree[idx] === -1) continue;
                                    level.push(tree[idx]);
                                    const l = 2*idx+1, r = 2*idx+2;
                                    if (l < n && tree[l] !== -1) queue.push(l);
                                    if (r < n && tree[r] !== -1) queue.push(r);
                                }
                                if (level.length) result.push(level);
                            }
                            return result;
                        }
                        const readline = require("readline");
                        const rl = readline.createInterface({ input: process.stdin });
                        rl.on("line", (line) => {
                            levelOrder(line.split(" ").map(Number)).forEach(l => console.log(l.join(" ")));
                            rl.close();
                        });""")
                .build();
        m10.addTestCase(TestCase.builder().input("3 9 20 -1 -1 15 7").expectedOutput("3\n9 20\n15 7").isHidden(false).orderIndex(0).explanation("Three levels").build());
        m10.addTestCase(TestCase.builder().input("1").expectedOutput("1").isHidden(false).orderIndex(1).explanation("Single node").build());
        m10.addTestCase(TestCase.builder().input("1 2 3 4 5").expectedOutput("1\n2 3\n4 5").isHidden(false).orderIndex(2).explanation("Complete tree").build());
        m10.addTestCase(TestCase.builder().input("1 -1 2 -1 -1 -1 3").expectedOutput("1\n2\n3").isHidden(true).orderIndex(3).explanation("Right skewed").build());
        m10.addTestCase(TestCase.builder().input("5 3 8 1 4 7 9").expectedOutput("5\n3 8\n1 4 7 9").isHidden(true).orderIndex(4).explanation("Full binary tree").build());
        problemRepository.save(m10);

        // =====================================================================
        // HARD PROBLEMS (10)
        // =====================================================================

        // H1 - Median of Two Sorted Arrays
        Problem h1 = Problem.builder()
                .title("Median of Two Sorted Arrays")
                .difficulty(Difficulty.HARD)
                .description("""
                        Given two sorted arrays nums1 and nums2, return the median of the two sorted arrays.
                        The overall run time complexity should be O(log(m+n)).

                        **Example 1:**
                        Input:
                        1 3
                        2
                        Output: 2.00000

                        **Example 2:**
                        Input:
                        1 2
                        3 4
                        Output: 2.50000""")
                .timeLimitSeconds(5).memoryLimitMb(256)
                .inputFormat("First line: space-separated integers of nums1.\nSecond line: space-separated integers of nums2.")
                .outputFormat("The median as a decimal rounded to 5 decimal places.")
                .constraints("0 <= m, n <= 1000\n1 <= m + n <= 2000\n-10^6 <= nums1[i], nums2[i] <= 10^6")
                .starterCodeJava("""
                        import java.util.*;
                        public class Main {
                            public static double findMedianSortedArrays(int[] nums1, int[] nums2) {
                                // Your code here
                                return 0.0;
                            }
                            public static void main(String[] args) {
                                Scanner sc = new Scanner(System.in);
                                int[] nums1 = Arrays.stream(sc.nextLine().split(" ")).mapToInt(Integer::parseInt).toArray();
                                int[] nums2 = Arrays.stream(sc.nextLine().split(" ")).mapToInt(Integer::parseInt).toArray();
                                System.out.printf("%.5f%n", findMedianSortedArrays(nums1, nums2));
                            }
                        }""")
                .starterCodePython("""
                        def find_median_sorted_arrays(nums1, nums2):
                            # Your code here
                            return 0.0
                        if __name__ == "__main__":
                            nums1 = list(map(int, input().split()))
                            nums2 = list(map(int, input().split()))
                            print(f"{find_median_sorted_arrays(nums1, nums2):.5f}")""")
                .starterCodeJavascript("""
                        function findMedianSortedArrays(nums1, nums2) {
                            // Your code here
                            return 0.0;
                        }
                        const readline = require("readline");
                        const rl = readline.createInterface({ input: process.stdin });
                        const lines = [];
                        rl.on("line", l => lines.push(l.trim()));
                        rl.on("close", () => {
                            console.log(findMedianSortedArrays(lines[0].split(" ").map(Number), lines[1].split(" ").map(Number)).toFixed(5));
                        });""")
                .build();
        h1.addTestCase(TestCase.builder().input("1 3\n2").expectedOutput("2.00000").isHidden(false).orderIndex(0).explanation("Odd total length").build());
        h1.addTestCase(TestCase.builder().input("1 2\n3 4").expectedOutput("2.50000").isHidden(false).orderIndex(1).explanation("Even total length").build());
        h1.addTestCase(TestCase.builder().input("0 0\n0 0").expectedOutput("0.00000").isHidden(false).orderIndex(2).explanation("All zeros").build());
        h1.addTestCase(TestCase.builder().input("1\n1").expectedOutput("1.00000").isHidden(true).orderIndex(3).explanation("Both single element").build());
        h1.addTestCase(TestCase.builder().input("1 3 5 7\n2 4 6 8").expectedOutput("4.50000").isHidden(true).orderIndex(4).explanation("Interleaved arrays").build());
        problemRepository.save(h1);

        // H2 - Trapping Rain Water
        Problem h2 = Problem.builder()
                .title("Trapping Rain Water")
                .difficulty(Difficulty.HARD)
                .description("""
                        Given n non-negative integers representing an elevation map where the width of each bar is 1,
                        compute how much water it can trap after raining.

                        **Example 1:**
                        Input: 0 1 0 2 1 0 1 3 2 1 2 1
                        Output: 6

                        **Example 2:**
                        Input: 4 2 0 3 2 5
                        Output: 9""")
                .timeLimitSeconds(5).memoryLimitMb(256)
                .inputFormat("A single line of space-separated non-negative integers representing the elevation map.")
                .outputFormat("A single integer — total units of water trapped.")
                .constraints("1 <= n <= 2 * 10^4\n0 <= height[i] <= 10^5")
                .starterCodeJava("""
                        import java.util.*;
                        public class Main {
                            public static int trap(int[] height) {
                                // Your code here
                                return 0;
                            }
                            public static void main(String[] args) {
                                Scanner sc = new Scanner(System.in);
                                int[] height = Arrays.stream(sc.nextLine().split(" ")).mapToInt(Integer::parseInt).toArray();
                                System.out.println(trap(height));
                            }
                        }""")
                .starterCodePython("""
                        def trap(height):
                            # Your code here
                            return 0
                        if __name__ == "__main__":
                            height = list(map(int, input().split()))
                            print(trap(height))""")
                .starterCodeJavascript("""
                        function trap(height) {
                            // Your code here
                            return 0;
                        }
                        const readline = require("readline");
                        const rl = readline.createInterface({ input: process.stdin });
                        rl.on("line", (line) => { console.log(trap(line.split(" ").map(Number))); rl.close(); });""")
                .build();
        h2.addTestCase(TestCase.builder().input("0 1 0 2 1 0 1 3 2 1 2 1").expectedOutput("6").isHidden(false).orderIndex(0).explanation("Classic example").build());
        h2.addTestCase(TestCase.builder().input("4 2 0 3 2 5").expectedOutput("9").isHidden(false).orderIndex(1).explanation("Valley example").build());
        h2.addTestCase(TestCase.builder().input("3 0 0 2 0 4").expectedOutput("10").isHidden(false).orderIndex(2).explanation("Large valley").build());
        h2.addTestCase(TestCase.builder().input("1 0 1").expectedOutput("1").isHidden(true).orderIndex(3).explanation("Minimal trap").build());
        h2.addTestCase(TestCase.builder().input("0 0 0 0").expectedOutput("0").isHidden(true).orderIndex(4).explanation("No water").build());
        problemRepository.save(h2);

        // H3 - Word Ladder
        Problem h3 = Problem.builder()
                .title("Word Ladder")
                .difficulty(Difficulty.HARD)
                .description("""
                        A transformation sequence from beginWord to endWord uses a dictionary.
                        Each step changes exactly one letter and the result must be in the dictionary.

                        Return the number of words in the shortest transformation sequence, or 0 if none exists.

                        **Example 1:**
                        Input:
                        hit
                        cog
                        hot dot dog lot log cog
                        Output: 5
                        Explanation: hit -> hot -> dot -> dog -> cog

                        **Example 2:**
                        Input:
                        hit
                        cog
                        hot dot dog lot log
                        Output: 0""")
                .timeLimitSeconds(10).memoryLimitMb(256)
                .inputFormat("First line: beginWord.\nSecond line: endWord.\nThird line: space-separated words in the dictionary.")
                .outputFormat("A single integer — length of shortest transformation sequence, or 0.")
                .constraints("1 <= beginWord.length <= 10\nAll words consist of lowercase English letters.")
                .starterCodeJava("""
                        import java.util.*;
                        public class Main {
                            public static int ladderLength(String beginWord, String endWord, List<String> wordList) {
                                // Your code here
                                return 0;
                            }
                            public static void main(String[] args) {
                                Scanner sc = new Scanner(System.in);
                                String beginWord = sc.nextLine().trim();
                                String endWord = sc.nextLine().trim();
                                List<String> wordList = Arrays.asList(sc.nextLine().trim().split(" "));
                                System.out.println(ladderLength(beginWord, endWord, wordList));
                            }
                        }""")
                .starterCodePython("""
                        from collections import deque
                        def ladder_length(begin_word, end_word, word_list):
                            # Your code here
                            return 0
                        if __name__ == "__main__":
                            begin = input().strip()
                            end = input().strip()
                            words = input().strip().split()
                            print(ladder_length(begin, end, words))""")
                .starterCodeJavascript("""
                        function ladderLength(beginWord, endWord, wordList) {
                            // Your code here
                            return 0;
                        }
                        const readline = require("readline");
                        const rl = readline.createInterface({ input: process.stdin });
                        const lines = [];
                        rl.on("line", l => lines.push(l.trim()));
                        rl.on("close", () => { console.log(ladderLength(lines[0], lines[1], lines[2].split(" "))); });""")
                .build();
        h3.addTestCase(TestCase.builder().input("hit\ncog\nhot dot dog lot log cog").expectedOutput("5").isHidden(false).orderIndex(0).explanation("hit->hot->dot->dog->cog").build());
        h3.addTestCase(TestCase.builder().input("hit\ncog\nhot dot dog lot log").expectedOutput("0").isHidden(false).orderIndex(1).explanation("cog not in list").build());
        h3.addTestCase(TestCase.builder().input("a\nc\na b c").expectedOutput("2").isHidden(false).orderIndex(2).explanation("Single char words").build());
        h3.addTestCase(TestCase.builder().input("hot\ndog\nhot dog").expectedOutput("2").isHidden(true).orderIndex(3).explanation("Two step").build());
        h3.addTestCase(TestCase.builder().input("red\ntax\nted tex tax tad den rex pee").expectedOutput("4").isHidden(true).orderIndex(4).explanation("Longer chain").build());
        problemRepository.save(h3);

        // H4 - Largest Rectangle in Histogram
        Problem h4 = Problem.builder()
                .title("Largest Rectangle in Histogram")
                .difficulty(Difficulty.HARD)
                .description("""
                        Given an array of integers heights representing histogram bar heights (width = 1 each),
                        return the area of the largest rectangle in the histogram.

                        **Example 1:**
                        Input: 2 1 5 6 2 3
                        Output: 10

                        **Example 2:**
                        Input: 2 4
                        Output: 4""")
                .timeLimitSeconds(5).memoryLimitMb(256)
                .inputFormat("A single line of space-separated non-negative integers representing bar heights.")
                .outputFormat("A single integer — the area of the largest rectangle.")
                .constraints("1 <= heights.length <= 10^5\n0 <= heights[i] <= 10^4")
                .starterCodeJava("""
                        import java.util.*;
                        public class Main {
                            public static int largestRectangleArea(int[] heights) {
                                // Your code here
                                return 0;
                            }
                            public static void main(String[] args) {
                                Scanner sc = new Scanner(System.in);
                                int[] heights = Arrays.stream(sc.nextLine().split(" ")).mapToInt(Integer::parseInt).toArray();
                                System.out.println(largestRectangleArea(heights));
                            }
                        }""")
                .starterCodePython("""
                        def largest_rectangle_area(heights):
                            # Your code here
                            return 0
                        if __name__ == "__main__":
                            heights = list(map(int, input().split()))
                            print(largest_rectangle_area(heights))""")
                .starterCodeJavascript("""
                        function largestRectangleArea(heights) {
                            // Your code here
                            return 0;
                        }
                        const readline = require("readline");
                        const rl = readline.createInterface({ input: process.stdin });
                        rl.on("line", (line) => { console.log(largestRectangleArea(line.split(" ").map(Number))); rl.close(); });""")
                .build();
        h4.addTestCase(TestCase.builder().input("2 1 5 6 2 3").expectedOutput("10").isHidden(false).orderIndex(0).explanation("5x2 rectangle").build());
        h4.addTestCase(TestCase.builder().input("2 4").expectedOutput("4").isHidden(false).orderIndex(1).explanation("Two bars").build());
        h4.addTestCase(TestCase.builder().input("6 2 5 4 5 1 6").expectedOutput("12").isHidden(false).orderIndex(2).explanation("Varied heights").build());
        h4.addTestCase(TestCase.builder().input("1 1 1 1 1").expectedOutput("5").isHidden(true).orderIndex(3).explanation("All same height").build());
        h4.addTestCase(TestCase.builder().input("0 9 0").expectedOutput("9").isHidden(true).orderIndex(4).explanation("Single tall bar").build());
        problemRepository.save(h4);

        // H5 - Regular Expression Matching
        Problem h5 = Problem.builder()
                .title("Regular Expression Matching")
                .difficulty(Difficulty.HARD)
                .description("""
                        Implement regular expression matching with support for . and *.

                        - . matches any single character.
                        - * matches zero or more of the preceding element.

                        The matching must cover the entire input string.

                        **Example 1:**
                        Input:
                        aa
                        a
                        Output: false

                        **Example 2:**
                        Input:
                        aa
                        a*
                        Output: true

                        **Example 3:**
                        Input:
                        ab
                        .*
                        Output: true""")
                .timeLimitSeconds(5).memoryLimitMb(256)
                .inputFormat("First line: string s.\nSecond line: pattern p.")
                .outputFormat("true or false")
                .constraints("1 <= s.length <= 20\n1 <= p.length <= 30\ns contains only lowercase English letters.\np contains only lowercase English letters, . and *.")
                .starterCodeJava("""
                        import java.util.*;
                        public class Main {
                            public static boolean isMatch(String s, String p) {
                                // Your code here
                                return false;
                            }
                            public static void main(String[] args) {
                                Scanner sc = new Scanner(System.in);
                                String s = sc.nextLine().trim();
                                String p = sc.nextLine().trim();
                                System.out.println(isMatch(s, p));
                            }
                        }""")
                .starterCodePython("""
                        def is_match(s, p):
                            # Your code here
                            return False
                        if __name__ == "__main__":
                            s = input().strip()
                            p = input().strip()
                            print("true" if is_match(s, p) else "false")""")
                .starterCodeJavascript("""
                        function isMatch(s, p) {
                            // Your code here
                            return false;
                        }
                        const readline = require("readline");
                        const rl = readline.createInterface({ input: process.stdin });
                        const lines = [];
                        rl.on("line", l => lines.push(l.trim()));
                        rl.on("close", () => { console.log(isMatch(lines[0], lines[1])); });""")
                .build();
        h5.addTestCase(TestCase.builder().input("aa\na").expectedOutput("false").isHidden(false).orderIndex(0).explanation("No match").build());
        h5.addTestCase(TestCase.builder().input("aa\na*").expectedOutput("true").isHidden(false).orderIndex(1).explanation("Star matches multiple").build());
        h5.addTestCase(TestCase.builder().input("ab\n.*").expectedOutput("true").isHidden(false).orderIndex(2).explanation("Dot-star matches all").build());
        h5.addTestCase(TestCase.builder().input("aab\nc*a*b").expectedOutput("true").isHidden(true).orderIndex(3).explanation("Complex pattern").build());
        h5.addTestCase(TestCase.builder().input("mississippi\nmis*is*p*.").expectedOutput("false").isHidden(true).orderIndex(4).explanation("Tricky mismatch").build());
        problemRepository.save(h5);

        // H6 - N-Queens Count
        Problem h6 = Problem.builder()
                .title("N-Queens Count")
                .difficulty(Difficulty.HARD)
                .description("""
                        Place n queens on an n x n chessboard so that no two queens attack each other.

                        Return the number of distinct solutions.

                        **Example 1:**
                        Input: 4
                        Output: 2

                        **Example 2:**
                        Input: 1
                        Output: 1

                        **Example 3:**
                        Input: 6
                        Output: 4""")
                .timeLimitSeconds(10).memoryLimitMb(256)
                .inputFormat("A single integer n.")
                .outputFormat("A single integer — the number of distinct n-queens solutions.")
                .constraints("1 <= n <= 12")
                .starterCodeJava("""
                        import java.util.*;
                        public class Main {
                            public static int totalNQueens(int n) {
                                // Your code here
                                return 0;
                            }
                            public static void main(String[] args) {
                                Scanner sc = new Scanner(System.in);
                                System.out.println(totalNQueens(Integer.parseInt(sc.nextLine().trim())));
                            }
                        }""")
                .starterCodePython("""
                        def total_n_queens(n):
                            # Your code here
                            return 0
                        if __name__ == "__main__":
                            print(total_n_queens(int(input())))""")
                .starterCodeJavascript("""
                        function totalNQueens(n) {
                            // Your code here
                            return 0;
                        }
                        const readline = require("readline");
                        const rl = readline.createInterface({ input: process.stdin });
                        rl.on("line", (n) => { console.log(totalNQueens(parseInt(n))); rl.close(); });""")
                .build();
        h6.addTestCase(TestCase.builder().input("4").expectedOutput("2").isHidden(false).orderIndex(0).explanation("4x4 board").build());
        h6.addTestCase(TestCase.builder().input("1").expectedOutput("1").isHidden(false).orderIndex(1).explanation("Trivial").build());
        h6.addTestCase(TestCase.builder().input("6").expectedOutput("4").isHidden(false).orderIndex(2).explanation("6x6 board").build());
        h6.addTestCase(TestCase.builder().input("8").expectedOutput("92").isHidden(true).orderIndex(3).explanation("Classic 8-queens").build());
        h6.addTestCase(TestCase.builder().input("12").expectedOutput("14200").isHidden(true).orderIndex(4).explanation("12x12 board").build());
        problemRepository.save(h6);

        // H7 - Minimum Window Substring
        Problem h7 = Problem.builder()
                .title("Minimum Window Substring")
                .difficulty(Difficulty.HARD)
                .description("""
                        Given two strings s and t, return the minimum window substring of s such that every
                        character in t (including duplicates) is included. Return empty string if none exists.

                        **Example 1:**
                        Input:
                        ADOBECODEBANC
                        ABC
                        Output: BANC

                        **Example 2:**
                        Input:
                        a
                        a
                        Output: a

                        **Example 3:**
                        Input:
                        a
                        aa
                        Output: (empty string)""")
                .timeLimitSeconds(5).memoryLimitMb(256)
                .inputFormat("First line: string s.\nSecond line: string t.")
                .outputFormat("The minimum window substring, or empty string if none exists.")
                .constraints("1 <= s.length, t.length <= 10^5\ns and t consist of uppercase and lowercase English letters.")
                .starterCodeJava("""
                        import java.util.*;
                        public class Main {
                            public static String minWindow(String s, String t) {
                                // Your code here
                                return "";
                            }
                            public static void main(String[] args) {
                                Scanner sc = new Scanner(System.in);
                                String s = sc.nextLine().trim();
                                String t = sc.nextLine().trim();
                                System.out.println(minWindow(s, t));
                            }
                        }""")
                .starterCodePython("""
                        def min_window(s, t):
                            # Your code here
                            return ""
                        if __name__ == "__main__":
                            s = input().strip()
                            t = input().strip()
                            print(min_window(s, t))""")
                .starterCodeJavascript("""
                        function minWindow(s, t) {
                            // Your code here
                            return "";
                        }
                        const readline = require("readline");
                        const rl = readline.createInterface({ input: process.stdin });
                        const lines = [];
                        rl.on("line", l => lines.push(l.trim()));
                        rl.on("close", () => { console.log(minWindow(lines[0], lines[1])); });""")
                .build();
        h7.addTestCase(TestCase.builder().input("ADOBECODEBANC\nABC").expectedOutput("BANC").isHidden(false).orderIndex(0).explanation("Classic case").build());
        h7.addTestCase(TestCase.builder().input("a\na").expectedOutput("a").isHidden(false).orderIndex(1).explanation("Same string").build());
        h7.addTestCase(TestCase.builder().input("a\naa").expectedOutput("").isHidden(false).orderIndex(2).explanation("Impossible").build());
        h7.addTestCase(TestCase.builder().input("ABCBA\nAB").expectedOutput("AB").isHidden(true).orderIndex(3).explanation("Min window at start").build());
        h7.addTestCase(TestCase.builder().input("aaflslflsldkalskaaa\naaa").expectedOutput("aaa").isHidden(true).orderIndex(4).explanation("Three same chars").build());
        problemRepository.save(h7);

        // H8 - Longest Increasing Path in Matrix
        Problem h8 = Problem.builder()
                .title("Longest Increasing Path in Matrix")
                .difficulty(Difficulty.HARD)
                .description("""
                        Given an m x n integer matrix, return the length of the longest strictly increasing path.

                        From each cell you can move in 4 directions (up, down, left, right).
                        You may not move diagonally or outside the boundary.

                        **Example 1:**
                        Input:
                        3 3
                        9 9 4
                        6 6 8
                        2 1 1
                        Output: 4

                        **Example 2:**
                        Input:
                        3 3
                        3 4 5
                        3 2 6
                        2 2 1
                        Output: 4""")
                .timeLimitSeconds(5).memoryLimitMb(256)
                .inputFormat("First line: two integers m n (rows and cols).\nNext m lines: n space-separated integers per row.")
                .outputFormat("A single integer — the length of the longest increasing path.")
                .constraints("1 <= m, n <= 200\n0 <= matrix[i][j] <= 2^31 - 1")
                .starterCodeJava("""
                        import java.util.*;
                        public class Main {
                            public static int longestIncreasingPath(int[][] matrix) {
                                // Your code here
                                return 0;
                            }
                            public static void main(String[] args) {
                                Scanner sc = new Scanner(System.in);
                                String[] dims = sc.nextLine().split(" ");
                                int m = Integer.parseInt(dims[0]), n = Integer.parseInt(dims[1]);
                                int[][] matrix = new int[m][n];
                                for (int i = 0; i < m; i++) {
                                    String[] parts = sc.nextLine().split(" ");
                                    for (int j = 0; j < n; j++) matrix[i][j] = Integer.parseInt(parts[j]);
                                }
                                System.out.println(longestIncreasingPath(matrix));
                            }
                        }""")
                .starterCodePython("""
                        def longest_increasing_path(matrix):
                            # Your code here
                            return 0
                        if __name__ == "__main__":
                            m, n = map(int, input().split())
                            matrix = [list(map(int, input().split())) for _ in range(m)]
                            print(longest_increasing_path(matrix))""")
                .starterCodeJavascript("""
                        function longestIncreasingPath(matrix) {
                            // Your code here
                            return 0;
                        }
                        const readline = require("readline");
                        const rl = readline.createInterface({ input: process.stdin });
                        const lines = [];
                        rl.on("line", l => lines.push(l.trim()));
                        rl.on("close", () => {
                            const [m, n] = lines[0].split(" ").map(Number);
                            const matrix = [];
                            for (let i = 1; i <= m; i++) matrix.push(lines[i].split(" ").map(Number));
                            console.log(longestIncreasingPath(matrix));
                        });""")
                .build();
        h8.addTestCase(TestCase.builder().input("3 3\n9 9 4\n6 6 8\n2 1 1").expectedOutput("4").isHidden(false).orderIndex(0).explanation("1->2->6->9").build());
        h8.addTestCase(TestCase.builder().input("3 3\n3 4 5\n3 2 6\n2 2 1").expectedOutput("4").isHidden(false).orderIndex(1).explanation("3->4->5->6").build());
        h8.addTestCase(TestCase.builder().input("1 1\n1").expectedOutput("1").isHidden(false).orderIndex(2).explanation("Single cell").build());
        h8.addTestCase(TestCase.builder().input("2 2\n1 2\n4 3").expectedOutput("4").isHidden(true).orderIndex(3).explanation("1->2->3->4").build());
        h8.addTestCase(TestCase.builder().input("3 4\n7 8 9 1\n6 5 4 2\n3 2 1 3").expectedOutput("6").isHidden(true).orderIndex(4).explanation("Longer path").build());
        problemRepository.save(h8);

        // H9 - Burst Balloons
        Problem h9 = Problem.builder()
                .title("Burst Balloons")
                .difficulty(Difficulty.HARD)
                .description("""
                        You are given n balloons. Each balloon has a number nums[i].
                        When you burst balloon i, you gain nums[i-1] * nums[i] * nums[i+1] coins.
                        Out-of-bounds indices are treated as 1.

                        Return the maximum coins you can collect by bursting all balloons wisely.

                        **Example 1:**
                        Input: 3 1 5 8
                        Output: 167

                        **Example 2:**
                        Input: 1 5
                        Output: 10""")
                .timeLimitSeconds(10).memoryLimitMb(256)
                .inputFormat("A single line of space-separated integers representing balloon values.")
                .outputFormat("A single integer — the maximum coins collectible.")
                .constraints("1 <= n <= 300\n0 <= nums[i] <= 100")
                .starterCodeJava("""
                        import java.util.*;
                        public class Main {
                            public static int maxCoins(int[] nums) {
                                // Your code here
                                return 0;
                            }
                            public static void main(String[] args) {
                                Scanner sc = new Scanner(System.in);
                                int[] nums = Arrays.stream(sc.nextLine().split(" ")).mapToInt(Integer::parseInt).toArray();
                                System.out.println(maxCoins(nums));
                            }
                        }""")
                .starterCodePython("""
                        def max_coins(nums):
                            # Your code here
                            return 0
                        if __name__ == "__main__":
                            nums = list(map(int, input().split()))
                            print(max_coins(nums))""")
                .starterCodeJavascript("""
                        function maxCoins(nums) {
                            // Your code here
                            return 0;
                        }
                        const readline = require("readline");
                        const rl = readline.createInterface({ input: process.stdin });
                        rl.on("line", (line) => { console.log(maxCoins(line.split(" ").map(Number))); rl.close(); });""")
                .build();
        h9.addTestCase(TestCase.builder().input("3 1 5 8").expectedOutput("167").isHidden(false).orderIndex(0).explanation("Classic example").build());
        h9.addTestCase(TestCase.builder().input("1 5").expectedOutput("10").isHidden(false).orderIndex(1).explanation("Two balloons").build());
        h9.addTestCase(TestCase.builder().input("1").expectedOutput("1").isHidden(false).orderIndex(2).explanation("Single balloon").build());
        h9.addTestCase(TestCase.builder().input("7 9 8 0 7 1 3 5 5 2 3").expectedOutput("1654").isHidden(true).orderIndex(3).explanation("Larger input").build());
        h9.addTestCase(TestCase.builder().input("8 3 6 2 9 8 4 1").expectedOutput("1386").isHidden(true).orderIndex(4).explanation("Eight balloons").build());
        problemRepository.save(h9);

        // H10 - Edit Distance
        Problem h10 = Problem.builder()
                .title("Edit Distance")
                .difficulty(Difficulty.HARD)
                .description("""
                        Given two strings word1 and word2, return the minimum number of operations required
                        to convert word1 to word2.

                        You have three operations: Insert a character, Delete a character, Replace a character.

                        **Example 1:**
                        Input:
                        horse
                        ros
                        Output: 3

                        **Example 2:**
                        Input:
                        intention
                        execution
                        Output: 5

                        **Example 3:**
                        Input:
                        abc
                        abc
                        Output: 0""")
                .timeLimitSeconds(5).memoryLimitMb(256)
                .inputFormat("First line: word1.\nSecond line: word2.")
                .outputFormat("A single integer — the minimum edit distance.")
                .constraints("0 <= word1.length, word2.length <= 500\nword1 and word2 consist of lowercase English letters.")
                .starterCodeJava("""
                        import java.util.*;
                        public class Main {
                            public static int minDistance(String word1, String word2) {
                                // Your code here
                                return 0;
                            }
                            public static void main(String[] args) {
                                Scanner sc = new Scanner(System.in);
                                String word1 = sc.nextLine().trim();
                                String word2 = sc.nextLine().trim();
                                System.out.println(minDistance(word1, word2));
                            }
                        }""")
                .starterCodePython("""
                        def min_distance(word1, word2):
                            # Your code here
                            return 0
                        if __name__ == "__main__":
                            word1 = input().strip()
                            word2 = input().strip()
                            print(min_distance(word1, word2))""")
                .starterCodeJavascript("""
                        function minDistance(word1, word2) {
                            // Your code here
                            return 0;
                        }
                        const readline = require("readline");
                        const rl = readline.createInterface({ input: process.stdin });
                        const lines = [];
                        rl.on("line", l => lines.push(l.trim()));
                        rl.on("close", () => { console.log(minDistance(lines[0], lines[1])); });""")
                .build();
        h10.addTestCase(TestCase.builder().input("horse\nros").expectedOutput("3").isHidden(false).orderIndex(0).explanation("horse->rorse->rose->ros").build());
        h10.addTestCase(TestCase.builder().input("intention\nexecution").expectedOutput("5").isHidden(false).orderIndex(1).explanation("Classic DP").build());
        h10.addTestCase(TestCase.builder().input("abc\nabc").expectedOutput("0").isHidden(false).orderIndex(2).explanation("Identical strings").build());
        h10.addTestCase(TestCase.builder().input("abc\n").expectedOutput("3").isHidden(true).orderIndex(3).explanation("Delete all chars").build());
        h10.addTestCase(TestCase.builder().input("kitten\nsitting").expectedOutput("3").isHidden(true).orderIndex(4).explanation("Classic Levenshtein").build());
        problemRepository.save(h10);
    }
}