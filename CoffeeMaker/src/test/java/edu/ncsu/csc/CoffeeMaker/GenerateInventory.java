package edu.ncsu.csc.CoffeeMaker;

import static org.junit.Assert.assertEquals;

import javax.transaction.Transactional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.models.Inventory;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.services.InventoryService;

@RunWith ( SpringRunner.class )
@EnableAutoConfiguration
@SpringBootTest ( classes = TestConfig.class )
public class GenerateInventory {

    @Autowired
    private InventoryService inventoryService;

    @Before
    public void setup () {
        final Inventory ivt = inventoryService.getInventory();
        ivt.addIngredient( new Ingredient( "MILK", 100 ) );
        ivt.addIngredient( new Ingredient( "COFFEE", 100 ) );
        ivt.addIngredient( new Ingredient( "SUGAR", 100 ) );
        inventoryService.save( ivt );
    }

    // @Test
    // public void checkInventorySize () {
    // final Inventory ivtCheck = inventoryService.getInventory();
    // assertEquals( ivtCheck.getIngredients().size(), 3 );
    // }

    @Test
    @Transactional
    public void checkUseIngredients () {
        final Recipe r1 = new Recipe();
        r1.setName( "Delicious Coffee" );

        r1.setPrice( 50 );

        r1.addIngredient( new Ingredient( "COFFEE", 10 ) );
        r1.addIngredient( new Ingredient( "SUGAR", 3 ) );
        r1.addIngredient( new Ingredient( "MILK", 2 ) );
        final Inventory ivtCheck = inventoryService.getInventory();
        ivtCheck.useIngredients( r1 );
        inventoryService.save( ivtCheck );
        inventoryService.getInventory();
        assertEquals( inventoryService.getInventory().getIngredients().get( 0 ).getName(), "MILK" );
        assertEquals( inventoryService.getInventory().getIngredients().get( 0 ).getAmount(), 98 );
        assertEquals( inventoryService.getInventory().getIngredients().get( 1 ).getName(), "COFFEE" );
        assertEquals( inventoryService.getInventory().getIngredients().get( 1 ).getAmount(), 90 );
        assertEquals( inventoryService.getInventory().getIngredients().get( 2 ).getName(), "SUGAR" );
        assertEquals( inventoryService.getInventory().getIngredients().get( 2 ).getAmount(), 97 );

    }

}
