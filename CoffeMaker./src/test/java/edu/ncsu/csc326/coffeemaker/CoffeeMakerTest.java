/*
 * Copyright (c) 2009,  Sarah Heckman, Laurie Williams, Dright Ho
 * All Rights Reserved.
 * 
 * Permission has been explicitly granted to the University of Minnesota 
 * Software Engineering Center to use and distribute this source for 
 * educational purposes, including delivering online education through
 * Coursera or other entities.  
 * 
 * No warranty is given regarding this software, including warranties as
 * to the correctness or completeness of this software, including 
 * fitness for purpose.
 * 
 * 
 * Modifications 
 * 20171114 - Ian De Silva - Updated to comply with JUnit 4 and to adhere to 
 * 							 coding standards.  Added test documentation.
 */
package edu.ncsu.csc326.coffeemaker;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import edu.ncsu.csc326.coffeemaker.exceptions.InventoryException;
import edu.ncsu.csc326.coffeemaker.exceptions.RecipeException;

import static org.junit.Assert.*;

/**
 * Unit tests for CoffeeMaker class.
 * 
 * @author Sarah Heckman and @Tahsin Tok
 */
public class CoffeeMakerTest {
	
	/**
	 * The object under test.
	 */
	private CoffeeMaker coffeeMaker;
	
	// Sample recipes to use in testing.
	private Recipe recipe1;
	private Recipe recipe2;
	private Recipe recipe3;
	private Recipe recipe4;

	/**
	 * Initializes some recipes to test with and the {@link CoffeeMaker} 
	 * object we wish to test.
	 * 
	 * @throws RecipeException  if there was an error parsing the ingredient 
	 * 		amount when setting up the recipe.
	 */
	@Before
	public void setUp() throws RecipeException {
		coffeeMaker = new CoffeeMaker();
		
		//Set up for r1
		recipe1 = new Recipe();
		recipe1.setName("Coffee");
		recipe1.setAmtChocolate("0");
		recipe1.setAmtCoffee("3");
		recipe1.setAmtMilk("1");
		recipe1.setAmtSugar("1");
		recipe1.setPrice("50");
		
		//Set up for r2
		recipe2 = new Recipe();
		recipe2.setName("Mocha");
		recipe2.setAmtChocolate("20");
		recipe2.setAmtCoffee("3");
		recipe2.setAmtMilk("1");
		recipe2.setAmtSugar("1");
		recipe2.setPrice("75");
		
		//Set up for r3
		recipe3 = new Recipe();
		recipe3.setName("Latte");
		recipe3.setAmtChocolate("0");
		recipe3.setAmtCoffee("3");
		recipe3.setAmtMilk("3");
		recipe3.setAmtSugar("1");
		recipe3.setPrice("100");
		
		//Set up for r4
		recipe4 = new Recipe();
		recipe4.setName("Hot Chocolate");
		recipe4.setAmtChocolate("4");
		recipe4.setAmtCoffee("0");
		recipe4.setAmtMilk("1");
		recipe4.setAmtSugar("1");
		recipe4.setPrice("65");
	}
	
	
	/**
	 * Given a coffee maker with the default inventory
	 * When we add inventory with well-formed quantities
	 * Then we do not get an exception trying to read the inventory quantities.
	 * 
	 * @throws InventoryException  if there was an error parsing the quanity
	 * 		to a positive integer.
	 */
	@Test
	public void testAddInventory() throws InventoryException {
		coffeeMaker.addInventory("4","7","0","9");
	}
	
	/**
	 * Given a coffee maker with the default inventory
	 * When we add inventory with malformed quantities (i.e., a negative 
	 * quantity and a non-numeric string)
	 * Then we get an inventory exception
	 * 
	 * @throws InventoryException  if there was an error parsing the quanity
	 * 		to a positive integer.
	 */
	@Test(expected = InventoryException.class)
	public void testAddInventoryException() throws InventoryException {
		coffeeMaker.addInventory("4", "-1", "asdf", "3");
	}
	
	/**
	 * Given a coffee maker with one valid recipe
	 * When we make coffee, selecting the valid recipe and paying more than 
	 * 		the coffee costs
	 * Then we get the correct change back.
	 */
	@Test
	public void testMakeCoffee() {
		coffeeMaker.addRecipe(recipe1);
		assertEquals(25, coffeeMaker.makeCoffee(0, 75));
	}

	@Test
	public void testAddRecipe() {
		assertTrue(coffeeMaker.addRecipe(recipe1));
		assertTrue(coffeeMaker.addRecipe(recipe2));
		assertFalse(coffeeMaker.addRecipe(recipe1));
	}

	@Test
	public void testDeleteRecipe() {
		coffeeMaker.addRecipe(recipe1);
		assertEquals("Coffee", coffeeMaker.deleteRecipe(0));
		assertNull(coffeeMaker.getRecipes()[0]);
	}

	@Test
	public void testEditRecipe() throws RecipeException {
		coffeeMaker.addRecipe(recipe1);
		Recipe newRecipe = new Recipe();
		newRecipe.setName("Espresso");
		newRecipe.setAmtChocolate("0");
		newRecipe.setAmtCoffee("2");
		newRecipe.setAmtMilk("0");
		newRecipe.setAmtSugar("0");
		newRecipe.setPrice("40");

		assertEquals("Coffee", coffeeMaker.editRecipe(0, newRecipe));
		assertEquals("Espresso", coffeeMaker.getRecipes()[0].getName());
	}
	@Test
	public void testCheckInventory() {

		String inventoryOutput = coffeeMaker.checkInventory();


		String expectedOutput = "Coffee: 15\n" +
				"Milk: 15\n" +
				"Sugar: 15\n" +
				"Chocolate: 15\n";


		assertEquals(expectedOutput, inventoryOutput);
	}
	/**
	 * Given a coffee maker with one valid recipe and sufficient inventory
	 * When we make coffee, selecting the valid recipe and paying the exact price
	 * Then the coffee should be dispensed with no change returned.
	 */
	@Test
	public void testMakeCoffeeExactPayment() {
		coffeeMaker.addRecipe(recipe1); // Coffee recipe
		int change = coffeeMaker.makeCoffee(0, 50); // Paying exactly the price
		assertEquals(0, change); // No change should be returned
	}

	/**
	 * Given a coffee maker with one valid recipe and sufficient inventory
	 * When we make coffee, selecting the valid recipe and paying less than the cost
	 * Then the coffee should not be dispensed and the user's money should be returned.
	 */
	@Test
	public void testMakeCoffeeInsufficientFunds() {
		coffeeMaker.addRecipe(recipe1); // Coffee recipe
		int change = coffeeMaker.makeCoffee(0, 30); // Paying less than the price
		assertEquals(30, change); // User's money should be returned
	}

	/**
	 * Given a coffee maker with one valid recipe
	 * When we make coffee with insufficient inventory (assuming we deplete the inventory)
	 * Then the coffee should not be dispensed and the user's money should be returned.
	 */
	@Test
	public void testMakeCoffeeInsufficientInventory() throws InventoryException {
		coffeeMaker.addRecipe(recipe1); // Coffee recipe
		// Use up the inventory for coffee
		coffeeMaker.addInventory("0", "0", "0", "0"); // Remove all inventory
		int change = coffeeMaker.makeCoffee(0, 75); // Attempt to pay
		assertEquals(75, change); // User's money should be returned
	}

}
