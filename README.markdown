AndroidRecord is an open-source ORM library for Android applications.

It's heavily inspired by Active Record and aims to ease performing basic CRUD operations on a SQLite database. It depends on conventions, so it requires very little setup and configuration.

Getting Started
===============

AndroidRecord is distributed as a library project (instead of a JAR) because it needs to hook into your app to do some of the things it does. So, to get started, first grab a copy of the code:

    git clone git@github.com:lyudmil/androidrecord.git

Then, look up your favorite IDE's instructions on how to add a library project for Android apps and follow the process to add AndroidRecord. You should be all set after that.

Using AndroidRecord
===================

Set up the database
---

To start using AndroidRecord within your project, you need to first create a database. To do that, run the following code in an activity of your choice (I suggest your root activity):

```java
DatabaseManager databaseManager = new DatabaseManager(getApplicationContext());
databaseManager.bootStrapDatabase();
```

This does the following:

- Looks up the resource `R.string.app_name` (which you ought to already have defined), converts its name to its underscored version, and creates a database with that name unless it already exists. If it already exists, it just uses it.

- Checks to see if there are any migrations to run. More on that later.

Create your first model
---

AndroidRecord is based on the [Active Record pattern](http://en.wikipedia.org/wiki/Active_record_pattern), so every model class represents the table schema, while every instance of the class is mapped to a row in the database.

So, the first step is to create a class that extends `ActiveRecordBase` and define the fields that are going to go in the table:

```java
public class Course extends ActiveRecordBase<Course> {

    public String name;	// All fields you wish to save to the database must be declared public
    public String description;

    // More code...
}
```

Then, you need to create the corresponding table in the database. 

To do this, you have to create your first migration. Migrations are a way of versioning your database. They are SQL files in the `assets/migrations` path named as consecutive numbers (1.sql, 2.sql, 3.sql...) that run in order if needed. When you call `databaseManager.bootStrapDatabase()`, it checks the current database version against the highest migration number defined and runs any migrations in between. That way you can easily move your database forward as your app evolves.

So, we must create a table for the `Course` class we defined above. The suggested way of doing this is by first creating a unit test:

```java
public class CourseTest extends ModelTestCase<Course> {	// ModelTestCase is a class provided in ActiveRecord

    public void testFirstMigrationCreatesCoursesTable() throws Exception {
        assertDatabaseTableCreated(Course.class, 1); // Check that migration 1.sql contains the SQL required to create a table for the Course class
    }
}
```

Run this test and it should fail, spitting out the required SQL. Create `assets/migrations/1.sql` and paste that SQL. Run the test again and it should pass. You're now all set to do CRUD operations on courses.

CRUD
====

Here's where this starts paying off...

Create
---

To insert a new model into the database, all you have to do is call the `ActiveRecordBase.save()` method on the instance you want. So, following our Course example, use the following code:

```java
Course course = new Course();
course.name = "Artificial Intelligence";
course.save();
```

That's all.

Read
---

There are several ways to read the database. If you know the ID of the record you want to look up:

```java
Course savedCourse = new Course().find(1);
```

If you want to get all the courses:

```java
List<Course> all = new Course().all();
```

The first course that matches a particular where clause:

```java
Course ai = new Course().find("name='Artificial Intelligence'");
```

All courses that match a particular where clause:

```java
List<Course> duplicates = new Course().where("name='Artificial Intelligence'");
```

Pretty simple, right?

Update
---

Updating a record works the same way inserting does. ActiveRecord checks to see if the record has already been saved to the database and issues an update query instead of a create one if it has been. So, to update records, look them up, change them in your code, and call `ActiveRecordBase.save()` when you're done.

Delete
---

Couldn't be simpler:

```java
course.destroy()
```

Any questions? Of course not.

Associations
============

One-to-many
---

One-to-many associations in ActiveRecord a bidirectional. So, let's say we wanted all of our courses to be taught by exactly one teacher. First, create the `Teacher` class and indicate that it's a one-to-many relationship:

```java
public class Teacher extends ActiveRecordBase<Teacher> {
    public String name;
    public String title;

    @HasMany(name = "teacher", relatedType = Course.class)
    public ActiveCollection<Course> lectures;		// Use ActiveCollection for collections you want to persist
}
```

There are two pieces of information you need to provide to the `@HasMany` annotation:

- name: the name of the field on the class on the "many" side of the association (`Course`) that represents the current class (`Teacher`). So, we're saying that the teacher of a course is represented by the `teacher` field on the `Course` class.

- relatedType: which class is on the "many" side of the association.

We also need to modify the `Course` class:

```java
public class Course extends ActiveRecordBase<Course> {
    // Previous field declarations

    @BelongsTo
    public Teacher teacher;

    // More code...
}
```

We also need to write another migration to add a column called `teacher_id` to the `courses` table (our unit test should tell us that). Then, we're all set. We can use the `lectures` field just like any other collection. The only gotcha is that the owner of a collection *must be saved before* any of the owned entities. So, if a `Teacher` hasn't been saved, you won't be able to save any of their `Course`'s without AndroidRecord spitting out errors at you.

One-to-one
---

One-to-one relations are much simpler to implement. Simply add a `@BelongsTo` annotation to the field of the owning class and add a database column to represent the relation (like what we did above with courses). You're strongly encouraged to use a unit test as a guide so that you get all the SQL right. It's not complicated, but why would you waste brain cycles thinking about it?

One-to-many
---

AndroidRecord doesn't currently support one-to-many associations. If you need to have one, you need to model it explicitly. Create a model that has a one-to-many relationship to each side of the many-to-many association you want to implement. So, continuing our courses example, modeling student enrollments in courses:

```java
public class Enrollment extends ActiveRecordBase<Enrollment> {

    @BelongsTo
    public Student student;

    @BelongsTo
    public Course course;
}
```

Then, you've reduced the many-to-many relationship to two one-to-many relationships.


There's much more to cover, but this should be a good start. If you need help, shoot me an email:
lyudmil.i.angelov@gmail.com

Hope you find it useful! Happy coding!


