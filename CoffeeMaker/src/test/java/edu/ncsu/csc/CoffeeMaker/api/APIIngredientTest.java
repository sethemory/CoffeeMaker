package edu.ncsu.csc.CoffeeMaker.api;

import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

@RunWith ( SpringRunner.class )
@SpringBootTest
@AutoConfigureMockMvc
public class APIIngredientTest {

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
     * Tests adding a single ingredient to the inventory
     *
     * @throws Exception
     */
    @Test
    @Transactional
    public void addIngredient () throws Exception {
        final Inventory i = new Inventory();
        mvc.perform( put( "/api/v1/inventory" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( i ) ) ).andExpect( status().isOk() );
        final Ingredient ingredient = new Ingredient( "MILK", 15 );
        mvc.perform( put( "/api/v1/ingredient" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( ingredient ) ) ).andExpect( status().isOk() );

    }

    /**
     * Tests getting a single ingredient by name
     *
     * @throws Exception
     */
    @Test
    @Transactional
    public void getIngredient () throws Exception {
        final Inventory i = new Inventory();
        mvc.perform( put( "/api/v1/inventory" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( i ) ) ).andExpect( status().isOk() );
        final Ingredient ingredient = new Ingredient( "MILK", 15 );
        mvc.perform( put( "/api/v1/ingredient" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( ingredient ) ) ).andExpect( status().isOk() );
        final String ingredientResponse = mvc.perform( get( "/api/v1/ingredients/MILK" ) ).andDo( print() )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();
        assertTrue( ingredientResponse.contains( "MILK" ) );
        mvc.perform( get( "/api/v1/ingredients/COFFEE" ) ).andDo( print() ).andExpect( status().is4xxClientError() )
                .andReturn().getResponse().getContentAsString();

    }

}
