package edu.ncsu.csc.CoffeeMaker.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.models.Inventory;
import edu.ncsu.csc.CoffeeMaker.services.IngredientService;
import edu.ncsu.csc.CoffeeMaker.services.InventoryService;

/**
 *
 * The APIIngredientController is responsible for getting and adding ingredients
 *
 * Spring will automatically convert all of the ResponseEntity and List results
 * to JSON
 *
 * @author Kiran Rao
 *
 */
@SuppressWarnings ( { "unchecked", "rawtypes" } )
@RestController
public class APIIngredientController extends APIController {

    /**
     * IngredientService object, to be autowired in by Spring to allow for
     * manipulating the Ingredient model
     */
    @Autowired
    private IngredientService ingredientService;

    /**
     * InventoryService object, to be autowired in by Spring to allow for
     * manipulating the Inventory model
     */
    @Autowired
    private InventoryService  inventoryService;

    /**
     * REST API method to provide GET access to a ingredient in the system
     *
     * @param name
     *            ingredient name
     * @return JSON representation of an ingredient
     */
    @GetMapping ( BASE_PATH + "ingredients/{name}" )
    public ResponseEntity getIngredient ( @PathVariable final String name ) {

        final Ingredient ingr = ingredientService.findByName( name );

        if ( null == ingr ) {
            return new ResponseEntity( HttpStatus.NOT_FOUND );
        }

        return new ResponseEntity( ingr, HttpStatus.OK );
    }

    /**
     * REST API endpoint to provide update access to CoffeeMaker's singleton
     * Inventory. This will update the Inventory of the CoffeeMaker by adding a
     * single ingredient to the inventory
     *
     * @param ingredient
     *            ingredient to add
     * @return response to the request
     */
    @PutMapping ( BASE_PATH + "/ingredient" )
    public ResponseEntity addIngredientToInventory ( @RequestBody final Ingredient ingredient ) {
        final Inventory inventoryCurrent = inventoryService.getInventory();
        inventoryCurrent.addIngredient( ingredient );
        inventoryService.save( inventoryCurrent );
        return new ResponseEntity( inventoryCurrent, HttpStatus.OK );
    }

}
