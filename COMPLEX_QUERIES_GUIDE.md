# Complex JPA Queries with Specifications and Predicates

This guide demonstrates how to write complex queries in Spring Boot using JPA Specifications and Predicates, avoiding raw SQL while leveraging the full power of the ORM.

## Table of Contents

1. [Understanding JPA Specifications](#understanding-jpa-specifications)
2. [Basic Predicate Patterns](#basic-predicate-patterns)
3. [Complex Query Examples](#complex-query-examples)
4. [Advanced Patterns](#advanced-patterns)
5. [Best Practices](#best-practices)

## Understanding JPA Specifications

### What are Specifications?

Specifications are a way to build dynamic queries using a fluent API. They allow you to:
- Combine multiple conditions dynamically
- Create reusable query components
- Build complex queries without raw SQL
- Maintain type safety

### Key Components

```java
// Repository must extend JpaSpecificationExecutor
public interface FeedPostRepository extends JpaRepository<FeedPosts, Long>, JpaSpecificationExecutor<FeedPosts> {
    // Your methods here
}

// Service uses Specifications
Specification<FeedPosts> spec = (root, query, cb) -> {
    // Your conditions here
    return cb.conjunction(); // Always true
};
```

## Basic Predicate Patterns

### 1. Simple Equality

```java
// Find posts by type
Specification<FeedPosts> spec = (root, query, cb) -> 
    cb.equal(root.get("type"), FeedPosts.FeedPostType.text);
```

### 2. Multiple Conditions with AND

```java
// Find posts by type AND author
Specification<FeedPosts> spec = (root, query, cb) -> 
    cb.and(
        cb.equal(root.get("type"), FeedPosts.FeedPostType.text),
        cb.equal(root.get("author").get("id"), authorId)
    );
```

### 3. Multiple Conditions with OR

```java
// Find posts by type OR author
Specification<FeedPosts> spec = (root, query, cb) -> 
    cb.or(
        cb.equal(root.get("type"), FeedPosts.FeedPostType.text),
        cb.equal(root.get("author").get("id"), authorId)
    );
```

### 4. Date Range Queries

```java
// Find posts within date range
Specification<FeedPosts> spec = (root, query, cb) -> 
    cb.between(root.get("createdAt"), startDate, endDate);
```

### 5. String Pattern Matching

```java
// Case-insensitive content search
Specification<FeedPosts> spec = (root, query, cb) -> 
    cb.like(cb.lower(root.get("content")), "%" + keyword.toLowerCase() + "%");
```

## Complex Query Examples

### Example 1: Multi-criteria Search

```java
public List<FeedPosts> findPostsByAdvancedCriteria(
        FeedPosts.FeedPostType type,
        Long authorId,
        ZonedDateTime startDate,
        ZonedDateTime endDate,
        String contentKeyword,
        boolean isAiGenerated) {

    Specification<FeedPosts> spec = Specification.where(null);

    // Add type filter
    if (type != null) {
        spec = spec.and((root, query, cb) -> cb.equal(root.get("type"), type));
    }

    // Add author filter
    if (authorId != null) {
        spec = spec.and((root, query, cb) -> cb.equal(root.get("author").get("id"), authorId));
    }

    // Add date range filter
    if (startDate != null && endDate != null) {
        spec = spec.and((root, query, cb) -> 
            cb.between(root.get("createdAt"), startDate, endDate));
    }

    // Add content keyword filter
    if (contentKeyword != null && !contentKeyword.trim().isEmpty()) {
        spec = spec.and((root, query, cb) -> 
            cb.like(cb.lower(root.get("content")), "%" + contentKeyword.toLowerCase() + "%"));
    }

    // Add AI generation filter
    spec = spec.and((root, query, cb) -> cb.equal(root.get("isAiGenerated"), isAiGenerated));

    return feedPostRepository.findAll(spec);
}
```

**Key Learning Points:**
- Use `Specification.where(null)` to start with an empty specification
- Chain conditions with `.and()` and `.or()`
- Check for null values before adding conditions
- Use `cb.lower()` for case-insensitive string matching

### Example 2: Subqueries for Aggregation

```java
public List<FeedPosts> findHighEngagementPosts(int minReactions, int minComments) {
    Specification<FeedPosts> spec = (root, query, cb) -> {
        // Subquery for reactions count
        var reactionSubquery = query.subquery(Long.class);
        var reactionRoot = reactionSubquery.from(Reactions.class);
        reactionSubquery.select(cb.count(reactionRoot))
                .where(cb.equal(reactionRoot.get("post"), root));

        // Subquery for comments count
        var commentSubquery = query.subquery(Long.class);
        var commentRoot = commentSubquery.from(Comments.class);
        commentSubquery.select(cb.count(commentRoot))
                .where(cb.equal(commentRoot.get("post"), root));

        // Combine conditions with OR
        return cb.or(
            cb.greaterThan(reactionSubquery, (long) minReactions),
            cb.greaterThan(commentSubquery, (long) minComments)
        );
    };

    return feedPostRepository.findAll(spec);
}
```

**Key Learning Points:**
- Use `query.subquery()` to create subqueries
- Use `cb.count()` for aggregation
- Reference the main query's root in subqueries
- Combine subqueries with logical operators

### Example 3: Cross-Entity Relationships

```java
public List<FeedPosts> findPostsByUserReactionPattern(Reactions.ReactionType reactionType, int minReactionCount) {
    Specification<FeedPosts> spec = (root, query, cb) -> {
        // Subquery to find users with specific reaction pattern
        var userSubquery = query.subquery(Long.class);
        var reactionRoot = userSubquery.from(Reactions.class);
        userSubquery.select(reactionRoot.get("user").get("id"))
                .where(cb.and(
                    cb.equal(reactionRoot.get("reactionType"), reactionType),
                    cb.equal(reactionRoot.get("user"), root.get("author"))
                ))
                .groupBy(reactionRoot.get("user").get("id"))
                .having(cb.greaterThan(cb.count(reactionRoot), (long) minReactionCount));

        return cb.in(root.get("author").get("id")).value(userSubquery);
    };

    return feedPostRepository.findAll(spec);
}
```

**Key Learning Points:**
- Use `groupBy()` and `having()` for grouped conditions
- Use `cb.in()` to check if a value is in a subquery result
- Navigate relationships with dot notation

### Example 4: Complex JOIN Operations

```java
public List<FeedPosts> findPostsWithUpcomingEvents(LocalDate startDate, LocalDate endDate) {
    Specification<FeedPosts> spec = (root, query, cb) -> {
        // Join with Events entity
        var eventJoin = root.join("events", jakarta.persistence.criteria.JoinType.INNER);
        
        return cb.and(
            cb.between(eventJoin.get("date"), startDate, endDate),
            cb.equal(root.get("type"), FeedPosts.FeedPostType.event)
        );
    };

    return feedPostRepository.findAll(spec);
}
```

**Key Learning Points:**
- Use `root.join()` to create JOINs
- Specify join type (INNER, LEFT, RIGHT)
- Access joined entity fields with the join variable

### Example 5: Array/Collection Operations

```java
public List<FeedPosts> findMediaPostsWithUserReactions(List<Long> userIds, Reactions.ReactionType reactionType) {
    Specification<FeedPosts> spec = (root, query, cb) -> {
        // Ensure it's a media post
        var mediaTypeCondition = cb.equal(root.get("type"), FeedPosts.FeedPostType.photo);
        
        // Ensure it has media URLs
        var hasMediaCondition = cb.isNotNull(root.get("mediaUrls"));
        
        // Subquery to check if specific users have reacted
        var reactionSubquery = query.subquery(Long.class);
        var reactionRoot = reactionSubquery.from(Reactions.class);
        reactionSubquery.select(cb.count(reactionRoot))
                .where(cb.and(
                    cb.equal(reactionRoot.get("post"), root),
                    cb.in(reactionRoot.get("user").get("id")).value(userIds),
                    cb.equal(reactionRoot.get("reactionType"), reactionType)
                ));

        var hasReactionsCondition = cb.greaterThan(reactionSubquery, 0L);

        return cb.and(mediaTypeCondition, hasMediaCondition, hasReactionsCondition);
    };

    return feedPostRepository.findAll(spec);
}
```

**Key Learning Points:**
- Use `cb.isNotNull()` to check for non-null values
- Use `cb.in()` with a list of values
- Combine multiple conditions with `cb.and()`

## Advanced Patterns

### Pattern 1: Dynamic Specification Building

```java
public class FeedPostSpecifications {
    
    public static Specification<FeedPosts> hasType(FeedPosts.FeedPostType type) {
        return (root, query, cb) -> cb.equal(root.get("type"), type);
    }
    
    public static Specification<FeedPosts> byAuthor(Long authorId) {
        return (root, query, cb) -> cb.equal(root.get("author").get("id"), authorId);
    }
    
    public static Specification<FeedPosts> inDateRange(ZonedDateTime start, ZonedDateTime end) {
        return (root, query, cb) -> cb.between(root.get("createdAt"), start, end);
    }
    
    public static Specification<FeedPosts> withContentKeyword(String keyword) {
        return (root, query, cb) -> 
            cb.like(cb.lower(root.get("content")), "%" + keyword.toLowerCase() + "%");
    }
    
    // Usage
    public List<FeedPosts> findPosts(FeedPosts.FeedPostType type, Long authorId) {
        Specification<FeedPosts> spec = hasType(type).and(byAuthor(authorId));
        return feedPostRepository.findAll(spec);
    }
}
```

### Pattern 2: Reusable Subquery Specifications

```java
public class SubquerySpecifications {
    
    public static Specification<FeedPosts> hasMinReactions(long minReactions) {
        return (root, query, cb) -> {
            var subquery = query.subquery(Long.class);
            var reactionRoot = subquery.from(Reactions.class);
            subquery.select(cb.count(reactionRoot))
                    .where(cb.equal(reactionRoot.get("post"), root));
            return cb.greaterThan(subquery, minReactions);
        };
    }
    
    public static Specification<FeedPosts> hasMinComments(long minComments) {
        return (root, query, cb) -> {
            var subquery = query.subquery(Long.class);
            var commentRoot = subquery.from(Comments.class);
            subquery.select(cb.count(commentRoot))
                    .where(cb.equal(commentRoot.get("post"), root));
            return cb.greaterThan(subquery, minComments);
        };
    }
}
```

### Pattern 3: Complex Aggregation Queries

```java
public List<FeedPosts> findPostsWithEngagementScore() {
    Specification<FeedPosts> spec = (root, query, cb) -> {
        // Calculate engagement score: reactions + comments
        var reactionSubquery = query.subquery(Long.class);
        var reactionRoot = reactionSubquery.from(Reactions.class);
        reactionSubquery.select(cb.count(reactionRoot))
                .where(cb.equal(reactionRoot.get("post"), root));

        var commentSubquery = query.subquery(Long.class);
        var commentRoot = commentSubquery.from(Comments.class);
        commentSubquery.select(cb.count(commentRoot))
                .where(cb.equal(commentRoot.get("post"), root));

        // Engagement score > 10
        return cb.greaterThan(
            cb.sum(reactionSubquery, commentSubquery), 
            10L
        );
    };

    return feedPostRepository.findAll(spec);
}
```

## Best Practices

### 1. Performance Considerations

```java
// ❌ Bad: N+1 query problem
List<FeedPosts> posts = feedPostRepository.findAll();
posts.forEach(post -> {
    List<Reactions> reactions = reactionRepository.findByPostId(post.getId());
    // Process reactions
});

// ✅ Good: Use JOIN FETCH
@Query("SELECT DISTINCT fp FROM FeedPosts fp " +
       "LEFT JOIN FETCH fp.reactions " +
       "LEFT JOIN FETCH fp.comments " +
       "WHERE fp.type = :type")
List<FeedPosts> findPostsWithReactionsAndComments(@Param("type") FeedPosts.FeedPostType type);
```

### 2. Null Safety

```java
// ❌ Bad: Potential NPE
Specification<FeedPosts> spec = (root, query, cb) -> 
    cb.equal(root.get("author").get("id"), authorId);

// ✅ Good: Null check
Specification<FeedPosts> spec = (root, query, cb) -> {
    if (authorId == null) return cb.conjunction();
    return cb.equal(root.get("author").get("id"), authorId);
};
```

### 3. Reusable Specifications

```java
// ✅ Good: Reusable components
public class FeedPostSpecs {
    public static Specification<FeedPosts> isAiGenerated() {
        return (root, query, cb) -> cb.equal(root.get("isAiGenerated"), true);
    }
    
    public static Specification<FeedPosts> hasMedia() {
        return (root, query, cb) -> cb.isNotNull(root.get("mediaUrls"));
    }
    
    public static Specification<FeedPosts> byType(FeedPosts.FeedPostType type) {
        return (root, query, cb) -> cb.equal(root.get("type"), type);
    }
}

// Usage
Specification<FeedPosts> spec = FeedPostSpecs.isAiGenerated()
    .and(FeedPostSpecs.hasMedia())
    .and(FeedPostSpecs.byType(FeedPosts.FeedPostType.photo));
```

### 4. Error Handling

```java
public List<FeedPosts> findPostsSafely(Specification<FeedPosts> spec) {
    try {
        return feedPostRepository.findAll(spec);
    } catch (Exception e) {
        log.error("Error executing specification query", e);
        return Collections.emptyList();
    }
}
```

## API Usage Examples

### 1. Multi-criteria Search
```bash
GET /api/v1/advanced/feedposts/search?type=text&authorId=1&contentKeyword=team&isAiGenerated=false
```

### 2. High Engagement Posts
```bash
GET /api/v1/advanced/feedposts/high-engagement?minReactions=5&minComments=3
```

### 3. User Reaction Pattern
```bash
GET /api/v1/advanced/feedposts/user-reaction-pattern?reactionType=like&minReactionCount=3
```

### 4. Poll Posts with Dominant Option
```bash
GET /api/v1/advanced/feedposts/poll-dominant-option?dominantOption=Option A
```

## Summary

JPA Specifications provide a powerful way to build complex queries without writing raw SQL. Key benefits:

1. **Type Safety**: Compile-time checking of entity properties
2. **Reusability**: Build specifications that can be combined
3. **Dynamic Queries**: Add conditions based on runtime parameters
4. **Maintainability**: Easy to modify and extend
5. **Performance**: Leverage database optimization

Remember to:
- Always check for null values before adding conditions
- Use JOIN FETCH for related entities to avoid N+1 queries
- Build reusable specification components
- Consider performance implications of complex subqueries
- Test your specifications with various data scenarios 