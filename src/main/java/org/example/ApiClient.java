package org.example;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;

public class ApiClient {

    private static final String BASE_URL = "https://openlibrary.org/subjects/";
    private static final String WIKIPEDIA_API = "https://en.wikipedia.org/api/rest_v1/page/summary/";
    private static final Random RANDOM = new Random();

    private static final String[] LEVELS = {
            "Beginner",
            "Intermediate",
            "Advanced"
    };

    /**
     * Fetch courses (books) from Open Library by subject
     * @param searchTerm e.g., "python", "mathematics", "computer_science"
     * @return List of Course objects
     */
    public List<Course> fetchCourses(String searchTerm) {
        List<Course> courses = new ArrayList<>();
        String url = BASE_URL + searchTerm + ".json?limit=20"; // limit to 20 books

        try {
            HttpClient client = HttpClient.newBuilder()
                    .version(HttpClient.Version.HTTP_2)
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("User-Agent", "Mozilla/5.0")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();

            if (responseBody == null || responseBody.isEmpty()) {
                System.out.println("Empty response from Open Library API");
                return courses;
            }

            JSONObject root = new JSONObject(responseBody);
            JSONArray works = root.getJSONArray("works");

            for (int i = 0; i < works.length(); i++) {
                JSONObject work = works.getJSONObject(i);

                String title = work.optString("title", "No Title");
                String workKey = work.optString("key", ""); // Get work key for detailed content
                JSONArray authorsArray = work.optJSONArray("authors");
                String author = "Unknown Author";
                if (authorsArray != null && authorsArray.length() > 0) {
                    author = authorsArray.getJSONObject(0).optString("name", "Unknown Author");
                }

                String category = searchTerm; // use search term as category
                String teachesSkill = "Learn " + title + " by " + author;

                // ⭐ Assign random level
                String level = LEVELS[RANDOM.nextInt(LEVELS.length)];

                String provider = "Open Library";

                courses.add(new Course(title, category, teachesSkill, level, provider, workKey));
            }

        } catch (IOException | InterruptedException e) {
            System.out.println("Error fetching courses: " + e.getMessage());
            e.printStackTrace();
        }

        return courses;
    }

    /**
     * Fetch detailed course content from Open Library using work key
     * @param workKey The Open Library work key (e.g., "/works/OL123456W")
     * @return CourseContent object with detailed information
     */
    public CourseContent fetchCourseContent(String workKey) {
        if (workKey == null || workKey.isEmpty()) {
            return new CourseContent("", "", "", "", "");
        }

        String url = "https://openlibrary.org" + workKey + ".json";

        try {
            HttpClient client = HttpClient.newBuilder()
                    .version(HttpClient.Version.HTTP_2)
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("User-Agent", "Mozilla/5.0")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();

            if (responseBody == null || responseBody.isEmpty()) {
                return new CourseContent("", "", "", "", "");
            }

            JSONObject work = new JSONObject(responseBody);

            String title = work.optString("title", "No Title");
            String description = "";
            
            // Try to get description from different fields
            if (work.has("description")) {
                Object descObj = work.get("description");
                if (descObj instanceof String) {
                    description = (String) descObj;
                } else if (descObj instanceof JSONObject) {
                    description = ((JSONObject) descObj).optString("value", "");
                }
            }

            // Get authors
            JSONArray authorsArray = work.optJSONArray("authors");
            StringBuilder authors = new StringBuilder();
            if (authorsArray != null) {
                for (int i = 0; i < authorsArray.length(); i++) {
                    JSONObject authorObj = authorsArray.getJSONObject(i);
                    JSONObject author = fetchAuthorDetails(authorObj.optString("key", ""));
                    if (author != null) {
                        if (authors.length() > 0) authors.append(", ");
                        authors.append(author.optString("name", "Unknown"));
                    }
                }
            }
            if (authors.length() == 0) authors.append("Unknown Author");

            // Get subjects as topics
            JSONArray subjects = work.optJSONArray("subjects");
            StringBuilder topics = new StringBuilder();
            List<String> subjectList = new ArrayList<>();
            if (subjects != null) {
                for (int i = 0; i < Math.min(subjects.length(), 5); i++) {
                    String subject = subjects.getString(i);
                    subjectList.add(subject);
                    if (topics.length() > 0) topics.append(", ");
                    topics.append(subject);
                }
            }

            // Get first publish date
            String publishDate = work.optString("first_publish_date", "");
            if (publishDate.isEmpty()) {
                JSONArray publishDates = work.optJSONArray("publish_date");
                if (publishDates != null && publishDates.length() > 0) {
                    publishDate = publishDates.getString(0);
                }
            }

            CourseContent content = new CourseContent(title, authors.toString(), description, topics.toString(), publishDate);
            
            // Try to fetch real educational content from Wikipedia/Wikibooks
            // If that fails, fall back to generated content
            boolean fetchedRealContent = fetchWikipediaContent(content, title, subjectList);
            
            if (!fetchedRealContent) {
                // Fallback: Generate detailed course chapters based on the book's subjects and description
                generateCourseChapters(content, title, subjectList, description);
            }
            
            return content;

        } catch (IOException | InterruptedException e) {
            System.out.println("Error fetching course content: " + e.getMessage());
            e.printStackTrace();
            return new CourseContent("", "", "", "", "");
        }
    }

    /**
     * Fetch author details from Open Library
     */
    private JSONObject fetchAuthorDetails(String authorKey) {
        if (authorKey == null || authorKey.isEmpty()) {
            return null;
        }

        try {
            String url = "https://openlibrary.org" + authorKey + ".json";
            HttpClient client = HttpClient.newBuilder()
                    .version(HttpClient.Version.HTTP_2)
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("User-Agent", "Mozilla/5.0")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.body() != null && !response.body().isEmpty()) {
                return new JSONObject(response.body());
            }
        } catch (Exception e) {
            // Ignore errors, return null
        }
        return null;
    }

    /**
     * Fetch real educational content from Wikipedia/Wikibooks
     * @return true if content was successfully fetched, false otherwise
     */
    private boolean fetchWikipediaContent(CourseContent content, String title, List<String> subjects) {
        List<String> chapters = new ArrayList<>();
        List<String> chapterContent = new ArrayList<>();
        
        // Try to fetch Wikipedia content for the main topic
        String mainTopic = title;
        if (subjects != null && !subjects.isEmpty()) {
            mainTopic = subjects.get(0); // Use first subject as main topic
        }
        
        // Clean topic name for API (remove special chars, spaces to underscores)
        String cleanTopic = mainTopic.replaceAll("[^a-zA-Z0-9 ]", "").replace(" ", "_");
        
        // Try Wikipedia first
        String wikiContent = fetchWikipediaPage(cleanTopic);
        if (wikiContent != null && !wikiContent.isEmpty()) {
            chapters.add("Introduction to " + mainTopic);
            chapterContent.add(wikiContent);
            
            // Try to fetch content for related subjects
            if (subjects != null && subjects.size() > 1) {
                for (int i = 1; i < Math.min(subjects.size(), 4); i++) {
                    String subject = subjects.get(i);
                    String cleanSubject = subject.replaceAll("[^a-zA-Z0-9 ]", "").replace(" ", "_");
                    String subjectContent = fetchWikipediaPage(cleanSubject);
                    
                    if (subjectContent != null && !subjectContent.isEmpty()) {
                        chapters.add("Understanding " + subject);
                        chapterContent.add(subjectContent);
                    }
                }
            }
            
            // Add summary chapter
            chapters.add("Summary and Review");
            chapterContent.add(generateSummaryContent(title, subjects));
            
            content.setChapters(chapters);
            content.setChapterContent(chapterContent);
            return true;
        }
        
        // Try Wikibooks as fallback
        String wikibooksContent = fetchWikibooksPage(cleanTopic);
        if (wikibooksContent != null && !wikibooksContent.isEmpty()) {
            chapters.add("Introduction to " + mainTopic);
            chapterContent.add(wikibooksContent);
            
            chapters.add("Summary and Review");
            chapterContent.add(generateSummaryContent(title, subjects));
            
            content.setChapters(chapters);
            content.setChapterContent(chapterContent);
            return true;
        }
        
        return false; // Failed to fetch real content
    }
    
    /**
     * Fetch content from Wikipedia API
     */
    private String fetchWikipediaPage(String topic) {
        try {
            String url = WIKIPEDIA_API + topic;
            HttpClient client = HttpClient.newBuilder()
                    .version(HttpClient.Version.HTTP_2)
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("User-Agent", "PathLearner/1.0 (Educational App)")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();

            if (responseBody == null || responseBody.isEmpty()) {
                return null;
            }

            // Check if response is valid JSON
            if (!responseBody.trim().startsWith("{")) {
                System.out.println("Wikipedia API returned non-JSON response for: " + topic);
                return null;
            }

            JSONObject json;
            try {
                json = new JSONObject(responseBody);
            } catch (Exception e) {
                System.out.println("Failed to parse Wikipedia JSON for: " + topic);
                return null;
            }
            
            // Check for error response
            if (json.has("type") && json.optString("type", "").contains("not_found")) {
                return null; // Page not found
            }
            
            // Extract text content
            String extract = json.optString("extract", "");
            String description = json.optString("description", "");
            
            if (extract == null || extract.isEmpty()) {
                return null;
            }
            
            // Clean the extract text - remove HTML entities, citations, etc.
            extract = extract.replaceAll("&nbsp;", " ")
                            .replaceAll("&amp;", "&")
                            .replaceAll("&lt;", "<")
                            .replaceAll("&gt;", ">")
                            .replaceAll("&quot;", "\"")
                            .replaceAll("&apos;", "'")
                            .replaceAll("&[a-zA-Z]+;", " ") // Remove other HTML entities
                            .replaceAll("\\[\\d+\\]", "") // Remove citation numbers like [1]
                            .replaceAll("\\[edit\\]", "") // Remove edit links
                            .replaceAll("\\[citation needed\\]", "") // Remove citation needed
                            .replaceAll("\\s+", " ") // Normalize whitespace
                            .trim();
            
            // Format as educational content
            StringBuilder content = new StringBuilder();
            content.append("=== ").append(topic.replace("_", " ")).append(" ===\n\n");
            
            if (!description.isEmpty()) {
                content.append("Description: ").append(description).append("\n\n");
            }
            
            content.append("LEARNING CONTENT\n\n");
            content.append(extract).append("\n\n");
            
            // Try to get more detailed content (but don't duplicate if it's the same)
            String fullContent = fetchWikipediaFullPage(topic);
            if (fullContent != null && !fullContent.isEmpty() && !fullContent.equals(extract)) {
                content.append("ADDITIONAL INFORMATION\n\n");
                content.append(fullContent);
            }
            
            return content.toString();

        } catch (Exception e) {
            System.out.println("Error fetching Wikipedia content: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Fetch full Wikipedia page content (simplified text)
     */
    private String fetchWikipediaFullPage(String topic) {
        try {
            // Use Wikipedia's extract API to get plain text (better than HTML)
            String url = "https://en.wikipedia.org/api/rest_v1/page/summary/" + topic;
            HttpClient client = HttpClient.newBuilder()
                    .version(HttpClient.Version.HTTP_2)
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("User-Agent", "PathLearner/1.0 (Educational App)")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();

            if (responseBody == null || responseBody.isEmpty()) {
                return null;
            }
            
            JSONObject json = new JSONObject(responseBody);
            String extract = json.optString("extract", "");
            
            // Clean the text - remove any remaining HTML entities or special characters
            extract = extract.replaceAll("&[a-zA-Z]+;", " ") // Remove HTML entities
                            .replaceAll("\\[\\d+\\]", "") // Remove citation numbers like [1]
                            .replaceAll("\\[edit\\]", "") // Remove edit links
                            .replaceAll("\\s+", " ") // Normalize whitespace
                            .trim();
            
            // Limit to first 1500 characters to keep it manageable
            if (extract.length() > 1500) {
                extract = extract.substring(0, 1500) + "...";
            }
            
            return extract;

        } catch (Exception e) {
            // Ignore errors, return null
            return null;
        }
    }
    
    /**
     * Fetch content from Wikibooks API
     */
    private String fetchWikibooksPage(String topic) {
        try {
            // Try summary API first (simpler, cleaner)
            String url = "https://en.wikibooks.org/api/rest_v1/page/summary/" + topic;
            HttpClient client = HttpClient.newBuilder()
                    .version(HttpClient.Version.HTTP_2)
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("User-Agent", "PathLearner/1.0 (Educational App)")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();

            if (responseBody == null || responseBody.isEmpty()) {
                return null;
            }
            
            try {
                JSONObject json = new JSONObject(responseBody);
                String extract = json.optString("extract", "");
                String description = json.optString("description", "");
                
                if (extract.isEmpty()) {
                    return null;
                }
                
                // Clean the text
                extract = extract.replaceAll("&[a-zA-Z]+;", " ")
                                .replaceAll("\\[\\d+\\]", "")
                                .replaceAll("\\[edit\\]", "")
                                .replaceAll("\\s+", " ")
                                .trim();
                
                StringBuilder content = new StringBuilder();
                content.append("=== ").append(topic.replace("_", " ")).append(" ===\n\n");
                
                if (!description.isEmpty()) {
                    content.append("Description: ").append(description).append("\n\n");
                }
                
                content.append("LEARNING CONTENT\n\n");
                content.append(extract);
                
                return content.toString();
            } catch (Exception e) {
                // If JSON parsing fails, return null
                return null;
            }

        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Generate detailed course chapters based on book information (fallback)
     */
    private void generateCourseChapters(CourseContent content, String title, List<String> subjects, String description) {
        List<String> chapters = new ArrayList<>();
        List<String> chapterContent = new ArrayList<>();
        
        // Chapter 1: Introduction
        chapters.add("Introduction to " + title);
        chapterContent.add(generateIntroductionContent(title, description));
        
        // Generate chapters based on subjects
        if (subjects != null && !subjects.isEmpty()) {
            int chapterNum = 2;
            for (int i = 0; i < Math.min(subjects.size(), 4); i++) {
                String subject = subjects.get(i);
                chapters.add("Understanding " + subject);
                chapterContent.add(generateSubjectContent(subject, title, chapterNum));
                chapterNum++;
            }
        } else {
            // Fallback chapters if no subjects
            chapters.add("Fundamentals and Core Concepts");
            chapterContent.add(generateGenericContent("Fundamentals", title, 2));
            
            chapters.add("Advanced Topics and Applications");
            chapterContent.add(generateGenericContent("Advanced Topics", title, 3));
            
            chapters.add("Practical Exercises and Examples");
            chapterContent.add(generateGenericContent("Practical Exercises", title, 4));
        }
        
        // Final chapter: Summary and Next Steps
        chapters.add("Summary, Review, and Next Steps");
        chapterContent.add(generateSummaryContent(title, subjects));
        
        content.setChapters(chapters);
        content.setChapterContent(chapterContent);
    }
    
    private String generateIntroductionContent(String title, String description) {
        StringBuilder intro = new StringBuilder();
        intro.append("Welcome to ").append(title).append("\n\n");
        
        if (description != null && !description.isEmpty()) {
            intro.append(description).append("\n\n");
        }
        
        intro.append("=== GETTING STARTED ===\n\n");
        intro.append("Before we dive into the content, let's understand what you'll be learning and how to approach this course effectively.\n\n");
        
        intro.append("Course Structure:\n");
        intro.append("This course is organized into logical modules that build upon each other. Each chapter introduces new concepts and reinforces previous learning through examples and practical applications.\n\n");
        
        intro.append("Learning Approach:\n");
        intro.append("1. Read each section carefully\n");
        intro.append("2. Try to understand the concepts before moving forward\n");
        intro.append("3. Practice with the examples provided\n");
        intro.append("4. Review key points at the end of each chapter\n\n");
        
        intro.append("What You Need:\n");
        intro.append("• A willingness to learn and practice\n");
        intro.append("• Time to work through the material\n");
        intro.append("• A notebook or document to take notes\n\n");
        
        intro.append("Ready to begin? Let's start with the fundamentals!");
        
        return intro.toString();
    }
    
    private String generateSubjectContent(String subject, String title, int chapterNum) {
        StringBuilder content = new StringBuilder();
        content.append("=== CHAPTER ").append(chapterNum).append(": ").append(subject.toUpperCase()).append(" ===\n\n");
        
        // Section 1: What is the subject?
        content.append("1. UNDERSTANDING ").append(subject.toUpperCase()).append("\n\n");
        content.append(subject).append(" is a fundamental concept in ").append(title).append(". ");
        content.append("At its core, ").append(subject.toLowerCase()).append(" involves understanding key principles, methodologies, and applications.\n\n");
        
        content.append("Key Concepts:\n");
        content.append("• Definition: ").append(subject).append(" can be defined as the study and application of...\n");
        content.append("• Importance: Understanding ").append(subject.toLowerCase()).append(" is crucial because...\n");
        content.append("• Scope: The field of ").append(subject.toLowerCase()).append(" encompasses...\n\n");
        
        // Section 2: Core Principles
        content.append("2. CORE PRINCIPLES\n\n");
        content.append("Let's explore the fundamental principles of ").append(subject.toLowerCase()).append(":\n\n");
        content.append("Principle 1: Foundation\n");
        content.append("Every aspect of ").append(subject.toLowerCase()).append(" is built upon a solid foundation. ");
        content.append("This foundation includes basic concepts, terminology, and fundamental rules that govern the subject.\n\n");
        
        content.append("Principle 2: Application\n");
        content.append("Theoretical knowledge must be applied in practical scenarios. ");
        content.append("Understanding how ").append(subject.toLowerCase()).append(" works in real-world situations is essential for mastery.\n\n");
        
        content.append("Principle 3: Problem-Solving\n");
        content.append("A key skill in ").append(subject.toLowerCase()).append(" is the ability to identify problems and develop effective solutions. ");
        content.append("This involves analytical thinking and creative approaches.\n\n");
        
        // Section 3: Step-by-Step Learning
        content.append("3. STEP-BY-STEP LEARNING GUIDE\n\n");
        content.append("Follow these steps to master ").append(subject.toLowerCase()).append(":\n\n");
        content.append("Step 1: Start with Basics\n");
        content.append("Begin by understanding the most basic concepts. Don't rush - take time to fully comprehend each idea before moving forward.\n\n");
        
        content.append("Step 2: Practice Regularly\n");
        content.append("Practice is essential. Work through examples, solve problems, and apply what you've learned in different contexts.\n\n");
        
        content.append("Step 3: Build Complexity Gradually\n");
        content.append("As you become comfortable with basic concepts, gradually introduce more complex topics. ");
        content.append("Each new concept builds upon previous knowledge.\n\n");
        
        content.append("Step 4: Apply Your Knowledge\n");
        content.append("Find opportunities to apply your understanding of ").append(subject.toLowerCase()).append(" in practical situations. ");
        content.append("Real-world application reinforces learning.\n\n");
        
        // Section 4: Examples and Applications
        content.append("4. EXAMPLES AND APPLICATIONS\n\n");
        content.append("Let's look at some practical examples of ").append(subject.toLowerCase()).append(":\n\n");
        content.append("Example 1: Basic Application\n");
        content.append("Consider a simple scenario where ").append(subject.toLowerCase()).append(" is applied. ");
        content.append("In this case, we can see how the fundamental principles work together to achieve a specific outcome.\n\n");
        
        content.append("Example 2: Intermediate Application\n");
        content.append("A more complex example demonstrates how multiple concepts interact. ");
        content.append("This shows the interconnected nature of different aspects of ").append(subject.toLowerCase()).append(".\n\n");
        
        content.append("Example 3: Advanced Application\n");
        content.append("An advanced example illustrates sophisticated applications. ");
        content.append("This requires a deep understanding of all previous concepts working in harmony.\n\n");
        
        // Section 5: Common Challenges
        content.append("5. COMMON CHALLENGES AND SOLUTIONS\n\n");
        content.append("As you learn ").append(subject.toLowerCase()).append(", you may encounter these common challenges:\n\n");
        content.append("Challenge 1: Understanding Complex Concepts\n");
        content.append("Solution: Break down complex ideas into smaller, manageable parts. Study each part individually, then see how they connect.\n\n");
        
        content.append("Challenge 2: Applying Theory to Practice\n");
        content.append("Solution: Start with simple applications and gradually work toward more complex scenarios. Practice regularly.\n\n");
        
        content.append("Challenge 3: Retaining Information\n");
        content.append("Solution: Use active learning techniques - take notes, create summaries, teach concepts to others, and review regularly.\n\n");
        
        // Section 6: Key Takeaways
        content.append("6. KEY TAKEAWAYS\n\n");
        content.append("By the end of this chapter, you should understand:\n");
        content.append("✓ The fundamental concepts of ").append(subject).append("\n");
        content.append("✓ How to apply these concepts in practice\n");
        content.append("✓ Common approaches and methodologies\n");
        content.append("✓ How to overcome typical learning challenges\n\n");
        
        content.append("Practice Exercise:\n");
        content.append("Take a moment to reflect on what you've learned. ");
        content.append("Try to explain ").append(subject.toLowerCase()).append(" in your own words. ");
        content.append("Think of at least one real-world application of these concepts.\n\n");
        
        content.append("Ready for the next chapter? Let's continue building your knowledge!");
        
        return content.toString();
    }
    
    private String generateGenericContent(String topic, String title, int chapterNum) {
        StringBuilder content = new StringBuilder();
        content.append("=== CHAPTER ").append(chapterNum).append(": ").append(topic.toUpperCase()).append(" ===\n\n");
        
        content.append("1. INTRODUCTION TO ").append(topic.toUpperCase()).append("\n\n");
        content.append("In this chapter, we'll explore ").append(topic.toLowerCase()).append(" in the context of ").append(title).append(". ");
        content.append("This is a crucial topic that builds upon everything you've learned so far.\n\n");
        
        content.append("What You'll Learn:\n");
        content.append("• Fundamental concepts and their applications\n");
        content.append("• Step-by-step processes and methodologies\n");
        content.append("• Real-world examples and case studies\n");
        content.append("• Best practices and professional techniques\n");
        content.append("• Common mistakes and how to avoid them\n\n");
        
        content.append("2. CORE CONCEPTS\n\n");
        content.append("Let's start by understanding the essential concepts:\n\n");
        content.append("Concept 1: Basic Understanding\n");
        content.append("The foundation of ").append(topic.toLowerCase()).append(" begins with understanding basic principles. ");
        content.append("These principles form the building blocks for more advanced topics.\n\n");
        
        content.append("Concept 2: Intermediate Applications\n");
        content.append("Once you understand the basics, you can apply them in more complex scenarios. ");
        content.append("This involves combining multiple concepts and understanding their interactions.\n\n");
        
        content.append("Concept 3: Advanced Techniques\n");
        content.append("Advanced applications require a deep understanding of all previous concepts. ");
        content.append("At this level, you can solve complex problems and create sophisticated solutions.\n\n");
        
        content.append("3. PRACTICAL GUIDE\n\n");
        content.append("Here's a practical guide to mastering ").append(topic.toLowerCase()).append(":\n\n");
        content.append("Step 1: Understand the Theory\n");
        content.append("Begin by studying the theoretical foundations. Read carefully, take notes, and make sure you understand each concept before proceeding.\n\n");
        
        content.append("Step 2: Work Through Examples\n");
        content.append("Examples help bridge the gap between theory and practice. Work through each example carefully, understanding why each step is taken.\n\n");
        
        content.append("Step 3: Practice Independently\n");
        content.append("Try solving problems on your own. Start with simple exercises and gradually increase difficulty as your confidence grows.\n\n");
        
        content.append("Step 4: Apply to Real Scenarios\n");
        content.append("Find opportunities to apply what you've learned in real-world situations. This reinforces your understanding and builds practical skills.\n\n");
        
        content.append("4. EXAMPLES AND EXERCISES\n\n");
        content.append("Example 1: Basic Application\n");
        content.append("Let's start with a simple example that demonstrates the basic concepts of ").append(topic.toLowerCase()).append(".\n\n");
        content.append("Scenario: [A practical scenario related to the topic]\n");
        content.append("Solution: [Step-by-step solution showing how to apply the concepts]\n");
        content.append("Explanation: [Why this approach works and what we learned]\n\n");
        
        content.append("Example 2: Intermediate Application\n");
        content.append("Now let's look at a more complex example that requires combining multiple concepts.\n\n");
        content.append("Scenario: [A more complex scenario]\n");
        content.append("Solution: [Detailed solution process]\n");
        content.append("Key Points: [Important takeaways from this example]\n\n");
        
        content.append("Practice Exercise:\n");
        content.append("Now it's your turn! Try to solve a similar problem on your own. ");
        content.append("Use the examples as a guide, but try to work through it independently.\n\n");
        
        content.append("5. COMMON PITFALLS AND SOLUTIONS\n\n");
        content.append("As you learn ").append(topic.toLowerCase()).append(", watch out for these common mistakes:\n\n");
        content.append("Pitfall 1: Rushing Through Concepts\n");
        content.append("Mistake: Trying to learn too quickly without fully understanding each concept.\n");
        content.append("Solution: Take your time. Master each concept before moving to the next.\n\n");
        
        content.append("Pitfall 2: Skipping Practice\n");
        content.append("Mistake: Reading theory but not practicing.\n");
        content.append("Solution: Regular practice is essential. Work through examples and exercises consistently.\n\n");
        
        content.append("Pitfall 3: Not Asking Questions\n");
        content.append("Mistake: Struggling silently when you don't understand something.\n");
        content.append("Solution: Review the material, try different approaches, and break problems into smaller parts.\n\n");
        
        content.append("6. SUMMARY AND REVIEW\n\n");
        content.append("In this chapter, we covered:\n");
        content.append("• The fundamental concepts of ").append(topic).append("\n");
        content.append("• How to apply these concepts in practice\n");
        content.append("• Step-by-step processes and methodologies\n");
        content.append("• Real-world examples and applications\n");
        content.append("• Common pitfalls and how to avoid them\n\n");
        
        content.append("Key Takeaway:\n");
        content.append("The most important thing to remember is that ").append(topic.toLowerCase()).append(" requires both theoretical understanding and practical application. ");
        content.append("Continue practicing and applying what you've learned.\n\n");
        
        content.append("Next Steps:\n");
        content.append("Review the key concepts from this chapter. Make sure you understand each section before moving forward. ");
        content.append("Practice with additional exercises to reinforce your learning.");
        
        return content.toString();
    }
    
    private String generateSummaryContent(String title, List<String> subjects) {
        StringBuilder summary = new StringBuilder();
        summary.append("=== FINAL CHAPTER: COURSE SUMMARY AND REVIEW ===\n\n");
        summary.append("Congratulations! You've completed the course on ").append(title).append("!\n\n");
        
        summary.append("1. WHAT YOU'VE ACCOMPLISHED\n\n");
        summary.append("Throughout this course, you've gained comprehensive knowledge in:\n\n");
        if (subjects != null && !subjects.isEmpty()) {
            for (int i = 0; i < subjects.size(); i++) {
                summary.append("Chapter ").append(i + 2).append(": ").append(subjects.get(i)).append("\n");
                summary.append("You learned the fundamental concepts, practical applications, and real-world uses of ").append(subjects.get(i).toLowerCase()).append(".\n\n");
            }
        } else {
            summary.append("• Fundamental concepts and core principles\n");
            summary.append("• Advanced topics and sophisticated applications\n");
            summary.append("• Practical skills and professional techniques\n");
            summary.append("• Problem-solving strategies and methodologies\n\n");
        }
        
        summary.append("2. COMPREHENSIVE REVIEW\n\n");
        summary.append("Let's review the key concepts from each chapter:\n\n");
        summary.append("Chapter 1 - Introduction:\n");
        summary.append("You learned the basics of ").append(title).append(", including fundamental concepts, terminology, and the overall structure of the subject.\n\n");
        
        if (subjects != null && !subjects.isEmpty()) {
            for (int i = 0; i < subjects.size(); i++) {
                summary.append("Chapter ").append(i + 2).append(" - ").append(subjects.get(i)).append(":\n");
                summary.append("You mastered the core principles, practical applications, and problem-solving techniques related to ").append(subjects.get(i).toLowerCase()).append(".\n\n");
            }
        }
        
        summary.append("3. SKILLS YOU'VE DEVELOPED\n\n");
        summary.append("Through this course, you've developed several important skills:\n\n");
        summary.append("✓ Analytical Thinking: You can break down complex problems and analyze them systematically.\n\n");
        summary.append("✓ Practical Application: You can apply theoretical knowledge to solve real-world problems.\n\n");
        summary.append("✓ Problem-Solving: You've learned various approaches to tackle challenges effectively.\n\n");
        summary.append("✓ Critical Evaluation: You can assess different solutions and choose the most appropriate approach.\n\n");
        
        summary.append("4. SELF-ASSESSMENT\n\n");
        summary.append("Take a moment to assess your understanding:\n\n");
        summary.append("Can you:\n");
        summary.append("• Explain the main concepts in your own words?\n");
        summary.append("• Apply what you've learned to new situations?\n");
        summary.append("• Identify when and how to use different techniques?\n");
        summary.append("• Solve problems using the methods you've learned?\n\n");
        
        summary.append("If you answered yes to most of these, you've successfully mastered the course content!\n\n");
        
        summary.append("5. CONTINUING YOUR LEARNING JOURNEY\n\n");
        summary.append("Learning doesn't stop here. Here's how to continue growing:\n\n");
        summary.append("Immediate Next Steps:\n");
        summary.append("1. Review any chapters where you feel less confident\n");
        summary.append("2. Practice with additional exercises and problems\n");
        summary.append("3. Try teaching the concepts to someone else (this reinforces your own understanding)\n\n");
        
        summary.append("Long-term Learning:\n");
        summary.append("1. Explore advanced topics and specialized areas\n");
        summary.append("2. Join communities related to ").append(title).append("\n");
        summary.append("3. Work on real-world projects to apply your skills\n");
        summary.append("4. Read additional resources and stay updated with new developments\n");
        summary.append("5. Consider taking advanced courses or certifications\n\n");
        
        summary.append("6. FINAL THOUGHTS\n\n");
        summary.append("You've completed a comprehensive course on ").append(title).append(". ");
        summary.append("The knowledge and skills you've gained are valuable and will serve you well in your continued learning and professional development.\n\n");
        
        summary.append("Remember:\n");
        summary.append("• Practice is essential for mastery\n");
        summary.append("• Learning is a continuous process\n");
        summary.append("• Don't be afraid to make mistakes - they're part of learning\n");
        summary.append("• Stay curious and keep exploring\n\n");
        
        summary.append("Congratulations on your achievement! You've taken an important step in your learning journey.\n\n");
        summary.append("Thank you for completing this course. Best of luck with your future learning endeavors!");
        
        return summary.toString();
    }

    // Test the API client
    public static void main(String[] args) {
        ApiClient client = new ApiClient();
        List<Course> courses = client.fetchCourses("python");

        System.out.println("Fetched " + courses.size() + " courses:");
        for (Course c : courses) {
            System.out.println(c);
        }
    }
}
