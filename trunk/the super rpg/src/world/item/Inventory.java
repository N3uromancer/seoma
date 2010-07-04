package world.item;

import java.util.HashMap;
import java.util.HashSet;

import world.item.weapon.Weapon;

public class Inventory
{
	HashMap<ItemSlot, Item> equipment = new HashMap<ItemSlot, Item>();
	HashSet<Item> items = new HashSet<Item>();
	
	/**
	 * creates a blank inventory with no items and nothing equipped
	 */
	public Inventory()
	{
		for(ItemSlot slot: ItemSlot.values())
		{
			equipment.put(slot, null);
		}
	}
	/**
	 * adds the passed item to the inventory
	 * @param i
	 */
	public void addItem(Item i)
	{
		items.add(i);
	}
	/**
	 * equips the passed item to the passed slots
	 * @param i the item to be equipped
	 * @param slots an array of slots for the item to be equipped to, ex. two handed weapon
	 */
	public void equipItem(Item i, ItemSlot[] slots)
	{
		for(ItemSlot slot: slots)
		{
			equipment.put(slot, i);
		}
	}
	/**
	 * gets the primary weapon used for attacking
	 * @return
	 */
	public Weapon getPrimaryWeapon()
	{
		return null;
	}
}
enum ItemSlot
{
	helmet(ItemType.armor),
	chest(ItemType.armor),
	rightArm(ItemType.weapon),
	leftArm(ItemType.weapon),
	legs(ItemType.armor);
	
	private ItemType type;
	ItemSlot(ItemType type)
	{
		this.type = type;
	}
}