package edu.ncsu.csc.CoffeeMaker.api;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import edu.ncsu.csc.CoffeeMaker.common.TestUtils;
import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.models.Inventory;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.services.InventoryService;
import edu.ncsu.csc.CoffeeMaker.services.RecipeService;

@ExtendWith ( SpringExtension.class )
@SpringBootTest
@AutoConfigureMockMvc
public class APICoffeeTest {

    @Autowired
    private MockMvc               mvc;

    @Autowired
    private RecipeService         service;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private InventoryService      iService;

    /**
     * Sets up the tests.
     */
    @BeforeEach
    public void setup () {

        mvc = MockMvcBuilders.webAppContextSetup( context ).build();

        final Inventory ivt = iService.getInventory();

        ivt.addIngredient( new Ingredient( "CHOCOLATE", 15 ) );
        ivt.addIngredient( new Ingredient( "COFFEE", 15 ) );
        ivt.addIngredient( new Ingredient( "MILK", 15 ) );
        ivt.addIngredient( new Ingredient( "SUGAR", 15 ) );

        iService.save( ivt );

        final Recipe recipe = new Recipe();
        recipe.setName( "Coffee" );
        recipe.setPrice( 50 );
        recipe.addIngredient( new Ingredient( "COFFEE", 3 ) );
        recipe.addIngredient( new Ingredient( "MILK", 1 ) );
        recipe.addIngredient( new Ingredient( "SUGAR", 1 ) );
        service.save( recipe );
    }

    @Test
    @Transactional
    public void testPurchaseBeverage1 () throws Exception {

        final String name = "Coffee";

        mvc.perform( post( String.format( "/api/v1/makecoffee/%s", name ) ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( 60 ) ) ).andExpect( status().isOk() )
                .andExpect( jsonPath( "$.message" ).value( 10 ) );

        assertTrue( name.contains(
                "Coffee" ) ); /* Make sure that now our recipe is there */

    }

    @Test
    @Transactional
    public void testPurchaseBeverage2 () throws Exception {
        /* Insufficient amount paid */

        final String name = "Coffee";

        mvc.perform( post( String.format( "/api/v1/makecoffee/%s", name ) ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( 40 ) ) ).andExpect( status().is4xxClientError() )
                .andExpect( jsonPath( "$.message" ).value( "Not enough money paid" ) );

        assertTrue( name.contains(
                "Coffee" ) ); /* Make sure that now our recipe is there */

    }

    @Test
    @Transactional
    public void testPurchaseBeverage3 () throws Exception {
        /* Insufficient inventory */

        final Inventory ivt = iService.getInventory();
        ivt.getIngredients().get( 1 ).setAmount( 0 );
        iService.save( ivt );

        final String name = "Coffee";

        mvc.perform( put( "/api/v1/inventory" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( ivt ) ) ).andExpect( status().isOk() );

        mvc.perform( post( String.format( "/api/v1/makecoffee/%s", name ) ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( 50 ) ) ).andExpect( status().is4xxClientError() )
                .andExpect( jsonPath( "$.message" ).value( "Not enough inventory" ) );

        assertTrue( name.contains( "Coffee" ) );
        /* Make sure that now our recipe is there */

    }
}
