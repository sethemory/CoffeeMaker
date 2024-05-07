package edu.ncsu.csc.CoffeeMaker.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import javax.validation.ConstraintViolationException;

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
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.services.RecipeService;

@ExtendWith ( SpringExtension.class )
@EnableAutoConfiguration
@SpringBootTest ( classes = TestConfig.class )
public class RecipeTest {

    @Autowired
    private RecipeService service;

    @BeforeEach
    public void setup () {
        service.deleteAll();
    }

    // This test method checks that the recipe is being created and initialized
    // to the correct values
    @Test
    @Transactional
    public void testRecipeConstructer () {
        // creating a recipe to test
        final Recipe r1 = new Recipe();
        assertEquals( "", r1.getName() );
        assertEquals( 0, r1.getIngredients().size() );
    }

    // This method will be testing the setting and getting of the recipe Name
    @Test
    @Transactional
    public void testName () {
        // creating a recipe to test
        final Recipe r1 = new Recipe();

        r1.setName( "test1" );
        assertEquals( "test1", r1.getName() );

        r1.setName( "test2" );
        assertEquals( "test2", r1.getName() );
    }

    // This method will be testing the setting and getting of the recipe Price
    @Test
    @Transactional
    public void testPrice () {
        // creating a recipe to test
        final Recipe r1 = new Recipe();

        r1.setPrice( 15 );
        assertEquals( (Integer) 15, r1.getPrice() );

        r1.setPrice( 10 );
        assertEquals( (Integer) 10, r1.getPrice() );
    }

    // This method will be testing the toString method for Recipe
    @Test
    @Transactional
    public void testToString () {
        // creating a recipe to test
        final Recipe r1 = new Recipe();
        r1.setName( "testRecipe" );

        assertEquals( "Recipe [name=testRecipe, ingredients=[]]", r1.toString() );
    }

    // This method is testing adding the recipe to the service correctly
    @Test
    @Transactional
    public void testAddRecipe () {

        // creating a recipe to test
        final Ingredient coff = new Ingredient( "coffee", 10 );
        final Ingredient milk = new Ingredient( "milk", 10 );
        final Ingredient sug = new Ingredient( "sugar", 5 );
        final Recipe r1 = new Recipe();
        r1.setName( "testRecipe1" );
        r1.setPrice( 15 );
        r1.addIngredient( coff );
        r1.addIngredient( milk );
        r1.addIngredient( sug );
        service.save( r1 );

        Assertions.assertEquals( 1, service.findAll().size(), "There should only one recipe in the CoffeeMaker" );
        Assertions.assertNotNull( service.findByName( "testRecipe1" ) );
    }

    @Test
    @Transactional
    public void testNoRecipes () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );

        // creating a recipe to test
        final Ingredient coff = new Ingredient( "coffee", 10 );
        final Ingredient milk = new Ingredient( "milk", 10 );
        final Ingredient sug = new Ingredient( "sugar", 5 );
        final Recipe r1 = new Recipe();
        r1.setName( "Tasty Drink" );
        r1.setPrice( 12 );
        r1.addIngredient( coff );
        r1.addIngredient( milk );
        r1.addIngredient( sug );

        // creating a recipe to test
        final Recipe r2 = new Recipe();
        r2.setPrice( -12 );

        final List<Recipe> recipes = List.of( r1, r2 );

        try {
            service.saveAll( recipes );
            Assertions.assertEquals( 0, service.count(),
                    "Trying to save a collection of elements where one is invalid should result in neither getting saved" );
        }
        catch ( final Exception e ) {
            Assertions.assertTrue( e instanceof ConstraintViolationException );
        }

    }

    // This test method will be testing if the service can delete the recipe
    // correctly
    @Test
    @Transactional
    public void testDeleteRecipe1 () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );

        // creating and adding recipe to service
        final Ingredient coff = new Ingredient( "coffee", 10 );
        final Ingredient milk = new Ingredient( "milk", 10 );
        final Ingredient sug = new Ingredient( "sugar", 5 );
        final Recipe r1 = new Recipe();
        r1.setName( "testRecipe1" );
        r1.setPrice( 15 );
        r1.addIngredient( coff );
        r1.addIngredient( milk );
        r1.addIngredient( sug );
        service.save( r1 );

        Assertions.assertEquals( 1, service.count(), "There should be one recipe in the database" );
        // deleting the recipe to check if it works correctly
        service.delete( r1 );
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );
    }

    @Test
    @Transactional
    public void testEditRecipe1 () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );

        // creating a recipe to test
        final Ingredient coff = new Ingredient( "coffee", 10 );
        final Ingredient milk = new Ingredient( "milk", 10 );
        final Ingredient sug = new Ingredient( "sugar", 5 );
        final Recipe r1 = new Recipe();
        r1.setName( "Coffee" );
        r1.setPrice( 15 );
        r1.addIngredient( coff );
        r1.addIngredient( milk );
        r1.addIngredient( sug );
        service.save( r1 );

        // changing the price to see if the service was updated properly
        r1.setPrice( 70 );
        service.save( r1 );

        final Recipe retrieved = service.findByName( "Coffee" );

        Assertions.assertEquals( 70, (int) retrieved.getPrice() );

        Assertions.assertEquals( 1, service.count(), "Editing a recipe shouldn't duplicate it" );

    }

    // This method will create two recipes to check. Will return true if all the
    // ingredients have amounts = 0 and false otherwise
    @Test
    @Transactional
    public void testCheckRecipe () {
        // creating a recipe to test
        final Ingredient coff = new Ingredient( "coffee", 10 );
        final Ingredient milk = new Ingredient( "milk", 10 );
        final Ingredient sug = new Ingredient( "sugar", 5 );
        final Recipe r1 = new Recipe();
        r1.setName( "testRecipe1" );
        r1.setPrice( 15 );
        r1.addIngredient( coff );
        r1.addIngredient( milk );
        r1.addIngredient( sug );

        // creating a recipe to test
        final Ingredient coff1 = new Ingredient( "coffee", 0 );
        final Ingredient milk1 = new Ingredient( "milk", 0 );
        final Ingredient sug1 = new Ingredient( "sugar", 0 );
        final Recipe r2 = new Recipe();
        r2.setName( "testRecipe2" );
        r2.setPrice( 15 );
        r2.addIngredient( coff1 );
        r2.addIngredient( milk1 );
        r2.addIngredient( sug1 );

        assertTrue( r2.checkRecipe() );
        assertFalse( r1.checkRecipe() );
    }

    // This function will be testing the equals method for the recipe class and
    // the multiple branches within the method
    @Test
    @Transactional
    public void testEquals () {
        // creating a recipe to test
        final Recipe r1 = new Recipe();
        r1.setName( "testRecipe1" );
        r1.setPrice( 15 );
        // creating a recipe to test
        final Recipe r2 = new Recipe();
        r2.setName( "testRecipe2" );
        r2.setPrice( 15 );
        // creating a recipe to test
        final Recipe r3 = new Recipe();
        r3.setName( "testRecipe1" );
        r3.setPrice( 15 );

        Assertions.assertTrue( r1.equals( r1 ) );
        Assertions.assertFalse( r1.equals( r2 ) );
        Assertions.assertFalse( r1.equals( 2 ) );
        Assertions.assertFalse( r1.equals( null ) );
        Assertions.assertTrue( r1.equals( r3 ) );

        r3.setName( null );
        Assertions.assertFalse( r3.equals( r1 ) );
    }

    // This method will be testing the hash function for the recipe class
    @Test
    @Transactional
    public void testHash () {
        // creating a recipe to test
        final Recipe r1 = new Recipe();
        r1.setName( "testRecipe1" );
        r1.setPrice( 15 );

        // creating a recipe to test
        final Recipe r2 = new Recipe();
        r2.setName( "testRecipe2" );
        r2.setPrice( 15 );

        Assertions.assertNotEquals( r1.hashCode(), r2.hashCode() );
    }

    // This method will be testing the addIngredient method but specifically
    // adding an ingredient with a negative amount
    @Test
    @Transactional
    public void testAddIngredient () {
        // creating a recipe to test
        final Ingredient coff = new Ingredient( "coffee", 10 );
        final Ingredient milk = new Ingredient( "milk", 10 );
        final Ingredient sug = new Ingredient( "sugar", -5 );
        final Recipe r1 = new Recipe();
        r1.setName( "testRecipe1" );
        r1.setPrice( 15 );
        r1.addIngredient( coff );
        r1.addIngredient( milk );

        Assertions.assertEquals( 2, r1.getIngredients().size() );

        // adding an invalid ingredient
        try {
            r1.addIngredient( sug );
        }
        catch ( final Exception e ) {
            Assertions.assertEquals( 2, r1.getIngredients().size() );
        }
    }

    // This method will be testing the deleteIngredient method for the recipe
    // class
    @Test
    @Transactional
    public void testDeleteIngredient () {
        // creating a recipe to test
        final Ingredient coff = new Ingredient( "coffee", 10 );
        final Ingredient milk = new Ingredient( "milk", 10 );
        final Ingredient sug = new Ingredient( "sugar", 5 );
        final Ingredient choc = new Ingredient( "chocolate", 5 );
        final Recipe r1 = new Recipe();
        r1.setName( "testRecipe1" );
        r1.setPrice( 15 );
        r1.addIngredient( coff );
        r1.addIngredient( milk );
        r1.addIngredient( sug );

        Assertions.assertEquals( 3, r1.getIngredients().size() );
        r1.deleteIngredient( sug );
        Assertions.assertEquals( 2, r1.getIngredients().size() );

        Assertions.assertNull( r1.deleteIngredient( choc ) );

        r1.deleteIngredient( coff );
        Assertions.assertEquals( 1, r1.getIngredients().size() );
        r1.deleteIngredient( milk );
        Assertions.assertEquals( 0, r1.getIngredients().size() );
    }
}
