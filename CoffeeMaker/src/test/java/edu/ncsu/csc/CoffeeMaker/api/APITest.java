package edu.ncsu.csc.CoffeeMaker.api;

import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import edu.ncsu.csc.CoffeeMaker.common.TestUtils;
import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.models.Inventory;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;

@RunWith ( SpringRunner.class )
@SpringBootTest
@AutoConfigureMockMvc
public class APITest {
    /**
     * MockMvc uses Spring's testing framework to handle requests to the REST
     * API
     */
    private MockMvc               mvc;

    @Autowired
    private WebApplicationContext context;

    /**
     * Sets up the tests.
     */
    @Before
    public void setup () {
        mvc = MockMvcBuilders.webAppContextSetup( context ).build();
    }

    /**
     * Test creation of recipes
     *
     * @throws Exception
     */
    @Test
    @Transactional
    public void ensureRecipe () throws Exception {
        String recipe = mvc.perform( get( "/api/v1/recipes" ) ).andDo( print() ).andExpect( status().isOk() )
                .andReturn().getResponse().getContentAsString();

        if ( !recipe.contains( "Mocha" ) ) {
            final Recipe r = new Recipe();
            r.addIngredient( new Ingredient( "MILK", 1 ) );
            r.addIngredient( new Ingredient( "CHOCOLATE", 7 ) );
            r.setPrice( 350 );
            r.setName( "Mocha" );

            mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                    .content( TestUtils.asJsonString( r ) ) ).andExpect( status().isOk() );

            recipe = mvc.perform( get( "/api/v1/recipes" ) ).andDo( print() ).andExpect( status().isOk() ).andReturn()
                    .getResponse().getContentAsString();

            assertTrue( recipe.contains( "Mocha" ) );

            final Inventory i = new Inventory();
            mvc.perform( put( "/api/v1/inventory" ).contentType( MediaType.APPLICATION_JSON )
                    .content( TestUtils.asJsonString( i ) ) ).andExpect( status().isOk() );
        }
    }

    /**
     * Test removal of recipes
     *
     * @throws Exception
     */
    @Test
    @Transactional
    public void deleteRecipe () throws Exception {
        String recipe = mvc.perform( get( "/api/v1/recipes" ) ).andDo( print() ).andExpect( status().isOk() )
                .andReturn().getResponse().getContentAsString();

        if ( !recipe.contains( "Mocha" ) ) {
            final Recipe r = new Recipe();
            r.addIngredient( new Ingredient( "MILK", 1 ) );
            r.addIngredient( new Ingredient( "CHOCOLATE", 7 ) );
            r.setPrice( 350 );
            r.setName( "Mocha" );

            mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                    .content( TestUtils.asJsonString( r ) ) ).andExpect( status().isOk() );

            recipe = mvc.perform( get( "/api/v1/recipes/Mocha" ) ).andDo( print() ).andExpect( status().isOk() )
                    .andReturn().getResponse().getContentAsString();

            assertTrue( recipe.contains( "Mocha" ) );

            final Inventory i = new Inventory();
            mvc.perform( put( "/api/v1/inventory" ).contentType( MediaType.APPLICATION_JSON )
                    .content( TestUtils.asJsonString( i ) ) ).andExpect( status().isOk() );

            mvc.perform( delete( "/api/v1/recipes/Mocha" ).contentType( MediaType.APPLICATION_JSON )
                    .content( TestUtils.asJsonString( r ) ) ).andExpect( status().isOk() );
        }

    }

    /**
     * Test get recipes from database
     *
     * @throws Exception
     */
    @Test
    @Transactional
    public void getRecipes () throws Exception {
        String recipe = mvc.perform( get( "/api/v1/recipes" ) ).andDo( print() ).andExpect( status().isOk() )
                .andReturn().getResponse().getContentAsString();

        final Recipe r1 = new Recipe();
        r1.addIngredient( new Ingredient( "MILK", 1 ) );
        r1.addIngredient( new Ingredient( "CHOCOLATE", 7 ) );
        r1.setPrice( 350 );
        r1.setName( "Mocha1" );

        final Recipe r2 = new Recipe();
        r2.addIngredient( new Ingredient( "MILK", 1 ) );
        r2.addIngredient( new Ingredient( "CHOCOLATE", 7 ) );
        r2.setPrice( 350 );
        r2.setName( "Mocha2" );

        mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( r1 ) ) ).andExpect( status().isOk() );
        mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( r2 ) ) ).andExpect( status().isOk() );

        recipe = mvc.perform( get( "/api/v1/recipes" ) ).andDo( print() ).andExpect( status().isOk() ).andReturn()
                .getResponse().getContentAsString();

        assertTrue( recipe.contains( "Mocha1" ) );
    }

}
