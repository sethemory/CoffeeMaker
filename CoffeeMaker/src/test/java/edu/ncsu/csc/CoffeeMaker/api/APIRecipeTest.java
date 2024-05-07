package edu.ncsu.csc.CoffeeMaker.api;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.transaction.Transactional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import edu.ncsu.csc.CoffeeMaker.common.TestUtils;
import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.services.RecipeService;

@RunWith ( SpringRunner.class )
@SpringBootTest
@AutoConfigureMockMvc
public class APIRecipeTest {

    /**
     * MockMvc uses Spring's testing framework to handle requests to the REST
     * API
     */
    private MockMvc               mvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private RecipeService         service;

    /**
     * Sets up the tests.
     */
    @Before
    public void setup () {
        mvc = MockMvcBuilders.webAppContextSetup( context ).build();
    }

    /**
     * Test creating a simple recipe no ingredients
     *
     * @throws Exception
     */
    @Test
    @Transactional
    public void ensureRecipe () throws Exception {
        service.deleteAll();

        final Recipe r = new Recipe();
        r.setPrice( 10 );
        r.setName( "Mocha" );

        mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( r ) ) ).andExpect( status().isOk() );

    }

    /**
     * Test creating a recipe with ingredients
     *
     * @throws Exception
     */
    @Test
    @Transactional
    public void testRecipeAPI () throws Exception {

        service.deleteAll();

        final Recipe recipe = new Recipe();
        recipe.setName( "Delicious Not-Coffee" );

        recipe.setPrice( 5 );

        recipe.addIngredient( new Ingredient( "MILK", 1 ) );

        mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( recipe ) ) );

        Assert.assertEquals( 1, (int) service.count() );

    }

    /**
     * Duplicate recipe test
     *
     * @throws Exception
     */
    @Test
    @Transactional
    public void testAddRecipe2 () throws Exception {

        /* Tests a recipe with a duplicate name to make sure it's rejected */

        Assert.assertEquals( "There should be no Recipes in the CoffeeMaker", 0, service.findAll().size() );
        final String name = "Coffee";
        final Recipe r1 = createRecipe( name, 10, "MILK", "TEA", "COFFEE", "SUGAR" );

        service.save( r1 );

        final Recipe r2 = createRecipe( name, 10, "MILK", "COFFEE", "SUGAR", "TEA" );
        mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( r2 ) ) ).andExpect( status().is4xxClientError() );

        Assert.assertEquals( "There should only one recipe in the CoffeeMaker", 1, service.findAll().size() );
    }

    /**
     * Multiple recipe test
     *
     * @throws Exception
     */
    @Test
    @Transactional
    public void testAddRecipe15 () throws Exception {

        /* Tests to make sure that our cap of 3 recipes is enforced */

        Assert.assertEquals( "There should be no Recipes in the CoffeeMaker", 0, service.findAll().size() );

        final Recipe r1 = createRecipe( "Coffee", 10, "MILK", "TEA", "COFFEE", "SUGAR" );
        service.save( r1 );
        final Recipe r2 = createRecipe( "Mocha", 20, "MILK", "TEA", "COFFEE", "SUGAR" );
        service.save( r2 );
        final Recipe r3 = createRecipe( "Latte", 30, "MILK", "TEA", "COFFEE", "SUGAR" );
        service.save( r3 );

        Assert.assertEquals( "Creating three recipes should result in three recipes in the database", 3,
                service.count() );

        final Recipe r4 = createRecipe( "Hot Chocolate", 50, "MILK", "TEA", "COFFEE", "SUGAR" );

        mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( r4 ) ) ).andExpect( status().isInsufficientStorage() );

        Assert.assertEquals( "Creating a fourth recipe should not get saved", 3, service.count() );
    }

    private Recipe createRecipe ( final String name, final Integer price, final String ingre1, final String ingre2,
            final String ingre3, final String ingre4 ) {
        final Recipe recipe = new Recipe();
        recipe.setName( name );
        recipe.setPrice( price );
        recipe.addIngredient( new Ingredient( ingre1, 10 ) );
        recipe.addIngredient( new Ingredient( ingre2, 20 ) );
        recipe.addIngredient( new Ingredient( ingre3, 30 ) );
        recipe.addIngredient( new Ingredient( ingre4, 40 ) );

        return recipe;
    }

    // Test to make sure the recipes have the ingredients
    @Test
    @Transactional
    public void testRecipeIngredients () throws Exception {

        /* Tests a recipe with a duplicate name to make sure it's rejected */

        Assert.assertEquals( "There should be no Recipes in the CoffeeMaker", 0, service.findAll().size() );
        final Recipe r1 = createRecipe( "Coffee", 10, "MILK", "TEA", "COFFEE", "SUGAR" );
        service.save( r1 );
        Assert.assertEquals( "There should only one recipe in the CoffeeMaker", 1, service.findAll().size() );

        final Ingredient i = new Ingredient();
        final Recipe r2 = new Recipe();
        r2.addIngredient( i );
        r2.setName( "Coffee2" );
        r2.setPrice( 10 );
        // service.save( r2 );

        mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( r2 ) ) ).andExpect( status().isOk() );

        Assert.assertEquals( "There should only one recipe in the CoffeeMaker", 2, service.findAll().size() );

        Assert.assertEquals( 4, service.findByName( "Coffee" ).getIngredients().size() );
        Assert.assertEquals( 1, service.findByName( "Coffee2" ).getIngredients().size() );
    }

    @Test
    @Transactional
    public void testEditRecipe () throws Exception {
        Assert.assertEquals( "There should be no Recipes in the CoffeeMaker", 0, service.findAll().size() );

        // creating the recipe that will be added and edited later
        final Recipe r1 = new Recipe();
        r1.setPrice( 10 );
        r1.setName( "Coffee" );
        final Ingredient i1 = new Ingredient( "ing1", 10 );
        final Ingredient i2 = new Ingredient( "ing2", 15 );
        final Ingredient i3 = new Ingredient( "ing3", 20 );
        r1.addIngredient( i1 );
        r1.addIngredient( i2 );
        r1.addIngredient( i3 );

        // adding the recipe that I will be editing later
        mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( r1 ) ) ).andExpect( status().isOk() );

        Assert.assertEquals( "There should only one recipe in the CoffeeMaker", 1, service.findAll().size() );

        // making sure that the values I will be changing are what we set them
        // originally
        assertEquals( (Integer) 10, service.findByName( "Coffee" ).getPrice() );
        assertEquals( "ing1", service.findByName( "Coffee" ).getIngredients().get( 0 ).getName() );

        // changing the values we just tested to test the edit method
        final Recipe recipe = service.findByName( r1.getName() );
        recipe.setPrice( 20 );
        recipe.deleteIngredient( i2 );

        assertEquals( (Integer) 20, recipe.getPrice() );
        assertEquals( 2, recipe.getIngredients().size() );

        // editing the existing recipe
        mvc.perform( put( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( recipe ) ) ).andExpect( status().isOk() );

        // making sure that the edits to the existing recipe were successful
        assertEquals( 1, service.findAll().size() );
        assertEquals( (Integer) 20, service.findByName( "Coffee" ).getPrice() );
        assertEquals( 2, service.findByName( "Coffee" ).getIngredients().size() );

    }

}
