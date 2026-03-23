-- ============================================================
-- V2__insert_sample_problems.sql
-- PASTE THIS AS YOUR ENTIRE V2 FILE
-- Contains ONLY the new 10 Medium + 10 Hard problems
-- The original 5 problems stay in V1 (already in DB)
-- ============================================================

-- ============================================================
-- MEDIUM PROBLEMS (10)
-- ============================================================

-- M1: Longest Substring Without Repeating Characters
INSERT INTO problems (id, title, description, difficulty, time_limit_seconds, memory_limit_mb, input_format, output_format, constraints, starter_code_java, starter_code_python, starter_code_javascript)
VALUES (
           'f1000001-0000-0000-0000-000000000001',
           'Longest Substring Without Repeating Characters',
           'Given a string s, find the length of the longest substring without repeating characters.

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
       Explanation: The answer is "wke".',
           'MEDIUM', 5, 256,
           'A single line containing string s.',
           'A single integer — the length of the longest substring without repeating characters.',
           '0 <= s.length <= 5 * 10^4
       s consists of English letters, digits, symbols and spaces.',
           'import java.util.*;

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
       }',
           'def length_of_longest_substring(s):
           # Your code here
           return 0

       if __name__ == "__main__":
           s = input()
           print(length_of_longest_substring(s))',
           'function lengthOfLongestSubstring(s) {
           // Your code here
           return 0;
       }

       const readline = require("readline");
       const rl = readline.createInterface({ input: process.stdin });
       rl.on("line", (s) => {
           console.log(lengthOfLongestSubstring(s));
           rl.close();
       });'
       );

INSERT INTO test_cases (problem_id, input, expected_output, is_hidden, order_index, explanation)
VALUES
    ('f1000001-0000-0000-0000-000000000001', 'abcabcbb', '3', false, 0, 'abc is the longest'),
    ('f1000001-0000-0000-0000-000000000001', 'bbbbb', '1', false, 1, 'single b'),
    ('f1000001-0000-0000-0000-000000000001', 'pwwkew', '3', false, 2, 'wke'),
    ('f1000001-0000-0000-0000-000000000001', 'a', '1', true, 3, 'single char'),
    ('f1000001-0000-0000-0000-000000000001', 'aab', '2', true, 4, 'ab is longest');


-- M2: 3Sum
INSERT INTO problems (id, title, description, difficulty, time_limit_seconds, memory_limit_mb, input_format, output_format, constraints, starter_code_java, starter_code_python, starter_code_javascript)
VALUES (
           'f1000002-0000-0000-0000-000000000002',
           '3Sum',
           'Given an integer array nums, return all triplets [nums[i], nums[j], nums[k]] such that i != j, i != k, j != k, and nums[i] + nums[j] + nums[k] == 0.

       The solution set must not contain duplicate triplets. Print each triplet in non-decreasing order, triplets sorted lexicographically.

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
       0 0 0',
           'MEDIUM', 5, 256,
           'A single line of space-separated integers.',
           'Each triplet on its own line with values space-separated in non-decreasing order. Print nothing if no triplets exist.',
           '3 <= nums.length <= 3000
       -10^5 <= nums[i] <= 10^5',
           'import java.util.*;

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
       }',
           'def three_sum(nums):
           # Your code here
           return []

       if __name__ == "__main__":
           nums = list(map(int, input().split()))
           result = three_sum(nums)
           for t in result:
               print(*t)',
           'function threeSum(nums) {
           // Your code here
           return [];
       }

       const readline = require("readline");
       const rl = readline.createInterface({ input: process.stdin });
       rl.on("line", (line) => {
           const nums = line.split(" ").map(Number);
           const result = threeSum(nums);
           result.forEach(t => console.log(t.join(" ")));
           rl.close();
       });'
       );

INSERT INTO test_cases (problem_id, input, expected_output, is_hidden, order_index, explanation)
VALUES
    ('f1000002-0000-0000-0000-000000000002', '-1 0 1 2 -1 -4', '-1 -1 2
-1 0 1', false, 0, 'Two valid triplets'),
    ('f1000002-0000-0000-0000-000000000002', '0 1 1', '', false, 1, 'No triplet sums to 0'),
    ('f1000002-0000-0000-0000-000000000002', '0 0 0', '0 0 0', false, 2, 'All zeros'),
    ('f1000002-0000-0000-0000-000000000002', '-2 0 0 2 2', '-2 0 2', true, 3, 'Deduplicated'),
    ('f1000002-0000-0000-0000-000000000002', '-4 -1 -1 0 1 2', '-1 -1 2
-1 0 1', true, 4, 'Sorted input');


-- M3: Product of Array Except Self
INSERT INTO problems (id, title, description, difficulty, time_limit_seconds, memory_limit_mb, input_format, output_format, constraints, starter_code_java, starter_code_python, starter_code_javascript)
VALUES (
           'f1000003-0000-0000-0000-000000000003',
           'Product of Array Except Self',
           'Given an integer array nums, return an array answer such that answer[i] is equal to the product of all elements of nums except nums[i].

       You must solve it in O(n) time without using the division operation.

       **Example 1:**
       Input: 1 2 3 4
       Output: 24 12 8 6

       **Example 2:**
       Input: -1 1 0 -3 3
       Output: 0 0 9 0 0',
           'MEDIUM', 5, 256,
           'A single line of space-separated integers.',
           'Space-separated integers representing the answer array.',
           '2 <= nums.length <= 10^5
       -30 <= nums[i] <= 30',
           'import java.util.*;

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
       }',
           'def product_except_self(nums):
           # Your code here
           return []

       if __name__ == "__main__":
           nums = list(map(int, input().split()))
           print(*product_except_self(nums))',
           'function productExceptSelf(nums) {
           // Your code here
           return [];
       }

       const readline = require("readline");
       const rl = readline.createInterface({ input: process.stdin });
       rl.on("line", (line) => {
           console.log(productExceptSelf(line.split(" ").map(Number)).join(" "));
           rl.close();
       });'
       );

INSERT INTO test_cases (problem_id, input, expected_output, is_hidden, order_index, explanation)
VALUES
    ('f1000003-0000-0000-0000-000000000003', '1 2 3 4', '24 12 8 6', false, 0, 'Basic case'),
    ('f1000003-0000-0000-0000-000000000003', '-1 1 0 -3 3', '0 0 9 0 0', false, 1, 'Contains zero'),
    ('f1000003-0000-0000-0000-000000000003', '2 3', '3 2', false, 2, 'Two elements'),
    ('f1000003-0000-0000-0000-000000000003', '1 0 0 2', '0 0 0 0', true, 3, 'Two zeros'),
    ('f1000003-0000-0000-0000-000000000003', '-1 -1 -1 -1', '-1 -1 -1 -1', true, 4, 'All negatives');


-- M4: Container With Most Water
INSERT INTO problems (id, title, description, difficulty, time_limit_seconds, memory_limit_mb, input_format, output_format, constraints, starter_code_java, starter_code_python, starter_code_javascript)
VALUES (
           'f1000004-0000-0000-0000-000000000004',
           'Container With Most Water',
           'You are given an integer array height of length n. Find two lines that together with the x-axis form a container holding the most water.

       Return the maximum amount of water the container can store.

       **Example 1:**
       Input: 1 8 6 2 5 4 8 3 7
       Output: 49

       **Example 2:**
       Input: 1 1
       Output: 1',
           'MEDIUM', 5, 256,
           'A single line of space-separated integers representing heights.',
           'A single integer — the maximum water the container can store.',
           '2 <= n <= 10^5
       0 <= height[i] <= 10^4',
           'import java.util.*;

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
       }',
           'def max_area(height):
           # Your code here
           return 0

       if __name__ == "__main__":
           height = list(map(int, input().split()))
           print(max_area(height))',
           'function maxArea(height) {
           // Your code here
           return 0;
       }

       const readline = require("readline");
       const rl = readline.createInterface({ input: process.stdin });
       rl.on("line", (line) => {
           console.log(maxArea(line.split(" ").map(Number)));
           rl.close();
       });'
       );

INSERT INTO test_cases (problem_id, input, expected_output, is_hidden, order_index, explanation)
VALUES
    ('f1000004-0000-0000-0000-000000000004', '1 8 6 2 5 4 8 3 7', '49', false, 0, 'Classic case'),
    ('f1000004-0000-0000-0000-000000000004', '1 1', '1', false, 1, 'Minimal'),
    ('f1000004-0000-0000-0000-000000000004', '4 3 2 1 4', '16', false, 2, 'Symmetric'),
    ('f1000004-0000-0000-0000-000000000004', '1 2 1', '2', true, 3, 'Three elements'),
    ('f1000004-0000-0000-0000-000000000004', '2 3 4 5 18 17 6', '17', true, 4, 'Max not at ends');


-- M5: Jump Game
INSERT INTO problems (id, title, description, difficulty, time_limit_seconds, memory_limit_mb, input_format, output_format, constraints, starter_code_java, starter_code_python, starter_code_javascript)
VALUES (
           'f1000005-0000-0000-0000-000000000005',
           'Jump Game',
           'You are given an integer array nums. You are initially positioned at the first index. Each element represents your maximum jump length at that position.

       Return true if you can reach the last index, or false otherwise.

       **Example 1:**
       Input: 2 3 1 1 4
       Output: true

       **Example 2:**
       Input: 3 2 1 0 4
       Output: false
       Explanation: You always arrive at index 3 which has jump length 0.',
           'MEDIUM', 5, 256,
           'A single line of space-separated integers.',
           'true or false',
           '1 <= nums.length <= 10^4
       0 <= nums[i] <= 10^5',
           'import java.util.*;

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
       }',
           'def can_jump(nums):
           # Your code here
           return False

       if __name__ == "__main__":
           nums = list(map(int, input().split()))
           print("true" if can_jump(nums) else "false")',
           'function canJump(nums) {
           // Your code here
           return false;
       }

       const readline = require("readline");
       const rl = readline.createInterface({ input: process.stdin });
       rl.on("line", (line) => {
           console.log(canJump(line.split(" ").map(Number)));
           rl.close();
       });'
       );

INSERT INTO test_cases (problem_id, input, expected_output, is_hidden, order_index, explanation)
VALUES
    ('f1000005-0000-0000-0000-000000000005', '2 3 1 1 4', 'true', false, 0, 'Can reach end'),
    ('f1000005-0000-0000-0000-000000000005', '3 2 1 0 4', 'false', false, 1, 'Stuck at zero'),
    ('f1000005-0000-0000-0000-000000000005', '0', 'true', false, 2, 'Already at end'),
    ('f1000005-0000-0000-0000-000000000005', '1 0 0 0', 'false', true, 3, 'Cannot pass zero'),
    ('f1000005-0000-0000-0000-000000000005', '5 0 0 0 0 0', 'true', true, 4, 'Big first jump');


-- M6: Longest Palindromic Substring
INSERT INTO problems (id, title, description, difficulty, time_limit_seconds, memory_limit_mb, input_format, output_format, constraints, starter_code_java, starter_code_python, starter_code_javascript)
VALUES (
           'f1000006-0000-0000-0000-000000000006',
           'Longest Palindromic Substring',
           'Given a string s, return the longest palindromic substring in s. If multiple answers have the same length, return the one that starts earliest.

       **Example 1:**
       Input: babad
       Output: bab

       **Example 2:**
       Input: cbbd
       Output: bb

       **Example 3:**
       Input: racecar
       Output: racecar',
           'MEDIUM', 5, 256,
           'A single line containing string s.',
           'The longest palindromic substring.',
           '1 <= s.length <= 1000
       s consists of only digits and English letters.',
           'import java.util.*;

       public class Main {
           public static String longestPalindrome(String s) {
               // Your code here
               return "";
           }

           public static void main(String[] args) {
               Scanner sc = new Scanner(System.in);
               System.out.println(longestPalindrome(sc.nextLine()));
           }
       }',
           'def longest_palindrome(s):
           # Your code here
           return ""

       if __name__ == "__main__":
           print(longest_palindrome(input()))',
           'function longestPalindrome(s) {
           // Your code here
           return "";
       }

       const readline = require("readline");
       const rl = readline.createInterface({ input: process.stdin });
       rl.on("line", (s) => { console.log(longestPalindrome(s)); rl.close(); });'
       );

INSERT INTO test_cases (problem_id, input, expected_output, is_hidden, order_index, explanation)
VALUES
    ('f1000006-0000-0000-0000-000000000006', 'babad', 'bab', false, 0, 'First longest'),
    ('f1000006-0000-0000-0000-000000000006', 'cbbd', 'bb', false, 1, 'Even palindrome'),
    ('f1000006-0000-0000-0000-000000000006', 'racecar', 'racecar', false, 2, 'Whole string'),
    ('f1000006-0000-0000-0000-000000000006', 'a', 'a', true, 3, 'Single char'),
    ('f1000006-0000-0000-0000-000000000006', 'aacabdkacaa', 'aca', true, 4, 'Multiple candidates');


-- M7: Search in Rotated Sorted Array
INSERT INTO problems (id, title, description, difficulty, time_limit_seconds, memory_limit_mb, input_format, output_format, constraints, starter_code_java, starter_code_python, starter_code_javascript)
VALUES (
           'f1000007-0000-0000-0000-000000000007',
           'Search in Rotated Sorted Array',
           'An integer array nums sorted in ascending order is possibly rotated at an unknown pivot. Given the array and an integer target, return the index of target if it is in nums, or -1 if not.

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
       Output: -1

       **Example 3:**
       Input:
       1
       0
       Output: -1',
           'MEDIUM', 5, 256,
           'First line: space-separated integers (the rotated array).
       Second line: the target integer.',
           'A single integer — the index of target, or -1 if not found.',
           '1 <= nums.length <= 5000
       -10^4 <= nums[i] <= 10^4
       All values in nums are unique.',
           'import java.util.*;

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
       }',
           'def search(nums, target):
           # Your code here
           return -1

       if __name__ == "__main__":
           nums = list(map(int, input().split()))
           target = int(input())
           print(search(nums, target))',
           'function search(nums, target) {
           // Your code here
           return -1;
       }

       const readline = require("readline");
       const rl = readline.createInterface({ input: process.stdin });
       const lines = [];
       rl.on("line", l => lines.push(l.trim()));
       rl.on("close", () => {
           const nums = lines[0].split(" ").map(Number);
           console.log(search(nums, parseInt(lines[1])));
       });'
       );

INSERT INTO test_cases (problem_id, input, expected_output, is_hidden, order_index, explanation)
VALUES
    ('f1000007-0000-0000-0000-000000000007', '4 5 6 7 0 1 2
0', '4', false, 0, 'Found after pivot'),
    ('f1000007-0000-0000-0000-000000000007', '4 5 6 7 0 1 2
3', '-1', false, 1, 'Not found'),
    ('f1000007-0000-0000-0000-000000000007', '1
0', '-1', false, 2, 'Single element miss'),
    ('f1000007-0000-0000-0000-000000000007', '3 1
1', '1', true, 3, 'Two element rotated'),
    ('f1000007-0000-0000-0000-000000000007', '6 7 8 1 2 3 4 5
8', '2', true, 4, 'Found before pivot');


-- M8: Decode Ways
INSERT INTO problems (id, title, description, difficulty, time_limit_seconds, memory_limit_mb, input_format, output_format, constraints, starter_code_java, starter_code_python, starter_code_javascript)
VALUES (
           'f1000008-0000-0000-0000-000000000008',
           'Decode Ways',
           'A message containing letters A-Z can be encoded: A=1, B=2, ..., Z=26.

       Given a string s of digits, return the number of ways to decode it.

       **Example 1:**
       Input: 12
       Output: 2
       Explanation: "12" can be "AB" (1 2) or "L" (12).

       **Example 2:**
       Input: 226
       Output: 3
       Explanation: "BZ" (2 26), "VF" (22 6), or "BBF" (2 2 6).

       **Example 3:**
       Input: 06
       Output: 0
       Explanation: "06" is invalid.',
           'MEDIUM', 5, 256,
           'A single line containing the string s (digits only).',
           'A single integer — the number of ways to decode the string.',
           '1 <= s.length <= 100
       s contains only digits.',
           'import java.util.*;

       public class Main {
           public static int numDecodings(String s) {
               // Your code here
               return 0;
           }

           public static void main(String[] args) {
               Scanner sc = new Scanner(System.in);
               System.out.println(numDecodings(sc.nextLine().trim()));
           }
       }',
           'def num_decodings(s):
           # Your code here
           return 0

       if __name__ == "__main__":
           print(num_decodings(input().strip()))',
           'function numDecodings(s) {
           // Your code here
           return 0;
       }

       const readline = require("readline");
       const rl = readline.createInterface({ input: process.stdin });
       rl.on("line", (s) => { console.log(numDecodings(s.trim())); rl.close(); });'
       );

INSERT INTO test_cases (problem_id, input, expected_output, is_hidden, order_index, explanation)
VALUES
    ('f1000008-0000-0000-0000-000000000008', '12', '2', false, 0, 'AB or L'),
    ('f1000008-0000-0000-0000-000000000008', '226', '3', false, 1, 'Three ways'),
    ('f1000008-0000-0000-0000-000000000008', '06', '0', false, 2, 'Leading zero invalid'),
    ('f1000008-0000-0000-0000-000000000008', '11106', '2', true, 3, 'Mixed zeros'),
    ('f1000008-0000-0000-0000-000000000008', '1', '1', true, 4, 'Single digit');


-- M9: Coin Change
INSERT INTO problems (id, title, description, difficulty, time_limit_seconds, memory_limit_mb, input_format, output_format, constraints, starter_code_java, starter_code_python, starter_code_javascript)
VALUES (
           'f1000009-0000-0000-0000-000000000009',
           'Coin Change',
           'You are given an integer array coins representing different denominations and an integer amount.

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
       Explanation: 11 = 5 + 5 + 1

       **Example 3:**
       Input:
       2
       3
       Output: -1',
           'MEDIUM', 5, 256,
           'First line: space-separated coin denominations.
       Second line: the target amount.',
           'A single integer — minimum coins needed, or -1 if impossible.',
           '1 <= coins.length <= 12
       1 <= coins[i] <= 2^31 - 1
       0 <= amount <= 10^4',
           'import java.util.*;

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
       }',
           'def coin_change(coins, amount):
           # Your code here
           return -1

       if __name__ == "__main__":
           coins = list(map(int, input().split()))
           amount = int(input())
           print(coin_change(coins, amount))',
           'function coinChange(coins, amount) {
           // Your code here
           return -1;
       }

       const readline = require("readline");
       const rl = readline.createInterface({ input: process.stdin });
       const lines = [];
       rl.on("line", l => lines.push(l.trim()));
       rl.on("close", () => {
           console.log(coinChange(lines[0].split(" ").map(Number), parseInt(lines[1])));
       });'
       );

INSERT INTO test_cases (problem_id, input, expected_output, is_hidden, order_index, explanation)
VALUES
    ('f1000009-0000-0000-0000-000000000009', '1 5 11
11', '1', false, 0, 'Single coin covers it'),
    ('f1000009-0000-0000-0000-000000000009', '1 2 5
11', '3', false, 1, '5+5+1'),
    ('f1000009-0000-0000-0000-000000000009', '2
3', '-1', false, 2, 'Impossible'),
    ('f1000009-0000-0000-0000-000000000009', '1
0', '0', true, 3, 'Amount is zero'),
    ('f1000009-0000-0000-0000-000000000009', '3 5 7
14', '2', true, 4, '7+7');


-- M10: Binary Tree Level Order Traversal
INSERT INTO problems (id, title, description, difficulty, time_limit_seconds, memory_limit_mb, input_format, output_format, constraints, starter_code_java, starter_code_python, starter_code_javascript)
VALUES (
           'f1000010-0000-0000-0000-000000000010',
           'Binary Tree Level Order Traversal',
           'Given the root of a binary tree (provided in BFS order, -1 means null), return the level order traversal of its node values.

       **Example 1:**
       Input: 3 9 20 -1 -1 15 7
       Output:
       3
       9 20
       15 7

       **Example 2:**
       Input: 1
       Output:
       1

       **Example 3:**
       Input: 1 2 3 4 5
       Output:
       1
       2 3
       4 5',
           'MEDIUM', 5, 256,
           'A single line of space-separated integers in BFS order. -1 means null.',
           'Each level on its own line, values space-separated left to right.',
           '1 <= number of nodes <= 2000
       -1000 <= Node.val <= 1000',
           'import java.util.*;

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
                       int left = 2 * idx + 1, right = 2 * idx + 2;
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
       }',
           'from collections import deque

       def level_order(tree):
           if not tree or tree[0] == -1:
               return []
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
           for level in level_order(tree):
               print(*level)',
           'function levelOrder(tree) {
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
       });'
       );

INSERT INTO test_cases (problem_id, input, expected_output, is_hidden, order_index, explanation)
VALUES
    ('f1000010-0000-0000-0000-000000000010', '3 9 20 -1 -1 15 7', '3
9 20
15 7', false, 0, 'Three levels'),
    ('f1000010-0000-0000-0000-000000000010', '1', '1', false, 1, 'Single node'),
    ('f1000010-0000-0000-0000-000000000010', '1 2 3 4 5', '1
2 3
4 5', false, 2, 'Complete tree'),
    ('f1000010-0000-0000-0000-000000000010', '1 -1 2 -1 -1 -1 3', '1
2
3', true, 3, 'Right skewed'),
    ('f1000010-0000-0000-0000-000000000010', '5 3 8 1 4 7 9', '5
3 8
1 4 7 9', true, 4, 'Full binary tree');


-- ============================================================
-- HARD PROBLEMS (10)
-- ============================================================

-- H1: Median of Two Sorted Arrays
INSERT INTO problems (id, title, description, difficulty, time_limit_seconds, memory_limit_mb, input_format, output_format, constraints, starter_code_java, starter_code_python, starter_code_javascript)
VALUES (
           'f2000001-0000-0000-0000-000000000001',
           'Median of Two Sorted Arrays',
           'Given two sorted arrays nums1 and nums2, return the median of the two sorted arrays.

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
       Output: 2.50000

       **Example 3:**
       Input:
       0 0
       0 0
       Output: 0.00000',
           'HARD', 5, 256,
           'First line: space-separated integers of nums1.
       Second line: space-separated integers of nums2.',
           'The median as a decimal rounded to 5 decimal places.',
           '0 <= m, n <= 1000
       1 <= m + n <= 2000
       -10^6 <= nums1[i], nums2[i] <= 10^6',
           'import java.util.*;

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
       }',
           'def find_median_sorted_arrays(nums1, nums2):
           # Your code here
           return 0.0

       if __name__ == "__main__":
           nums1 = list(map(int, input().split()))
           nums2 = list(map(int, input().split()))
           print(f"{find_median_sorted_arrays(nums1, nums2):.5f}")',
           'function findMedianSortedArrays(nums1, nums2) {
           // Your code here
           return 0.0;
       }

       const readline = require("readline");
       const rl = readline.createInterface({ input: process.stdin });
       const lines = [];
       rl.on("line", l => lines.push(l.trim()));
       rl.on("close", () => {
           const nums1 = lines[0].split(" ").map(Number);
           const nums2 = lines[1].split(" ").map(Number);
           console.log(findMedianSortedArrays(nums1, nums2).toFixed(5));
       });'
       );

INSERT INTO test_cases (problem_id, input, expected_output, is_hidden, order_index, explanation)
VALUES
    ('f2000001-0000-0000-0000-000000000001', '1 3
2', '2.00000', false, 0, 'Odd total length'),
    ('f2000001-0000-0000-0000-000000000001', '1 2
3 4', '2.50000', false, 1, 'Even total length'),
    ('f2000001-0000-0000-0000-000000000001', '0 0
0 0', '0.00000', false, 2, 'All zeros'),
    ('f2000001-0000-0000-0000-000000000001', '1
1', '1.00000', true, 3, 'Both single element'),
    ('f2000001-0000-0000-0000-000000000001', '1 3 5 7
2 4 6 8', '4.50000', true, 4, 'Interleaved arrays');


-- H2: Trapping Rain Water
INSERT INTO problems (id, title, description, difficulty, time_limit_seconds, memory_limit_mb, input_format, output_format, constraints, starter_code_java, starter_code_python, starter_code_javascript)
VALUES (
           'f2000002-0000-0000-0000-000000000002',
           'Trapping Rain Water',
           'Given n non-negative integers representing an elevation map where the width of each bar is 1, compute how much water it can trap after raining.

       **Example 1:**
       Input: 0 1 0 2 1 0 1 3 2 1 2 1
       Output: 6

       **Example 2:**
       Input: 4 2 0 3 2 5
       Output: 9',
           'HARD', 5, 256,
           'A single line of space-separated non-negative integers representing the elevation map.',
           'A single integer — total units of water trapped.',
           '1 <= n <= 2 * 10^4
       0 <= height[i] <= 10^5',
           'import java.util.*;

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
       }',
           'def trap(height):
           # Your code here
           return 0

       if __name__ == "__main__":
           height = list(map(int, input().split()))
           print(trap(height))',
           'function trap(height) {
           // Your code here
           return 0;
       }

       const readline = require("readline");
       const rl = readline.createInterface({ input: process.stdin });
       rl.on("line", (line) => {
           console.log(trap(line.split(" ").map(Number)));
           rl.close();
       });'
       );

INSERT INTO test_cases (problem_id, input, expected_output, is_hidden, order_index, explanation)
VALUES
    ('f2000002-0000-0000-0000-000000000002', '0 1 0 2 1 0 1 3 2 1 2 1', '6', false, 0, 'Classic example'),
    ('f2000002-0000-0000-0000-000000000002', '4 2 0 3 2 5', '9', false, 1, 'Valley example'),
    ('f2000002-0000-0000-0000-000000000002', '3 0 0 2 0 4', '10', false, 2, 'Large valley'),
    ('f2000002-0000-0000-0000-000000000002', '1 0 1', '1', true, 3, 'Minimal trap'),
    ('f2000002-0000-0000-0000-000000000002', '0 0 0 0', '0', true, 4, 'No water');


-- H3: Word Ladder
INSERT INTO problems (id, title, description, difficulty, time_limit_seconds, memory_limit_mb, input_format, output_format, constraints, starter_code_java, starter_code_python, starter_code_javascript)
VALUES (
           'f2000003-0000-0000-0000-000000000003',
           'Word Ladder',
           'A transformation sequence from beginWord to endWord uses a dictionary. Each step changes exactly one letter and the result must be in the dictionary.

       Return the number of words in the shortest transformation sequence, or 0 if no sequence exists.

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
       Output: 0',
           'HARD', 10, 256,
           'First line: beginWord.
       Second line: endWord.
       Third line: space-separated words in the dictionary.',
           'A single integer — length of shortest transformation sequence, or 0.',
           '1 <= beginWord.length <= 10
       endWord.length == beginWord.length
       1 <= wordList.length <= 5000
       All words consist of lowercase English letters.',
           'import java.util.*;

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
       }',
           'from collections import deque

       def ladder_length(begin_word, end_word, word_list):
           # Your code here
           return 0

       if __name__ == "__main__":
           begin = input().strip()
           end = input().strip()
           words = input().strip().split()
           print(ladder_length(begin, end, words))',
           'function ladderLength(beginWord, endWord, wordList) {
           // Your code here
           return 0;
       }

       const readline = require("readline");
       const rl = readline.createInterface({ input: process.stdin });
       const lines = [];
       rl.on("line", l => lines.push(l.trim()));
       rl.on("close", () => {
           console.log(ladderLength(lines[0], lines[1], lines[2].split(" ")));
       });'
       );

INSERT INTO test_cases (problem_id, input, expected_output, is_hidden, order_index, explanation)
VALUES
    ('f2000003-0000-0000-0000-000000000003', 'hit
cog
hot dot dog lot log cog', '5', false, 0, 'hit->hot->dot->dog->cog'),
    ('f2000003-0000-0000-0000-000000000003', 'hit
cog
hot dot dog lot log', '0', false, 1, 'cog not in list'),
    ('f2000003-0000-0000-0000-000000000003', 'a
c
a b c', '2', false, 2, 'Single char words'),
    ('f2000003-0000-0000-0000-000000000003', 'hot
dog
hot dog', '2', true, 3, 'Two step'),
    ('f2000003-0000-0000-0000-000000000003', 'red
tax
ted tex tax tad den rex pee', '4', true, 4, 'Longer chain');


-- H4: Largest Rectangle in Histogram
INSERT INTO problems (id, title, description, difficulty, time_limit_seconds, memory_limit_mb, input_format, output_format, constraints, starter_code_java, starter_code_python, starter_code_javascript)
VALUES (
           'f2000004-0000-0000-0000-000000000004',
           'Largest Rectangle in Histogram',
           'Given an array of integers heights representing histogram bar heights (width = 1 each), return the area of the largest rectangle in the histogram.

       **Example 1:**
       Input: 2 1 5 6 2 3
       Output: 10
       Explanation: The largest rectangle has area = 5*2 = 10 (bars at index 2 and 3).

       **Example 2:**
       Input: 2 4
       Output: 4',
           'HARD', 5, 256,
           'A single line of space-separated non-negative integers representing bar heights.',
           'A single integer — the area of the largest rectangle.',
           '1 <= heights.length <= 10^5
       0 <= heights[i] <= 10^4',
           'import java.util.*;

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
       }',
           'def largest_rectangle_area(heights):
           # Your code here
           return 0

       if __name__ == "__main__":
           heights = list(map(int, input().split()))
           print(largest_rectangle_area(heights))',
           'function largestRectangleArea(heights) {
           // Your code here
           return 0;
       }

       const readline = require("readline");
       const rl = readline.createInterface({ input: process.stdin });
       rl.on("line", (line) => {
           console.log(largestRectangleArea(line.split(" ").map(Number)));
           rl.close();
       });'
       );

INSERT INTO test_cases (problem_id, input, expected_output, is_hidden, order_index, explanation)
VALUES
    ('f2000004-0000-0000-0000-000000000004', '2 1 5 6 2 3', '10', false, 0, '5x2 rectangle'),
    ('f2000004-0000-0000-0000-000000000004', '2 4', '4', false, 1, 'Two bars'),
    ('f2000004-0000-0000-0000-000000000004', '6 2 5 4 5 1 6', '12', false, 2, 'Varied heights'),
    ('f2000004-0000-0000-0000-000000000004', '1 1 1 1 1', '5', true, 3, 'All same height'),
    ('f2000004-0000-0000-0000-000000000004', '0 9 0', '9', true, 4, 'Single tall bar');


-- H5: Regular Expression Matching
INSERT INTO problems (id, title, description, difficulty, time_limit_seconds, memory_limit_mb, input_format, output_format, constraints, starter_code_java, starter_code_python, starter_code_javascript)
VALUES (
           'f2000005-0000-0000-0000-000000000005',
           'Regular Expression Matching',
           'Implement regular expression matching with support for . and *.

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
       Output: true',
           'HARD', 5, 256,
           'First line: string s.
       Second line: pattern p.',
           'true or false',
           '1 <= s.length <= 20
       1 <= p.length <= 30
       s contains only lowercase English letters.
       p contains only lowercase English letters, . and *.',
           'import java.util.*;

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
       }',
           'def is_match(s, p):
           # Your code here
           return False

       if __name__ == "__main__":
           s = input().strip()
           p = input().strip()
           print("true" if is_match(s, p) else "false")',
           'function isMatch(s, p) {
           // Your code here
           return false;
       }

       const readline = require("readline");
       const rl = readline.createInterface({ input: process.stdin });
       const lines = [];
       rl.on("line", l => lines.push(l.trim()));
       rl.on("close", () => { console.log(isMatch(lines[0], lines[1])); });'
       );

INSERT INTO test_cases (problem_id, input, expected_output, is_hidden, order_index, explanation)
VALUES
    ('f2000005-0000-0000-0000-000000000005', 'aa
a', 'false', false, 0, 'No match'),
    ('f2000005-0000-0000-0000-000000000005', 'aa
a*', 'true', false, 1, 'Star matches multiple'),
    ('f2000005-0000-0000-0000-000000000005', 'ab
.*', 'true', false, 2, 'Dot-star matches all'),
    ('f2000005-0000-0000-0000-000000000005', 'aab
c*a*b', 'true', true, 3, 'Complex pattern'),
    ('f2000005-0000-0000-0000-000000000005', 'mississippi
mis*is*p*.', 'false', true, 4, 'Tricky mismatch');


-- H6: N-Queens Count
INSERT INTO problems (id, title, description, difficulty, time_limit_seconds, memory_limit_mb, input_format, output_format, constraints, starter_code_java, starter_code_python, starter_code_javascript)
VALUES (
           'f2000006-0000-0000-0000-000000000006',
           'N-Queens Count',
           'Place n queens on an n x n chessboard so that no two queens attack each other.

       Return the number of distinct solutions.

       **Example 1:**
       Input: 4
       Output: 2

       **Example 2:**
       Input: 1
       Output: 1

       **Example 3:**
       Input: 6
       Output: 4',
           'HARD', 10, 256,
           'A single integer n.',
           'A single integer — the number of distinct n-queens solutions.',
           '1 <= n <= 12',
           'import java.util.*;

       public class Main {
           public static int totalNQueens(int n) {
               // Your code here
               return 0;
           }

           public static void main(String[] args) {
               Scanner sc = new Scanner(System.in);
               System.out.println(totalNQueens(Integer.parseInt(sc.nextLine().trim())));
           }
       }',
           'def total_n_queens(n):
           # Your code here
           return 0

       if __name__ == "__main__":
           print(total_n_queens(int(input())))',
           'function totalNQueens(n) {
           // Your code here
           return 0;
       }

       const readline = require("readline");
       const rl = readline.createInterface({ input: process.stdin });
       rl.on("line", (n) => { console.log(totalNQueens(parseInt(n))); rl.close(); });'
       );

INSERT INTO test_cases (problem_id, input, expected_output, is_hidden, order_index, explanation)
VALUES
    ('f2000006-0000-0000-0000-000000000006', '4', '2', false, 0, '4x4 board'),
    ('f2000006-0000-0000-0000-000000000006', '1', '1', false, 1, 'Trivial'),
    ('f2000006-0000-0000-0000-000000000006', '6', '4', false, 2, '6x6 board'),
    ('f2000006-0000-0000-0000-000000000006', '8', '92', true, 3, 'Classic 8-queens'),
    ('f2000006-0000-0000-0000-000000000006', '12', '14200', true, 4, '12x12 board');


-- H7: Minimum Window Substring
INSERT INTO problems (id, title, description, difficulty, time_limit_seconds, memory_limit_mb, input_format, output_format, constraints, starter_code_java, starter_code_python, starter_code_javascript)
VALUES (
           'f2000007-0000-0000-0000-000000000007',
           'Minimum Window Substring',
           'Given two strings s and t, return the minimum window substring of s such that every character in t (including duplicates) is included. If no such window exists, return an empty string.

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
       Output: (empty string)',
           'HARD', 5, 256,
           'First line: string s.
       Second line: string t.',
           'The minimum window substring, or empty string if none exists.',
           '1 <= s.length, t.length <= 10^5
       s and t consist of uppercase and lowercase English letters.',
           'import java.util.*;

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
       }',
           'def min_window(s, t):
           # Your code here
           return ""

       if __name__ == "__main__":
           s = input().strip()
           t = input().strip()
           print(min_window(s, t))',
           'function minWindow(s, t) {
           // Your code here
           return "";
       }

       const readline = require("readline");
       const rl = readline.createInterface({ input: process.stdin });
       const lines = [];
       rl.on("line", l => lines.push(l.trim()));
       rl.on("close", () => { console.log(minWindow(lines[0], lines[1])); });'
       );

INSERT INTO test_cases (problem_id, input, expected_output, is_hidden, order_index, explanation)
VALUES
    ('f2000007-0000-0000-0000-000000000007', 'ADOBECODEBANC
ABC', 'BANC', false, 0, 'Classic case'),
    ('f2000007-0000-0000-0000-000000000007', 'a
a', 'a', false, 1, 'Same string'),
    ('f2000007-0000-0000-0000-000000000007', 'a
aa', '', false, 2, 'Impossible'),
    ('f2000007-0000-0000-0000-000000000007', 'ABCBA
AB', 'AB', true, 3, 'Min window at start'),
    ('f2000007-0000-0000-0000-000000000007', 'aaflslflsldkalskaaa
aaa', 'aaa', true, 4, 'Three same chars');


-- H8: Longest Increasing Path in Matrix
INSERT INTO problems (id, title, description, difficulty, time_limit_seconds, memory_limit_mb, input_format, output_format, constraints, starter_code_java, starter_code_python, starter_code_javascript)
VALUES (
           'f2000008-0000-0000-0000-000000000008',
           'Longest Increasing Path in Matrix',
           'Given an m x n integer matrix, return the length of the longest strictly increasing path.

       From each cell you can move in 4 directions (up, down, left, right). You may not move diagonally or outside the boundary.

       **Example 1:**
       Input:
       3 3
       9 9 4
       6 6 8
       2 1 1
       Output: 4
       Explanation: Path [1,2,6,9]

       **Example 2:**
       Input:
       3 3
       3 4 5
       3 2 6
       2 2 1
       Output: 4
       Explanation: Path [3,4,5,6]',
           'HARD', 5, 256,
           'First line: two integers m n (rows and cols).
       Next m lines: n space-separated integers per row.',
           'A single integer — the length of the longest increasing path.',
           '1 <= m, n <= 200
       0 <= matrix[i][j] <= 2^31 - 1',
           'import java.util.*;

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
       }',
           'def longest_increasing_path(matrix):
           # Your code here
           return 0

       if __name__ == "__main__":
           m, n = map(int, input().split())
           matrix = [list(map(int, input().split())) for _ in range(m)]
           print(longest_increasing_path(matrix))',
           'function longestIncreasingPath(matrix) {
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
       });'
       );

INSERT INTO test_cases (problem_id, input, expected_output, is_hidden, order_index, explanation)
VALUES
    ('f2000008-0000-0000-0000-000000000008', '3 3
9 9 4
6 6 8
2 1 1', '4', false, 0, '1->2->6->9'),
    ('f2000008-0000-0000-0000-000000000008', '3 3
3 4 5
3 2 6
2 2 1', '4', false, 1, '3->4->5->6'),
    ('f2000008-0000-0000-0000-000000000008', '1 1
1', '1', false, 2, 'Single cell'),
    ('f2000008-0000-0000-0000-000000000008', '2 2
1 2
4 3', '4', true, 3, '1->2->3->4'),
    ('f2000008-0000-0000-0000-000000000008', '3 4
7 8 9 1
6 5 4 2
3 2 1 3', '6', true, 4, 'Longer path');


-- H9: Burst Balloons
INSERT INTO problems (id, title, description, difficulty, time_limit_seconds, memory_limit_mb, input_format, output_format, constraints, starter_code_java, starter_code_python, starter_code_javascript)
VALUES (
           'f2000009-0000-0000-0000-000000000009',
           'Burst Balloons',
           'You are given n balloons. Each balloon has a number nums[i]. When you burst balloon i, you gain nums[i-1] * nums[i] * nums[i+1] coins. Out-of-bounds indices are treated as 1.

       Return the maximum coins you can collect by bursting all balloons wisely.

       **Example 1:**
       Input: 3 1 5 8
       Output: 167

       **Example 2:**
       Input: 1 5
       Output: 10',
           'HARD', 10, 256,
           'A single line of space-separated integers representing balloon values.',
           'A single integer — the maximum coins collectible.',
           '1 <= n <= 300
       0 <= nums[i] <= 100',
           'import java.util.*;

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
       }',
           'def max_coins(nums):
           # Your code here
           return 0

       if __name__ == "__main__":
           nums = list(map(int, input().split()))
           print(max_coins(nums))',
           'function maxCoins(nums) {
           // Your code here
           return 0;
       }

       const readline = require("readline");
       const rl = readline.createInterface({ input: process.stdin });
       rl.on("line", (line) => {
           console.log(maxCoins(line.split(" ").map(Number)));
           rl.close();
       });'
       );

INSERT INTO test_cases (problem_id, input, expected_output, is_hidden, order_index, explanation)
VALUES
    ('f2000009-0000-0000-0000-000000000009', '3 1 5 8', '167', false, 0, 'Classic example'),
    ('f2000009-0000-0000-0000-000000000009', '1 5', '10', false, 1, 'Two balloons'),
    ('f2000009-0000-0000-0000-000000000009', '1', '1', false, 2, 'Single balloon'),
    ('f2000009-0000-0000-0000-000000000009', '7 9 8 0 7 1 3 5 5 2 3', '1654', true, 3, 'Larger input'),
    ('f2000009-0000-0000-0000-000000000009', '8 3 6 2 9 8 4 1', '1386', true, 4, 'Eight balloons');


-- H10: Edit Distance
INSERT INTO problems (id, title, description, difficulty, time_limit_seconds, memory_limit_mb, input_format, output_format, constraints, starter_code_java, starter_code_python, starter_code_javascript)
VALUES (
           'f2000010-0000-0000-0000-000000000010',
           'Edit Distance',
           'Given two strings word1 and word2, return the minimum number of operations required to convert word1 to word2.

       You have three operations: Insert a character, Delete a character, Replace a character.

       **Example 1:**
       Input:
       horse
       ros
       Output: 3
       Explanation: horse->rorse->rose->ros

       **Example 2:**
       Input:
       intention
       execution
       Output: 5

       **Example 3:**
       Input:
       abc
       abc
       Output: 0',
           'HARD', 5, 256,
           'First line: word1.
       Second line: word2.',
           'A single integer — the minimum edit distance.',
           '0 <= word1.length, word2.length <= 500
       word1 and word2 consist of lowercase English letters.',
           'import java.util.*;

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
       }',
           'def min_distance(word1, word2):
           # Your code here
           return 0

       if __name__ == "__main__":
           word1 = input().strip()
           word2 = input().strip()
           print(min_distance(word1, word2))',
           'function minDistance(word1, word2) {
           // Your code here
           return 0;
       }

       const readline = require("readline");
       const rl = readline.createInterface({ input: process.stdin });
       const lines = [];
       rl.on("line", l => lines.push(l.trim()));
       rl.on("close", () => { console.log(minDistance(lines[0], lines[1])); });'
       );

INSERT INTO test_cases (problem_id, input, expected_output, is_hidden, order_index, explanation)
VALUES
    ('f2000010-0000-0000-0000-000000000010', 'horse
ros', '3', false, 0, 'horse->rorse->rose->ros'),
    ('f2000010-0000-0000-0000-000000000010', 'intention
execution', '5', false, 1, 'Classic DP'),
    ('f2000010-0000-0000-0000-000000000010', 'abc
abc', '0', false, 2, 'Identical strings'),
    ('f2000010-0000-0000-0000-000000000010', 'abc
', '3', true, 3, 'Delete all chars'),
    ('f2000010-0000-0000-0000-000000000010', 'kitten
sitting', '3', true, 4, 'Classic Levenshtein');