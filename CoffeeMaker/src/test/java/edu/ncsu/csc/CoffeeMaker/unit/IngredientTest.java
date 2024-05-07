package edu.ncsu.csc.CoffeeMaker.unit;

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import edu.ncsu.csc.CoffeeMaker.TestConfig;
import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.services.IngredientService;

@ExtendWith ( SpringExtension.class )
@EnableAutoConfiguration
@SpringBootTest ( classes = TestConfig.class )
public class IngredientTest {

    @Autowired
    private IngredientService service;

    @BeforeEach
    public void setup () {
        service.deleteAll();
    }

    // This method will be testing the constructors for the ingredient object
    // and make sure values are initialized correctly
    @Test
    @Transactional
    public void testIngredientConstructers () {
        // creating a ingredient to test with the empty constructor
        final Ingredient i1 = new Ingredient();
        assertEquals( null, i1.getName() );
        assertEquals( 0, i1.getAmount() );

        // creating a ingredient to test
        final Ingredient i2 = new Ingredient( "testIngred", 20 );
        assertEquals( "testIngred", i2.getName() );
        assertEquals( 20, i2.getAmount() );
    }

    // This method will be testing the set and get functionality for the id
    // field in ingredient
    @Test
    @Transactional
    public void testIngredientId () {
        // creating a ingredient to test
        final Ingredient i1 = new Ingredient( "testIngred", 20 );

        assertEquals( null, i1.getId() );
    }

    // This method will be testing the set and get functionality for the name
    // field in the ingredient
    @Test
    @Transactional
    public void testIngredientName () {
        // creating a ingredient to test
        final Ingredient i1 = new Ingredient( "testIngred", 20 );

        // checking the name matches the one from the constructor
        assertEquals( "testIngred", i1.getName() );

        // checking the name matches the new name from the set method
        i1.setName( "testChanged" );
        assertEquals( "testChanged", i1.getName() );
    }

    // This method will be testing the set and get functionality for the amount
    // field in ingredient
    @Test
    @Transactional
    public void testIngredientAmount () {
        // creating a ingredient to test
        final Ingredient i1 = new Ingredient( "testIngred", 20 );

        // checking the amount matches the one from the constructor
        assertEquals( 20, i1.getAmount() );

        // checking the amount matches the new name from the set method
        i1.setAmount( 50 );
        assertEquals( 50, i1.getAmount() );
    }

    // This method will be testing the set and get functionality for the
    // toString method in ingredient
    @Test
    @Transactional
    public void testIngredientToString () {
        // creating a ingredient to test
        final Ingredient i1 = new Ingredient( "testIngred", 20 );

        assertEquals( "Ingredient [id=null, name=testIngred, amount=20]", i1.toString() );
    }

    // This method will be testing the functionality to add ingredients to the
    // ingredient service and can find the ingredients by their name
    @SuppressWarnings ( "unchecked" )
    @Test
    @Transactional
    public void testAddIngredients () {
        Assertions.assertEquals( 0, service.findAll().size() );

        // creating a ingredients to test
        final Ingredient coff = new Ingredient( "coffee", 10 );
        final Ingredient milk = new Ingredient( "milk", 10 );
        final Ingredient sug = new Ingredient( "sugar", 5 );

        // saving an ingredient to the service object and then checking service
        // size
        service.save( coff );
        Assertions.assertEquals( 1, service.findAll().size() );

        // saving an ingredient to the service object and then checking service
        // size
        service.save( milk );
        Assertions.assertEquals( 2, service.findAll().size() );

        // saving an ingredient to the service object and then checking service
        // size
        service.save( sug );
        Assertions.assertEquals( 3, service.findAll().size() );

        // making sure the service can find an ingredient by its name
        Assertions.assertNotNull( service.findByName( "milk" ) );
    }

    // This method is checking the that service object is handling the deletion
    // of ingredient objects correctly
    @SuppressWarnings ( "unchecked" )
    @Test
    @Transactional
    public void testDeleteIngredients () {
        Assertions.assertEquals( 0, service.findAll().size() );

        // creating a ingredients to test
        final Ingredient coff = new Ingredient( "coffee", 10 );
        final Ingredient milk = new Ingredient( "milk", 10 );
        final Ingredient sug = new Ingredient( "sugar", 5 );

        // saving the ingredients to the service object and then checking
        // service size
        service.save( coff );
        service.save( milk );
        service.save( sug );
        Assertions.assertEquals( 3, service.findAll().size() );

        // checking if the service updates correctly with delete calls
        service.delete( milk );
        Assertions.assertEquals( 2, service.findAll().size() );

        // making sure the service cannot find an ingredient because it was
        // deleted
        Assertions.assertNull( service.findByName( "milk" ) );
    }

}
