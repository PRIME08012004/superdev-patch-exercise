* So the bug i found was in `All Statuses Button` so the type of Status can be `Done , In-progress and Open ` when you select the specific tab it was showing random statuses so i checked in the repository 
* The bug is operator precedence in your WHERE clause. In SQL, AND binds tighter than OR, any task whose title matches is returned regardless of status, so the filter appears to do nothing.
so i applied changes 
```
@Query(value = """
    SELECT * FROM tasks
    WHERE archived = FALSE
      AND (LOWER(title) LIKE :term OR LOWER(description) LIKE :term)
      AND (:status IS NULL OR status = :status)
    ORDER BY created_at DESC
    """,
       nativeQuery = true)

```       
* Kept the scope focused: no new CRUD operations or endpoints, and the UI stays minimal.
* The biggest remaining risk that i thought of is in `schema.sql` the priority of every task is by default set to be medium and can be resolved with `Priority not assigned` to know better about what kind is actually `medium` and what is actually `Priority not assigned`
* i used claude (Web) during running the project locally i was facing JDK mismatch problem so i simply ask it to give me  bash command for installing JDK-17 and Setting up path for the Home dir