package edu.ncsu.csc.CoffeeMaker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.services.RecipeService;

@RunWith ( SpringRunner.class )
@EnableAutoConfiguration
@SpringBootTest ( classes = TestConfig.class )

public class TestDatabaseInteraction {

    @Autowired
    private RecipeService recipeService;

    /**
     * Tests the database with adding a recipe and saving it, and retrieving the
     * ingredients, amounts, and price
     */
    @Test
    @Transactional
    public void testRecipes () {
        recipeService.deleteAll();

        final Recipe r = new Recipe();

        r.setName( "Mocha" );
        r.addIngredient( new Ingredient( "Coffee", 5 ) );
        r.addIngredient( new Ingredient( "Milk", 5 ) );
        r.addIngredient( new Ingredient( "Matcha", 5 ) );
        r.addIngredient( new Ingredient( "Pumpkin Spice", 5 ) );
        r.addIngredient( new Ingredient( "Apple Cider", 5 ) );
        r.setPrice( 350 );

        recipeService.save( r );

        final List<Recipe> dbRecipes = recipeService.findAll();

        assertEquals( 1, dbRecipes.size() );

        final Recipe dbRecipe = dbRecipes.get( 0 );

        assertEquals( r.getName(), dbRecipe.getName() );
        assertEquals( r.getIngredients().get( 0 ).getAmount(), dbRecipe.getIngredients().get( 0 ).getAmount() );
        assertEquals( r.getIngredients().get( 1 ).getAmount(), dbRecipe.getIngredients().get( 1 ).getAmount() );
        assertEquals( r.getIngredients().get( 2 ).getAmount(), dbRecipe.getIngredients().get( 2 ).getAmount() );
        assertEquals( r.getIngredients().get( 3 ).getAmount(), dbRecipe.getIngredients().get( 3 ).getAmount() );
        assertEquals( r.getIngredients().get( 4 ).getAmount(), dbRecipe.getIngredients().get( 4 ).getAmount() );

        assertEquals( r.getPrice(), dbRecipe.getPrice() );

        final Recipe dbRecipeByName = recipeService.findByName( r.getName() );

        assertEquals( r.getIngredients().get( 0 ).getAmount(), dbRecipeByName.getIngredients().get( 0 ).getAmount() );
        assertEquals( r.getIngredients().get( 1 ).getAmount(), dbRecipeByName.getIngredients().get( 1 ).getAmount() );
        assertEquals( r.getIngredients().get( 2 ).getAmount(), dbRecipeByName.getIngredients().get( 2 ).getAmount() );
        assertEquals( r.getIngredients().get( 3 ).getAmount(), dbRecipeByName.getIngredients().get( 3 ).getAmount() );
        assertEquals( r.getIngredients().get( 4 ).getAmount(), dbRecipeByName.getIngredients().get( 4 ).getAmount() );
        assertEquals( r.getPrice(), dbRecipeByName.getPrice() );

        assertTrue( recipeService.existsById( dbRecipe.getId() ) );

        assertEquals( r, recipeService.findById( dbRecipe.getId() ) );

        assertNull( recipeService.findById( null ) );

        assertNull( recipeService.findById( (long) -1 ) );

        dbRecipe.setPrice( 15 );
        recipeService.save( dbRecipe );

        assertEquals( 1, recipeService.count() );

        assertEquals( 15, (int) recipeService.findAll().get( 0 ).getPrice() );

        final Recipe c = new Recipe();

        r.setName( "Not Mocha" );
        r.setPrice( 350 );

        recipeService.save( c );

        final List<Recipe> newRecipes = recipeService.findAll();

        assertEquals( 2, newRecipes.size() );

        newRecipes.remove( 0 );

        assertEquals( 1, newRecipes.size() );

        newRecipes.remove( 0 );

        assertEquals( 0, newRecipes.size() );

    }

    /**
     * Tests the database with adding a recipe with ingredients and price, then
     * proceeding to delete it making sure that it is deleted from the database
     * as well
     */
    @Test
    @Transactional
    public void testDeleteRecipes () {
        recipeService.deleteAll();
        final Recipe r = new Recipe();

        r.setName( "Mocha" );
        r.addIngredient( new Ingredient( "Coffee", 5 ) );
        r.addIngredient( new Ingredient( "Milk", 5 ) );
        r.addIngredient( new Ingredient( "Matcha", 5 ) );
        r.addIngredient( new Ingredient( "Pumpkin Spice", 5 ) );
        r.addIngredient( new Ingredient( "Apple Cider", 5 ) );
        r.setPrice( 350 );

        recipeService.save( r );

        final List<Recipe> dbRecipes = recipeService.findAll();

        assertEquals( 1, dbRecipes.size() );

        final Recipe dbRecipe = dbRecipes.get( 0 );

        assertEquals( r.getIngredients().get( 0 ).getAmount(), dbRecipe.getIngredients().get( 0 ).getAmount() );
        assertEquals( r.getIngredients().get( 1 ).getAmount(), dbRecipe.getIngredients().get( 1 ).getAmount() );
        assertEquals( r.getIngredients().get( 2 ).getAmount(), dbRecipe.getIngredients().get( 2 ).getAmount() );
        assertEquals( r.getIngredients().get( 3 ).getAmount(), dbRecipe.getIngredients().get( 3 ).getAmount() );
        assertEquals( r.getIngredients().get( 4 ).getAmount(), dbRecipe.getIngredients().get( 4 ).getAmount() );
        assertEquals( r.getPrice(), dbRecipe.getPrice() );

        recipeService.delete( r );
        assertEquals( 0, recipeService.findAll().size() );

    }

}
