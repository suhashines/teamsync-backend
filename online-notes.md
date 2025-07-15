### tables : 

**human entity** - users

**project oriented** - projects, tasks, projectmembers, taskstatushistory , 

<!-- tasks_attachments --> created by hibernate

**social media** - feedposts, ---| events          , comments , 
                                 | appreciations
                                 | pollvotes

<!-- events_participants  
feed_posts_media_urls 
feed_posts_poll_options -->

**messaging** - channels(channel_type), messages

**special table** -  reactions (message_id,post_id,comment_id) -> only one of them is valid




# Derived Query Methods : 

Let's dive into the convention behind Spring Data JPA's repository method names. In Spring Data, you can define query methods by simply declaring method names that follow a specific pattern. The framework then automatically generates the implementation based on those names, saving you from writing boilerplate query code.

---

## 1. **The Naming Convention**

### Basic Structure

A repository method name is generally structured as follows:

* **Prefix:** Most commonly `findBy`, but you can also use `readBy`, `queryBy`, or `getBy` (they all behave the same way).
* **Property:** The entity attribute name exactly as it is defined in your model.
* **Operator/Keyword:** You can specify condition operators such as `And`, `Or`, `Between`, `LessThan`, `GreaterThan`, `Containing`, etc. These operators help you build complex queries from a method name.

For example, if you have an entity with an attribute `name`, a method named `findByName(String name)` will return all records with a matching name.

---

## 2. **Common Operators and Keywords**

Here are some useful examples:

* **Equality:**

  * `findByName(String name)` → finds by equality.
* **Logical AND / OR:**

  * `findByNameAndCategory(String name, String category)`
    Uses `And` to match both attributes.
* **Comparison Operators:**

  * `findByPriceGreaterThan(Double price)`
    Finds all entries with a price greater than the given value.
  * `findByPriceLessThan(Double price)`
    Finds those with a price less than the given value.
* **Between:**

  * `findByPriceBetween(Double minPrice, Double maxPrice)`
    Finds entries with price values between the two specified.
* **Like/Containing:**

  * `findByNameContaining(String keyword)`
    Finds entries where the name contains the given keyword (this translates to SQL's `LIKE %keyword%`).
  * Adding `IgnoreCase` (e.g., `findByNameContainingIgnoreCase`) helps with case-insensitive searches.
* **Ordering:**

  * `findByCategoryOrderByPriceAsc(String category)`
    Returns data ordered by price in ascending order.

The key is that each part of the method name corresponds directly to an entity field and an intended condition.

---


## 3. **Detailed Example**

Let’s consider the `Product` entity from our earlier example:

```java
@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Double price;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    // Getters and setters
}
```

Now, we want to build a repository interface with methods that let us search using criteria based on the product's attributes.

### ProductRepository Example

```java
public interface ProductRepository extends JpaRepository<Product, Long> {

    // 1. Find products by exact name match.
    List<Product> findByName(String name);

    // 2. Find products whose names contain a specified keyword (case insensitive).
    List<Product> findByNameContainingIgnoreCase(String keyword);

    // 3. Find products within a specified price range.
    List<Product> findByPriceBetween(Double minPrice, Double maxPrice);

    // 4. Find products that have a price greater than a specified value.
    List<Product> findByPriceGreaterThan(Double price);

    // 5. Find products by category name (navigating through relationship).
    List<Product> findByCategory_Name(String categoryName);

    // 6. Combine conditions: category, price range, and name keyword.
    List<Product> findByCategory_NameAndPriceBetweenAndNameContainingIgnoreCase(
        String categoryName, Double minPrice, Double maxPrice, String keyword
    );
}
```

### Explanation

1. **`findByName`:**
   This method tells Spring Data to look for records where the `name` property of `Product` exactly matches the provided string.

2. **`findByNameContainingIgnoreCase`:**
   Here, the `Containing` keyword is translated into a `LIKE` query with `%keyword%`, and `IgnoreCase` ensures the search is case-insensitive.

3. **`findByPriceBetween`:**
   This method searches for products where the `price` falls within the given range (inclusive).

4. **`findByPriceGreaterThan`:**
   Uses the `GreaterThan` keyword to compare the product's price to the passed value.

5. **`findByCategory_Name`:**
   When you have a relationship (i.e., a `Product` belongs to a `Category`), you can traverse that relationship in the method name using an underscore (`_`) or direct camel case navigation. This method finds products that belong to the category with a given `name`.

6. **`findByCategory_NameAndPriceBetweenAndNameContainingIgnoreCase`:**
   This example combines multiple conditions:

   * It first traverses into the `Category` and matches on `name`.
   * Then it filters products in the given price range.
   * Finally, it matches those products whose names contain the specified keyword in a case-insensitive manner.

The method naming should directly reflect the entity model's attributes. Each part of the name (after `findBy`) represents a clause in the `WHERE` part of the SQL query.

---

## 4. **Putting It All Together in a Service**

To better illustrate, let's create a service method that uses one of the repository functions:

```java
@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    /**
     * Returns products based on multiple filters: category name, price range, and a keyword in the product name.
     */
    public List<Product> searchProducts(String categoryName, Double minPrice, Double maxPrice, String keyword) {
        return productRepository.findByCategory_NameAndPriceBetweenAndNameContainingIgnoreCase(
                categoryName, minPrice, maxPrice, keyword
        );
    }
}
```

This service method neatly encapsulates the repository call. The method name in the repository tells Spring Data exactly how to build the SQL query behind the scenes based on our criteria.

---

## 5. **Conclusion**

* **Understand the entity model:** The attributes in your entity (and related entities) directly inform your method names.
* **Prefix with a keyword:** Use `findBy` (or `readBy`, `queryBy`, `getBy`) to start your method.
* **Chain attribute names and condition keywords:** Conditions like `Between`, `Containing`, and relational navigation (`Category_Name`) all follow directly from your model properties.
* **Combine conditions with `And` or `Or`:** This lets you formulate complex queries without writing any SQL.

With these conventions, building complex queries becomes a natural extension of your domain model, letting you write clean, readable, and maintainable query methods in your repository interfaces.

Would you like more examples or further clarification on any part?


| Keyword                                            | Meaning              |
| -------------------------------------------------- | -------------------- |
| `findBy` / `readBy` / `getBy`                      | Start of the method  |
| `And`, `Or`                                        | Combine conditions   |
| `Is`, `Equals`                                     | Exact match          |
| `Like`, `Containing`, `StartingWith`, `EndingWith` | Partial string match |
| `LessThan`, `GreaterThan`, `Between`               | Range                |
| `In`, `NotIn`                                      | List-based queries   |
| `OrderBy...Asc/Desc`                               | Sorting              |




# 👣 Step 1: What is stream()?

A stream in Java is like a pipeline that you can use to:

    process each element one by one

    transform (like map, filter, group)

    collect the final result

Example:

```java

List<String> names = List.of("Alice", "Bob", "Charlie");

Stream<String> stream = names.stream();

```
Here, stream() doesn’t do anything by itself — it just gives you a tool to process the list element by element.

## 👣 Step 2: map() — Apply a Function to Each Element

Let’s say we want to get the length of each name:

```java
List<Integer> lengths = names.stream()
    .map(name -> name.length())  // ⬅️ Applies this function to each item
    .collect(Collectors.toList());
```

This gives: [5, 3, 7]

So:

    map means: "apply this function to each element"

    You can use method reference too: .map(String::length)




**Probable Questions** :

1. Given a post_id(5,12,19,26) of type poll, find the number of votes against each options along with the voters

request: 

- [x] GET
/pollvotes/voters/poll_id

response: 

```json
[
  {
    "optionName": "Modern",
    "voterIds": [1, 4],
    "count": 2
  },
  {
    "optionName": "Classic",
    "voterIds": [2],
    "count": 1
  },
  {
    "optionName": "Bold",
    "voterIds": [3],
    "count": 1
  }
]


```

```java
List<PollVotes> = pollRepo.findByPoll_id(poll_id)
 /* [
 {},
 {}
 ]

 */

```
2. Find all the tasks that have been completed this month
3. 