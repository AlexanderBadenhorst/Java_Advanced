package Chap11.Exercises.Page363;

/*
---Will compile---

-takeAnimals(new ArrayList<Animal>());

-takeDogs(new ArrayList<>());

List<Dog> dogs = new ArrayList<>();
takeDogs(dogs);


-takeSomeAnimals(new ArrayList<Dog>());

-takeSomeAnimals(new ArrayList<>());

-takeSomeAnimals(new ArrayList<Animal>());

List<Animal> animals = new ArrayList<>();
takeSomeAnimals(animals);


-takeObjects(new ArrayList<Object>());


---Will NOT compile---

-takeDogs(new ArrayList<Animal>());

-takeAnimals(new ArrayList<Dog>());

List<Object> objects = new ArrayList<>();
takeObjects(objects);


-takeObjects(new ArrayList<Dog>());
 */